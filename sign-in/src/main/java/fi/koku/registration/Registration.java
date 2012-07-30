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

/**
 * @author mikkope
 *
 */
public class Registration {

  private String firstname;
  private String lastname;
  private String pic;
  
  private String dayOfBirth;
  
  private String email;
  private String phonenumber;
  
  private String useraccount;
  private String password;
  private String password2;
  
  private String preferredContactMethod;
  private boolean acceptTermsOfUse;
  
  
  public Registration() {
    
  }
  
  public String getFirstname() {
    return firstname;
  }
  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }
  public String getLastname() {
    return lastname;
  }
  public void setLastname(String lastname) {
    this.lastname = lastname;
  }
  public String getPic() {
    return pic;
  }
  public void setPic(String pic) {
    this.pic = pic;
  }
  public String getDayOfBirth() {
    return dayOfBirth;
  }
  public void setDayOfBirth(String dayOfBirth) {
    this.dayOfBirth = dayOfBirth;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getPhonenumber() {
    return phonenumber;
  }
  public void setPhonenumber(String phonenumber) {
    this.phonenumber = phonenumber;
  }
  public String getPreferredContactMethod() {
    return preferredContactMethod;
  }
  public void setPreferredContactMethod(String preferredContactMethod) {
    this.preferredContactMethod = preferredContactMethod;
  }
  public boolean isAcceptTermsOfUse() {
    return acceptTermsOfUse;
  }
  public void setAcceptTermsOfUse(boolean acceptTermsOfUse) {
    this.acceptTermsOfUse = acceptTermsOfUse;
  }
  public String getUseraccount() {
    return useraccount;
  }
  public void setUseraccount(String useraccount) {
    this.useraccount = useraccount;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getPassword2() {
    return password2;
  }
  public void setPassword2(String password) {
    this.password2 = password;
  }
  
}