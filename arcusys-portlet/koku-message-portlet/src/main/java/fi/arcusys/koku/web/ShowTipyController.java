package fi.arcusys.koku.web;


import static fi.arcusys.koku.common.util.Constants.ATTR_MY_ACTION;
import static fi.arcusys.koku.common.util.Constants.ATTR_REQUEST_ID;
import static fi.arcusys.koku.common.util.Constants.ATTR_USERNAME;
import static fi.arcusys.koku.common.util.Constants.ATTR_USER_ID;
import static fi.arcusys.koku.common.util.Constants.MY_ACTION_SHOW_TIPY;
import static fi.arcusys.koku.common.util.Constants.VIEW_SHOW_INFO_REQUEST;

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
import fi.arcusys.koku.common.services.inforequest.employee.KokuEmployeeTietopyyntoServiceHandle;
import fi.arcusys.koku.common.services.inforequest.model.KokuInformationRequestDetail;
import fi.arcusys.koku.common.services.users.UserIdResolver;
import fi.arcusys.koku.common.util.Properties;
import fi.arcusys.koku.web.util.ModelWrapper;
import fi.arcusys.koku.web.util.impl.ModelWrapperImpl;

/**
 * Shows Tietopyyntö form page and store the current query information on the jsp page
 * 
 * @author Toni Turunen
 */
@Controller("singleTipyController")
@RequestMapping(value = "VIEW")
public class ShowTipyController extends AbstractController {
	private static final Logger LOG = LoggerFactory.getLogger(ShowTipyController.class);

	@Resource
	private ResourceBundleMessageSource messageSource;
	
	@ActionMapping(params = "action=toTipy")
	public void actionPageView(
			PortletSession session,
			@ModelAttribute(value = "tipy") ModelWrapper<KokuInformationRequestDetail> tipy,
			@RequestParam(value = "requestId") String requestId,
			ActionResponse actionResponse) {
		actionResponse.setRenderParameter(ATTR_MY_ACTION, MY_ACTION_SHOW_TIPY);
		actionResponse.setRenderParameter(ATTR_REQUEST_ID, requestId);
	}
	
	/**
	 * Shows warrant page
	 * @param response RenderResponse
	 * @return consent page
	 */
	@RenderMapping(params = "myaction=showTipy")
	public String showPageView(RenderResponse response) {
		return VIEW_SHOW_INFO_REQUEST;
	}
		
	/**
	 * Creates data model integrated into the page and stores the page
	 * @param authorizationId authorization id
	 * @param currentPage current page id
	 * @param keyword page parameter keyword
	 * @param orderType page parameter order type
	 * @param request RenderRequest
	 * @return KokuAuthorizationSummary data model
	 */
	@ModelAttribute(value = "tipy")
	public ModelWrapper<KokuInformationRequestDetail> model(
			@RequestParam String requestId,
			PortletSession portletSession) {
		
		ModelWrapper<KokuInformationRequestDetail> modelWrapper = null;
		
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
			long reqId = -1; 
			try {
				reqId  = Long.valueOf(requestId);
			} catch (NumberFormatException nfe) {
				throw new KokuServiceException("AuthorizationID is not valid! Username: " + username + " UserId: " + userId + " AuthorizationId: "+ requestId, nfe);
			}
			
			KokuInformationRequestDetail info = null;
			if (Properties.IS_LOORA_PORTAL) {
				KokuEmployeeTietopyyntoServiceHandle handle = new KokuEmployeeTietopyyntoServiceHandle(messageSource);
				info = handle.getRequestDetails(reqId);
				modelWrapper = new ModelWrapperImpl<KokuInformationRequestDetail>(info);
			} else {
				throw new KokuServiceException("PortalMode missing?");
			}
		} catch (KokuServiceException kse) {
			LOG.error("Failed to show infoRequest details. infoRequestId: '"+requestId + 
					"' username: '"+username+"'", kse);
			modelWrapper = new ModelWrapperImpl<KokuInformationRequestDetail>(null, ResponseStatus.FAIL, kse.getErrorcode());
		}
		return modelWrapper;
	}
}
