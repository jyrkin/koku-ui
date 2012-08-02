package fi.arcusys.koku.proxy;

import javax.servlet.http.HttpServletRequest;


/**
 * ProxyAuthentication for Citizen side portal.
 *
 * Note: Currently citizens doesn't need access to Intalio attachments so
 * access to file files is forbidden by default. Change implementation if needed.
 *
 * @author Toni Turunen
 *
 */
public class ProxyAuthenticationKunpo extends  AbstractProxyAuthentication {

	public ProxyAuthenticationKunpo(HttpServletRequest request) {
		super(request);
	}

	@Override
	public boolean isAllowedToAccessFile(String file) {
		// Need to implement properly
		return true;
	}

}
