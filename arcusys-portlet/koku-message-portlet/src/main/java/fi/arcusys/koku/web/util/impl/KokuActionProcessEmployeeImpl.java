package fi.arcusys.koku.web.util.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.koku.av.AvEmployeeServiceHandle;
import fi.arcusys.koku.exceptions.KokuServiceException;
import fi.arcusys.koku.web.util.exception.KokuActionProcessException;

public class KokuActionProcessEmployeeImpl extends AbstractKokuActionProcess {
	
	private static final Logger LOG = LoggerFactory.getLogger(KokuActionProcessEmployeeImpl.class);
	
	/* Lazily initialized */
	private AvEmployeeServiceHandle avEmployeeServiceHandle = null;

	public KokuActionProcessEmployeeImpl(String userId) {
		super(userId);
	}

	@Override
	public void cancelAppointments(String[] appointmentIds, String[] targetPersons, String comment) throws KokuActionProcessException {
		
		if (appointmentIds == null) {
			throw new KokuActionProcessException("AppoimentsIds parameter is null");
		}
		
		if (targetPersons != null) {
			LOG.warn("TargetPersons is not null. This might indicate bug somewhere in code, because Employee AppoimentWS doesn't require it.");			
		}
		
		if (avEmployeeServiceHandle == null) {
			avEmployeeServiceHandle = new AvEmployeeServiceHandle();
		}

		final int appointments = appointmentIds.length;
		String appointmentId;
		for (int i=0; i < appointments; i++ ) {
			appointmentId = appointmentIds[i];
			long  appId = 0;
			try {
				appId = (long) Long.parseLong(appointmentId);
				avEmployeeServiceHandle.cancelAppointments(appId, comment);
			} catch (NumberFormatException nfe) {
				throw new KokuActionProcessException("Invalid appointmentId. AppointmentId: '"+appointmentId+"' comment: '"+comment+"'", nfe);
			} catch (KokuServiceException e) {
				throw new KokuActionProcessException("Failed to cancelAppointment! appoimentId: '" + 
						appointmentId + "' comment: '" + comment + "'", e);
			}
		}
	}

	@Override
	public void revokeWarrants(String[] warrantIds, String comment)
			throws KokuActionProcessException {
		throw new KokuActionProcessException("Not implemented. Employee can't revoke warrant");
	}

	@Override
	public void revokeConsents(String[] consentIds)
			throws KokuActionProcessException {
		throw new KokuActionProcessException("Not implemented. Employee can't revoke consent");		
	}	
}
