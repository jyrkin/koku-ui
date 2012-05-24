package fi.arcusys.koku.web.exporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.support.ResourceBundleMessageSource;

import fi.arcusys.koku.common.services.requests.models.*;
import fi.arcusys.koku.common.services.users.KokuUser;
import fi.arcusys.koku.common.util.MessageUtil;

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

	/*
	 * Holds the answers and name/comment information of one person
	 * for one row in the resulting CSV file.
	 */
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

		public String getFormattedAnswerRow() {
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

	/*
	 * Add a person entry to the persons list.
	 */
	private CSVPerson addPerson(String name, String comment) {
		CSVPerson person = new CSVPerson(name, comment);
		this.persons.add(person);
		return person;
	}

	/*
	 * Returns all the answer rows of people who gave an answer in a CSV format
	 * */
	private String getFormattedAnswerRows() {
		StringBuilder formattedRow = new StringBuilder();
		for (CSVPerson person : this.persons) {
			formattedRow.append(person.getFormattedAnswerRow());
		}
		return formattedRow.toString();
	}

	private int getColumnWidth(int column) {
		return columnWidths.get(column);
	}

	/*
	 * Transform the data given in the constructor and return it as a csv string.
	 */
	@Override
	public String getContents() {
		final Locale locale = MessageUtil.getLocale();
		String responseSummary = messageSource.getMessage("export.responseSummary", null, locale);
		String respondent = messageSource.getMessage("export.respondent", null, locale);
		String comment = messageSource.getMessage("export.comment", null, locale);
		String missed = messageSource.getMessage("export.missed", null, locale);
		return getContents(responseSummary, respondent, comment, missed);
	}

	/*
	 * Two separate methods to make testing easier,
	 * mocking a messageSource object discovered to be non-trivial.
	 */
	public String getContents(String responseSummary, String respondent, String comment, String missed) {

		/* Iterate over the data in kokuRequest and input them into the data structures */
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

		StringBuilder csv = new StringBuilder(4096);

		/* UTF-8 BOM (Do not remove, otherwise Excel won't recognize characters correctly!) */
		csv.append('\uFEFF');

		/* Headers */
		csv.append(addQuote(responseSummary));
		csv.append(NEW_LINE);
		csv.append(addQuote(respondent)+SEPARATOR);

		/*  Question descriptions */
		for (KokuQuestion q : kokuRequest.getQuestions()) {
			int i = kokuRequest.getQuestions().indexOf(q);
			int width = getColumnWidth(i);
			String description = addQuote(q.getDescription());
			csv.append(addQuote(description) + getSeparators(width));
		}

		/*  Comment header */
		csv.append(addQuote(comment));
		csv.append(NEW_LINE);

		/* Data */
		csv.append(this.getFormattedAnswerRows());
		csv.append(NEW_LINE);

		/* Entries of people who didn't answer */
		csv.append(addQuote(missed));
		csv.append(NEW_LINE);
		for (KokuUser name : kokuRequest.getUnrespondedList()) {
			csv.append(addQuote(name.getFullName()));
			csv.append(NEW_LINE);
		}

		return csv.toString();
	}

	/**
	 * Returns n SEPARATORs, used for padding in CSV export
	 * @param s
	 * @return
	 */
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
