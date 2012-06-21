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
package fi.koku.portlet.filter.userinfo;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

public class SecurityUtils {
  
  public static final String KEY_CSRF_TOKEN = "koku_csrf_token";
  
  public static String getCSRFTokenFromSession(PortletRequest pReq) {
    PortletSession p = pReq.getPortletSession();
    if(p!=null){
      return getCSRFTokenFromSession(p);
    }
    return null;
  }

  public static String getCSRFTokenFromSession(PortletSession session) {
    return (String) session.getAttribute(KEY_CSRF_TOKEN);
  }
  
  
  public static boolean hasValidCSRFToken( PortletRequest pr ) {
      String sessionToken = getCSRFTokenFromSession(pr);      
      if ( sessionToken != null ) {
        return sessionToken.equals(pr.getParameter(KEY_CSRF_TOKEN));
      }
      return false;
  }
  

  

}
