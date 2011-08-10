package com.ixonos.koku.lok;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * This class creates log entries for demo purposes
 * 
 * @author makinsu
 *
 */
public class LogDemoFactory {

  private String user = "";
  private String child = "";
  private String event_type = "";
  private String event_description = "";
  private String calling_system = "";

  public static int BASIC_LOG = 0; //Tapahtumaloki
  public static int MANIPULATION_LOG = 1; //Tapahtumalokin kasittelyloki
  
  Random generator = new Random();
  String[] event_types = {"luku", "kirjoitus", "poisto", "luonti"};
  String[] manipulation_log_event_types = {"arkistointi", "luku"};
  String[] event_descriptions = {"4v-suunnitelma", "erityisruokavalio", "vasu", "osoitetiedot", "perhetiedot"};
  String[] calling_systems = {"KKS", "PYH", "TIVA", "KV"};
 
  /*
   * Create a demo log entry 
   */
  LogEntry createLogEntry(int id, int logType){
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy hh.mm:ss");
    LogEntry entry = new LogEntry();
    
    user = "kayttaja"+id;
    child = "child"+id*2;
    int nr = generator.nextInt(4);
 
    if(MANIPULATION_LOG==logType){
      nr = generator.nextInt(manipulation_log_event_types.length);
      event_type = manipulation_log_event_types[nr];
    }else{
      event_type = event_types[nr];
    }
    nr = generator.nextInt(5);
    event_description = event_descriptions[nr];
    nr = generator.nextInt(4);
    calling_system = calling_systems[nr];
    
    entry.setLog_id(""+id);
          
    entry.setTimestamp(simpleDateFormat.format(new Date()));
    entry.setUser(user);
    entry.setChild(child);
    entry.setEvent_type(event_type);
    entry.setEvent_description(event_description);
    entry.setCalling_system(calling_system);
    
    return entry;
  }
}
