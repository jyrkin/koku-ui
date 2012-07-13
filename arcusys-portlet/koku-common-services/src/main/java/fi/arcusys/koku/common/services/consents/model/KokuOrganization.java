package fi.arcusys.koku.common.services.consents.model;

public class KokuOrganization {

	private String organizationId;
	private String organizationName;

	public KokuOrganization(fi.arcusys.koku.tiva.citizenservice.Organization organization) {
		this.setOrganizationId(organization.getOrganizationId());
		this.setOrganizationName(organization.getOrganizationName());
	}

	public KokuOrganization(fi.arcusys.koku.tiva.employeeservice.Organization organization) {
		this.setOrganizationId(organization.getOrganizationId());
		this.setOrganizationName(organization.getOrganizationName());
	}

	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((organizationId == null) ? 0 : organizationId.hashCode());
		result = prime
				* result
				+ ((organizationName == null) ? 0 : organizationName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KokuOrganization other = (KokuOrganization) obj;
		if (organizationId == null) {
			if (other.organizationId != null)
				return false;
		} else if (!organizationId.equals(other.organizationId))
			return false;
		if (organizationName == null) {
			if (other.organizationName != null)
				return false;
		} else if (!organizationName.equals(other.organizationName))
			return false;
		return true;
	}

}
