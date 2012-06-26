package fi.arcusys.koku.web;

import static fi.arcusys.koku.common.util.Constants.ATTR_MESSAGE_ID;
import static fi.arcusys.koku.common.util.Constants.ATTR_MY_ACTION;
import static fi.arcusys.koku.common.util.Constants.MY_ACTION_SHOW_MESSAGE;
import static fi.arcusys.koku.common.util.Constants.VIEW_SHOW_MESSAGE;

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
import fi.arcusys.koku.common.services.messages.MessageHandle;
import fi.arcusys.koku.common.services.messages.model.KokuMessage;
import fi.arcusys.koku.common.util.Constants;
import fi.arcusys.koku.web.util.ModelWrapper;
import fi.arcusys.koku.web.util.impl.ModelWrapperImpl;

/**
 * Shows message details page and store the current query information on the jsp page
 * @author Jinhua Chen
 * Jun 22, 2011
 */
@Controller("singleMessageController")
@RequestMapping(value = "VIEW")
public class ShowMessageController {
	private static final Logger LOG = LoggerFactory.getLogger(ShowMessageController.class);


	@Resource
	private ResourceBundleMessageSource messageSource;

	@ActionMapping(params = "action=toMessage")
	public void actionPageView(
			PortletSession session,
			@ModelAttribute(value = "message") ModelWrapper<KokuMessage> message,
			@RequestParam(value = "messageId") String messageId,
			ActionResponse response) {

		response.setRenderParameter(ATTR_MY_ACTION, MY_ACTION_SHOW_MESSAGE);
		response.setRenderParameter(ATTR_MESSAGE_ID, messageId);
	}

	/**
	 * Shows message page
	 * @param response RenderResponse
	 * @return message page
	 */
	@RenderMapping(params = "myaction=showMessage")
	public String showPageView(RenderResponse response) {
		return VIEW_SHOW_MESSAGE;
	}

	/**
	 * Creates data model integrated into the page and stores the page
	 * @param messageId message id
	 * @param currentPage current page id
	 * @param taskType task type requested
	 * @param keyword page parameter keyword
	 * @param orderType page parameter order type
	 * @param request RenderRequest
	 * @return
	 * @return message data model
	 */
	@ModelAttribute(value = "message")
	public ModelWrapper<KokuMessage> model (
			@RequestParam String messageId,
			PortletSession portletSession)	{
		ModelWrapper<KokuMessage> modelWrapper = null;
		KokuMessage message = null;
		try {
			MessageHandle msghandle = new MessageHandle(messageSource);
			message = msghandle.getMessageById(messageId);
			modelWrapper = new ModelWrapperImpl<KokuMessage>(message, ResponseStatus.OK);
		} catch (KokuServiceException kse) {
			LOG.error("Failed to show message details. messageId: '"+messageId +
					 "' username: '" + (String)portletSession.getAttribute(Constants.ATTR_USERNAME) + "'", kse);
			modelWrapper = new ModelWrapperImpl<KokuMessage>(null, ResponseStatus.FAIL, kse.getErrorcode());
		}
		return modelWrapper;
	}
}
