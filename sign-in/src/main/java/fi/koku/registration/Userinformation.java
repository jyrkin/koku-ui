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
 * Userinformation
 * 
 * @author mikkope
 *
 */
public class Userinformation {

  private String firstname;
  private String lastname;
  private String pic;
  private String useraccount;
  private String dayOfBirth;
  
  private String email;
  private String phonenumber;
  
  private String newpassword;
  private String newpassword2;
  private String currentpassword;
     
  public Userinformation() {
    
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

  public String getUseraccount() {
    return useraccount;
  }

  public void setUseraccount(String useraccount) {
    this.useraccount = useraccount;
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

  public String getNewpassword() {
    return newpassword;
  }

  public void setNewpassword(String newpassword) {
    this.newpassword = newpassword;
  }

  public String getNewpassword2() {
    return newpassword2;
  }

  public void setNewpassword2(String newpassword2) {
    this.newpassword2 = newpassword2;
  }

  public String getCurrentpassword() {
    return currentpassword;
  }

  public void setCurrentpassword(String currentpassword) {
    this.currentpassword = currentpassword;
  }

  public String getPhonenumber() {
    return phonenumber;
  }

  public void setPhonenumber(String phonenumber) {
    this.phonenumber = phonenumber;
  }
 
  
  
}