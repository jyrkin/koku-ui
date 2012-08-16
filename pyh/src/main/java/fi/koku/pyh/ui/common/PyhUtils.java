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
package fi.koku.pyh.ui.common;

import javax.portlet.ActionResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Common helper methods for PYH portlet
 * 
 * @author Ixonos / hekkata
 * 
 */
public final class PyhUtils {

  private static final Logger LOG = LoggerFactory
      .getLogger(PyhUtils.class);
  
  private PyhUtils() {

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
