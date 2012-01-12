package fi.arcusys.koku.navi.util.impl;

import static fi.arcusys.koku.util.Constants.JSON_ARCHIVE_INBOX;
import static fi.arcusys.koku.util.Constants.JSON_INBOX;
import static fi.arcusys.koku.util.Constants.JSON_LOGIN_STATUS;
import static fi.arcusys.koku.util.Constants.TOKEN_STATUS_INVALID;
import static fi.arcusys.koku.util.Constants.TOKEN_STATUS_VALID;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;

import fi.arcusys.koku.kv.model.KokuFolderType;
import fi.arcusys.koku.util.PortalRole;

public class QueryProcessEmployeeImpl extends AbstractQueryProcess {
	
	private static final Logger LOGGER = Logger.getLogger(QueryProcessEmployeeImpl.class);	

	public QueryProcessEmployeeImpl(MessageSource messages) {
		super(messages);
	}

	@Override
	public JSONObject getJsonModel(String username, String userId, String token, PortalRole role) {
		JSONObject jsonModel = new JSONObject();
		if (userId == null) {
			jsonModel.put(JSON_LOGIN_STATUS, TOKEN_STATUS_INVALID);
		} else {			
			jsonModel.put(JSON_LOGIN_STATUS, TOKEN_STATUS_VALID);			
			jsonModel.put(JSON_INBOX, String.valueOf(getNewMessageNum(userId, KokuFolderType.INBOX)));			
			jsonModel.put(JSON_ARCHIVE_INBOX, String.valueOf(getNewMessageNum(userId, KokuFolderType.ARCHIVE_INBOX)));
		}
		return jsonModel;
	}

}
