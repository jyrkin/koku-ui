package fi.arcusys.koku.common.services.facades.employee;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.warrants.model.KokuAuthorizationSummary;

/**
 * Browse citizen received or sent Warrants / Authorizations (Valtakirjat)
 * 
 * @author Toni Turunen
 *
 */
public interface EmployeeWarrantTasks {
	
	
	/**
	 * Returns citizen received/sent warrants (citizen created)
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuAuthorizationSummary> getWarrants(WarrantSearchCriteria criteria, Page page) throws KokuServiceException;
	

	/**
	 * SearchCriteria for warrants (authorizations / valtakirjat)
	 * 
	 * NOTE:TemplateName must be primary search param before templateId when searching!
	 * 
	 * @author Toni Turunen
	 *
	 */
	public interface WarrantSearchCriteria {
		String getTemplateName();
		Long getAuthorizationTemplateId();
		String getRecipientUid();
		String getSenderUid();
		String getTargetPersonUid();
	}
}
