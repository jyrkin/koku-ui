package fi.arcusys.koku.web;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.requests.employee.EmployeeRequestHandle;
import fi.arcusys.koku.common.services.requests.models.KokuRequest;
import fi.arcusys.koku.common.util.DummyMessageSource;
import fi.arcusys.koku.web.exporter.Exporter;
import fi.arcusys.koku.web.exporter.csv.CSVExporter;

/**
 * Generates csv file containing response summary information
 * @author Jinhua Chen
 * Aug 4, 2011
 */
@Controller("exportFileController")
@RequestMapping(value = "VIEW")
public class ExportFileController {

	private static final Logger LOG = LoggerFactory.getLogger(ExportFileController.class);
	private static final int MAX_FILENAME_SIZE = 15;
	private static final String FILENAME_REGEX_FILTER = "[^a-zA-ZåäöÅÄÖ_ ]";

	@Resource
	private ResourceBundleMessageSource messageSource;

	/**
	 * Generates the request summary with given request id in csv format
	 * for downloading
	 * @param requestId
	 * @param resourceRequest
	 * @param response
	 */
	@ResourceMapping(value = "exportFile")
	public void download(@RequestParam(value = "newRequestId") String requestId,
			ResourceRequest resourceRequest, ResourceResponse response) {
		response.setContentType("text/csv; charset=utf-8");
		EmployeeRequestHandle reqhandle = new EmployeeRequestHandle(new DummyMessageSource());
		KokuRequest kokuRequest = null;
		try {
			kokuRequest = reqhandle.getKokuRequestById(requestId);
			if (kokuRequest == null) {
				LOG.error("getKokuRequestById returned null. requestId: '"+requestId+"'");
				return;
			}
		} catch (KokuServiceException kse) {
			LOG.error("Error when trying to create CSV export. WS doesn't work properly. requestId: '"+requestId+"'", kse);
		}
		String requestSubject = kokuRequest.getSubject();
		if (requestSubject == null || requestSubject.isEmpty()) {
			requestSubject = "vastaus";
		} else {
			requestSubject = requestSubject.replaceAll(FILENAME_REGEX_FILTER, "");
			requestSubject = requestSubject.substring(0, MAX_FILENAME_SIZE);

			/* Firefox doesn't seem to handle spaces for some reason, the filename after
			 * a space is truncated and the .csv file extension is lost, this fixes it. */
			requestSubject = requestSubject.replaceAll(" ", "_");
		}

		/* Note: Do not change Pragma and Cache-Control values. Otherwise IE doesn't recognize
		 * CSV file properly when using HTTPS. See http://support.microsoft.com/kb/316431 for more details.
		 * This is issue with IE5 - IE9 versions. */
		response.setProperty("Pragma", "private");
		response.setProperty("Cache-Control", "private, must-revalidate");

		response.setProperty("Content-Disposition", "attachment; filename="+requestSubject+".csv");
		response.setProperty("Content-Type", "text/csv, charset=UTF-8; encoding=UTF-8");

		PrintWriter responseWriter = null;
		try {
			responseWriter = response.getWriter();
			final BufferedWriter writer = new BufferedWriter(responseWriter);

			Exporter csvExporter = new CSVExporter(kokuRequest, messageSource);

			/* UTF-8 BOM (Do not remove, otherwise Excel won't recognize characters correctly!) */
			writer.write('\uFEFF');

			writer.write(csvExporter.getContents());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			final String username = resourceRequest.getUserPrincipal().getName();
			LOG.error("Generate csv file failed. Username: '"+username+"'  RequestId: '"+kokuRequest.getRequestId()+"'");
		}
	}


}
