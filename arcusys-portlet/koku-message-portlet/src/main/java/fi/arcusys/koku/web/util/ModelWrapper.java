package fi.arcusys.koku.web.util;

import fi.arcusys.koku.common.services.facades.impl.ResponseStatus;


/**
 * 
 * Interface passes real model and additional information about request
 * (e.g errorMessages etc.)
 * 
 * @author Toni Turunen
 *
 * @param <T>
 */
public interface ModelWrapper<T> {
	
	/**
	 * Returns real model
	 * 
	 * @return
	 */
	T getModel();
	
	
	/**
	 * Returns responseStatus (OK/FAIL)
	 * 
	 * @return
	 */
	ResponseStatus getResponseStatus();
	
	
	/**
	 * Return errorcode
	 * 
	 * @return errorcode
	 */
	String getErrorCode();

}
