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
public class ProxyAuthenticationKunpo implements ProxyAuthentication {

	public ProxyAuthenticationKunpo(HttpServletRequest request) {
		// TODO
	}

	@Override
	public boolean isAllowedToAccessFile(String file) {
		// Need to implement properly
		// Currently Kunpo users doesn't need access to Intalio
		// attachments so all access attempts should be prevent for now.
		return false;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}



}
