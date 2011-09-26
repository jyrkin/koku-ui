package fi.koku.lok;

import java.util.Date;

public class AdminLogEntry {
  
  private String message; // message
  private String logId; // dataItemId: id given by the logging system
  private Date timestamp; // timestamp
  private String user;  // userPic: pic of the user
  private String child; // customerPic: pic of the child
  private String dataItemType;  // dataItemType: kks.vasu, kks.4v, family info, ..
  private String operation;  // operation: create, read, write, ..
  private String clientSystemId; // clientSystemId: pyh, kks, kunpo, pegasos..
  private String customerPic; //##TODO## Check if this is needed

  public String getLogId() {
    return logId;
  }

  public void setLogId(String logId) {
    this.logId = logId;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  // format for timestamp: yyyy-mm-dd
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getChild() {
    return child;
  }

  public void setChild(String child) {
    this.child = child;
  }

  public String getDataItemType() {
    return dataItemType;
  }

  public void setDataItemType(String dataItemType) {
    this.dataItemType = dataItemType;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getClientSystemId() {
    return clientSystemId;
  }

  public void setClientSystemId(String clientSystemId) {
    this.clientSystemId = clientSystemId;
  }

  public void setMessage(String message) {
    this.message = message;
  }
  
  public String getCustomer(){
    return customerPic;
  }
  
  public void setCustomer(String customerPic) {
    this.customerPic = customerPic;
  }
  

  public AdminLogEntry(){  
  }
  
  public AdminLogEntry(String message) {
    this.message = message;
  }
  
  public String getMessage() {
    message = logId +" "+ timestamp +" "+ user +" "+ child +" "+ dataItemType +" "+ 
      operation +" "+ clientSystemId;
    
    return message;
  }

 
 
}