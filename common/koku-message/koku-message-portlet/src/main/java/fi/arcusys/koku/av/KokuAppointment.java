package fi.arcusys.koku.av;

/**
 * Summary appointment data model
 * @author Jinhua Chen
 * Aug 23, 2011
 */
public class KokuAppointment {

	private long appointmentId;
	private String sender;
	private String subject;
	private String description;	
	
	/* getters */
	public long getAppointmentId() {
		return appointmentId;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getDescription() {
		return description;
	}
	
	/* setters */
	public void setAppointmentId(long appointmentId) {
		this.appointmentId = appointmentId;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}


