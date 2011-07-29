package fi.arcusys.koku.appointment;

public class Slot {

    private long appointmentId;
    private int slotNumber;
    private String date;
    private String startTime;
    private String endTime;
    private String location;
    
    /* getters */
    public long getAppointmentId() {
    	return appointmentId;
    }
    
    public long getSlotNumber() {
    	return slotNumber;
    }
    
    public String getDate() {
    	return date;
    }
    
    public String getStartTime() {
    	return startTime;
    }
    
    public String getEndTime() {
    	return endTime;
    }
    
    public String getLocation() {
    	return location;
    }
    
    /* setters */
    public void setAppointmentId(long appointmentId) {
    	this.appointmentId = appointmentId;
    }
    
    public void setSlotNumber(int slotNumber) {
    	this.slotNumber = slotNumber;
    }
    
    public void setAppointmentDate(String date) {
    	this.date = date;
    }
    
    public void setStartTime(String startTime) {
    	this.startTime = startTime;
    }
    
    public void setEndTime(String endTime) {
    	this.endTime = endTime;
    }
    
    public void setLocation(String location) {
    	this.location = location;
    }
}
