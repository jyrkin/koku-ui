package fi.arcusys.koku.proxy;

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
public class ProxyAuthenticationLoora extends AbstractProxyAuthentication {

	public ProxyAuthenticationLoora(HttpServletRequest request) {
		super(request);
	}

	@Override
	public boolean isAllowedToAccessFile(String file) {
		// Need to implement properly
		return true;
	}

}
