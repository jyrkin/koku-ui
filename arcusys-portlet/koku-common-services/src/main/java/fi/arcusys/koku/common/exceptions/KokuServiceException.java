package fi.arcusys.koku.common.exceptions;

/**
 * KokuServiceException 
 * 
 * Usually this exception will thrown when WS doesn't respond properly or at all.
 * 
 * @author Toni Turunen
 */
public class KokuServiceException extends KokuCommonException {
	
	private static final long serialVersionUID = 1L;
			
	public KokuServiceException(String message) {
		super(message);
	}

	public KokuServiceException(String message, Throwable cause) {
		super(message, cause);
	}
		
}
