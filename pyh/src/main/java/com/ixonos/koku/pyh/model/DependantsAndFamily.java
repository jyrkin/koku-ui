package com.ixonos.koku.pyh.model;

import java.util.List;

/**
 * Wrapper class which contains dependants and family.
 * This class is used as a helper object to minimize remote service calls.
 * 
 * @author hurulmi
 *
 */
public class DependantsAndFamily {
  
  private Family family;
  private List<Dependant> dependants;
  
  public DependantsAndFamily() {
  }
  
  public void setDependants(List<Dependant> dependants) {
    this.dependants = dependants;
  }
  
  public void setFamily(Family family) {
    this.family = family;
  }
  
  public List<Dependant> getDependants() {
    return dependants;
  }
  
  public Family getFamily() {
    return family;
  }
}
