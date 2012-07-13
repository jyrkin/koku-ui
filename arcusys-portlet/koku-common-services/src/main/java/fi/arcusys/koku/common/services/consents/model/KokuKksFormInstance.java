package fi.arcusys.koku.common.services.consents.model;

import java.util.ArrayList;
import java.util.List;

public class KokuKksFormInstance {

	private String instanceId;
	private String instanceName;
	private List<KokuKksFormField> fields = null;

	/* CitizenService wsdl generated KksFormInstance transformation constructor
	 * The two types are identical but because two version of them are generated
	 * from the wsdl's then two constructors are needed as well */
	public KokuKksFormInstance(fi.arcusys.koku.tiva.citizenservice.KksFormInstance formInstance) {
		this.setInstanceId(formInstance.getInstanceId());
		this.setInstanceName(formInstance.getInstanceName());
		if (formInstance.getFields() == null) {
			for (fi.arcusys.koku.tiva.citizenservice.KksFormField formField : formInstance.getFields()) {
				this.getFields().add(new KokuKksFormField(formField));
			}
		}
	}

	/* EmployeeService wsdl generated KksFormInstance transformation constructor */
	public KokuKksFormInstance(fi.arcusys.koku.tiva.employeeservice.KksFormInstance formInstance) {
		this.setInstanceId(formInstance.getInstanceId());
		this.setInstanceName(formInstance.getInstanceName());
		if (formInstance.getFields() == null) {
			for (fi.arcusys.koku.tiva.employeeservice.KksFormField formField : formInstance.getFields()) {
				this.getFields().add(new KokuKksFormField(formField));
			}
		}
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public List<KokuKksFormField> getFields() {
		if (this.fields == null) {
			this.setFields(new ArrayList<KokuKksFormField>());
		}
		return fields;
	}

	public void setFields(List<KokuKksFormField> fields) {
		this.fields = fields;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result
				+ ((instanceId == null) ? 0 : instanceId.hashCode());
		result = prime * result
				+ ((instanceName == null) ? 0 : instanceName.hashCode());
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
		KokuKksFormInstance other = (KokuKksFormInstance) obj;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (instanceId == null) {
			if (other.instanceId != null)
				return false;
		} else if (!instanceId.equals(other.instanceId))
			return false;
		if (instanceName == null) {
			if (other.instanceName != null)
				return false;
		} else if (!instanceName.equals(other.instanceName))
			return false;
		return true;
	}

}
