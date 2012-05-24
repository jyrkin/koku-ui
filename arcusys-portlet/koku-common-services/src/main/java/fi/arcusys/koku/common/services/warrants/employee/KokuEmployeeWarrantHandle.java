package fi.arcusys.koku.common.services.warrants.employee;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.employee.EmployeeWarrantTasks;
import fi.arcusys.koku.common.services.facades.impl.ResultListImpl;
import fi.arcusys.koku.common.services.warrants.AbstractWarrantHandle;
import fi.arcusys.koku.common.services.warrants.model.KokuAuthorizationSummary;
import fi.arcusys.koku.common.services.warrants.model.KokuValtakirjapohja;
import fi.arcusys.koku.tiva.warrant.employeewarrantservice.AuthorizationCriteria;
import fi.arcusys.koku.tiva.warrant.employeewarrantservice.AuthorizationQuery;
import fi.arcusys.koku.tiva.warrant.employeewarrantservice.AuthorizationShortSummary;
import fi.arcusys.koku.tiva.warrant.employeewarrantservice.Valtakirjapohja;

/**
 * Handles warrants (valtakirja) related operations for employee
 * 
 * @author Toni Turunen
 *
 */
public class KokuEmployeeWarrantHandle extends AbstractWarrantHandle implements EmployeeWarrantTasks {
	
	private static final int MAX_SEARCH_RESULTS 	= 1;

	private final KokuEmployeeWarrantService service;
	
	public KokuEmployeeWarrantHandle(MessageSource messageSource) {
		super(messageSource);
		service = new KokuEmployeeWarrantService();	
	}
	
	@Override
	public ResultList<KokuAuthorizationSummary> getWarrants(WarrantSearchCriteria criteria, Page page)
			throws KokuServiceException {
		final Long templateId = getAuthorizationTemplateId(criteria.getTemplateName());
		WarrantSearchCriteria search = null;
		if (templateId != null && templateId > 0) {
			search = new WarrantSearchCriteriaImpl(templateId, criteria);
		} else {
			search = criteria;
		}
		final List<KokuAuthorizationSummary> authorizations = getAuthorizations(search, page);
		final int total = getTotalAuthorizations(search);
		return new ResultListImpl<KokuAuthorizationSummary>(authorizations, total, page);
	}
	
	private Long getAuthorizationTemplateId(String templateName) throws KokuServiceException {
		if (templateName == null || templateName.isEmpty() || templateName.trim().isEmpty()) {
			return null;
		}
		final List<Valtakirjapohja> templates = service.searchAuthorizationTemplates(templateName, MAX_SEARCH_RESULTS);
		if (templates != null && !templates.isEmpty()) {
			return templates.get(0).getTemplateId();
		} else {
			return null;
		}
	}
		
	/**
	 * Return authorizations by given authorization template, recipient and user 
	 * 
	 * @param searchCriteria
	 * @param page
	 * @return List of Authorizations
	 */
	private List<KokuAuthorizationSummary> getAuthorizations(WarrantSearchCriteria searchCriteria, Page page) throws KokuServiceException {
		AuthorizationQuery query = new AuthorizationQuery();
		query.setCriteria(createCriteria(searchCriteria));
		query.setStartNum(page.getFirst());
		query.setMaxNum(page.getLast());
		List<AuthorizationShortSummary> authShortSummaries = service.getAuthorizations(query);
		List<KokuAuthorizationSummary> summaries = new ArrayList<KokuAuthorizationSummary>();
		for (AuthorizationShortSummary shortSummary : authShortSummaries) {
			KokuAuthorizationSummary kokuSummary = new KokuAuthorizationSummary(shortSummary);
			localize(kokuSummary);
			summaries.add(kokuSummary);
		}
		return summaries;
	}
	
	private AuthorizationCriteria createCriteria(WarrantSearchCriteria searchCriteria) {
		AuthorizationCriteria criteria = new AuthorizationCriteria();
		criteria.setSenderUid(searchCriteria.getSenderUid());
		criteria.setReceipientUid(searchCriteria.getRecipientUid());
		criteria.setTargetPersonUid(searchCriteria.getTargetPersonUid());
		criteria.setAuthorizationTemplateId(searchCriteria.getAuthorizationTemplateId());
		return criteria;
	}
	
	/**
	 * Search Valtakirjapohja(s) by templateName. Keyword can also
	 * be partial name of Valtakirja. 
	 * 
	 * 
	 * @param templateName (also partial name accepted)
	 * @param maxNum how many results will be returned
	 * @return List of KokuValtakirjapohjas
	 */
	public List<KokuValtakirjapohja> searchWarrantTemplates(String templateName, int maxNum) throws KokuServiceException {
		List<Valtakirjapohja> warrantTemplates = service.searchAuthorizationTemplates(templateName, maxNum);
		List<KokuValtakirjapohja> kokuWarrantTemplates = new ArrayList<KokuValtakirjapohja>();
		for (Valtakirjapohja warrantTemplate : warrantTemplates) {
			kokuWarrantTemplates.add(new KokuValtakirjapohja(warrantTemplate));
		}
		return kokuWarrantTemplates;		
	}
	
	/**
	 * Return number of received authorizations by recipient UID
	 * 
	 * @param recipientUserId
	 * @return number of recivied authorizations
	 * @throws KokuServiceException
	 */
	public int getUserRecievedWarrantCount(String recipientUserId) throws KokuServiceException {
		AuthorizationCriteria criteria = new AuthorizationCriteria();
		criteria.setReceipientUid(recipientUserId);
		return service.getTotalAuthorizations(criteria);
	}
	
	/**
	 * Return number of total authorizartions by templateId, recipient UID and sender UID
	 * 
	 * @param authorizationTemplateId
	 * @param receipientUid
	 * @param senderUid
	 * @return
	 * @throws KokuServiceException
	 */
	private int getTotalAuthorizations(WarrantSearchCriteria criteria) throws KokuServiceException {
		return service.getTotalAuthorizations(createCriteria(criteria));
	}

	/**
	 * Return authorization with additional details by given authorizationId
	 * 
	 * @param valtakirjaId
	 * @return Authorization with additional details
	 * @throws KokuServiceException
	 */
	public KokuAuthorizationSummary getAuthorizationDetails(int valtakirjaId) throws KokuServiceException {
		KokuAuthorizationSummary kokuSummary = new KokuAuthorizationSummary(service.getAuthorizationDetails(valtakirjaId));
		localize(kokuSummary);
		return kokuSummary;
	}
	
	private void localize(KokuAuthorizationSummary kokuSummary) throws KokuServiceException {
		kokuSummary.setLocalizedStatus(getLocalizedAuthStatus(kokuSummary.getStatus()));
	}
	
	private static class WarrantSearchCriteriaImpl implements WarrantSearchCriteria {
		
		public final WarrantSearchCriteria criteria;
		public final Long templateId;
		
		public WarrantSearchCriteriaImpl(Long templateId, WarrantSearchCriteria criteria) {
			this.criteria = criteria;
			this.templateId = templateId;
		}
		
		@Override
		public String getTemplateName() {
			return null;
		}

		@Override
		public Long getAuthorizationTemplateId() {
			return templateId;
		}

		@Override
		public String getRecipientUid() {
			return criteria.getRecipientUid();
		}

		@Override
		public String getSenderUid() {
			return criteria.getSenderUid();
		}

		@Override
		public String getTargetPersonUid() {
			return criteria.getTargetPersonUid();
		}		
	}	
}
