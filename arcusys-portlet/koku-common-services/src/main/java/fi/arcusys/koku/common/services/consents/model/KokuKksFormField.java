package fi.arcusys.koku.common.services.consents.model;

public class KokuKksFormField {

	private String fieldId;
	private String fieldName;

	public KokuKksFormField(fi.arcusys.koku.tiva.citizenservice.KksFormField formField) {
		this.setFieldId(formField.getFieldId());
		this.setFieldName(formField.getFieldName());
	}

	public KokuKksFormField(fi.arcusys.koku.tiva.employeeservice.KksFormField formField) {
		this.setFieldId(formField.getFieldId());
		this.setFieldName(formField.getFieldName());
	}

	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldId == null) ? 0 : fieldId.hashCode());
		result = prime * result
				+ ((fieldName == null) ? 0 : fieldName.hashCode());
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
		KokuKksFormField other = (KokuKksFormField) obj;
		if (fieldId == null) {
			if (other.fieldId != null)
				return false;
		} else if (!fieldId.equals(other.fieldId))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}

}
