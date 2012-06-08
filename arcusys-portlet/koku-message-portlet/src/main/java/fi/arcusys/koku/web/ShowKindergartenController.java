package fi.arcusys.koku.web;


import static fi.arcusys.koku.common.util.Constants.ATTR_APPLICATION_ID;
import static fi.arcusys.koku.common.util.Constants.ATTR_MY_ACTION;
import static fi.arcusys.koku.common.util.Constants.ATTR_TASK_TYPE;
import static fi.arcusys.koku.common.util.Constants.MY_ACTION_SHOW_APPLICATION_KINDERGARTEN;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_APPLICATION_KINDERGARTEN_BROWSE;
import static fi.arcusys.koku.common.util.Constants.VIEW_SHOW_APPLICATION_KINDERGARTEN;

import javax.annotation.Resource;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fi.arcusys.koku.common.services.hak.employee.HakServiceHandle;
import fi.arcusys.koku.common.services.hak.model.KokuApplicationSummary;
import fi.arcusys.koku.web.util.ModelWrapper;

/**
 * Shows applications (Daycare)
 * 
 * @author Toni Turunen
 */
@Controller("singleKindergartenController")
@RequestMapping(value = "VIEW")
public class ShowKindergartenController extends AbstractController {
	
	@Resource
	private ResourceBundleMessageSource messageSource;

	@ActionMapping(params = "action=toApplications")
	public void actionPageView(
			PortletSession session,
			@ModelAttribute(value = "application") ModelWrapper<KokuApplicationSummary> application,
			@RequestParam(value = "applicationId") String applicationId,
			@RequestParam(value = "taskType") String taskType,
			ActionResponse actionResponse) {
		actionResponse.setRenderParameter(ATTR_MY_ACTION, MY_ACTION_SHOW_APPLICATION_KINDERGARTEN);
		actionResponse.setRenderParameter(ATTR_TASK_TYPE, taskType);
		actionResponse.setRenderParameter(ATTR_APPLICATION_ID, applicationId);
	}	
	
	/**
	 * Shows warrant page
	 * @param response RenderResponse
	 * @return consent page
	 */
	@RenderMapping(params = "myaction=showApplicationKindergarten")
	public String showPageView(RenderResponse response) {
		return VIEW_SHOW_APPLICATION_KINDERGARTEN;
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
	@ModelAttribute(value = "application")
	public KokuApplicationSummary model(
			@RequestParam String applicationId,
			@RequestParam String taskType, 
			PortletSession portletSession) {
		
		KokuApplicationSummary application = null;		
		if(taskType.equals(TASK_TYPE_APPLICATION_KINDERGARTEN_BROWSE)) {
			HakServiceHandle handle = new HakServiceHandle(messageSource);
			application = handle.getApplicantDetails(applicationId);
		}
		return application;
	}
}
