package fi.arcusys.koku.proxy;

/**
 * Dummy ProxyAuthentication. By default this implementation
 * should forbidden access to all attachment files.
 *
 * @author Toni Turunen
 *
 */
public class ProxyAuthenticationDummy implements ProxyAuthentication {


	@Override
	public boolean isAllowedToAccessFile(String file) {
		return false;
	}

	@Override
	public String getUsername() {
		return null;
	}

	@Override
	public boolean isLoggedIn() {
		return false;
	}

}
