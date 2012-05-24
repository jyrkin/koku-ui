//package fi.arcusys.koku.common.web.util.impl;
//
//import java.util.List;
//import java.util.Locale;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.MessageSource;
//
//import fi.arcusys.koku.common.exceptions.KokuServiceException;
//import fi.arcusys.koku.common.services.messages.message.MessageHandle;
//import fi.arcusys.koku.common.services.messages.model.KokuMessage;
//import fi.arcusys.koku.common.util.MessageUtil;
//import fi.arcusys.koku.common.web.util.KokuTaskProcess;
//import fi.arcusys.koku.common.web.util.tasks.Page;
//import fi.arcusys.koku.common.web.util.tasks.ResultList;
//import fi.arcusys.koku.common.web.util.tasks.MessageTasks.MessageSearchCriteria;
//import fi.arcusys.koku.common.web.util.tasks.impl.TaskListFailureImpl;
//import fi.arcusys.koku.common.web.util.tasks.impl.ResultListImpl;
//
//public class AbstractKokuTaskProcessImpl implements KokuTaskProcess {
//	
//	private static final Logger LOG = LoggerFactory.getLogger(AbstractKokuTaskProcessImpl.class);
//
//	private final MessageSource messageSource;
//	private final Locale locale = MessageUtil.getLocale();
//
//	public AbstractKokuTaskProcessImpl(MessageSource messages) {
//		this.messageSource = messages;
//	}
//
//	@Override
//	public ResultList<KokuMessage> getRecievedMessages(String uid, MessageSearchCriteria criteria, Page page) throws KokuServiceException {
//		return getMessages(uid, "msg_inbox", criteria, null, page);
//	}
//
//	@Override
//	public ResultList<KokuMessage> getReciviedArchivedMessages(String uid,
//			MessageSearchCriteria criteria, Page page) throws KokuServiceException {
//		return getMessages(uid, "msg_archive_inbox", criteria, null, page);
//	}
//
//	@Override
//	public ResultList<KokuMessage> getSentMessages(String uid, MessageSearchCriteria criteria, Page page) throws KokuServiceException {
//		return getMessages(uid, "msg_outbox", criteria, null, page);
//	}
//
//	@Override
//	public ResultList<KokuMessage> getSentArchivedMessages(String uid,
//			MessageSearchCriteria criteria, Page page) throws KokuServiceException {
//		return getMessages(uid, "msg_archive_inbox", criteria, null, page);
//	}
//	
//	private ResultList<KokuMessage> getMessages(String userUid, String messageType, MessageSearchCriteria criteria, String orderType, Page page) {
//		try {
//			MessageHandle msgHandle = new MessageHandle();
//			msgHandle.setMessageSource(getMessageSource());
//			List<KokuMessage> tasks = msgHandle.getMessages(userUid, messageType, criteria.getKeywords(), criteria.getFilteringFields(), page.getPosition(), page.getMaxResults());
//			int totalTasksNum = msgHandle.getTotalMessageNum(userUid, messageType, criteria.getKeywords(), criteria.getFilteringFields());
//			return new ResultListImpl<KokuMessage>(tasks, totalTasksNum, page);
//		} catch (KokuServiceException e) {
//			logError(e);
//			return new TaskListFailureImpl<KokuMessage>(e.getErrorcode());
//		}
//	}
//
//	protected final void logError(KokuServiceException e, String msg) {
//		LOG.error(msg, e);	
//	}
//	
//	protected final void logError(KokuServiceException e) {
//		logError(e, "Koku WS query failed");
//	}
//	
//	protected final MessageSource getMessageSource() {
//		return messageSource;
//	}
//
//	protected final String getLocalization(final String message) {
//		return getLocalization(message, null);
//	}
//	
//	protected final String getLocalization(final String message, Object[] params) {
//		return messageSource.getMessage(message, params, locale);		
//	}
//	
//
//
//}
