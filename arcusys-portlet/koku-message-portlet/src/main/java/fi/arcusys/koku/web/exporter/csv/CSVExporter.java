package fi.arcusys.koku.web.exporter.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

import fi.arcusys.koku.kv.model.KokuAnswer;
import fi.arcusys.koku.kv.model.KokuQuestion;
import fi.arcusys.koku.kv.model.KokuRequest;
import fi.arcusys.koku.kv.model.KokuResponse;
import fi.arcusys.koku.kv.requestservice.QuestionType;
import fi.arcusys.koku.users.KokuUser;
import fi.arcusys.koku.util.MessageUtil;
import fi.arcusys.koku.web.exporter.Exporter;

public class CSVExporter implements Exporter {

	protected static final String SEPARATOR 			= ";";
	protected static final String TEXT_DELIMITER		= "\"";
	protected static final String NEW_LINE				= "\n";
	protected static final String INNER_FIELD_SEPARATOR	= ", ";
	protected static final String FILTER				= "\\res\\n|\\res|\\n|"+SEPARATOR+"|"+TEXT_DELIMITER;

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

	private KokuRequest kokuRequest;
	private ResourceBundleMessageSource messageSource;
	private List<CSVPerson> persons;
	private List<Column> answerColumns;

	public CSVExporter(KokuRequest kokuRequest, ResourceBundleMessageSource messageSource) {
		this.kokuRequest = kokuRequest;
		this.messageSource = messageSource;
		this.persons = new ArrayList<CSVPerson>();
		this.answerColumns = new ArrayList<Column>();
	}

	/* Add a person entry to the persons list. */
	private CSVPerson addPerson(String name, String comment) {
		CSVPerson person = new CSVPerson(name, comment);
		this.persons.add(person);
		return person;
	}

	private void addAnswerColumn(String header, QuestionType type) {
		this.answerColumns.add(new Column(header));
	}

	private void splitAndAddAnswer(String answer, CSVPerson person, int column) {
		answerColumns.get(column).splitAndAddAnswer(answer, person);
	}

	private String getFormattedQuestionHeaders() {
		StringBuilder headers = new StringBuilder();
		for (Column answerColumn : this.answerColumns) {
			headers.append(answerColumn.getFormattedColumnHeader());
		}
		return headers.toString();
	}

	/* Returns all the answer rows of people who gave an answer in a CSV format */
	private String getFormattedAnswerRows() {
		StringBuilder answers = new StringBuilder();
		for (CSVPerson person : this.persons) {
			/* Name field */
			answers.append(person.getFormattedName());
			answers.append(SEPARATOR);

			/* Column answers */
			for (Column answerColumn : this.answerColumns) {
				answers.append(answerColumn.getFormattedPersonColumnAnswers(person));
			}

			/* Comment field */
			answers.append(person.getFormattedComment());
			answers.append(NEW_LINE);
		}
		return answers.toString();
	}

	/* Transform the data given in the constructor and return it as a csv string. */
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

		/* Iterate over all the data in kokuRequest and input it into the data structures */
		for (KokuQuestion q : kokuRequest.getQuestions()) {
			this.addAnswerColumn(q.getDescription(), q.getType());
		}
		for (KokuResponse res : kokuRequest.getRespondedList()) {
			CSVPerson person = this.addPerson(res.getReplierUser().getFullName(), res.getComment());
			Collections.sort(res.getAnswers(), SORT_BY_ANSWER_NUMBER);
			for (KokuAnswer answer : res.getAnswers()) {
				int column = res.getAnswers().indexOf(answer);
				if (answer != null) {
					this.splitAndAddAnswer(answer.getAnswer(), person, column);
				}
			}
		}

		StringBuilder csv = new StringBuilder(4096);

		/* Headers */
		csv.append(addQuote(responseSummary));
		csv.append(NEW_LINE);
		csv.append(addQuote(respondent)+SEPARATOR);

		/*  Question descriptions */
		csv.append(this.getFormattedQuestionHeaders());

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
	 * Adds the quotation mark char '"' to the string
	 * @param s
	 * @return
	 */
	protected static String addQuote(String s) {
		return TEXT_DELIMITER+s.replaceAll(FILTER, "")+TEXT_DELIMITER;
	}

}
