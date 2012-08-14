package fi.koku.taskmanager.controller;

import static fi.arcusys.koku.common.util.Constants.ATTR_TOKEN;
import static fi.arcusys.koku.common.util.Constants.ATTR_USERNAME;
import static fi.arcusys.koku.common.util.Constants.JSON_EDITABLE;
import static fi.arcusys.koku.common.util.Constants.JSON_RESULT;
import static fi.arcusys.koku.common.util.Constants.JSON_TASKS;
import static fi.arcusys.koku.common.util.Constants.JSON_TASK_STATE;
import static fi.arcusys.koku.common.util.Constants.JSON_TOKEN_STATUS;
import static fi.arcusys.koku.common.util.Constants.JSON_TOTAL_ITEMS;
import static fi.arcusys.koku.common.util.Constants.JSON_TOTAL_PAGES;
import static fi.arcusys.koku.common.util.Constants.JSON_WS_MESSAGE;
import static fi.arcusys.koku.common.util.Constants.PREF_EDITABLE;
import static fi.arcusys.koku.common.util.Constants.PREF_EXCLUDE_FILTER;
import static fi.arcusys.koku.common.util.Constants.PREF_TASK_FILTER;
import static fi.arcusys.koku.common.util.Constants.RESPONSE;
import static fi.arcusys.koku.common.util.Constants.RESPONSE_FAIL;
import static fi.arcusys.koku.common.util.Constants.RESPONSE_OK;
import static fi.arcusys.koku.common.util.Constants.TOKEN_STATUS_INVALID;
import static fi.arcusys.koku.common.util.Constants.TOKEN_STATUS_VALID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.xml.stream.XMLStreamException;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import fi.arcusys.koku.common.exceptions.IntalioException;
import fi.arcusys.koku.common.proxy.IllegalOperationCall;
import fi.arcusys.koku.common.proxy.XmlProxy;
import fi.arcusys.koku.common.services.intalio.Task;
import fi.arcusys.koku.common.services.intalio.TaskHandle;
import fi.arcusys.koku.common.util.TaskUtil;
import fi.koku.portlet.filter.userinfo.UserInfo;

/**
 * Handles ajax request from web and returns the data with json string
 *
 * @author Jinhua Chen
 * May 11, 2011
 */
@Controller("ajaxController")
@RequestMapping(value = "VIEW")
public class AjaxController {

	private static final Logger LOG = LoggerFactory.getLogger(AjaxController.class);

	/**
	 * Shows ajax for retrieving intalio tasks
	 *
	 * @param page page id
	 * @param taskTypeStr intalio task type
	 * @param keyword keyword for searching/filtering
	 * @param orderType order type of task
	 * @param modelmap
	 * @param request
	 * @param response
	 * @return ajax view with intalio tasks and related information in json format
	 */
	@ResourceMapping(value = "getTask")
	public String showAjax(
			@RequestParam(value = "page") int page,
			@RequestParam(value = "taskType") String taskTypeStr,
			@RequestParam(value = "keyword") String keyword,
			@RequestParam(value = "orderType") String orderType,
			ModelMap modelmap,
			PortletRequest request,
			PortletResponse response) {

		final long start = System.nanoTime();
		final PortletSession portletSession = request.getPortletSession();
		final String token = (String) portletSession.getAttribute(ATTR_TOKEN);
		final String username = (String) portletSession.getAttribute(ATTR_USERNAME);
		final PortletPreferences pref = request.getPreferences();

		final int taskType = getTaskType(taskTypeStr);
		final String taskFilter = pref.getValue(PREF_TASK_FILTER, "");
		final String filteredKeyword = taskFilter + '%' + ((taskFilter.equals(keyword)) ? "" : keyword);
		final List<String> excludeFilter = Arrays.asList(pref.getValue(PREF_EXCLUDE_FILTER, "").split(","));
		JSONObject jsonModel = getJsonModel(taskType, page, filteredKeyword, excludeFilter, orderType, token, username);

		Boolean editableForm = Boolean.valueOf(pref.getValue(PREF_EDITABLE, Boolean.FALSE.toString()));
		jsonModel.put(JSON_EDITABLE, editableForm.toString());
		modelmap.addAttribute(RESPONSE, jsonModel);
		LOG.debug("TaskMgr ajax call took: "+ ((System.nanoTime() - start)/1000/1000)+"ms");
		return AjaxViewResolver.AJAX_PREFIX;
	}

	/**
	 * Gets the task state
	 *
	 * @param taskId intalio task id
	 * @param modelmap
	 * @param request
	 * @param response
	 * @return ajax view with task state in json format
	 */
	@ResourceMapping(value = "getState")
	public String getTaskState (
			@RequestParam(value = "taskId") String taskId,
			ModelMap modelmap,
			PortletRequest request,
			PortletResponse response) {

		final PortletSession portletSession = request.getPortletSession();
		final String token = (String) portletSession.getAttribute(ATTR_TOKEN);
		final String username = (String) portletSession.getAttribute(ATTR_USERNAME);

		JSONObject jsonModel = new JSONObject();

		if (token == null) {
			jsonModel.put(JSON_TOKEN_STATUS, TOKEN_STATUS_INVALID);
			LOG.info("Intalio token is invalid!");
		} else {
			TaskHandle taskhandle = new TaskHandle(token, username);
			String taskState;
			try {
				taskState = taskhandle.getTaskStatus(taskId);
			} catch (IntalioException e) {
				LOG.error("Retrieving taskState failed! ", e);
				taskState = null;
			}
			jsonModel.put(JSON_TASK_STATE, taskState);
		}
		modelmap.addAttribute(RESPONSE, jsonModel);
		return AjaxViewResolver.AJAX_PREFIX;
	}

	/**
	 * Processes task query and gets task list
	 *
	 * @param taskType task type
	 * @param page page id
	 * @param keyword keyword for searching/filtering
	 * @param orderType order type of tasks
	 * @param token user participant token
	 * @param username user name
	 * @return task information in Json format
	 */
	private JSONObject getJsonModel(
			int taskType,
			int page,
			String keyword,
			List<String> excludeFilter,
			String orderType,
			String token,
			String username) {
		JSONObject jsonModel = new JSONObject();

		if (token == null) {
			jsonModel.put(JSON_TOKEN_STATUS, TOKEN_STATUS_INVALID);
			LOG.info("Intalio token is invalid!");
		} else {
			TaskHandle taskhandle = new TaskHandle(token, username);
			int numPerPage = TaskUtil.PAGE_NUMBER;
			int totalTasksNum = 0;
			int totalPages = 0;
			List<Task> tasks = null;
			try {
				String first = String.valueOf((page-1)*numPerPage);
				String max =  String.valueOf(numPerPage);
				tasks = taskhandle.getTasksByParams(taskType, keyword, excludeFilter, orderType, first, max);
				totalTasksNum = taskhandle.getTotalTasksNumber(taskType, keyword);
				totalPages = (totalTasksNum == 0) ? 1:(int) Math.ceil((double)totalTasksNum/numPerPage);
			} catch (IntalioException e) {
				LOG.error("Retrieving tasks failed!", e);
				tasks = Collections.emptyList();
			}
			jsonModel.put(JSON_TOTAL_ITEMS, totalTasksNum);
			jsonModel.put(JSON_TOTAL_PAGES, totalPages);
			jsonModel.put(JSON_TASKS, tasks);
			jsonModel.put(JSON_TOKEN_STATUS, TOKEN_STATUS_VALID);
		}
		return jsonModel;
	}


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
		modelmap.addAttribute(JSON_RESULT, RESPONSE_FAIL);

		try {
			XmlProxy proxy = new XmlProxy(service, message, user);
			String result = proxy.send(request);
			modelmap.addAttribute(JSON_WS_MESSAGE, result);
			modelmap.addAttribute(JSON_RESULT, RESPONSE_OK);
		} catch (IllegalOperationCall ioc) {
			LOG.error("Illegal operation call. User '" + username + "' tried to call restricted method that he/she doesn't have sufficient permission. ", ioc);
		} catch (XMLStreamException xse) {
			LOG.error("Unexpected XML-parsing error. User '" + username + "'", xse);
		} catch (Exception e) {
			LOG.error("Couldn't send given message. Parsing error propably. Username: '"+username+"'", e);
		}
		return AjaxViewResolver.AJAX_PREFIX;
	}


	/**
	 * Converts task type string to integer
	 *
	 * @param taskTypeStr task type string
	 * @return task type
	 */
	private int getTaskType(String taskTypeStr) {

		if (taskTypeStr.equals("task")) {
			return TaskUtil.TASK;
		} else if(taskTypeStr.equals("notification")) {
			return TaskUtil.NOTIFICATION;
		} else if(taskTypeStr.equals("process")) {
			return TaskUtil.PROCESS;
		} else {
			return TaskUtil.PROCESS;
		}
	}
}
