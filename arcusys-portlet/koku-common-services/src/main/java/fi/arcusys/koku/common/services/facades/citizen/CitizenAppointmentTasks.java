package fi.arcusys.koku.common.services.facades.citizen;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.appointments.model.KokuAppointment;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;

/**
 * Appointments for Citizens
 * 
 * @author Toni Turunen
 *
 */
public interface CitizenAppointmentTasks {
	
	/**
	 * Returns list of new unanswered appointments 
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuAppointment> getNewAppointments(String uid, Page page) throws KokuServiceException;
	
	/**
	 * Returns list of already answered appointments
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuAppointment> getAnsweredAppointments(String uid, Page page) throws KokuServiceException;
	
	/**
	 * Returns list of old appointments (history)
	 * 
	 * @param uid
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuAppointment> getOldAppointments(String uid, Page page) throws KokuServiceException;
}
