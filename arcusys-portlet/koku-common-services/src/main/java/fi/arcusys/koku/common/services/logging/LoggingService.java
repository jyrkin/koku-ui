package fi.arcusys.koku.common.services.logging;

import javax.xml.ws.BindingProvider;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.util.Properties;
import fi.arcusys.koku.logging.LoggingService_Service;

public class LoggingService {

	private final fi.arcusys.koku.logging.LoggingService service;

	public LoggingService() {
		LoggingService_Service logService = new LoggingService_Service();
		service = logService.getLoggingServicePort();
		((BindingProvider)service).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, Properties.LOGGING_SERVICE);
	}

	public void logFileDownload(String uid, String path, String message) throws KokuServiceException {
		try {
			service.logFileDownload(uid, path, message);
		} catch (RuntimeException e) {
			throw new KokuServiceException("Failed to log file download. UID: '"+uid+"' Path: '"+path+"' Message:'"+message+"'", e);
		}
	}

	public void logFileUploads(String uid, String path, String message) throws KokuServiceException {
		try {
			service.logFileUpload(uid, path, message);
		} catch (RuntimeException e) {
			throw new KokuServiceException("Failed to log file upload. UID: '"+uid+"' Path: '"+path+"' Message:'"+message+"'", e);
		}
	}

}
