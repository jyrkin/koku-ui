package fi.arcusys.koku.common.services.facades.employee;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.appointments.model.KokuAppointment;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;

/**
 * Browse applications (päivähoitohakemus?)
 * 
 * @author Toni Turunen
 *
 */
public interface EmployeeApplicationsTasks {
	
	/**
	 * Returns list of created applications
	 * 
	 * @param uid
	 * @param criteria
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuAppointment> getApplications(String uid, ApplicationSearchCriteria criteria, Page page) throws KokuServiceException;
	
	public interface ApplicationSearchCriteria {
		Long getApplicationTemplateId();
	}
}
