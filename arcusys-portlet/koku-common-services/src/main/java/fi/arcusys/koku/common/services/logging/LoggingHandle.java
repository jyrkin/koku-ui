package fi.arcusys.koku.common.services.logging;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.AbstractHandle;
import fi.arcusys.koku.common.services.facades.Logging;
import fi.arcusys.koku.common.util.DummyMessageSource;

public class LoggingHandle extends AbstractHandle implements Logging {

	private final LoggingService service;

	public LoggingHandle() {
		super(new DummyMessageSource());
		service = new LoggingService();
	}

	@Override
	public void logFileDownload(String uid, String path, String message) throws KokuServiceException {
		service.logFileDownload(uid, path, message);
	}

	@Override
	public void logFileUpload(String uid, String path, String message) throws KokuServiceException {
		service.logFileUploads(uid, path, message);
	}

}
