package fi.arcusys.koku.common.services.facades;

import java.util.List;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.messages.model.KokuMessage;

/**
 * Citizen/Employee side sent and received messages
 * 
 * @author Toni Turunen
 *
 */
public interface MessageTasks {
	
	/**
	 * Returns received messages
	 * 
	 * @param uid
	 * @param criteria
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuMessage> getRecievedMessages(String uid, MessageSearchCriteria criteria, Page page) throws KokuCommonException;
	/**
	 * Returns archived received messages
	 * 
	 * @param uid
	 * @param criteria
	 * @param page
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuMessage> getReciviedArchivedMessages(String uid, MessageSearchCriteria criteria, Page page) throws KokuCommonException;
	
	/**
	 * Returns sent messages
	 * 
	 * @param uid
	 * @param criteria
	 * @param page
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuMessage> getSentMessages(String uid, MessageSearchCriteria criteria, Page page) throws KokuCommonException;
	
	/**
	 * Returns sent archived messages
	 * 
	 * @param uid
	 * @param criteria
	 * @param page
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuMessage> getSentArchivedMessages(String uid, MessageSearchCriteria criteria, Page page) throws KokuCommonException;

	public interface MessageSearchCriteria {
		List<String> getKeywords();
		List<String> getFilteringFields();		
	}
	
}
