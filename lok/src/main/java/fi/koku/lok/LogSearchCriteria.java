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
package fi.koku.lok;

import java.util.Date;

/**
 * Data class for user Log search criteria.
 * 
 * @author aspluma
 */
public class LogSearchCriteria {
  private String pic;
  private String concept;
  private Date from;
  private Date to;
  private String picType;
  

  public LogSearchCriteria() {
  }
  
  public LogSearchCriteria(Date from, Date to){
    super();
    this.from = from;
    this.to = to;
  }
  
  public LogSearchCriteria(String pic, String concept, Date from, Date to, String picType) {
    super();
    this.pic = pic;
    this.concept = concept;
    this.from = from;
    this.to = to;
    this.picType = picType;
  }

  public String getPic() {
    return pic;
  }
  public void setPic(String pic) {
    this.pic = pic;
  }
  public String getConcept() {
    return concept;
  }
  public void setConcept(String concept) {
    this.concept = concept;
  }
  public Date getFrom() {
    return from;
  }
  public void setFrom(Date from) {
    this.from = from;
  }
  public Date getTo() {
    return to;
  }
  public void setTo(Date to) {
    this.to = to;
  }
  public String getPicType() {
	return picType;
 }
 public void setPicType(String picType) {
    this.picType = picType;
 }
}
