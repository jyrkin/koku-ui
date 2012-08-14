package fi.arcusys.koku.navi.util.impl;

import static fi.arcusys.koku.common.util.Constants.JSON_ARCHIVE_INBOX;
import static fi.arcusys.koku.common.util.Constants.JSON_DAYCARE_PAYMENT_DISCOUNT_TOTAL;
import static fi.arcusys.koku.common.util.Constants.JSON_DAYCARE_PAYMENT_MODIFY_TOTAL;
import static fi.arcusys.koku.common.util.Constants.JSON_DAYCARE_PAYMENT_TOTAL;
import static fi.arcusys.koku.common.util.Constants.JSON_DAYCARE_TERMINATION_TOTAL;
import static fi.arcusys.koku.common.util.Constants.JSON_INBOX;
import static fi.arcusys.koku.common.util.Constants.JSON_INFO_REQUESTS_TOTAL;
import static fi.arcusys.koku.common.util.Constants.JSON_LOGIN_STATUS;
import static fi.arcusys.koku.common.util.Constants.JSON_REQUESTS_TOTAL;
import static fi.arcusys.koku.common.util.Constants.TOKEN_STATUS_INVALID;
import static fi.arcusys.koku.common.util.Constants.TOKEN_STATUS_VALID;
import static fi.arcusys.koku.common.util.Properties.RECEIVED_DAYCARE_PAYMENT_DISCOUNT;
import static fi.arcusys.koku.common.util.Properties.RECEIVED_DAYCARE_PAYMENT_FILTER;
import static fi.arcusys.koku.common.util.Properties.RECEIVED_DAYCARE_PAYMENT_MODIFY;
import static fi.arcusys.koku.common.util.Properties.RECEIVED_DAYCARE_TERMINATION;
import static fi.arcusys.koku.common.util.Properties.RECEIVED_INFO_REQUESTS_FILTER;
import static fi.arcusys.koku.common.util.Properties.RECEIVED_REQUESTS_FILTER;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.messages.model.KokuFolderType;
import fi.arcusys.koku.common.util.PortalRole;


public class QueryProcessEmployeeImpl extends AbstractQueryProcess {

	private static final Logger LOG = LoggerFactory.getLogger(QueryProcessEmployeeImpl.class);

	public QueryProcessEmployeeImpl(MessageSource messages) {
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
			} catch (KokuServiceException kse) {
				LOG.error("Failed to get count(s) (message/archive/consensts/appointments. ", kse);
				jsonModel.put(JSON_INBOX, 0);
				jsonModel.put(JSON_ARCHIVE_INBOX, 0);
			}

			try {
				jsonModel.put(JSON_INFO_REQUESTS_TOTAL, getTotalTasks(username, token, RECEIVED_INFO_REQUESTS_FILTER));
				jsonModel.put(JSON_REQUESTS_TOTAL, getTotalTasks(userId, token, RECEIVED_REQUESTS_FILTER));
				jsonModel.put(JSON_DAYCARE_PAYMENT_TOTAL, getTotalTasks(userId, token, RECEIVED_DAYCARE_PAYMENT_FILTER));
				jsonModel.put(JSON_DAYCARE_PAYMENT_MODIFY_TOTAL, getTotalTasks(userId, token, RECEIVED_DAYCARE_PAYMENT_MODIFY));
				jsonModel.put(JSON_DAYCARE_PAYMENT_DISCOUNT_TOTAL, getTotalTasks(userId, token, RECEIVED_DAYCARE_PAYMENT_DISCOUNT));
				jsonModel.put(JSON_DAYCARE_TERMINATION_TOTAL, getTotalTasks(userId, token, RECEIVED_DAYCARE_TERMINATION));
			} catch (Exception e) {
				LOG.error("Couldn't get TotalRequests (Saapuneet pyynnöt / Tietopyynnöt). See following errorMsg: "+e.getMessage(), e);
				jsonModel.put(JSON_INFO_REQUESTS_TOTAL, 0);
				jsonModel.put(JSON_REQUESTS_TOTAL, 0);
			}
		}
		return jsonModel;
	}



}
