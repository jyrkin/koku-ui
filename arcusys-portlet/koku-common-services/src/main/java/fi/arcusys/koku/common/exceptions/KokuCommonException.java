package fi.arcusys.koku.common.exceptions;

import java.util.UUID;

/**
 * KokuCommonException 
 * 
 * General KokuException
 * 
 * @author Toni Turunen
 */
public class KokuCommonException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public static String generateErrorCode() {
		return UUID.randomUUID().toString();
	}
	
	private final String uuid;
	
	public KokuCommonException(String message) {
		super(message);
		uuid = generateErrorCode();
	}

	public KokuCommonException(String message, Throwable cause) {
		super(message, cause);
		uuid = generateErrorCode();
	}
		
	private final String getErrorCode() {
		return "Unique KOKU error code: '" + uuid + "'. ";
	}

	@Override
	public String getMessage() {
		return getErrorCode() + super.getMessage();
	}
	
	public final String getErrorcode() {
		return uuid;
	}	
}
