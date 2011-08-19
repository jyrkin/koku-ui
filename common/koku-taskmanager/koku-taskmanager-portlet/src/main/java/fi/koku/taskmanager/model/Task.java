package fi.koku.taskmanager.model;

/**
 * task data model for task manager, modified from to intalio task object
 * @author Jinhua Chen
 * May 9, 2011
 */
public class Task {

	private String id;
	private String type;
	private String state;
	private String processId;
	private String description;
	private String creationDate;
	private String link;	

	public Task(String id, String processId, String description,
			String creationDate) {
		this.id = id;
		this.processId = processId;
		this.description = description;
		this.creationDate = creationDate;

	}
	
	public Task() {}
	
	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public String getState() {
		return state;
	}
	
	public String getProcessId() {
		return processId;
	}
	public String getDescription() {
		return description;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	public String getLink() {
		return link;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	public void setLink(String link) {
		this.link = link;
	}

}
