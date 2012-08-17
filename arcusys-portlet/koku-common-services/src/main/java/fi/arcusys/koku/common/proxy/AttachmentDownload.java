package fi.arcusys.koku.common.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.services.facades.Logging;
import fi.arcusys.koku.common.services.logging.LoggingHandle;
import fi.arcusys.koku.common.services.users.UserIdResolver;
import fi.koku.portlet.filter.userinfo.UserInfo;

public class AttachmentDownload {

	private static final Logger LOG = LoggerFactory.getLogger(AttachmentDownload.class);

	private final Logging serviceLog = new LoggingHandle();
	private final UserIdResolver resolver = new UserIdResolver();


	public AttachmentDownload(ResourceRequest request, ResourceResponse response, String path) {

		final UserInfo user = (UserInfo) request.getPortletSession().getAttribute(UserInfo.KEY_USER_INFO);

		try {
			if (user == null) {
				LOG.warn("UserInfo is missing! User session propably timeouted. Prevented user to download file: '" + path + "'");
				generateErrorMsg("Access denied", user, response);
				return;
			}

			// Looks strange? UserInfo.getUid returns portal username not WS UID.
			final String uid = resolver.getUserId(user.getUid());

			if (uid == null) {
				final String msg = "For somereason WS couldn't resolve UID. User is logged in, but he doesn't have proper UID in KokuWS. User: '"+user.toString()+"'. Prevented user to download file: '" + path + "'";
				LOG.error(msg);
				serviceLog.logFileDownload(uid, path, msg);
				generateErrorMsg("Access denied", user, response);
				return;
			}

			final AttachmentProxy proxy = new AttachmentProxy(path, user);
			byte[] fileContent = proxy.getFile();
			LOG.debug("ResponseCode: "+proxy.getResponseCode());
			LOG.debug("Headers: "+proxy.getHeaders().toString());
			LOG.debug("ContentType: "+proxy.getContentType());

			if (proxy.getResponseCode() != 200) {
				LOG.warn("User '"+user.getUid()+"' (UID: '"+uid+"') couldn't download file '"+path+"'. Proxy responseCode: '"+proxy.getResponseCode()+"'. ProxyPath: '"+proxy.getProxyURL(path)+"'");
				generateErrorMsg("Access denied", user, response);
				serviceLog.logFileDownload(uid, path, "User (username: '"+user.getUid()+"') tried to download file, but it's missing. Proxy returned ResponseCode: '"+proxy.getResponseCode()+"'");
				return;
			}
			serviceLog.logFileDownload(uid, path, "Accessed to file");

			final Map<String, String> headers = proxy.getHeaders();
			final Set<String> headerNames = headers.keySet();

			for (String headerName : headerNames) {
				response.setProperty(headerName, headers.get(headerName));
			}
			response.setContentType(proxy.getContentType());
			response.setProperty("Content-Disposition", "attachment; filename="+proxy.getFilename());

			final OutputStream out = response.getPortletOutputStream();
			out.write(fileContent);
			out.flush();
			out.close();
		} catch (KokuCommonException e) {
			LOG.error("Failed to provide file path: '"+path+"' User: '"+user.toString()+"'", e);
			generateErrorMsg("Access denied", user, response);
		} catch (IOException ioe) {
			LOG.error("Failed to provide file path: '"+path+"' User: '"+user.toString()+"'", ioe);
			generateErrorMsg("Server side error", user, response);
		}
	}

	private void generateErrorMsg(String msg, UserInfo user, ResourceResponse response) {
		try {
			PrintWriter writer;
			writer = response.getWriter();
			writer.write("Error: "+msg);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			LOG.error("Failed to show error message to user. Attachement download feature might be broken. User: '"+user.toString()+"'");
		}
	}
}
