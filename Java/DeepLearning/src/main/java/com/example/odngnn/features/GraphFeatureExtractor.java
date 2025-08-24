package main.java.com.example.odngnn.features;

import main.java.com.example.odngnn.data.ODNNetworkData;
import main.java.com.example.odngnn.model.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

/**
 * 图特征提取器，将ODN网络转换为GNN输入格式
 */
public class GraphFeatureExtractor {
    private static final int FEATURE_DIM = 10; // 节点特征维度
    
    /**
     * 提取图数据
     */
    public GraphData extractGraphData(ODNNetworkData networkData) {
        // 创建节点映射
        Map<String, Integer> nodeMapping = createNodeMapping(networkData);
        
        // 提取节点特征
        INDArray nodeFeatures = extractNodeFeatures(networkData, nodeMapping);
        
        // 提取邻接矩阵
        INDArray adjacencyMatrix = extractAdjacencyMatrix(networkData, nodeMapping);
        
        // 提取边权重
        INDArray edgeWeights = extractEdgeWeights(networkData, nodeMapping);
        
        return new GraphData(nodeFeatures, adjacencyMatrix, edgeWeights, nodeMapping);
    }
    
    private Map<String, Integer> createNodeMapping(ODNNetworkData networkData) {
        Map<String, Integer> mapping = new HashMap<>();
        int index = 0;
        
        // 添加人井节点
        for (Facility facility : networkData.getFacilities()) {
            mapping.put(facility.getFacilityId(), index++);
        }
        
        // 添加设备节点
        for (Device device : networkData.getDevices()) {
            mapping.put(device.getDeviceId(), index++);
        }
        
        return mapping;
    }
    
    private INDArray extractNodeFeatures(ODNNetworkData networkData, Map<String, Integer> nodeMapping) {
        int numNodes = nodeMapping.size();
        INDArray features = Nd4j.zeros(numNodes, FEATURE_DIM);
        
        // 人井特征
        for (Facility facility : networkData.getFacilities()) {
            int nodeIndex = nodeMapping.get(facility.getFacilityId());
            INDArray nodeFeature = Nd4j.zeros(1, FEATURE_DIM);
            
            // 特征0: 节点类型 (0=人井, 1=设备)
            nodeFeature.putScalar(0, 0.0);
            
            // 特征1-3: 人井类型编码
            nodeFeature.putScalar(1, encodeMhType(facility.getMhType()));
            
            // 特征4: 人井中设备数量
            long deviceCount = networkData.getDevices().stream()
                .filter(d -> d.getFacilityId() != null && d.getFacilityId().equals(facility.getFacilityId()))
                .count();
            nodeFeature.putScalar(4, deviceCount / 10.0); // 归一化
            
            features.putRow(nodeIndex, nodeFeature);
        }
        
        // 设备特征
        for (Device device : networkData.getDevices()) {
            int nodeIndex = nodeMapping.get(device.getDeviceId());
            INDArray nodeFeature = Nd4j.zeros(1, FEATURE_DIM);
            
            // 特征0: 节点类型
            nodeFeature.putScalar(0, 1.0);
            
            // 特征1-3: 设备类型编码
            nodeFeature.putScalar(1, encodeDeviceType(device.getResSpecId()));
            
            // 特征4: 可用容量（归一化）
            nodeFeature.putScalar(4, device.getAvailCapacity() / 288.0); // 假设最大288芯
            
            // 特征5: 连接的光缆数量
            long cableCount = networkData.getCables().stream()
                .filter(c -> c.getADeviceId().equals(device.getDeviceId()) || 
                           c.getZDeviceId().equals(device.getDeviceId()))
                .count();
            nodeFeature.putScalar(5, cableCount / 10.0); // 归一化
            
            features.putRow(nodeIndex, nodeFeature);
        }
        
        return features;
    }
    
    private double encodeMhType(String mhType) {
        switch (mhType == null ? "" : mhType) {
            case "KES": return 1.0;
            case "KS": return 0.7;
            case "PS": return 0.3;
            default: return 0.0;
        }
    }
    
    private double encodeDeviceType(String deviceType) {
        switch (deviceType) {
            case "ODF": return 1.0;
            case "ODB": return 0.6;
            case "F_CLOSURE": return 0.2;
            default: return 0.0;
        }
    }
    
    private INDArray extractAdjacencyMatrix(ODNNetworkData networkData, Map<String, Integer> nodeMapping) {
        int numNodes = nodeMapping.size();
        INDArray adjacency = Nd4j.zeros(numNodes, numNodes);
        
        // 添加管道连接（人井之间）
        for (Duct duct : networkData.getDucts()) {
            Integer aIndex = nodeMapping.get(duct.getAFacilityId());
            Integer zIndex = nodeMapping.get(duct.getZFacilityId());
            
            if (aIndex != null && zIndex != null) {
                adjacency.putScalar(aIndex, zIndex, 1.0);
                adjacency.putScalar(zIndex, aIndex, 1.0); // 无向图
            }
        }
        
        // 添加光缆连接（设备之间）
        for (Cable cable : networkData.getCables()) {
            Integer aIndex = nodeMapping.get(cable.getADeviceId());
            Integer zIndex = nodeMapping.get(cable.getZDeviceId());
            
            if (aIndex != null && zIndex != null) {
                adjacency.putScalar(aIndex, zIndex, 1.0);
                // 光缆有方向性，从A到Z
            }
        }
        
        // 添加设备到人井的连接
        for (Device device : networkData.getDevices()) {
            Integer deviceIndex = nodeMapping.get(device.getDeviceId());
            Integer facilityIndex = nodeMapping.get(device.getFacilityId());
            
            if (deviceIndex != null && facilityIndex != null) {
                adjacency.putScalar(facilityIndex, deviceIndex, 1.0);
                adjacency.putScalar(deviceIndex, facilityIndex, 1.0); // 双向连接
            }
        }
        
        return adjacency;
    }
    
    private INDArray extractEdgeWeights(ODNNetworkData networkData, Map<String, Integer> nodeMapping) {
        int numNodes = nodeMapping.size();
        INDArray weights = Nd4j.zeros(numNodes, numNodes);
        
        // 管道权重（基于长度）
        for (Duct duct : networkData.getDucts()) {
            Integer aIndex = nodeMapping.get(duct.getAFacilityId());
            Integer zIndex = nodeMapping.get(duct.getZFacilityId());
            
            if (aIndex != null && zIndex != null) {
                double weight = duct.getLength() / 1000.0; // 归一化到km
                weights.putScalar(aIndex, zIndex, weight);
                weights.putScalar(zIndex, aIndex, weight);
            }
        }
        
        // 光缆权重（基于长度和成本）
        for (Cable cable : networkData.getCables()) {
            Integer aIndex = nodeMapping.get(cable.getADeviceId());
            Integer zIndex = nodeMapping.get(cable.getZDeviceId());
            
            if (aIndex != null && zIndex != null) {
                double weight = cable.getLength() / 1000.0; // 归一化到km
                weights.putScalar(aIndex, zIndex, weight);
            }
        }
        
        // 设备到人井的权重设为很小的值
        for (Device device : networkData.getDevices()) {
            Integer deviceIndex = nodeMapping.get(device.getDeviceId());
            Integer facilityIndex = nodeMapping.get(device.getFacilityId());
            
            if (deviceIndex != null && facilityIndex != null) {
                weights.putScalar(facilityIndex, deviceIndex, 0.001);
                weights.putScalar(deviceIndex, facilityIndex, 0.001);
            }
        }
        
        return weights;
    }
    
    /**
     * 图数据容器
     */
    public static class GraphData {
        private final INDArray nodeFeatures;
        private final INDArray adjacencyMatrix;
        private final INDArray edgeWeights;
        private final Map<String, Integer> nodeMapping;
        
        public GraphData(INDArray nodeFeatures, INDArray adjacencyMatrix, 
                        INDArray edgeWeights, Map<String, Integer> nodeMapping) {
            this.nodeFeatures = nodeFeatures;
            this.adjacencyMatrix = adjacencyMatrix;
            this.edgeWeights = edgeWeights;
            this.nodeMapping = nodeMapping;
        }
        
        public INDArray getNodeFeatures() { return nodeFeatures; }
        public INDArray getAdjacencyMatrix() { return adjacencyMatrix; }
        public INDArray getEdgeWeights() { return edgeWeights; }
        public Map<String, Integer> getNodeMapping() { return nodeMapping; }
    }
}
