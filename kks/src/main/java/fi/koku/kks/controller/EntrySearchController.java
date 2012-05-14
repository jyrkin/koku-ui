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

import java.util.Arrays;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fi.koku.kks.model.KKSCollection;
import fi.koku.kks.model.KksService;
import fi.koku.kks.model.Person;
import fi.koku.kks.ui.common.utils.Utils;

/**
 * Controller for entry searches
 * 
 * @author tuomape
 */
@Controller(value = "entrySearchController")
@RequestMapping(value = "VIEW")
public class EntrySearchController {

  @Autowired
  @Qualifier("kksService")
  private KksService kksService;

  private static final Logger LOG = LoggerFactory.getLogger(EntrySearchController.class);

  @RenderMapping(params = "action=showSearchResult")
  public String showResults(PortletSession session, @ModelAttribute(value = "child") Person child,
      @RequestParam(value = "description") String description,
      @RequestParam(value = "classification") String classification, RenderResponse response, Model model) {
    LOG.debug("show search result");
    String tmp[] = classification.replaceAll(" ", "").split(",");
    List<String> names = Arrays.asList(tmp);
    List<KKSCollection> collections = kksService.searchKksCollections(names, child.getPic(),
        Utils.getPicFromSession(session));

    String pic = Utils.getPicFromSession(session);
    
    model.addAttribute("child", child);
    model.addAttribute("collections", collections);
    model.addAttribute("description", description);
    model.addAttribute("authorized", kksService.getAuthorizedRegistries(pic));

    return "search_result";
  }

  @ActionMapping(params = "action=searchEntries")
  public void search(@ModelAttribute(value = "child") Person child,
      @RequestParam(value = "classification") String classification,
      @RequestParam(value = "description") String description, ActionResponse response, SessionStatus sessionStatus) {
    LOG.debug("search entries");

    response.setRenderParameter("action", "showSearchResult");
    response.setRenderParameter("pic", child.getPic());
    response.setRenderParameter("description", description);
    response.setRenderParameter("classification", classification);
    sessionStatus.setComplete();
  }

  @ModelAttribute("child")
  public Person getchild(PortletSession session, @RequestParam(value = "pic") String pic) {
    return kksService.searchCustomer(pic, Utils.getPicFromSession(session));
  }

}
