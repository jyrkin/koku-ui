package fi.arcusys.koku.web.util.impl;

import static fi.arcusys.koku.common.util.Constants.JSON_FAILURE_UUID;
import static fi.arcusys.koku.common.util.Constants.JSON_LOGIN_STATUS;
import static fi.arcusys.koku.common.util.Constants.JSON_RESULT;
import static fi.arcusys.koku.common.util.Constants.JSON_TASKS;
import static fi.arcusys.koku.common.util.Constants.JSON_TOTAL_ITEMS;
import static fi.arcusys.koku.common.util.Constants.JSON_TOTAL_PAGES;
import static fi.arcusys.koku.common.util.Constants.RESPONSE_FAIL;
import static fi.arcusys.koku.common.util.Constants.RESPONSE_OK;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_MESSAGE_ARCHIVE_INBOX;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_MESSAGE_ARCHIVE_OUTBOX;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_MESSAGE_INBOX;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_MESSAGE_OUTBOX;
import static fi.arcusys.koku.common.util.Constants.TOKEN_STATUS_INVALID;
import static fi.arcusys.koku.common.util.Constants.TOKEN_STATUS_VALID;

import java.util.UUID;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.exceptions.KokuPortletException;
import fi.arcusys.koku.common.services.facades.MessageTasks;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.impl.KokuPage;
import fi.arcusys.koku.common.services.facades.impl.MessageCriteria;
import fi.arcusys.koku.common.services.messages.MessageHandle;
import fi.arcusys.koku.web.util.KokuTaskQueryProcess;

public abstract class AbstractQueryProcess implements KokuTaskQueryProcess {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractQueryProcess.class);
	private final MessageSource messageSource;
	
	protected abstract JSONObject getJsonTasks(String taskType, Page page, String keyword, String field, String userUid) throws KokuCommonException;
	
	public AbstractQueryProcess(MessageSource messages) {
		this.messageSource = messages;
	}
	
	@Override
	public JSONObject getJsonModel(String taskType, int pageNum, String keyword, String field, String userUid) {
		try {			
			if (userUid == null) {
				LOG.info("No logged in user");
				final JSONObject jsonModel = new JSONObject();		
				jsonModel.put(JSON_LOGIN_STATUS, TOKEN_STATUS_INVALID);
				return jsonModel;
			}
			
			final Page page = new KokuPage(pageNum);					
			if (taskType.equals(TASK_TYPE_MESSAGE_INBOX)) { // for message inbox
				final MessageTasks messageTasks = new MessageHandle(getMessageSource());
				return createResult(messageTasks.getRecievedMessages(userUid, new MessageCriteria(keyword, field), page));
			} else if (taskType.equals(TASK_TYPE_MESSAGE_OUTBOX)) { // for message outbox
				final MessageTasks messageTasks = new MessageHandle(getMessageSource());
				return createResult(messageTasks.getSentMessages(userUid, new MessageCriteria(keyword, field), page));
			} else if (taskType.equals(TASK_TYPE_MESSAGE_ARCHIVE_INBOX)) { // for message inbox archived
				final MessageTasks messageTasks = new MessageHandle(getMessageSource());
				return createResult(messageTasks.getReciviedArchivedMessages(userUid, new MessageCriteria(keyword, field), page));
			} else if (taskType.equals(TASK_TYPE_MESSAGE_ARCHIVE_OUTBOX)) { // for message outbox archived
				final MessageTasks messageTasks = new MessageHandle(getMessageSource());
				return createResult(messageTasks.getSentArchivedMessages(userUid, new MessageCriteria(keyword, field), page));
			} else {
				return getJsonTasks(taskType, page, keyword, field, userUid);				
			}
		} catch (KokuCommonException kse) {
			return createFailedResult("Koku query failed", kse.getErrorcode(), kse);
		} catch (RuntimeException e) {
			return createFailedResult("Something went very wrong while trying to query tasks", e);
		}
	}
	
	protected JSONObject createFailedResult(String message, Exception e) {
		final String uuid = UUID.randomUUID().toString();
		return createFailedResult(message, uuid, e);
	}
	
	protected JSONObject createFailedResult(String message, String uuid, Exception e) {
		final JSONObject jsonModel = new JSONObject();		
		LOG.error(message + " Koku errorcode: '"+uuid+"'", e);
		jsonModel.put(JSON_RESULT, RESPONSE_FAIL);
		jsonModel.put(JSON_FAILURE_UUID, uuid);
		return jsonModel;
	}
	
	/**
	 * ResultList conversion to JSONObject
	 * 
	 * @param resultList
	 * @return JSONObject
	 * @throws KokuPortletException
	 */
	protected JSONObject createResult(ResultList<?> resultList) throws KokuPortletException {
		if (resultList == null) {
			throw new KokuPortletException("ResultList is null!?");
		}
		final JSONObject json = new JSONObject();
		json.put(JSON_RESULT, RESPONSE_OK);
		json.put(JSON_TASKS, resultList.getResults());
		json.put(JSON_TOTAL_ITEMS, resultList.getTotalTasks());
		json.put(JSON_TOTAL_PAGES, resultList.getTotalPages());
		json.put(JSON_LOGIN_STATUS, TOKEN_STATUS_VALID);
		return json;
	}
	
	@Override
	public final MessageSource getMessageSource() {
		return messageSource;
	}

}
