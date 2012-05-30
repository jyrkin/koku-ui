package fi.arcusys.koku.web.exporter.csv;
import static fi.arcusys.koku.web.exporter.csv.CSVExporter.addQuote;

class CSVPerson{
	private String name;
	private String comment;

	public CSVPerson(String name, String comment) {
		this.name = name;
		this.comment = comment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFormattedName() {
		return addQuote(this.name);
	}

	public String getFormattedComment() {
		return addQuote(this.comment);
	}
}
