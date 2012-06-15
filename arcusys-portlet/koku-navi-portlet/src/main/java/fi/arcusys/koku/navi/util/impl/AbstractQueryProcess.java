package fi.arcusys.koku.navi.util.impl;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.IntalioException;
import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.intalio.TaskHandle;
import fi.arcusys.koku.common.services.messages.MessageHandle;
import fi.arcusys.koku.common.services.messages.model.KokuFolderType;
import fi.arcusys.koku.common.util.DummyMessageSource;
import fi.arcusys.koku.navi.util.QueryProcess;

public abstract class AbstractQueryProcess implements QueryProcess {

	private static final Logger LOG = Logger.getLogger(AbstractQueryProcess.class);

	private MessageSource messageSource;

	public AbstractQueryProcess(MessageSource messages) {
		this.messageSource = messages;
	}


	public final void setMessageSource(MessageSource messages) {
		this.messageSource = messages;
	}


	public final MessageSource getMessageSource() {
		return messageSource;
	}


	/**
	 * Returns number of new messages in the given folder type from web services
	 *
	 * @param userId
	 * @param folderType
	 * @return number of messages
	 * @throws KokuServiceException
	 */
	protected int getNewMessageNum(String userId, KokuFolderType folderType) throws KokuServiceException {
		MessageHandle messageHandle = new MessageHandle(new DummyMessageSource());
		return messageHandle.getUnreadMessages(userId, folderType);
	}

	/**
	 * Returns total amount of tasks
	 *
	 * @param userId
	 * @param token (from intalio)
	 * @filter filttering string
	 * @return number or requests
	 */
	protected int getTotalTasks(final String username, final String token, final String filter) {
		if (username != null && !username.isEmpty() && token != null && !token.isEmpty()) {
			TaskHandle handle = new TaskHandle(token, username);
			try {
				return handle.getTasksTotalNumber(filter);
			} catch (IntalioException e) {
				LOG.error(e);
				return 0;
			}
		} else {
			return 0;
		}
	}

}
