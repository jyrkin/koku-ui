package fi.arcusys.koku.common.services.facades.employee;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.RequestTasks;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.requests.models.KokuRequest;

/**
 * Employee created requests (Pyynnöt)
 * 
 * @author Toni Turunen
 *
 */
public interface EmployeeRequestTasks extends RequestTasks {
	
	/**
	 * Returns list of employee sent unanswered requests. (Pyynnöt - Lähetetyt -  Avoimet) 
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuRequest> getRequestsSentOpen(String uid, Page page) throws KokuCommonException;
	
	/**
	 * Returns list of employee sent answered requests (Pyynnöt - Lähetety - Valmiit)
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuRequest> getRequestsSentDone(String uid, Page page) throws KokuCommonException;

}
