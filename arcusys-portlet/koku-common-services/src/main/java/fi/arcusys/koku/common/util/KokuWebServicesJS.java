package fi.arcusys.koku.common.util;

public enum KokuWebServicesJS {

	APPOINTMENT_PROCESSING_SERVICE	("KokuAppointmentProcessingService"),
	SUOSTUMUS_PROCESSING_SERVICE	("KokuSuostumusProcessingService"),
	VALTAKIRJA_PROCESSING_SERVICE	("KokuValtakirjaProcessingService"),
	TIETOPYYNTO_PROCESSING_SERVICE	("KokuTietopyyntoProcessingService"),
	HAK_PROCESSING_SERVICE			("KokuHakProcessingService"),
	REQUEST_PROCESSING_SERVICE		("KokuRequestProcessingService"),
	USERS_AND_GROUPS_SERVICE		("UsersAndGroupsService"),
	KV_MESSAGE_SERVICE				("KvMessageService");

	private final String value;

	KokuWebServicesJS(String v) {
	    value = v;
	}

	public String value() {
	    return value;
	}

	public static KokuWebServicesJS fromValue(String value) {
	    for (KokuWebServicesJS element: KokuWebServicesJS.values()) {
	        if (element.value.equalsIgnoreCase(value)) {
	            return element;
	        }
	    }
	    throw new IllegalArgumentException("Could not convert " + value + " to KokuWebServicesJS value");
	}
}
