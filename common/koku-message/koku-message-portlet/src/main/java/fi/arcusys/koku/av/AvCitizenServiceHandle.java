package fi.arcusys.koku.av;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

import fi.arcusys.koku.av.citizenservice.AppointmentRespondedTO;
import fi.arcusys.koku.av.citizenservice.AppointmentSlot;
import fi.arcusys.koku.av.citizenservice.AppointmentWithTarget;
import fi.arcusys.koku.util.MessageUtil;

/**
 * Handles appointments related operations
 * @author Jinhua Chen
 * Aug 22, 2011
 */
public class AvCitizenServiceHandle {
	
	private Logger LOG = Logger.getLogger(AvCitizenServiceHandle.class);
	
	private ResourceBundleMessageSource messageSource;
	
	private AvCitizenService acs;
	private String loginUser;
	
	/**
	 * Constructor and initialization
	 */
	public AvCitizenServiceHandle() {
		acs = new AvCitizenService();
	}
	
	public AvCitizenServiceHandle(String loginUser) {
		this.loginUser = loginUser;
		acs = new AvCitizenService();
	}
	
	/**
	 * Gets appointments and generates the appointment data model
	 * @param user user name
	 * @param startNum start index of appointment
	 * @param maxNum maximum number of appointments
	 * @param taskType task type requested
	 * @return a list of appointments
	 */
	public List<KokuAppointment> getAppointments(String user, int startNum, int maxNum, String taskType) {
		List<AppointmentWithTarget> appSummaryList;
		List<KokuAppointment> appList = new ArrayList<KokuAppointment>();
		
		if(taskType.equals("app_inbox_citizen"))
			appSummaryList = acs.getAssignedAppointments(user, startNum, maxNum);
		else if(taskType.equals("app_response_citizen"))
			appSummaryList = acs.getRespondedAppointments(user, startNum, maxNum);
		else
			return appList;
				
		//KokuAppointment kokuAppointment;
		CitizenAppointment kokuAppointment;
		Iterator<AppointmentWithTarget> it = appSummaryList.iterator();
		while(it.hasNext()) {
			AppointmentWithTarget appSummary = it.next();
			kokuAppointment = new CitizenAppointment();
			kokuAppointment.setAppointmentId(appSummary.getAppointmentId());
			kokuAppointment.setSender(appSummary.getSender());
			kokuAppointment.setSubject(appSummary.getSubject());
			kokuAppointment.setDescription(appSummary.getDescription());
			kokuAppointment.setTargetPerson(appSummary.getTargetPerson());
			kokuAppointment.setStatus(appSummary.getStatus().toString());
			appList.add(kokuAppointment);		
		}
		
		return appList;
	}

	/**
	 * Gets the appointment in detail
	 * @param appointmentId appointment id
	 * @return detailed citizen appointment
	 */
	public CitizenAppointment getAppointmentById(String appointmentId, String targetUser) {
		long  appId = (long) Long.parseLong(appointmentId);
		CitizenAppointment ctzAppointment = new CitizenAppointment();
		AppointmentRespondedTO appointment = acs.getAppointmentRespondedById(appId, targetUser);
		ctzAppointment.setAppointmentId(appointment.getAppointmentId());
		ctzAppointment.setSender(appointment.getSender());
		ctzAppointment.setSubject(appointment.getSubject());
		ctzAppointment.setDescription(appointment.getDescription());
		if(appointment.getStatus() != null) {
			ctzAppointment.setStatus(localizeActionRequestStatus(appointment.getStatus().toString()));
		}		
		ctzAppointment.setSlot(formatSlot(appointment.getApprovedSlot()));
		ctzAppointment.setReplier(appointment.getReplier());
		ctzAppointment.setReplierComment(appointment.getReplierComment());
		ctzAppointment.setTargetPerson(appointment.getTargetPerson());
		
		return ctzAppointment;		
	}
	
	/**
	 * Gets the total number of appointments
	 * @param user user name
	 * @param taskType task type requested 
	 * @return the total number of appointments
	 */
	public int getTotalAppointmentsNum(String user, String taskType) {
		
		if(taskType.equals("app_inbox_citizen"))// for citizen
			return acs.getTotalAssignedAppointmentNum(user);
		else if(taskType.equals("app_response_citizen"))
			return acs.getTotalRespondedAppointmentNum(user);
		else
			return 0;
	}
	
	/**
	 * Formats the slot data model
	 * @param appSlot slot of appointment
	 * @return formatted slot data model
	 */
	private Slot formatSlot(AppointmentSlot appSlot) {
		Slot slot = new Slot();
		String dateString = "d.M.yyyy";
		String timeString = "HH:mm:ss";
		slot.setSlotNumber(appSlot.getSlotNumber());
		slot.setAppointmentDate(MessageUtil.formatDateByString(appSlot.getAppointmentDate(), dateString));
		slot.setStartTime(MessageUtil.formatDateByString(appSlot.getStartTime(), timeString));
		slot.setEndTime(MessageUtil.formatDateByString(appSlot.getEndTime(), timeString));
		slot.setLocation(appSlot.getLocation());
		
		return slot;
	}
	
	/**
	 * Cancels appointments
	 * @param appointmentIdStr
	 * @param targetPerson
	 * @param comment
	 * @return operation response
	 */
	public String cancelAppointments(String appointmentIdStr, String targetPerson, String comment) {
		long  appId = (long) Long.parseLong(appointmentIdStr);
		
		try {
			acs.cancelAppointment(appId, targetPerson, loginUser, comment);
			return MessageUtil.RESPONSE_OK;
		} catch(RuntimeException e) {
			return MessageUtil.RESPONSE_FAIL;
		}		
	}
	

	private String localizeActionRequestStatus(String appointmentStatus) {
		if (messageSource == null) {
			LOG.warn("MessageSource is null. Localization doesn't work properly");
			return appointmentStatus;
		}
		Locale locale = MessageUtil.getLocale();
		try {
			if (appointmentStatus.equals("Created")) {
				return messageSource.getMessage("AppointmentStatus.Created", null, locale);
			} else if (appointmentStatus.equals("Approved")) {
				return messageSource.getMessage("AppointmentStatus.Approved", null, locale);
			} else if (appointmentStatus.equals("Declined")) {
				return messageSource.getMessage("AppointmentStatus.Declined", null, locale);
			} else if (appointmentStatus.equals("Deleted")) {
				return messageSource.getMessage("AppointmentStatus.Deleted", null, locale);
			} else {				
				return appointmentStatus;
			}
		} catch (NoSuchMessageException nsme) {
			LOG.warn("Coulnd't find localized message for '" +appointmentStatus +"'. Localization doesn't work properly");
			return appointmentStatus;
		}
	}

	public final ResourceBundleMessageSource getMessageSource() {
		return messageSource;
	}

	public final void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
}
