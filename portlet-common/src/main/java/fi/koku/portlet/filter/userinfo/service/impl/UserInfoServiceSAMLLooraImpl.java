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
package fi.koku.portlet.filter.userinfo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.koku.portlet.filter.userinfo.UserInfo;
import fi.koku.portlet.filter.userinfo.UserInfoConstants;
import fi.koku.portlet.filter.userinfo.UserInfoFactory;
import fi.koku.portlet.filter.userinfo.service.UserInfoService;
import fi.koku.services.entity.person.v1.Person;
import fi.koku.services.entity.person.v1.PersonConstants;
import fi.koku.services.entity.person.v1.PersonService;

/**
 * This implementation gets user information in SAML Loora environment
 * 
 * @author tuomape
 *
 */
public class UserInfoServiceSAMLLooraImpl implements UserInfoService{

  private static final Logger LOG = LoggerFactory.getLogger(UserInfoServiceLooraImpl.class);
  
  public UserInfo getUserInfoById(String pic) {
    
    UserInfo ret = null;
    
    if(StringUtils.isNotBlank(pic)){
        PersonService ps = new PersonService();
        List<String> user = new ArrayList<String>(1);
        user.add(pic);//Add current user
        
        try{
          List<Person> persons = ps.getPersonsByPics(user, PersonConstants.PERSON_SERVICE_DOMAIN_OFFICER, pic, UserInfoConstants.COMPONENT_USER_INFO_FILTER);
          Person p = persons.get(0);//Get the first one (actually should there should be only 0 or 1 persons in list)
          
          if ( p != null && p.getPic() != null && !"".equals(p.getPic())) {
        	  ret = UserInfoFactory.instance().createUserInfo(pic, p.getPic(), p.getFname(), p.getSname());
          }
        }catch(Exception e){
          LOG.error("Person not found with with uid="+pic);
        }
    }
    return ret;
  }
}
