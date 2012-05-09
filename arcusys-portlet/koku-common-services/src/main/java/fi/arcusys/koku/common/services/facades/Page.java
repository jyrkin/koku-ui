package fi.arcusys.koku.common.services.facades;

/**
 * Query page
 * 
 * @author Toni Turunen
 *
 */
public interface Page {
	
	/**
	 * Returns wanted query position 
	 * 
	 * @return
	 */
	Integer getFirst();
	
	/**
	 * Returns query max size
	 * 
	 * @return
	 */
	Integer getLast();	
}
