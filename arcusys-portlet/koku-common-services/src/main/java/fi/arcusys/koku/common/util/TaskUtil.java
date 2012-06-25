package fi.arcusys.koku.common.util;


/**
 * Utilities used in the task manager
 * @author Jinhua Chen
 * May 10, 2011
 */
public final class TaskUtil {

	private TaskUtil() { }

	/**
	 * Superuser contains username and participant token of intalio server
	 */
	public static final int PAGE_NUMBER = 5; // number of tasks in one page

	public static final int TASK = 1;
	public static final int NOTIFICATION = 2;
	public static final int PROCESS = 3;

	public static final String TASK_TYPE = "PATask";
	public static final String NOTIFICATION_TYPE = "Notification";
	public static final String PROCESS_TYPE = "PIPATask";

	/**
	 * Gets intalio task type string according to the integer task type
	 * @param taskType intalio task type in Integer
	 * @return intalio task type as in database
	 */
	public static String getTaskType(int taskType) {

		switch (taskType) {
			case TaskUtil.NOTIFICATION:
				return TaskUtil.NOTIFICATION_TYPE;
			case TaskUtil.PROCESS:
				return TaskUtil.PROCESS_TYPE;
			case TaskUtil.TASK:	// fallthrough
			default:
				return TaskUtil.TASK_TYPE;
		}
	}

}
