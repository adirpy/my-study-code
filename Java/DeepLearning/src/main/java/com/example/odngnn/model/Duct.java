package main.java.com.example.odngnn.model;

public class Duct {
    private String ductId;
    private String name;
    private String fullName;
    private String aFacilityId;
    private String zFacilityId;
    private double diameter;
    private double length;
    private double usageRate;

    // Getters and Setters
    public String getDuctId() { return ductId; }
    public void setDuctId(String ductId) { this.ductId = ductId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAFacilityId() { return aFacilityId; }
    public void setAFacilityId(String aFacilityId) { this.aFacilityId = aFacilityId; }
    public String getZFacilityId() { return zFacilityId; }
    public void setZFacilityId(String zFacilityId) { this.zFacilityId = zFacilityId; }
    public double getDiameter() { return diameter; }
    public void setDiameter(double diameter) { this.diameter = diameter; }
    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }
    public double getUsageRate() { return usageRate; }
    public void setUsageRate(double usageRate) { this.usageRate = usageRate; }
}
