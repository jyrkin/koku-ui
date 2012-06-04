package fi.arcusys.koku.common.services.requests.models;

import java.util.ArrayList;
import java.util.List;

import fi.arcusys.koku.kv.requestservice.MultipleChoice;
import fi.arcusys.koku.kv.requestservice.QuestionType;


public class KokuQuestion {
	
    private String description;
    private int questionNumber;
    private QuestionType type;
    private KokuAnswer answer;
    private List<KokuPossibleAnswer> possibleAnswers = new ArrayList<KokuPossibleAnswer>();

	public KokuQuestion(fi.arcusys.koku.kv.requestservice.Question question) {
		description = question.getDescription();
		questionNumber = question.getNumber();
		type = question.getType();
	}
	
	public KokuQuestion(fi.arcusys.koku.kv.requestservice.Question question, List<MultipleChoice> multipleChoices) {
		this.description = question.getDescription();
		this.questionNumber = question.getNumber();
		this.type = question.getType();
		
		if (multipleChoices != null) {
			for (MultipleChoice multipleChoice : multipleChoices) {
				if (this.questionNumber == multipleChoice.getQuestionNumber()) {
					possibleAnswers.add(new KokuPossibleAnswer(multipleChoice));
				}
			}
		}
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumber() {
		return questionNumber;
	}

	public void setNumber(int number) {
		this.questionNumber = number;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public KokuAnswer getAnswer() {
		return answer;
	}

	public void setAnswer(KokuAnswer answer) {
		this.answer = answer;
	}

	public List<KokuPossibleAnswer> getPossibleAnswers() {
		return possibleAnswers;
	}

	public void setPossibleAnswers(List<KokuPossibleAnswer> possibleAnswers) {
		this.possibleAnswers = possibleAnswers;
	}

	@Override
	public String toString() {
		return "KokuQuestion [description=" + description + ", questionNumber="
				+ questionNumber + ", type=" + type + ", answer=" + answer
				+ ", possibleAnswers=" + possibleAnswers + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((possibleAnswers == null) ? 0 : possibleAnswers.hashCode());
		result = prime * result + questionNumber;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		KokuQuestion other = (KokuQuestion) obj;
		if (answer == null) {
			if (other.answer != null)
				return false;
		} else if (!answer.equals(other.answer))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (possibleAnswers == null) {
			if (other.possibleAnswers != null)
				return false;
		} else if (!possibleAnswers.equals(other.possibleAnswers))
			return false;
		if (questionNumber != other.questionNumber)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
}
