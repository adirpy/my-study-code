package main.java.com.example.odngnn.data;

import main.java.com.example.odngnn.model.*;
import java.util.List;

/**
 * ODN网络数据容器
 */
public class ODNNetworkData {
    private List<Facility> facilities;
    private List<Device> devices;
    private List<Cable> cables;
    private List<Duct> ducts;
    private List<CostConf> costs;
    private List<CableDuctRela> cableDuctRelas;
    private List<Link> links;
    
    public ODNNetworkData(List<Facility> facilities, List<Device> devices, 
                         List<Cable> cables, List<Duct> ducts, 
                         List<CostConf> costs, List<CableDuctRela> cableDuctRelas,
                         List<Link> links) {
        this.facilities = facilities;
        this.devices = devices;
        this.cables = cables;
        this.ducts = ducts;
        this.costs = costs;
        this.cableDuctRelas = cableDuctRelas;
        this.links = links;
    }
    
    // Getters
    public List<Facility> getFacilities() { return facilities; }
    public List<Device> getDevices() { return devices; }
    public List<Cable> getCables() { return cables; }
    public List<Duct> getDucts() { return ducts; }
    public List<CostConf> getCosts() { return costs; }
    public List<CableDuctRela> getCableDuctRelas() { return cableDuctRelas; }
    public List<Link> getLinks() { return links; }
}
