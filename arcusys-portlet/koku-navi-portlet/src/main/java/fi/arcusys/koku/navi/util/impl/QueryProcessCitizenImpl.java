package fi.arcusys.koku.navi.util.impl;

import static fi.arcusys.koku.common.util.Constants.JSON_APPOINTMENT_TOTAL;
import static fi.arcusys.koku.common.util.Constants.JSON_ARCHIVE_INBOX;
import static fi.arcusys.koku.common.util.Constants.JSON_CONSENTS_TOTAL;
import static fi.arcusys.koku.common.util.Constants.JSON_DAYCARE_HOLIDAYS_TOTAL;
import static fi.arcusys.koku.common.util.Constants.JSON_INBOX;
import static fi.arcusys.koku.common.util.Constants.JSON_LOGIN_STATUS;
import static fi.arcusys.koku.common.util.Constants.JSON_REQUESTS_TOTAL;
import static fi.arcusys.koku.common.util.Constants.TOKEN_STATUS_INVALID;
import static fi.arcusys.koku.common.util.Constants.TOKEN_STATUS_VALID;
import static fi.arcusys.koku.common.util.Properties.RECEIVED_DAYCARE_HOLIDAYS;
import static fi.arcusys.koku.common.util.Properties.RECEIVED_REQUESTS_FILTER;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.appointments.citizen.AvCitizenServiceHandle;
import fi.arcusys.koku.common.services.consents.citizen.TivaCitizenServiceHandle;
import fi.arcusys.koku.common.services.messages.model.KokuFolderType;
import fi.arcusys.koku.common.util.DummyMessageSource;
import fi.arcusys.koku.common.util.PortalRole;


public class QueryProcessCitizenImpl extends AbstractQueryProcess {

	private static final Logger LOG = LoggerFactory.getLogger(QueryProcessCitizenImpl.class);

	public QueryProcessCitizenImpl(MessageSource messages) {
		super(messages);
	}

	@Override
	public JSONObject getJsonModel(String username, String userId, String token, PortalRole role) {
		JSONObject jsonModel = new JSONObject();
		if (userId == null) {
			jsonModel.put(JSON_LOGIN_STATUS, TOKEN_STATUS_INVALID);
		} else {
			try {
				jsonModel.put(JSON_LOGIN_STATUS, TOKEN_STATUS_VALID);
				jsonModel.put(JSON_INBOX, getNewMessageNum(userId, KokuFolderType.INBOX));
				jsonModel.put(JSON_ARCHIVE_INBOX, getNewMessageNum(userId, KokuFolderType.ARCHIVE_INBOX));
				jsonModel.put(JSON_CONSENTS_TOTAL, getTotalAssignedConsents(userId));
				jsonModel.put(JSON_APPOINTMENT_TOTAL, getTotalAssignedAppointments(userId));
				jsonModel.put(JSON_DAYCARE_HOLIDAYS_TOTAL, getTotalTasks(userId, token, RECEIVED_DAYCARE_HOLIDAYS));
				try {
					jsonModel.put(JSON_REQUESTS_TOTAL, getTotalTasks(userId, token, RECEIVED_REQUESTS_FILTER));
				} catch (Exception e) {
					LOG.error("Coulnd't get TotalRequests (Valtakirja yht.). See following errorMsg: "+e.getMessage(), e);
				}
			} catch (KokuServiceException kse) {
				LOG.error("Failed to get count(s) (message/archive/consensts/appointments. ", kse);
				jsonModel.put(JSON_INBOX, 0);
				jsonModel.put(JSON_ARCHIVE_INBOX, 0);
				jsonModel.put(JSON_CONSENTS_TOTAL, 0);
				jsonModel.put(JSON_APPOINTMENT_TOTAL, 0);
			}
		}
		return jsonModel;
	}


	/**
	 * Returns total amount of assigned consents (not just new ones)
	 *
	 * @param userId
	 * @return number of assigned consents
	 * @throws KokuServiceException
	 */
	private int getTotalAssignedConsents(String userId) throws KokuServiceException {
		TivaCitizenServiceHandle handle = new TivaCitizenServiceHandle(new DummyMessageSource());
		return handle.getTotalAssignedConsents(userId);
	}

	/**
	 * Returns total amount of assigned appointments
	 *
	 * @param userId
	 * @return number of assigned appointments
	 * @throws KokuServiceException
	 */
	private int getTotalAssignedAppointments(String userId) throws KokuServiceException {
		AvCitizenServiceHandle handle = new AvCitizenServiceHandle(new DummyMessageSource(), userId);
		return handle.getTotalAssignedAppointmentsNum(userId);
	}



}
