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

/**
 * Class for holding info for group creations
 * 
 * @author tuomape
 *
 */
public class GroupCreatable {
  
  private String type;
  private String name;
  private String customers[];
 


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getCustomers() {
    return customers;
  }

  public void setCustomers(String[] customers) {
    this.customers = customers;
  }

  
}
