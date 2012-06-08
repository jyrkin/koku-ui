package fi.arcusys.koku.web;


import static fi.arcusys.koku.common.util.Constants.ATTR_AUTHORIZATION_ID;
import static fi.arcusys.koku.common.util.Constants.ATTR_MY_ACTION;
import static fi.arcusys.koku.common.util.Constants.ATTR_TASK_TYPE;
import static fi.arcusys.koku.common.util.Constants.ATTR_USERNAME;
import static fi.arcusys.koku.common.util.Constants.ATTR_USER_ID;
import static fi.arcusys.koku.common.util.Constants.MY_ACTION_SHOW_WARRANT;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_WARRANT_BROWSE_RECEIEVED;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_WARRANT_BROWSE_SENT;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_WARRANT_LIST_CITIZEN_CONSENTS;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_WARRANT_LIST_SUBJECT_CONSENTS;
import static fi.arcusys.koku.common.util.Constants.VIEW_SHOW_WARRANT;

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
import fi.arcusys.koku.common.services.facades.impl.ResponseStatus;
import fi.arcusys.koku.common.services.users.UserIdResolver;
import fi.arcusys.koku.common.services.warrants.citizens.KokuCitizenWarrantHandle;
import fi.arcusys.koku.common.services.warrants.employee.KokuEmployeeWarrantHandle;
import fi.arcusys.koku.common.services.warrants.model.KokuAuthorizationSummary;
import fi.arcusys.koku.web.util.ModelWrapper;
import fi.arcusys.koku.web.util.impl.ModelWrapperImpl;

/**
 * Shows warrant form page and store the current query information on the jsp page
 * 
 * @author Toni Turunen
 */
@Controller("singleWarrantController")
@RequestMapping(value = "VIEW")
public class ShowWarrantController extends AbstractController {
	private static final Logger LOG = LoggerFactory.getLogger(ShowWarrantController.class);
	
	@Resource
	private ResourceBundleMessageSource messageSource;
	
	@ActionMapping(params = "action=toWarrant")
	public void actionPageView(
			PortletSession session,
			@ModelAttribute(value = "warrant") ModelWrapper<KokuAuthorizationSummary> warrant,
			@RequestParam(value = "authorizationId") String authorizationId,
			@RequestParam(value = "taskType") String taskType,
			ActionResponse actionResponse) {
		actionResponse.setRenderParameter(ATTR_MY_ACTION, MY_ACTION_SHOW_WARRANT);
		actionResponse.setRenderParameter(ATTR_TASK_TYPE, taskType);
		actionResponse.setRenderParameter(ATTR_AUTHORIZATION_ID, authorizationId);
	}
	
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
	public ModelWrapper<KokuAuthorizationSummary> model(
			@RequestParam String authorizationId,
			@RequestParam String taskType, 
			PortletSession portletSession) {
		
		ModelWrapper<KokuAuthorizationSummary> model = null;		
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
		
		KokuAuthorizationSummary warrant = null;
		try {			
			long authId = -1; 
			try {
				authId  = Long.valueOf(authorizationId);
			} catch (NumberFormatException nfe) {
				throw new KokuServiceException("AuthorizationID is not valid! Username: '" + username + "' UserId: '" + userId + "' AuthorizationId: '"+ authorizationId+"'", nfe);
			}
			if(taskType.equals(TASK_TYPE_WARRANT_BROWSE_RECEIEVED)) {
				KokuCitizenWarrantHandle handle = new KokuCitizenWarrantHandle(messageSource);
				warrant = handle.getAuthorizationSummaryById(authId, userId);
			} else if(taskType.equals(TASK_TYPE_WARRANT_BROWSE_SENT)) {
				KokuCitizenWarrantHandle handle = new KokuCitizenWarrantHandle(messageSource);
				warrant = handle.getAuthorizationSummaryById(authId, userId);
			} else if (taskType.equals(TASK_TYPE_WARRANT_LIST_CITIZEN_CONSENTS) || taskType.equals(TASK_TYPE_WARRANT_LIST_SUBJECT_CONSENTS)) {
				KokuEmployeeWarrantHandle handle = new KokuEmployeeWarrantHandle(messageSource);
				try {
					warrant = handle.getAuthorizationDetails(Integer.valueOf(authorizationId));					
				} catch (NumberFormatException nfe) {
					throw new KokuServiceException("AuthorizationID is not valid! Username: '" + username + "' UserId: '" + userId + "' AuthorizationId: '"+ authorizationId+"'", nfe);
				}
			}
			model = new ModelWrapperImpl<KokuAuthorizationSummary>(warrant);
		} catch (KokuServiceException kse) {
			LOG.error("Failed to show warrant details. authorizationId: '"+authorizationId + 
					"' username: '"+username+" taskType: '"+taskType + "'", kse);
			model = new ModelWrapperImpl<KokuAuthorizationSummary>(null, ResponseStatus.FAIL, kse.getErrorcode());
		}
		return model;
	}
	
}
