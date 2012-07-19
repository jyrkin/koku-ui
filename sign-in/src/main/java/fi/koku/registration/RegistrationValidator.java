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

import org.apache.commons.lang.StringUtils;
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
    
    Registration r = (Registration)target;
    
    //Email
    CommonValidationUtils.validateEmail(r.getEmail(), errors);
    
    CommonValidationUtils.validateUserAccount(r.getUseraccount(), errors);
    
    //Preferred contact method and phonenumber
    if( "phone".equals( r.getPreferredContactMethod() ) ){
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phonenumber",
          "ui.registration.phonenumber.cannot.be.empty", "Phonenumber is required.");
    }
    
    //Password validations
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
        "ui.registration.password.cannot.be.empty", "Password is required.");
    
    if(r.getPassword()!=null && !r.getPassword().equals(r.getPassword2())){
        errors.rejectValue("password","ui.registration.password.does.not.match");
        errors.rejectValue("password2","ui.registration.password.does.not.match");
    }else{
        //Passwords match, check against more spesific rules
      CommonValidationUtils.validatePassword(r.getPassword(), errors);
    }
    
    //Terms of use
    if( !r.isAcceptTermsOfUse()){
      errors.rejectValue("acceptTermsOfUse", "ui.registration.accepttermsofuse.false");
    }
    
    
  }

 
  
  
}
