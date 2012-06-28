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

import java.util.Locale;

import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import fi.koku.kks.model.KksService;
import fi.koku.kks.model.Message;
import fi.koku.kks.model.Person;
import fi.koku.kks.ui.common.utils.Utils;

/**
 * Controller for child info.
 * 
 * @author tuomape
 * 
 */
@Controller(value = "messageController")
@RequestMapping(value = "VIEW")
public class MessageController {
  
  @Autowired
  @Qualifier("kksService")
  private KksService kksService;  
    
  @Autowired
  @Qualifier("messageValidator")
  private Validator messageValidator;
  
  @Autowired
  private ApplicationContext context;

  @ActionMapping(params = "action=toMessage")
  public void toChildInfo(
      @RequestParam(value = "collectionName") String collectionName,
      @RequestParam(value = "pic") String pic,
      @RequestParam(value = "childName") String childName,
      @RequestParam(value = "fromGroup", required = false) String fromGroup,
      @RequestParam(value = "selected", required = false) String selected,
      ActionResponse response,
      SessionStatus sessionStatus) {
    
    response.setRenderParameter("action", "showMessage");
    response.setRenderParameter("collectionName", collectionName );
    response.setRenderParameter("pic", pic );
    response.setRenderParameter("childName", pic );
    
    
    if (StringUtils.isNotEmpty(fromGroup)) {
      response.setRenderParameter("fromGroup", fromGroup);
    }
    
    if (StringUtils.isNotEmpty(selected)) {
      response.setRenderParameter("selected", selected);
    }
    
    sessionStatus.setComplete();
  }

  @RenderMapping(params = "action=showMessage")
  public String show(PortletSession session,
      @ModelAttribute(value = "child") Person child,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "childName") String childName,
      @RequestParam(value = "collectionName") String collectionName,
      @RequestParam(value = "fromGroup", required = false) String fromGroup,
      @RequestParam(value = "selected", required = false) String selected,
      @ModelAttribute(value = "kks_message") Message message,
      BindingResult errors,
      RenderResponse response, Model model) {
    model.addAttribute("childName", childName);
    model.addAttribute("collectionName", collectionName);
    model.addAttribute("error", error);
    
    if (StringUtils.isNotEmpty(fromGroup)) {
      model.addAttribute("fromGroup", fromGroup);
    }
    
    if (StringUtils.isNotEmpty(selected)) {
      model.addAttribute("selected", selected);
    }
    
    return "message";
  }
  
  @RenderMapping(params = "action=showMessageErrors")
  public String showErrors(PortletSession session,
      @ModelAttribute(value = "child") Person child,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "childName") String childName,
      @RequestParam(value = "collectionName") String collectionName,
      @RequestParam(value = "fromGroup", required = false) String fromGroup,
      @RequestParam(value = "selected", required = false) String selected,
      RenderResponse response, Model model) {
    model.addAttribute("childName", childName);
    model.addAttribute("collectionName", collectionName);
    model.addAttribute("error", error);
    return "message";
  }

  @ActionMapping(params = "action=sendMessage")
  public void send(PortletSession session, @RequestParam(value = "collectionName") String collectionName, @RequestParam(value = "childName") String childName, 
      @RequestParam(value = "cancel", required = false ) Boolean cancel,
      @RequestParam(value = "fromGroup", required = false) String fromGroup,
      @RequestParam(value = "selected", required = false) String selected,
      @ModelAttribute(value = "child") Person child,
      @ModelAttribute(value = "kks_message") Message message,
      BindingResult errors, ActionResponse response,
      SessionStatus sessionStatus) {
    
    if (StringUtils.isNotEmpty(fromGroup)) {
      response.setRenderParameter("fromGroup", fromGroup);
    }
    
    if (StringUtils.isNotEmpty(selected)) {
      response.setRenderParameter("selected", selected);
    }
    
    
    if ( cancel != null && cancel ) {
      response.setRenderParameter("action", "showChild" );
      response.setRenderParameter("pic", child.getPic());
    } else {
      messageValidator.validate(message, errors);
      
      if ( errors.hasErrors() ) {
        response.setRenderParameter("action", "showMessageErrors" );
        response.setRenderParameter("pic", child.getPic());
        response.setRenderParameter("collectionName", collectionName );
        response.setRenderParameter("childName", childName );
        response.setRenderParameter("error", "ui.kks.send.message.mandatory" );
        sessionStatus.setComplete();
        return;
      }
      
      setMessageExtraInfo(message);
      
      boolean result = kksService.sendMessage(Utils.getPicFromSession(session), message);
            
      if ( !result ) {
        removeDefaultExtraInfo(message);
        response.setRenderParameter("action", "showMessage" );
        response.setRenderParameter("pic", child.getPic());
        response.setRenderParameter("collectionName", collectionName );
        response.setRenderParameter("childName", childName );
        response.setRenderParameter("error", "ui.kks.send.message.failed" );
        sessionStatus.setComplete();
        return;
      }
      
      response.setRenderParameter("action", "showChild");
      response.setRenderParameter("pic", child.getPic());
      response.setRenderParameter("message", "ui.kks.send.message.sent");
    }
    sessionStatus.setComplete();
  }

  private void setMessageExtraInfo(Message message) {
    String messageContent =  message.getMessage();

    if  ( messageContent.endsWith("\n") || messageContent.endsWith("\r\n") ) {
      messageContent = messageContent + context.getMessage("ui.kks.send.extra.info", 
          new Object[] {""}, Locale.getDefault());        
    } else {
      messageContent = messageContent + "\n" + context.getMessage("ui.kks.send.extra.info", 
          new Object[] {""}, Locale.getDefault());      
    }
    message.setMessage(messageContent);
  }  

  private void removeDefaultExtraInfo( Message message ) {
    String messageContent =  message.getMessage();
    String defaultMsg = context.getMessage("ui.kks.send.extra.info", 
        new Object[] {""}, Locale.getDefault());
    
    messageContent = messageContent.replace(defaultMsg, ""); 
    message.setMessage(messageContent);
  }
  
  @ModelAttribute("kks_message")
  public Message getMessage(@RequestParam(value = "pic") String pic, @RequestParam(value = "childName") String childName, @RequestParam(value = "collectionName") String collectionName ) {
    Message m = new Message();
    m.setTitle(childName + ": " + collectionName );
    m.setTargetChild(pic);
    return m;
  }
  
  @ModelAttribute("child")
  public Person getchild(PortletSession session, @RequestParam(value = "pic") String pic) {
    return kksService.searchCustomer(pic, Utils.getPicFromSession(session));
  }
}
