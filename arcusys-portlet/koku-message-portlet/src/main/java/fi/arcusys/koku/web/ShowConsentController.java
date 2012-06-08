package fi.arcusys.koku.web;


import static fi.arcusys.koku.common.util.Constants.ATTR_CONSENT_ID;
import static fi.arcusys.koku.common.util.Constants.ATTR_MY_ACTION;
import static fi.arcusys.koku.common.util.Constants.ATTR_TASK_TYPE;
import static fi.arcusys.koku.common.util.Constants.ATTR_USERNAME;
import static fi.arcusys.koku.common.util.Constants.ATTR_USER_ID;
import static fi.arcusys.koku.common.util.Constants.MY_ACTION_SHOW_CONSENT;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_CONSENT_CITIZEN_CONSENTS;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_CONSENT_CITIZEN_CONSENTS_OLD;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_CONSENT_EMPLOYEE_CONSENTS;
import static fi.arcusys.koku.common.util.Constants.VIEW_SHOW_CONSENT;

import javax.annotation.Resource;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.consents.citizen.TivaCitizenServiceHandle;
import fi.arcusys.koku.common.services.consents.employee.TivaEmployeeServiceHandle;
import fi.arcusys.koku.common.services.consents.model.KokuConsent;
import fi.arcusys.koku.common.services.facades.impl.ResponseStatus;
import fi.arcusys.koku.common.services.users.UserIdResolver;
import fi.arcusys.koku.common.util.Constants;
import fi.arcusys.koku.web.util.ModelWrapper;
import fi.arcusys.koku.web.util.impl.ModelWrapperImpl;

/**
 * Shows task form page and store the current query information on the jsp page
 * @author Jinhua Chen
 * Aug 17, 2011
 */
@Controller("singleConsentController")
@RequestMapping(value = "VIEW")
public class ShowConsentController extends AbstractController {
	private static final Logger LOG = LoggerFactory.getLogger(ShowConsentController.class);

	
	@Resource
	private ResourceBundleMessageSource messageSource;
	
	@ActionMapping(params = "action=toConsent")
	public void actionPageView(
			PortletSession session,
			@ModelAttribute(value = "consent") ModelWrapper<KokuConsent> response,
			@RequestParam(value = "consentId") String consentId,
			@RequestParam(value = "taskType") String taskType,
			ActionResponse actionResponse) {
		actionResponse.setRenderParameter(ATTR_MY_ACTION, MY_ACTION_SHOW_CONSENT);
		actionResponse.setRenderParameter(ATTR_TASK_TYPE, taskType);
		actionResponse.setRenderParameter(ATTR_CONSENT_ID, consentId);
	}
	
	/**
	 * Shows consent page
	 * @param response RenderResponse
	 * @return consent page
	 */
	@RenderMapping(params = "myaction=showConsent")
	public String showPageView(RenderResponse response) {
		return VIEW_SHOW_CONSENT;
	}
		
	/**
	 * Creates data model integrated into the page and stores the page
	 * @param consentId consent id
	 * @param currentPage current page id
	 * @param taskType task type requested
	 * @param keyword page parameter keyword
	 * @param orderType page parameter order type
	 * @param request RenderRequest
	 * @return consent data model
	 */
	@ModelAttribute(value = "consent")
	public ModelWrapper<KokuConsent> model(
			@RequestParam(value="consentId", required=false) String consentId,
			@RequestParam String taskType, 
			PortletSession portletSession) {
		
		ModelWrapper<KokuConsent> model = null;
		KokuConsent consent = null;
		
		final String username = (String) portletSession.getAttribute(ATTR_USERNAME);
		String userId = (String) portletSession.getAttribute(ATTR_USER_ID);
		if (userId == null) {
			try {			
				UserIdResolver resolver = new UserIdResolver();
				userId = resolver.getUserId(username, getPortalRole());
				portletSession.setAttribute(ATTR_USER_ID, userId);
			} catch (KokuServiceException e) {
				LOG.error("Failed to get UserUid username: '"+username+"' portalRole: '"+getPortalRole()+"'", e);
			}
		}
		
		try {			
			if (userId == null) {
				throw new KokuServiceException("UserId is null. Can't show consent details! username: '"+username+"'");
			}
			if(taskType.equals(TASK_TYPE_CONSENT_CITIZEN_CONSENTS) || taskType.equals(TASK_TYPE_CONSENT_CITIZEN_CONSENTS_OLD)) {
				TivaCitizenServiceHandle handle = new TivaCitizenServiceHandle(messageSource, userId);
				consent = handle.getConsentById(consentId);
			} else if(taskType.equals(TASK_TYPE_CONSENT_EMPLOYEE_CONSENTS)) {
				TivaEmployeeServiceHandle handle = new TivaEmployeeServiceHandle(messageSource);
				consent = handle.getConsentDetails(consentId);
			}
	//		else if (taskType.equals(TASK_TYPE_WARRANT_LIST_CITIZEN_CONSENTS)) {
	//			// TODO: Need some logic here? 
	//			// REMOVE ME?
	//		}
			model = new ModelWrapperImpl<KokuConsent>(consent);
		} catch (KokuServiceException kse) {
			LOG.error("Failed to show consent details. consentId: '"+consentId + 
					"' username: '"+(String)portletSession.getAttribute(Constants.ATTR_USERNAME)+"' taskType: '"+taskType + 
					"'", kse);
			model = new ModelWrapperImpl<KokuConsent>(null, ResponseStatus.FAIL, kse.getErrorcode());
		}
		return model;
	}
	
}
