package fi.koku.lok;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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

import fi.koku.services.utility.log.v1.AuditInfoType;
import fi.koku.services.utility.log.v1.LogEntriesType;
import fi.koku.services.utility.log.v1.LogEntryType;
import fi.koku.services.utility.log.v1.LogQueryCriteriaType;
import fi.koku.services.utility.log.v1.LogServiceFactory;
import fi.koku.services.utility.log.v1.LogServicePortType;
import fi.koku.services.utility.log.v1.ServiceFault;

import fi.koku.lok.*;

/**
 * Controller for viewing log views, for admin. This implements LOK-4 (Tarkista
 * lokin käsittelyloki).
 * 
 * @author makinsu
 */
@Controller
@RequestMapping(value = "VIEW")
public class LogViewController {

  private static final Logger log = LoggerFactory.getLogger(LogViewController.class);

  // Use log service
  private LogServicePortType logService;
  
  private CriteriaSerializer criteriaSerializer = new CriteriaSerializer();
  private SimpleDateFormat dateFormat = new SimpleDateFormat(LogConstants.DATE_FORMAT);

  LogUtils lu = new LogUtils();

  public LogViewController(){
    LogServiceFactory logServiceFactory = new LogServiceFactory(
        LogConstants.LOG_SERVICE_USER_ID, LogConstants.LOG_SERVICE_PASSWORD,
        LogConstants.LOG_SERVICE_ENDPOINT);
    logService = logServiceFactory.getLogService();    
    log.debug("Got logService!");
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
  public String render(RenderRequest req, @RequestParam(value = "visited", required = false) String visited,
      @ModelAttribute(value = "logSearchCriteria") LogSearchCriteria criteria, RenderResponse res, Model model) {

//    res.setTitle(resourceBundle.getMessage("koku.lok.header.view", null, req.getLocale()));
/** TESTI 23.9.
    try {
      String startDateStr = lu.getDateString(1); // 1 year ago
      String endDateStr = lu.getDateString(0); // now
      model.addAttribute("startDate", startDateStr);
      model.addAttribute("endDate", endDateStr);
      // set dates to criteria
      criteria.setFrom(dateFormat.parse(startDateStr));
      criteria.setTo(dateFormat.parse(endDateStr));
      log.debug("startDateStr = " + startDateStr + ", endDateStr = " + endDateStr);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      // TODO: Lisää virheidenkäsittely
    }

   
    // note: empty results error handling is on the jsp page
    if (criteria != null) {

      if (LogConstants.REAL_ADMIN_LOG) {
        // TODO: poista tämä (Virheviestin testausta varten)
        // model.addAttribute("entries", new ArrayList<LogEntry>());
        model.addAttribute("entries", getAdminLogEntries(criteria));
      } else {
        model.addAttribute("entries", getDemoAdminLogEntries(criteria));
      }

      model.addAttribute("searchParams", criteria);

      if (visited != null) {
        model.addAttribute("visited", "---");
      }

      log.info("criteria: " + criteria.getPic() + ", " + criteria.getConcept() + ", " + criteria.getFrom() + ", "
          + criteria.getTo());
      
    
//      log.debug("criteria: " + criteria.getFrom() + ", " + criteria.getTo());
    } else {
      log.debug("criteria: null");
    }

    return "view";
    */
   
    // default endtime is now
    Calendar endtime = Calendar.getInstance();
    // default starttime is 1 year ago
    Calendar starttime = Calendar.getInstance();
    starttime.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1);

    String startDateStr = dateFormat.format(starttime.getTime());
    model.addAttribute("startDate", startDateStr);
    String endDateStr = dateFormat.format(endtime.getTime());
    model.addAttribute("endDate", endDateStr);

    log.debug("modeliin lisätty startDateStr = " + startDateStr + ", endDateStr = " + endDateStr);
    
    if (visited != null) {
    if (criteria != null) {
      model.addAttribute("entries", getAdminLogEntries(criteria));
      model.addAttribute("searchParams", criteria);
      
    
        model.addAttribute("visited", "---");
      }
      
      log.debug("criteria: " + criteria.getFrom() + ", " + criteria.getTo());
    } else {
      log.debug("criteria: null");
    }

    return "view";
  }

  // portlet action phase
  @ActionMapping(params = "action=viewLog")
  public void doSearchArchive(@ModelAttribute(value = "logSearchCriteria") LogSearchCriteria criteria,
      @ModelAttribute(value = "visited") String visited, BindingResult result, ActionResponse response) {

    if (visited != null) {
      response.setRenderParameter("visited", visited);
    }

    response.setRenderParameter("logSearchCriteria", criteriaSerializer.getAsText(criteria));
    response.setRenderParameter("action", "viewLog");
  }

  /**
   * Method for reading log entries in the 'log of logs'
   * 
   * @param criteria
   * @return
   */
  private List<AdminLogEntry> getAdminLogEntries(LogSearchCriteria criteria) {
    List<AdminLogEntry> entryList = new ArrayList<AdminLogEntry>();

    try {
     
      LogQueryCriteriaType criteriatype = new LogQueryCriteriaType();

      // the user does not have to give the dates so these might be null
      Calendar start = Calendar.getInstance();
      if(criteria.getFrom() == null){
        start = null;
      } else{
        start.setTime(criteria.getFrom());
      }
      Calendar end = Calendar.getInstance();
      if(criteria.getTo() == null){
        end = null;
      } else{
        end.setTime(criteria.getTo());
      }
      
      // assume that also null arguments are ok!! TODO: ota huomioon
      // kantakyselyissä Vai onko pakolliset?
      criteriatype.setStartTime(start);
      criteriatype.setEndTime(end);
      criteriatype.setLogType(LogConstants.LOG_ADMIN);

      // TODO: ADD HERE WRITING TO LOG
      // "tapahtumatieto = hakuehdot"

      // Set the user information
      //TODO: muutos!
      //TODO: TÄMÄ ALKUUN?
      // call to log database
      AuditInfoType audit = new AuditInfoType();
      audit.setComponent("lok"); //FIXME
      audit.setUserId("090979-9999");  // FIXME
/*     
      // write this query to normal log
      LogEntryType logEntryTypeWrite = new LogEntryType();
      logEntryTypeWrite.setUserPic(audit.getUserId());
      // LOK-4: "Tapahtumatietona hakuehdot"
      logEntryTypeWrite.setMessage("start: "+dateFormat.format(criteriatype.getStartTime().getTime())+", end: "+dateFormat.format(criteriatype.getEndTime().getTime()));
      logEntryTypeWrite.setTimestamp(Calendar.getInstance());
      logEntryTypeWrite.setOperation("search");
      logEntryTypeWrite.setClientSystemId("adminlog");
      logEntryTypeWrite.setDataItemType("log");
      logEntryTypeWrite.setDataItemId("dataitemid");
      // call to lok service
      logService.opLog(logEntryTypeWrite, audit);
*/ 
      log.debug("criteriatype start: " + criteriatype.getStartTime() + "\n end: " + criteriatype.getEndTime());
    
      LogEntriesType entriestype = logService.opQueryLog(criteriatype, audit);

      // get the log entries list from the database
      List<LogEntryType> entryTypeList = entriestype.getLogEntry();

      log.debug("entryTypeList size: " + entryTypeList.size());

      for (Iterator<?> i = entryTypeList.iterator(); i.hasNext();) {
        AdminLogEntry logEntry = new AdminLogEntry();
        LogEntryType logEntryType = (LogEntryType) i.next();

        // put values that were read from the database in logEntry for showing
        // them to the user
        logEntry.setTimestamp(logEntryType.getTimestamp().getTime());
        logEntry.setUser(logEntryType.getUserPic());
        logEntry.setOperation(logEntryType.getOperation()); // read, write, ..

        // "käsitelty tieto": all these together!
        // kks, pyh, kunpo, ..
//        logEntry.setClientSystemId(logEntryType.getClientSystemId());
        // pic of the child
        logEntry.setCustomer(logEntryType.getCustomerPic());
        // kks.vasu, kks.4v, ..
//        logEntry.setDataItemType(logEntryType.getDataItemType());
        // id given by the system that wrote the log
//        logEntry.setLogId(logEntryType.getDataItemId());
        
        // other info about the log entry
        //TODO: this already has all the other fields inside it, should this be used???
        logEntry.setMessage(logEntryType.getMessage());

        entryList.add(logEntry);
      }

   // TODO: Parempi virheenkäsittely
  
    
    } // TODO: Parempi virheenkäsittely
 catch (ServiceFault e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return entryList;
  }

  /**
   * Method creates random log entries for admin log for demo purposes.
   * 
   * @param searchCriteria
   * @return
   */
  private List<LogEntry> getDemoAdminLogEntries(LogSearchCriteria searchCriteria) {

    if (searchCriteria != null) {
      log.debug("searchCriteria=" + searchCriteria.toString());
    }

    List<LogEntry> r = null;
    LogDemoFactory factory = new LogDemoFactory();
    r = new ArrayList<LogEntry>();

    for (int i = 0; i < 5; i++) {
      r.add(factory.createLogEntry(i, LogDemoFactory.MANIPULATION_LOG));
    }

    return r;

  }

  private static class CriteriaSerializer {

    public String[] getAsText(LogSearchCriteria c) {
      SimpleDateFormat df = new SimpleDateFormat(LogConstants.DATE_FORMAT);
      String[] text = new String[] { c.getFrom() != null ? df.format(c.getFrom()) : "",
          c.getTo() != null ? df.format(c.getTo()) : "" };
      return text;
    }

    public LogSearchCriteria getFromRenderParameter(String[] text) {
      SimpleDateFormat df = new SimpleDateFormat(LogConstants.DATE_FORMAT);
      Date d1 = null, d2 = null;
      try {
        if (ArrayUtils.isNotEmpty(text)) {
          if (StringUtils.isNotBlank(text[0])) {
            d1 = df.parse(text[0]);
          }

          if (StringUtils.isNotBlank(text[1])) {
            d2 = df.parse(text[1]);
          }
        }
      } catch (ParseException e) {
        throw new IllegalArgumentException("error parsing date string", e);
      }
      return new LogSearchCriteria(d1, d2);
    }
  }

}