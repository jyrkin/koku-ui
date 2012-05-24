package fi.arcusys.koku.common.services.warrants.citizens;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.citizen.CitizenWarrantTasks;
import fi.arcusys.koku.common.services.facades.impl.ResultListImpl;
import fi.arcusys.koku.common.services.warrants.AbstractWarrantHandle;
import fi.arcusys.koku.common.services.warrants.model.KokuAuthorizationSummary;
import fi.arcusys.koku.tiva.warrant.citizenwarrantservice.AuthorizationShortSummary;


public class KokuCitizenWarrantHandle extends AbstractWarrantHandle implements CitizenWarrantTasks {

	private final KokuCitizenWarrantService service;	
	
	/**
	 * Constructor and initialization
	 */
	public KokuCitizenWarrantHandle(MessageSource messageSource)  {
		super(messageSource);
		service = new KokuCitizenWarrantService();
	}
	
	@Override
	public ResultList<KokuAuthorizationSummary> getRecievedWarrants(String uid,
			Page page) throws KokuServiceException {
		final List<KokuAuthorizationSummary> warrants = getReceivedAuthorizations(uid, page);
		final int total = getTotalReceivedAuthorizations(uid);
		return new ResultListImpl<KokuAuthorizationSummary>(warrants, total, page);
	}

	@Override
	public ResultList<KokuAuthorizationSummary> getSentWarrants(String uid,
			Page page) throws KokuServiceException {
		final List<KokuAuthorizationSummary> warrants = getSentAuthorizations(uid, page);
		final int total = getTotalSentAuthorizations(uid);
		return new ResultListImpl<KokuAuthorizationSummary>(warrants, total, page);
	}
	
	private List<KokuAuthorizationSummary> getSentAuthorizations(String userId, Page page) throws KokuServiceException {
		 List<AuthorizationShortSummary> summaries = service.getSentAuthorizations(userId, page.getFirst(), page.getLast());
		 List<KokuAuthorizationSummary> summariesModels = new ArrayList<KokuAuthorizationSummary>();
		 for (AuthorizationShortSummary summary : summaries) {
			 KokuAuthorizationSummary shortSummary = new KokuAuthorizationSummary(summary);
			 shortSummary.setLocalizedStatus(getLocalizedAuthStatus(shortSummary.getStatus()));
			 summariesModels.add(shortSummary);
		 }
		 return summariesModels;
	}
	
	private List<KokuAuthorizationSummary> getReceivedAuthorizations(String userId, Page page) throws KokuServiceException {
		 List<AuthorizationShortSummary> summaries = service.getReceivedAuthorizations(userId, page.getFirst(), page.getLast());
		 List<KokuAuthorizationSummary> summariesModels = new ArrayList<KokuAuthorizationSummary>();
		 for (AuthorizationShortSummary summary : summaries) {
			 KokuAuthorizationSummary shortSummary = new KokuAuthorizationSummary(summary);
			 shortSummary.setLocalizedStatus(getLocalizedAuthStatus(shortSummary.getStatus()));
			 summariesModels.add(shortSummary);
		 }
		 return summariesModels;	
	}
	
	public KokuAuthorizationSummary getAuthorizationSummaryById(long authorizationId, String userId) throws KokuServiceException {
		
		KokuAuthorizationSummary summary = new KokuAuthorizationSummary(service.getAuthorizationSummaryById(authorizationId, userId));
		if (summary.getStatus() != null) {
			summary.setLocalizedStatus(getLocalizedAuthStatus(summary.getStatus()));
		}
		if (summary.getType() != null) {
			summary.setLocalizedType(getLocalizedWarrantCreateType(summary.getType()));
		}
		return summary;
	}
	
	public int getTotalSentAuthorizations(String userId) throws KokuServiceException {
		return service.getTotalSentAuthorizations(userId);
	}
	
	public int getTotalReceivedAuthorizations(String userId) throws KokuServiceException {
		return service.getTotalReceivedAuthorizations(userId);
	}
	
	public void revokeOwnAuthorization(long authorizationId, String user, String comment) throws KokuServiceException {
		service.revokeOwnAuthorization(authorizationId, user, comment);
	}

	
	
}
