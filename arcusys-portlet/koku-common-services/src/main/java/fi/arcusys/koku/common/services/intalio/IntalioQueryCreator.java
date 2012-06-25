package fi.arcusys.koku.common.services.intalio;

import java.util.List;

import fi.arcusys.koku.common.exceptions.IntalioException;
import fi.arcusys.koku.common.util.TaskUtil;

/**
 * Creates subquery for Intalio WS
 *
 * @author Toni Turunen
 *
 */
public class IntalioQueryCreator {

	private final int taskType;
	private final String keyword;
	private final List<String> excludeList;
	private final String orderType;

	public IntalioQueryCreator(int taskType, String keyword,
			List<String> excludeList, String orderType) {
		this.taskType = taskType;
		this.keyword = keyword;
		this.excludeList = excludeList;
		this.orderType = orderType;
	}

	/**
	 * Creates subquery to get available tasks
	 *
	 * @param taskType the intalio task type
	 * @param keyword the keyword for searching/filetering
	 * @param orderType order type of tasks
	 * @return query string for intalio database
	 * @throws IntalioException if taskType is not correct
	 */
	public String getTaskSubQuery() throws IntalioException {
		StringBuilder subQuery = new StringBuilder(1024);
		String orderTypeStr = getOrderTypeStr(orderType);

		switch (taskType) {
		case TaskUtil.TASK:
			subQuery.append("(T._state = TaskState.READY OR T._state = TaskState.CLAIMED)");
			subQuery.append(" AND T._description like '%");
			break;
		case TaskUtil.NOTIFICATION:
			subQuery.append("T._state = TaskState.READY");
			subQuery.append(" AND T._description like '%");
			break;
		case TaskUtil.PROCESS:
			subQuery.append("T._description like '%");
			break;
		default:
			throw new IntalioException("TaskType not supported! Please see TaskUtil class for supported TaskTypes.");
		}
		subQuery.append((keyword == null) ? "" : keyword);
		subQuery.append("%'");
		if (excludeList != null && !excludeList.isEmpty()) {
			boolean filterFailure = false;
			boolean first = true;
			StringBuilder excludes = new StringBuilder(128);
			excludes.append(" AND NOT( ");
			for (String exclude : excludeList) {
				// We do not want to add any empty filters because that will definitely cause problems
				if (exclude.isEmpty()) {
					filterFailure = true;
				}
				if (!first) {
					excludes.append(" OR ");
				}
				excludes.append("T._description like '");
				excludes.append(exclude);
				excludes.append("%'");
				first = false;
			}
			excludes.append(")");
			if (!filterFailure) {
				subQuery.append(excludes);
			}
		}
		if (orderTypeStr != null) {
			subQuery.append(" ORDER BY ");
			subQuery.append(orderTypeStr);
		}
		return subQuery.toString();
	}

	/**
	 * Gets query order type according to order string from jsp page
	 *
	 * @param orderType
	 *            order type of tasks
	 * @return order type query for intalio tasks
	 */
	private String getOrderTypeStr(String orderType) {
		if (orderType == null) {
			return "T._creationDate DESC";
		}

		String orderTypeStr;
		if (orderType.equals("description_desc")) {
			orderTypeStr = "T._description DESC";
		} else if (orderType.equals("description_asc")) {
			orderTypeStr = "T._description ASC";
		} else if (orderType.equals("state_desc")) {
			orderTypeStr = "T._state DESC";
		} else if (orderType.equals("state_asc")) {
			orderTypeStr = "T._state ASC";
		} else if (orderType.equals("creationDate_desc")) {
			orderTypeStr = "T._creationDate DESC";
		} else if (orderType.equals("creationDate_asc")) {
			orderTypeStr = "T._creationDate ASC";
		} else {
			orderTypeStr = "T._creationDate DESC";
		}
		return orderTypeStr;
	}


}
