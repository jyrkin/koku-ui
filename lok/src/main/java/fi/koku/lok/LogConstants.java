package fi.koku.lok;

import java.util.Locale;

/**
 * Log constants.
 * 
 * @author aspluma
 */
public final class LogConstants {
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final Locale LOCALE_FI = new Locale("fi");

  private LogConstants() {
    //Contains only static constants. No need for new instances
  }
  
}
