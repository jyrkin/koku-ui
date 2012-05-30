package fi.arcusys.koku.common.exceptions;

/**
 * KokuPortletException 
 * 
 * This exception will thrown when problem lies in portlet side
 * 
 * @author Toni Turunen
 */
public class KokuPortletException extends KokuCommonException {
	
	private static final long serialVersionUID = 1L;
			
	public KokuPortletException(String message) {
		super(message);
	}

	public KokuPortletException(String message, Throwable cause) {
		super(message, cause);
	}
		
}
