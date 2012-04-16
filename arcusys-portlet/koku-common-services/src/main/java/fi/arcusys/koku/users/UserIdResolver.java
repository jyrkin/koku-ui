package fi.arcusys.koku.users;

import org.apache.log4j.Logger;

import fi.arcusys.koku.exceptions.KokuServiceException;
import fi.arcusys.koku.util.PortalRole;

public class UserIdResolver {
	
	private static final Logger LOG = Logger.getLogger(UserIdResolver.class);

	private final KokuUserService userService;
	
	public UserIdResolver() {
		userService = new KokuUserService();
	}

	/**
	 * Returns unique userId by given username. 
	 * If user not found returns null.
	 * 
	 * @param username or null if user not found
	 * @return userId
	 */
	public String getUserId(String username, PortalRole role) throws KokuServiceException {
		String userId = null;
		if (username == null || username.isEmpty()) {
			return null;
		}
		
		long start = System.nanoTime();
		switch (role) {
		case CITIZEN:
			userId = userService.getUserUidByKunpoName(username);
			break;
		case EMPLOYEE:
			userId = userService.getUserUidByLooraName(username);
			break;
		default:
			userId = null;
			break;
		}
		LOG.info("getUserId  - "+((System.nanoTime()-start)/1000/1000) + "ms");

		if (userId == null) {
			LOG.info("Couldn't find userId by given username: " + username + " PortalRole: " + role);
		}
		return userId;
	}
}
