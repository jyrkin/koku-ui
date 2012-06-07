package fi.arcusys.koku.common.services.requests.models;


public class KokuPossibleAnswer {
		
    private String answerDescription;
    private int answerNumber;

	public KokuPossibleAnswer(fi.arcusys.koku.kv.requestservice.MultipleChoice multipleChoice) {
		this.answerDescription = multipleChoice.getDescription();
		this.answerNumber = multipleChoice.getNumber();
	}
	
	public KokuPossibleAnswer(String answerDescription, int answerNumber) {
		this.answerDescription = answerDescription;
		this.answerNumber = answerNumber;
	}

	public String getAnswerDescription() {
		return answerDescription;
	}

	public void setAnswerDescription(String answerDescription) {
		this.answerDescription = answerDescription;
	}

	public int getAnswerNumber() {
		return answerNumber;
	}

	public void setAnswerNumber(int answerNumber) {
		this.answerNumber = answerNumber;
	}

	@Override
	public String toString() {
		return "KokuPossibleAnswer [answerDescription=" + answerDescription
				+ ", answerNumber=" + answerNumber + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((answerDescription == null) ? 0 : answerDescription
						.hashCode());
		result = prime * result + answerNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KokuPossibleAnswer other = (KokuPossibleAnswer) obj;
		if (answerDescription == null) {
			if (other.answerDescription != null)
				return false;
		} else if (!answerDescription.equals(other.answerDescription))
			return false;
		if (answerNumber != other.answerNumber)
			return false;
		return true;
	}
	
	
}
