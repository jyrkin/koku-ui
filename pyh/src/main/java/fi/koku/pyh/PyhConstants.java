package fi.koku.pyh;

import fi.koku.settings.KoKuPropertiesUtil;

/**
 * This class contains static constants used in PYH.
 * 
 * @author hurulmi
 *
 */
public class PyhConstants {

  public static final String CUSTOMER_SERVICE_ENDPOINT = KoKuPropertiesUtil.get("customer.service.endpointaddress");
  public static final String COMMUNITY_SERVICE_ENDPOINT = KoKuPropertiesUtil.get("community.service.endpointaddress");
  public static final String LOK_SERVICE_ENDPOINT = KoKuPropertiesUtil.get("lok.service.endpointaddress");
  
  public static final String KOKU_FROM_EMAIL_ADDRESS = KoKuPropertiesUtil.get("koku.from.email.address");
  public static final String KOKU_SUPPORT_EMAIL_ADDRESS = KoKuPropertiesUtil.get("koku.support.email.address");
  
  final public static String CUSTOMER_SERVICE_USER_ID = KoKuPropertiesUtil.get("pyh.customer.service.user.id");
  final public static String CUSTOMER_SERVICE_PASSWORD = KoKuPropertiesUtil.get("pyh.customer.service.password");
  
  final public static String COMMUNITY_SERVICE_USER_ID = KoKuPropertiesUtil.get("pyh.community.service.user.id");
  final public static String COMMUNITY_SERVICE_PASSWORD = KoKuPropertiesUtil.get("pyh.community.service.password");
  
  final public static String LOK_SERVICE_USER_ID = KoKuPropertiesUtil.get("pyh.lok.service.user.id");
  final public static String LOK_SERVICE_PASSWORD = KoKuPropertiesUtil.get("pyh.lok.service.password");
  
  final public static String COMPONENT_PYH = "PYH";
}
