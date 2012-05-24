package fi.arcusys.koku.common.services.requests.employee;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuCommonException;
import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.AbstractHandle;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.employee.EmployeeRequestTasks;
import fi.arcusys.koku.common.services.facades.impl.ResultListImpl;
import fi.arcusys.koku.common.services.requests.models.KokuRequest;
import fi.arcusys.koku.common.services.requests.models.KokuResponseDetail;
import fi.arcusys.koku.common.services.requests.models.KokuResponseSummary;
import fi.arcusys.koku.kv.requestservice.RequestSummary;
import fi.arcusys.koku.kv.requestservice.RequestType;
import fi.arcusys.koku.kv.requestservice.ResponseSummary;

/**
 * Handles request related operations
 * @author Jinhua Chen
 * Aug 22, 2011
 */
public class EmployeeRequestHandle extends AbstractHandle implements EmployeeRequestTasks  {
		
	private EmployeeRequestService rs;

	/**
	 * Constructor and initialization
	 */
	public EmployeeRequestHandle(MessageSource messageSource) {
		super(messageSource);
		rs = new EmployeeRequestService();
	}
	
	@Override
	public ResultList<KokuResponseSummary> getRequestsRecievedReplied(String uid, Page page) throws KokuCommonException {
		return getRepliedResponseSummaries(uid, page);
	}

	@Override
	public ResultList<KokuResponseSummary> getRequestsRecieviedRepliedOld(String uid, Page page) throws KokuCommonException {
		return getOldResponseSummaries(uid, page);
	}

	@Override
	public ResultList<KokuRequest> getRequestsSentOpen(String uid, Page page) throws KokuCommonException {
		return getRequests(uid, RequestType.VALID, null, page);
	}

	@Override
	public ResultList<KokuRequest> getRequestsSentDone(String uid, Page page) throws KokuCommonException {
		return getRequests(uid, RequestType.OUTDATED, null, page);
	}
	
	/**
	 * Returns list of requestSummaries
	 * 
	 * @param username user name
	 * @param requestTypeStr request type string
	 * @param subQuery query string for quering
	 * @param startNum start index of request
	 * @param maxNum maximum number of requests
	 * @return a list of requests
	 */
	private ResultList<KokuRequest> getRequests(String userId, RequestType requestType, String subQuery, Page page)  throws KokuServiceException {
		final List<KokuRequest> reqList = new ArrayList<KokuRequest>();
		final List<RequestSummary> reqs = rs.getRequests(userId, requestType, subQuery, page.getFirst(), page.getLast());
		for (RequestSummary summary : reqs) {
			reqList.add(new KokuRequest(summary));
		}
		final int totalRequests = getTotalRequestsNum(userId, requestType);
		return new ResultListImpl<KokuRequest>(reqList, totalRequests, page);
	}
	
	/**
	 * Returns list of replied responses
	 * 
	 * @param userUid
	 * @param startNum
	 * @param maxNum
	 * @return a list of replied responses
	 */
	private ResultList<KokuResponseSummary> getRepliedResponseSummaries(String userUid, Page page) throws KokuServiceException {
		final List<KokuResponseSummary> responses = rawToViewModel(rs.getRepliedRequests(userUid, page.getFirst(), page.getLast()));
		final int totalResponses = getTotalResponsesRepliedNum(userUid);
		return new ResultListImpl<KokuResponseSummary>(responses, totalResponses, page);		
	}
	
	/**
	 * Returns list of old responses
	 * 
	 * @param userUid
	 * @param startNum
	 * @param maxNum
	 * @return a list of old requests
	 */
	private ResultList<KokuResponseSummary> getOldResponseSummaries(String userUid, Page page) throws KokuServiceException {
		final List<KokuResponseSummary> responses = rawToViewModel(rs.getOldRequests(userUid, page.getFirst(), page.getLast()));
		final int totalResponses = getTotalResponsesOldNum(userUid);
		return new ResultListImpl<KokuResponseSummary>(responses, totalResponses, page);
	}
	
	private List<KokuResponseSummary> rawToViewModel(List<ResponseSummary> responses) {
		List<KokuResponseSummary> resultList = new ArrayList<KokuResponseSummary>();
		for (ResponseSummary summary : responses) {
			resultList.add(new KokuResponseSummary(summary));
		}
		return resultList;
	}
	
	/**
	 * Returns request in detail
	 * 
	 * @param requestId request id
	 * @return detailed request
	 */
	public KokuRequest getKokuRequestById(String requestId) throws KokuServiceException {
		long  reqId = 0;
		try {
			reqId = (long) Long.parseLong(requestId);			
		} catch (NumberFormatException nfe) {
			throw new KokuServiceException("Given requestId invalid. RequestId: '"+requestId+"'", nfe);
		}
		return getKokuRequestById(reqId);
	}	

	/**
	 * Returns request in detail
	 * 
	 * @param requestId request id
	 * @return detailed request
	 */
	public KokuRequest getKokuRequestById(long requestId) throws KokuServiceException {
		return new KokuRequest(rs.getRequestById(requestId));
	}
	
	/**
	 * Returns response in detail
	 * 
	 * @param responseId
	 * @return detailed response
	 */
	public KokuResponseDetail getKokuResponseById(long responseId) throws KokuServiceException {
		return new KokuResponseDetail(rs.getResponseById(responseId));
	}	
	
	/**
	 * Returns total number of requests
	 * 
	 * @param user user name
	 * @param requestTypeStr request type string
	 * @return the total number of requests
	 */
	private int getTotalRequestsNum(String userId, RequestType requestType) throws KokuServiceException {
		return rs.getTotalRequestNum(userId, requestType);
	}
	
	/**
	 * Returns total number of old responses
	 * 
	 * @param userUid
	 * @return the total number of old responses
	 */
	private int getTotalResponsesOldNum(String userUid) throws KokuServiceException {
		return rs.getTotalResponsesOldNum(userUid);
	}
	
	/**
	 * Returns total number of replied responses
	 * 
	 * @param userUid
	 * @return the total number of old responses
	 */
	private int getTotalResponsesRepliedNum(String userUid) throws KokuServiceException {
		return rs.getTotalResponsesRepliedNum(userUid);
	}


}
