package fi.arcusys.koku.web;

import static fi.arcusys.koku.common.util.Constants.ATTR_MY_ACTION;
import static fi.arcusys.koku.common.util.Constants.ATTR_REQUEST_ID;
import static fi.arcusys.koku.common.util.Constants.MY_ACTION_SHOW_REQUEST;
import static fi.arcusys.koku.common.util.Constants.VIEW_SHOW_REQUEST;

import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
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
import fi.arcusys.koku.common.services.requests.employee.EmployeeRequestHandle;
import fi.arcusys.koku.common.services.requests.models.KokuRequest;
import fi.arcusys.koku.common.util.Constants;
import fi.arcusys.koku.common.util.DummyMessageSource;
import fi.arcusys.koku.common.util.Properties;
import fi.arcusys.koku.web.util.ModelWrapper;
import fi.arcusys.koku.web.util.impl.ModelWrapperImpl;


/**
 * Shows request details page and store the current query information on the jsp page
 * 
 * @author Jinhua Chen
 * Jul 29, 2011
 */
@Controller("singleRequestController")
@RequestMapping(value = "VIEW")
public class ShowRequestController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ShowRequestController.class);

	
	@ActionMapping(params = "action=toRequest")
	public void actionPageView(
			PortletSession session,
			@ModelAttribute(value = "request") ModelWrapper<KokuRequest> request,
			@RequestParam(value = "requestId") String requestId,
			ActionResponse response) {
		response.setRenderParameter(ATTR_MY_ACTION, MY_ACTION_SHOW_REQUEST);
		response.setRenderParameter(ATTR_REQUEST_ID, requestId);
	}	
	
	/**
	 * Shows request page
	 * @param response RenderResponse
	 * @return request page
	 */
	@RenderMapping(params = "myaction=showRequest")
	public String showPageView(RenderResponse response) {
		return VIEW_SHOW_REQUEST;
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
	@ModelAttribute(value = "request")
	public ModelWrapper<KokuRequest> model(
			@RequestParam String requestId,
			PortletSession portletSession) {
		
		ModelWrapper<KokuRequest> model = null;
		KokuRequest kokuRequest = null;
		try {
			if (Properties.IS_LOORA_PORTAL) {
				EmployeeRequestHandle reqhandle = new EmployeeRequestHandle(new DummyMessageSource());
				kokuRequest = reqhandle.getKokuRequestById(requestId);			
			} else {
				throw new KokuServiceException("No operation. username: '" + (String)portletSession.getAttribute(Constants.ATTR_USERNAME)  + "'");
			}
			model = new ModelWrapperImpl<KokuRequest>(kokuRequest);
		} catch (KokuServiceException kse) {
			LOG.error("Failed to show request details. requestId: '"+requestId + 
					"' username: '"+ (String)portletSession.getAttribute(Constants.ATTR_USERNAME) +"'", kse);
			model = new ModelWrapperImpl<KokuRequest>(null, ResponseStatus.FAIL, kse.getErrorcode());
		}		
		return model;
	}
}
