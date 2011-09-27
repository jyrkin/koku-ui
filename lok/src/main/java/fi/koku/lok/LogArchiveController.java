package fi.koku.lok;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.support.ResourceBundleMessageSource;
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

import fi.koku.KoKuFaultException;
import fi.koku.services.utility.log.v1.AuditInfoType;
import fi.koku.services.utility.log.v1.LogArchivalParametersType;
import fi.koku.services.utility.log.v1.LogServiceFactory;
import fi.koku.services.utility.log.v1.LogServicePortType;
import fi.koku.services.utility.log.v1.ServiceFault;

/**
 * Controller for log archiving (LOK). This implements LOK-2.
 * 
 * @author makinsu
 */
@Controller
@RequestMapping(value = "VIEW")
public class LogArchiveController {

  private static final Logger log = LoggerFactory.getLogger(LogArchiveController.class);

// Use log service
  private LogServicePortType logService;
  
  private ArchiveSerializer archiveSerializer = new ArchiveSerializer();
  SimpleDateFormat dateFormat = new SimpleDateFormat(LogConstants.DATE_FORMAT);
  LogUtils lu = new LogUtils();
  
  public LogArchiveController(){
    LogServiceFactory logServiceFactory = new LogServiceFactory(
        LogConstants.LOG_SERVICE_USER_ID, LogConstants.LOG_SERVICE_PASSWORD,
        LogConstants.LOG_SERVICE_ENDPOINT);
    logService = logServiceFactory.getLogService();    
  }
  
  // customize form data binding
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    dateFormat.setLenient(false);
    CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
    binder.registerCustomEditor(Date.class, dateEditor);
  }

  // initialize form backing objects
  @ModelAttribute("logArchiveDate")
  public LogArchiveDate getCommandObject() {
    return new LogArchiveDate();
  }

  /**
   * public String show(@ModelAttribute(value = "child") Person child,
   * 
   * @RequestParam(value = "collection") String collection, RenderResponse
   *                     response, Model model) {
   * @param req
   * @param res
   * @param model
   * @return
   */
  // portlet render phase
  @RenderMapping(params = "action=archiveLog")
  public String render(RenderRequest req, @RequestParam(value = "visited", required = false) String visited, 
      @RequestParam(value = "error", required = false) String error,
      @ModelAttribute(value = "logArchiveDate") LogArchiveDate logarchivedate,
      RenderResponse res, Model model) {

    log.debug("render archiveLog");

    try{

      if(logarchivedate != null && logarchivedate.getEndDate() != null){

        if (visited != null) { // page has been visited

          log.debug("logarchivedate: "+logarchivedate.getEndDate());
          String archiveDateStr = dateFormat.format(logarchivedate.getEndDate());

          // this is needed so that the date can be easily formatted to YYYY-MM-dd format on the jsp
          model.addAttribute("archiveDateDate", logarchivedate.getEndDate());
          model.addAttribute("logArchiveDate", logarchivedate);
          
          model.addAttribute("visited", "---");
          model.addAttribute("endDate", archiveDateStr);  
          log.debug("modeliin lisätty archiveDateStr=" + archiveDateStr);

        } else{
          log.debug("visited == null");

          String defaultDateStr = lu.getDateString(2); // default is two years ago TODO: make static
          model.addAttribute("endDate", defaultDateStr);  
          log.debug("modeliin lisätty endDate = " + defaultDateStr);
          model.addAttribute("endDate", defaultDateStr);

          Calendar time = Calendar.getInstance();
          time.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 2); // default is  two years ago
          //TODO: tai ei tehdä parsintaa webbisivulla vaan tässä??
          model.addAttribute("archiveDateDate", time.getTime());
         
          log.debug("modeliin lisätty archiveDate=" + defaultDateStr);

        }
      }else{
        log.debug("logarchivedate == null");
        // set the default archive date
        String defaultDateStr = lu.getDateString(2); // default is two years ago TODO: make static
        model.addAttribute("endDate", defaultDateStr);  
        log.debug("modeliin lisätty endDate = " + defaultDateStr);
      }
      log.debug("error = "+error);
      
        if(error != null ){
          model.addAttribute("error", "--"); // TODO: voisi olla virhekoodi tms.
        }

    }catch(KoKuFaultException e){
      log.error(e.getMessage(), e);
      //TODO: Lisää virheidenkäsittely
      // käyttäjälle näytetään virheviesti "koku.lok.archive.parsing.error"

      }

    return "archive";
  }

  
  // portlet action phase
  @ActionMapping(params = "action=archiveLog")
  public void doArchive(@ModelAttribute(value = "logArchiveDate") LogArchiveDate logarchivedate, 
      @ModelAttribute(value = "visited") String visited, BindingResult result,
      ActionResponse response) {

    log.debug("log archive action phase");
 
    String archivedate = archiveSerializer.getAsText(logarchivedate);
    log.debug("logarchivedate: "+archivedate);
    
    if(archivedate == null){
      log.debug("archivedate on null!");
      //TODO: PITÄÄ LISÄTÄ KÄYTTÄJÄLLE ILMOITUS RUUDULLE VÄÄRÄSTÄ SYÖTTEESTÄ!
    } else{
      log.debug("saatiin jsp-sivulta archive end date: " + logarchivedate.getEndDate());
    }
    
    if (visited != null) {
      response.setRenderParameter("visited", visited);
    }
    response.setRenderParameter("logArchiveDate", archiveSerializer.getAsText(logarchivedate));
    response.setRenderParameter("action", "archiveLog");

  }

//portlet render phase
  @RenderMapping(params = "action=startArchiveLog")
  public String renderStart(RenderRequest req, @RequestParam(value = "error", required = false) String error,
      @ModelAttribute(value = "logArchiveDate") LogArchiveDate logarchivedate,
      RenderResponse res, Model model) {

    log.debug("start archive render phase: archiving started");
    if (logarchivedate != null) {
      log.debug("archive end date: " + logarchivedate.getEndDate());
    } else{
      log.debug("action: logarchivedate == null!");
    }

    log.debug("error = "+error);
    
    if(error != null){
      
      model.addAttribute("error", error); // TODO: voisi olla virhekoodi tms.
      return "archive";
    }
    return "archive2";
  }

  @ActionMapping(params = "action=startArchiveLog")
  public void startArchive(@ModelAttribute(value = "logArchiveDate") LogArchiveDate logarchivedate,
      BindingResult result, ActionResponse response) {
 
    try{
    
      LogArchivalParametersType archiveParametersType = new LogArchivalParametersType();
      
      if(logarchivedate != null && logarchivedate.getEndDate() != null){
        archiveParametersType.setEndDate(lu.dateToCalendar(logarchivedate.getEndDate()));

        // call to log database
        AuditInfoType audit = new AuditInfoType();
        audit.setComponent("lok"); //FIXME Voi olla demossa näin!
        audit.setUserId("luser");  // FIXME

        log.debug("log archive action phase: starting archiving");

        // call to log archive service
        logService.opArchiveLog(archiveParametersType, audit);

    /* TODO: TÄMÄ TULEE LOKSERVICEEN:
     *  2) kopioi tapahtumalokista lokitiedot arkistolokiin
        2.1) Annetulla aikavälillä ei ole yhtään arkistoitavaa lokitietoa
          -> Käyttäjälle ilmoitetaan UI:ssa (koku.lok.archive.nothing.to.archive)
        2.2) arkistoloki ei vastaa tai kuittaa onnistunutta lokitietojen kopiointia
          -> lokitietoja ei poisteta tapahtumalokista
          -> käsittelylokiin tallennetaan virheviesti
          -> käyttäjälle ilmoitetaan virheestä UI:ssa (koku.lok.archive.error)
     3) poista kopioidut tiedot tapahtumalokista
     4) taltioi käsittelylokiin tieto arkistoinnista  
     */
      }else{
        response.setRenderParameter("error", "arkistointipvm puuttuu");
      }
 
    }// TODO: lisää tähän catch sitä varten, että tulee virheet 2.1 tai 2.2
 catch (ServiceFault e) {
   log.debug("fault: "+e.getFaultInfo().getCode());
 
   if(LogConstants.LOG_NOTHING_TO_ARCHIVE == e.getFaultInfo().getCode()){
 
     response.setRenderParameter("error", "koku.lok.archive.nothing.to.archive"); //TODO: tuleeko tähän virhekoodi, jolla jsp:ssä valitaan virhe??
   log.debug("ei arkistoitavaa");
   }else{
     response.setRenderParameter("error", "--"); //TODO: mikä olisi tämä yleinen virhe???
     log.debug("tuntematon virhe startArchivessa");
   }
 }
    
     // TODO: Parempi virheenkäsittely
    response.setRenderParameter("archiveDateStr", archiveSerializer.getAsText(logarchivedate));
    response.setRenderParameter("action", "startArchiveLog");

  }

  private static class ArchiveSerializer {

    private SimpleDateFormat df = new SimpleDateFormat(LogConstants.DATE_FORMAT);

    /**
     * Method formats the given Date into a String.
     * If the input is null or cannot be formatted, the method returns a null value.
     */
    public String getAsText(LogArchiveDate logarchivedate) {
      String date = null;

      if (logarchivedate != null) {
        date = logarchivedate.getEndDate() != null ? df.format(logarchivedate.getEndDate()) : "";

        log.debug("getAsText: formatoitu archivedate: " + date);
      }

      return date;
    }

    /**
     * Method for parsing the date parameter.
     * @param text
     * @return
     */
    public LogArchiveDate getFromRenderParameter(String text) {

      Date d1 = null;
      try {
        if (StringUtils.isNotEmpty(text)) {
          d1 = df.parse(text);
        }
      } catch (ParseException e) {
        throw new IllegalArgumentException("error parsing date string", e);
      }

      return new LogArchiveDate(d1);
    }

    // TODO: tarvitaanko tätä?
    public LogArchiveDate getFromRenderParameter(String[] text) {

      Date d1 = null;
      try {
        if (ArrayUtils.isNotEmpty(text) && StringUtils.isNotEmpty(text[0])) {
          d1 = df.parse(text[0]);
        }
      } catch (ParseException e) {
        throw new IllegalArgumentException("error parsing date string", e);
      }

      return new LogArchiveDate(d1);
    }
  }
}