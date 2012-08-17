package fi.arcusys.koku.common.services.facades;

import fi.arcusys.koku.common.exceptions.KokuServiceException;

/**
 * Simple file logging interface
 *
 * @author Toni Turunen
 *
 */
public interface Logging {
	void logFileDownload(String uid, String path, String message) throws KokuServiceException;
	void logFileUpload(String uid, String path, String message) throws KokuServiceException;
}
