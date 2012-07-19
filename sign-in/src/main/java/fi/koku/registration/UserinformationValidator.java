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
 * Validator for Userinformation form
 * 
 * @author mikkope
 *
 */
@Component("userinformationValidator")
public class UserinformationValidator implements Validator  {
  
  private static final Logger log = LoggerFactory.getLogger(UserinformationValidator.class);
  
  @Override
  public boolean supports(Class clazz) {
    return Userinformation.class.isAssignableFrom(clazz);
  }
 
  @Override
  public void validate(Object target, Errors errors) {
    Userinformation ui = (Userinformation)target;
  //Email
    CommonValidationUtils.validateEmail(ui.getEmail(), errors);
    
    //Password validations
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentpassword",
        "ui.registration.password.cannot.be.empty", "Password is required.");
    
    //New password validations
    if(StringUtils.isNotBlank(ui.getNewpassword())){
      //Empty textfield will be ignored
      if( !ui.getNewpassword().equals(ui.getNewpassword2())){
        errors.rejectValue("newpassword","ui.registration.password.does.not.match");
        errors.rejectValue("newpassword2","ui.registration.password.does.not.match");
      }else{
        //Passwords match, check against more spesific rules
        CommonValidationUtils.validatePassword(ui.getNewpassword(), errors);
      }
      
    }
    
  }

}
