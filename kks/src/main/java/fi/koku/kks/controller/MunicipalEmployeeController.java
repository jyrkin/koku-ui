/*
 * Copyright 2012 Ixonos Plc, Finland. All rights reserved.
 * 
 * This file is part of Kohti kumppanuutta.
 *
 * This file is licensed under GNU LGPL version 3.
 * Please see the 'license.txt' file in the root directory of the package you received.
 * If you did not receive a license, please contact the copyright holder
 * (kohtikumppanuutta@ixonos.com).
 *
 */
package fi.koku.kks.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;
import javax.validation.Valid;

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

import fi.koku.kks.model.Creatable;
import fi.koku.kks.model.GroupCreatable;
import fi.koku.kks.model.KksService;
import fi.koku.kks.model.Person;
import fi.koku.kks.ui.common.utils.Utils;
import fi.koku.services.entity.kks.v1.ServiceFault;
import fi.koku.services.entity.person.v1.Group;

/**
 * Controller for role municipal employee
 * 
 * @author tuomape
 * 
 */
@Controller(value = "municipalEmplouyeeController")
@RequestMapping(value = "VIEW")
public class MunicipalEmployeeController {

  @Autowired
  @Qualifier("kksService")
  private KksService kksService;

  @Autowired
  @Qualifier("groupCreationValidator")
  private Validator groupCreationValidator;

  private static final Logger LOG = LoggerFactory
      .getLogger(MunicipalEmployeeController.class);

  @InitBinder("groupCreationValidator")
  public void initBinder(WebDataBinder binder) {
    binder.setValidator(groupCreationValidator);
  }

  @RenderMapping(params = "action=showEmployee")
  public String show(PortletSession session, RenderResponse response,
      @RequestParam(value = "childs", required = false) String[] childs,
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "selected", required = false) String selected,
      Model model) {
    LOG.debug("show employee");

    String pic = Utils.getPicFromSession(session);

    model.addAttribute("childs", toChilds(childs, pic));
    List<String> groups = kksService.getGroups(pic);

    if (StringUtils.isNotEmpty(selected)) {
      Group group = kksService.getGroup(pic, selected);
      model.addAttribute("selected", selected);
      model.addAttribute("childs", new ArrayList<Person>());
      if (childs != null && childs.length > 0) {
        model.addAttribute("selectedPic", childs[0]);
      }

      if (group != null) {
        model.addAttribute("groupChilds", group.getPersons());
      }
    }

    model.addAttribute("groups", groups);

    if (search != null) {
      model.addAttribute("search", search);
    }

    if (error != null) {
      model.addAttribute("error", error);
    }
    return "search";
  }

  @RenderMapping(params = "action=showGroup")
  public String showGroup(PortletSession session, RenderResponse response,
      @RequestParam(value = "selected") String selected,
      @RequestParam(value = "message", required = false) String message,
      @ModelAttribute(value = "group") GroupCreatable groupCreatable,
      BindingResult bindingResult, Model model) {
    LOG.debug("show group " + selected);

    String pic = Utils.getPicFromSession(session);

    setContent(selected, groupCreatable, model, pic, true);
    groupCreatable.setName("");
    groupCreatable.setType("");
    if (StringUtils.isNotEmpty(message)) {
      model.addAttribute("message", message);
    }
    return "group";
  }

  private void setContent(String selected, GroupCreatable groupCreatable,
      Model model, String pic, boolean setChilds) {
    model.addAttribute("selected", selected);
    model.addAttribute("creatables",
        kksService.searchPersonCreatableCollections(null, ""));

    Group group = kksService.getGroup(pic, selected);
    model.addAttribute("childs", new ArrayList<Person>());

    if (group != null) {
      List<String> pics = new ArrayList<String>();

      for (fi.koku.services.entity.person.v1.Person p : group.getPersons()) {
        pics.add(p.getPic());
      }

      try {

        if (setChilds && groupCreatable.getCustomers() == null
            || groupCreatable.getCustomers().length == 0) {
          groupCreatable.setCustomers(pics.toArray(new String[pics.size()]));
        }
        model.addAttribute("groupChilds",
            kksService.getGroupCollections(pic, pics));
        model.addAttribute("group", groupCreatable);
      } catch (ServiceFault e) {
        LOG.error("Failed to get group: " + e.toString());
      }
    }
  }

  @ActionMapping(params = "action=addCollectionsForGroup")
  public void addCollections(PortletSession session,
      @RequestParam(value = "cancel", required = false) Boolean cancel,
      @RequestParam(value = "selected") String selected,
      @ModelAttribute(value = "group") GroupCreatable groupCreation,
      BindingResult bindingResult, ActionResponse response,
      SessionStatus sessionStatus) {
    LOG.info("addCollectionsForGroup");

    if (cancel != null && cancel) {
      response.setRenderParameter("action", "showEmployee");
      response.setRenderParameter("childs", new String[] { "" });
      response.setRenderParameter("selected", selected);
    } else {
      groupCreationValidator.validate(groupCreation, bindingResult);

      String pic = Utils.getPicFromSession(session);

      String tmp[] = groupCreation.getCustomers();
      List<String> pics = Arrays.asList(tmp);

      if (bindingResult.hasErrors()) {
        response.setRenderParameter("action", "showGroupErrors");
        response.setRenderParameter("selected", selected);
      } else {

        Creatable a = Creatable.create(groupCreation.getType());
        String name = "".equals(groupCreation.getName()) ? a.getName()
            : groupCreation.getName();

        boolean success = kksService.addCollectionForGroup(pic, pics,
            a.getId(), name);

        response.setRenderParameter("action", "showGroup");
        response.setRenderParameter("selected", selected);

        if (!success) {
          response.setRenderParameter("error", "ui.kks.error.group.creation");
        } else {
          response.setRenderParameter("message", "ui.kks.group.info");
        }
      }
    }
    sessionStatus.setComplete();
  }

  @RenderMapping(params = "action=showGroupErrors")
  public String showErrors(PortletSession session,
      @RequestParam(value = "selected") String selected,
      @RequestParam(value = "error", required = false) String error,
      @ModelAttribute(value = "group") GroupCreatable groupCreation,
      BindingResult bindingResult, RenderResponse response, Model model) {
    model.addAttribute("selected", selected);
    groupCreationValidator.validate(groupCreation, bindingResult);

    String pic = Utils.getPicFromSession(session);
    setContent(selected, groupCreation, model, pic, false);

    if (StringUtils.isNotEmpty(error)) {
      model.addAttribute("error", error);
    }
    return "group";
  }

  @ActionMapping(params = "action=searchChild")
  public void fecthChild(PortletSession session,
      @ModelAttribute(value = "child") Person child,
      BindingResult bindingResult, ActionResponse response,
      SessionStatus sessionStatus) {
    LOG.info("search child");

    String pic = Utils.getPicFromSession(session);
    String error = validateInput(child, pic);
    Person p = error == null ? kksService.searchPerson(child,
        Utils.getPicFromSession(session)) : null;

    if (p != null) {
      response.setRenderParameter("action", "showChild");
      response.setRenderParameter("pic", p.getPic());
    } else {
      response.setRenderParameter("action", "showEmployee");
      response.setRenderParameter("childs", new String[] { "" });
      response.setRenderParameter("search", "true");

      if (error != null) {
        response.setRenderParameter("error", error);
      }
    }
    sessionStatus.setComplete();
  }

  @ActionMapping(params = "action=searchGroup")
  public void fecthGroup(PortletSession session,
      @RequestParam(value = "selected") String selected,
      @ModelAttribute(value = "child") Person child,
      BindingResult bindingResult, ActionResponse response,
      SessionStatus sessionStatus) {
    LOG.info("search group");

    response.setRenderParameter("action", "showEmployee");
    response.setRenderParameter("childs", new String[] { "" });
    response.setRenderParameter("selected", selected);

    sessionStatus.setComplete();
  }

  @ActionMapping(params = "action=toGroupActions")
  public void toGroup(PortletSession session,
      @RequestParam(value = "selected") String selected,
      ActionResponse response, SessionStatus sessionStatus) {
    LOG.info("to group");

    response.setRenderParameter("action", "showGroup");
    response.setRenderParameter("selected", selected);
    sessionStatus.setComplete();
  }

  private String validateInput(Person child, String pic) {
    if (child.getPic().trim().equals(pic)) {
      return "ui.kks.illegal.pic.search";
    }

    return null;
  }

  @ModelAttribute("child")
  public Person getCommandObject() {
    LOG.debug("get entry command object");
    return new Person();
  }

  @ModelAttribute("group")
  public GroupCreatable getGroupCommandObject() {
    LOG.debug("get group creatable command object");
    return new GroupCreatable();
  }

  private List<Person> toChilds(String[] childIds, String user) {
    List<Person> tmp = new ArrayList<Person>();

    if (childIds != null) {
      for (String s : childIds) {
        if (!"".equals(s)) {
          tmp.add(kksService.searchCustomer(s, user));
        }
      }
    }
    return tmp;
  }

}
