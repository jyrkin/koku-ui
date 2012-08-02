package fi.arcusys.koku.proxy;

/**
 * Simple proxy authentication interface
 *
 * @author Toni Turunen
 *
 */
public interface ProxyAuthentication {

	String getUsername();
	boolean isLoggedIn();
	boolean isAllowedToAccessFile(String file);

}
