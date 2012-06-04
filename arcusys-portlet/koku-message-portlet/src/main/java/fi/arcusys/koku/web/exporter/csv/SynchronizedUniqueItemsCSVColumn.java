package fi.arcusys.koku.web.exporter.csv;

import static fi.arcusys.koku.web.exporter.csv.CSVExporter.INNER_FIELD_SEPARATOR;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.SEPARATOR;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.addQuote;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.getSeparators;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fi.arcusys.koku.common.services.requests.models.KokuPossibleAnswer;

/* This column only outputs unique items, and it synchronizes the same
 * answers given by multiple people to the same output CSV column */
class SynchronizedUniqueItemsCSVColumn implements CSVColumn {
	
	/* Answer consists of the answer string mapped to the people who gave that answer.
	 * This is done to synchronize the columns easily. */
	private class PossibleAnswer {
		private int sortNumber;
		private Set<CSVPerson> peopleGivenThisAnswer = new HashSet<CSVPerson>();
		
		public PossibleAnswer(CSVPerson person) {
			this.sortNumber = SynchronizedUniqueItemsCSVColumn.this.nextAnswerSortNumber;
			SynchronizedUniqueItemsCSVColumn.this.nextAnswerSortNumber += 1;
			this.addAnswer(person);
		}
		
		public PossibleAnswer(int sortNumber) {
			this.sortNumber = sortNumber;
			if (SynchronizedUniqueItemsCSVColumn.this.nextAnswerSortNumber <= sortNumber) {
				SynchronizedUniqueItemsCSVColumn.this.nextAnswerSortNumber = sortNumber + 1;
			}
		}
		
		public void addAnswer(CSVPerson person) {
			this.peopleGivenThisAnswer.add(person);
		}
				
		public boolean containsPerson(CSVPerson person) {
			return this.peopleGivenThisAnswer.contains(person);
		}
		
		public boolean hasAnswers() {
			return this.peopleGivenThisAnswer.size() > 0;
		}
		
		public int getSortNumber() {
			return this.sortNumber;
		}
	}

	private String header;
	private int nextAnswerSortNumber = 0;
	private Map<String, PossibleAnswer> possibleAnswerMap = new HashMap<String, PossibleAnswer>();

	public SynchronizedUniqueItemsCSVColumn(String header, List<KokuPossibleAnswer> kokuPossibleAnswers) {
		this.header = header;
		for (KokuPossibleAnswer kokuPossibleAnswer : kokuPossibleAnswers) {
			possibleAnswerMap.put(kokuPossibleAnswer.getAnswerDescription(), new PossibleAnswer(kokuPossibleAnswer.getAnswerNumber()));
		}
	}

	public void addAnswer(String answer, CSVPerson person) {
		if (answer.isEmpty()) {
			return;
		}
		if (possibleAnswerMap.containsKey(answer)) {
			this.possibleAnswerMap.get(answer).addAnswer(person);
		} else {
			this.possibleAnswerMap.put(answer, new PossibleAnswer(person));
		}
	}

	public void splitAndAddAnswer(String fullAnswer, CSVPerson person) {
		for (String answer : fullAnswer.split(INNER_FIELD_SEPARATOR)) {
			try {
				this.addAnswer(URLDecoder.decode(answer, "UTF-8"), person);
			} catch (UnsupportedEncodingException e) {
				this.addAnswer(answer, person);
			}
		}
	}

	/* Returns the CSV formatted header for the column that this type represents */
	public String getFormattedColumnHeader() {
		return addQuote(this.header) + getSeparators(this.getWidth());
	}

	/* Returns one person's CSV formatted answer for this column */
	public String getCSVFormattedAnswer(CSVPerson person) {
		StringBuilder csv_string = new StringBuilder();
		TreeMap <Integer, String> answers = new TreeMap<Integer, String>();
		for (Map.Entry<String, PossibleAnswer> entry : possibleAnswerMap.entrySet()) {
			String answerCSVString = addQuote(entry.getKey());
			PossibleAnswer answer = entry.getValue();

			if (answer.containsPerson(person)) {
				answers.put(answer.getSortNumber(), answerCSVString);
			} else if (answer.hasAnswers()) {
				answers.put(answer.getSortNumber(),"");
			}
		}
		
		for (String answerCSVString : answers.values()) {
			csv_string.append(answerCSVString);
			csv_string.append(SEPARATOR);
		}
		
		return csv_string.toString();
	}

	private int getWidth() {
		int size = 0;
		
		for (PossibleAnswer pAnswer : this.possibleAnswerMap.values()) {
			if (pAnswer.hasAnswers()) {
				size += 1;
			}
		}
		
		if (size == 0) {
			/* Even without answers, the datastructure takes at least one CSV column of space */
			size = 1;
		}
		
		return size;

	}
}
