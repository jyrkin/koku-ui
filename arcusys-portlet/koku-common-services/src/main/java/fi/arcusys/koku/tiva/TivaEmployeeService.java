package fi.arcusys.koku.tiva;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import fi.arcusys.koku.tiva.employeeservice.ConsentCriteria;
import fi.arcusys.koku.tiva.employeeservice.ConsentQuery;
import fi.arcusys.koku.tiva.employeeservice.ConsentSummary;
import fi.arcusys.koku.tiva.employeeservice.ConsentTO;
import fi.arcusys.koku.tiva.employeeservice.KokuLooraSuostumusService_Service;
import fi.arcusys.koku.tiva.employeeservice.SuostumuspohjaShort;
import fi.arcusys.koku.users.KokuUserService;
import fi.arcusys.koku.util.PropertiesUtil;
import fi.koku.settings.KoKuPropertiesUtil;

/**
 * Retrieves Tiva consent data and related operations via web services
 * @author Jinhua Chen
 * Aug 15, 2011
 */
public class TivaEmployeeService {
		
	private static final Logger LOG = Logger.getLogger(TivaEmployeeService.class);		
	public static final URL TIVA_EMPLOYEE_WSDL_LOCATION;
	
	static {
		try {
			LOG.info("TivaEmployeeService WSDL location: " + KoKuPropertiesUtil.get("TivaEmployeeService"));
			TIVA_EMPLOYEE_WSDL_LOCATION =  new URL(KoKuPropertiesUtil.get("TivaEmployeeService"));
		} catch (MalformedURLException e) {
			LOG.error("Failed to create TivaEmployeeService WSDL url! Given URL address is not valid!");
			throw new ExceptionInInitializerError(e);
		}
	}
		
	private KokuLooraSuostumusService_Service kls;
	
	/**
	 * Constructor and initialization
	 */
	public TivaEmployeeService() {
		this.kls = new KokuLooraSuostumusService_Service(TIVA_EMPLOYEE_WSDL_LOCATION);
	}
	
	/**
	 * Gets consents
	 * @param user user name
	 * @param query query for filtering consents
	 * @return a list of summary consents
	 */
	public List<ConsentSummary> getConsents(String user, ConsentQuery query) {
		try {
			return kls.getKokuLooraSuostumusServicePort().getConsents(user, query);
		} catch(RuntimeException e) {
			return null;
		}
	}
	
	/**
	 * Gets consent in detail
	 * @param consentId consent id
	 * @return detailed consent
	 */
	public ConsentTO getConsentDetails(long consentId) {
		return kls.getKokuLooraSuostumusServicePort().getConsentDetails(consentId);
	}
	
	/**
	 * Gets total number of consents
	 * @param user user name
	 * @param query query for filtering consents
	 * @return the total number of consents
	 */
	public int getTotalConsents(String user, ConsentCriteria criteria) {
		return kls.getKokuLooraSuostumusServicePort().getTotalConsents(user, criteria);
	}
	
	/**
	 * Searches the consent templates
	 * @param searchString searching keyword
	 * @param limit limited number of results
	 * @return a list of templates
	 */
	public List<SuostumuspohjaShort> searchConsentTemplates(String searchString, int limit) {
		return kls.getKokuLooraSuostumusServicePort().searchConsentTemplates(searchString, limit);
	}
}