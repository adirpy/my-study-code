package main.java.com.example.odngnn.model;

public class Facility {
    private String facilityId;
    private String name;
    private String fullName;
    private String mhType; // KES/KS/PS/空

    // Getters and Setters
    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getMhType() { return mhType; }
    public void setMhType(String mhType) { this.mhType = mhType; }
}
