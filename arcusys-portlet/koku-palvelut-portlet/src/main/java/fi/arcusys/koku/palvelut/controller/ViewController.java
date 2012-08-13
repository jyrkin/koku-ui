package fi.arcusys.koku.palvelut.controller;

import static fi.arcusys.koku.common.util.Constants.ATTR_PREFERENCES;
import static fi.arcusys.koku.common.util.Constants.JSON_RESULT;
import static fi.arcusys.koku.common.util.Constants.JSON_WS_MESSAGE;
import static fi.arcusys.koku.common.util.Constants.PREF_SHOW_ONLY_FORM_BY_DESCRIPTION;
import static fi.arcusys.koku.common.util.Constants.PREF_SHOW_ONLY_FORM_BY_ID;
import static fi.arcusys.koku.common.util.Constants.PREF_SHOW_TASKS_BY_ID;
import static fi.arcusys.koku.common.util.Constants.RESPONSE_FAIL;
import static fi.arcusys.koku.common.util.Constants.RESPONSE_OK;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.xml.stream.XMLStreamException;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import fi.arcusys.koku.common.proxy.IllegalOperationCall;
import fi.arcusys.koku.common.proxy.XmlProxy;
import fi.arcusys.koku.common.services.intalio.Task;
import fi.arcusys.koku.common.services.intalio.TaskHandle;
import fi.arcusys.koku.palvelut.util.AjaxViewResolver;
import fi.arcusys.koku.palvelut.util.TokenResolver;
import fi.koku.portlet.filter.userinfo.UserInfo;


@Controller("viewController")
@RequestMapping(value = "VIEW")
public class ViewController extends FormHolderController {

	private static final Logger LOG = LoggerFactory.getLogger(ViewController.class);

	public static final String FORM_VIEW_ACTION 						= "formview";
	public static final String VIEW_ACTION 								= "view";
	public static final String VIEW_CURRENT_FOLDER 						= "folderId";
	public static final String ADMIN_ACTION 							= "action";
	public static final String ADMIN_REMOVE_CATEGORY_ACTION 			= "removeCategory";
	public static final String ADMIN_REMOVE_FORM_ACTION 				= "removeForm";
	public static final String ROOT_CATEGORY_LIST_MODEL_NAME 			= "rootCategories";


	@ResourceMapping(value = "serviceNames")
	public String servicesAjax(ModelMap modelmap, PortletRequest request, PortletResponse response) {
		JSONObject obj = new JSONObject();
		modelmap.addAttribute("endpoints" , XmlProxy.getServiceNames());
		modelmap.addAttribute(JSON_RESULT, RESPONSE_OK);
		return AjaxViewResolver.AJAX_PREFIX;
	}

	@ResourceMapping(value = "sendWsRequest")
	public String servicesAjaxSend(
			@RequestParam(value = "service") String service,
			@RequestParam(value = "message") String message,
			ModelMap modelmap, PortletRequest request, PortletResponse response) {

		LOG.debug("Service: '"+service+"' Messsage: '"+message+"'");

		final String username = request.getUserPrincipal().getName();
		final UserInfo user = (UserInfo) request.getPortletSession().getAttribute(UserInfo.KEY_USER_INFO);
		final JSONObject obj = new JSONObject();
		String result = null;
		modelmap.addAttribute(JSON_RESULT, RESPONSE_FAIL);

		try {
			XmlProxy proxy = new XmlProxy(service, message, user);
			result = proxy.send(request);
			modelmap.addAttribute(JSON_RESULT, RESPONSE_OK);
		} catch (IllegalOperationCall ioc) {
			LOG.error("Illegal operation call. User '" + username + "' tried to call restricted method that he/she doesn't have sufficient permission. ", ioc);
		} catch (XMLStreamException xse) {
			LOG.error("Unexpected XML-parsing error. User '" + username + "'", xse);
		} catch (Exception e) {
			LOG.error("Couldn't send given message. Parsing error propably. Username: '"+username+"'", e);
		}

		if (result == null || result.isEmpty()) {
			modelmap.addAttribute(JSON_WS_MESSAGE, "");
		} else {
			modelmap.addAttribute(JSON_WS_MESSAGE, result);
		}
		return AjaxViewResolver.AJAX_PREFIX;
	}


	/*
	 * @see org.springframework.web.portlet.mvc.AbstractController#handleRenderRequestInternal(javax.portlet.RenderRequest,
	 *      javax.portlet.RenderResponse)
	 */
	@RenderMapping
	public ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderRequest response) throws Exception {

		final String username = request.getRemoteUser();
		PortletSession portletSession = request.getPortletSession();

		if (username == null) {
			LOG.info("Can't show Intalio form. User not logged in");
			return getFailureView(request);
		}

		final PortletPreferences prefs = request.getPreferences();
		final Boolean showFormById = Boolean.valueOf(prefs.getValue(PREF_SHOW_TASKS_BY_ID, null));
		final String formId = prefs.getValue(PREF_SHOW_ONLY_FORM_BY_ID, null);
		final String formDescription = prefs.getValue(PREF_SHOW_ONLY_FORM_BY_DESCRIPTION, null);
		LOG.debug("showFormById " + showFormById);

		ModelAndView mav = new ModelAndView(FORM_VIEW_ACTION, "model", null);
		mav.addObject(ATTR_PREFERENCES, request.getPreferences());
		Task t = null;
		TokenResolver tokenUtil = new TokenResolver();
		TaskHandle taskHandle = new TaskHandle();
		try {
			if (showFormById) {
				LOG.debug("Loading taskID: '" +formId+"'");
				t = taskHandle.getTask(formId, tokenUtil.getAuthenticationToken(request));
			} else {
				t = taskHandle.getTaskByDescription(tokenUtil.getAuthenticationToken(request), formDescription);
				// Fallback. Try to get form by Id
				if (t == null) {
					LOG.error("Fallback! Couldn't find task by description name. Trying to get form by Id. Description: '" + formDescription + "'");
					t = taskHandle.getTask(formId, tokenUtil.getAuthenticationToken(request));
				}
				LOG.debug("Loading taskDescription: '" +t.getDescription()+ "' Id: '"+t.getId()+"'");
			}
		} catch (Exception e) {
			if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
				LOG.error("Failure while trying to get Task. Use FormId: '"+showFormById+"' Description: '"+formDescription+"' Username: '"+request.getUserPrincipal().getName()+"'. Some hints to fix problem: " +
						"\n\t1. Logged in proper user? (this portlet doesn't work correctly with admin/nonlogged users. " +
						"\n\t2. Task might be updated. Reselect form in 'edit'-mode. " +
						"\n\t3. Check that connection to Intalio server is up. ", e.getMessage());
			}
			return getFailureView(request);
		}

		if (t == null) {
			LOG.error("Coulnd't find task by descrption or formId! Description: '"+formDescription+"' FormId: '"+formId+"'");
		}

		mav.addObject("formholder", getFormHolderFromTask(request, t.getDescription()));
		return mav;
	}

	private ModelAndView getFailureView(RenderRequest request) {
		ModelAndView failureMav = new ModelAndView("failureView", "model", null);
		failureMav.addObject("prefs", request.getPreferences());
		return failureMav;
	}


	@ActionMapping(value="NORMAL")
	public void handleActionRequestInternal( ActionRequest request, ActionResponse response ) throws Exception {
	}

	@ResourceMapping(value="NORMAL")
	public void handleResourceRequestInteral(ResourceRequest request, ResourceResponse response ) throws Exception {

	}
}
