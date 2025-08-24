package main.java.com.example.odngnn.gnn;

import main.java.com.example.odngnn.data.ODNNetworkData;
import main.java.com.example.odngnn.features.GraphFeatureExtractor;
import main.java.com.example.odngnn.pathfinding.PathFinder;
import main.java.com.example.odngnn.model.Device;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 改进的GNN模型，用于ODN网络路径成本预测
 */
public class GNNModel {
    private MultiLayerNetwork model;
    private GraphFeatureExtractor featureExtractor;
    private PathFinder pathFinder;
    private Random random = new Random(42);
    
    private static final int FEATURE_DIM = 10;
    private static final int HIDDEN_SIZE = 64;
    private static final int OUTPUT_SIZE = 1; // 预测路径成本
    
    public GNNModel() {
        this.featureExtractor = new GraphFeatureExtractor();
        this.pathFinder = new PathFinder();
    }

    /**
     * 构建GNN模型架构
     */
    public void buildModel() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Adam(0.001))
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(FEATURE_DIM * 2) // 起始节点和目标节点特征拼接
                        .nOut(HIDDEN_SIZE)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(HIDDEN_SIZE)
                        .nOut(HIDDEN_SIZE)
                        .activation(Activation.RELU)
                        .build())
                .layer(2, new DenseLayer.Builder()
                        .nIn(HIDDEN_SIZE)
                        .nOut(HIDDEN_SIZE / 2)
                        .activation(Activation.RELU)
                        .build())
                .layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .nIn(HIDDEN_SIZE / 2)
                        .nOut(OUTPUT_SIZE)
                        .activation(Activation.IDENTITY)
                        .build())
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(100));
    }

    /**
     * 预训练模型
     */
    public void pretrain(List<ODNNetworkData> trainingData, int epochs) {
        System.out.println("开始GNN模型预训练...");
        
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0.0;
            int batchCount = 0;
            
            for (ODNNetworkData networkData : trainingData) {
                // 提取图特征
                GraphFeatureExtractor.GraphData graphData = 
                    featureExtractor.extractGraphData(networkData);
                
                // 生成训练样本
                List<TrainingSample> samples = generateTrainingSamples(networkData, graphData);
                
                for (TrainingSample sample : samples) {
                    // 训练模型
                    DataSet dataSet = new DataSet(sample.getInput(), sample.getLabel());
                    model.fit(dataSet);
                    
                    totalLoss += model.score();
                    batchCount++;
                }
            }
            
            double avgLoss = totalLoss / batchCount;
            System.out.println("Epoch " + epoch + ", 平均损失: " + String.format("%.6f", avgLoss));
        }
        
        System.out.println("预训练完成!");
    }
    
    /**
     * 生成训练样本
     */
    private List<TrainingSample> generateTrainingSamples(ODNNetworkData networkData, 
                                                       GraphFeatureExtractor.GraphData graphData) {
        List<TrainingSample> samples = new ArrayList<>();
        Map<String, Integer> nodeMapping = graphData.getNodeMapping();
        INDArray nodeFeatures = graphData.getNodeFeatures();
        
        // 随机选择起始人井和目标ODF设备对
        List<String> facilities = new ArrayList<>();
        List<String> odfDevices = new ArrayList<>();
        
        for (String nodeId : nodeMapping.keySet()) {
            if (nodeId.startsWith("FAC_")) {
                facilities.add(nodeId);
            } else if (nodeId.startsWith("DEV_")) {
                // 检查是否为ODF设备
                for (Device device : networkData.getDevices()) {
                    if (device.getDeviceId().equals(nodeId) && 
                        "ODF".equals(device.getResSpecId())) {
                        odfDevices.add(nodeId);
                        break;
                    }
                }
            }
        }
        
        // 生成训练样本
        int numSamples = Math.min(100, facilities.size() * odfDevices.size());
        for (int i = 0; i < numSamples; i++) {
            String startFacility = facilities.get(random.nextInt(facilities.size()));
            String targetODF = odfDevices.get(random.nextInt(odfDevices.size()));
            int requiredCores = random.nextInt(24) + 4; // 4-28芯
            
            // 使用Dijkstra算法计算真实最低成本
            PathFinder.PathResult pathResult = 
                pathFinder.findLowestCostPath(networkData, startFacility, requiredCores);
            
            if (pathResult.getPath() != null) {
                // 提取节点特征
                Integer startIndex = nodeMapping.get(startFacility);
                Integer targetIndex = nodeMapping.get(targetODF);
                
                if (startIndex != null && targetIndex != null) {
                    INDArray startFeature = nodeFeatures.getRow(startIndex).reshape(1, FEATURE_DIM);
                    INDArray targetFeature = nodeFeatures.getRow(targetIndex).reshape(1, FEATURE_DIM);
                    
                    // 拼接起始和目标节点特征
                    INDArray input = Nd4j.hstack(startFeature, targetFeature);
                    
                    // 标签为归一化的路径成本
                    double normalizedCost = Math.log(pathResult.getTotalCost() + 1) / 10.0;
                    INDArray label = Nd4j.scalar(normalizedCost).reshape(1, 1);
                    
                    samples.add(new TrainingSample(input, label, pathResult.getTotalCost()));
                }
            }
        }
        
        System.out.println("为网络生成了 " + samples.size() + " 个训练样本");
        return samples;
    }

    /**
     * 预测路径成本
     */
    public double predictPathCost(GraphFeatureExtractor.GraphData graphData, 
                                String startNode, String targetNode) {
        Map<String, Integer> nodeMapping = graphData.getNodeMapping();
        INDArray nodeFeatures = graphData.getNodeFeatures();
        
        Integer startIndex = nodeMapping.get(startNode);
        Integer targetIndex = nodeMapping.get(targetNode);
        
        if (startIndex == null || targetIndex == null) {
            return Double.MAX_VALUE;
        }
        
        INDArray startFeature = nodeFeatures.getRow(startIndex).reshape(1, FEATURE_DIM);
        INDArray targetFeature = nodeFeatures.getRow(targetIndex).reshape(1, FEATURE_DIM);
        INDArray input = Nd4j.hstack(startFeature, targetFeature);
        
        INDArray output = model.output(input);
        double normalizedCost = output.getDouble(0);
        
        // 反归一化
        return Math.exp(normalizedCost * 10.0) - 1;
    }

    /**
     * 使用GNN预测最低成本路径
     */
    public String findLowestCostPath(ODNNetworkData networkData, String startFacility, 
                                   String odfDevice, int nCores) {
        GraphFeatureExtractor.GraphData graphData = 
            featureExtractor.extractGraphData(networkData);
        
        double predictedCost = predictPathCost(graphData, startFacility, odfDevice);
        
        // 也运行真实的路径查找进行比较
        PathFinder.PathResult actualResult = 
            pathFinder.findLowestCostPath(networkData, startFacility, nCores);
        
        StringBuilder result = new StringBuilder();
        result.append("GNN预测成本: ").append(String.format("%.2f", predictedCost)).append("\n");
        
        if (actualResult.getPath() != null) {
            result.append("实际路径: ").append(actualResult.toString()).append("\n");
            double error = Math.abs(predictedCost - actualResult.getTotalCost()) / actualResult.getTotalCost() * 100;
            result.append("预测误差: ").append(String.format("%.2f%%", error));
        } else {
            result.append("无法找到实际路径");
        }
        
        return result.toString();
    }
    
    /**
     * 保存模型
     */
    public void saveModel(String filePath) {
        try {
            model.save(new File(filePath));
            System.out.println("模型已保存到: " + filePath);
        } catch (IOException e) {
            System.err.println("保存模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 加载模型
     */
    public void loadModel(String filePath) {
        try {
            model = MultiLayerNetwork.load(new File(filePath), true);
            System.out.println("模型已从 " + filePath + " 加载");
        } catch (IOException e) {
            System.err.println("加载模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 训练样本类
     */
    private static class TrainingSample {
        private final INDArray input;
        private final INDArray label;
        private final double actualCost;
        
        public TrainingSample(INDArray input, INDArray label, double actualCost) {
            this.input = input;
            this.label = label;
            this.actualCost = actualCost;
        }
        
        public INDArray getInput() { return input; }
        public INDArray getLabel() { return label; }
        public double getActualCost() { return actualCost; }
    }
}
