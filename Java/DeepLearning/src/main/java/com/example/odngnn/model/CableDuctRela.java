package main.java.com.example.odngnn.model;

public class CableDuctRela {
    private String cableId;
    private String ductId;
    private int seq;
    private double usageRate;

    // Getters and Setters
    public String getCableId() { return cableId; }
    public void setCableId(String cableId) { this.cableId = cableId; }
    public String getDuctId() { return ductId; }
    public void setDuctId(String ductId) { this.ductId = ductId; }
    public int getSeq() { return seq; }
    public void setSeq(int seq) { this.seq = seq; }
    public double getUsageRate() { return usageRate; }
    public void setUsageRate(double usageRate) { this.usageRate = usageRate; }
}
