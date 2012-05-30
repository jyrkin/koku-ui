package fi.arcusys.koku.common.services.facades.employee;

import java.util.Calendar;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.inforequest.model.KokuInformationRequestSummary;

/**
 * InfoRequests (Tietopyynnöt)
 * 
 * @author Toni Turunen
 *
 */
public interface InfoRequestTasks {
	
	/**
	 * Returns list of employee received infoRequests
	 * 
	 * @param uid
	 * @param filter
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuInformationRequestSummary> getRecievedAndRepliedInfoRequests(String uid, InformationRequestSearchCriteria criteria, Page page) throws KokuServiceException;
	
	/**
	 * Returns list of employee sent infoRequests
	 * 
	 * @param uid
	 * @param filter
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuInformationRequestSummary> getSentInfoRequests(String uid, InformationRequestSearchCriteria criteria, Page page) throws KokuServiceException;
	
	/**
	 * Returns list of infoRequests by given criteria params. See {@link InformationRequestSearchCriteria}
	 * 
	 * @param criteria
	 * @param position
	 * @param max
	 * @return
	 * @throws KokuServiceException
	 */
	ResultList<KokuInformationRequestSummary> getInfoRequests(InformationRequestSearchCriteria criteria, Page page) throws KokuServiceException;

	public interface InformationRequestSearchCriteria {
		Calendar getSentFrom();
		Calendar getSentTo();
		Calendar getRepliedFrom();
		Calendar getRepliedTo();
		String getTargetPersonUid();
		String getSenderUid();
		String getReceiverUid();
		String getFreeTextSearch();
		String getInformationContent();
	}
	
	
}
