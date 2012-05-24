package fi.arcusys.koku.common.services.inforequest.model;

public enum KokuInformationRequestStatus {

	OPEN("Open"),
	VALID("Valid"),
	EXPIRED("Expired"),
	DECLINED("Declined");
	
	private final String value;
	
	KokuInformationRequestStatus(String v) {
	    value = v;
	}
	
	public String value() {
	    return value;
	}
	
	public static KokuInformationRequestStatus fromValue(String v) {
	    for (KokuInformationRequestStatus c: KokuInformationRequestStatus.values()) {
	        if (c.value.equals(v)) {
	            return c;
	        }
	    }
	    throw new IllegalArgumentException(v);
	}
}