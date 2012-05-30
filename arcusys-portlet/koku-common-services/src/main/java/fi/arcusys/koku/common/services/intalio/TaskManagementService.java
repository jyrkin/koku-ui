package fi.arcusys.koku.common.services.intalio;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import fi.arcusys.intalio.tms.CountAvailableTasksRequest;
import fi.arcusys.intalio.tms.GetAvailableTasksRequest;
import fi.arcusys.intalio.tms.GetAvailableTasksResponse;
import fi.arcusys.intalio.tms.GetTaskListRequest;
import fi.arcusys.intalio.tms.GetTaskListResponse;
import fi.arcusys.intalio.tms.GetTaskRequest;
import fi.arcusys.intalio.tms.GetTaskResponse;
import fi.arcusys.intalio.tms.InvalidInputMessageFault_Exception;
import fi.arcusys.intalio.tms.InvalidParticipantTokenFault_Exception;
import fi.arcusys.intalio.tms.Task;
import fi.arcusys.intalio.tms.TaskManagementServices;
import fi.arcusys.intalio.tms.TaskManagementServicesPortType;
import fi.arcusys.intalio.tms.TaskMetadata;
import fi.arcusys.intalio.token.TokenService;
import fi.arcusys.intalio.token.TokenServicePortType;
import fi.arcusys.koku.common.exceptions.IntalioAuthException;
import fi.arcusys.koku.common.util.Properties;

/**
 * Handles tasks processing via intalio web services
 * 
 * TODO: Should we throw KokuServiceException if something fails like we do in other services?
 * 
 * @author Jinhua Chen May 9, 2011
 */
public class TaskManagementService {

	private static final Logger LOG = Logger
			.getLogger(TaskManagementService.class);

	private final TokenServicePortType tokenService;
	private final TaskManagementServicesPortType taskMgrService;

	/**
	 * Constructor
	 */
	public TaskManagementService() {
		final TokenService ts = new TokenService();
		final TaskManagementServices tms = new TaskManagementServices();

		tokenService = ts.getService();
		taskMgrService = tms.getTaskManagementServicesSOAP();
		((BindingProvider) tokenService).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Properties.INTALIO_TOKEN_SERVICE);
		((BindingProvider) taskMgrService).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				Properties.INTALIO_TASKMGR_SERVICE);
	}

	/**
	 * Gets participant token from intalio bpms server. The server authenticates
	 * user by username and password, then generates token.
	 * 
	 * @param username
	 *            username of intalio user
	 * @param password
	 *            password of intalio user
	 * @return Intalio participant token
	 */
	public String getParticipantToken(String username, String password)
			throws IntalioAuthException {
		String participantToken = null;
		try {
			participantToken = tokenService
					.authenticateUser(username, password);
		} catch (Exception e) {
			throw new IntalioAuthException("Username: '"+username+"'Trying to get intalio token failed: " + e.getMessage(), e);
		}
		return participantToken;
	}

	/**
	 * Gets Tasks from WS /axis2/services/TaskManagementServices using
	 * getAvailableTasks operation.
	 * 
	 * @param participantToken
	 *            intalio participant token
	 * @param taskType
	 *            the intalio task type: "PATask", "PIPATask", "Notification"
	 * @param subQuery
	 *            the sql string for intalio tasks database
	 * @param first
	 *            the beginning index of the tasks
	 * @param max
	 *            the maximum tasks to be queried
	 * @return a list of intalio tasks
	 */
	public List<TaskMetadata> getAvailableTasks(String participantToken,
			String taskType, String subQuery, String first, String max)  {

		List<TaskMetadata> taskList = new ArrayList<TaskMetadata>();
		try {
			GetAvailableTasksRequest getAvailTasksReq = new GetAvailableTasksRequest();
			getAvailTasksReq.setParticipantToken(participantToken);
			getAvailTasksReq.setTaskType(taskType);
			getAvailTasksReq.setSubQuery(subQuery);
			getAvailTasksReq.setFirst(first);
			getAvailTasksReq.setMax(max);
			GetAvailableTasksResponse availTasksRes;
			availTasksRes = taskMgrService.getAvailableTasks(getAvailTasksReq);
			taskList = availTasksRes.getTask();
		} catch (InvalidParticipantTokenFault_Exception e) {
			LOG.error("getAvailableTasks - TaskType: '"+taskType+"' Token: '"+participantToken+"' subQuery: '"+subQuery+"' InvalidParticipantTokenFault_Exception: "
					+ e.getMessage());
		} catch (InvalidInputMessageFault_Exception e2) {
			LOG.error("getAvailableTasks - TaskType: '"+taskType+"' Token: '"+participantToken+"' subQuery: '"+subQuery+"' InvalidInputMessageFault_Exception: "
					+ e2.getMessage());
		} catch (Exception e1) {
			LOG.error("getAvailableTasks - TaskType: '"+taskType+"' Token: '"+participantToken+"' subQuery: '"+subQuery+"' Intalio exception: " + e1.getMessage(), e1);
		}
		return taskList;
	}

	/**
	 * Gets the total number of tasks
	 * 
	 * @param participantToken
	 *            intalio participant token
	 * @param taskType
	 *            the intalio task type: "PATask", "PIPATask", "Notification"
	 * @param subQuery
	 *            the sql string for intalio tasks database
	 * @return total number, returns '0' if no results found
	 */
	public String getTotalTasksNumber(String participantToken, String taskType,
			String subQuery) {
		String totalNum = "0";
		try {
			CountAvailableTasksRequest countAvailTasksReq = new CountAvailableTasksRequest();
			countAvailTasksReq.setParticipantToken(participantToken);
			countAvailTasksReq.setTaskType(taskType);
			countAvailTasksReq.setSubQuery(subQuery);
			totalNum = taskMgrService.countAvailableTasks(countAvailTasksReq);
		} catch (InvalidParticipantTokenFault_Exception e) {
			LOG.error("getTotalTasksNumber - TaskType: '"+taskType+"' Token: '"+participantToken+"' subQuery: '"+subQuery+"'  InvalidParticipantTokenFault_Exception: ", e);
		} catch (InvalidInputMessageFault_Exception e2) {
			LOG.error("getTotalTasksNumber - TaskType: '"+taskType+"' Token: '"+participantToken+"' subQuery: '"+subQuery+"' InvalidInputMessageFault_Exception: ",	e2);
		} catch (Exception e1) {
			LOG.error("getTotalTasksNumber -TaskType: '"+taskType+"' Token: '"+participantToken+"' subQuery: '"+subQuery+"'  Intalio exception: ", e1);
		}
		return totalNum;
	}

	/**
	 * Gets a Intalio task with task id and participant token
	 * 
	 * @param taskId
	 *            intalio task id
	 * @param participantToken
	 *            intalio participant token
	 * @return Intalio task
	 */
	public Task getTask(String taskId, String participantToken) {
		Task task = null;
		try {
			GetTaskRequest req = new GetTaskRequest();
			req.setTaskId(taskId);
			req.setParticipantToken(participantToken);
			GetTaskResponse res = taskMgrService.getTask(req);
			task = res.getTask();
		} catch (InvalidParticipantTokenFault_Exception e) {
			LOG.error("getTask - Token: '"+participantToken+"' InvalidParticipantTokenFault_Exception: "
					+ e.getMessage());
		} catch (InvalidInputMessageFault_Exception e2) {
			LOG.error("getTask - Token: '"+participantToken+"' InvalidInputMessageFault_Exception: "
					+ e2.getMessage());
		} catch (Exception e1) {
			LOG.error("getTask - Token: '"+participantToken+"' Intalio exception: " + e1.getMessage());
		}
		return task;
	}

	public List<TaskMetadata> getTaskList(String token) {
		List<TaskMetadata> taskList = new ArrayList<TaskMetadata>();
		try {
			GetTaskListRequest req = new GetTaskListRequest();
			req.setParticipantToken(token);
			GetTaskListResponse res = taskMgrService.getTaskList(req);
			taskList = res.getTask();
		} catch (InvalidParticipantTokenFault_Exception e) {
			LOG.error("getTaskList - Token: '"+token+"' InvalidParticipantTokenFault_Exception"
					+ e.getMessage());
		} catch (InvalidInputMessageFault_Exception e) {
			LOG.error("getTaskList - Token: '"+token+"' InvalidInputMessageFault_Exception"
					+ e.getMessage());
		}

		return taskList;
	}

	
}
