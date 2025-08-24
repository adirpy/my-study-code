package main.java.com.example.odngnn.service;

import main.java.com.example.odngnn.data.ODNNetworkData;
import main.java.com.example.odngnn.gnn.GNNModel;
import main.java.com.example.odngnn.pathfinding.PathFinder;
import main.java.com.example.odngnn.model.Device;
import main.java.com.example.odngnn.model.Facility;

import java.util.ArrayList;
import java.util.List;

/**
 * ODN网络查询服务，提供光路查询的完整功能
 */
public class ODNQueryService {
    private GNNModel gnnModel;
    private PathFinder pathFinder;
    private ODNNetworkData networkData;
    
    public ODNQueryService(GNNModel gnnModel, ODNNetworkData networkData) {
        this.gnnModel = gnnModel;
        this.pathFinder = new PathFinder();
        this.networkData = networkData;
    }
    
    /**
     * 查询从指定人井到ODF的最低成本光路
     * 
     * @param startFacilityId 起始人井ID
     * @param requiredCores 所需纤芯数
     * @return 查询结果
     */
    public QueryResult findOptimalPath(String startFacilityId, int requiredCores) {
        // 1. 验证输入参数
        if (!isValidFacility(startFacilityId)) {
            return new QueryResult(false, "指定的人井ID不存在: " + startFacilityId, null, 0.0, null);
        }
        
        if (requiredCores <= 0 || requiredCores > 288) {
            return new QueryResult(false, "所需纤芯数不合理: " + requiredCores, null, 0.0, null);
        }
        
        // 2. 检查指定人井是否有足够容量的设备
        Device localDevice = findLocalDeviceWithCapacity(startFacilityId, requiredCores);
        String actualStartNode = startFacilityId;
        String routeDescription = "";
        
        if (localDevice == null) {
            // 3. 如果本地没有合适设备，找到最近的有合适设备的人井
            NearestDeviceResult nearestResult = findNearestDeviceWithCapacity(startFacilityId, requiredCores);
            if (nearestResult == null || nearestResult.getDevice() == null) {
                return new QueryResult(false, 
                    "在网络中找不到满足 " + requiredCores + " 芯容量要求的设备", 
                    null, 0.0, null);
            }
            
            actualStartNode = nearestResult.getDevice().getFacilityId();
            routeDescription = "需要先从 " + startFacilityId + " 到达 " + actualStartNode + 
                " (距离: " + String.format("%.2f", nearestResult.getDistance()) + ")，";
        }
        
        // 4. 使用路径查找算法找到最优路径
        PathFinder.PathResult pathResult = pathFinder.findLowestCostPath(
            networkData, actualStartNode, requiredCores);
        
        if (pathResult.getPath() == null) {
            return new QueryResult(false, "无法找到到达ODF的可行路径", null, 0.0, null);
        }
        
        // 5. 构建查询结果
        List<String> completePath = new ArrayList<>();
        if (!startFacilityId.equals(actualStartNode)) {
            completePath.add(startFacilityId);
        }
        completePath.addAll(pathResult.getPath());
        
        String fullDescription = routeDescription + pathResult.getMessage();
        
        // 6. 获取接入设备ID（路径中的最后一个设备节点）
        String accessDeviceId = findAccessDeviceInPath(pathResult.getPath());
        
        return new QueryResult(true, fullDescription, completePath, 
            pathResult.getTotalCost(), accessDeviceId);
    }
    
    /**
     * 使用GNN模型预测路径成本（用于快速评估）
     */
    public double predictPathCost(String startFacilityId, String targetDeviceId) {
        try {
            return Double.parseDouble(gnnModel.findLowestCostPath(
                networkData, startFacilityId, targetDeviceId, 12).split(":")[1].trim());
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }
    
    /**
     * 验证人井ID是否存在
     */
    private boolean isValidFacility(String facilityId) {
        return networkData.getFacilities().stream()
            .anyMatch(f -> f.getFacilityId().equals(facilityId));
    }
    
    /**
     * 在指定人井中查找有足够容量的设备
     */
    private Device findLocalDeviceWithCapacity(String facilityId, int requiredCores) {
        return networkData.getDevices().stream()
            .filter(d -> d.getFacilityId().equals(facilityId) && 
                        d.getAvailCapacity() >= requiredCores)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 查找最近的有足够容量设备的人井
     */
    private NearestDeviceResult findNearestDeviceWithCapacity(String startFacilityId, int requiredCores) {
        Device bestDevice = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Device device : networkData.getDevices()) {
            if (device.getAvailCapacity() >= requiredCores) {
                // 计算到该设备所在人井的距离（简化为路径查找）
                PathFinder.PathResult pathToDevice = pathFinder.findLowestCostPath(
                    networkData, startFacilityId, 1); // 使用1芯来计算距离
                
                if (pathToDevice.getPath() != null && 
                    pathToDevice.getPath().contains(device.getFacilityId())) {
                    if (pathToDevice.getTotalCost() < minDistance) {
                        minDistance = pathToDevice.getTotalCost();
                        bestDevice = device;
                    }
                }
            }
        }
        
        return bestDevice != null ? new NearestDeviceResult(bestDevice, minDistance) : null;
    }
    
    /**
     * 在路径中查找接入设备ID
     */
    private String findAccessDeviceInPath(List<String> path) {
        // 返回路径中最后一个ODF设备
        for (int i = path.size() - 1; i >= 0; i--) {
            String nodeId = path.get(i);
            if (nodeId.startsWith("DEV_")) {
                for (Device device : networkData.getDevices()) {
                    if (device.getDeviceId().equals(nodeId) && 
                        "ODF".equals(device.getResSpecId())) {
                        return nodeId;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 查询结果类
     */
    public static class QueryResult {
        private final boolean success;
        private final String message;
        private final List<String> route;
        private final double totalCost;
        private final String accessDeviceId;
        
        public QueryResult(boolean success, String message, List<String> route, 
                          double totalCost, String accessDeviceId) {
            this.success = success;
            this.message = message;
            this.route = route;
            this.totalCost = totalCost;
            this.accessDeviceId = accessDeviceId;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<String> getRoute() { return route; }
        public double getTotalCost() { return totalCost; }
        public String getAccessDeviceId() { return accessDeviceId; }
        
        @Override
        public String toString() {
            if (!success) {
                return "查询失败: " + message;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("查询成功!\n");
            sb.append("接入设备ID: ").append(accessDeviceId).append("\n");
            sb.append("总成本: ").append(String.format("%.2f", totalCost)).append("\n");
            sb.append("路由路径: ");
            
            if (route != null && !route.isEmpty()) {
                for (int i = 0; i < route.size(); i++) {
                    if (i > 0) sb.append(" -> ");
                    sb.append(route.get(i));
                }
            }
            
            sb.append("\n详细信息: ").append(message);
            return sb.toString();
        }
    }
    
    /**
     * 最近设备结果类
     */
    private static class NearestDeviceResult {
        private final Device device;
        private final double distance;
        
        public NearestDeviceResult(Device device, double distance) {
            this.device = device;
            this.distance = distance;
        }
        
        public Device getDevice() { return device; }
        public double getDistance() { return distance; }
    }
}
