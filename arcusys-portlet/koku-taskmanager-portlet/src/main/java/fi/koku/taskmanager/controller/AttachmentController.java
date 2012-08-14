package fi.koku.taskmanager.controller;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.proxy.AttachmentProxy;
import fi.koku.portlet.filter.userinfo.UserInfo;


@Controller("attachmentFileController")
@RequestMapping(value = "VIEW")
public class AttachmentController {
	private static final Logger LOG = LoggerFactory.getLogger(AttachmentController.class);

	@ResourceMapping(value = "attachment")
	public void actionPageView(
			@RequestParam(value = "path") String path,
			ResourceRequest request,
			ResourceResponse response) {

		UserInfo user = (UserInfo) request.getPortletSession().getAttribute(UserInfo.KEY_USER_INFO);
		if (user == null) {
			LOG.error("UserInfo is missing! Prevented user to download file: '" + path + "'");
			generateErrorMsg("Access denied", user, response);
			return;
		}

		try {
			final AttachmentProxy proxy = new AttachmentProxy(path, user);
			byte[] fileContent = proxy.getFile();
			LOG.debug("ResponseCode: "+proxy.getResponseCode());
			LOG.debug("Headers: "+proxy.getHeaders().toString());
			LOG.debug("ContentType: "+proxy.getContentType());

			if (proxy.getResponseCode() != 200) {
				LOG.warn("User '"+user.getUid()+"' couldn't download file '"+path+"'. Proxy responseCode: '"+proxy.getResponseCode()+"'. ProxyPath: '"+proxy.getProxyURL(path)+"'");
				generateErrorMsg("Access denied", user, response);
				return;
			}

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
