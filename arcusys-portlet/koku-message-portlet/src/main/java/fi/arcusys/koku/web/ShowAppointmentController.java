package fi.arcusys.koku.web;

import static fi.arcusys.koku.common.util.Constants.ATTR_APPOIMENT_ID;
import static fi.arcusys.koku.common.util.Constants.ATTR_MY_ACTION;
import static fi.arcusys.koku.common.util.Constants.ATTR_TARGET_PERSON;
import static fi.arcusys.koku.common.util.Constants.MY_ACTION_SHOW_APPOINTMENT;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_APPOINTMENT_RESPONSE_CITIZEN;
import static fi.arcusys.koku.common.util.Constants.TASK_TYPE_APPOINTMENT_RESPONSE_EMPLOYEE;
import static fi.arcusys.koku.common.util.Constants.VIEW_SHOW_CITIZEN_APPOINTMENT;
import static fi.arcusys.koku.common.util.Constants.VIEW_SHOW_EMPLOYEE_APPOINTMENT;

import javax.annotation.Resource;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fi.arcusys.koku.common.exceptions.KokuServiceException;
import fi.arcusys.koku.common.services.appointments.citizen.AvCitizenServiceHandle;
import fi.arcusys.koku.common.services.appointments.employee.AvEmployeeServiceHandle;
import fi.arcusys.koku.common.services.appointments.model.KokuAppointment;
import fi.arcusys.koku.common.services.facades.impl.ResponseStatus;
import fi.arcusys.koku.common.util.Constants;
import fi.arcusys.koku.common.util.Properties;
import fi.arcusys.koku.web.util.ModelWrapper;
import fi.arcusys.koku.web.util.impl.ModelWrapperImpl;


/**
 * Shows appointment page and store the current query information on the jsp page
 * @author Jinhua Chen
 * Aug 4, 2011
 */
@Controller("singleAppointmentController")
@RequestMapping(value = "VIEW")
public class ShowAppointmentController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ShowAppointmentController.class);
	
	@Resource
	private ResourceBundleMessageSource messageSource;
	
	@ActionMapping(params = "action=toAppointment")
	public void actionPageView(
			PortletSession session,
			@ModelAttribute(value = "appointment") ModelWrapper<KokuAppointment> appointment,
			@RequestParam(value = "appointmentId") String appointmentId,
			@RequestParam(value = "targetPerson", required=false) String targetPerson,
			ActionResponse actionResponse) {
		actionResponse.setRenderParameter(ATTR_MY_ACTION, MY_ACTION_SHOW_APPOINTMENT);
		actionResponse.setRenderParameter(ATTR_APPOIMENT_ID, appointmentId);
		actionResponse.setRenderParameter( ATTR_TARGET_PERSON, targetPerson);
	}
	
	/**
	 * Shows the page that presents appointment in detail for either employee
	 * or citizen according to task type
	 * @param taskType task type requested
	 * @param response RenderResponse
	 * @return appointment page
	 */
	@RenderMapping(params = "myaction=showAppointment")
	public String showPageView(RenderResponse response) {

		String page = VIEW_SHOW_CITIZEN_APPOINTMENT;
		
		if(Properties.IS_KUNPO_PORTAL) {
			page = VIEW_SHOW_CITIZEN_APPOINTMENT;
		} else if(Properties.IS_LOORA_PORTAL) {
			page = VIEW_SHOW_EMPLOYEE_APPOINTMENT;
		}
		return page;
	}
		
	/**
	 * Creates data model integrated into the page and stores the page
	 * parameters in the session
	 * @param appointmentId appointment id
	 * @param currentPage current page id
	 * @param taskType task type requested
	 * @param keyword page parameter keyword
	 * @param orderType page parameter order type
	 * @param request RenderRequest
	 * @return appointment data model
	 */
	@ModelAttribute(value = "appointment")
	public ModelWrapper<KokuAppointment> model(
			@RequestParam String appointmentId,
			@RequestParam(value = "targetPerson", required = false) String targetPerson,
			PortletSession portletSession) {	
		
		ModelWrapper<KokuAppointment> model = null;		
		KokuAppointment app = null;
		try {
			if (Properties.IS_KUNPO_PORTAL) {
				AvCitizenServiceHandle handle = new AvCitizenServiceHandle(messageSource);
				app = handle.getAppointmentById(appointmentId, targetPerson);
			} else if(Properties.IS_LOORA_PORTAL) {
				AvEmployeeServiceHandle handle = new AvEmployeeServiceHandle(messageSource);
				app = handle.getAppointmentById(appointmentId);
			} else {
				throw new KokuServiceException("PortalMode missing?");
			}
			model = new ModelWrapperImpl<KokuAppointment>(app);
		} catch (KokuServiceException e) {
			LOG.error("Failed to show appointment details. appointmentId: '"+appointmentId + 
					"' username: '"+ (String)portletSession.getAttribute(Constants.ATTR_USERNAME) +"'", e);
			model = new ModelWrapperImpl<KokuAppointment>(null, ResponseStatus.FAIL, e.getErrorcode());
		}
		return model;
	}	
	
}
