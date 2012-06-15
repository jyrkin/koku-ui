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

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import fi.koku.kks.model.GroupCreatable;

@Component("groupCreationValidator")
public class GroupCreationValidator implements Validator{

  @Override
  public boolean supports(Class clazz) {
    //just validate the Message instances
    return GroupCreatable.class.isAssignableFrom(clazz);
  }
 
  @Override
  public void validate(Object target, Errors errors) {
     
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type",
         "NotEmpty.creation.field", "Type is required.");
    
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name",
        "NotEmpty.creation.name", "Name is required.");
    
    GroupCreatable gc = (GroupCreatable)target;
    
    if ( gc.getCustomers() == null || gc.getCustomers().length == 0 ) {
      errors.rejectValue("customers", "ui.kks.error.group.child.needed");
    }
  }
}
