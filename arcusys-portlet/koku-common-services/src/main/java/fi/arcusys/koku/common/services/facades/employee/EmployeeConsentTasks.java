package fi.arcusys.koku.common.services.facades.employee;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.consents.model.KokuConsent;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;

/**
 * Employee sent consents (Suostumukset)
 * 
 * @author Toni Turunen
 *
 */
public interface EmployeeConsentTasks {
	
	/**
	 * Returns list of employee sent consents 
	 * 
	 * @param uid
	 * @param criteria
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuConsent> getSentConsents(String uid, ConsentSearchCriteria criteria, Page page) throws KokuServiceException;	

	public interface ConsentSearchCriteria {
		Long getConsentTemplateId();
		String getRecipientUid();
	}
}
