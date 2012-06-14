package fi.arcusys.koku.web;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import static fi.arcusys.koku.common.util.Constants.*;

/**
 * Handles the main message page
 * @author Jinhua Chen
 * Aug 12, 2011
 */
@Controller("messageController")
@RequestMapping(value = "VIEW")
public class MessageController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);
	
	@ActionMapping(params = "action=toHome")
	public void actionPageView(
			PortletSession session,
			ActionResponse actionResponse) {
		actionResponse.setRenderParameter(ATTR_MY_ACTION, "home");
	}	
	
	/**
	 * Handles the portlet request to show the default page
	 * @param request RenderRequest
	 * @param response RenderResponse
	 * @param modelmap ModelMap
	 * @return message page
	 */
	@RenderMapping
	public String showPageView(ModelMap modelmap) {
		return getView();
	}

	/**
	 * Returns the default home page and  stores page parameters
	 * @param request RenderRequest
	 * @param response RenderResponse
	 * @param modelmap ModelMap
	 * @return home page with page parameters
	 */
	@RenderMapping(params = "myaction=home")
	public String showHome(
			ModelMap modelmap) {
		return getView();
	}

	private String getView() {
		switch(getPortalRole()) {
			case CITIZEN: return VIEW_MESSAGE_CITIZEN;
			case EMPLOYEE: return VIEW_MESSAGE_EMPLOYEE;
			default: return VIEW_ERROR;
		}
	}
	
	/**
	 * Clears page parameters in session
	 * @param request RenderRequest
	 */
	public void clearSession(RenderRequest request) {
		PortletSession ps = request.getPortletSession();
		ps.removeAttribute(ATTR_CURRENT_PAGE, PortletSession.APPLICATION_SCOPE);
		ps.removeAttribute(ATTR_TASK_TYPE, PortletSession.APPLICATION_SCOPE);
		ps.removeAttribute(ATTR_KEYWORD, PortletSession.APPLICATION_SCOPE);
		ps.removeAttribute(ATTR_ORDER_TYPE, PortletSession.APPLICATION_SCOPE);
	}
	
	@ModelAttribute(value = "loginStatus")
	public String model(PortletRequest request) {
		if (checkUserToken(request)) {
			return "VALID";
		} else {
			return "INVALID";
		}
	}

	/**
	 * Check user logged in or not, and put user info to session
	 * @param request RenderRequest
	 * @return true if user is login, otherwise false
	 */
	public boolean checkUserToken(PortletRequest request) {
		String userid = null;

		try {
			userid = request.getRemoteUser();

			if (userid != null) { // user is logged in				
				PortletSession portletSession = request.getPortletSession();
				portletSession.setAttribute(ATTR_USERNAME, userid);
			}
		} catch (Exception e) {
			LOG.error("Exception when getting user id");
		}

		return (userid != null);
	}

}
