package fi.arcusys.koku.web.util.impl;

import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_APPLICATION_KINDERGARTEN_BROWSE;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_APPOINTMENT_INBOX_EMPLOYEE;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_APPOINTMENT_RESPONSE_EMPLOYEE;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_CONSENT_EMPLOYEE_CONSENTS;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_INFO_REQUEST_BROWSE;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_INFO_REQUEST_BROWSE_REPLIED;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_INFO_REQUEST_BROWSE_SENT;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_REQUEST_DONE_EMPLOYEE;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_REQUEST_OLD;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_REQUEST_REPLIED;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_REQUEST_VALID_EMPLOYEE;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_WARRANT_LIST_CITIZEN_CONSENTS;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_WARRANT_LIST_SUBJECT_CONSENTS;
import net.sf.json.JSONObject;

import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.exceptions.KokuPortletException;
import fi.arcusys.koku.common.services.appointments.employee.AvEmployeeServiceHandle;
import fi.arcusys.koku.common.services.consents.employee.TivaEmployeeServiceHandle;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.employee.EmployeeApplicationsTasks;
import fi.arcusys.koku.common.services.facades.employee.EmployeeAppointmentTasks;
import fi.arcusys.koku.common.services.facades.employee.EmployeeConsentTasks;
import fi.arcusys.koku.common.services.facades.employee.EmployeeRequestTasks;
import fi.arcusys.koku.common.services.facades.employee.EmployeeWarrantTasks;
import fi.arcusys.koku.common.services.facades.employee.EmployeeWarrantTasks.WarrantSearchCriteria;
import fi.arcusys.koku.common.services.facades.employee.InfoRequestTasks;
import fi.arcusys.koku.common.services.facades.employee.InfoRequestTasks.InformationRequestSearchCriteria;
import fi.arcusys.koku.common.services.hak.employee.HakServiceHandle;
import fi.arcusys.koku.common.services.inforequest.employee.KokuEmployeeTietopyyntoServiceHandle;
import fi.arcusys.koku.common.services.requests.employee.EmployeeRequestHandle;
import fi.arcusys.koku.common.services.warrants.employee.KokuEmployeeWarrantHandle;
import fi.arcusys.koku.web.util.impl.criteria.AppointmentSearchImpl;
import fi.arcusys.koku.web.util.impl.criteria.ConsentCriteriaSearchImpl;
import fi.arcusys.koku.web.util.impl.criteria.InfoRequestSearchParser;
import fi.arcusys.koku.web.util.impl.criteria.WarrantSearchParser;
import fi.arcusys.koku.web.util.impl.criteria.WarrantSearchParser.Mode;

public class QueryProcessEmployeeImpl extends AbstractQueryProcess {
	
	public QueryProcessEmployeeImpl(MessageSource messages) {
		super(messages);
	}

	@Override
	protected JSONObject getJsonTasks(String taskType, Page page, String keyword, String field, String userUid) throws KokuCommonException {
	
		ResultList<?> results = null;
		if (taskType.equals(TASK_TYPE_APPLICATION_KINDERGARTEN_BROWSE)) { // Asiointipalvelut - Selaa hakemuksia (päivähoito) - employee
			EmployeeApplicationsTasks reqHandle = new HakServiceHandle(getMessageSource());
			results = reqHandle.getApplications(userUid, null, page);
		} else if (taskType.equals(TASK_TYPE_REQUEST_VALID_EMPLOYEE)) { // for request (Pyynnöt) - employee Avoimet
			EmployeeRequestTasks reqHandle = new EmployeeRequestHandle(getMessageSource());
			results = reqHandle.getRequestsSentOpen(userUid, page);
		} else if (taskType.equals(TASK_TYPE_REQUEST_DONE_EMPLOYEE)) { // Pyynnöt - vastatut/vanhat
			EmployeeRequestTasks reqHandle = new EmployeeRequestHandle(getMessageSource());
			results = reqHandle.getRequestsSentDone(userUid, page);
		} else if (taskType.equals(TASK_TYPE_REQUEST_REPLIED)) { // Pyynnöt - vastatut
			EmployeeRequestTasks handle = new EmployeeRequestHandle(getMessageSource());
			results = handle.getRequestsRecievedReplied(userUid, page);
		} else if (taskType.equals(TASK_TYPE_REQUEST_OLD)) { // Pyynnöt - vanhat
			EmployeeRequestTasks handle = new EmployeeRequestHandle(getMessageSource());
			results = handle.getRequestsRecieviedRepliedOld(userUid, page);
		} else if (taskType.equals(TASK_TYPE_INFO_REQUEST_BROWSE_REPLIED)) { // Virkailija: Selaa vastaanotettuja tietopyyntöjä
			InfoRequestTasks handle = new KokuEmployeeTietopyyntoServiceHandle(getMessageSource());
			results = handle.getRecievedAndRepliedInfoRequests(userUid, createInfoReqCriteria(keyword), page);
		} else if (taskType.equals(TASK_TYPE_INFO_REQUEST_BROWSE_SENT)) { // Virkailija: Selaa lähetettyjä tietopyyntöjä
			InfoRequestTasks handle = new KokuEmployeeTietopyyntoServiceHandle(getMessageSource());
			results = handle.getSentInfoRequests(userUid, createInfoReqCriteria(keyword), page);
		} else if (taskType.equals(TASK_TYPE_INFO_REQUEST_BROWSE)) { // ADMIN: Selaa tietopyyntöjä
			InfoRequestTasks handle = new KokuEmployeeTietopyyntoServiceHandle(getMessageSource());
			results = handle.getInfoRequests(createInfoReqCriteria(keyword), page);
		} else if (taskType.equals(TASK_TYPE_APPOINTMENT_INBOX_EMPLOYEE)) { // Tapaamiset - Avoimet
			EmployeeAppointmentTasks handle = new AvEmployeeServiceHandle(getMessageSource());
			results = handle.getOpenAppoiments(userUid, new AppointmentSearchImpl(keyword), page);
		} else if (taskType.equals(TASK_TYPE_APPOINTMENT_RESPONSE_EMPLOYEE)) { // Tapaamiset - Valmiit
			EmployeeAppointmentTasks handle = new AvEmployeeServiceHandle(getMessageSource());
			results = handle.getReadyAppointments(userUid, new AppointmentSearchImpl(keyword), page);
		} else if(taskType.equals(TASK_TYPE_CONSENT_EMPLOYEE_CONSENTS)) { // Virkailijan lähetetyt suostumus pyynnöt
			EmployeeConsentTasks tivaHandle = new TivaEmployeeServiceHandle(getMessageSource());
			results = tivaHandle.getSentConsents(userUid, new ConsentCriteriaSearchImpl(keyword, field), page);
		} else if(taskType.equals(TASK_TYPE_WARRANT_LIST_CITIZEN_CONSENTS)) {	// Virkailija: Selaa asiakkaan valtakirjoja TIVA-13
			EmployeeWarrantTasks warrantHandle = new KokuEmployeeWarrantHandle(getMessageSource());
			results = warrantHandle.getWarrants(createWarrantSearchCriteria(Mode.USER_ID, keyword), page);
		} else if(taskType.equals(TASK_TYPE_WARRANT_LIST_SUBJECT_CONSENTS)) {	// Virkailija: Selaa asian valtakirjoja TIVA-14
			KokuEmployeeWarrantHandle warrantHandle = new KokuEmployeeWarrantHandle(getMessageSource());
			results = warrantHandle.getWarrants(createWarrantSearchCriteria(Mode.TEMPLATE_NAME, keyword), page);
		} else {
			throw new KokuPortletException("No such a taskType: '"+taskType+"'");
		}
		return createResult(results);
	}

	private InformationRequestSearchCriteria createInfoReqCriteria(String keyword) {
		return new InfoRequestSearchParser(keyword).getCriteria();
	}
	
	private WarrantSearchCriteria createWarrantSearchCriteria(Mode mode, String keyword) {
		return new WarrantSearchParser(mode, keyword).getSearchCriteria();
	}
}
