/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package com.ixonos.koku.pyh;

import java.util.List;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.ixonos.koku.pyh.model.DependantsAndFamily;
import com.ixonos.koku.pyh.model.FamilyIdAndFamilyMembers;
import com.ixonos.koku.pyh.model.FamilyMember;
import com.ixonos.koku.pyh.model.Person;

import fi.koku.portlet.filter.userinfo.UserInfo;

@Controller(value = "familyInformationController")
@RequestMapping(value = "VIEW")
public class FamilyInformationController {

  private static Logger log = LoggerFactory.getLogger(FamilyInformationController.class);

  @Autowired
  @Qualifier(value = "pyhDemoService")
  private PyhDemoService pyhDemoService;

  @RenderMapping
  public String render(PortletSession session, Model model, RenderRequest request) {
    
    String userPic = "";
    
    UserInfo userInfo = (UserInfo)session.getAttribute(UserInfo.KEY_USER_INFO);
    if (userInfo != null) {
      userPic = userInfo.getPic();
      //log.info("Got PIC from session. userPic = " + userPic);
      
    } else {
      // TODO: mitä tehdään kun käyttäjää ei voida tunnistaa?
      log.error("ERROR: UserInfo returns no PIC!");
    }
    
    Person user = pyhDemoService.getUser(userPic);
    DependantsAndFamily daf = pyhDemoService.getDependantsAndFamily(userPic);
    FamilyIdAndFamilyMembers fidm = pyhDemoService.getOtherFamilyMembers(userPic);
    
    model.addAttribute("user", user);
    model.addAttribute("dependants", daf.getDependants());
    model.addAttribute("otherFamilyMembers", fidm.getFamilyMembers());
    model.addAttribute("currentFamilyId", fidm.getFamilyId());
    model.addAttribute("messages", pyhDemoService.getMessagesFor(user));
    model.addAttribute("sentMessages", pyhDemoService.getSentMessages(user));
    
    model.addAttribute("supportEmailAddress", PyhConstants.KOKU_SUPPORT_EMAIL_ADDRESS);
    
    return "familyinformation";
  }
  
}
