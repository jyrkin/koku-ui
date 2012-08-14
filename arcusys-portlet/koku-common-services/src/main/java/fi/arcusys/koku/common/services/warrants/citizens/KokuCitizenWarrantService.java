package fi.arcusys.koku.common.services.warrants.citizens;

import java.util.List;

import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.util.Properties;
import fi.arcusys.koku.tiva.warrant.citizenwarrantservice.AuthorizationShortSummary;
import fi.arcusys.koku.tiva.warrant.citizenwarrantservice.AuthorizationSummary;
import fi.arcusys.koku.tiva.warrant.citizenwarrantservice.KokuKunpoValtakirjaService;
import fi.arcusys.koku.tiva.warrant.citizenwarrantservice.KokuKunpoValtakirjaService_Service;

/**
 * WS client to get citizen warrants
 *
 * @author Toni Turunen
 *
 */
public class KokuCitizenWarrantService {

	private static final Logger LOG = LoggerFactory.getLogger(KokuCitizenWarrantService.class);

	private final KokuKunpoValtakirjaService service;

	public KokuCitizenWarrantService() {
		final KokuKunpoValtakirjaService_Service serviceInit = new KokuKunpoValtakirjaService_Service();
		service = serviceInit.getKokuKunpoValtakirjaServicePort();
		((BindingProvider)service).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, Properties.KOKU_KUNPO_VALTAKIRJA_SERVICE);
	}

	/**
	 * Returns authorization (warrant) by given Id
	 *
	 * @param authorizationId
	 * @param userId
	 * @return Authorization
	 * @throws KokuServiceException
	 */
	public AuthorizationSummary getAuthorizationSummaryById(long authorizationId, String userId) throws KokuServiceException {
		try {
			return service.getAuthorizationSummaryById(authorizationId, userId);
		} catch(RuntimeException e) {
			throw new KokuServiceException("getAuthorizationSummaryById failed. userId: '"+userId+"' authorizationId: '"+authorizationId+"'", e);
		}
	}

	/**
	 * Returns list of recieved authorizations by given user
	 *
	 * @param userId
	 * @param startNum
	 * @param maxNum
	 * @return list of recieved authorizations
	 * @throws KokuServiceException
	 */
	public List<AuthorizationShortSummary> getReceivedAuthorizations(String userId, int startNum, int maxNum) throws KokuServiceException {
		try {
			return service.getReceivedAuthorizations(userId, startNum, maxNum);
		} catch(RuntimeException e) {
			throw new KokuServiceException("getReceivedAuthorizations failed. userId: '"+userId+"'", e);
		}
	}

	/**
	 * Returns list of sent authorizations by given user
	 *
	 * @param userId
	 * @param startNum
	 * @param maxNum
	 * @return list of sent authorizations
	 * @throws KokuServiceException
	 */
	public List<AuthorizationShortSummary> getSentAuthorizations(String userId, int startNum, int maxNum) throws KokuServiceException {
		try {
			return service.getSentAuthorizations(userId, startNum, maxNum);
		} catch(RuntimeException e) {
			throw new KokuServiceException("getSentAuthorizations failed. userId: '"+userId+"'", e);
		}
	}

	/**
	 * Returns number of sent authorizations by given user
	 *
	 * @param userId
	 * @return number of sent authorizations
	 * @throws KokuServiceException
	 */
	public int getTotalSentAuthorizations(String userId) throws KokuServiceException {
		try {
			return service.getTotalSentAuthorizations(userId);
		} catch(RuntimeException e) {
			throw new KokuServiceException("getTotalSentAuthorizations failed. userId: '"+userId+"'", e);
		}
	}

	/**
	 * Returns number of received authorizations by given userId
	 *
	 * @param userId
	 * @return number of received authorizations
	 * @throws KokuServiceException
	 */
	public int getTotalReceivedAuthorizations(String userId) throws KokuServiceException {
		try {
			return service.getTotalReceivedAuthorizations(userId);
		} catch(RuntimeException e) {
			throw new KokuServiceException("getTotalReceivedAuthorizations failed. userId: '"+userId+"'", e);
		}
	}

	/**
	 * Revoke sent authorization
	 *
	 * @param authorizationId
	 * @param user
	 * @param comment
	 */
	public void revokeOwnAuthorization(long authorizationId, String user, String comment) throws KokuServiceException {
		try {
			service.revokeOwnAuthorization(authorizationId, user, comment);
		} catch (RuntimeException e) {
			throw new KokuServiceException("Warrant revoking authorization failed. authorizationId: '"+authorizationId
					+ "' user: '"+user+"' comment: '"+comment+"'");
		}
	}
}

