/*
 * Copyright 2012 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (kohtikumppanuutta@ixonos.com).
 * 
 * 
 */
package fi.koku.registration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.validation.Valid;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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

import com.ixonos.authentication.AuthenticationServiceClient;
import com.ixonos.authentication.AuthenticationServiceClientImpl;
import com.ixonos.authentication.VetumaUserInfo;

import fi.koku.portlet.filter.userinfo.SecurityUtils;
import fi.koku.services.utility.portal.v1.ContactInfoType;
import fi.koku.services.utility.portal.v1.ContactInfoUpdateType;
import fi.koku.services.utility.portal.v1.PortalUserAllType;
import fi.koku.services.utility.portal.v1.PortalUserPicQueryParamType;
import fi.koku.services.utility.portal.v1.PortalUserServicePortType;
import fi.koku.services.utility.portal.v1.PortalUserType;
import fi.koku.services.utility.portal.v1.PortalUserUpdateType;
import fi.koku.services.utility.portal.v1.ServiceFault;

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

  @Autowired
  @Qualifier("userinformationValidator")
  private Validator userinformationValidator;

  @InitBinder("registration")
  public void initBinder(WebDataBinder binder) {
    binder.setValidator(registrationValidator);
  }

  @InitBinder("userinformation")
  public void initBinder2(WebDataBinder binder) {
    binder.setValidator(userinformationValidator);
  }

  @RenderMapping
  public String render(PortletSession session, RenderRequest req, Model model,
      @ModelAttribute(value = "registration") Registration r, RenderResponse res) {

    boolean isLoggedIn = hasPortalUsername(req);
    boolean isStrongAuth = isStrongAuthenticated(req);
    if (isStrongAuth) {
      try {
        addCsrfTokenIntoSession(req);
      } catch (IOException e) {
        log.error("Failed to add CSRF to session on registration", e);
        e.printStackTrace();
      } catch (PortletException e) {
        log.error("Failed to add CSRF to session on registration", e);
        e.printStackTrace();
      }
      model.addAttribute(SecurityUtils.KEY_CSRF_TOKEN, SecurityUtils.getCSRFTokenFromSession(req));      
    }

    if (isLoggedIn) {

      if (isStrongAuth) {
        // HAE KAYTTAJAN TIEDOT HETULLA JA ID:lla ja nayta oma sivu
        String pic = getPic(req);
        String username = req.getRemoteUser();
        Registration registration = getRegistration(req, pic, username);
        if (registration != null) {

          Userinformation u = getUserinformation(req, pic);
          model.addAttribute("userinformation", u);

          return "userinformation";
        } else {
          // /GetAuthURL
          String url = generateRegistrationURL(res);
          model.addAttribute("registrationURL", url);
          return "notauthenticated";
        }

      } else {
        // NOT REGISTERED. Palvelu vaatii rekisteroinnin rekisteroidy tasta
        String url = generateRegistrationURL(res);
        model.addAttribute("registrationURL", url);
        return "notregistered";
      }
    } else {
      if (isStrongAuth) {
        // Katsotaan onko kayttaja rekisteroitynyt jo aikaisemmin
        String pic = getPic(req);
        String username = req.getRemoteUser();
        Registration registration = getRegistration(req, pic, username);
        if (registration != null) {
          // 1) Show current information #TODO# Requires PortalUserType from WS
          // model.addAttribute("registration",registration);
          // return "userinformation";

          // 2) Show login link to user #TODO# Requires PortalUserType from WS
          // String loginurl =
          // "/portal/login?username="+registration.getUseraccount()+"&password="+registration.getPassword();
          // model.addAttribute("loginurl", loginurl);
          // return "createlogin";

          // 3) Suggest to login with username&pwd
          model.addAttribute("alreadyregistered", true);
          return "notauthenticated";

        } else {
          // Kayttajan tietoja ei loytynyt, joten han ei ole rekisteroitynyt
          // aiemmin
          // Show populated Registration form
          registration = prePopulateRegistration(req, r);
          model.addAttribute("registration", registration);
          return "registration";
        }
      } else {
        // /GetAuthURL
        String url = generateRegistrationURL(res);
        model.addAttribute("registrationURL", url);
        return "notauthenticated";
      }
    }

  }

  private String generateRegistrationURL(RenderResponse res) {
    AuthenticationServiceClient authServiceClient = AuthenticationServiceClientImpl.instance();
    String randomStr = org.apache.commons.lang.RandomStringUtils.randomAlphanumeric(10);
    String url = authServiceClient.getAuthenticationURL(res, "unregistered" + randomStr);
    return url;
  }

  private Registration prePopulateRegistration(PortletRequest req, Registration r) {
    if (r == null)
      r = new Registration();
    VetumaUserInfo ui = getUserInfo(req);
    if (ui != null) {
      r.setFirstname(ui.getFirstname());
      r.setLastname(ui.getLastname());
      r.setDayOfBirth(getDayOfBirth(ui.getId()));
      r.setPic(ui.getId());

    }
    if (StringUtils.isNotBlank(req.getRemoteUser())) {
      r.setUseraccount(req.getRemoteUser());
    }

    if (StringUtils.isBlank(r.getPreferredContactMethod())) {
      r.setPreferredContactMethod("email");
    }

    return r;
  }

  @RenderMapping(params = "action=showMessageErrorsAAA")
  public String showErrors(@Valid @ModelAttribute(value = "registration") Registration registration,
      BindingResult errors) {
    return "registration";
  }

  @RenderMapping(params = "action=showMessageErrorsUserinformation")
  public String showErrors2(@Valid @ModelAttribute(value = "userinformation") Userinformation userinformation,
      BindingResult errors) {
    return "userinformation";
  }

  @RenderMapping(params = "action=kokuregistration_createLogin")
  public String createLogin(@ModelAttribute(value = "registration") Registration registration, Model model)
      throws UnsupportedEncodingException {
    // #TODO# Consider getting "baseURL" as a configurable property
    String loginurl = "/portal/login?username=" + URLEncoder.encode(registration.getUseraccount(), "UTF-8")
        + "&password=" + URLEncoder.encode(registration.getPassword(), "UTF-8");
    model.addAttribute("loginurl", loginurl);
    return "createlogin";
  }

  @RenderMapping(params = "action=showCsrfError")
  public String showError(PortletSession session, RenderRequest req, Model model) {
    return "error";
  }

  @ActionMapping(params = "action=saveRegistrationAAA")
  public void action(ActionResponse response, ActionRequest request, SessionStatus sessionStatus,
      @ModelAttribute(value = "registration") Registration r, BindingResult errors) {

    if (!SecurityUtils.hasValidCSRFToken(request)) {
      setCsrfErrorPage(response, sessionStatus);
      return;
    }

    registrationValidator.validate(r, errors);

    if (errors.hasErrors()) {
      response.setRenderParameter("action", "showMessageErrorsAAA");
      sessionStatus.setComplete();
      return;
    }

    // Try to add portal user
    try {
      ServiceFactory factory = new ServiceFactory();
      PortalUserServicePortType serv = factory.getPortalUserService();// #TODO#CHECKPOINT
      PortalUserType u = new PortalUserType();
      u.setFirstNames(r.getFirstname());

      u.setSurName(r.getLastname());
      u.setUserName(r.getUseraccount());
      u.setPic(r.getPic());
      u.setPassword(r.getPassword());
      u.setBirthDate(convertCalendar(ssnToCalendar(r.getPic())));
      // #TODO# Check these: Needs to be integer -objects. Otherwise fails to go
      // to service.
      u.setNotificationMethod(new Integer(1));
      u.setDisabled(new Integer(1));
      // #TODO# Check this. Contactinfotype fields must be set. Otherwise fails
      ContactInfoType c = new ContactInfoType();
      c.setEmail(r.getEmail());
      c.setPhoneNumber(r.getPhonenumber());
      c.setCity("");
      c.setCountry("");
      c.setStreetAddress("");
      c.setPostalCode("");
      u.getContactInfos().add(c);

      serv.opAddPortalUser(u);

      // User added succesfully
      response.setRenderParameter("action", "kokuregistration_createLogin");

    } catch (ServiceFault e) {
      log.error("Failed to add portaluser. username=" + r.getUseraccount(), e);

    } catch (Exception ex) {
      log.error("Failed to add portaluser. username=", ex);
    }
  }

  @ActionMapping(params = "action=kokuregistrationsaveUserinformation")
  public void saveUserinformationAction(PortletRequest req, ActionResponse response, SessionStatus sessionStatus,
      @ModelAttribute(value = "userinformation") Userinformation userinfo, BindingResult errors) {

    if (!SecurityUtils.hasValidCSRFToken(req)) {
      setCsrfErrorPage(response, sessionStatus);
      return;
    }

    userinformationValidator.validate(userinfo, errors);

    if (errors.hasErrors()) {
      response.setRenderParameter("action", "showMessageErrorsUserinformation");
      sessionStatus.setComplete();
      return;
    }

    try {
      PortalUserServicePortType serv = new ServiceFactory().getPortalUserService();
      PortalUserUpdateType u = new PortalUserUpdateType();

      u.setUserName( /* r.getUseraccount() */req.getRemoteUser());
      u.setPassword(userinfo.getCurrentpassword());
      u.setPic(getPic(req));

      u.setNewPassword(userinfo.getNewpassword());
      // #TODO# Check these: Needs to be integer -objects. Otherwise fails to go
      // to service.
      u.setNotificationMethod(new Integer(1));
      ContactInfoUpdateType c = new ContactInfoUpdateType();
      c.setEmail(userinfo.getEmail());
      c.setPhoneNumber(userinfo.getPhonenumber());
      c.setCity("");
      c.setCountry("");
      c.setPostalCode("");
      c.setStreetAddress("");
      u.getContactInfoUpdates().add(c);

      serv.opUpdatePortalUser(u);

    } catch (Exception ex) {
      log.error("Failed.", ex);
    }
    response.setRenderParameter("action", "aaa");// Default
  }

  @ModelAttribute("registration")
  public Registration getRegistration(PortletRequest pReq,
      @RequestParam(required = false, value = "email", defaultValue = "") String email,
      @RequestParam(required = false, value = "phonenumber", defaultValue = "") String phonenumber,
      @RequestParam(required = false, value = "useraccount", defaultValue = "") String useraccount,
      @RequestParam(required = false, value = "password", defaultValue = "") String password) {

    Registration r = new Registration();
    VetumaUserInfo ui = getUserInfo(pReq);
    if (ui != null) {
      r.setFirstname(ui.getFirstname());
      r.setLastname(ui.getLastname());
      r.setDayOfBirth(getDayOfBirth(ui.getId()));
      r.setPic(ui.getId());
      r.setUseraccount(useraccount);
      r.setPhonenumber(phonenumber);
      r.setPassword(password);
    }
    if (StringUtils.isBlank(r.getPreferredContactMethod())) {
      r.setPreferredContactMethod("email");
    }

    return r;
  }

  @ModelAttribute("userinformation")
  public Userinformation getUserinformation(PortletRequest pReq,
      @RequestParam(required = false, value = "email", defaultValue = "") String email,
      @RequestParam(required = false, value = "phonenumber", defaultValue = "") String phonenumber,
      @RequestParam(required = false, value = "useraccount", defaultValue = "") String useraccount,
      @RequestParam(required = false, value = "currentpassword", defaultValue = "") String currentpassword) {
    String pic = getPic(pReq);
    Userinformation userinfo = getUserinformation(pReq, pic);

    // Userinformation userinfo = new Userinformation();
    VetumaUserInfo ui = getUserInfo(pReq);
    if (ui != null) {
      userinfo.setFirstname(ui.getFirstname());
      userinfo.setLastname(ui.getLastname());
      userinfo.setDayOfBirth(getDayOfBirth(ui.getId()));
      userinfo.setPic(ui.getId());
      userinfo.setUseraccount(useraccount);
    }
    return userinfo;
  }

  private VetumaUserInfo getUserInfo(PortletRequest pReq) {
    // GetVetumaInfo
    AuthenticationServiceClient authServiceClient = AuthenticationServiceClientImpl.instance();
    return authServiceClient.getVetumaUserInfo(pReq);
  }

  private boolean hasPortalUsername(RenderRequest req) {
    String remoteUser = req.getRemoteUser();
    if (remoteUser != null) {
      return true;
    } else {
      return false;
    }
  }

  private boolean isStrongAuthenticated(RenderRequest req) {
    VetumaUserInfo u = getUserInfo(req);
    if (u != null) {
      // Add CSRF to session

      return true;
    } else {
      return false;
    }
  }

  private String getPic(PortletRequest req) {
    String pic = null;
    VetumaUserInfo vui = getUserInfo(req);
    if (vui == null) {
      // All users in our case are strongly authenticated and vui should exist.
    } else {
      pic = vui.getId();
    }

    return pic;
  }

  private Registration getRegistration(PortletRequest req, String pic, String username) {
    try {
      PortalUserAllType pUser = getPortalUserWithPicAndUsername(pic, username);
      if (pUser != null) {
        // Fill Registration
        Registration r = new Registration();
        r.setDayOfBirth(getDayOfBirth(pUser.getPic()));

        r.setFirstname(pUser.getFirstNames());
        r.setLastname(pUser.getSurName());
        r.setUseraccount(pUser.getUserName());

        handleContactInfo(pUser, r);

        return r;
      }
    } catch (Exception ex) {
      log.error("Failed", ex);
    }
    return null;
  }

  private void handleContactInfo(PortalUserAllType pUser, Registration r) {
    // Traverses through CI objects and gets LAST not blank email and
    // phonenumber values.
    r.setEmail("");
    r.setPhonenumber("");
    List<ContactInfoType> contactInfos = pUser.getContactInfos();
    if (CollectionUtils.isNotEmpty(contactInfos)) {
      for (ContactInfoType ci : contactInfos) {
        if (ci != null) {
          if (StringUtils.isNotBlank(ci.getEmail())) {
            r.setEmail(ci.getEmail());
          }
          if (StringUtils.isNotBlank(ci.getPhoneNumber())) {
            r.setPhonenumber(ci.getPhoneNumber());
          }
        }

      }
    }
  }

  private PortalUserAllType getPortalUserWithPicAndUsername(String pic, String username) throws ServiceFault {
    PortalUserAllType pUser = null;
    if (StringUtils.isNotBlank(username) || StringUtils.isNotBlank(pic)) {
      PortalUserServicePortType uservice = new ServiceFactory().getPortalUserService();
      PortalUserPicQueryParamType userPic = new PortalUserPicQueryParamType();
      userPic.setPic(pic);
      userPic.setUserName(username);
      pUser = uservice.opGetPortalUserByPic(userPic);
    }
    return pUser;
  }

  private Userinformation getUserinformation(PortletRequest req, String pic) {
    try {
      String username = req.getRemoteUser();
      PortalUserAllType pUser = getPortalUserWithPicAndUsername(pic, username);
      if (pUser != null) {
        // Fill Registration
        Userinformation ui = new Userinformation();
        ui.setDayOfBirth(getDayOfBirth(pUser.getPic()));

        ui.setFirstname(pUser.getFirstNames());
        ui.setLastname(pUser.getSurName());
        String uname = StringUtils.isBlank(pUser.getUserName()) ? req.getRemoteUser() : pUser.getUserName();
        ui.setUseraccount(uname);

        handleContactInfo(pUser, ui);
        return ui;
      }
    } catch (Exception ex) {
      log.error("Failed", ex);
    }
    return new Userinformation();
  }

  private void handleContactInfo(PortalUserAllType pUser, Userinformation ui) {
    // Traverses through CI objects and gets LAST not blank email and
    // phonenumber values.
    ui.setEmail("");
    ui.setPhonenumber("");
    List<ContactInfoType> contactInfos = pUser.getContactInfos();
    if (CollectionUtils.isNotEmpty(contactInfos)) {
      for (ContactInfoType ci : contactInfos) {
        if (ci != null) {
          if (StringUtils.isNotBlank(ci.getEmail())) {
            ui.setEmail(ci.getEmail());
          }
          if (StringUtils.isNotBlank(ci.getPhoneNumber())) {
            ui.setPhonenumber(ci.getPhoneNumber());
          }
        }

      }
    }
  }

  private String getDayOfBirth(String ssn) {
    if (StringUtils.isNotBlank(ssn) && ssn.length() == 11) {
      int date = Integer.valueOf(ssn.substring(0, 2));
      int month = Integer.valueOf(ssn.substring(2, 4));
      int year = Integer.valueOf(ssn.substring(4, 6));
      String delim = ssn.substring(6, 7);
      final String AFTER_2000_SSN_DELIM = "A";
      year = AFTER_2000_SSN_DELIM.equals(delim) ? 2000 + year : 1900 + year;

      Calendar c = Calendar.getInstance();
      c.set(Calendar.DATE, date);
      c.set(Calendar.MONTH, month - 1);
      c.set(Calendar.YEAR, year);
      SimpleDateFormat sdf = new SimpleDateFormat();
      sdf.applyPattern("dd.MM.yyyy");
      return sdf.format(c.getTime());
    }
    return "";
  }

  private Calendar ssnToCalendar(String ssn) {
    if (StringUtils.isNotBlank(ssn) && ssn.length() == 11) {
      int date = Integer.valueOf(ssn.substring(0, 2));
      int month = Integer.valueOf(ssn.substring(2, 4));
      int year = Integer.valueOf(ssn.substring(4, 6));
      String delim = ssn.substring(6, 7);
      final String AFTER_2000_SSN_DELIM = "A";
      year = AFTER_2000_SSN_DELIM.equals(delim) ? 2000 + year : 1900 + year;

      Calendar c = Calendar.getInstance();
      c.set(Calendar.DATE, date);
      c.set(Calendar.MONTH, month - 1);
      c.set(Calendar.YEAR, year);
      return c;
    }
    return null;
  }

  private XMLGregorianCalendar convertCalendar(Calendar calendar) {
    DatatypeFactory f;
    try {
      f = DatatypeFactory.newInstance();
      GregorianCalendar gc = new GregorianCalendar();
      gc.setTimeInMillis(calendar.getTimeInMillis());
      return f.newXMLGregorianCalendar(gc);
    } catch (DatatypeConfigurationException e) {
      log.error("Failed to convert calendar to XMLGregorianCalendarFormat");
    }
    return null;
  }

  private void addCsrfTokenIntoSession(PortletRequest request) throws IOException, PortletException {
    request.getPortletSession().setAttribute(SecurityUtils.KEY_CSRF_TOKEN, generateCsrfToken());
  }

  private String generateCsrfToken() {
    long seed = System.currentTimeMillis();
    SecureRandom r = new SecureRandom();
    r.setSeed(seed);
    return Long.toString(seed) + Long.toString(Math.abs(r.nextLong()));
  }

  private void setCsrfErrorPage(ActionResponse response, SessionStatus sessionStatus) {
    log.error("Possible CSRF attack detected, forwarding to error view!");
    response.setRenderParameter("action", "showCsrfError");
    sessionStatus.setComplete();
  }

  private String getCsrfErrorPage() {
    return "error";
  }

}
