package com.ixonos.koku.lok;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

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
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;


/**
 * Controller for log archiving (LOK).
 * This implements LOK-2.
 * 
 * @author makinsu
 */
@Controller
@RequestMapping(value = "VIEW")
public class LogArchiveController {
  private static final String ARCHIVE_DATE_RENDER_PARAM = "log-archive";
  private ArchiveSerializer archiveSerializer = new ArchiveSerializer();
  
  
  @Autowired
  private ResourceBundleMessageSource resourceBundle;
  
//customize form data binding
  @InitBinder
  public void initBinder(WebDataBinder binder) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(LogConstants.DATE_FORMAT);
    dateFormat.setLenient(false);
    CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
    binder.registerCustomEditor(Date.class, dateEditor);
  }
  
  // initialize form backing objects
  @ModelAttribute("logArchiveDate")
  public LogArchiveDate getCommandObject() {
    return new LogArchiveDate();
  }
  
  // portlet render phase
  @RenderMapping(params="op=archiveLog")
  public String render(RenderRequest req, RenderResponse res, Model model) {

  System.out.println("log archive render phase");
 
    res.setTitle(resourceBundle.getMessage("koku.lok.portlet.title", null, req.getLocale()));

    LogArchiveDate archivedate = null;
    if(req.getParameterValues(ARCHIVE_DATE_RENDER_PARAM) != null) {
      archivedate = archiveSerializer.getFromRenderParameter(req.getParameterValues(ARCHIVE_DATE_RENDER_PARAM));
      model.addAttribute("archiveDate", archivedate.getDate());
    }    
    
    return "archive";
}

//portlet render phase
  @RenderMapping(params="op=startArchiveLog")
  public String renderStart(RenderRequest req, RenderResponse res, Model model) {

  System.out.println("log archive render phase: archiving started");
 
    res.setTitle(resourceBundle.getMessage("koku.lok.portlet.title", null, req.getLocale()));

    LogArchiveDate archivedate = null;
    if(req.getParameterValues(ARCHIVE_DATE_RENDER_PARAM) != null) {
      archivedate = archiveSerializer.getFromRenderParameter(req.getParameterValues(ARCHIVE_DATE_RENDER_PARAM));
      model.addAttribute("archiveDate", archivedate.getDate());
    }    
    
    return "archive2";
}
//portlet action phase
  @ActionMapping(params="op=archiveLog")
  public void doArchive(@ModelAttribute(value = "logArchiveDate") LogArchiveDate archivedate, BindingResult result,
      ActionResponse response) {
    System.out.println("log archive action phase");
   
    // TODO:
    
    // 1) vahvistetaan käyttäjältä annettu päivä
    // 2) kopioi tapahtumalokista lokitiedot arkistolokiin 
    // 3) poista kopioidut tiedot tapahtumalokista
    // 4) taltioi käsittelylokiin tieto arkistoinnista
    // 
    // 5) ilmoita käyttäjälle, että arkistointi onnistui
    
    
    response.setRenderParameter(ARCHIVE_DATE_RENDER_PARAM, archiveSerializer.getAsText(archivedate));
    response.setRenderParameter("op", "archiveLog");
   
  }
  
  @ActionMapping(params="op=startArchiveLog")
  public void startArchive(@ModelAttribute(value = "logArchiveDate") LogArchiveDate archivedate, BindingResult result,
      ActionResponse response) {
    System.out.println("log archive action phase: starting archiving");
   
    // TODO: ADD HERE THE ACTUAL ARCHIVING COMMAND
    
    response.setRenderParameter(ARCHIVE_DATE_RENDER_PARAM, archiveSerializer.getAsText(archivedate));
    response.setRenderParameter("op", "startArchiveLog");
  
  }
    
  private static class ArchiveSerializer{
    public String getAsText(LogArchiveDate archivedate){
      SimpleDateFormat df = new SimpleDateFormat(LogConstants.DATE_FORMAT);
      String date = archivedate.getDate() != null ? df.format(archivedate.getDate()) : ""; 
      
      System.out.println("getAsText: date: "+date);
     
      return date;
    }
    
    public LogArchiveDate getFromRenderParameter(String[] text) {
      SimpleDateFormat df = new SimpleDateFormat(LogConstants.DATE_FORMAT);
   
      Date d1 = null;
      try {
        d1 = text[0].length() > 0 ? df.parse(text[0]) : null;
      } catch (ParseException e) {
        throw new IllegalArgumentException("error parsing date string", e);
      }
      
      return new LogArchiveDate(d1);
    }
  }
}