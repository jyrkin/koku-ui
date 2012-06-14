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
import org.springframework.validation.Errors;

/**
 * Utilities to be used in validator classes. 
 * 
 * @author mikkope
 *
 */


public abstract class CommonValidationUtils {

  //May contain only a-z,A-Z, numbers and special characters "-_."
  private static final String USERACCOUNT_REGEXP = "([A-Za-z0-9\\-\\_\\.]+)";
  
  //Allowed chars
  private static final String PASSWORD_REGEXP = "([A-Za-z0-9\\@\\#\\$\\%]+)";
  //Require one or more digit, lowercase, uppercase, special char and length 8-20
  private static final String PASSWORD_REGEXP2 = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";
  
  
  public static void validateEmail(String email, Errors errors) {
    if(StringUtils.isNotBlank(email)){
      //Simple check for email
      if( !email.contains("@")){
        errors.rejectValue("email","ui.registration.email.invalid");
        
      }
    }else{
      errors.reject("email","ui.registration.email.cannot.be.empty");
    }
  }

  /**
   * Validate useraccount
   * 
   * @param useraccount Useraccount as string
   * @param errors 
   */
  public static void validateUserAccount(String useraccount, Errors errors) {
    
    if(StringUtils.isNotBlank(useraccount)){
        
        //At least 6 chars
        if( useraccount.length() < 6 ){
          errors.rejectValue("useraccount", "ui.registration.useraccount.size.limit", "Useraccount has to contain at least 6 characters");
        }
       
        if (!useraccount.matches( USERACCOUNT_REGEXP )){
          errors.rejectValue("useraccount", "ui.registration.useraccount.characters.limit", "Useraccount has to consist of a-z, A-Z, 0-9");
        }
      
    } else {
      errors.rejectValue("useraccount", "ui.registration.useraccount.cannot.be.empty", "Useraccount is required.");
    }
       
  }

  
  /**
   * Validate password
   * 
   * @param password Password as string
   * @param errors
   */
  public static void validatePassword(String password, Errors errors) {
    
    if(StringUtils.isNotBlank(password)){
        
        //At least 8 chars but max 20 chars
        if( password.length() < 8 && password.length() < 21){
          errors.rejectValue("password", "ui.registration.password.size.limit", "Password has to contain at least 8 characters");
        }
        
        if (!password.matches( PASSWORD_REGEXP ) || 
            !password.matches( PASSWORD_REGEXP2 )){
              errors.rejectValue("password", "ui.registration.password.characters.limit", 
                  "Password has to contain at least one number, one lowercase, one uppercase and @#$%");
        }
      
    } else {
      errors.rejectValue("password", "ui.registration.password.cannot.be.empty", "Password is required.");
    }
       
  }
  
}
