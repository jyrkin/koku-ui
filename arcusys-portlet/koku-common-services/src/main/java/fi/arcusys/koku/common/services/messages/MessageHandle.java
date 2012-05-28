package fi.arcusys.koku.common.services.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.AbstractHandle;
import fi.arcusys.koku.common.services.facades.MessageTasks;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.impl.ResultListImpl;
import fi.arcusys.koku.common.services.messages.model.KokuFolderType;
import fi.arcusys.koku.common.services.messages.model.KokuMessage;
import fi.arcusys.koku.common.services.users.KokuUser;
import fi.arcusys.koku.common.util.MessageUtil;
import fi.arcusys.koku.kv.messageservice.Criteria;
import fi.arcusys.koku.kv.messageservice.Fields;
import fi.arcusys.koku.kv.messageservice.FolderType;
import fi.arcusys.koku.kv.messageservice.Message;
import fi.arcusys.koku.kv.messageservice.MessageQuery;
import fi.arcusys.koku.kv.messageservice.MessageStatus;
import fi.arcusys.koku.kv.messageservice.MessageSummary;
import fi.arcusys.koku.kv.messageservice.OrderBy;
import fi.arcusys.koku.kv.messageservice.Type;
import fi.arcusys.koku.kv.messageservice.User;

/**
 * Handles request of messages and retrieves messages
 * @author Jinhua Chen
 * Jun 22, 2011
 */
public class MessageHandle extends AbstractHandle implements MessageTasks {
	
	private static final Logger LOG = Logger.getLogger(MessageHandle.class);
	
	private static final OrderBy MSG_ORDER_BY_DATE; 
	
	static {
		/* sets the order type, default is ordering by created date descending */
		// NOTE: OrderBy is not immutable object!
		MSG_ORDER_BY_DATE = new OrderBy();
		MSG_ORDER_BY_DATE.setField(Fields.CREATED_DATE);
		MSG_ORDER_BY_DATE.setType(Type.DESC);
	}
	
	private final MessageService ms;
	
	public MessageHandle(MessageSource messageSource) {
		super(messageSource);
		ms = new MessageService();
	}
	
	@Override
	public ResultList<KokuMessage> getRecievedMessages(final String uid,
			final MessageSearchCriteria criteria, final Page page)
			throws KokuCommonException {
		return getMessages(uid, FolderType.INBOX, criteria, page);
	}

	@Override
	public ResultList<KokuMessage> getReciviedArchivedMessages(final String uid,
			final MessageSearchCriteria criteria, final Page page)
			throws KokuCommonException {
		return getMessages(uid, FolderType.ARCHIVE_INBOX, criteria, page);
	}

	@Override
	public ResultList<KokuMessage> getSentMessages(final String uid,
			final MessageSearchCriteria criteria, final Page page)
			throws KokuCommonException {
		return getMessages(uid, FolderType.OUTBOX, criteria, page);
	}

	@Override
	public ResultList<KokuMessage> getSentArchivedMessages(final String uid,
			final MessageSearchCriteria criteria, final Page page)
			throws KokuCommonException {
		return getMessages(uid, FolderType.ARCHIVE_OUTBOX, criteria, page);
	}
		
	private ResultList<KokuMessage> getMessages(final String uid, final FolderType folderType,
			final MessageSearchCriteria criteria, final Page page) throws KokuCommonException {		
		final MessageQuery messageQuery = new MessageQuery();
		messageQuery.setStartNum(page.getFirst());
		messageQuery.setMaxNum(page.getLast());
		messageQuery.setCriteria(convertMessageSearchToWsCriteria(criteria));
		messageQuery.getOrderBy().add(MSG_ORDER_BY_DATE);
		
		final List<MessageSummary> msgs = ms.getMessages(uid, folderType, messageQuery);
		final List<KokuMessage> msgList = new ArrayList<KokuMessage>();
		for (MessageSummary msgSum : msgs) {
			final KokuMessage message = new KokuMessage();
			convertMessageSummaryToKokuMessage(message, msgSum);
			msgList.add(message);		
		}
		final int tasksTotal = getTotalMessageNum(uid, folderType, criteria); 
		return new ResultListImpl<KokuMessage>(msgList, tasksTotal, page);
	}
	
	private Criteria convertMessageSearchToWsCriteria(final MessageSearchCriteria msgCriteria) {
		final Criteria wsCriteria = new Criteria();
		wsCriteria.getKeywords().addAll(msgCriteria.getKeywords());
		for (String filter : msgCriteria.getFilteringFields()) {
			try {
				wsCriteria.getFields().add(Fields.valueOf(filter));
			} catch (IllegalArgumentException e) {
				LOG.warn("Illegal filtering argument! filter: '"+filter+"'");
			}
		}
		return wsCriteria;
	}
	
	private int getTotalMessageNum(final String userId, final FolderType folderType, final MessageSearchCriteria criteria) throws KokuServiceException {
		return ms.getTotalMessageNum(userId, folderType, convertMessageSearchToWsCriteria(criteria));
	}
	
	/**
	 * Returns the total amount of messages with given filtering conditions
	 * 
	 * @param user username
	 * @param messageType type of message
	 * @param keyword keyword string for filtering
	 * @param field field string for filtering
	 * @return number of messages
	 */
	public int getTotalMessageNum(String userId, String messageType, String keyword, String field) throws KokuServiceException {

		FolderType folderType = MessageUtil.getFolderType(messageType);
		/* sets the criteria for searching including keyword for each field, default is searching all fields */
		Criteria criteria = createCriteria(keyword, field);
		return ms.getTotalMessageNum(userId, folderType, criteria);		
	}
	
	/**
	 * Returns total amount of unread messages
	 * 
	 * @param username
	 * @param folderType
	 * @return
	 * @throws KokuServiceException
	 */
	public int getUnreadMessages(String username, KokuFolderType folderType) throws KokuServiceException {
		FolderType type = FolderType.valueOf(folderType.toString());
		return ms.getUnreadMessageNum(username, type);
	}
	
	/**
	 * Gets detailed message with content
	 * 
	 * @param messageId message id
	 * @return detailed message
	 */
	public KokuMessage getMessageById(String messageId) throws KokuServiceException {
		long  msgId = 0;
		try {
			 msgId = (long) Long.parseLong(messageId);			
		} catch (NumberFormatException nfe) {
			LOG.error("Couldn't show message details, because messageId can't parse properly (must be long). Given MessageId: "+messageId, nfe);
			return new KokuMessage();
		}
		setMessageStatusRead(msgId);		
		Message msg = ms.getMessageById(msgId);
		KokuMessage message = new KokuMessage();
		convertMessageToKokuMessage(message, msg);
		return message;
	}
	
	private void convertMessageSummaryToKokuMessage(final KokuMessage kokuMessage, final MessageSummary message) {
		kokuMessage.setMessageId(message.getMessageId());
		kokuMessage.setSubject(message.getSubject());
		kokuMessage.setCreationDate(MessageUtil.formatTaskDate(message.getCreationDate()));
		kokuMessage.setMessageType(MessageUtil.getMessageType(message.getMessageType()));
		kokuMessage.setMessageStatus(message.getMessageStatus().value());
		kokuMessage.setMessageStatusLocalized(localizeMsgStatus(message.getMessageStatus()));
		kokuMessage.setReplyDisabled(message.isReplyDisabled());
		for (User recipientUser : message.getRecipientUserInfos()) {
			kokuMessage.getRecipientUsers().add(new KokuUser(recipientUser));
		}
		kokuMessage.setSenderUser(new KokuUser(message.getSenderUserInfo()));
	}
	
	private void convertMessageToKokuMessage(final KokuMessage kokuMessge, final Message message) {
		convertMessageSummaryToKokuMessage(kokuMessge, message);
		if (message.getDeliveryFailedTo() != null) {
			for (User user : message.getDeliveryFailedTo()) {
				kokuMessge.getDeliveryFailedTo().add(new KokuUser(user));				
			}
		}
		kokuMessge.setContent(message.getContent());
	}
	
	
	private String localizeMsgStatus(final MessageStatus status ) {
		if (getMessageSource() == null) {
			LOG.warn("getMessageSource() is null. Localization doesn't work properly");
			return status.toString().toLowerCase();
		}
		Locale locale = MessageUtil.getLocale();
		
		try {	
			switch(status) {
			case READ:
				return getMessageSource().getMessage("MessageStatus.READ", null, locale);
			case UNREAD:
				return getMessageSource().getMessage("MessageStatus.UNREAD", null, locale);
			default:
				return getMessageSource().getMessage("unknown", null, locale);
			}
		} catch (NoSuchMessageException nsme) {
			LOG.warn("Coulnd't find localized message. Localization doesn't work properly");
			return status.toString().toLowerCase();
		}
	}
	
	/**
	 * Sets message status to read
	 * 
	 * @param messageId message id
	 * @throws KokuServiceException 
	 */
	private void setMessageStatusRead(long messageId) throws KokuServiceException {
		List<Long> messageIds = new ArrayList<Long>();	
		messageIds.add(messageId);	
		ms.setMessageStatus(messageIds, MessageStatus.READ);
	}
	
	/**
	 * Archives messages
	 * 
	 * @param messageIds a list of message ids
	 * @return operation response information
	 * @throws KokuServiceException 
	 */
	public void archiveMessages(List<Long> messageIds) throws KokuServiceException {		
		ms.archiveMessages(messageIds);		
	}
	
	/**
	 * Archives old messages
	 * 
	 * @param userUid
	 * @param folderType
	 * @return operation response information
	 * @throws KokuServiceException 
	 */
	public void archiveOldMessages(String userUid, FolderType folderType) throws KokuServiceException {
		ms.archiveOldMessages(userUid, folderType);
	}
	
	/**
	 * Deletes messages
	 * 
	 * @param messageIds a list of message ids
	 * @return operation response information
	 * @throws KokuServiceException 
	 */
	public void deleteMessages(List<Long> messageIds) throws KokuServiceException {				
		ms.deleteMessages(messageIds);		
	}
	
	/**
	 * Creates filtering criteria
	 * 
	 * @param keyword keyword string
	 * @param field filed string
	 * @return filtering criteria
	 */
	private Criteria createCriteria(String keyword, String field) {
		Criteria criteria = new Criteria();
		
		if(keyword.trim().length() > 0) {
			String[] keywords = keyword.split(" ");
			criteria.getKeywords().addAll(Arrays.asList(keywords));
		}else {
			return null;
		}
		
		String[] fields = field.split("_");
		
		for(int i=0; i < fields.length; i++) {
			if(fields[i].equals("1")) {
				criteria.getFields().add(Fields.SENDER);
			}else if(fields[i].equals("2")) {
				criteria.getFields().add(Fields.RECEIVER);
			}else if(fields[i].equals("3")) {
				criteria.getFields().add(Fields.SUBJECT);
			}else if(fields[i].equals("4")) {
				criteria.getFields().add(Fields.CONTENT);
			}
		}
		return criteria;
	}


}
