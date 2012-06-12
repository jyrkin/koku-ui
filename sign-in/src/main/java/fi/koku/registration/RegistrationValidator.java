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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validator for Registration form
 * 
 * @author mikkope
 *
 */
@Component("registrationValidator")
public class RegistrationValidator implements Validator  {
  
  private static final Logger log = LoggerFactory.getLogger(RegistrationValidator.class);
  
  @Override
  public boolean supports(Class clazz) {
    //just validate the Registration instances
    return Registration.class.isAssignableFrom(clazz);
  }
 
  @Override
  public void validate(Object target, Errors errors) {
 
    log.error("DEBUG_ validator");
    
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "useraccount",
        "ui.registration.useraccount.cannot.be.empty", "Useraccount is required.");
   //#TODO# Add check if useraccount is usable (service call)
    
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
         "ui.registration.email.cannot.be.empty", "Email is required.");
    
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phonenumber",
        "ui.registration.phonenumber.cannot.be.empty", "Phonenumber is required.");
    
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
        "ui.registration.password.cannot.be.empty", "Password is required.");
    //#TODO# Add more spesific password validity checks
    
  }

}
