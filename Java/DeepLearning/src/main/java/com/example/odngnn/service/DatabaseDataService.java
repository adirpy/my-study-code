package main.java.com.example.odngnn.service;

import main.java.com.example.odngnn.dao.DAOFactory;
import main.java.com.example.odngnn.data.ODNNetworkData;
import main.java.com.example.odngnn.model.*;
import main.java.com.example.odngnn.util.DatabaseUtil;

import java.util.List;

/**
 * 数据库数据加载服务
 */
public class DatabaseDataService {
    
    private final DAOFactory daoFactory;
    
    public DatabaseDataService() {
        this.daoFactory = DAOFactory.getInstance();
    }
    
    /**
     * 从数据库加载完整的ODN网络数据
     */
    public ODNNetworkData loadNetworkDataFromDatabase() {
        System.out.println("开始从数据库加载ODN网络数据...");
        
        try {
            // 测试数据库连接
            if (!DatabaseUtil.testConnection()) {
                System.err.println("数据库连接失败，使用默认模拟数据");
                return null;
            }
            
            System.out.println("数据库连接成功");
            System.out.println(DatabaseUtil.getPoolStatus());
            
            // 加载各种数据
            List<Facility> facilities = loadFacilities();
            List<Device> devices = loadDevices();
            List<Cable> cables = loadCables();
            List<Duct> ducts = loadDucts();
            List<CostConf> costs = loadCostConfigs();
            List<CableDuctRela> cableDuctRelas = loadCableDuctRelations();
            List<Link> links = loadLinks();
            
            // 打印加载统计
            printLoadStatistics(facilities, devices, cables, ducts, costs, cableDuctRelas, links);
            
            return new ODNNetworkData(facilities, devices, cables, ducts, costs, cableDuctRelas, links);
            
        } catch (Exception e) {
            System.err.println("从数据库加载数据时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 加载人井数据
     */
    private List<Facility> loadFacilities() {
        System.out.print("加载人井数据...");
        List<Facility> facilities = daoFactory.getFacilityDAO().findAll();
        System.out.println(" 完成，共加载 " + facilities.size() + " 个人井");
        return facilities;
    }
    
    /**
     * 加载设备数据
     */
    private List<Device> loadDevices() {
        System.out.print("加载设备数据...");
        List<Device> devices = daoFactory.getDeviceDAO().findAll();
        System.out.println(" 完成，共加载 " + devices.size() + " 个设备");
        
        // 统计各类型设备数量
        long odfCount = devices.stream().filter(d -> "ODF".equals(d.getResSpecId())).count();
        long odbCount = devices.stream().filter(d -> "ODB".equals(d.getResSpecId())).count();
        long closureCount = devices.stream().filter(d -> "F_CLOSURE".equals(d.getResSpecId())).count();
        
        System.out.println("  - ODF设备: " + odfCount + " 个");
        System.out.println("  - ODB设备: " + odbCount + " 个");
        System.out.println("  - 接头盒: " + closureCount + " 个");
        
        return devices;
    }
    
    /**
     * 加载光缆数据
     */
    private List<Cable> loadCables() {
        System.out.print("加载光缆数据...");
        List<Cable> cables = daoFactory.getCableDAO().findAll();
        System.out.println(" 完成，共加载 " + cables.size() + " 条光缆");
        
        // 统计光缆容量
        int totalCapacity = cables.stream().mapToInt(Cable::getCapacity).sum();
        int totalAvailCores = cables.stream().mapToInt(Cable::getZAvailCores).sum();
        
        System.out.println("  - 总容量: " + totalCapacity + " 芯");
        System.out.println("  - 可用纤芯: " + totalAvailCores + " 芯");
        
        return cables;
    }
    
    /**
     * 加载管道数据
     */
    private List<Duct> loadDucts() {
        System.out.print("加载管道数据...");
        List<Duct> ducts = daoFactory.getDuctDAO().findAll();
        System.out.println(" 完成，共加载 " + ducts.size() + " 条管道");
        
        // 统计管道长度
        double totalLength = ducts.stream().mapToDouble(Duct::getLength).sum();
        System.out.println("  - 总长度: " + String.format("%.2f", totalLength) + " 米");
        
        return ducts;
    }
    
    /**
     * 加载成本配置数据
     */
    private List<CostConf> loadCostConfigs() {
        System.out.print("加载成本配置数据...");
        List<CostConf> costs = daoFactory.getCostConfDAO().findAll();
        System.out.println(" 完成，共加载 " + costs.size() + " 项成本配置");
        return costs;
    }
    
    /**
     * 加载光缆管道关系数据
     */
    private List<CableDuctRela> loadCableDuctRelations() {
        System.out.print("加载光缆管道关系数据...");
        List<CableDuctRela> relations = daoFactory.getCableDuctRelaDAO().findAll();
        System.out.println(" 完成，共加载 " + relations.size() + " 条关系");
        return relations;
    }
    
    /**
     * 加载连接数据
     */
    private List<Link> loadLinks() {
        System.out.print("加载连接数据...");
        List<Link> links = daoFactory.getLinkDAO().findAll();
        System.out.println(" 完成，共加载 " + links.size() + " 个连接");
        return links;
    }
    
    /**
     * 打印加载统计信息
     */
    private void printLoadStatistics(List<Facility> facilities, List<Device> devices, 
                                   List<Cable> cables, List<Duct> ducts, 
                                   List<CostConf> costs, List<CableDuctRela> cableDuctRelas, 
                                   List<Link> links) {
        System.out.println("\n=== 数据库数据加载统计 ===");
        System.out.println("人井数量: " + facilities.size());
        System.out.println("设备数量: " + devices.size());
        System.out.println("光缆数量: " + cables.size());
        System.out.println("管道数量: " + ducts.size());
        System.out.println("成本配置: " + costs.size());
        System.out.println("光缆管道关系: " + cableDuctRelas.size());
        System.out.println("连接数量: " + links.size());
        System.out.println("========================\n");
    }
    
    /**
     * 测试数据库连接并显示基本信息
     */
    public void testDatabaseConnection() {
        System.out.println("=== 数据库连接测试 ===");
        
        if (DatabaseUtil.testConnection()) {
            System.out.println("✓ 数据库连接成功");
            System.out.println("连接池状态: " + DatabaseUtil.getPoolStatus());
            
            // 测试各表数据量
            try {
                int facilityCount = daoFactory.getFacilityDAO().count();
                int deviceCount = daoFactory.getDeviceDAO().count();
                
                System.out.println("数据库表统计:");
                System.out.println("  - 人井表: " + facilityCount + " 条记录");
                System.out.println("  - 设备表: " + deviceCount + " 条记录");
                
            } catch (Exception e) {
                System.err.println("获取表统计信息失败: " + e.getMessage());
            }
            
        } else {
            System.err.println("✗ 数据库连接失败");
            System.err.println("请检查数据库配置和连接参数");
        }
        
        System.out.println("====================\n");
    }
    
    /**
     * 关闭数据库连接池
     */
    public void shutdown() {
        DatabaseUtil.shutdown();
    }
}
