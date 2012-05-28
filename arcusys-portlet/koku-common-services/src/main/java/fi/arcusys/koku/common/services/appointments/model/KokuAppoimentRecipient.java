package fi.arcusys.koku.common.services.appointments.model;

import java.util.ArrayList;
import java.util.List;

import fi.arcusys.koku.av.employeeservice.AppointmentReceipientTO;
import fi.arcusys.koku.av.employeeservice.User;
import fi.arcusys.koku.common.services.users.KokuUser;

public class KokuAppoimentRecipient {
	
	private List<KokuUser> recipients;
	private final KokuUser targetPerson;	
	
	public KokuAppoimentRecipient(AppointmentReceipientTO recipient) {
		targetPerson = new KokuUser(recipient.getTargetPersonUserInfo());
		for (User recipientUser : recipient.getReceipientUserInfos()) {
			getRecipients().add(new KokuUser(recipientUser));
		}
	}

	/**
	 * @return the recipients
	 */
	public final List<KokuUser> getRecipients() {
		if (recipients == null) {
			recipients = new ArrayList<KokuUser>();
		}
		return recipients;
	}

	/**
	 * @return the targetPerson
	 */
	public final KokuUser getTargetPerson() {
		return targetPerson;
	}
}
