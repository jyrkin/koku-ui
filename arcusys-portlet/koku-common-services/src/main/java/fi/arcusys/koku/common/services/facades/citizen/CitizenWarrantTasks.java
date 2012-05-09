package fi.arcusys.koku.common.services.facades.citizen;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.warrants.model.KokuAuthorizationSummary;

/**
 * Citizen Warrants / Authorizations (Valtakirjat)
 * 
 * @author Toni Turunen
 *
 */
public interface CitizenWarrantTasks {
	
	/**
	 * Returns citizen received warrants
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuAuthorizationSummary> getRecievedWarrants(String uid, Page page) throws KokuServiceException;
	
	/**
	 * Return citizen sent warrants (citizen created)
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuAuthorizationSummary> getSentWarrants(String uid, Page page) throws KokuServiceException;
}
