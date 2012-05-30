package fi.arcusys.koku.common.services.facades.employee;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.appointments.model.EmployeeAppointment;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;

/**
 * Appointments for Employee
 * 
 * @author Toni Turunen
 *
 */
public interface EmployeeAppointmentTasks {
	
	/**
	 * Returns list of open appointments (not answered yet)
	 * 
	 * @param uid
	 * @param targetPersonSSN
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<EmployeeAppointment> getOpenAppoiments(String uid, AppointmentsSearchCriteria criteria, Page page) throws KokuServiceException;
	
	/**
	 * Return list of answered or old appointments 
	 * 
	 * @param uid
	 * @param targetPersonSSN
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<EmployeeAppointment> getReadyAppointments(String uid, AppointmentsSearchCriteria criteria, Page page) throws KokuServiceException;
	
	public interface AppointmentsSearchCriteria {
		String getTargetPersonSsn();
	}
}
