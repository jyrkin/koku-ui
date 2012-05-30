package fi.arcusys.koku.web.util.impl.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fi.arcusys.koku.common.services.facades.employee.EmployeeWarrantTasks.WarrantSearchCriteria;

/**
 * Temporary class to create searchCriterias
 * 
 * @author Toni Turunen
 *
 */
public class WarrantSearchParser {
	
	private static final String SENDER_UID 			= "senderUid";
	private static final String RECIEVER_UID 		= "recieverUid";
	private static final String TARGET_PERSON_UID 	= "targetPersonUid";
	private static final String TEMPLATE_ID 		= "templateId";
	private static final String TEMPLATE_NAME		= "templateName";
	private static final String FILTER				= "filter";
	protected static final String SPLIT_REGEX 				= "\\|";


	public enum Mode {
		USER_ID,
		TEMPLATE_NAME
	}
	
	private final WarrantSearchCriteria searchCriteria;
	
	public WarrantSearchParser(final Mode mode, final String keyword) {
		switch(mode) {
		case TEMPLATE_NAME:
			this.searchCriteria = new WarrantSearchCriteriaImpl(createTemplateSearchMap(keyword));
			break;
		case USER_ID:
			this.searchCriteria = new WarrantSearchCriteriaImpl(createSearchCitizenSearchMap(keyword));
			break;
		default:
			this.searchCriteria = new WarrantSearchCriteriaImpl();
		}
	}
	
	/**
	 * @return searchCriteria
	 */
	public final WarrantSearchCriteria getSearchCriteria() {
		return searchCriteria;
	}
	
	private Map<String, String> createSearchCitizenSearchMap(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
				return null;
		}
		
		Map<String, String> searchMap = new HashMap<String, String>(5);
		String[] split = keyword.split(SPLIT_REGEX);
		if (split.length == 0) {
			return null;
		}
		// FIXME: Not like this..  
		for (int i = 0; i < split.length; i++) {
			if (i == 0) {
				searchMap.put(RECIEVER_UID, split[i]);				
			}
			if (i == 1) {
				searchMap.put(SENDER_UID, split[i]);				
			}
			if (i == 2) {
				searchMap.put(TARGET_PERSON_UID, split[i]);				
			}
			if (i == 3) {
				searchMap.put(TEMPLATE_ID, split[i]);				
			}
		}
		return searchMap;
	}
	
	private Map<String, String> createTemplateSearchMap(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
				return null;
		}
		
		Map<String, String> searchMap = new HashMap<String, String>(5);
		String[] split = keyword.split(SPLIT_REGEX);
		if (split.length == 0) {
			return null;
		}
		// FIXME: Not like this..  
		for (int i = 0; i < split.length; i++) {
			if (i == 0) {
				searchMap.put(TEMPLATE_NAME, split[i]);				
			}
			if (i == 1) {
				searchMap.put(FILTER, split[i]);				
			}
		}
		return searchMap;
	}	
	
	private static class WarrantSearchCriteriaImpl implements WarrantSearchCriteria {
		
		private final Map<String, String> searchMap;
		
		public WarrantSearchCriteriaImpl() {
			this.searchMap = Collections.emptyMap();
		}
		
		public WarrantSearchCriteriaImpl(Map<String, String> searchMap) {
			if (searchMap == null) {
				this.searchMap = Collections.emptyMap();
			} else {
				this.searchMap = searchMap;				
			}
		}

		@Override
		public Long getAuthorizationTemplateId() {
			try {
				final String templateId = searchMap.get(TEMPLATE_ID);
				if (templateId == null) {
					return null;
				}
				return Long.valueOf(templateId);
			} catch (NumberFormatException nfe) {
				return null;
			}
		}
	
		@Override
		public String getRecipientUid() {
			return searchMap.get(RECIEVER_UID);
		}
	
		@Override
		public String getSenderUid() {
			return searchMap.get(SENDER_UID);
		}
	
		@Override
		public String getTargetPersonUid() {
			return searchMap.get(TARGET_PERSON_UID);
		}
	
		@Override
		public String getTemplateName() {
			return searchMap.get(TEMPLATE_NAME);
		}
	}
}
