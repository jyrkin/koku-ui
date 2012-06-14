package fi.koku.taskmanager.controller;

import static fi.arcusys.koku.common.util.Constants.ATTR_MY_ACTION;
import static fi.arcusys.koku.common.util.Constants.ATTR_TASK_LINK;
import static fi.arcusys.koku.common.util.Constants.MY_ACTION_TASKFORM;
import static fi.arcusys.koku.common.util.Constants.VIEW_TASK_FORM;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * Shows task form page and store the current query information on the jsp page
 *
 * @author Jinhua Chen
 * May 11, 2011
 */
@Controller("formController")
@RequestMapping(value = "VIEW")
public class FormController {

	private static final Logger LOG = LoggerFactory.getLogger(FormController.class);

	@ActionMapping(params = "action=toTaskform")
	public void actionPageView(
			PortletSession session,
			@RequestParam(value = "tasklink", required=false) String link,
			ActionResponse actionResponse) {
		actionResponse.setRenderParameter(ATTR_MY_ACTION, MY_ACTION_TASKFORM);
		try {
			/* JBoss seems to decoding reqParam partially. Re-encode again..  */
			actionResponse.setRenderParameter(ATTR_TASK_LINK, URLEncoder.encode(link, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOG.error("Failed to encode url. Bug perhaps?", e);
			actionResponse.setRenderParameter(ATTR_TASK_LINK, link);
		}
	}

	/**
	 * Shows a Intalio form page and set the page variable
	 *
	 * @param response
	 * @return Intalio form page
	 */
	@RenderMapping(params = "myaction=taskform")
	public String showForm(RenderResponse response) {
		return VIEW_TASK_FORM;
	}
}
