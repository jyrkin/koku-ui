package fi.arcusys.koku.web.util.impl;

import static fi.arcusys.koku.common.util.Constants.RESPONSE_FAIL;

import java.util.Arrays;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.appointments.citizen.AvCitizenServiceHandle;
import fi.arcusys.koku.common.services.consents.citizen.TivaCitizenServiceHandle;
import fi.arcusys.koku.common.services.warrants.citizens.KokuCitizenWarrantHandle;
import fi.arcusys.koku.web.util.exception.KokuActionProcessException;

public class KokuActionProcessCitizenImpl extends AbstractKokuActionProcess {

	/* Lazily initialized */
	private AvCitizenServiceHandle avCitizenServiceHandle = null;
	private KokuCitizenWarrantHandle warrantHandle = null;
	private TivaCitizenServiceHandle tivaHandle = null;

	public KokuActionProcessCitizenImpl(String userId) {
		super(userId);
	}

	@Override
	public void cancelAppointments(String[] appointmentIds, String[] targetPersons, String comment) throws KokuActionProcessException {

		if (appointmentIds == null || targetPersons == null) {
			throw new KokuActionProcessException("AppoimentsId or targetPersons parameter(s) are null");
		}

		/* Lazy services loading */
		if (avCitizenServiceHandle == null) {
			avCitizenServiceHandle = new AvCitizenServiceHandle(DUMMY_MSG_SOURCE, getUserId());
		}

		final int appointments = appointmentIds.length;
		final int targetPersonCount = targetPersons.length;

		if (appointments != targetPersonCount) {
			throw new KokuActionProcessException("Appointments and targetPerson count doesn't match! appointments: '"+
					Arrays.toString(appointmentIds)+"' targetPersons: '"+Arrays.toString(targetPersons)+"'");
		}

		String appointmentId;
		String targetPerson;
		for (int i=0; i < appointments; i++ ) {
			appointmentId = appointmentIds[i];
			targetPerson = targetPersons[i];
			long  appId = 0;
			try {
				appId = (long) Long.parseLong(appointmentId);
				avCitizenServiceHandle.cancelAppointments(appId, targetPerson, comment);
			} catch (NumberFormatException nfe) {
				throw new KokuActionProcessException("Invalid appointmentId. AppointmentId: '"+appointmentId+"' targetPerson: '"+targetPerson+"' comment: '"+comment+"'", nfe);
			} catch (KokuServiceException e) {
				throw new KokuActionProcessException("Failed to cancelAppointment! appoimentId: '" +
						appointmentId + "' targetPerson: '" + targetPerson + "' comment: '" + comment + "'", e);
			}
		}
	}

	@Override
	public void revokeWarrants(String[] warrantIds, String comment)
			throws KokuActionProcessException {

		if (warrantIds == null) {
			throw new KokuActionProcessException("warrantIds parameter is null");
		}

		/* Lazy initialization */
		if (warrantHandle == null) {
			warrantHandle = new KokuCitizenWarrantHandle(DUMMY_MSG_SOURCE);
		}

		for(String authorizationId : warrantIds) {
			try {
				long authId = Long.parseLong(authorizationId);
				warrantHandle.revokeOwnAuthorization(authId, getUserId(), comment);
			} catch (NumberFormatException nfe) {
				throw new KokuActionProcessException("Couldn't revoke warrant! Invalid authorizationId. UserId: "+ getUserId() + " AuthorizationId: "+ authorizationId + "Comment: " + comment, nfe);
			} catch (KokuServiceException kse) {
				throw new KokuActionProcessException("Revoking warrant failed! authId: '"+authorizationId+"' userId: '"+getUserId()+"' comment:'"+comment+"'");
			}
		}
	}

	@Override
	public void revokeConsents(String[] consentIds)
			throws KokuActionProcessException {

		if (consentIds == null) {
			throw new KokuActionProcessException("consentIds parameter is null");
		}
		if (tivaHandle == null) {
			tivaHandle = new TivaCitizenServiceHandle(DUMMY_MSG_SOURCE, getUserId());
		}
		try {
			for(String consentId : consentIds) {
				tivaHandle.revokeOwnConsent(consentId);
			}
		} catch (KokuServiceException e) {
			throw new KokuActionProcessException("Failed to revoke consent(s). ConsentIds: '"+Arrays.toString(consentIds)+"'", e);
		}
	}

	@Override
	public void disableAppointmentSlot(long appointmentId, int slotNumber)
			throws KokuActionProcessException {
		throw new KokuActionProcessException("Not implemented for citizen.");
	}
}
