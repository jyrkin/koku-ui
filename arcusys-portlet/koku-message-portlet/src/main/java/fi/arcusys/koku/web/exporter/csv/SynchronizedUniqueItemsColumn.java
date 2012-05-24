package fi.arcusys.koku.web.exporter.csv;

import static fi.arcusys.koku.web.exporter.csv.CSVExporter.INNER_FIELD_SEPARATOR;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.SEPARATOR;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.addQuote;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.getSeparators;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fi.arcusys.koku.web.util.Strings;

/* This column only outputs unique items, and it synchronizes the same
 * answers given by multiple people to the same output CSV column */
class SynchronizedUniqueItemsColumn implements Column {

	private String header;

	/* Mapping of answer strings to sets of strings which represent the
	 * people who gave that particular answer */
	private Map<String, Set<CSVPerson>> columnAnswerMap = new TreeMap<String, Set<CSVPerson>>(
			new Comparator<String>() {
				public int compare(String o1, String o2) {
					return Strings.compareNatural(o1, o2);
				}
			}
			);

	public SynchronizedUniqueItemsColumn(String header) {
		this.header = header;
	}

	public void splitAndAddAnswer(String fullAnswer, CSVPerson person) {
		Set<CSVPerson> answeredPeople;
		for (String answer : fullAnswer.split(INNER_FIELD_SEPARATOR)) {
			if (answer.isEmpty() || answer == null) {
				continue;
			}

			if (columnAnswerMap.containsKey(answer)) {
				answeredPeople = columnAnswerMap.get(answer);
			} else {
				answeredPeople = new HashSet<CSVPerson>();
				columnAnswerMap.put(answer, answeredPeople);
			}
			answeredPeople.add(person);
		}
	}

	/* Returns the CSV formatted header for the column that this type represents */
	public String getFormattedColumnHeader() {
		return addQuote(this.header) + getSeparators(this.getWidth());
	}

	/* Returns one person's CSV formatted answer for this column */
	public String getFormattedPersonColumnAnswer(CSVPerson person) {
		StringBuilder csv_string = new StringBuilder();
		for (Map.Entry<String, Set<CSVPerson>> entry : columnAnswerMap.entrySet()) {
			String answer = entry.getKey();
			Set<CSVPerson> answeredPeople = entry.getValue();

			if (answeredPeople.contains(person)) {
				csv_string.append(addQuote(answer));
			}

			csv_string.append(SEPARATOR);
		}
		return csv_string.toString();
	}

	private int getWidth() {
		return this.columnAnswerMap.size();
	}
}
