package fi.arcusys.koku.common.services.facades;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.requests.models.KokuResponseSummary;

/**
 * Requests (Pyynnöt)
 * 
 * @author Toni Turunen
 *
 */
public interface RequestTasks {
	
	/**
	 * Requests replied ( Pyynnöt - Vastaanotetut - Vastatut)
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuResponseSummary> getRequestsRecievedReplied(String uid, Page page) throws KokuCommonException;
	
	/**
	 * Requests replied old ( Pyynnöt - Vastaanotetut -  vanhat (vastatut) )
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuResponseSummary> getRequestsRecieviedRepliedOld(String uid, Page page) throws KokuCommonException;

}
