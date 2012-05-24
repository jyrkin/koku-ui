package fi.arcusys.koku.common.services.appointments.employee;

import static fi.arcusys.koku.common.util.Constants.DATE;
import static fi.arcusys.koku.common.util.Constants.TIME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import fi.arcusys.koku.av.employeeservice.Appointment;
import fi.arcusys.koku.av.employeeservice.Appointment.AcceptedSlots;
import fi.arcusys.koku.av.employeeservice.Appointment.AcceptedSlots.Entry;
import fi.arcusys.koku.av.employeeservice.AppointmentCriteria;
import fi.arcusys.koku.av.employeeservice.AppointmentReceipientTO;
import fi.arcusys.koku.av.employeeservice.AppointmentSlot;
import fi.arcusys.koku.av.employeeservice.AppointmentSummary;
import fi.arcusys.koku.av.employeeservice.AppointmentSummaryStatus;
import fi.arcusys.koku.av.employeeservice.AppointmentUserRejected;
import fi.arcusys.koku.av.employeeservice.User;
import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.AbstractHandle;
import fi.arcusys.koku.common.services.appointments.model.EmployeeAppointment;
import fi.arcusys.koku.common.services.appointments.model.EmployeeAppointment.UserWithTarget;
import fi.arcusys.koku.common.services.appointments.model.KokuAppointment;
import fi.arcusys.koku.common.services.appointments.model.KokuAppointmentUserRejected;
import fi.arcusys.koku.common.services.appointments.model.Slot;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.employee.EmployeeAppointmentTasks;
import fi.arcusys.koku.common.services.facades.impl.ResultListImpl;
import fi.arcusys.koku.common.services.users.KokuUser;
import fi.arcusys.koku.common.util.MessageUtil;


/**
 * Handles the appointments for employee
 * @author Jinhua Chen
 * Aug 22, 2011
 */
public class AvEmployeeServiceHandle extends AbstractHandle implements EmployeeAppointmentTasks {
	
	private static final Logger LOG = Logger.getLogger(AvEmployeeServiceHandle.class);
	
	private AvEmployeeService aes;
	
	/**
	 * Constructor and initialization
	 */
	public AvEmployeeServiceHandle(MessageSource messageSource) {
		super(messageSource);
		aes = new AvEmployeeService();
	}
	
	@Override
	public ResultList<EmployeeAppointment> getOpenAppoiments(String uid, AppointmentsSearchCriteria criteria, Page page)
			throws KokuServiceException {		
		final AppointmentCriteria wsCriteria = createAppointmentCriteria(criteria);
		final List<EmployeeAppointment> appointments = getAppointmentList(
					aes.getCreatedAppointments(
							uid,  
							wsCriteria, 
							page.getFirst(), 
							page.getLast()
					)
				);
		final int appointmentsTotal = aes.getTotalCreatedAppointmentNum(uid, wsCriteria);
		return new ResultListImpl<EmployeeAppointment>(appointments, appointmentsTotal, page);
	}

	@Override
	public ResultList<EmployeeAppointment> getReadyAppointments(String uid, AppointmentsSearchCriteria criteria, Page page)
			throws KokuServiceException {
		final AppointmentCriteria wsCriteria = createAppointmentCriteria(criteria);
		final List<EmployeeAppointment> appointments = getAppointmentList(
					aes.getProcessedAppointments(
							uid, 
							wsCriteria,
							page.getFirst(), 
							page.getLast()
					)
				);
		final int appointmentsTotal = aes.getTotalProcessedAppointments(uid, wsCriteria);
		return new ResultListImpl<EmployeeAppointment>(appointments, appointmentsTotal, page);	
	}	
	
	private AppointmentCriteria createAppointmentCriteria(AppointmentsSearchCriteria searchCriteria) {
		AppointmentCriteria criteria = null;
		if (searchCriteria != null && searchCriteria.getTargetPersonSsn() != null && !searchCriteria.getTargetPersonSsn().trim().isEmpty()) {
			criteria = new AppointmentCriteria();
			criteria.setTargetPersonHetu(searchCriteria.getTargetPersonSsn().trim());			
		}
		return criteria;
	}
	
	
	/**
	 * Gets summary appointments
	 * @param user user
	 * @param startNum start index of appointment
	 * @param maxNum maximum number of appointments
	 * @param taskType task type requested
	 * @return a list of summary appointments
	 */
	private List<EmployeeAppointment> getAppointmentList(List<Appointment> appList) {
		List<EmployeeAppointment> empAppList = new ArrayList<EmployeeAppointment>();					
		for (Appointment app : appList) 
		    empAppList.add(fillEmployeeAppointment(app, new EmployeeAppointment()));
		
		return empAppList;
	}

	/**
	 * Gets detailed appointment which is used to be presented
	 * @param appointmentId appointment id
	 * @return detailed appointment for employee
	 */
	public EmployeeAppointment getAppointmentById(String appointmentId) throws KokuServiceException {
		 
		long  appId = 0;
		try {
			appId = (long) Long.parseLong(appointmentId);
		} catch (NumberFormatException nfe) {
			throw new KokuServiceException("Invalid appointmentId. AppointmentId: '"+appointmentId+"'", nfe);
		}
		EmployeeAppointment empAppointment = new EmployeeAppointment();
		Appointment appointment = aes.getAppointmentById(appId);
		return fillEmployeeAppointment(appointment, empAppointment);
	}
	
	private EmployeeAppointment fillEmployeeAppointment(final Appointment appointment, final EmployeeAppointment empAppointment) {
	    empAppointment.setAppointmentId(appointment.getAppointmentId());
        empAppointment.setSenderUser(new KokuUser(appointment.getSenderUserInfo()));
        empAppointment.setSubject(appointment.getSubject());
        empAppointment.setDescription(appointment.getDescription());
        empAppointment.setStatus(localizeActionRequestStatus(appointment.getStatus()));     
        empAppointment.setAcceptedSlots(appointment.getAcceptedSlots());
        empAppointment.setSenderRole(appointment.getSenderRole());
        empAppointment.getRecipients().addAll(appointment.getRecipients());
        empAppointment.setCancellationComment(appointment.getCancelComment());
        empAppointment.getUsersRejected().addAll(convertUserRejectedToKokuUserRejected(appointment.getUsersRejectedWithComments()));
        empAppointment.getRejectedUsers().addAll(formatRejectedUsers(getUsers(appointment.getUsersRejectedWithComments()), appointment.getRecipients()));
        empAppointment.getUnrespondedUsers().addAll(calcUnrespondedUsers(appointment));
        List<Slot> allSlots = formatSlots(appointment.getSlots(), appointment.getAcceptedSlots(), appointment.getRecipients());     
        setSlots(empAppointment, allSlots);
        return empAppointment;
	}
	
	private List<KokuAppointmentUserRejected> convertUserRejectedToKokuUserRejected(List<AppointmentUserRejected> rejectedUsers) {
		if (rejectedUsers == null) {
			return new ArrayList<KokuAppointmentUserRejected>();
		}
		List<KokuAppointmentUserRejected> list = new ArrayList<KokuAppointmentUserRejected>();
		for (AppointmentUserRejected rejectedUser : rejectedUsers ) {
			list.add(new KokuAppointmentUserRejected(rejectedUser));
		}
		return list;
	}
	
	
	/**
	 * Generates slot object for client side user interface
	 * @param appSlots AppointmentSlot
	 * @param acceptedSlots AcceptedSlots
	 * @param recipients AppointmentReceipientTO
	 * @return a list of slots
	 */
	private List<Slot> formatSlots(List<AppointmentSlot> appSlots, AcceptedSlots acceptedSlots,
			List<AppointmentReceipientTO> recipients) {
		
		List<Slot> slots = new ArrayList<Slot>();
		/* Why GMT0? See issue KOKU-1210 - Ajanvarauksessa tapaamisten ajat muuttuvat */
		final TimeZone timeZone = TimeZone.getTimeZone("GMT+0:00");
		for (AppointmentSlot appSlot : appSlots) {
			Slot slot = new Slot();
			slot.setSlotNumber(appSlot.getSlotNumber());
			slot.setAppointmentDate(MessageUtil.formatDateByString(appSlot.getAppointmentDate(), DATE, timeZone));
			slot.setStartTime(MessageUtil.formatDateByString(appSlot.getStartTime(), TIME, timeZone));
			slot.setEndTime(MessageUtil.formatDateByString(appSlot.getEndTime(), TIME, timeZone));
			slot.setLocation(appSlot.getLocation());
			slot.setComment(appSlot.getComment());			
			slots.add(slot);
		}
		
		// go through the accepted slots and set related information (O²)
		for (Entry entry : acceptedSlots.getEntry()) {
			for (Slot slot : slots) {
				if(slot.getSlotNumber() == entry.getKey()) {
					slot.setApproved(true);
					KokuUser targetPerson = new KokuUser(entry.getValue());
					slot.setTargetPersonUser(targetPerson);
					slot.getRecipientUsers().addAll(mapRecipients(targetPerson, recipients));
					break;
				}
			}
		}		
		return slots;
	}
    
	/**
	 * Finds the approved slots and unapproved slots and sets them afterwards
	 * 
	 * @param empAppointment EmployeeAppointment
	 * @param slots all slots
	 */
	private void setSlots(EmployeeAppointment empAppointment, List<Slot> slots) {
		List<Slot> approvedSlots = new ArrayList<Slot>();
		List<Slot> unapprovedSlots = new ArrayList<Slot>();
		Slot slot;
		Iterator<Slot> it = slots.iterator();
		
		while(it.hasNext()) {
			slot = it.next();
			if (slot.getApproved() == true) {
				approvedSlots.add(slot);
			} else {
				unapprovedSlots.add(slot);
			}
		}
		
		empAppointment.getApprovedSlots().addAll(approvedSlots);
		empAppointment.getUnapprovedSlots().addAll(unapprovedSlots);	
	}
	
	/**
	 * Maps the recipients with the given targetPerson and convert the
	 * recipients list to string
	 * 
	 * @param targetPerson target person
	 * @param recipients AppointmentReceipientTO recipients
	 * @return recipients string corresponding target person 
	 */
	private List<KokuUser> mapRecipients(final KokuUser targetPerson, List<AppointmentReceipientTO> recipients) {
		final List<KokuUser> kokuRecipients = new ArrayList<KokuUser>();		
		for (AppointmentReceipientTO appReceipient : recipients) {
			KokuUser appRecepientUser = new KokuUser(appReceipient.getTargetPersonUserInfo());
			if (appRecepientUser.equals(targetPerson)) {
				for (User receipientUser : appReceipient.getReceipientUserInfos()) {
					kokuRecipients.add(new KokuUser(receipientUser));					
				}
				break;
			}
		}
		return kokuRecipients;
	}
	
	/**
	 * Formats the rejected users with corresponding target person
	 * 
	 * @param userRejected user who rejected slot
	 * @param recipients recipients list
	 * @return a list of rejected users
	 */
	private List<UserWithTarget> formatRejectedUsers(List<KokuUser> userRejected, List<AppointmentReceipientTO> recipients) {
		List<UserWithTarget> rejectedUsers = new ArrayList<UserWithTarget>();
		
		for(KokuUser rejectedUser : userRejected) {
			List<KokuUser> recipientsList = mapRecipients(rejectedUser, recipients);
			UserWithTarget user = new UserWithTarget();
			user.setTargetPersonUser(rejectedUser);
			user.getRecipientUsers().addAll(recipientsList);
			rejectedUsers.add(user);
		}
		return rejectedUsers;
	}
	
	private List<UserWithTarget> calcUnrespondedUsers(Appointment appointment) {
		List<UserWithTarget> unrespondedUsers = new ArrayList<UserWithTarget>();
		List<AppointmentUserRejected> userRejected = appointment.getUsersRejectedWithComments();
		AcceptedSlots acceptedSlots = appointment.getAcceptedSlots();

		for (AppointmentReceipientTO app : appointment.getRecipients()) {
			
			KokuUser targetPerson = new KokuUser(app.getTargetPersonUserInfo());
			
			if(!hasTargetPerson(targetPerson, acceptedSlots, userRejected)) {
				UserWithTarget user = new UserWithTarget();
				user.setTargetPersonUser(targetPerson);
				user.setTargetPersonUser(new KokuUser(app.getTargetPersonUserInfo()));
				for (User recipientUser : app.getReceipientUserInfos()) {
					user.getRecipientUsers().add(new KokuUser(recipientUser));					
				}
				unrespondedUsers.add(user);
			}
		}		
		return unrespondedUsers;
	}
	
	/**
     * @param users
     * @return
     */
    private List<KokuUser> getUsers(List<AppointmentUserRejected> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        
        final List<KokuUser> result = new ArrayList<KokuUser>(users.size());
        for (final AppointmentUserRejected user : users) {
            result.add(new KokuUser(user.getUserInfo()));
        }
        return result;
    }

    /**
	 * Checks that the targetPeson exists or not
	 * @param targetPerson
	 * @param acceptedSlots
	 * @param userRejected
	 * @return true or false
	 */
	private boolean hasTargetPerson(final KokuUser targetPerson, final AcceptedSlots acceptedSlots, final List<AppointmentUserRejected> userRejected) {
		for (Entry entry : acceptedSlots.getEntry()) {
			KokuUser target = new KokuUser(entry.getValue());			
			if(target.equals(targetPerson)) {
				return true;
			}
		}
		
		for (AppointmentUserRejected user : userRejected) {
			KokuUser target = new KokuUser(user.getUserInfo());			
			if(target.equals(targetPerson)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Cancels appointment
	 * @param appointmentIdStr
	 * @param comment
	 */
	public void cancelAppointments(long appId, String comment) throws KokuServiceException {		
			aes.cancelAppointment(appId, comment);
	}
	
	private String localizeActionRequestStatus(AppointmentSummaryStatus appointmentStatus) {
		if (getMessageSource() == null) {
			LOG.warn(MESSAGE_SOURCE_MISSING);
			return appointmentStatus.toString();
		}
		Locale locale = MessageUtil.getLocale();
		try {
			switch (appointmentStatus) {
			case APPROVED:
				return getMessageSource().getMessage("AppointmentStatus.Approved", null, locale);
			case CANCELLED:
				return getMessageSource().getMessage("AppointmentStatus.Cancelled", null, locale);				
			case CREATED:
				return getMessageSource().getMessage("AppointmentStatus.Created", null, locale);
			default:
				return appointmentStatus.toString();
			}					
		} catch (NoSuchMessageException nsme) {
			LOG.warn("Coulnd't find localized message for '" +appointmentStatus +"'. Localization doesn't work properly");
			return appointmentStatus.toString();
		}
	}

	
}
