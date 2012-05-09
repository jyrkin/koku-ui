package fi.arcusys.koku.common.services.facades;

import java.util.List;

/**
 * Wrapper class for tasks
 * 
 * @author Toni Turunen
 *
 * @param <T>
 */
public interface ResultList<T> {

	/**
	 * List of results
	 * 
	 * @return list of results
	 */
	List<T> getResults();
	/**
	 * Total number of tasks. 
	 * This not same as getTasks.length()
	 * 
	 * @return number of task
	 */
	Integer getTotalTasks();
	/**
	 * Total pages
	 * 
	 * @return
	 */
	Integer getTotalPages();
	/**
	 * Return single page length
	 * 
	 * @return page length
	 */
	Integer getPageLength();
	/**
	 * Returns current page num
	 * 
	 * @return pagenum
	 */
	Integer getCurrentPage();
}
