package fi.arcusys.koku.web.util.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.messages.MessageHandle;
import fi.arcusys.koku.common.util.DummyMessageSource;
import fi.arcusys.koku.kv.messageservice.FolderType;
import fi.arcusys.koku.web.util.KokuActionProcess;
import fi.arcusys.koku.web.util.exception.KokuActionProcessException;


public abstract class AbstractKokuActionProcess implements KokuActionProcess  {
		
	protected final static MessageSource DUMMY_MSG_SOURCE = new DummyMessageSource();
	
	private MessageHandle msghandle = null;
	private final String userId;
	
	public AbstractKokuActionProcess(String userId) {
		this.userId = userId;
	}
	
	@Override
	public void archiveOldMessages(String folderType) throws KokuActionProcessException {
		
		if (folderType == null) {
			throw new KokuActionProcessException("FolderType is null!");
		}		
		instantiateService();
		try {
			msghandle.archiveOldMessages(getUserId(), FolderType.fromValue(folderType));			
		} catch (KokuServiceException e) {
			throw new KokuActionProcessException("Archiving old messages failed.", e);
		}
	}

	@Override
	public void deleteMessages(String[] messageIds) throws KokuActionProcessException {

		if (messageIds == null) {
			throw new KokuActionProcessException("Deleting message(s) failed. messageIds parameter is null");
		}
		instantiateService();		
		List<Long> msgIds = getMessageIds(messageIds);		
		try {
			msghandle.deleteMessages(msgIds);
		} catch (KokuServiceException kse) {
			throw new KokuActionProcessException("Deleting one or more messages failed.");			
		}		
	}

	@Override
	public void archiveMessages(String[] messageIds) throws KokuActionProcessException {
		
		if (messageIds == null) {
			throw new KokuActionProcessException("Archiving message(s) failed. messageIds parameter is null");
		}
		instantiateService();	
		List<Long> formattedMsgIds = getMessageIds(messageIds);		
		try {
			msghandle.archiveMessages(formattedMsgIds);
		} catch (KokuServiceException kse) {
			throw new KokuActionProcessException("Archiving one or more messages failed.");
		}
	}
	
	private List<Long> getMessageIds(String[] messageIds) throws KokuActionProcessException {
		List<Long> formattedMsgIds = new ArrayList<Long>();		
		try {
			for(String msgId : messageIds) {
				formattedMsgIds.add(Long.parseLong(msgId));				
			}
		} catch (NumberFormatException nfe) {
			throw new KokuActionProcessException("Error while parsing messageIds. MessageId is not valid number.", nfe);
		}
		return formattedMsgIds;
	}
	
	private void instantiateService() {
		if (msghandle == null) {
			msghandle = new MessageHandle(DUMMY_MSG_SOURCE);
		}
	}

	protected final String getUserId() {
		return userId;
	}
}
