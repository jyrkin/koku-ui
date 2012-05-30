package fi.arcusys.koku.web.util.impl;

import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_APPOINTMENT_INBOX_CITIZEN;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_APPOINTMENT_RESPONSE_CITIZEN;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_APPOINTMENT_RESPONSE_CITIZEN_OLD;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_CONSENT_ASSIGNED_CITIZEN;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_CONSENT_CITIZEN_CONSENTS;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_CONSENT_CITIZEN_CONSENTS_OLD;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_REQUEST_OLD;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_REQUEST_REPLIED;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_WARRANT_BROWSE_RECEIEVED;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_WARRANT_BROWSE_SENT;
import net.sf.json.JSONObject;

import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.exceptions.KokuPortletException;
import fi.arcusys.koku.common.services.appointments.citizen.AvCitizenServiceHandle;
import fi.arcusys.koku.common.services.consents.citizen.TivaCitizenServiceHandle;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.RequestTasks;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.citizen.CitizenAppointmentTasks;
import fi.arcusys.koku.common.services.facades.citizen.CitizenConsentTasks;
import fi.arcusys.koku.common.services.facades.citizen.CitizenWarrantTasks;
import fi.arcusys.koku.common.services.requests.citizen.CitizenRequestHandle;
import fi.arcusys.koku.common.services.warrants.citizens.KokuCitizenWarrantHandle;

public class QueryProcessCitizenImpl extends AbstractQueryProcess {
	
	public QueryProcessCitizenImpl(MessageSource messages) {
		super(messages);
	}
		
	@Override
	protected JSONObject getJsonTasks(String taskType, Page page, String keyword, String field, String userUid) throws KokuCommonException {
		ResultList<?> results = null;

		if (taskType.equals(TASK_TYPE_REQUEST_REPLIED)) { // Pyynnöt - vastatut
			RequestTasks handle = new CitizenRequestHandle(getMessageSource());
			results = handle.getRequestsRecievedReplied(userUid, page);
		}  else if (taskType.equals(TASK_TYPE_REQUEST_OLD)) { // Pyynnöt - vanhat
			RequestTasks handle = new CitizenRequestHandle(getMessageSource());
			results = handle.getRequestsRecieviedRepliedOld(userUid, page);
		} else if (taskType.equals(TASK_TYPE_APPOINTMENT_INBOX_CITIZEN)) {
			CitizenAppointmentTasks handle = new AvCitizenServiceHandle(getMessageSource(), userUid);
			results = handle.getNewAppointments(userUid, page);
		} else if (taskType.equals(TASK_TYPE_APPOINTMENT_RESPONSE_CITIZEN_OLD)) {
			CitizenAppointmentTasks handle = new AvCitizenServiceHandle(getMessageSource(), userUid);
			results = handle.getOldAppointments(userUid, page);
		} else if (taskType.equals(TASK_TYPE_APPOINTMENT_RESPONSE_CITIZEN)) {	// Tapaamiset - Vastausta odottavat / vastatut 
			CitizenAppointmentTasks handle = new AvCitizenServiceHandle(getMessageSource(), userUid);
			results = handle.getAnsweredAppointments(userUid, page);
		} else if (taskType.equals(TASK_TYPE_CONSENT_ASSIGNED_CITIZEN)) { // consent (Valtakirja / Suostumus) Kansalaiselle saapuneet pyynnöt(/suostumukset) 
			CitizenConsentTasks tivaHandle = new TivaCitizenServiceHandle(getMessageSource());
			results = tivaHandle.getNewConsents(userUid, page);;
		} else if(taskType.equals(TASK_TYPE_CONSENT_CITIZEN_CONSENTS)) { // consent (Valtakirja / Suostumus) Kansalaiselle vastatut pyynnöt(/suostumukset) 
			CitizenConsentTasks tivaHandle = new TivaCitizenServiceHandle(getMessageSource());
			results = tivaHandle.getRepliedConsents(userUid, page);
		} else if(taskType.equals(TASK_TYPE_CONSENT_CITIZEN_CONSENTS_OLD)) { // consent (Valtakirja / Suostumus) Kansalaiselle vastatut vanhentuneet pyynnöt(/suostumukset) 
			CitizenConsentTasks tivaHandle = new TivaCitizenServiceHandle(getMessageSource());
			results = tivaHandle.getOldConsents(userUid, page);
		} else if(taskType.equals(TASK_TYPE_WARRANT_BROWSE_RECEIEVED)) {	// Kuntalainen: Valtuuttajana TIVA-11
			CitizenWarrantTasks warrantHandle = new KokuCitizenWarrantHandle(getMessageSource());
			results = warrantHandle.getRecievedWarrants(userUid, page);
		} else if(taskType.equals(TASK_TYPE_WARRANT_BROWSE_SENT)) {	// Kuntalainen: Valtuutettuna TIVA-11
			CitizenWarrantTasks warrantHandle = new KokuCitizenWarrantHandle(getMessageSource());
			results = warrantHandle.getSentWarrants(userUid, page);
		} else  {
			throw new KokuPortletException("No such a taskType: '"+taskType+"'");
		}
		return createResult(results);
	}
}