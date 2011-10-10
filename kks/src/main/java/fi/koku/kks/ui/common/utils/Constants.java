package fi.koku.kks.ui.common.utils;

import fi.koku.settings.KoKuPropertiesUtil;

public final class Constants {

  private static final String LINE_BREAK = "</br>";

  private Constants() {

  }

  final public static String ENDPOINT = KoKuPropertiesUtil.get("kks.service.endpointaddress");

  final public static String CUSTOMER_SERVICE_USER_ID = "marko";
  final public static String CUSTOMER_SERVICE_PASSWORD = "marko";

  final public static String COMMUNITY_SERVICE_USER_ID = "marko";
  final public static String COMMUNITY_SERVICE_PASSWORD = "marko";

  final public static String KKS_SERVICE_USER_ID = "marko";
  final public static String KKS_SERVICE_PASSWORD = "marko";

  final public static String COMMUNITY_TYPE_GUARDIAN_COMMUNITY = "guardian_community";

  final public static String ROLE_DEPENDANT = "dependant";
  final public static String ROLE_GUARDIAN = "guardian";

  final public static String COMPONENT_KKS = "KKS";

}
