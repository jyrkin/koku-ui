package fi.arcusys.koku.common.services.requests.citizen;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.AbstractHandle;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.RequestTasks;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.impl.ResultListImpl;
import fi.arcusys.koku.common.services.requests.models.KokuResponseDetail;
import fi.arcusys.koku.common.services.requests.models.KokuResponseSummary;
import fi.arcusys.koku.kv.requestservice.ResponseDetail;
import fi.arcusys.koku.kv.requestservice.ResponseSummary;

/**
 * Handles request related operations
 * @author Jinhua Chen
 * Aug 22, 2011
 */
public class CitizenRequestHandle extends AbstractHandle implements RequestTasks {
		
	private CitizenRequestService rs;

	/**
	 * Constructor and initialization
	 */
	public CitizenRequestHandle(MessageSource messageSource) {
		super(messageSource);
		rs = new CitizenRequestService();
	}
	
	@Override
	public ResultList<KokuResponseSummary> getRequestsRecievedReplied(
			String uid, Page page) throws KokuCommonException {
		final List<KokuResponseSummary> responses = getRepliedRequests(uid, page);
		final int total = getTotalRepliedRequests(uid);
		return new ResultListImpl<KokuResponseSummary>(responses, total, page);
	}

	@Override
	public ResultList<KokuResponseSummary> getRequestsRecieviedRepliedOld(
			String uid, Page page) throws KokuCommonException {
		final List<KokuResponseSummary> responses = getOldRequests(uid, page);
		final int total = getTotalOldRequests(uid);
		return new ResultListImpl<KokuResponseSummary>(responses, total, page);	
	}
	
	/**
	 * Gets request summary and generates koku request data model
	 * @param userUid
	 * @param requestTypeStr request type string
	 * @param subQuery query string for quering
	 * @param startNum start index of request
	 * @param maxNum maximum number of requests
	 * @return a list of requests
	 */
	private List<KokuResponseSummary> getRepliedRequests(String userUid, Page page) throws KokuServiceException {		
		return getResponseList(rs.getRepliedRequests(userUid, page.getFirst(), page.getLast()));
	}
	
	/**
	 * Gets request summary and generates koku request data model
	 * @param userUid
	 * @param requestTypeStr request type string
	 * @param subQuery query string for quering
	 * @param startNum start index of request
	 * @param maxNum maximum number of requests
	 * @return a list of requests
	 */
	private List<KokuResponseSummary> getOldRequests(String userUid, Page page) throws KokuServiceException {
		return getResponseList(rs.getOldRequests(userUid, page.getFirst(), page.getLast()));
	}
	
	private List<KokuResponseSummary> getResponseList(List<ResponseSummary> reqs) {
		List<KokuResponseSummary> reqList = new ArrayList<KokuResponseSummary>();
		for (ResponseSummary summary : reqs) {
			reqList.add(new KokuResponseSummary(summary));
		}
		return reqList;
	}
	
	/**
	 * Gets request in detail
	 * @param requestId request id
	 * @return detailed request
	 */
	public KokuResponseDetail getResponseById(String requestId) throws KokuServiceException {
		long reqId = 0;
		try {
			reqId = (long) Long.parseLong(requestId);			
		} catch (NumberFormatException nfe) {
			throw new KokuServiceException("RequestId is not valid. requestId: '"+requestId+"'", nfe);
		}
		ResponseDetail req = rs.getResponseDetail(reqId);
		return new KokuResponseDetail(req);
	}
	
	/**
	 * Gets total number of old replied requests
	 * @param user user name
	 * @param requestTypeStr request type string
	 * @return the total number of requests
	 */
	private int getTotalOldRequests(String userUid) throws KokuServiceException {
		return rs.getTotalOldRequests(userUid);
	}
	
	/**
	 * Gets total number of old replied requests
	 * @param user user name
	 * @param requestTypeStr request type string
	 * @return the total number of requests
	 */
	private int getTotalRepliedRequests(String userUid) throws KokuServiceException {
		return rs.getTotalRepliedRequests(userUid);
	}



}
