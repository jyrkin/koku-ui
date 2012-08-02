package fi.arcusys.koku.proxy;

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>ProxyAuthentication for Employee portal (JBoss)</h1>
 *
 * <p><b>NOTE!</b>
 * JBoss Portal 2.7 doesn't share automatically session attributes between servlets/portlets
 * in same WAR package (sharing session attributes between war files is forbidden from servlet spec 2.4).</p>
 *
 * <p>Add '/jboss-portal-2.7.2/server/default/deploy/jboss-web.deployer/context.xml'-file following lines to
 * get automatic session attribute sharing working:</p>
 *
 * 	<code>
 * 		&lt;!-- Needed for portal session paths --&gt;
 *		&lt;!SessionCookie path="/"/&gt;
 *	</code>'
 *
 * <p>See: https://issues.jboss.org/browse/GTNWCI-15</p>
 *
 * @author Toni Turunen
 *
 */
public class ProxyAuthenticationLoora implements ProxyAuthentication {

	// javax.portlet.p./default/Message/MessagePortletWindow?USER_username
	// javax.portlet.*?USER_username
	private static final Pattern REGEX_SESSION_USERNAME = Pattern.compile("javax.portlet.*?USER_username");

	private boolean loggedIn = false;
	private String username = null;

	public ProxyAuthenticationLoora(HttpServletRequest request) {

		Enumeration<?> attributeNames = request.getSession().getAttributeNames();
		boolean foundUser = false;
		while (attributeNames.hasMoreElements()) {
			final String name = (String)attributeNames.nextElement();
			Matcher matcher = REGEX_SESSION_USERNAME.matcher(name);
			if (matcher.matches()) {
				loggedIn = Boolean.TRUE;
				username = (String) request.getSession().getAttribute(name);
				foundUser = true;
				break;
			}
		}

		if (!foundUser) {
			loggedIn = Boolean.FALSE;
			username = null;
		}
	}

	@Override
	public boolean isAllowedToAccessFile(String file) {
		// Need to implement properly
		return true;
	}

	@Override
	public boolean isLoggedIn() {
		return loggedIn;
	}

	@Override
	public String getUsername() {
		return username;
	}

}
