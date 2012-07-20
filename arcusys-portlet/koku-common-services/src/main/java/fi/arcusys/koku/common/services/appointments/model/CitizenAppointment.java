package fi.arcusys.koku.common.services.appointments.model;

import fi.arcusys.koku.common.services.users.KokuUser;

/**
 * Appointment data model for citizen
 * @author Jinhua Chen
 * Aug 22, 2011
 */
public class CitizenAppointment extends KokuAppointment{

	private Slot slot;
	private KokuUser replierUser;
	private String replierComment;
	private KokuUser targetPersonUser;
	private boolean modifiable;

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

	/**
	 * @return the replierUser
	 */
	public final KokuUser getReplierUser() {
		return replierUser;
	}

	/**
	 * @param replierUser the replierUser to set
	 */
	public final void setReplierUser(KokuUser replierUser) {
		this.replierUser = replierUser;
	}

    public Slot getSlot() {
		return slot;
	}

	public String getReplierComment() {
		return replierComment;
	}

	public void setSlot(Slot slot) {
		this.slot = slot;
	}

	public void setReplierComment(String replierComment) {
		this.replierComment = replierComment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (modifiable ? 1231 : 1237);
		result = prime * result
				+ ((replierComment == null) ? 0 : replierComment.hashCode());
		result = prime * result
				+ ((replierUser == null) ? 0 : replierUser.hashCode());
		result = prime * result + ((slot == null) ? 0 : slot.hashCode());
		result = prime
				* result
				+ ((targetPersonUser == null) ? 0 : targetPersonUser.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CitizenAppointment other = (CitizenAppointment) obj;
		if (modifiable != other.modifiable)
			return false;
		if (replierComment == null) {
			if (other.replierComment != null)
				return false;
		} else if (!replierComment.equals(other.replierComment))
			return false;
		if (replierUser == null) {
			if (other.replierUser != null)
				return false;
		} else if (!replierUser.equals(other.replierUser))
			return false;
		if (slot == null) {
			if (other.slot != null)
				return false;
		} else if (!slot.equals(other.slot))
			return false;
		if (targetPersonUser == null) {
			if (other.targetPersonUser != null)
				return false;
		} else if (!targetPersonUser.equals(other.targetPersonUser))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CitizenAppointment [slot=" + slot + ", replierUser="
				+ replierUser + ", replierComment=" + replierComment
				+ ", targetPersonUser=" + targetPersonUser + ", modifiable="
				+ modifiable + "]";
	}

	public boolean isModifiable() {
		return modifiable;
	}

	public void setModifiable(boolean modifiable) {
		this.modifiable = modifiable;
	}



}
