package main.java.com.example.odngnn.pathfinding;

import main.java.com.example.odngnn.data.ODNNetworkData;
import main.java.com.example.odngnn.model.*;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;

/**
 * 路径查找器，使用Dijkstra算法找到最低成本路径
 */
public class PathFinder {
    
    /**
     * 查找从指定人井到ODF的最低成本路径
     */
    public PathResult findLowestCostPath(ODNNetworkData networkData, String startFacilityId, 
                                       int requiredCores) {
        // 构建加权图
        Graph<String, DefaultWeightedEdge> graph = buildWeightedGraph(networkData);
        
        // 找到所有ODF设备
        List<Device> odfDevices = findODFDevices(networkData, requiredCores);
        
        if (odfDevices.isEmpty()) {
            return new PathResult(null, Double.MAX_VALUE, "没有找到满足容量要求的ODF设备");
        }
        
        // 检查起始人井是否有设备
        String startNode = findBestStartNode(networkData, startFacilityId, requiredCores, graph);
        
        // 对每个ODF设备计算最短路径
        PathResult bestResult = null;
        double minCost = Double.MAX_VALUE;
        
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = 
            new DijkstraShortestPath<>(graph);
        
        for (Device odfDevice : odfDevices) {
            try {
                GraphPath<String, DefaultWeightedEdge> path = 
                    dijkstra.getPath(startNode, odfDevice.getDeviceId());
                
                if (path != null) {
                    double totalCost = calculateTotalCost(path, networkData, requiredCores);
                    
                    if (totalCost < minCost) {
                        minCost = totalCost;
                        bestResult = new PathResult(path.getVertexList(), totalCost, 
                            "成功找到路径到ODF: " + odfDevice.getDeviceId());
                    }
                }
            } catch (Exception e) {
                // 忽略无法到达的路径
            }
        }
        
        if (bestResult == null) {
            return new PathResult(null, Double.MAX_VALUE, "无法找到到达ODF的路径");
        }
        
        return bestResult;
    }
    
    private Graph<String, DefaultWeightedEdge> buildWeightedGraph(ODNNetworkData networkData) {
        Graph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        
        // 添加节点
        for (Facility facility : networkData.getFacilities()) {
            graph.addVertex(facility.getFacilityId());
        }
        for (Device device : networkData.getDevices()) {
            graph.addVertex(device.getDeviceId());
        }
        
        // 添加管道边（双向）
        for (Duct duct : networkData.getDucts()) {
            DefaultWeightedEdge e1 = graph.addEdge(duct.getAFacilityId(), duct.getZFacilityId());
            if (e1 != null) {
                graph.setEdgeWeight(e1, calculateDuctCost(duct, networkData.getCosts()));
            }
            
            DefaultWeightedEdge e2 = graph.addEdge(duct.getZFacilityId(), duct.getAFacilityId());
            if (e2 != null) {
                graph.setEdgeWeight(e2, calculateDuctCost(duct, networkData.getCosts()));
            }
        }
        
        // 添加光缆边（有向）
        for (Cable cable : networkData.getCables()) {
            DefaultWeightedEdge e = graph.addEdge(cable.getADeviceId(), cable.getZDeviceId());
            if (e != null) {
                graph.setEdgeWeight(e, calculateCableCost(cable, networkData.getCosts()));
            }
        }
        
        // 添加设备到人井的连接（双向，成本很低）
        for (Device device : networkData.getDevices()) {
            DefaultWeightedEdge e1 = graph.addEdge(device.getFacilityId(), device.getDeviceId());
            if (e1 != null) {
                graph.setEdgeWeight(e1, 1.0); // 很小的成本
            }
            
            DefaultWeightedEdge e2 = graph.addEdge(device.getDeviceId(), device.getFacilityId());
            if (e2 != null) {
                graph.setEdgeWeight(e2, 1.0);
            }
        }
        
        return graph;
    }
    
    private List<Device> findODFDevices(ODNNetworkData networkData, int requiredCores) {
        List<Device> odfDevices = new ArrayList<>();
        for (Device device : networkData.getDevices()) {
            if ("ODF".equals(device.getResSpecId()) && 
                device.getAvailCapacity() >= requiredCores) {
                odfDevices.add(device);
            }
        }
        return odfDevices;
    }
    
    private String findBestStartNode(ODNNetworkData networkData, String startFacilityId, 
                                   int requiredCores, Graph<String, DefaultWeightedEdge> graph) {
        // 检查指定人井是否有足够容量的设备
        for (Device device : networkData.getDevices()) {
            if (device.getFacilityId().equals(startFacilityId) && 
                device.getAvailCapacity() >= requiredCores) {
                return device.getDeviceId(); // 直接使用该设备
            }
        }
        
        // 如果没有合适设备，找最近的有合适设备的人井
        String bestFacility = null;
        double minDistance = Double.MAX_VALUE;
        
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = 
            new DijkstraShortestPath<>(graph);
        
        for (Device device : networkData.getDevices()) {
            if (device.getAvailCapacity() >= requiredCores) {
                try {
                    GraphPath<String, DefaultWeightedEdge> path = 
                        dijkstra.getPath(startFacilityId, device.getFacilityId());
                    
                    if (path != null && path.getWeight() < minDistance) {
                        minDistance = path.getWeight();
                        bestFacility = device.getFacilityId();
                    }
                } catch (Exception e) {
                    // 忽略无法到达的设备
                }
            }
        }
        
        return bestFacility != null ? bestFacility : startFacilityId;
    }
    
    private double calculateDuctCost(Duct duct, List<CostConf> costs) {
        double baseCost = findCost("NEW_DUCT", String.valueOf((int)duct.getDiameter()), "m", costs);
        double openManholeAMCost = findCost("OPEN_MANHOLE", "default", "pcs", costs);
        double openManholeZCost = findCost("OPEN_MANHOLE", "default", "pcs", costs);
        
        return duct.getLength() * baseCost + openManholeAMCost + openManholeZCost;
    }
    
    private double calculateCableCost(Cable cable, List<CostConf> costs) {
        double baseCost = findCost("NEW_F_CABLE", String.valueOf(cable.getCapacity()), "m", costs);
        double spliceCost = findCost("SPLICE_PRE", "default", "pcs", costs);
        
        return cable.getLength() * baseCost + spliceCost;
    }
    
    private double calculateTotalCost(GraphPath<String, DefaultWeightedEdge> path, 
                                    ODNNetworkData networkData, int requiredCores) {
        double totalCost = path.getWeight();
        
        // 添加纤芯连接成本
        double spliceUnitCost = findCost("SPLICE", "default", "pcs", networkData.getCosts());
        totalCost += requiredCores * spliceUnitCost;
        
        return totalCost;
    }
    
    private double findCost(String category, String type, String unit, List<CostConf> costs) {
        for (CostConf cost : costs) {
            if (cost.getCategory() != null && cost.getCategory().equals(category) && 
                cost.getType() != null && cost.getType().equals(type) && 
                cost.getUnit() != null && cost.getUnit().equals(unit)) {
                return cost.getCost();
            }
        }
        
        // 如果找不到精确匹配，返回默认值
        if ("OPEN_MANHOLE".equals(category)) {
            return 100.0; // 默认开井盖成本
        } else if ("NEW_F_CABLE".equals(category)) {
            return 20.0; // 默认光缆成本
        } else if ("NEW_DUCT".equals(category)) {
            return 200.0; // 默认管道成本
        } else if ("SPLICE_PRE".equals(category)) {
            return 50.0; // 默认连接预处理成本
        } else if ("SPLICE".equals(category)) {
            return 10.0; // 默认每纤芯连接成本
        }
        
        return 0.0;
    }
    
    /**
     * 路径查找结果
     */
    public static class PathResult {
        private final List<String> path;
        private final double totalCost;
        private final String message;
        
        public PathResult(List<String> path, double totalCost, String message) {
            this.path = path;
            this.totalCost = totalCost;
            this.message = message;
        }
        
        public List<String> getPath() { return path; }
        public double getTotalCost() { return totalCost; }
        public String getMessage() { return message; }
        
        @Override
        public String toString() {
            if (path == null) {
                return "路径查找失败: " + message;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("找到路径: ");
            for (int i = 0; i < path.size(); i++) {
                if (i > 0) sb.append(" -> ");
                sb.append(path.get(i));
            }
            sb.append("\n总成本: ").append(String.format("%.2f", totalCost));
            sb.append("\n消息: ").append(message);
            
            return sb.toString();
        }
    }
}
