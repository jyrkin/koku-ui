package fi.arcusys.koku.common.services.appointments.citizen;

import static fi.arcusys.koku.common.util.Constants.DATE;
import static fi.arcusys.koku.common.util.Constants.TIME;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import fi.arcusys.koku.av.citizenservice.AppointmentRespondedTO;
import fi.arcusys.koku.av.citizenservice.AppointmentSlot;
import fi.arcusys.koku.av.citizenservice.AppointmentSummaryStatus;
import fi.arcusys.koku.av.citizenservice.AppointmentWithTarget;
import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.AbstractHandle;
import fi.arcusys.koku.common.services.appointments.model.CitizenAppointment;
import fi.arcusys.koku.common.services.appointments.model.KokuAppointment;
import fi.arcusys.koku.common.services.appointments.model.Slot;
import fi.arcusys.koku.common.services.facades.Page;
import fi.arcusys.koku.common.services.facades.ResultList;
import fi.arcusys.koku.common.services.facades.citizen.CitizenAppointmentTasks;
import fi.arcusys.koku.common.services.facades.impl.ResultListImpl;
import fi.arcusys.koku.common.services.users.KokuUser;
import fi.arcusys.koku.common.util.MessageUtil;


/**
 * Handles appointments related operations
 * @author Jinhua Chen
 * Aug 22, 2011
 */
public class AvCitizenServiceHandle extends AbstractHandle implements CitizenAppointmentTasks {

	private static final Logger LOG = Logger.getLogger(AvCitizenServiceHandle.class);

	private final AvCitizenService acs;
	private String loginUserId;

	/**
	 * Constructor and initialization
	 */
	public AvCitizenServiceHandle(MessageSource messageSource) {
		super(messageSource);
		acs = new AvCitizenService();
	}

	public AvCitizenServiceHandle(MessageSource messageSource, String loginUser) {
		super(messageSource);
		loginUserId = loginUser;
		acs = new AvCitizenService();
	}

	@Override
	public ResultList<KokuAppointment> getNewAppointments(String uid, Page page)
			throws KokuServiceException {
		final List<KokuAppointment> apps = getAssignedAppointmentsSummary(uid, page);
		final int total = getTotalAssignedAppointmentsNum(uid);
		return new ResultListImpl<KokuAppointment>(apps, total, page);
	}

	@Override
	public ResultList<KokuAppointment> getAnsweredAppointments(String uid,
			Page page) throws KokuServiceException {
		final List<KokuAppointment> apps = getRespondedAppointmentSummary(uid, page);
		final int total = getTotalRespondedAppointmentsNum(uid);
		return new ResultListImpl<KokuAppointment>(apps, total, page);
	}

	@Override
	public ResultList<KokuAppointment> getOldAppointments(String uid, Page page)
			throws KokuServiceException {
		final List<KokuAppointment> apps = getOldAppointmentsSummary(uid, page);
		final int total = getTotalOldAppointmentsNum(uid);
		return new ResultListImpl<KokuAppointment>(apps, total, page);
	}

	private List<KokuAppointment> getAssignedAppointmentsSummary(String uid, Page page) throws KokuServiceException {
		return getPojoAppointments(acs.getAssignedAppointments(uid, page.getFirst(), page.getLast()));
	}

	private List<KokuAppointment> getRespondedAppointmentSummary(String uid, Page page) throws KokuServiceException {
		return getPojoAppointments(acs.getRespondedAppointments(uid, page.getFirst(), page.getLast()));
	}

	private List<KokuAppointment> getOldAppointmentsSummary(String uid, Page page) throws KokuServiceException {
		return getPojoAppointments(acs.getOldAppointments(uid, page.getFirst(), page.getLast()));
	}


	private List<KokuAppointment> getPojoAppointments(List<AppointmentWithTarget> appSummaryList) {
		final List<KokuAppointment> appList = new ArrayList<KokuAppointment>();
		for (AppointmentWithTarget appSummary : appSummaryList) {
			final CitizenAppointment kokuAppointment = new CitizenAppointment();
			kokuAppointment.setAppointmentId(appSummary.getAppointmentId());
			kokuAppointment.setSenderUser(new KokuUser(appSummary.getSenderUserInfo()));
			kokuAppointment.setSubject(appSummary.getSubject());
			kokuAppointment.setDescription(appSummary.getDescription());
			kokuAppointment.setTargetPersonUser(new KokuUser(appSummary.getTargetPersonUserInfo()));
			kokuAppointment.getTargetPersonUser().setUid(appSummary.getTargetPersonUserInfo().getUid());
			kokuAppointment.setStatus(localizeActionRequestStatus(appSummary.getStatus()));
			appList.add(kokuAppointment);
		}
		return appList;
	}

	/**
	 * Gets the appointment in detail
	 * @param appointmentId appointment id
	 * @return detailed citizen appointment
	 */
	public CitizenAppointment getAppointmentById(String appointmentId, String targetUser) throws KokuServiceException {
		long  appId = 0;
		try {
			appId = Long.parseLong(appointmentId);
		} catch (NumberFormatException nfe) {
			throw new KokuServiceException("Invalid appointmentId. AppointmentId: '"+appointmentId+"'", nfe);
		}
		final CitizenAppointment ctzAppointment = new CitizenAppointment();
		AppointmentRespondedTO appointment = acs.getAppointmentRespondedById(appId, targetUser);
		ctzAppointment.setAppointmentId(appointment.getAppointmentId());
		ctzAppointment.setSenderUser(new KokuUser(appointment.getSenderUserInfo()));
		// KOKU-1234 - 'Tapaamiset'-listauksiin tarvitaan tieto kenelle viesti on lähetetty (Työntekijän puoli)
		// ctzAppointment.setReceivingUser(new KokuUser());
		ctzAppointment.setSubject(appointment.getSubject());
		ctzAppointment.setDescription(appointment.getDescription());
		if (appointment.getStatus() != null) {
			ctzAppointment.setStatus(localizeActionRequestStatus(appointment.getStatus()));
		}
		if (appointment.getApprovedSlot() != null) {
			ctzAppointment.setSlot(formatSlot(appointment.getApprovedSlot()));
		}
		ctzAppointment.setReplierUser(new KokuUser(appointment.getReplierUserInfo()));
		ctzAppointment.setReplierComment(appointment.getReplierComment());
		ctzAppointment.setTargetPersonUser(new KokuUser(appointment.getTargetPersonUserInfo()));
		ctzAppointment.setCancellationComment(appointment.getEmployeesCancelComent());
		ctzAppointment.setModifiable(appointment.isModifiable());

		return ctzAppointment;
	}

	public int getTotalAssignedAppointmentsNum(String userId) throws KokuServiceException {
		return acs.getTotalAssignedAppointmentNum(userId);
	}

	private int getTotalRespondedAppointmentsNum(String userId) throws KokuServiceException {
		return acs.getTotalRespondedAppointmentNum(userId);
	}

	private int getTotalOldAppointmentsNum(String userId) throws KokuServiceException {
		return acs.getTotalOldAppointments(userId);
	}

	/**
	 * Formats the slot data model
	 * @param appSlot slot of appointment
	 * @return formatted slot data model
	 */
	private Slot formatSlot(AppointmentSlot appSlot) {
		final TimeZone timeZone = TimeZone.getTimeZone("GMT+0:00");
		Slot slot = new Slot();
		slot.setSlotNumber(appSlot.getSlotNumber());
		slot.setAppointmentDate(MessageUtil.formatDateByString(appSlot.getAppointmentDate(), DATE, timeZone));
		slot.setStartTime(MessageUtil.formatDateByString(appSlot.getStartTime(), TIME, timeZone));
		slot.setEndTime(MessageUtil.formatDateByString(appSlot.getEndTime(), TIME, timeZone));
		slot.setLocation(appSlot.getLocation());
		slot.setDisabled(slot.isDisabled());
		slot.setComment(appSlot.getComment());
		return slot;
	}

	/**
	 * Cancels appointments
	 * @param appointmentId
	 * @param targetPerson
	 * @param comment
	 * @return operation response
	 */
	public void cancelAppointments(long appointmentId, String targetPerson, String comment) throws KokuServiceException {
		acs.cancelAppointment(appointmentId, targetPerson, loginUserId, comment);
	}

	private String localizeActionRequestStatus(AppointmentSummaryStatus appointmentStatus) {
		if (getMessageSource() == null) {
			LOG.warn(MESSAGE_SOURCE_MISSING);
			return appointmentStatus.toString();
		}
		Locale locale = MessageUtil.getLocale();
		try {
			switch (appointmentStatus) {
			case APPROVED:
				return getMessageSource().getMessage("AppointmentStatus.Approved", null, locale);
			case CANCELLED:
				return getMessageSource().getMessage("AppointmentStatus.Cancelled", null, locale);
			case CREATED:
				return getMessageSource().getMessage("AppointmentStatus.Created", null, locale);
			case DECLINED:
				return getMessageSource().getMessage("AppointmentStatus.Declined", null, locale);
			case INVALIDATED:
				return getMessageSource().getMessage("AppointmentStatus.Invalidated", null, locale);
			case NEW:
				return getMessageSource().getMessage("AppointmentStatus.New", null, locale);
			case CLOSED:
				return getMessageSource().getMessage("AppointmentStatus.Closed", null, locale);
			case IN_PROGRESS:
				return getMessageSource().getMessage("AppointmentStatus.InProgress", null, locale);
			default:
				return appointmentStatus.toString();
			}
		} catch (NoSuchMessageException nsme) {
			LOG.warn("Coulnd't find localized message for '" +appointmentStatus +"'. Localization doesn't work properly");
			return appointmentStatus.toString();
		}
	}
}
