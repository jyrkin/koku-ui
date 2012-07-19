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

package fi.koku.registration;

import fi.koku.settings.KoKuPropertiesUtil;

import fi.koku.services.utility.portal.v1.PortalUserServicePortType;
import fi.koku.services.utility.portaluser.v1.PortalUserServiceFactory;

/**
 * Service factory for registration
 * 
 * @author mikkope
 *
 */
public class ServiceFactory {

  public PortalUserServicePortType getPortalUserService() {
    String uid = KoKuPropertiesUtil.get(RegistrationConstants.PORTAL_USER_SERVICE_USER_ID);
    String pwd = KoKuPropertiesUtil.get(RegistrationConstants.PORTAL_USER_SERVICE_PASSWORD);
    String ep = KoKuPropertiesUtil.get(RegistrationConstants.PORTAL_USER_SERVICE_ENDPOINT);
    PortalUserServiceFactory factory = new PortalUserServiceFactory(uid, pwd, ep);
    return factory.getPortalUserService();
   }

}

