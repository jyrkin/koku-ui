/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.pyh.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fi.koku.portlet.filter.userinfo.UserInfoUtils;
import fi.koku.pyh.model.CommunityRole;
import fi.koku.pyh.model.Dependant;
import fi.koku.pyh.model.DependantsAndFamily;
import fi.koku.pyh.model.Family;
import fi.koku.pyh.model.FamilyIdAndFamilyMembers;
import fi.koku.pyh.model.Person;
import fi.koku.pyh.ui.common.FamilyNotFoundException;
import fi.koku.pyh.ui.common.GuardianForChildNotFoundException;
import fi.koku.pyh.ui.common.PyhDemoService;

/**
 * Controller for editing user's family information.
 * 
 * @author hurulmi
 * 
 */

@Controller(value = "editFamilyInformationController")
@RequestMapping(value = "VIEW")
public class EditFamilyInformationController {

  private static Logger log = LoggerFactory.getLogger(EditFamilyInformationController.class);

  @Autowired
  @Qualifier(value = "pyhDemoService")
  private PyhDemoService pyhDemoService;

  @RenderMapping(params = "action=editFamilyInformation")
  public String render(RenderRequest request, Model model, PortletSession session) {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    // TODO: hae perhe ja huoltajuusyhteisöt vain kertaalleen
    
    Person user = pyhDemoService.getUser(userPic);
    DependantsAndFamily daf = pyhDemoService.getDependantsAndFamily(userPic);
    FamilyIdAndFamilyMembers fidm = pyhDemoService.getOtherFamilyMembers(userPic);
    
    model.addAttribute("user", user);
    model.addAttribute("dependants", daf.getDependants());
    model.addAttribute("otherFamilyMembers", fidm.getFamilyMembers());
    model.addAttribute("parentsFull", pyhDemoService.isParentsSet(userPic));
    model.addAttribute("messages", pyhDemoService.getSentMessages(user));
    model.addAttribute("searchedUsers", null);
    
    //Boolean childsGuardianshipInformationNotFound = new Boolean(request.getParameter("childsGuardianshipInformationNotFound"));
    Boolean childsGuardianshipInformationNotFound = Boolean.valueOf(request.getParameter("childsGuardianshipInformationNotFound"));
    
    // if child's guardianship information is not found show a notification in JSP
    model.addAttribute("childsGuardianshipInformationNotFound", childsGuardianshipInformationNotFound.booleanValue());
    
    Family family = daf.getFamily();
    String communityId;
    if (family != null) {
      communityId = family.getCommunityId();
    } else {
      // create a family community for user if does not exist
      communityId = pyhDemoService.addFamily(userPic);
    }
    session.setAttribute("familyCommunityId", communityId);
    
    return "editfamilyinformation";
  }
  
  // this render method does not clear the search results
  @RenderMapping(params = "action=editFamilyInformationWithSearchResults")
  public String renderWithSearchResults(RenderRequest request, Model model) {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    String surname = request.getParameter("surname");
    String pic = request.getParameter("pic");
    List<Person> searchedUsers = pyhDemoService.searchUsers(surname, pic, userPic);
    
    Person user = pyhDemoService.getUser(userPic);
    DependantsAndFamily daf = pyhDemoService.getDependantsAndFamily(userPic);
    FamilyIdAndFamilyMembers fidm = pyhDemoService.getOtherFamilyMembers(userPic);
    
    request.setAttribute("search", true);
    model.addAttribute("user", user);
    model.addAttribute("dependants", daf.getDependants());
    model.addAttribute("otherFamilyMembers", fidm.getFamilyMembers());
    model.addAttribute("parentsFull", pyhDemoService.isParentsSet(userPic));
    model.addAttribute("messages", pyhDemoService.getSentMessages(user));
    model.addAttribute("searchedUsers", searchedUsers);
    
    return "editfamilyinformation";
  }
  
  @ActionMapping(params = "action=addDependantAsFamilyMember")
  public void addDependantAsFamilyMember(RenderRequest request, @RequestParam String dependantPic, ActionResponse response) {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    pyhDemoService.insertDependantToFamily(userPic, dependantPic, CommunityRole.CHILD);
    response.setRenderParameter("action", "editFamilyInformation");
  }

  @ActionMapping(params = "action=removeFamilyMember")
  public void removeFamilyMember(RenderRequest request, @RequestParam String familyMemberPic, ActionResponse response) {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    pyhDemoService.removeFamilyMember(familyMemberPic, userPic);
    response.setRenderParameter("action", "editFamilyInformation");
  }

  @ActionMapping(params = "action=removeDependant")
  public void removeDependant(RenderRequest request, @RequestParam String familyMemberPic, ActionResponse response) {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    for (Dependant d : pyhDemoService.getDependantsAndFamily(userPic).getDependants()) {
      if (d.getPic().equals(familyMemberPic)) {
        d.setMemberOfUserFamily(false);
      }
    }
    pyhDemoService.removeFamilyMember(familyMemberPic, userPic);
    response.setRenderParameter("action", "editFamilyInformation");
  }
  
  @ActionMapping(params = "action=searchUsers")
  public void searchUsers(ActionRequest request, ActionResponse response) {
    //String userPic = UserInfoUtils.getPicFromSession(request);
    
    String surname = request.getParameter("searchSurname");
    String pic = request.getParameter("searchPic");
    
    // TODO: refactoring: this method will not be called but we call render method directly
    //pyhDemoService.searchUsers(surname, pic /*, userPic /*customer id == user pic*/, userPic);
    response.setRenderParameter("surname", surname);
    response.setRenderParameter("pic", pic);
    response.setRenderParameter("action", "editFamilyInformationWithSearchResults");
  }

  @ActionMapping(params = "action=addUsersToFamily")
  public void addUsersToFamily(ActionRequest request, ActionResponse response, PortletSession session) {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    String communityId = (String) session.getAttribute("familyCommunityId");
    
    HashMap<String, String> parameterMap = new HashMap<String, String>();
    HashMap<String, String> personMap = new HashMap<String, String>();
    String personPic = "";
    String personRole = "";

    for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
      String param = e.nextElement();
      String paramValue = request.getParameter(param);
      if (param.startsWith("userPic") || param.startsWith("userRole")) {
        parameterMap.put(param, paramValue);
      }
    }

    Set<String> keys = parameterMap.keySet();
    Iterator<String> si = keys.iterator();
    while (si.hasNext()) {
      String key = si.next();
      if (key.startsWith("userPic")) {
        String[] tokens = key.split("_");
        // index is the number after '_' in parameter name, e.g. userPic_1
        // (index is 1)
        String index = tokens[1];

        personPic = parameterMap.get(key);
        personRole = parameterMap.get("userRole_" + index);
        personMap.put(personPic, personRole);
      }
    }
    
    boolean childsGuardianshipInformationNotFound = false;
    try {
      pyhDemoService.addPersonsAsFamilyMembers(personMap, userPic, communityId);
    } catch (FamilyNotFoundException fnfe) {
      log.error("EditFamilyInformationController.addUsersToFamily() caught FamilyNotFoundException!", fnfe);
      // show general error page
      throw new RuntimeException(fnfe);
    } catch (GuardianForChildNotFoundException gnfe) {
      log.error("EditFamilyInformationController.addUsersToFamily() caught GuardianForChildNotFoundException!", gnfe);
      // show error message in JSP view
      childsGuardianshipInformationNotFound = true;
    }
    
    //response.setRenderParameter("childsGuardianshipInformationNotFound", new Boolean(childsGuardianshipInformationNotFound).toString());
    response.setRenderParameter("childsGuardianshipInformationNotFound", String.valueOf(childsGuardianshipInformationNotFound));
    response.setRenderParameter("action", "editFamilyInformation");
  }

}
