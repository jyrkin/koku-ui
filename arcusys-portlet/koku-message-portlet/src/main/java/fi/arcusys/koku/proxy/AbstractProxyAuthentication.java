package fi.arcusys.koku.proxy;

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public abstract class AbstractProxyAuthentication implements ProxyAuthentication {

	private static final Pattern REGEX_SESSION_USERNAME = Pattern.compile("javax.portlet.*?USER_username");

	private boolean loggedIn = false;
	private String username = null;

	public AbstractProxyAuthentication(HttpServletRequest request) {

		Enumeration<?> attributeNames = request.getSession().getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			final String name = (String)attributeNames.nextElement();
			Matcher matcher = REGEX_SESSION_USERNAME.matcher(name);
			if (matcher.matches()) {
				username = (String) request.getSession().getAttribute(name);
				break;
			}
		}

		if (username != null) {
			loggedIn = true;
		}
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isLoggedIn() {
		return loggedIn;
	}
}
