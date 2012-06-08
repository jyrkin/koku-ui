package fi.arcusys.koku.web;

import static fi.arcusys.koku.common.util.Constants.*;

import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.facades.impl.ResponseStatus;
import fi.arcusys.koku.common.services.requests.citizen.CitizenRequestHandle;
import fi.arcusys.koku.common.services.requests.models.KokuRequest;
import fi.arcusys.koku.common.services.requests.models.KokuResponseDetail;
import fi.arcusys.koku.common.util.Constants;
import fi.arcusys.koku.common.util.DummyMessageSource;
import fi.arcusys.koku.web.util.ModelWrapper;
import fi.arcusys.koku.web.util.impl.ModelWrapperImpl;


/**
 * Shows request response details page and store the current query information on the jsp page
 * @author Jinhua Chen
 * Jul 29, 2011
 */
@Controller("singleResponseController")
@RequestMapping(value = "VIEW")
public class ShowResponseController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ShowResponseController.class);
	
	

	@ActionMapping(params = "action=toResponse")
	public void actionPageView(
			PortletSession session,
			@ModelAttribute(value = "response") ModelWrapper<KokuRequest> response,
			@RequestParam(value = "responseId") String responseId,
			@RequestParam(value = "taskType") String taskType,
			ActionResponse actionResponse) {
		actionResponse.setRenderParameter(ATTR_MY_ACTION, MY_ACTION_SHOW_REQUEST_RESPONSE);
		actionResponse.setRenderParameter(ATTR_TASK_TYPE, taskType);
		actionResponse.setRenderParameter(ATTR_RESPONSE_ID, responseId);
	}		
	
	/**
	 * Shows request page
	 * @param response RenderResponse
	 * @return request page
	 */
	@RenderMapping(params = "myaction=showResponse")
	public String showPageView(RenderResponse response) {
		return VIEW_SHOW_RESPONSE;
	}
		
	/**
	 * Creates data model integrated into the page and stores the page
	 * @param requestId request id
	 * @param currentPage current page id
	 * @param taskType task type requested
	 * @param keyword page parameter keyword
	 * @param orderType page parameter order type
	 * @param request RenderRequest
	 * @return request data model
	 */
	@ModelAttribute(value = "response")
	public ModelWrapper<KokuResponseDetail> model(
			@RequestParam String responseId,
			@RequestParam String taskType, 
			PortletSession portletSession) {
		
		ModelWrapper<KokuResponseDetail> model = null;
		KokuResponseDetail details = null;
		try {
			if (taskType.equals(Constants.TASK_TYPE_REQUEST_REPLIED) || taskType.equals(Constants.TASK_TYPE_REQUEST_OLD)) {
				CitizenRequestHandle handle = new CitizenRequestHandle(new DummyMessageSource());
				details = handle.getResponseById(responseId);
			}
			model = new ModelWrapperImpl<KokuResponseDetail>(details);
		} catch (KokuServiceException kse) {
			LOG.error("Failed to show response details. responseId: '"+responseId + 
					"' username: '"+ (String)portletSession.getAttribute(Constants.ATTR_USERNAME) +" taskType: '"+taskType + 
					"'", kse);
			model = new ModelWrapperImpl<KokuResponseDetail>(null, ResponseStatus.FAIL, kse.getErrorcode());
		}
		return model;
	}
}
