package fi.arcusys.koku.common.services.intalio;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import fi.arcusys.koku.common.exceptions.IntalioException;
import fi.arcusys.koku.common.util.TaskUtil;

public class IntalioQueryCreatorTest {

	public static final String KEYWORD = "Uusi Pyyntö:";
	public static final String ORDER_TYPE = "T._creationDate DESC";

	public static final String EXCLUDE_KEYWORD_1 = "Hoitopyyntö";
	public static final String EXCLUDE_KEYWORD_2 = "Hoitolähete";
	public static final String EXCLUDE_KEYWORD_3 = "Pakkohoito";
	public static final String EMPTY = "";


	public static final String GET_ALL_TASKS = 						"(T._state = TaskState.READY OR T._state = TaskState.CLAIMED) AND T._description like '%%' ORDER BY T._creationDate DESC";

	public static final String GET_ALL_PREFIXED_TASKS =				"(T._state = TaskState.READY OR T._state = TaskState.CLAIMED) " +
																	"AND T._description like '%" + KEYWORD + "%' ORDER BY T._creationDate DESC";

	public static final String GET_ALL_NOTIFICATIONS = 				"T._state = TaskState.READY " +
																	"AND T._description like '%%' ORDER BY T._creationDate DESC";

	public static final String GET_ALL_PREFIXED_NOTIFICATIONS =		"T._state = TaskState.READY " +
																	"AND T._description like '%" + KEYWORD + "%' ORDER BY T._creationDate DESC";

	public static final String GET_ALL_PREFIXED_TASKS_AND_FILTER = "(T._state = TaskState.READY OR T._state = TaskState.CLAIMED) " +
																	"AND T._description like '%" + KEYWORD + "%' " +
																	"AND NOT( T._description like '" + EXCLUDE_KEYWORD_1 + "%' OR T._description like '" + EXCLUDE_KEYWORD_2 + "%' OR T._description like '" + EXCLUDE_KEYWORD_3 + "%') " +
																	"ORDER BY T._creationDate DESC";

	public static final List<String> EXCLUDES_LIST;

	static {
		List<String> excludesModf = new ArrayList<String>();
		excludesModf.add(EXCLUDE_KEYWORD_1);
		excludesModf.add(EXCLUDE_KEYWORD_2);
		excludesModf.add(EXCLUDE_KEYWORD_3);
		EXCLUDES_LIST = Collections.unmodifiableList(excludesModf);
	}

	@Test(expected=IntalioException.class)
	public void throwExceptionIfWrongTaskType() throws IntalioException {
		IntalioQueryCreator creator = new IntalioQueryCreator(0, null, null, null);
		creator.getTaskSubQuery();
	}

	@Test
	public void getQueryEvenIfThereIsNullValues() throws IntalioException {
		IntalioQueryCreator creator = new IntalioQueryCreator(TaskUtil.TASK, null, null, null);
		assertEquals(GET_ALL_TASKS, creator.getTaskSubQuery());
	}

	@Test
	public void getQueryAllTasksWithoutKeyword() throws IntalioException {
		IntalioQueryCreator creator = new IntalioQueryCreator(TaskUtil.TASK, "", null, ORDER_TYPE);
		assertEquals(GET_ALL_TASKS, creator.getTaskSubQuery());
	}

	@Test
	public void getQueryAllNotificationsWithoutKeyword() throws IntalioException {
		IntalioQueryCreator creator = new IntalioQueryCreator(TaskUtil.NOTIFICATION, "", null, ORDER_TYPE);
		assertEquals(GET_ALL_NOTIFICATIONS, creator.getTaskSubQuery());
	}

	@Test
	public void getQueryTasksWithKeyword() throws IntalioException {
		IntalioQueryCreator creator = new IntalioQueryCreator(TaskUtil.TASK, KEYWORD, null, ORDER_TYPE);
		assertEquals(GET_ALL_PREFIXED_TASKS, creator.getTaskSubQuery());
	}

	@Test
	public void getQueryTasksWithKeywordAndEmptyExcludeResults() throws IntalioException {
		IntalioQueryCreator creator = new IntalioQueryCreator(TaskUtil.TASK, KEYWORD, new ArrayList<String>(), ORDER_TYPE);
		assertEquals(GET_ALL_PREFIXED_TASKS, creator.getTaskSubQuery());
	}

	@Test
	public void getQueryTasksWithKeywortdAndExcludeResults() throws IntalioException {
		IntalioQueryCreator creator = new IntalioQueryCreator(TaskUtil.TASK, KEYWORD, EXCLUDES_LIST, ORDER_TYPE);
		assertEquals(GET_ALL_PREFIXED_TASKS_AND_FILTER, creator.getTaskSubQuery());
	}

	@Test
	public void getQueryTaskWithKeywordAndExcludeEmptyStringTest() throws IntalioException {
		List<String> list = new ArrayList<String>();
		list.add(EMPTY);
		IntalioQueryCreator creator = new IntalioQueryCreator(TaskUtil.TASK, KEYWORD, list, ORDER_TYPE);
		assertEquals(GET_ALL_PREFIXED_TASKS, creator.getTaskSubQuery());
		list.add(EMPTY);
		creator = new IntalioQueryCreator(TaskUtil.TASK, KEYWORD, list, ORDER_TYPE);
		assertEquals(GET_ALL_PREFIXED_TASKS, creator.getTaskSubQuery());
	}

}
