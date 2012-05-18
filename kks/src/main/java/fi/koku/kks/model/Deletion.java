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
