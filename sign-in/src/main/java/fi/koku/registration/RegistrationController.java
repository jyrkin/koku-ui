/*
 * Copyright 2012 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (kohtikumppanuutta@ixonos.com).
 * 
 * 
 */
package fi.koku.registration;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

//import com.ixonos.authentication.AuthenticationServiceClientImpl;
//import com.ixonos.authentication.VetumaUserInfo;

/**
 * Controller for the Registration
 * 
 * 
 * @author Ixonos / mikkope
 * 
 */
@Controller("RegistrationController")
@RequestMapping(value = "VIEW")
public class RegistrationController {

  private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);
  
  @Autowired
  @Qualifier("registrationValidator")
  private Validator registrationValidator;
  
  
  @InitBinder("registration")
  public void initBinder(WebDataBinder binder) {
    binder.setValidator(registrationValidator);
  }
  
  @RenderMapping
  public String render(PortletSession session, RenderRequest req, Model model) {
    model.addAttribute("registration",new Registration());
    return "registration";
  }

  @RenderMapping(params = "action=showMessageErrorsAAA")
  public String showErrors(@Valid @ModelAttribute(value = "registration") Registration registration, BindingResult errors) {
    return "registration";
  }
  
  
  @ActionMapping(params="action=saveRegistrationAAA")
  public void action(
      ActionResponse response,
      SessionStatus sessionStatus,
      @ModelAttribute(value = "registration") Registration registration,
      BindingResult errors){
      
      registrationValidator.validate(registration, errors);
      
      if ( errors.hasErrors() ) {
        response.setRenderParameter("action", "showMessageErrorsAAA" );
        sessionStatus.setComplete();
        return;
      }
  }
  
//  @RenderMapping(params="action=dummyVetuma")
//  public String vetumashow(PortletRequest pReq){
//    AuthServiceClient authServiceClient = AuthenticationServiceClientImpl.instance();
//    VetumaUserInfo vetuma = authServiceClient.getVetumaUserInfo(pReq);
//    return "registration";
//  }
  
  
  @ModelAttribute("registration")
  public Registration getRegistration(
      @RequestParam(required=false, value="email", defaultValue="") String email, 
      @RequestParam(required=false, value="phonenumber", defaultValue="") String phonenumber,
      @RequestParam(required=false, value="useraccount", defaultValue="") String useraccount,
      @RequestParam(required=false, value="password", defaultValue="") String password) {
    
      Registration r = new Registration();
      r.setUseraccount(useraccount);
      r.setPhonenumber(phonenumber);
      r.setPassword(password);
           
      return r;
  }
  
  
  
  
  
}
