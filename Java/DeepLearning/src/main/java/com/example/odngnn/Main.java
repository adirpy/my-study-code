package main.java.com.example.odngnn;

import main.java.com.example.odngnn.data.ODNDataGenerator;
import main.java.com.example.odngnn.data.ODNNetworkData;
import main.java.com.example.odngnn.gnn.GNNModel;
import main.java.com.example.odngnn.pathfinding.PathFinder;
import main.java.com.example.odngnn.model.Device;
import main.java.com.example.odngnn.service.DatabaseDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import main.java.com.example.odngnn.model.*;

/**
 * ODN GNN系统主入口类
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== ODN网络GNN建模系统 ===");
        
        // 1. 尝试从数据库加载数据，如果失败则使用模拟数据
        System.out.println("\n1. 加载ODN网络数据...");
        List<ODNNetworkData> trainingData = loadTrainingData();
        
        // 2. 构建并训练GNN模型
        System.out.println("\n2. 构建并训练GNN模型...");
        GNNModel gnnModel = new GNNModel();
        gnnModel.buildModel();
        
        // 预训练模型
        int epochs = 50;
        gnnModel.pretrain(trainingData, epochs);
        
        // 3. 保存训练好的模型
        String modelPath = "odn_gnn_model.zip";
        gnnModel.saveModel(modelPath);
        
        // 4. 测试模型性能
        System.out.println("\n3. 测试模型性能...");
        testModel(gnnModel, trainingData.get(0));
        
        // 5. 演示查询功能
        System.out.println("\n4. 演示查询功能...");
        demonstrateQuery(gnnModel, trainingData.get(0));
        
        System.out.println("\n=== 系统运行完成 ===");
    }
    
    /**
     * 加载训练数据（优先从数据库，备用模拟数据）
     */
    private static List<ODNNetworkData> loadTrainingData() {
        List<ODNNetworkData> trainingData = new ArrayList<>();
        
        // 首先尝试从数据库加载
        DatabaseDataService dbService = new DatabaseDataService();
        
        try {
            // 测试数据库连接
            dbService.testDatabaseConnection();
            
            // 尝试加载数据库数据
            ODNNetworkData dbData = dbService.loadNetworkDataFromDatabase();
            
            if (dbData != null && !dbData.getFacilities().isEmpty()) {
                System.out.println("✓ 成功从数据库加载网络数据");
                
                // 如果数据量太大，进行抽样以避免内存问题
                if (dbData.getFacilities().size() > 1000) {
                    System.out.println("数据量较大，进行抽样处理...");
                    dbData = sampleNetworkData(dbData, 1000);
                    System.out.println("抽样后数据规模：" + dbData.getFacilities().size() + " 个人井");
                }
                
                trainingData.add(dbData);
                
                // 如果数据库数据较少，补充一些模拟数据用于训练
                if (dbData.getFacilities().size() < 50) {
                    System.out.println("数据库数据较少，补充模拟数据...");
                    trainingData.addAll(generateSimulatedData());
                }
            } else {
                System.out.println("⚠ 数据库无有效数据，使用模拟数据");
                trainingData.addAll(generateSimulatedData());
            }
            
        } catch (Exception e) {
            System.err.println("⚠ 数据库连接失败，使用模拟数据: " + e.getMessage());
            trainingData.addAll(generateSimulatedData());
        } finally {
            // 确保清理数据库连接
            try {
                dbService.shutdown();
            } catch (Exception e) {
                System.err.println("关闭数据库连接时出错: " + e.getMessage());
            }
        }
        
        return trainingData;
    }
    
    /**
     * 生成模拟训练数据
     */
    private static List<ODNNetworkData> generateSimulatedData() {
        List<ODNNetworkData> trainingData = new ArrayList<>();
        ODNDataGenerator generator = new ODNDataGenerator();
        
        // 生成多个不同规模的网络用于训练
        int[][] networkSizes = {
            {20, 15, 25}, // 小网络：20人井，15设备，25光缆
            {50, 40, 60}, // 中网络：50人井，40设备，60光缆
            {80, 60, 100} // 大网络：80人井，60设备，100光缆
        };
        
        for (int[] size : networkSizes) {
            for (int i = 0; i < 2; i++) { // 每种规模生成2个网络
                ODNNetworkData networkData = generator.generateNetworkData(
                    size[0], size[1], size[2]);
                trainingData.add(networkData);
                System.out.println("生成模拟网络 " + (trainingData.size()) + 
                    ": " + size[0] + "人井, " + size[1] + "设备, " + size[2] + "光缆");
            }
        }
        
        return trainingData;
    }
    
    /**
     * 测试模型性能
     */
    private static void testModel(GNNModel gnnModel, ODNNetworkData testData) {
        PathFinder pathFinder = new PathFinder();
        
        // 随机选择几个测试案例
        String[] testFacilities = {"FAC_0001", "FAC_0005", "FAC_0010"};
        int[] testCoreCounts = {4, 12, 24};
        
        System.out.println("测试案例：");
        for (String facility : testFacilities) {
            for (int cores : testCoreCounts) {
                System.out.println("\n--- 测试：从 " + facility + " 查找 " + cores + "芯光路 ---");
                
                // 使用传统方法
                PathFinder.PathResult traditionalResult = 
                    pathFinder.findLowestCostPath(testData, facility, cores);
                
                if (traditionalResult.getPath() != null) {
                    System.out.println("传统算法结果：");
                    System.out.println(traditionalResult.toString());
                    
                    // 使用GNN预测
                    System.out.println("\nGNN预测结果：");
                    String gnnResult = gnnModel.findLowestCostPath(testData, facility, "DEV_0001", cores);
                    System.out.println(gnnResult);
                } else {
                    System.out.println("传统算法无法找到路径");
                }
            }
        }
    }
    
    /**
     * 演示查询功能
     */
    private static void demonstrateQuery(GNNModel gnnModel, ODNNetworkData networkData) {
        System.out.println("=== 光路查询演示 ===");
        
        // 模拟用户查询
        String userFacility = "FAC_0003";
        int requiredCores = 8;
        
        System.out.println("查询需求：");
        System.out.println("- 起始人井：" + userFacility);
        System.out.println("- 所需纤芯数：" + requiredCores);
        
        // 查找最佳路径
        PathFinder pathFinder = new PathFinder();
        PathFinder.PathResult result = pathFinder.findLowestCostPath(
            networkData, userFacility, requiredCores);
        
        if (result.getPath() != null) {
            System.out.println("\n查询结果：");
            System.out.println(result.toString());
            
            // 显示路径详情
            System.out.println("\n路径详情：");
            List<String> path = result.getPath();
            for (int i = 0; i < path.size(); i++) {
                String nodeId = path.get(i);
                String nodeType = nodeId.startsWith("FAC_") ? "人井" : "设备";
                System.out.println((i + 1) + ". " + nodeType + ": " + nodeId);
                
                if (i < path.size() - 1) {
                    System.out.println("   ↓");
                }
            }
            
            // GNN对比预测
            System.out.println("\n=== GNN预测对比 ===");
            String targetODF = findFirstODF(networkData);
            if (targetODF != null) {
                String gnnPrediction = gnnModel.findLowestCostPath(
                    networkData, userFacility, targetODF, requiredCores);
                System.out.println(gnnPrediction);
            }
        } else {
            System.out.println("\n查询结果：" + result.getMessage());
        }
    }
    
    /**
     * 查找第一个ODF设备
     */
    private static String findFirstODF(ODNNetworkData networkData) {
        for (Device device : networkData.getDevices()) {
            if ("ODF".equals(device.getResSpecId())) {
                return device.getDeviceId();
            }
        }
        return null;
    }
    
    /**
     * 对大规模网络数据进行抽样，以避免内存问题
     */
    private static ODNNetworkData sampleNetworkData(ODNNetworkData originalData, int maxFacilities) {
        // 随机选择指定数量的人井
        List<Facility> sampledFacilities = originalData.getFacilities().stream()
            .limit(maxFacilities)
            .collect(Collectors.toList());
        
        Set<String> facilityIds = sampledFacilities.stream()
            .map(Facility::getFacilityId)
            .collect(Collectors.toSet());
        
        // 选择与抽样人井相关的设备
        List<Device> sampledDevices = originalData.getDevices().stream()
            .filter(d -> d.getFacilityId() != null && facilityIds.contains(d.getFacilityId()))
            .collect(Collectors.toList());
        
        Set<String> deviceIds = sampledDevices.stream()
            .map(Device::getDeviceId)
            .collect(Collectors.toSet());
        
        // 选择与抽样设备相关的光缆（双端设备都必须存在）
        List<Cable> sampledCables = originalData.getCables().stream()
            .filter(c -> deviceIds.contains(c.getADeviceId()) && deviceIds.contains(c.getZDeviceId()))
            .collect(Collectors.toList());
        
        // 选择与抽样人井相关的管道（双端人井都必须存在）
        List<Duct> sampledDucts = originalData.getDucts().stream()
            .filter(d -> facilityIds.contains(d.getAFacilityId()) && facilityIds.contains(d.getZFacilityId()))
            .collect(Collectors.toList());
        
        Set<String> cableIds = sampledCables.stream()
            .map(Cable::getCableId)
            .collect(Collectors.toSet());
        
        Set<String> ductIds = sampledDucts.stream()
            .map(Duct::getDuctId)
            .collect(Collectors.toSet());
        
        // 选择相关的连接和关系
        List<Link> sampledLinks = originalData.getLinks().stream()
            .filter(l -> deviceIds.contains(l.getResId()) && 
                        (cableIds.contains(l.getAResId()) || cableIds.contains(l.getZResId())))
            .collect(Collectors.toList());
        
        List<CableDuctRela> sampledRelas = originalData.getCableDuctRelas().stream()
            .filter(r -> cableIds.contains(r.getCableId()) && ductIds.contains(r.getDuctId()))
            .collect(Collectors.toList());
        
        return new ODNNetworkData(
            sampledFacilities,
            sampledDevices,
            sampledCables,
            sampledDucts,
            originalData.getCosts(), // 保留所有成本配置
            sampledRelas,
            sampledLinks
        );
    }
}
