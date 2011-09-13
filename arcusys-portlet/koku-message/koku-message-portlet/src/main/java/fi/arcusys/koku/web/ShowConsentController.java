package fi.arcusys.koku.web;


import javax.annotation.Resource;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fi.arcusys.koku.tiva.KokuConsent;
import fi.arcusys.koku.tiva.TivaCitizenServiceHandle;
import fi.arcusys.koku.tiva.TivaEmployeeServiceHandle;
import static fi.arcusys.koku.util.Constants.*;

/**
 * Shows task form page and store the current query information on the jsp page
 * @author Jinhua Chen
 * Aug 17, 2011
 */
@Controller("singleConsentController")
@RequestMapping(value = "VIEW")
public class ShowConsentController {
	private Logger LOG = Logger.getLogger(ShowConsentController.class);

	
	@Resource
	ResourceBundleMessageSource messageSource;
	
	/**
	 * Shows consent page
	 * @param response RenderResponse
	 * @return consent page
	 */
	@RenderMapping(params = "myaction=showConsent")
	public String showPageView(RenderResponse response) {
		return "showconsent";
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
	public KokuConsent model(@RequestParam String consentId,
			@RequestParam String currentPage,@RequestParam String taskType, 
			@RequestParam String keyword, @RequestParam String orderType,
			RenderRequest request) {

		// store parameters in session for returning page from form page	
		request.getPortletSession().setAttribute(ATTR_CURRENT_PAGE, currentPage, PortletSession.APPLICATION_SCOPE);
		request.getPortletSession().setAttribute(ATTR_TASK_TYPE, taskType, PortletSession.APPLICATION_SCOPE);
		request.getPortletSession().setAttribute(ATTR_KEYWORD, keyword, PortletSession.APPLICATION_SCOPE);
		request.getPortletSession().setAttribute(ATTR_ORDER_TYPE, orderType, PortletSession.APPLICATION_SCOPE);
		
		KokuConsent consent = null;
		
		if(taskType.equals(TASK_TYPE_CONSENT_CITIZEN_CONSENTS)) {
			PortletSession portletSession = request.getPortletSession();
			String username = (String) portletSession.getAttribute(ATTR_USERNAME);
			TivaCitizenServiceHandle handle = new TivaCitizenServiceHandle(username);
			handle.setMessageSource(messageSource);
			consent = handle.getConsentById(consentId);
		} else if(taskType.equals(TASK_TYPE_CONSENT_EMPLOYEE_CONSENTS)) {
			TivaEmployeeServiceHandle handle = new TivaEmployeeServiceHandle();
			handle.setMessageSource(messageSource);
			consent = handle.getConsentDetails(consentId);
		} else if (taskType.equals(TASK_TYPE_CONSENT_LIST_CITIZEN_CONSENTS)) {
			// TODO: Need some logic here? 
			// REMOVE ME?
		}
		return consent;
	}
	
}
