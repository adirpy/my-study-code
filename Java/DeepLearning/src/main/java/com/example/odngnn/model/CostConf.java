package main.java.com.example.odngnn.model;

public class CostConf {
    private String category; // NEW_DUCT, NEW_OSC, etc.
    private String type;
    private String unit; // m, pcs
    private double cost;

    // Getters and Setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
}
