package fi.arcusys.koku.web.exporter.csv;

import static fi.arcusys.koku.web.exporter.csv.CSVExporter.INNER_FIELD_SEPARATOR;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.SEPARATOR;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.addQuote;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


class Column {
	/* Mapping of answer strings to sets of strings which represent the
	 * people who gave that particular answer */
	private String header;
	private Map<String, Set<String>> columnAnswerMap = new TreeMap<String, Set<String>>();

	public Column(String header) {
		this.header = header;
	}

	public void splitAndAddAnswer(String fullAnswer, CSVPerson person) {
		Set<String> answeredPeople;
		for (String answer : fullAnswer.split(INNER_FIELD_SEPARATOR)) {
			if (columnAnswerMap.containsKey(answer)) {
				answeredPeople = columnAnswerMap.get(answer);
			} else {
				answeredPeople = new HashSet<String>();
				columnAnswerMap.put(answer, answeredPeople);
			}
			answeredPeople.add(person.getName());
		}
	}

	/* Returns the CSV formatted header for the column that this type represents */
	public String getFormattedColumnHeader() {
		return addQuote(this.header) + getSeparators(this.getWidth());
	}

	/* Returns one person's CSV formatted answers for this column */
	public String getFormattedPersonColumnAnswers(CSVPerson person) {
		StringBuilder csv_string = new StringBuilder();
		String personName = person.getName();
		for (Map.Entry<String, Set<String>> entry : columnAnswerMap.entrySet()) {
			String answer = entry.getKey();
			Set<String> answeredPeople = entry.getValue();

			if (answeredPeople.contains(personName)) {
				csv_string.append(addQuote(answer));
			}

			csv_string.append(SEPARATOR);
		}
		return csv_string.toString();
	}

	public int getWidth() {
		return this.columnAnswerMap.size();
	}

	private static String getSeparators(int n) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < n; i++) {
			s.append(SEPARATOR);
		}
		return s.toString();
	}
}
