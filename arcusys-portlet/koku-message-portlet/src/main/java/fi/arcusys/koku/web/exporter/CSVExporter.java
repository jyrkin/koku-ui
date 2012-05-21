package fi.arcusys.koku.web.exporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.support.ResourceBundleMessageSource;

import fi.arcusys.koku.kv.model.KokuAnswer;
import fi.arcusys.koku.kv.model.KokuQuestion;
import fi.arcusys.koku.kv.model.KokuRequest;
import fi.arcusys.koku.kv.model.KokuResponse;
import fi.arcusys.koku.users.KokuUser;
import fi.arcusys.koku.util.MessageUtil;

public class CSVExporter implements Exporter {

	private static final String SEPARATOR 				= ";";
	private static final String TEXT_DELIMITER			= "\"";
	private static final String NEW_LINE				= "\n";
	private static final String INNER_FIELD_SEPARATOR	= ", ";
	private static final String FILTER					= "\\res\\n|\\res|\\n|"+SEPARATOR+"|"+TEXT_DELIMITER;

	private static final Comparator<KokuAnswer> SORT_BY_ANSWER_NUMBER = new Comparator<KokuAnswer>() {
			@Override
			public int compare(KokuAnswer o1, KokuAnswer o2) {
				if (o1.getQuestionNumber() > o2.getQuestionNumber()) {
					return 1;
				} else if (o1.getQuestionNumber() < o2.getQuestionNumber()) {
					return -1;
				} else {
					return 0;
				}
			}
		};

	private class CSVPerson{
		private List<String[]> rowAnswers;
		private String name;
		private String comment;

		public CSVPerson(String name, String comment) {
			this.name = name;
			this.comment = comment;
			this.rowAnswers = new ArrayList<String[]>();
		}

		public void addAnswer(String answer) {
			Map<Integer, Integer> columnWidths = CSVExporter.this.columnWidths;
			String[] splitAnswer = answer.split(INNER_FIELD_SEPARATOR);
			this.rowAnswers.add(splitAnswer);
			int i = this.rowAnswers.indexOf(splitAnswer);
			if (columnWidths.containsKey(i)) {
				if (columnWidths.get(i) < splitAnswer.length) {
					columnWidths.put(i, splitAnswer.length);
				}
			} else {
				columnWidths.put(i, splitAnswer.length);
			}
		}

		public String getFormattedRow() {
			StringBuilder row = new StringBuilder();
			row.append(addQuote(this.name) + SEPARATOR);
			for (String[] answer : this.rowAnswers) {
				int column = this.rowAnswers.indexOf(answer);
				int answerWidth = answer.length;
				int maxAnswerWidth = columnWidths.get(column);
				int padding = maxAnswerWidth - answerWidth;
				for (String answerCell : answer) {
					row.append(addQuote(answerCell) + SEPARATOR);
				}
				row.append(getSeparators(padding));
			}
			row.append(addQuote(this.comment));
			row.append(NEW_LINE);
			return row.toString();
		}
	}

	private KokuRequest kokuRequest;
	private ResourceBundleMessageSource messageSource;
	private List<CSVPerson> persons;
	private Map<Integer, Integer> columnWidths;

	public CSVExporter(KokuRequest kokuRequest, ResourceBundleMessageSource messageSource) {
		this.kokuRequest = kokuRequest;
		this.messageSource = messageSource;
		this.persons = new ArrayList<CSVPerson>();
		this.columnWidths = new HashMap<Integer, Integer>();
	}

	private CSVPerson addPerson(String name, String comment) {
		CSVPerson person = new CSVPerson(name, comment);
		this.persons.add(person);
		return person;
	}

	private String getFormattedRows() {
		StringBuilder formattedRow = new StringBuilder();
		for (CSVPerson person : this.persons) {
			formattedRow.append(person.getFormattedRow());
		}
		return formattedRow.toString();
	}

	private int getColumnWidth(int column) {
		return columnWidths.get(column);
	}

	@Override
	public String getContents() {
		final Locale locale = MessageUtil.getLocale();
		StringBuilder csv = null;

		for (KokuResponse res : kokuRequest.getRespondedList()) {
			CSVPerson person = this.addPerson(res.getReplierUser().getFullName(), res.getComment());
			Collections.sort(res.getAnswers(), SORT_BY_ANSWER_NUMBER);
			for (KokuAnswer answer : res.getAnswers()) {
				if (answer != null) {
					person.addAnswer(answer.getAnswer());
				}
				else {
					assert (false);
				}
			}
		}

		csv = new StringBuilder(4096);
		/* UTF-8 BOM (Do not remove, otherwise Excel won't recognize characters correctly!) */
		csv.append('\uFEFF');
		/* Headers */
		csv.append(addQuote(messageSource.getMessage("export.responseSummary", null, locale)));
		csv.append(NEW_LINE);
		csv.append(addQuote(messageSource.getMessage("export.respondent", null, locale))+SEPARATOR);

		// Question descriptions
		for (KokuQuestion q : kokuRequest.getQuestions()) {
			int i = kokuRequest.getQuestions().indexOf(q);
			int width = getColumnWidth(i);
			String description = addQuote(q.getDescription());
			csv.append(addQuote(description) + getSeparators(width));
		}

		// Comment header
		csv.append(addQuote(messageSource.getMessage("export.comment", null, locale)));
		csv.append(NEW_LINE);

		/* Data */
		csv.append(this.getFormattedRows());

		csv.append(NEW_LINE);
		csv.append(addQuote(messageSource.getMessage("export.missed", null, locale)));
		csv.append(NEW_LINE);

		for (KokuUser name : kokuRequest.getUnrespondedList()) {
			csv.append(addQuote(name.getFullName()));
			csv.append(NEW_LINE);
		}
		return csv.toString();
	}

	private static String getSeparators(int n) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < n; i++) {
			s.append(SEPARATOR);
		}
		return s.toString();
	}

	/**
	 * Adds the quotation mark char '"' to the string
	 * @param s
	 * @return
	 */
	private static String addQuote(String s) {
		return TEXT_DELIMITER+s.replaceAll(FILTER, "")+TEXT_DELIMITER;
	}

}
