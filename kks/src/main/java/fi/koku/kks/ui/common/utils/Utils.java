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
package fi.koku.kks.ui.common.utils;

import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.support.SessionStatus;

import fi.koku.kks.controller.ChildController;
import fi.koku.portlet.filter.userinfo.UserInfo;

/**
 * Common helper methods for KKS portlet
 * 
 * @author Ixonos / tuomape
 * 
 */
public final class Utils {

  private static final Logger LOG = LoggerFactory
      .getLogger(Utils.class);
  
  private Utils() {

  }

  public static String getPicFromSession(PortletSession session) {
    UserInfo info = Utils.getUserInfoFromSession(session);
    if (info == null) {
      return "";
    }
    return info.getPic();
  }

  public static UserInfo getUserInfoFromSession(PortletSession session) {
    return (UserInfo) session.getAttribute(UserInfo.KEY_USER_INFO);
  }
  
  
  public static String picToDate(String pic) {
    String tmp = pic.substring(0,5);
    StringBuilder sb = new StringBuilder();
    
    int counter = 0;
    for ( char a : tmp.toCharArray() ) {      
      if ( counter == 2 ) {
        sb.append(".");
        counter = 0;   
      } 
      sb.append(a);     
      counter++;
    }
    return sb.toString();
  }
  
  public static void setCsrfErrorPage(ActionResponse response,
      SessionStatus sessionStatus) {
    LOG.error("Possible CSRF attack detected, forwarding to error view!");
    response.setRenderParameter("action", "showCsrfError");
    sessionStatus.setComplete();
  }
  
  public static String getCsrfErrorPage() {
    return "error";
  }

}
