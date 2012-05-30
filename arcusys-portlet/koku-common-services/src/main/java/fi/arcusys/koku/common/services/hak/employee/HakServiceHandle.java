package fi.arcusys.koku.common.services.hak.employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.MessageSource;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.AbstractHandle;
import fi.arcusys.koku.common.services.appointments.model.KokuAppointment;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.impl.ResultListImpl;
import fi.arcusys.koku.common.services.facades.employee.EmployeeApplicationsTasks;
import fi.arcusys.koku.common.services.facades.employee.EmployeeAppointmentTasks;
import fi.arcusys.koku.common.services.hak.model.KokuApplicationSummary;
import fi.arcusys.koku.common.services.hak.model.KokuKindergarten;


/**
 * Mockup service handle
 * 
 * 
 * @author Toni Turunen
 *
 */
public class HakServiceHandle extends AbstractHandle implements EmployeeApplicationsTasks {
	
	
	private static final List<KokuApplicationSummary> applicants = new ArrayList<KokuApplicationSummary>(); 
	private static final List<KokuKindergarten> kindergartens = new ArrayList<KokuKindergarten>();
	
	// Mockup data
	static {
		KokuKindergarten kindergarten = new KokuKindergarten();
		String name = "Paiholan päiväkoti";
		kindergarten.setName(name);
		kindergarten.setKindergartenId(1);
		kindergartens.add(kindergarten);
		
		kindergarten = new KokuKindergarten();
		kindergarten.setName("Paimion päiväkoti");
		kindergarten.setKindergartenId(2);
		kindergartens.add(kindergarten);
		
		KokuApplicationSummary lassi = new KokuApplicationSummary();
		lassi.setApplicantName("Lassi Lapsi");
		lassi.setApplicantGuardianName("Kirsi Kuntalainen");
		lassi.setKindergartenName("Paiholan päiväkoti");
		lassi.setPlaceAccepted("Kyllä");
		lassi.setApplicantAccepted("Ei");
		lassi.setCreatedAt("23.9.2011");
		lassi.setAnsweredAt("24.9.2011");
		lassi.setInEffectAt("25.9.2011");
		lassi.setApplicationId(1);
		applicants.add(lassi);
		
		KokuApplicationSummary liisa = new KokuApplicationSummary();
		liisa.setApplicantName("Liisa Lapsi");
		liisa.setApplicantGuardianName("Kirsi Kuntalainen");
		liisa.setKindergartenName("Paimion päiväkoti");
		liisa.setPlaceAccepted("Kyllä");
		liisa.setApplicantAccepted("Kyllä");
		liisa.setCreatedAt("23.9.2011");
		liisa.setAnsweredAt("24.9.2011");
		liisa.setInEffectAt("25.9.2011");
		lassi.setApplicationId(2);
		applicants.add(liisa);
	}
	
	public HakServiceHandle(MessageSource messageSource) {
		super(messageSource);
	}
	
	@Override
	public ResultList<KokuAppointment> getApplications(String uid,
			ApplicationSearchCriteria criteria, Page page)
			throws KokuServiceException {
		return new ResultListImpl<KokuAppointment>(new ArrayList<KokuAppointment>(), 0, page);
	}
	
	private List<KokuApplicationSummary> getApplicants(String userId, String keyword, int startNum, int maxNum) {		
		return new ArrayList<KokuApplicationSummary>();
	}
	
	private int getTotalRequestedApplicants(String userId, String keyword) {
		return 0;
	}

	public List<KokuApplicationSummary> searchKindergartenByName(String keyword, int maxSuggestionResults) {
		List<KokuApplicationSummary> results = new ArrayList<KokuApplicationSummary>();
		for (KokuApplicationSummary applicant : applicants) {
			if (applicant.getKindergartenName().startsWith(keyword)) {
				results.add(applicant);
			}
		}
		// return results;
		return new ArrayList<KokuApplicationSummary>();
	}

	public KokuApplicationSummary getApplicantDetails(String applicationId) {
		Integer id = Integer.valueOf(applicationId);
		for (KokuApplicationSummary application : applicants) {
			if (application.getApplicationId() != null && application.getApplicationId().equals(id)) {
				return application;
			}
		}
		return null;
	}



	
}
