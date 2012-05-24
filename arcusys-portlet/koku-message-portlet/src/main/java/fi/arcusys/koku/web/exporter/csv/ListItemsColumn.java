package fi.arcusys.koku.web.exporter.csv;

import static fi.arcusys.koku.web.exporter.csv.CSVExporter.INNER_FIELD_SEPARATOR;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.SEPARATOR;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.addQuote;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.getSeparators;

import java.util.HashMap;
import java.util.Map;

/* This column handles multiple answers with the same value from one person
 * It doesn't synchronize the answers to the same CSV column,
 * but it does padding CSV columns so that the headers and the columns after this column align properly*/
class ListItemsColumn implements Column {

	private String header;
	private int maxWidth = 0;

	/* Mapping of people to the answers they've given */
	private Map<CSVPerson, String[]> columnAnswers = new HashMap<CSVPerson, String[]>();


	public ListItemsColumn(String header) {
		this.header = header;
	}

	public void splitAndAddAnswer(String fullAnswer, CSVPerson person) {
		String[] answers = fullAnswer.split(INNER_FIELD_SEPARATOR);
		this.columnAnswers.put(person, answers);
		if (this.maxWidth < answers.length) {
			this.maxWidth = answers.length;
		}
	}

	/* Returns the CSV formatted header for the column that this type represents */
	public String getFormattedColumnHeader() {
		return addQuote(this.header) + getSeparators(this.maxWidth);
	}

	/* Returns one person's CSV formatted answer for this column */
	public String getFormattedPersonColumnAnswer(CSVPerson person) {
		StringBuilder csv_string = new StringBuilder();
		String[] columnAnswer = this.columnAnswers.get(person);
		for (String answer : columnAnswer) {
			csv_string.append(addQuote(answer));
			csv_string.append(SEPARATOR);
		}
		/* Padding */
		csv_string.append(getSeparators(this.maxWidth - columnAnswer.length));
		return csv_string.toString();
	}
}
