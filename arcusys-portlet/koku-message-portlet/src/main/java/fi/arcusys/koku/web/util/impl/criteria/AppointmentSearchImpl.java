package fi.arcusys.koku.web.util.impl.criteria;

import fi.arcusys.koku.common.services.facades.employee.EmployeeAppointmentTasks.AppointmentsSearchCriteria;

public class AppointmentSearchImpl implements AppointmentsSearchCriteria {

	public final String targetPersonSsn;
	
	public AppointmentSearchImpl(String targetPersonSsn) {
		this.targetPersonSsn = targetPersonSsn;
	}
	
	@Override
	public String getTargetPersonSsn() {
		return targetPersonSsn;
	}
}
