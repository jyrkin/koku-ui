package fi.arcusys.koku.common.services.messages;

import java.util.List;

import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.util.Properties;
import fi.arcusys.koku.kv.messageservice.Criteria;
import fi.arcusys.koku.kv.messageservice.FolderType;
import fi.arcusys.koku.kv.messageservice.KokuMessageService;
import fi.arcusys.koku.kv.messageservice.KokuMessageService_Service;
import fi.arcusys.koku.kv.messageservice.MessageQuery;
import fi.arcusys.koku.kv.messageservice.MessageStatus;
import fi.arcusys.koku.kv.messageservice.MessageSummary;


/**
 * Retrieves messages data and related operations via web services
 * @author Jinhua Chen
 * Jul 29, 2011
 */
public class MessageService {

	private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

	private final KokuMessageService service;

	/**
	 * Constructor and initialization
	 */
	public MessageService() {
		KokuMessageService_Service ms = new KokuMessageService_Service();
		service = ms.getKokuMessageServicePort();
		((BindingProvider)service).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, Properties.KV_MESSAGE_SERVICE);
	}

	/**
	 * Gets messages
	 * @param user username
	 * @param folderType Message folder type such as inbox, outbox and archive
	 * @param messageQuery query for filtering messages
	 * @return a list of messages
	 */
	public List<MessageSummary> getMessages(String user, FolderType folderType, MessageQuery messageQuery) throws KokuServiceException {
		List<MessageSummary> messages;
		long start = System.nanoTime();
		try {
			messages = service.getMessages(user, folderType, messageQuery);
		} catch(RuntimeException e) {
			throw new KokuServiceException("getMessages failed. user: '"+user+"' folderType: '"+folderType+"'", e);
		}
		LOG.info("getMessages  - "+((System.nanoTime()-start)/1000/1000) + "ms");
		return messages;
	}

	/**
	 * Gets total number of messages
	 * @param user username
	 * @param folderType  Message folder type such as inbox, outbox and archive
	 * @param criteria criteria for filtering messages
	 * @return the amount of messages
	 */
	public int getTotalMessageNum(String user, FolderType folderType, Criteria criteria) throws KokuServiceException {

		long start = System.nanoTime();
		int count = 0;
		try {
			count = service.getTotalMessages(user, folderType, criteria);
		} catch(RuntimeException e) {
			throw new KokuServiceException("getTotalMessageNum failed. user: '"+user+"' folderType: '"+folderType+"'", e);
		}
		LOG.info("getMessageCount  - "+((System.nanoTime()-start)/1000/1000) + "ms");
		return count;
	}


	/**
	 * Gets the detailed message with content by messageId
	 * @param messageId
	 * @return The detailed message
	 */
	public fi.arcusys.koku.kv.messageservice.Message getMessageById(long messageId) throws KokuServiceException {
		try {
			return service.getMessageById(messageId);
		} catch(RuntimeException e) {
			throw new KokuServiceException("getMessageById failed. messageId: '"+messageId+"'", e);
		}
	}

	/**
	 * Gets unread messages
	 * @param user Username
	 * @param FolderType Message folder type such as inbox, outbox and archive
	 * @return Unread messages
	 */
	public int getUnreadMessageNum(String user, FolderType folderType) throws KokuServiceException {
		try {
			return service.getUnreadMessages(user, folderType);
		} catch(RuntimeException e) {
			throw new KokuServiceException("getUnreadMessageNum failed. messageId: user: '"+user+"' folderType: '"+folderType+"'", e);
		}
	}

	/**
	 * Deletes the messages
	 * @param messageIds List of message ids to be deleted
	 * @return Operation status
	 */
	public void deleteMessages(List<Long> messageIds) throws KokuServiceException {
		try {
			service.deleteMessages(messageIds);
		} catch(RuntimeException e) {
			throw new KokuServiceException("ERROR while deleting message(s). See errorMsg: "+ e.getMessage(), e);
		}
	}

	/**
	 * Sets the message status: read or unread
	 * @param messageIds List of message ids
	 * @param messageStatus message status
	 * @return Operation status
	 */
	public void setMessageStatus(List<Long> messageIds, MessageStatus messageStatus) throws KokuServiceException {
		try {
			service.setMessagesStatus(messageIds, messageStatus);
		} catch(RuntimeException e) {
			throw new KokuServiceException("ERROR while setting message(s) status. See errorMsg: "+ e.getMessage(), e);
		}
	}

	/**
	 * Archives messages
	 *
	 * @param messageIds a list of message ids
	 * @return Operation status
	 */
	public void archiveMessages(List<Long> messageIds) throws KokuServiceException {
		try {
			service.archiveMessages(messageIds);
		} catch(RuntimeException e) {
			throw new KokuServiceException("ERROR while archiveMessages. See errorMsg: "+ e.getMessage(), e);
		}
	}

	/**
	 * Archives old messages (more than 3 months older)
	 *
	 * @param userUid
	 * @param folderType
	 */
	public void archiveOldMessages(String userUid, FolderType folderType) throws KokuServiceException {
		try {
			service.archiveOldMessages(userUid, folderType);
		} catch (RuntimeException e) {
			throw new KokuServiceException("ERROR while archiveOldMessages. See errorMsg: "+ e.getMessage(), e);
		}
	}
}
