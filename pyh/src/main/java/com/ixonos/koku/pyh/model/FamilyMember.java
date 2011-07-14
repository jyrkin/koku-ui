package com.ixonos.koku.pyh.model;

public class FamilyMember extends Person {
  
  private String role;
  
  public FamilyMember(String firstname, String middlename, String surname, String ssn, String birthdate, String role) {
    super(firstname, middlename, surname, ssn, birthdate);
    this.role = role;
  }
  
  public String getRole() {
    return role;
  }
  
  public void setRole(String role) {
    this.role = role;
  }
  
}
