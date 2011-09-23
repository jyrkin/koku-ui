package fi.koku.demo;

import fi.koku.lok.KoKuWSFactory;

import fi.koku.services.utility.log.v1.LogEntriesType;
import fi.koku.services.utility.log.v1.LogEntryType;
import fi.koku.services.utility.log.v1.LogQueryCriteriaType;
import fi.koku.services.utility.log.v1.LogServicePortType;
import fi.koku.services.utility.log.v1.ServiceFault;


public class LogClient {
  
  public static void main(String ... args) {
    KoKuWSFactory f = new KoKuWSFactory();
    LogServicePortType s = f.getLogService();

    LogEntriesType r =  null;
    try {
      r = s.opQueryLog(new LogQueryCriteriaType(), null);
    } catch (ServiceFault e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    for(LogEntryType e : r.getLogEntry()) {
      System.out.println("e: "+e);
    }
  }

}
