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

import javax.portlet.PortletSession;

import fi.koku.portlet.filter.userinfo.UserInfo;

/**
 * Common helper methods for KKS portlet
 * 
 * @author Ixonos / tuomape
 * 
 */
public final class Utils {

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

}
