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

/**
 * Class for presenting single message. Message is always sent to child guardians
 * 
 * @author tuomape
 */
public class Message {
  
  private String title;
  private String message;
  private String targetChild;
  
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public String getTargetChild() {
    return targetChild;
  }
  
  public void setTargetChild(String targetChild) {
    this.targetChild = targetChild;
  }
   
  

}
