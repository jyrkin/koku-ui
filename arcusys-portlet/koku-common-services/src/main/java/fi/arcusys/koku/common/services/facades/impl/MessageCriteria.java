package fi.arcusys.koku.common.services.facades.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fi.arcusys.koku.common.services.facades.MessageTasks.MessageSearchCriteria;
import fi.arcusys.koku.kv.messageservice.Fields;

public class MessageCriteria implements MessageSearchCriteria {

	private final List<String> keywords = new ArrayList<String>();
	private final List<String> fields = new ArrayList<String>();
	
	public MessageCriteria(String keyword, String field) {
		if(keyword.trim().length() > 0) {
			String[] keywordsStr = keyword.split(" ");
			keywords.addAll(Arrays.asList(keywordsStr));
		}
		if(field.trim().length() > 0) {
			String[] fieldsStr =  field.split("_");
			for(int i=0; i < fieldsStr.length; i++) {
				if (fieldsStr[i].equals("1")) {
					fields.add(Fields.SENDER.toString());
				} else if (fieldsStr[i].equals("2")) {
					fields.add(Fields.RECEIVER.toString());
				} else if (fieldsStr[i].equals("3")) {
					fields.add(Fields.SUBJECT.toString());
				} else if (fieldsStr[i].equals("4")) {
					fields.add(Fields.CONTENT.toString());
				}
			}
		}
		
	}
	
	@Override
	public List<String> getKeywords() {
		return keywords;
	}

	@Override
	public List<String> getFilteringFields() {
		return fields;
	}

}
