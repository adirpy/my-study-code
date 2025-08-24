package main.java.com.example.odngnn.model;

public class Cable {
    private String cableId;
    private String name;
    private String fullName;
    private String aDeviceId;
    private String zDeviceId;
    private double diameter;
    private double length;
    private int capacity;
    private int zAvailCores;
    private int zAvailConnACores;

    // Getters and Setters
    public String getCableId() { return cableId; }
    public void setCableId(String cableId) { this.cableId = cableId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getADeviceId() { return aDeviceId; }
    public void setADeviceId(String aDeviceId) { this.aDeviceId = aDeviceId; }
    public String getZDeviceId() { return zDeviceId; }
    public void setZDeviceId(String zDeviceId) { this.zDeviceId = zDeviceId; }
    public double getDiameter() { return diameter; }
    public void setDiameter(double diameter) { this.diameter = diameter; }
    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getZAvailCores() { return zAvailCores; }
    public void setZAvailCores(int zAvailCores) { this.zAvailCores = zAvailCores; }
    public int getZAvailConnACores() { return zAvailConnACores; }
    public void setZAvailConnACores(int zAvailConnACores) { this.zAvailConnACores = zAvailConnACores; }
}
