package main.java.com.example.odngnn.model;

public class Device {
    private String deviceId;
    private String name;
    private String fullName;
    private String resSpecId; // ODF, ODB, F_CLOSURE
    private String facilityId;
    private int availCapacity;

    // Getters and Setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getResSpecId() { return resSpecId; }
    public void setResSpecId(String resSpecId) { this.resSpecId = resSpecId; }
    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public int getAvailCapacity() { return availCapacity; }
    public void setAvailCapacity(int availCapacity) { this.availCapacity = availCapacity; }
}
