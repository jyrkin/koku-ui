package fi.arcusys.koku.common.services.intalio;

import static fi.arcusys.koku.common.util.Constants.DATE_FORMAT;
import static fi.arcusys.koku.common.util.Constants.TIME_ZONE;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fi.arcusys.intalio.tms.TaskData;
import fi.arcusys.intalio.tms.TaskMetadata;
import fi.arcusys.koku.common.exceptions.IntalioAuthException;
import fi.arcusys.koku.common.exceptions.IntalioException;
import fi.arcusys.koku.common.util.Properties;
import fi.arcusys.koku.common.util.TaskUtil;

/**
 * Handles the intalio task processing including querying tasks, formatting task
 * to be presented to web
 *
 * @author Jinhua Chen May 9, 2011
 */

public class TaskHandle {

	// TODO: We probably need some sort filter here?
	public static final String TASKMGR_REQUESTS_FILTER = "";
	private static final String LOCAL_AJAXFORMS_WEB_APP_URL_PART = "/palvelut-portlet/ajaxforms/";
	private static final String ADDRESS_REGEX = "http://.+/gi/";

	private static final String START_TASK_SEARCH = "0";
	private static final String MAX_TASKS = "50";

	private static final String UNKNOWN = "?";

	private final TaskManagementService taskMngServ = new TaskManagementService();

	private String message;
	private final String participantToken;
	private String username;

	/**
	 * Constructor and initialization
	 */
	public TaskHandle() {
		this.message = "";
		this.participantToken = null;
	}

	/**
	 * Constructor with intalio participant token and username
	 */
	public TaskHandle(String token, String username) {
		this.participantToken = token;
		this.username = username;
	}

	/**
	 * Gets available tasks by the given parameters and return task list
	 *
	 * @param taskType the intalio task type
	 * @param keyword the keyword for searching/filetering
	 * @param orderType order type of tasks
	 * @param first the beginning index of the tasks
	 * @param max the maximum tasks to be queried
	 * @return available task list
	 * @throws IntalioException
	 */
	public List<Task> getTasksByParams(int taskType, String keyword,
			String orderType, String first, String max) throws IntalioException {
		return getTasksByParams(taskType, keyword, new ArrayList<String>(), orderType, first, max);
	}

	/**
	 * Gets available tasks by the given parameters and return task list
	 *
	 * @param taskType the intalio task type
	 * @param keyword the keyword for searching/filetering
	 * @param orderType order type of tasks
	 * @param first the beginning index of the tasks
	 * @param max the maximum tasks to be queried
	 * @return available task list
	 * @throws IntalioException
	 */
	public List<Task> getTasksByParams(int taskType, String keyword, List<String> exclude,
			String orderType, String first, String max) throws IntalioException {
		final String taskTypeStr = TaskUtil.getTaskType(taskType);
		final String subQuery = new IntalioQueryCreator(taskType, keyword, exclude, orderType).getTaskSubQuery();
		return getTasks(taskTypeStr, subQuery, first, max);
	}

	/**
	 * Gets tasks from task management service
	 *
	 * @param taskType   the intalio task type
	 * @param subQuery the sql string for intalio tasks database
	 * @param first the beginning index of the tasks
	 * @param max the maximum tasks to be queried
	 * @return a list of tasks
	 * @throws IntalioException
	 */
	public List<Task> getTasks(String taskType, String subQuery, String first,
			String max) throws IntalioException {
		List<TaskMetadata> tasklist = taskMngServ.getAvailableTasks(participantToken, taskType, subQuery, first, max);
		return createTasks(tasklist, participantToken);
	}

	public Task getTask(String taskId, String token) throws IntalioException {
    	final fi.arcusys.intalio.tms.Task task = taskMngServ.getTask(taskId, token);
		return createTask(task.getMetadata(), task.getInput());
	}

	public List<Task> getPIPATaskList(String token) throws IntalioException {
		List<TaskMetadata> metadata = taskMngServ.getAvailableTasks(token,
				TaskUtil.PROCESS_TYPE, "", START_TASK_SEARCH, MAX_TASKS);
		return createTasklistByMetadata(metadata, TaskUtil.PROCESS_TYPE);
	}

	public Task getTaskByDescription(String token, String description)
			throws IntalioException {
		Task task = null;
		List<TaskMetadata> metadata = taskMngServ.getAvailableTasks(token,
				TaskUtil.PROCESS_TYPE, "", START_TASK_SEARCH, MAX_TASKS);

		if (!metadata.isEmpty()) {
			for (TaskMetadata data : metadata) {
				if (data.getDescription().equals(description)) {
					task = new Task(data, TaskUtil.PROCESS_TYPE);
					break;
				}
			}
			return task;
		} else {
			return null;
		}
	}

	/**
	 * Gets task status such as 'READY', 'CLAIMED', 'COMPLETED'
	 *
	 * @param taskId intalio task id
	 * @return the intalio task status
	 * @throws IntalioException
	 */
	public String getTaskStatus(String taskId) throws IntalioException {
		return taskMngServ.getTask(taskId, participantToken).getMetadata()
				.getTaskState();
	}

	/**
	 * Creates task model to be shown in portlet from intalio task
	 *
	 * @param tasklist a list of intalio tasks
	 * @return formatted task list to be presented on web
	 * @throws IntalioException
	 */
	private List<Task> createTasks(List<TaskMetadata> tasklist, String token)
			throws IntalioException {
		List<Task> myTasklist = new ArrayList<Task>();
		/*
		 * Unfortunately getTasks WS call doesn't contain Input object which
		 * includes information what we want (like sender name). Only way seems
		 * to be call task details and this will generate more WS calls (default
		 * is 10). =\
		 *
		 * Better ideas?
		 */
		for (TaskMetadata task : tasklist) {
			fi.arcusys.intalio.tms.Task taskDetails = taskMngServ.getTask(
					task.getTaskId(), token);
			myTasklist.add(createTask(taskDetails.getMetadata(),
					taskDetails.getInput()));
		}
		return myTasklist;
	}

	private List<Task> createTasklistByMetadata(
			List<fi.arcusys.intalio.tms.TaskMetadata> tasksMetadata,
			String processTaskType) {
		List<Task> taskList = new ArrayList<Task>(tasksMetadata.size());

		for (fi.arcusys.intalio.tms.TaskMetadata metaData : tasksMetadata) {
			taskList.add(new Task(metaData, processTaskType));
		}
		return taskList;
	}

	private fi.arcusys.koku.common.services.intalio.Task createTask(
			TaskMetadata task, fi.arcusys.intalio.tms.TaskData input) throws IntalioException {
		if (task == null) {
			return null;
		}
		fi.arcusys.koku.common.services.intalio.Task myTask = new fi.arcusys.koku.common.services.intalio.Task();
		myTask.setDescription(task.getDescription());

		if (task.getTaskState() != null) {
			myTask.setState(task.getTaskState().toString());
		} else {
			myTask.setState("");
		}
		myTask.setCreationDate(formatTaskDate(task.getCreationDate()));
		myTask.setLink(createTaskLink(task));
		myTask.setSenderName(getSenderNameFromTaskInput(task, input));
		myTask.setFormUrl(task.getFormUrl());
		return myTask;
	}

	/**
	 * Returns task sender name (if available)
	 *
	 * @param task
	 * @param input
	 * @return sender name
	 */
	private String getSenderNameFromTaskInput(TaskMetadata task, TaskData input) {
		final String descriptionName = task.getDescription();
		if (descriptionName == null || descriptionName.isEmpty()) {
			return UNKNOWN;
		}
		if (descriptionName.startsWith(Properties.RECEIVED_REQUESTS_FILTER)) {
			// Uusi pyyntö
			return getSenderName(input, "User_SenderDisplay");
		} else if (descriptionName
				.contains(Properties.RECEIVED_INFO_REQUESTS_FILTER)) {
			// Uusi tietopyyntö
			return getSenderName(input, "Perustiedot_Lahettaja");
		} else if (descriptionName
				.endsWith(Properties.RECEIVED_WARRANTS_FILTER)) {
			// Vastaanotetut valtakirjat
			return getSenderName(input, "Tiedot_LahettajaDisplay");
		} else {
			return getSenderName(input, "SenderFullname");
		}
	}

	private String getSenderName(final fi.arcusys.intalio.tms.TaskData input,
			final String nodename) {
		if (input == null || input.getAny() == null || input.getAny().isEmpty()) {
			return UNKNOWN;
		}
		final Object object = input.getAny().get(0);
		if (!(object instanceof Element)) {
			return UNKNOWN;
		}
		final Element element = ((Element) object);
		final NodeList senderNameList = element.getElementsByTagName(nodename);
		if (senderNameList == null || senderNameList.getLength() == 0) {
			return UNKNOWN;
		}
		final Node senderNameNode = senderNameList.item(0);
		if (senderNameNode.getFirstChild() == null) {
			return UNKNOWN;
		}
		return senderNameNode.getFirstChild().getNodeValue();
	}

	/**
	 * Formats the task date with given format and Helsinki timezone
	 *
	 * @param xmlGregorianCalendar
	 * @return formatted date string
	 */
	public String formatTaskDate(XMLGregorianCalendar xmlGregorianCalendar) {
		Calendar cal = xmlGregorianCalendar.toGregorianCalendar();
		SimpleDateFormat dataformat = new SimpleDateFormat(DATE_FORMAT);
		dataformat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
		return dataformat.format(cal.getTime());
	}

	/**
	 * Creates form operation link of task
	 *
	 * @param task intalio task object
	 * @return intalio task form string
	 * @throws IntalioException
	 */
	public String createTaskLink(TaskMetadata task) throws IntalioException {
		String link = "";
		String taskType = "";
		Object[] params = null;
		String type = task.getTaskType().toString();

		if (type.equals("ACTIVITY")) { // tasks
			taskType = TaskUtil.TASK_TYPE;
		} else if (type.equals("NOTIFICATION")) { // notifications
			taskType = TaskUtil.NOTIFICATION_TYPE;
		} else if (type.equals("INIT")) { // processes
			taskType = TaskUtil.PROCESS_TYPE;
		} else {
			taskType = TaskUtil.TASK_TYPE;
		}
		String url = task.getFormUrl().toString();
		url = url.replaceFirst(ADDRESS_REGEX, LOCAL_AJAXFORMS_WEB_APP_URL_PART);

		try {
			params = new Object[] { url, task.getTaskId(), taskType,
					URLEncoder.encode(url, "UTF-8"), participantToken,
					URLEncoder.encode(username, "UTF-8"), false };
		} catch (UnsupportedEncodingException e) {
			throw new IntalioException("Unsupported Encoding Exception. Link can't be created because it's not using UTF-8 encoding", e);
		}
		link = MessageFormat
				.format("{0}?id={1}&type={2}&url={3}&token={4}&user={5}&claimTaskOnOpen={6}",
						params);
		return link;
	}

	/**
	 * Gets total tasks number
	 *
	 * @param taskType the intalio task type
	 * @param subQuery the sql string for intalio tasks database
	 * @return total number of total tasks
	 * @throws IntalioException
	 */
	public int getTotalTasksNumber(int taskType, String keyword)
			throws IntalioException {
		int totalNum = 0;
		String subQuery;
		String totalNumStr;
		String taskTypeStr;
		taskTypeStr = TaskUtil.getTaskType(taskType);
		subQuery = new IntalioQueryCreator(taskType, keyword, null, null).getTaskSubQuery();
		totalNumStr = taskMngServ.getTotalTasksNumber(participantToken,
				taskTypeStr, subQuery);
		totalNum = Integer.parseInt(totalNumStr);
		return totalNum;
	}

	public int getTasksTotalNumber(final String keywordFilter)
			throws IntalioException {
		final String filter = (keywordFilter != null) ? keywordFilter : "";
		return Integer.valueOf(taskMngServ.getTotalTasksNumber(
				participantToken, TaskUtil.TASK_TYPE,
				new IntalioQueryCreator(TaskUtil.TASK, filter, null, null).getTaskSubQuery()));
	}

	public int getRequestsTasksTotalNumber() throws IntalioException {
		return getTasksTotalNumber("");
	}


	/**
	 * Gets token authenticated by username and password
	 *
	 * @param username username of intalio user
	 * @param password password of intalio user
	 * @return intalio participant token
	 */
	public String getTokenByUser(String username, String password)
			throws IntalioAuthException {
		String token = null;
		token = taskMngServ.getParticipantToken(username, password);
		return token;
	}

	/**
	 * Gets participant token
	 *
	 * @return intalio participant token
	 */
	public String getToken() {
		return participantToken;
	}

	/**
	 * Shows handling message e.g. error message
	 *
	 * @return message log
	 */
	public String getMessage() {
		return message;
	}

}
