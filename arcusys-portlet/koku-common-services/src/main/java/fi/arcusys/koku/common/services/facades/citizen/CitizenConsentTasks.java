package fi.arcusys.koku.common.services.facades.citizen;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.consents.model.KokuConsent;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;

/**
 * Citizen received consents (Suostumukset)
 * 
 * @author Toni Turunen
 *
 */
public interface CitizenConsentTasks {
	
	/**
	 * Returns list of recieved new consents
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuConsent> getNewConsents(String uid, Page page) throws KokuServiceException;
	
	/**
	 * Returns list of received and replied consents
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuConsent> getRepliedConsents(String uid, Page page) throws KokuServiceException;

	/**
	 * Returns list of old consents (expired?)
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuConsent> getOldConsents(String uid, Page page) throws KokuServiceException;
}
