/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.pyh.controller;

import java.util.ArrayList;
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
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fi.koku.portlet.filter.userinfo.UserInfoUtils;
import fi.koku.pyh.ui.common.Log;
import fi.koku.pyh.ui.common.PyhConstants;
import fi.koku.services.entity.community.v1.AuditInfoType;
import fi.koku.services.entity.community.v1.CommunitiesType;
import fi.koku.services.entity.community.v1.CommunityQueryCriteriaType;
import fi.koku.services.entity.community.v1.CommunityServiceConstants;
import fi.koku.services.entity.community.v1.CommunityServiceFactory;
import fi.koku.services.entity.community.v1.CommunityServicePortType;
import fi.koku.services.entity.community.v1.CommunityType;
import fi.koku.services.entity.community.v1.MemberPicsType;
import fi.koku.services.entity.community.v1.MemberType;
import fi.koku.services.entity.community.v1.MembersType;
import fi.koku.services.entity.customer.v1.CustomerServiceFactory;
import fi.koku.services.entity.customer.v1.CustomerServicePortType;
import fi.koku.services.entity.customer.v1.CustomerType;
import fi.koku.services.entity.customerservice.exception.FamilyNotFoundException;
import fi.koku.services.entity.customerservice.exception.GuardianForChildNotFoundException;
import fi.koku.services.entity.customerservice.exception.TooManyFamiliesException;
import fi.koku.services.entity.customerservice.helper.FamilyHelper;
import fi.koku.services.entity.customerservice.helper.MessageHelper;
import fi.koku.services.entity.customerservice.model.CommunityRole;
import fi.koku.services.entity.customerservice.model.Dependant;
import fi.koku.services.entity.customerservice.model.DependantsAndFamily;
import fi.koku.services.entity.customerservice.model.Family;
import fi.koku.services.entity.customerservice.model.FamilyIdAndFamilyMembers;
import fi.koku.services.entity.customerservice.model.Person;

/**
 * Controller for user's family information editing view.
 * 
 * @author hurulmi
 * 
 */
@Controller(value = "editFamilyInformationController")
@RequestMapping(value = "VIEW")
public class EditFamilyInformationController {

  private static Logger logger = LoggerFactory.getLogger(EditFamilyInformationController.class);
  
  @Autowired
  private MailSender mailSender;
  
  @Autowired
  private SimpleMailMessage templateMessage;
  
  private CustomerServicePortType customerService;
  private CommunityServicePortType communityService;
  private FamilyHelper familyHelper;
  private MessageHelper messageHelper;
  
  public EditFamilyInformationController() {
    CustomerServiceFactory customerServiceFactory = new CustomerServiceFactory(PyhConstants.CUSTOMER_SERVICE_USER_ID, PyhConstants.CUSTOMER_SERVICE_PASSWORD, PyhConstants.CUSTOMER_SERVICE_ENDPOINT);
    customerService = customerServiceFactory.getCustomerService();
    
    CommunityServiceFactory communityServiceFactory = new CommunityServiceFactory(PyhConstants.COMMUNITY_SERVICE_USER_ID, PyhConstants.COMMUNITY_SERVICE_PASSWORD, PyhConstants.COMMUNITY_SERVICE_ENDPOINT);
    communityService = communityServiceFactory.getCommunityService();
    
    familyHelper = new FamilyHelper(customerService, communityService, PyhConstants.COMPONENT_PYH);
    messageHelper = new MessageHelper(customerService, communityService, PyhConstants.COMPONENT_PYH);
  }
  
  @RenderMapping(params = "action=editFamilyInformation")
  public String render(RenderRequest request, Model model) throws fi.koku.services.entity.customer.v1.ServiceFault,
    fi.koku.services.entity.community.v1.ServiceFault {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    // TODO: hae perhe ja huoltajuusyhteisöt vain kertaalleen
    
    CustomerType customer = customerService.opGetCustomer(userPic, CustomerServiceFactory.createAuditInfoType(PyhConstants.COMPONENT_PYH, userPic));
    
    logger.debug("EditFamilyInformationController.render(): returning customer: " + customer.getEtunimetNimi() + " " + customer.getSukuNimi() + ", " + customer.getHenkiloTunnus());
    Person user = new Person(customer);
    
    DependantsAndFamily daf = familyHelper.getDependantsAndFamily(userPic);
    FamilyIdAndFamilyMembers fidm = familyHelper.getOtherFamilyMembers(userPic);
    
    model.addAttribute("user", user);
    model.addAttribute("dependants", daf.getDependants());
    model.addAttribute("otherFamilyMembers", fidm.getFamilyMembers());
    model.addAttribute("parentsFull", familyHelper.isParentsSet(userPic));
    model.addAttribute("messages", messageHelper.getSentMessages(user));
    model.addAttribute("searchedUsers", null);
    
    Boolean childsGuardianshipInformationNotFound = Boolean.valueOf(request.getParameter("childsGuardianshipInformationNotFound"));
    
    // if child's guardianship information is not found show a notification in JSP
    model.addAttribute("childsGuardianshipInformationNotFound", childsGuardianshipInformationNotFound.booleanValue());
    
    // create user family community if does not exist
    if (daf.getFamily() == null) {
      addFamily(userPic);
    }
    
    return "editfamilyinformation";
  }
  
  // this render method does not clear the search results
  @RenderMapping(params = "action=editFamilyInformationWithSearchResults")
  public String renderWithSearchResults(RenderRequest request, Model model) throws fi.koku.services.entity.customer.v1.ServiceFault {
    String userPic = UserInfoUtils.getPicFromSession(request);
    String surname = request.getParameter("surname");
    String pic = request.getParameter("pic");
    List<Person> searchedUsers = familyHelper.searchUsers(surname, pic, userPic);
    
    CustomerType customer = customerService.opGetCustomer(userPic, CustomerServiceFactory.createAuditInfoType(PyhConstants.COMPONENT_PYH, userPic));
    
    logger.debug("EditFamilyInformationController.render(): returning customer: " + customer.getEtunimetNimi() + " " + customer.getSukuNimi() + ", " + customer.getHenkiloTunnus());

    Person user = new Person(customer);
    
    DependantsAndFamily daf = familyHelper.getDependantsAndFamily(userPic);
    FamilyIdAndFamilyMembers fidm = familyHelper.getOtherFamilyMembers(userPic);
    
    request.setAttribute("search", true);
    model.addAttribute("user", user);
    model.addAttribute("dependants", daf.getDependants());
    model.addAttribute("otherFamilyMembers", fidm.getFamilyMembers());
    model.addAttribute("parentsFull", familyHelper.isParentsSet(userPic));
    model.addAttribute("messages", messageHelper.getSentMessages(user));
    model.addAttribute("searchedUsers", searchedUsers);
    
    // user family shouldn't be null because it was created in the other render method already
    Family family = daf.getFamily();
    String familyCommunityId = family.getCommunityId();
    model.addAttribute("familyCommunityId", familyCommunityId);
    
    return "editfamilyinformation";
  }
  
  @ActionMapping(params = "action=addDependantAsFamilyMember")
  public void addDependantAsFamilyMember(ActionRequest request, @RequestParam String dependantPic, ActionResponse response) 
    throws TooManyFamiliesException, FamilyNotFoundException, fi.koku.services.entity.community.v1.ServiceFault {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    insertDependantToFamily(userPic, dependantPic, CommunityRole.CHILD);
    response.setRenderParameter("action", "editFamilyInformation");
  }

  @ActionMapping(params = "action=removeFamilyMember")
  public void removeFamilyMember(ActionRequest request, @RequestParam String familyMemberPic, ActionResponse response)
    throws fi.koku.services.entity.community.v1.ServiceFault {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    removeFamilyMember(familyMemberPic, userPic);
    response.setRenderParameter("action", "editFamilyInformation");
  }
  
  @ActionMapping(params = "action=removeDependant")
  public void removeDependant(ActionRequest request, @RequestParam String familyMemberPic, ActionResponse response)
    throws fi.koku.services.entity.community.v1.ServiceFault {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    for (Dependant d : familyHelper.getDependantsAndFamily(userPic).getDependants()) {
      if (d.getPic().equals(familyMemberPic)) {
        d.setMemberOfUserFamily(false);
      }
    }
    removeFamilyMember(familyMemberPic, userPic);
    response.setRenderParameter("action", "editFamilyInformation");
  }
  
  @ActionMapping(params = "action=searchUsers")
  public void searchUsers(ActionRequest request, ActionResponse response) {
    String surname = request.getParameter("searchSurname");
    String pic = request.getParameter("searchPic");
    
    // TODO: refactoring: this method will not be called but we call render method directly
    //pyhDemoService.searchUsers(surname, pic /*, userPic /*customer id == user pic*/, userPic);
    response.setRenderParameter("surname", surname);
    response.setRenderParameter("pic", pic);
    response.setRenderParameter("action", "editFamilyInformationWithSearchResults");
  }
  
  @ActionMapping(params = "action=addUsersToFamily")
  public void addUsersToFamily(ActionRequest request, ActionResponse response, PortletSession session) throws FamilyNotFoundException,
    TooManyFamiliesException, fi.koku.services.entity.customer.v1.ServiceFault, fi.koku.services.entity.community.v1.ServiceFault {
    String userPic = UserInfoUtils.getPicFromSession(request);
    
    HashMap<String, String> personMap = new HashMap<String, String>();
    
    String familyCommunityId = request.getParameter("familyCommunityId");
    String personPicFromJsp = request.getParameter("userPic");
    String personRoleFromJsp = request.getParameter("userRole");
    
    personMap.put(personPicFromJsp, personRoleFromJsp);
    
    boolean childsGuardianshipInformationNotFound = false;
    try {
      
      if (logger.isDebugEnabled()) {
        logger.debug("addPersonsAsFamilyMembers(): adding persons:");
        Set<String> set = personMap.keySet();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
          String personPic = it.next();
          logger.debug("person pic: " + personPic);
        }
      }
      
      if (familyCommunityId == null) {
        throw new FamilyNotFoundException("PyhDemoService.addPersonsAsFamilyMembers: cannot add family members because family community does not exist!");
      }
      
      Set<String> keys = personMap.keySet();
      Iterator<String> si = keys.iterator();
      
      // get current user (unnecessary?)
      // -->
      CustomerType customer;
      fi.koku.services.entity.customer.v1.AuditInfoType customerAuditInfoType = new fi.koku.services.entity.customer.v1.AuditInfoType();
      customerAuditInfoType.setComponent(PyhConstants.COMPONENT_PYH);
      customerAuditInfoType.setUserId(userPic);
      
      customer = customerService.opGetCustomer(userPic, customerAuditInfoType);
      Person user = new Person(customer);
      // <--
      
      while (si.hasNext()) {
        String memberToAddPic = si.next();
        String role = personMap.get(memberToAddPic);
        
        CommunityRole communityRole = CommunityRole.create(role);
        
        // TODO: check generateRecipients 'user' parameter: can we use PIC instead of Person?
        // if we can then getting Person user is unnecessary
        List<String> recipients = generateRecipients(memberToAddPic, user, communityRole, userPic/*current user's pic*/);
        
        if (CommunityRole.PARENT.equals(communityRole) || CommunityRole.FATHER.equals(communityRole) || 
            CommunityRole.MOTHER.equals(communityRole)) {
          messageHelper.sendParentAdditionMessage(familyCommunityId, memberToAddPic, userPic, communityRole);
        } else if (CommunityRole.CHILD.equals(communityRole) && recipients.size() == 0) {
          // we don't have guardian information for the child so we can't send the request
          
          // TODO: nämä tekstit Language-ext.properties
          String messageSubject = "KoKu: puutteelliset tiedot järjestelmässä";
          String messageText = "Järjestelmästä ei löydy lapsen huoltajatietoja. Lapsen HETU: " + memberToAddPic;
          
          SimpleMailMessage mailMessage = new SimpleMailMessage(this.templateMessage);
          mailMessage.setFrom(PyhConstants.KOKU_FROM_EMAIL_ADDRESS);
          mailMessage.setTo(PyhConstants.KOKU_SUPPORT_EMAIL_ADDRESS);
          mailMessage.setSubject(messageSubject);
          mailMessage.setText(messageText);
          
          try {
            this.mailSender.send(mailMessage);
          } catch (MailException me) {
            // even if mail sending fails, PYH operation continues normally
            logger.error("PyhDemoService.addPersonsAsFamilyMembers: sending mail to KoKu support failed!", me);
          }
          
          Log.getInstance().send(userPic, "", "pyh.membership.request", "Cannot send approval request because guardian for child " + memberToAddPic + " is not found");
          throw new GuardianForChildNotFoundException("Guardian for child (pic: " + memberToAddPic + ") not found!");
          
        } else if (recipients.size() == 0) {
          insertInto(userPic, memberToAddPic, communityRole);
        } else {
          messageHelper.sendFamilyAdditionMessage(familyCommunityId, recipients, userPic, memberToAddPic, communityRole);
        }
      }
      
    } catch (GuardianForChildNotFoundException gnfe) {
      logger.error("EditFamilyInformationController.addUsersToFamily() caught GuardianForChildNotFoundException: cannot send membership " +
      		"request because guardian for the child was not found. The child was not added into the family.");
      // show error message in JSP view
      childsGuardianshipInformationNotFound = true;
    }
    
    response.setRenderParameter("childsGuardianshipInformationNotFound", String.valueOf(childsGuardianshipInformationNotFound));
    response.setRenderParameter("action", "editFamilyInformation");
  }
  
  /**
   * Selects persons (PICs) to whom send the confirmation message for a operation, for example adding a dependant 
   * into a family.
   */
  private List<String> generateRecipients(String memberToAddPic, Person user, CommunityRole role, String currentUserPic) 
    throws fi.koku.services.entity.community.v1.ServiceFault, TooManyFamiliesException, FamilyNotFoundException {
    List<String> recipientPics = new ArrayList<String>();
    
    if (CommunityRole.CHILD.equals(role)) {
      CommunityQueryCriteriaType communityCriteria = new CommunityQueryCriteriaType();
      communityCriteria.setCommunityType(CommunityServiceConstants.COMMUNITY_TYPE_GUARDIAN_COMMUNITY);
      
      MemberPicsType memberPics = new MemberPicsType();
      memberPics.getMemberPic().add(memberToAddPic);
      communityCriteria.setMemberPics(memberPics);
      
      CommunitiesType communitiesType = null;
      
      communitiesType = communityService.opQueryCommunities(communityCriteria, CommunityServiceFactory.createAuditInfoType(PyhConstants.COMPONENT_PYH, currentUserPic));
      
      if (communitiesType != null) {
        List<CommunityType> communities = communitiesType.getCommunity();
        Iterator<CommunityType> ci = communities.iterator();
        while (ci.hasNext()) {
          CommunityType community = ci.next();
          MembersType membersType = community.getMembers();
          List<MemberType> members = membersType.getMember();
          Iterator<MemberType> mi = members.iterator();
          while (mi.hasNext()) {
            MemberType member = mi.next();
            if (member.getRole().equals(CommunityServiceConstants.COMMUNITY_ROLE_GUARDIAN)) {
              recipientPics.add(member.getPic());
            }
          }
        }
      }
    } else {
      
      MemberType familyMember = null;
      familyMember = familyHelper.getFamily(currentUserPic).getOtherParent(user.getPic());
      
      if (familyMember != null) {
        recipientPics.add(familyMember.getPic());
      }
      
      Family family = null;
      try {
        family = familyHelper.getFamily(memberToAddPic);
      } catch (FamilyNotFoundException fnfe) {
        // person to be added as family member has no family,
        // continue normally
      }
      
      if (family != null) {
        for (MemberType member : family.getParents()) {
          if (!member.getPic().equals(user.getPic()) && !recipientPics.contains(member.getPic())) {
            recipientPics.add(member.getPic());
          }
        }
      }
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("generateRecipients(): returning pics:");
      Iterator<String> rpi = recipientPics.iterator();
      while (rpi.hasNext()) {
        logger.debug("recipient pic: " + rpi.next());
      }
    }
    
    return recipientPics;
  }
  
  /**
   * Inserts a new member into a family community.
   */
  private void insertInto(String toFamilyPic, String memberToAddPic, CommunityRole role) throws TooManyFamiliesException, 
    FamilyNotFoundException, fi.koku.services.entity.community.v1.ServiceFault {
    Family family = null;
    family = familyHelper.getFamily(toFamilyPic);
    
    if (family != null) {
      family.addFamilyMember(memberToAddPic, role.getRoleID());
      
      communityService.opUpdateCommunity(family.getCommunity(), CommunityServiceFactory.createAuditInfoType(PyhConstants.COMPONENT_PYH, toFamilyPic));
      Log.getInstance().update(toFamilyPic, "", "pyh.family.community", "Adding person " + memberToAddPic + " into family");
      
      if (logger.isDebugEnabled()) {
        logger.debug("insertInto(): members after insert:");
        List<MemberType> members = family.getAllMembers();
        Iterator<MemberType> mti = members.iterator();
        while (mti.hasNext()) {
          MemberType m = mti.next();
          logger.debug("member pic: " + m.getPic());
        }
      }
    }
  }
  
  /**
   * Inserts new community with one member.
   * 
   */
  private String addFamily(String userPic) throws fi.koku.services.entity.community.v1.ServiceFault {
    logger.debug("calling addFamily() with parameter:");
    logger.debug("userPic: " + userPic);
    
    MemberType member = new MemberType();
    member.setPic(userPic);
    member.setRole(CommunityServiceConstants.COMMUNITY_ROLE_PARENT);
    
    MembersType members = new MembersType();
    members.getMember().add(member);
    
    CommunityType community = new CommunityType();
    community.setMembers(members);
    community.setType(CommunityServiceConstants.COMMUNITY_TYPE_FAMILY);
    
    String communityId = null;
    communityId = communityService.opAddCommunity(community, CommunityServiceFactory.createAuditInfoType(PyhConstants.COMPONENT_PYH, userPic));
    Log.getInstance().update(userPic, "", "pyh.family.community", "Adding family for user " + userPic);
    
    return communityId;
  }
  
  /**
   * Inserts a dependant into a family.
   */
  private void insertDependantToFamily(String userPic, String dependantPic, CommunityRole role) throws TooManyFamiliesException, 
    FamilyNotFoundException, fi.koku.services.entity.community.v1.ServiceFault {
    
    Family family = null;
    family = familyHelper.getFamily(userPic);
    
    if (family != null) {
      family.addFamilyMember(dependantPic, role.getRoleID());
      
      communityService.opUpdateCommunity(family.getCommunity(), CommunityServiceFactory.createAuditInfoType(PyhConstants.COMPONENT_PYH, userPic));
      Log.getInstance().update(userPic, "", "pyh.family.community", "Adding dependant " + dependantPic + " into family");
      
      if (logger.isDebugEnabled()) {
        logger.debug("insertDependantToFamily(): family members after insert:");
        List<MemberType> members = family.getAllMembers();
        Iterator<MemberType> mi = members.iterator();
        while (mi.hasNext()) {
          MemberType m = mi.next();
          logger.debug("member pic: " + m.getPic());
        }
      }
    }
  }
  
  /**
   * Removes a family member from a family community.
   */
  private void removeFamilyMember(String familyMemberPic, String userPic) throws fi.koku.services.entity.community.v1.ServiceFault {
    CommunityQueryCriteriaType communityQueryCriteria = new CommunityQueryCriteriaType();
    communityQueryCriteria.setCommunityType(CommunityServiceConstants.COMMUNITY_TYPE_FAMILY);
    
    MemberPicsType memberPics = new MemberPicsType();
    memberPics.getMemberPic().add(familyMemberPic);
    communityQueryCriteria.setMemberPics(memberPics);
    
    AuditInfoType communityAuditInfoType = CommunityServiceFactory.createAuditInfoType(PyhConstants.COMPONENT_PYH, userPic);
    
    CommunitiesType communitiesType = communityService.opQueryCommunities(communityQueryCriteria, communityAuditInfoType);
    
    if (communitiesType != null) {
      List<CommunityType> communityType = communitiesType.getCommunity();
      Iterator<CommunityType> ci = communityType.iterator();
      CommunityType community = null;
      while (ci.hasNext()) {
        community = ci.next();
        MembersType membersType = community.getMembers();
        List<MemberType> members = membersType.getMember();
        Iterator<MemberType> mi = members.iterator();
        while (mi.hasNext()) {
          MemberType member = mi.next();
          if (member.getPic().equals(familyMemberPic) && isMemberOfCommunity(userPic, members)) {
            members.remove(member);
            
            // TODO: place outside the loop
            communityService.opUpdateCommunity(community, communityAuditInfoType);
            Log.getInstance().update(userPic, "", "pyh.family.community", "Removing family member " + familyMemberPic + " from family");
            
            if (logger.isDebugEnabled()) {
              logger.debug("removeFamilyMember(): members after removing:");
              Iterator<MemberType> mit = members.iterator();
              while (mit.hasNext()) {
                MemberType m = mit.next();
                logger.debug("member pic: " + m.getPic());
              }
            }
            
            return;
          }
        }
      }
    }
  }
  
  /**
   * Returns true if person is member of a community. Otherwise returns false.
   * 
   * personPic - PIC of the person
   * members - list of members of the community
   */
  private boolean isMemberOfCommunity(String personPic, List<MemberType> members) {
    Iterator<MemberType> mi = members.iterator();
    while (mi.hasNext()) {
      MemberType member = mi.next();
      if (personPic.equals(member.getPic())) {
        return true;
      }
    }
    return false;
  }
  
}
