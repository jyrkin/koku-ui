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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

/**
 * Test class for CommonValidationUtils and RegistrationValidator validator
 * 
 * @author mikkope
 *
 */
public class RegistrationValidatorTest {

    RegistrationValidator validator;
  
 
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
      validator = new RegistrationValidator();
    }

    @After
    public void tearDown() throws Exception {
    }
  
    
  @Test
  public void testRegistrationValidation() {

    Registration r = new Registration();
    r.setAcceptTermsOfUse(true);
    r.setDayOfBirth("");
    r.setEmail("teemu.testaaja@localhost.fix");
    r.setFirstname("");
    r.setLastname("");
    r.setPassword("");
    r.setPassword2("");
    r.setPhonenumber("");
    r.setPic("");
    r.setPreferredContactMethod("phone");
    r.setUseraccount("");
    
    Errors errors = new BeanPropertyBindingResult(r, "r");
    validator.validate(r, errors);

    assertTrue(errors.hasErrors());
    assertNull( errors.getFieldError("email") );
    assertNotNull( errors.getFieldError("phonenumber") );
    assertNotNull( errors.getFieldError("password") );
    assertNotNull( errors.getFieldError("useraccount") );
    
  }
  
  @Test
  public void testUseraccountValidValue() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validateUserAccount("diipadaapa", errors);

    assertFalse(errors.hasErrors());
    assertNull( errors.getFieldError("useraccount") );
    
  }
  
  @Test
  public void testUseraccountValidValueWithAllowedSpecialChar() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validateUserAccount("diipa.daapa", errors);

    assertFalse(errors.hasErrors());
    assertNull( errors.getFieldError("useraccount") );
    
  }
  
  
  @Test
  public void testUseraccountInvalidValueTooShort() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validateUserAccount("a", errors);

    assertTrue(errors.hasErrors());
    assertNotNull( errors.getFieldError("useraccount") );
    
  }
  
  @Test
  public void testUseraccountInvalidValueNotAllowedSpecialChar() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validateUserAccount("diipadaapa(", errors);

    assertTrue(errors.hasErrors());
    assertNotNull( errors.getFieldError("useraccount") );
    
  }
  
  @Test
  public void testUseraccountInvalidValueNotAllowedSpecialCharacter2() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validateUserAccount("diipädääpä", errors);

    assertTrue(errors.hasErrors());
    assertNotNull( errors.getFieldError("useraccount") );
    
  }
  
  @Test
  public void testPasswordValidValue() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validatePassword("size8With@#$%", errors);

    assertFalse(errors.hasErrors());
    assertNull( errors.getFieldError("password") );
    
  }
  
  
  @Test
  public void testPasswordInvalidValueNotAllowedSpecialCharacter() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validatePassword("size8With@#$%ääää", errors);

    assertTrue(errors.hasErrors());
    assertNotNull( errors.getFieldError("password") );
    
  }
  
  @Test
  public void testPasswordInvalidValueNotAllowedSpecialCharacter2() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validatePassword("Diipödööpö1", errors);

    assertTrue(errors.hasErrors());
    assertNotNull( errors.getFieldError("password") );
    
  }
  
  @Test
  public void testPasswordInvalidValueNoUppercase() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validatePassword("size8with@#$%", errors);

    assertTrue(errors.hasErrors());
    assertNotNull( errors.getFieldError("password") );
    
  }
  
  @Test
  public void testPasswordInvalidValueNoLowercase() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validatePassword("SIZE8WITH@#$%", errors);

    assertTrue(errors.hasErrors());
    assertNotNull( errors.getFieldError("password") );
    
  }
  
  @Test
  public void testPasswordInvalidValueTooShort() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validatePassword("aA1@#$%", errors);

    assertTrue(errors.hasErrors());
    assertNotNull( errors.getFieldError("password") );
    
  }
  
  @Test
  public void testPasswordInvalidValueTooLong() {

    Errors errors = new BeanPropertyBindingResult(new Registration(), "r");
    
    //Test actual method
    CommonValidationUtils.validatePassword("size8With@#$%size8With@#$%", errors);

    assertTrue(errors.hasErrors());
    assertNotNull( errors.getFieldError("password") );
    
  }
  
}