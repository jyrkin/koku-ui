package fi.koku.lok;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fi.koku.services.entity.authorizationinfo.util.AuthUtils;
import fi.koku.services.entity.authorizationinfo.v1.AuthorizationInfoService;
import fi.koku.services.entity.authorizationinfo.v1.impl.AuthorizationInfoServiceDummyImpl;
import fi.koku.services.entity.authorizationinfo.v1.model.Role;
import fi.koku.services.entity.person.v1.PersonService;
import fi.koku.services.utility.log.v1.AuditInfoType;
import fi.koku.services.utility.log.v1.LogEntriesType;
import fi.koku.services.utility.log.v1.LogEntryType;
import fi.koku.services.utility.log.v1.LogQueryCriteriaType;
import fi.koku.services.utility.log.v1.LogServiceFactory;
import fi.koku.services.utility.log.v1.LogServicePortType;
import fi.koku.services.utility.log.v1.ServiceFault;

/**
 * Controller for viewing log views, for admin. This implements use case LOK-4
 * (Tarkista lokin käsittelyloki).
 * 
 * @author makinsu
 */
@Controller
@RequestMapping(value = "VIEW")
public class LogViewController {

  private static final Logger log = LoggerFactory.getLogger(LogViewController.class);

  // Use log service
  private LogServicePortType logService;
  private PersonService personService;

  private AuthorizationInfoService authorizationInfoService;

  private CriteriaSerializer criteriaSerializer = new CriteriaSerializer();
  private SimpleDateFormat dateFormat = new SimpleDateFormat(LogConstants.DATE_FORMAT);

  LogUtils lu = new LogUtils();

  public LogViewController() {
    LogServiceFactory logServiceFactory = new LogServiceFactory(LogConstants.LOG_SERVICE_USER_ID,
        LogConstants.LOG_SERVICE_PASSWORD, LogConstants.LOG_SERVICE_ENDPOINT);
    logService = logServiceFactory.getLogService();
    log.debug("Got logService!");

    authorizationInfoService = new AuthorizationInfoServiceDummyImpl();
    personService = new PersonService();
  }

  // customize form data binding
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    dateFormat.setLenient(false);
    CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
    binder.registerCustomEditor(Date.class, dateEditor);
  }

  // initialize form backing objects
  @ModelAttribute("logSearchCriteria")
  public LogSearchCriteria getCommandObject() {
    return new LogSearchCriteria();
  }

  // portlet render phase
  @RenderMapping(params = "action=viewLog")
  public String render(PortletSession session, RenderRequest req,
      @RequestParam(value = "visited", required = false) String visited,
      @ModelAttribute(value = "logSearchCriteria") LogSearchCriteria criteria,
      //@RequestParam(value = "originalFrom", required = false) String origFrom,
      //@RequestParam(value = "originalTo", required = false) String origTo, 
      RenderResponse res, Model model) {

    // get user pic and role
    String userPic = LogUtils.getPicFromSession(session);

    List<Role> userRoles = authorizationInfoService.getUsersRoles(LogConstants.COMPONENT_LOK, userPic);

    // add a flag for allowing this user to see the operations on page
    // search.jsp
    if (AuthUtils.isOperationAllowed("ViewAdminLogFile", userRoles)) {
      model.addAttribute("allowedToView", true);
    }

    if (visited == null) {
      // these are runtime constants, not given by the user!
      String startDateStr = lu.getDateString(1);
      String endDateStr = lu.getDateString(0);
      model.addAttribute("startDate", startDateStr);
      model.addAttribute("endDate", endDateStr);
    }

    if (criteria != null) {
      if (visited != null) {

        // Check that the input parameters are not null and in the correct
        // format
        String[] errors = lu.checkInputParameters(criteria, LogConstants.LOG_ADMIN);
        model.addAttribute("error0", errors[0]);
        model.addAttribute("error1", errors[1]);
        model.addAttribute("error3", errors[3]);
        
        if (errors[0] == null && errors[1] == null && errors[3] == null) {

          try {
            // make the query to the admin log
            List<AdminLogEntry> entries = getAdminLogEntries(criteria, userPic);
            // The user's name (not pic as in the database) should be shown,
            // so change pics to names
            lu.changePicsToNamesAdmin(entries, userPic, personService);

            model.addAttribute("entries", entries);

          } catch (ServiceFault fault) { // error contacting the Log Service
            model.addAttribute("error", "koku.lok.error.viewlog");
          }
          model.addAttribute("searchParams", criteria);
          log.debug("criteria: " + criteria.getFrom() + ", " + criteria.getTo());
          model.addAttribute("visited", "---");
          model.addAttribute("logSearchCriteria", criteria);
        } /*else {
          // if a field could not have been formatted to a date, put here the
          // original string
          // to show the user

  //        log.debug("origFrom: " + origFrom + ", from: " + criteria.getFrom());
   //       log.debug("origTo: " + origTo + ", to: " + criteria.getTo());

 /*         if (criteria.getFrom() == null) {
            model.addAttribute("startDate", origFrom);
          } else {
            model.addAttribute("startDate", criteria.getFrom());
          }
          if (criteria.getTo() == null) {
            model.addAttribute("endDate", origTo);
          } else {
            model.addAttribute("endDate", criteria.getTo());
          }
        }*/
        // model.addAttribute("logSearchCriteria", criteria);
      }

    } else {
      log.debug("criteria: null");
    }

    return "view";
  }

  // portlet action phase
  @ActionMapping(params = "action=viewLog")
  public void doSearchArchive(@ModelAttribute(value = "logSearchCriteria") LogSearchCriteria criteria,
      BindingResult result, @RequestParam(value = "visited") String visited, ActionResponse response) {

    if (visited != null) {
      response.setRenderParameter("visited", visited);
    }

    log.debug("action criteria = " + criteria);

   
    // If something goes wrong in serializing the criteria, the portlet must not
    // die
    // and the portlet must not query the log service
    try {
 /*     // save the from and to fields to show the user in case they cannot be
      // formatted
      if (criteria.getFrom() != null) {
        response.setRenderParameter("originalFrom", criteria.getFrom().toString());
      }
      if (criteria.getTo() != null) {
        response.setRenderParameter("originalTo", criteria.getTo().toString());
      }
   */
      response.setRenderParameter("logSearchCriteria", criteriaSerializer.getAsText(criteria));

    } catch (IllegalArgumentException e) {
      log.error("illegal argument");
    }

    response.setRenderParameter("action", "viewLog");
  }

  /**
   * Method for reading log entries in the 'log of logs'
   * 
   * @param criteria
   * @return
   */
  private List<AdminLogEntry> getAdminLogEntries(LogSearchCriteria criteria, String userPic) throws ServiceFault {
    List<AdminLogEntry> entryList = new ArrayList<AdminLogEntry>();

    // try {

    LogQueryCriteriaType criteriatype = new LogQueryCriteriaType();

    // the user does not have to give the dates so these might be null
    Calendar start = Calendar.getInstance();
    if (criteria.getFrom() == null) {
      start = null;
    } else {
      start.setTime(criteria.getFrom());
    }
    Calendar end = Calendar.getInstance();
    if (criteria.getTo() == null) {
      end = null;
    } else {
      end.setTime(criteria.getTo());
    }

    // assume that also null arguments are ok!! TODO: ota huomioon
    // kantakyselyissä Vai onko pakolliset?
    criteriatype.setStartTime(start);
    criteriatype.setEndTime(end);
    criteriatype.setLogType(LogConstants.LOG_ADMIN);

    // Set the user information
    AuditInfoType audit = new AuditInfoType();
    audit.setComponent(LogConstants.COMPONENT_LOK);
    audit.setUserId(userPic);

    log.debug("criteriatype start: " + criteriatype.getStartTime() + "\n end: " + criteriatype.getEndTime());
    if (criteriatype.getStartTime() == null || criteriatype.getEndTime() == null) {
      log.debug("null-arvoja kriteriassa");
    } else {

      // call to lok service
      LogEntriesType entriestype = logService.opQueryLog(criteriatype, audit);

      // get the log entries list from the database
      List<LogEntryType> entryTypeList = entriestype.getLogEntry();

      log.debug("entryTypeList size: " + entryTypeList.size());

      for (Iterator<?> i = entryTypeList.iterator(); i.hasNext();) {
        AdminLogEntry logEntry = new AdminLogEntry();
        LogEntryType logEntryType = (LogEntryType) i.next();

        log.debug("got from service timestamp: " + logEntryType.getTimestamp().get(Calendar.HOUR_OF_DAY) + ":"
            + logEntryType.getTimestamp().get(Calendar.MINUTE) + ":" + logEntryType.getTimestamp().get(Calendar.SECOND));
        // put values that were read from the database in logEntry for showing
        // them to the user
        logEntry.setTimestamp(logEntryType.getTimestamp().getTime());
        logEntry.setUser(logEntryType.getUserPic());
        logEntry.setOperation(logEntryType.getOperation()); // read, write, ..

        // pic of the child
        logEntry.setCustomer(logEntryType.getCustomerPic());
        // kks.vasu, kks.4v, ..

        // other info about the log entry
        logEntry.setMessage(logEntryType.getMessage());

        entryList.add(logEntry);
      }

      // TODO: Parempi virheenkäsittely

    }
    // } // TODO: Parempi virheenkäsittely
    /*
     * catch (ServiceFault e) { // TODO Auto-generated catch block
     * e.printStackTrace(); }
     */
    return entryList;
  }

  private static class CriteriaSerializer {

    public String[] getAsText(LogSearchCriteria c) {
      String[] text = new String[] {};

      SimpleDateFormat df = new SimpleDateFormat(LogConstants.DATE_FORMAT);

      log.debug("getAsText from: " + c.getFrom());
      if (c != null) {
        text = new String[] { c.getPic(), c.getConcept(), c.getFrom() != null ? df.format(c.getFrom()) : "",
            c.getTo() != null ? df.format(c.getTo()) : "" };
      }
      return text;
    }

    /*
     * public String[] getAsText(LogSearchCriteria c) { SimpleDateFormat df =
     * new SimpleDateFormat(LogConstants.DATE_FORMAT); //
     * log.debug(c.getFrom()+", "+df.format(c.getFrom())); String[] text = new
     * String[] { c.getFrom() != null ? df.format(c.getFrom()) : "", c.getTo()
     * != null ? df.format(c.getTo()) : "" };
     * 
     * return text; }
     */
  }
}