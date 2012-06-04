package fi.arcusys.koku.web.exporter.csv;

interface CSVColumn {

	/* If the answer can be split (IE. it has INNER_FIELD_SEPARATORs),
	 * split it and then add it to the structure */
	void splitAndAddAnswer(String fullAnswer, CSVPerson person);

	/* Returns the CSV formatted header for the column that this type represents */
	String getFormattedColumnHeader();

	/* Returns one person's CSV formatted answers for this column */
	String getCSVFormattedAnswer(CSVPerson person);
}
