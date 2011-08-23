package fi.arcusys.koku.av;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fi.arcusys.koku.av.EmployeeAppointment.RejectedUser;
import fi.arcusys.koku.av.employeeservice.Appointment;
import fi.arcusys.koku.av.employeeservice.Appointment.AcceptedSlots;
import fi.arcusys.koku.av.employeeservice.Appointment.AcceptedSlots.Entry;
import fi.arcusys.koku.av.employeeservice.AppointmentReceipientTO;
import fi.arcusys.koku.av.employeeservice.AppointmentSlot;
import fi.arcusys.koku.av.employeeservice.AppointmentSummary;
import fi.arcusys.koku.util.MessageUtil;

/**
 * Handles the appointments for employee
 * @author Jinhua Chen
 * Aug 22, 2011
 */
public class AvEmployeeServiceHandle {
	private AvEmployeeService aes;
	
	public AvEmployeeServiceHandle() {
		aes = new AvEmployeeService();
	}
	
	/**
	 * Gets summary appointments
	 * @param user user
	 * @param startNum start index of appointment
	 * @param maxNum maximum number of appointments
	 * @param taskType task type requested
	 * @return a list of summary appointments
	 */
	public List<KokuAppointment> getAppointments(String user, int startNum, int maxNum, String taskType) {

		List<AppointmentSummary> appSummaryList;
		List<KokuAppointment> appList = new ArrayList<KokuAppointment>();
		
		if(taskType.equals("app_inbox_employee"))
			appSummaryList = aes.getCreatedAppointments(user, startNum, maxNum);
		else if(taskType.equals("app_response_employee"))
			appSummaryList = aes.getRespondedAppointments(user, startNum, maxNum);
		else
			return appList;
			
		KokuAppointment kokuAppointment;
		
		Iterator<AppointmentSummary> it = appSummaryList.iterator();
		while(it.hasNext()) {
			AppointmentSummary appSummary = it.next();
			kokuAppointment = new KokuAppointment();
			kokuAppointment.setAppointmentId(appSummary.getAppointmentId());
			kokuAppointment.setSender(appSummary.getSender());
			kokuAppointment.setSubject(appSummary.getSubject());
			kokuAppointment.setDescription(appSummary.getDescription());
			
			appList.add(kokuAppointment);		
		}
		
		return appList;
	}

	/**
	 * Gets detailed appointment which is used to be presented
	 * @param appointmentId appointment id
	 * @return detailed appointment for employee
	 */
	public EmployeeAppointment getAppointmentById(String appointmentId) {
		 
		long  appId = (long) Long.parseLong(appointmentId);
		EmployeeAppointment empAppointment = new EmployeeAppointment();
		Appointment appointment = aes.getAppointmentById(appId);
		empAppointment.setAppointmentId(appointment.getAppointmentId());
		empAppointment.setSender(appointment.getSender());
		empAppointment.setSubject(appointment.getSubject());
		empAppointment.setDescription(appointment.getDescription());
		empAppointment.setStatus(appointment.getStatus());		
		empAppointment.setAcceptedSlots(appointment.getAcceptedSlots());
		empAppointment.setRecipients(appointment.getRecipients());
		empAppointment.setUsersRejected(appointment.getUsersRejected());
		empAppointment.setRejectedUsers(formatRejectedUsers(appointment.getUsersRejected(), appointment.getRecipients()));
		List<Slot> allSlots = formatSlots(appointment.getSlots(), appointment.getAcceptedSlots(), appointment.getRecipients());		
		setSlots(empAppointment, allSlots);
		
		return empAppointment;		
	}
	
	/**
	 * Gets the total number of appointments
	 * @param user user
	 * @param taskType task type requested
	 * @return the number of appointments
	 */
	public int getTotalAppointmentsNum(String user, String taskType) {
		
		if(taskType.equals("app_inbox_employee"))// for employee
			return aes.getTotalCreatedAppointmentNum(user);
		else if(taskType.equals("app_response_employee"))
			return aes.getTotalProcessedAppointments(user);
		else
			return 0;
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
		Slot slot;
		String dateString = "d.M.yyyy";
		String timeString = "HH:mm:ss";
		Iterator<AppointmentSlot> it = appSlots.iterator();
		
		while(it.hasNext()) {
			slot = new Slot();
			AppointmentSlot appSlot = it.next();
			slot.setAppointmentId(appSlot.getAppointmentId());
			slot.setSlotNumber(appSlot.getSlotNumber());
			slot.setAppointmentDate(MessageUtil.formatDateByString(appSlot.getAppointmentDate(), dateString));
			slot.setStartTime(MessageUtil.formatDateByString(appSlot.getStartTime(), timeString));
			slot.setEndTime(MessageUtil.formatDateByString(appSlot.getEndTime(), timeString));
			slot.setLocation(appSlot.getLocation());
			slot.setComment(appSlot.getComment());
			
			slots.add(slot);
		}
		// go through the accepted slots and set related information
		Iterator<Entry> itEntry = acceptedSlots.getEntry().iterator();
		
		while(itEntry.hasNext()) {
			Entry entry = itEntry.next();
			int slotId = entry.getKey();
			String targetPerson;
			
			for(int i=0; i < slots.size(); i++) {
				slot = slots.get(i);
				if(slot.getSlotNumber() == slotId) {
					slot.setApproved(true);
					targetPerson = entry.getValue();
					slot.setTargetPerson(targetPerson);
					slot.setRecipients(mapRecipients(targetPerson, recipients));
					break;
				}
			}
		}
		
		return slots;
	}
	
	/**
	 * Finds the approved slots and unapproved slots and sets them afterwards
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
			if(slot.getApproved() == true) {
				approvedSlots.add(slot);
			}else {
				unapprovedSlots.add(slot);
			}
		}
		
		empAppointment.setApprovedSlots(approvedSlots);
		empAppointment.setUnapprovedSlots(unapprovedSlots);	
	}
	/**
	 * Maps the recipients with the given targetPerson and convert the
	 * recipients list to string
	 * @param targetPerson target person
	 * @param recipients AppointmentReceipientTO recipients
	 * @return recipients string corresponding target person 
	 */
	private String mapRecipients(String targetPerson, List<AppointmentReceipientTO> recipients) {
		String recipientsStr = "";
		Iterator<AppointmentReceipientTO> it = recipients.iterator();
		AppointmentReceipientTO receipientTo;
		
		while(it.hasNext()) {
			receipientTo = it.next();
			if(receipientTo.getTargetPerson().equals(targetPerson)) {
				recipientsStr = formatRecipients(receipientTo.getReceipients());
				break;
			}
		}
		
		return recipientsStr;
	}
	
	/**
	 * Formats a list of string to string
	 * @param recipients a list of string
	 * @return recipients string
	 */
	private String formatRecipients(List<String> recipients) {
		Iterator<String> it = recipients.iterator();
		String recipientStr = "";
		String recipient;
		
		while(it.hasNext()) {
			recipient = it.next();
			
			if(recipient.trim().length() > 0)
				recipientStr += recipient + ", ";		
		}
		
		if(recipientStr.lastIndexOf(",") > 0)
			recipientStr = recipientStr.substring(0, recipientStr.length()-2);
		
		return recipientStr;
	}
	
	/**
	 * Formats the rejected users with corresponding target person
	 * @param userRejected user who rejected slot
	 * @param recipients recipients list
	 * @return a list of rejected users
	 */
	private List<RejectedUser> formatRejectedUsers(List<String> userRejected, List<AppointmentReceipientTO> recipients) {
		List<RejectedUser> rejectedUsers = new ArrayList<RejectedUser>();
		RejectedUser user;
		Iterator<String> it = userRejected.iterator();
		String targetPerson;
		String recipientsStr;
		
		while(it.hasNext()) {
			targetPerson = it.next();
			recipientsStr = mapRecipients(targetPerson, recipients);
			user = new RejectedUser();
			user.setTargetPerson(targetPerson);
			user.setRecipients(recipientsStr);
			rejectedUsers.add(user);
		}
		
		return rejectedUsers;
	}
	
}