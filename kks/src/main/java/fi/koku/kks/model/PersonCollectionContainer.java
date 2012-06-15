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
package fi.koku.kks.model;

import java.util.ArrayList;
import java.util.List;

public class PersonCollectionContainer {

  private String pic;
  private Person customer;
  
  private List<KKSCollection> collections;
      
  public PersonCollectionContainer() {
    collections = new ArrayList<KKSCollection>();
  }


  public String getPic() {
    return pic;
  }


  public void setPic(String pic) {
    this.pic = pic;
  }


  public Person getCustomer() {
    return customer;
  }


  public void setCustomer(Person customer) {
    this.customer = customer;
  }


  public List<KKSCollection> getCollections() {
    return collections;
  }


  public void setCollections(List<KKSCollection> collections) {
    this.collections = collections;
  }
  
  
}
