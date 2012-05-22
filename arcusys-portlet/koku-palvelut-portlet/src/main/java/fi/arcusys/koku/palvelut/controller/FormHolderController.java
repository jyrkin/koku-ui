package fi.arcusys.koku.palvelut.controller;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.intalio.tempo.workflow.task.Task;
import fi.arcusys.koku.intalio.Task;

import fi.arcusys.koku.palvelut.model.client.FormHolder;
import fi.arcusys.koku.palvelut.util.TaskUtil;
import fi.arcusys.koku.palvelut.util.TokenResolver;
import fi.arcusys.koku.palvelut.util.URLUtil;
import fi.arcusys.koku.intalio.TaskHandle;

/**
 * @author Dmitry Kudinov (dmitry.kudinov@arcusys.fi)
 * Jul 21, 2011
 */
public abstract class FormHolderController
{
//	@Autowired(required = false)
//	private PortletContext portletContext;
	
	private static final Log LOG = LogFactory.getLog(FormHolderController.class);

	public FormHolderController() {
		super();
	}

	protected List<FormHolder> getFormHoldersFromTasks(PortletRequest request) {
		TokenResolver tokenResolver = new TokenResolver();
		TaskHandle taskHandle = new TaskHandle();
		String token = tokenResolver.getAuthenticationToken(request);
//		List<Task> taskList = TaskUtil.getPIPATaskList(token);
		List<Task> taskList = taskHandle.getPIPATaskList(token);
//		taskList.get(0).get
		List<FormHolder> formList = new ArrayList<FormHolder>();
		for (Task task: taskList) {
				String taskFormURL = getFormUrlByTask(request, token, task);
				formList.add(new FormHolder(task.getDescription(), taskFormURL));
		}
		return formList;
	}

	protected FormHolder getFormHolderFromTask(PortletRequest request, String description) {
		TokenResolver tokenResolver = new TokenResolver();
		TaskHandle taskHandle = new TaskHandle();
		String token = tokenResolver.getAuthenticationToken(request);
	
//		List<Task> taskList = TaskUtil.getPIPATaskList(token);
		List<Task> taskList = taskHandle.getPIPATaskList(token);
		for (Task task : taskList) {
			if (task.getDescription().equals(description)) {
				String taskFormURL = getFormUrlByTask(request, token, task);
				return new FormHolder(description, taskFormURL);
			}
		}
		LOG.error("Didn't find any form! Username: '"+request.getUserPrincipal().getName()+"'");	
		return null;
	}
	
	private String getFormUrlByTask(PortletRequest request, String token, Task task) {
		return URLUtil.getFormURLForTask(task, token, request);
	}

}