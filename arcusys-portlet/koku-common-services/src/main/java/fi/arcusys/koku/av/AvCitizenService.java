package fi.arcusys.koku.av;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import fi.arcusys.koku.av.citizenservice.AppointmentRespondedTO;
import fi.arcusys.koku.av.citizenservice.AppointmentWithTarget;
import fi.arcusys.koku.av.citizenservice.KokuKunpoAppointmentService_Service;
import fi.arcusys.koku.tiva.tietopyynto.employee.KokuEmployeeTietopyyntoService;
import fi.arcusys.koku.util.PropertiesUtil;
import fi.koku.settings.KoKuPropertiesUtil;

/**
 * Retrieves appointment data and related operations via web services
 * @author Jinhua Chen
 * Aug 22, 2011
 */
public class AvCitizenService {
		
	private static final Logger LOG = Logger.getLogger(AvCitizenService.class);		
	public static final URL AV_CITIZEN_WSDL_LOCATION;
	
	static {
		try {
			LOG.info("AvCitizenService WSDL location: " + KoKuPropertiesUtil.get("AvCitizenService"));
			AV_CITIZEN_WSDL_LOCATION =  new URL(KoKuPropertiesUtil.get("AvCitizenService"));
		} catch (MalformedURLException e) {
			LOG.error("Failed to create AvCitizenService WSDL url! Given URL address is not valid!");
			throw new ExceptionInInitializerError(e);
		}
	}
	
	private KokuKunpoAppointmentService_Service as;
	
	/**
	 * Constructor and initialization
	 */
	public AvCitizenService() {
		this.as = new KokuKunpoAppointmentService_Service(AV_CITIZEN_WSDL_LOCATION);
	}
	
	/**
	 * Gets the assigned appointments
	 * @param user user name
	 * @param startNum the start index
	 * @param maxNum the maximum number
	 * @return a list of summary of appointments
	 */
	public List<AppointmentWithTarget> getAssignedAppointments(String user, int startNum, int maxNum) {
		return as.getKokuKunpoAppointmentServicePort().getAssignedAppointments(user, startNum, maxNum);
	}
	
	/**
	 * Gets the responded appointments
	 * @param user user name
	 * @param startNum the start index
	 * @param maxNum the maximum number
	 * @return a list of summary of appointments
	 */
	public List<AppointmentWithTarget> getRespondedAppointments(String user, int startNum, int maxNum) {
		return as.getKokuKunpoAppointmentServicePort().getRespondedAppointments(user, startNum, maxNum);
	}
	
	/**
	 * Gets the already expired or cancelled appointments
	 * 
	 * @param userId
	 * @param startNum
	 * @param maxNum
	 * @return a list of summary of appointments
	 */
	public List<AppointmentWithTarget>  getOldAppointments(String userId, int startNum, int maxNum) {
		return as.getKokuKunpoAppointmentServicePort().getOldAppointments(userId, startNum, maxNum);
	}
	
	
	/**
	 * Gets the amount of assigned appointments
	 * @param user user name
	 * @return the number of assigned appointments
	 */
	public int getTotalAssignedAppointmentNum(String user) {
		return as.getKokuKunpoAppointmentServicePort().getTotalAssignedAppointments(user);
	}
	
	/**
	 * Gets the amount of responded appointments
	 * @param user user name
	 * @return the number of responded appointments
	 */
	public int getTotalRespondedAppointmentNum(String user) {
		return as.getKokuKunpoAppointmentServicePort().getTotalRespondedAppointments(user);
	}
	
	/**
	 * Gets the amount of old (expired/cancelled) appointments
	 * @param user user name
	 * @return the number of old appointments
	 */
	public int getTotalOldAppointments(String userId) {
		return as.getKokuKunpoAppointmentServicePort().getTotalOldAppointments(userId);
	}
	
	/**
	 * Gets the responded appointment in detail
	 * @param appointmentId appointment id
	 * @return detailed appointment
	 */
	public AppointmentRespondedTO getAppointmentRespondedById(long appointmentId, String targetUser) {
		return as.getKokuKunpoAppointmentServicePort().getAppointmentRespondedById(appointmentId, targetUser);
	}
	
	/**
	 * Cancels appointment
	 * @param appointmentId
	 * @param targetUser
	 * @param user
	 * @param comment
	 */
	public void cancelAppointment(long appointmentId, String targetUser, String user, String comment) {
		as.getKokuKunpoAppointmentServicePort().cancelRespondedAppointment(appointmentId, targetUser, user, comment);
	}
	

}
