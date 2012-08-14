package fi.arcusys.koku.common.services.inforequest.employee;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.employee.InfoRequestTasks;
import fi.arcusys.koku.common.services.facades.impl.ResultListImpl;
import fi.arcusys.koku.common.services.inforequest.AbstractTietopyyntoHandle;
import fi.arcusys.koku.common.services.inforequest.model.KokuInformationRequestDetail;
import fi.arcusys.koku.common.services.inforequest.model.KokuInformationRequestSummary;
import fi.arcusys.koku.common.util.MessageUtil;
import fi.arcusys.koku.tiva.tietopyynto.employee.InformationRequestCriteria;
import fi.arcusys.koku.tiva.tietopyynto.employee.InformationRequestQuery;
import fi.arcusys.koku.tiva.tietopyynto.employee.InformationRequestSummary;

/**
 * ServiceHandle to handle infoRequests (Tietopyynnöt)
 *
 * @author Toni Turunen
 *
 */
public class KokuEmployeeTietopyyntoServiceHandle extends AbstractTietopyyntoHandle implements InfoRequestTasks {

	private static final Logger LOG = LoggerFactory.getLogger(KokuEmployeeTietopyyntoServiceHandle.class);

	private final KokuEmployeeTietopyyntoService service;


	/**
	 * Constructor and initialization
	 */
	public KokuEmployeeTietopyyntoServiceHandle(MessageSource messageSource) {
		super(messageSource);
		service = new KokuEmployeeTietopyyntoService();
	}


	@Override
	public ResultList<KokuInformationRequestSummary> getRecievedAndRepliedInfoRequests(
			String uid, InformationRequestSearchCriteria criteria, Page page) throws KokuServiceException {
		return getRepliedRequests(uid, criteria, page);
	}

	@Override
	public ResultList<KokuInformationRequestSummary> getSentInfoRequests(
			String uid, InformationRequestSearchCriteria criteria, Page page) throws KokuServiceException {
		return getSentRequests(uid, criteria, page);
	}

	@Override
	public ResultList<KokuInformationRequestSummary> getInfoRequests(
			InformationRequestSearchCriteria criteria, Page page) throws KokuServiceException {
		return getRequests(criteria, page);
	}


	/**
	 * Returns list of replied KokuInformationRequestSummaries. (Tietopyyntö)
	 *
	 * @param keyword
	 * @param startNum
	 * @param maxNum
	 * @return List of KokuInformationRequestSummaries
	 */
	private ResultList<KokuInformationRequestSummary> getRepliedRequests(String userId, InformationRequestSearchCriteria criteria, Page page) throws KokuServiceException {
		final List<KokuInformationRequestSummary> infoRequests =  getRepliedRequests(
				userId,
				createCriteria(criteria),
				page.getFirst(),
				page.getLast()
			);
		final int totalInfoRequests = getTotalRepliedRequests(userId, criteria);
		return new ResultListImpl<KokuInformationRequestSummary>(infoRequests, totalInfoRequests, page);
	}

	/**
	 * Returns list of sent KokuInformationRequestSummaries. (Tietopyyntö)
	 *
	 * @param keyword
	 * @param startNum
	 * @param maxNum
	 * @return List of KokuInformationRequestSummaries
	 */
	private ResultList<KokuInformationRequestSummary> getSentRequests(String userId, InformationRequestSearchCriteria criteria, Page page) throws KokuServiceException {
		final List<KokuInformationRequestSummary> infoRequests = getSentRequests(
				userId,
				createCriteria(criteria),
				page.getFirst(),
				page.getLast()
			);
		final int totalSentInfoRequests = getTotalSentRequests(userId, criteria);
		return new ResultListImpl<KokuInformationRequestSummary>(infoRequests, totalSentInfoRequests, page);
	}

	/**
	 * Returns list of KokuInformationRequestSummaries. (Tietopyyntö) For admin users only
	 *
	 * @param keyword
	 * @param startNum
	 * @param maxNum
	 * @return List of KokuInformationRequestSummaries
	 */
	private ResultList<KokuInformationRequestSummary> getRequests(InformationRequestSearchCriteria criteria, Page page) throws KokuServiceException {
		final List<KokuInformationRequestSummary> infoRequests = getRequests(
				createCriteria(criteria),
				page.getFirst(),
				page.getLast()
			);
		final int totalRequests = getTotalRequests(criteria);
		return new ResultListImpl<KokuInformationRequestSummary>(infoRequests, totalRequests, page);
	}

	/**
	 * Returns detailed information about tietopyyntö
	 *
	 * @param requestId
	 * @return Detailed info
	 */
	public KokuInformationRequestDetail getRequestDetails(long requestId) throws KokuServiceException {
		KokuInformationRequestDetail details =  new KokuInformationRequestDetail(service.getRequestDetails(requestId));
		if (details.getAccessType() != null) {
			details.setLocalizedAccessType(getLocalizedInfoAccessType(details.getAccessType()));
		}
		localizeDetails(details);
		return details;
	}

	/**
	 * Returns total replied requests count
	 *
	 * @param receiverUid
	 * @return replied requests count
	 */
	public int getTotalRepliedRequests(String receiverUid, InformationRequestSearchCriteria criteria) throws KokuServiceException {
		return service.getTotalRepliedRequests(receiverUid, createCriteria(criteria));
	}

	/**
	 * Returns requests total
	 *
	 * @param receiverUid
	 * @return request count
	 */
	public int getTotalRequests(InformationRequestSearchCriteria criteria) throws KokuServiceException {
		return service.getTotalRequests(createCriteria(criteria));
	}

	/**
	 * Return sent requests count
	 *
	 * @param senderUid
	 * @return sent requests count
	 */
	public int getTotalSentRequests(String senderUid, InformationRequestSearchCriteria criteria) throws KokuServiceException {
		return service.getTotalSentRequests(senderUid, createCriteria(criteria));
	}


	private List<KokuInformationRequestSummary> getRepliedRequests(String userId, InformationRequestCriteria criteria, int startNum, int maxNum) throws KokuServiceException {
		try {
			return createLocalPojos(service.getRepliedRequests(userId, getInformationReqQuery(criteria, startNum, maxNum)));
		} catch (RuntimeException e) {
			LOG.error("Request: getRepliedRequests has failed", e);
			return new ArrayList<KokuInformationRequestSummary>();
		}
	}

	private List<KokuInformationRequestSummary> getSentRequests(String userId, InformationRequestCriteria criteria, int startNum, int maxNum) throws KokuServiceException {
		try {
			return createLocalPojos(service.getSentRequests(userId, getInformationReqQuery(criteria, startNum, maxNum)));
		} catch (RuntimeException e) {
			LOG.error("Request: getSentRequests has failed", e);
			return new ArrayList<KokuInformationRequestSummary>();
		}
	}

	private List<KokuInformationRequestSummary> getRequests(InformationRequestCriteria criteria, int startNum, int maxNum) throws KokuServiceException {
		try {
			return createLocalPojos(service.getRequests(getInformationReqQuery(criteria, startNum, maxNum)));
		} catch (RuntimeException e) {
			LOG.error("Request: getRequests has failed", e);
			return new ArrayList<KokuInformationRequestSummary>();
		}
	}

	private InformationRequestCriteria createCriteria(InformationRequestSearchCriteria searchCriteria) {
		InformationRequestCriteria criteria = new InformationRequestCriteria();
		criteria.setCreatedFromDate(MessageUtil.convertCalendarToXmlGregorianCalendar(searchCriteria.getSentFrom()));
		criteria.setCreatedToDate(MessageUtil.convertCalendarToXmlGregorianCalendar(searchCriteria.getSentTo()));
		criteria.setRepliedFromDate(MessageUtil.convertCalendarToXmlGregorianCalendar(searchCriteria.getRepliedFrom()));
		criteria.setRepliedToDate(MessageUtil.convertCalendarToXmlGregorianCalendar(searchCriteria.getRepliedTo()));
		criteria.setTargetPersonUid(searchCriteria.getTargetPersonUid());
		criteria.setReceiverUid(searchCriteria.getReceiverUid());
		criteria.setSenderUid(searchCriteria.getSenderUid());
		criteria.setFreeText(searchCriteria.getFreeTextSearch());
		criteria.setInformationContent(searchCriteria.getInformationContent());
		return criteria;
	}

	private List<KokuInformationRequestSummary> createLocalPojos(List<InformationRequestSummary> summaries) throws KokuServiceException {
		List<KokuInformationRequestSummary> kokuSummaries = new ArrayList<KokuInformationRequestSummary>();
		for (InformationRequestSummary summary : summaries) {
			KokuInformationRequestSummary kokuSummary = new KokuInformationRequestSummary(summary);
			localizeSummary(kokuSummary);
			kokuSummaries.add(kokuSummary);
		}
		return kokuSummaries;
	}

	private InformationRequestQuery getInformationReqQuery(InformationRequestCriteria criteria, int startNum, int maxNum) {
		InformationRequestQuery query = new InformationRequestQuery();
		query.setStartNum(startNum);
		query.setMaxNum(maxNum);
		query.setCriteria(criteria);
		return query;
	}

	private void localizeDetails(KokuInformationRequestDetail kokuSummary) throws KokuServiceException {
		localizeSummary(kokuSummary);
	}

	private void localizeSummary(KokuInformationRequestSummary kokuSummary) throws KokuServiceException {
		if (kokuSummary.getStatus() != null) {
			kokuSummary.setLocalizedStatus(getLocalizedInformationRequestSummary(kokuSummary.getStatus()));
		}
	}


}
