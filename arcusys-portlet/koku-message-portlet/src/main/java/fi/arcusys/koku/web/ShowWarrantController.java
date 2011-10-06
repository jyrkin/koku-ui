package fi.arcusys.koku.web;


import static fi.arcusys.koku.util.Constants.*;

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

import fi.arcusys.koku.tiva.warrant.citizens.KokuCitizenWarrantHandle;
import fi.arcusys.koku.tiva.warrant.employee.KokuEmployeeWarrantHandle;
import fi.arcusys.koku.tiva.warrant.employeewarrantservice.KokuLooraValtakirjaService_Service;
import fi.arcusys.koku.tiva.warrant.model.KokuAuthorizationSummary;
import fi.arcusys.koku.users.UserIdResolver;

/**
 * Shows warrant form page and store the current query information on the jsp page
 * 
 * @author Toni Turunen
 */
@Controller("singleWarrantController")
@RequestMapping(value = "VIEW")
public class ShowWarrantController extends AbstractController {
	private Logger LOG = Logger.getLogger(ShowWarrantController.class);

	
	@Resource
	ResourceBundleMessageSource messageSource;
	
	/**
	 * Shows warrant page
	 * @param response RenderResponse
	 * @return consent page
	 */
	@RenderMapping(params = "myaction=showWarrant")
	public String showPageView(RenderResponse response) {
		return VIEW_SHOW_WARRANT;
	}
		
	/**
	 * Creates data model integrated into the page and stores the page
	 * @param authorizationId authorization id
	 * @param currentPage current page id
	 * @param taskType task type requested
	 * @param keyword page parameter keyword
	 * @param orderType page parameter order type
	 * @param request RenderRequest
	 * @return KokuAuthorizationSummary data model
	 */
	@ModelAttribute(value = "warrant")
	public KokuAuthorizationSummary model(
			@RequestParam String authorizationId,
			@RequestParam String currentPage,
			@RequestParam String taskType, 
			@RequestParam String keyword,
			@RequestParam String orderType,
			RenderRequest request) {

		// store parameters in session for returning page from form page	
		request.getPortletSession().setAttribute(ATTR_CURRENT_PAGE, currentPage, PortletSession.APPLICATION_SCOPE);
		request.getPortletSession().setAttribute(ATTR_TASK_TYPE, taskType, PortletSession.APPLICATION_SCOPE);
		request.getPortletSession().setAttribute(ATTR_KEYWORD, keyword, PortletSession.APPLICATION_SCOPE);
		request.getPortletSession().setAttribute(ATTR_ORDER_TYPE, orderType, PortletSession.APPLICATION_SCOPE);
		
		KokuAuthorizationSummary warrant = null;
		
		PortletSession portletSession = request.getPortletSession();
		String username = (String) portletSession.getAttribute(ATTR_USERNAME);
		UserIdResolver resolver = new UserIdResolver();
		String userId = resolver.getUserId(username, getPortalRole(request));
		long authId = -1; 
		try {
			authId  = Long.valueOf(authorizationId);
		} catch (NumberFormatException nfe) {
			LOG.error("AuthorizationID is not valid! Username: " + username + " UserId: " + userId + " AuthorizationId: "+ authorizationId);
			return null;
		}
		
		if(taskType.equals(TASK_TYPE_WARRANT_BROWSE_RECEIEVED)) {
			KokuCitizenWarrantHandle handle = new KokuCitizenWarrantHandle();
			handle.setMessageSource(messageSource);
			warrant = handle.getAuthorizationSummaryById(authId, userId);
		} else if(taskType.equals(TASK_TYPE_WARRANT_BROWSE_SENT)) {
			KokuCitizenWarrantHandle handle = new KokuCitizenWarrantHandle();
			handle.setMessageSource(messageSource);
			warrant = handle.getAuthorizationSummaryById(authId, userId);
		} else if (taskType.equals(TASK_TYPE_WARRANT_LIST_CITIZEN_CONSENTS) || taskType.equals(TASK_TYPE_WARRANT_LIST_SUBJECT_CONSENTS)) {
			KokuEmployeeWarrantHandle handle = new KokuEmployeeWarrantHandle();
			handle.setMessageSource(messageSource);
			try {
				warrant = handle.getAuthorizationDetails(Integer.valueOf(authorizationId));					
			} catch (NumberFormatException nfe) {
				// Silently catch exception
			}
		}
		return warrant;
	}
	
}