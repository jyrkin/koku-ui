package fi.arcusys.koku.web;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import fi.arcusys.koku.kv.KokuRequest;
import fi.arcusys.koku.kv.RequestHandle;
import fi.arcusys.koku.kv.requestservice.Answer;
import fi.arcusys.koku.kv.requestservice.Question;
import fi.arcusys.koku.kv.requestservice.Response;

/**
 * Generates csv file containing response summary information
 * @author Jinhua Chen
 * Aug 4, 2011
 */
@Controller("exportFileController")
@RequestMapping(value = "VIEW")
public class ExportFileController {

	private Logger logger = Logger.getLogger(ExportFileController.class);
	
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
		response.setProperty("Content-Disposition", "attachment; filename=response.csv");
		RequestHandle reqhandle = new RequestHandle();
		KokuRequest kokuRequest = reqhandle.getKokuRequestById(requestId);

		try {
			BufferedWriter writer = new BufferedWriter(response.getWriter());

			if (kokuRequest == null) {
				return;
			} else {
				writer.write(addQuote("Response Summary"));
				writer.newLine();
				writer.write(addQuote()+",");

				Iterator<Question> it_q = kokuRequest.getQuestions().iterator();

				while (it_q.hasNext()) {
					Question q = it_q.next();
					writer.write(addQuote(q.getDescription()) + "," +addQuote()+ ",");
				}
				writer.newLine();
				writer.write(addQuote("Respondent")+",");

				for (int i = 0; i < kokuRequest.getQuestions().size(); i++) {
					writer.write(addQuote("Answer")+","+addQuote("Comment")+",");
				}
				writer.newLine();

				Iterator<Response> it_res = kokuRequest.getRespondedList()
						.iterator();
				while (it_res.hasNext()) {
					Response res = it_res.next();
					writer.write(addQuote(res.getName()) + ",");

					Iterator<Answer> it_ans = res.getAnswers().iterator();
					while (it_ans.hasNext()) {
						Answer answer = it_ans.next();
						writer.write(addQuote(answer.getAnswer()) + ","
								+ addQuote(answer.getComment()) + ",");
					}
					writer.newLine();
				}
				writer.newLine();
				writer.write(addQuote("Missed"));
				writer.newLine();
				Iterator<String> it_unres = kokuRequest.getUnrespondedList()
						.iterator();
				while (it_unres.hasNext()) {
					String name = it_unres.next();
					writer.write(addQuote(name));
					writer.newLine();
				}

				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			logger.error("Generate csv file failed");
		}
	}
	
	/**
	 * Adds the quotation mark char '"' to the string
	 * @param s
	 * @return
	 */
	private String addQuote(String s) {
		String q = "\"";
		
		return q+s+q;
	}
	
	/**
	 * Generates quotation mark char '"'
	 * @return
	 */
	private String addQuote() {
		return "\"\"";
	}

}
