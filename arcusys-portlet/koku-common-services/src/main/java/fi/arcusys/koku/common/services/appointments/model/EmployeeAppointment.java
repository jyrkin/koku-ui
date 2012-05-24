package fi.arcusys.koku.common.services.appointments.model;

import java.util.ArrayList;
import java.util.List;

import fi.arcusys.koku.av.employeeservice.Appointment.AcceptedSlots;
import fi.arcusys.koku.av.employeeservice.AppointmentReceipientTO;
import fi.arcusys.koku.common.services.users.KokuUser;

/**
 * Appointment data model for employee
 * @author Jinhua Chen
 * Aug 22, 2011
 */
public class EmployeeAppointment extends KokuAppointment {

	private final List<Slot> approvedSlots = new ArrayList<Slot>();
	private final List<Slot> unapprovedSlots = new ArrayList<Slot>();
	private final List<AppointmentReceipientTO> recipients = new ArrayList<AppointmentReceipientTO>();
	private AcceptedSlots acceptedSlots;
	private final List<UserWithTarget> rejectedUsers = new ArrayList<UserWithTarget>();
	private final List<UserWithTarget> unrespondedUsers = new ArrayList<UserWithTarget>();
	private final List<KokuAppointmentUserRejected> usersRejected = new ArrayList<KokuAppointmentUserRejected>();
	private String senderRole;
	
	/* getters */	
	public List<AppointmentReceipientTO> getRecipients() {
		return recipients;
	}
		
	public List<Slot> getApprovedSlots() {
		return approvedSlots;
	}
	
	public List<Slot> getUnapprovedSlots() {
		return unapprovedSlots;
	}
	
	public AcceptedSlots getAcceptedSlots() {
		return acceptedSlots;
	}
		
	public List<UserWithTarget> getRejectedUsers() {
		return rejectedUsers;
	}
	
	public List<UserWithTarget> getUnrespondedUsers() {
		return unrespondedUsers;
	}
				
	public void setAcceptedSlots(AcceptedSlots acceptedSlots) {
		this.acceptedSlots = acceptedSlots;
	}
				
	public final List<KokuAppointmentUserRejected> getUsersRejected() {
		return usersRejected;
	}

	public String getSenderRole() {
		return senderRole;
	}

	public void setSenderRole(String senderRole) {
		this.senderRole = senderRole;
	}

	public static class UserWithTarget {
		private KokuUser targetPersonUser;
		private List<KokuUser> recipientsUsers;
		
		/**
		 * @return the targetPersonUser
		 */
		public final KokuUser getTargetPersonUser() {
			return targetPersonUser;
		}

		/**
		 * @param targetPersonUser the targetPersonUser to set
		 */
		public final void setTargetPersonUser(KokuUser targetPersonUser) {
			this.targetPersonUser = targetPersonUser;
		}

		public List<KokuUser> getRecipientUsers() {
			if (recipientsUsers == null) {
				recipientsUsers = new ArrayList<KokuUser>();
			}
			return recipientsUsers;
		}
	}
}

