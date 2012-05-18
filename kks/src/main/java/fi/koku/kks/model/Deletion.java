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

public class Deletion {

  private String comment;
  private boolean sendMessage;

  public Deletion() {
    comment = "";
    sendMessage = true;
  }
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public boolean isSendMessage() {
    return sendMessage;
  }

  public void setSendMessage(boolean sendMessage) {
    this.sendMessage = sendMessage;
  }   
  
  
  
}
