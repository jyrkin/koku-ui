package fi.arcusys.koku.web.util.impl.criteria;

import org.apache.log4j.Logger;

import fi.arcusys.koku.common.services.facades.employee.EmployeeConsentTasks.ConsentSearchCriteria;

/**
 * Temporary class to create searchCriterias
 * 
 * @author Toni Turunen
 *
 */
public class ConsentCriteriaSearchImpl implements ConsentSearchCriteria {
	
	private static final Logger LOG = Logger.getLogger(ConsentCriteriaSearchImpl.class);

	private final String recipientUid;
	private Long templateId;
	
	public ConsentCriteriaSearchImpl(String keyword, String field) {
		
		if (keyword != null && !keyword.isEmpty()) {
			this.recipientUid = keyword;			
		} else {
			this.recipientUid = null;
		}
		
		if(field.trim().length() > 0) {
			try {
				long value = Long.parseLong(field);
				this.templateId = (value >= 0) ? value : null;
			} catch (NumberFormatException nfe) {
				LOG.warn("Invalid field. Creating criteria for consent filtering failed. Field: '"+field+"'");
				return;
			}
		}
	}

	@Override
	public Long getConsentTemplateId() {
		return templateId;
	}

	@Override
	public String getRecipientUid() {
		return recipientUid;
	}
	
	

}
