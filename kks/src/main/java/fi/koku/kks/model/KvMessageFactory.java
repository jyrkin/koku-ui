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
package fi.koku.kks.model;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import fi.koku.services.entity.kv.v1.KokuCommonMessagingService;
import fi.koku.services.entity.kv.v1.KokuCommonMessagingService_Service;
import fi.koku.settings.KoKuPropertiesUtil;


public class KvMessageFactory {

  private final URL wsdlLocation = getClass().getClassLoader().getResource("wsdl/kvService.wsdl");

  public KvMessageFactory() {

  }

  
  public KokuCommonMessagingService getService() {

    KokuCommonMessagingService_Service ft = new KokuCommonMessagingService_Service(wsdlLocation, new QName(
        "http://services.koku.fi/entity/kv/v1", "KokuCommonMessagingService"));

    KokuCommonMessagingService port = ft.getKokuCommonMessagingServicePort();
    String epAddr = KoKuPropertiesUtil.get("kks.kv.service.full.endpointaddress");

    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    return port;
  }

}