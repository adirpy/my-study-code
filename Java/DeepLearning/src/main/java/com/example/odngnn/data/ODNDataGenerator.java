package main.java.com.example.odngnn.data;

import main.java.com.example.odngnn.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ODN网络数据生成器，用于生成训练数据
 */
public class ODNDataGenerator {
    private Random random = new Random(42);
    
    /**
     * 生成模拟的ODN网络数据
     */
    public ODNNetworkData generateNetworkData(int numFacilities, int numDevices, int numCables) {
        List<Facility> facilities = generateFacilities(numFacilities);
        List<Device> devices = generateDevices(numDevices, facilities);
        List<Cable> cables = generateCables(numCables, devices);
        List<Duct> ducts = generateDucts(facilities);
        List<CostConf> costs = generateCostConfig();
        List<CableDuctRela> cableDuctRelas = generateCableDuctRelations(cables, ducts);
        List<Link> links = generateLinks(devices, cables);
        
        return new ODNNetworkData(facilities, devices, cables, ducts, costs, cableDuctRelas, links);
    }
    
    private List<Facility> generateFacilities(int count) {
        List<Facility> facilities = new ArrayList<>();
        String[] types = {"KES", "KS", "PS", ""};
        
        for (int i = 0; i < count; i++) {
            Facility facility = new Facility();
            facility.setFacilityId("FAC_" + String.format("%04d", i));
            facility.setName("人井_" + i);
            facility.setFullName("MH_" + String.format("%04d", i));
            facility.setMhType(types[random.nextInt(types.length)]);
            facilities.add(facility);
        }
        return facilities;
    }
    
    private List<Device> generateDevices(int count, List<Facility> facilities) {
        List<Device> devices = new ArrayList<>();
        String[] deviceTypes = {"ODF", "ODB", "F_CLOSURE"};
        
        for (int i = 0; i < count; i++) {
            Device device = new Device();
            device.setDeviceId("DEV_" + String.format("%04d", i));
            device.setName("设备_" + i);
            device.setFullName("DEVICE_" + String.format("%04d", i));
            device.setResSpecId(deviceTypes[random.nextInt(deviceTypes.length)]);
            // 随机分配到人井
            device.setFacilityId(facilities.get(random.nextInt(facilities.size())).getFacilityId());
            device.setAvailCapacity(random.nextInt(48) + 12); // 12-60芯容量
            devices.add(device);
        }
        return devices;
    }
    
    private List<Cable> generateCables(int count, List<Device> devices) {
        List<Cable> cables = new ArrayList<>();
        int[] capacities = {12, 24, 48, 96, 144, 288};
        
        for (int i = 0; i < count; i++) {
            Cable cable = new Cable();
            cable.setCableId("CAB_" + String.format("%04d", i));
            cable.setName("光缆_" + i);
            cable.setFullName("CABLE_" + String.format("%04d", i));
            
            // 随机选择A端和Z端设备
            Device aDevice = devices.get(random.nextInt(devices.size()));
            Device zDevice = devices.get(random.nextInt(devices.size()));
            while (zDevice.equals(aDevice)) {
                zDevice = devices.get(random.nextInt(devices.size()));
            }
            
            cable.setADeviceId(aDevice.getDeviceId());
            cable.setZDeviceId(zDevice.getDeviceId());
            cable.setDiameter(random.nextDouble() * 20 + 10); // 10-30mm
            cable.setLength(random.nextDouble() * 1000 + 100); // 100-1100m
            
            int capacity = capacities[random.nextInt(capacities.length)];
            cable.setCapacity(capacity);
            cable.setZAvailCores(random.nextInt(capacity / 2) + capacity / 4); // 25%-75%可用
            cable.setZAvailConnACores(random.nextInt(cable.getZAvailCores()));
            
            cables.add(cable);
        }
        return cables;
    }
    
    private List<Duct> generateDucts(List<Facility> facilities) {
        List<Duct> ducts = new ArrayList<>();
        int ductCount = 0;
        
        // 为相邻的人井生成管道连接
        for (int i = 0; i < facilities.size() - 1; i++) {
            for (int j = i + 1; j < facilities.size() && j <= i + 3; j++) {
                if (random.nextDouble() < 0.7) { // 70%概率有连接
                    Duct duct = new Duct();
                    duct.setDuctId("DUCT_" + String.format("%04d", ductCount++));
                    duct.setName("管道_" + ductCount);
                    duct.setFullName("DUCT_" + String.format("%04d", ductCount));
                    duct.setAFacilityId(facilities.get(i).getFacilityId());
                    duct.setZFacilityId(facilities.get(j).getFacilityId());
                    duct.setDiameter(random.nextDouble() * 100 + 50); // 50-150mm
                    duct.setLength(random.nextDouble() * 500 + 50); // 50-550m
                    duct.setUsageRate(random.nextDouble() * 0.8); // 0-80%占用率
                    ducts.add(duct);
                }
            }
        }
        return ducts;
    }
    
    private List<CostConf> generateCostConfig() {
        List<CostConf> costs = new ArrayList<>();
        
        // 管道成本
        costs.add(createCostConf("NEW_DUCT", "50", "m", 150.0));
        costs.add(createCostConf("NEW_DUCT", "100", "m", 200.0));
        costs.add(createCostConf("NEW_DUCT", "150", "m", 250.0));
        
        // 光缆成本
        costs.add(createCostConf("NEW_F_CABLE", "12", "m", 8.0));
        costs.add(createCostConf("NEW_F_CABLE", "24", "m", 12.0));
        costs.add(createCostConf("NEW_F_CABLE", "48", "m", 20.0));
        costs.add(createCostConf("NEW_F_CABLE", "96", "m", 35.0));
        costs.add(createCostConf("NEW_F_CABLE", "144", "m", 50.0));
        costs.add(createCostConf("NEW_F_CABLE", "288", "m", 85.0));
        
        // 设备成本
        costs.add(createCostConf("NEW_OSC", "ODF", "pcs", 8000.0));
        costs.add(createCostConf("NEW_OSC", "ODB", "pcs", 3000.0));
        costs.add(createCostConf("NEW_OSC", "F_CLOSURE", "pcs", 500.0));
        
        // 开井盖成本
        costs.add(createCostConf("OPEN_MANHOLE", "KES", "pcs", 200.0));
        costs.add(createCostConf("OPEN_MANHOLE", "KS", "pcs", 150.0));
        costs.add(createCostConf("OPEN_MANHOLE", "PS", "pcs", 100.0));
        costs.add(createCostConf("OPEN_MANHOLE", "", "pcs", 80.0));
        
        // 连接成本
        costs.add(createCostConf("SPLICE_PRE", "default", "pcs", 50.0));
        costs.add(createCostConf("SPLICE", "default", "pcs", 10.0));
        
        return costs;
    }
    
    private CostConf createCostConf(String category, String type, String unit, double cost) {
        CostConf conf = new CostConf();
        conf.setCategory(category);
        conf.setType(type);
        conf.setUnit(unit);
        conf.setCost(cost);
        return conf;
    }
    
    private List<CableDuctRela> generateCableDuctRelations(List<Cable> cables, List<Duct> ducts) {
        List<CableDuctRela> relations = new ArrayList<>();
        
        for (Cable cable : cables) {
            // 随机选择1-3个管道穿越
            int numDucts = random.nextInt(3) + 1;
            for (int i = 0; i < numDucts && i < ducts.size(); i++) {
                CableDuctRela rela = new CableDuctRela();
                rela.setCableId(cable.getCableId());
                rela.setDuctId(ducts.get(random.nextInt(ducts.size())).getDuctId());
                rela.setSeq(i + 1);
                rela.setUsageRate(random.nextDouble() * 0.3); // 0-30%占用率
                relations.add(rela);
            }
        }
        return relations;
    }
    
    private List<Link> generateLinks(List<Device> devices, List<Cable> cables) {
        List<Link> links = new ArrayList<>();
        
        // 生成设备间的连接
        for (Cable cable : cables) {
            if (random.nextDouble() < 0.8) { // 80%的光缆有连接
                Link link = new Link();
                link.setResSpecId("F_CLOSURE"); // 假设在接头盒中连接
                link.setResId("CLOSURE_" + cable.getCableId());
                link.setAResSpecId("CABLE");
                link.setAResId(cable.getCableId());
                link.setANo(random.nextInt(cable.getCapacity()) + 1);
                link.setZResSpecId("DEVICE");
                link.setZResId(cable.getZDeviceId());
                link.setZNo(random.nextInt(48) + 1);
                links.add(link);
            }
        }
        return links;
    }
}
