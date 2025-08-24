package main.java.com.example.odngnn.graph;

import main.java.com.example.odngnn.model.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class ODNGraph {
    private Graph<String, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

    public void buildGraph(List<Facility> facilities, List<Duct> ducts, List<Device> devices, List<Cable> cables, List<CostConf> costs) {
        // 添加节点：人井和设备
        for (Facility f : facilities) {
            graph.addVertex(f.getFacilityId());
        }
        for (Device d : devices) {
            graph.addVertex(d.getDeviceId());
        }

        // 添加边：管道和光缆，计算权重基于成本
        for (Duct duct : ducts) {
            // 管道无方向，但我们添加双向边
            DefaultWeightedEdge e1 = graph.addEdge(duct.getAFacilityId(), duct.getZFacilityId());
            graph.setEdgeWeight(e1, calculateDuctCost(duct, costs));
            DefaultWeightedEdge e2 = graph.addEdge(duct.getZFacilityId(), duct.getAFacilityId());
            graph.setEdgeWeight(e2, calculateDuctCost(duct, costs));
        }

        for (Cable cable : cables) {
            // 光缆有方向，从A到Z
            DefaultWeightedEdge e = graph.addEdge(cable.getADeviceId(), cable.getZDeviceId());
            graph.setEdgeWeight(e, calculateCableCost(cable, costs));
        }

        // TODO: 添加更多连接，如设备到人井
    }

    private double calculateDuctCost(Duct duct, List<CostConf> costs) {
        // TODO: 根据成本表计算
        return duct.getLength() * findCost("NEW_DUCT", duct.getDiameter() + "", "m", costs);
    }

    private double calculateCableCost(Cable cable, List<CostConf> costs) {
        // TODO: 根据成本表计算
        return cable.getLength() * findCost("NEW_F_CABLE", cable.getCapacity() + "", "m", costs);
    }

    private double findCost(String category, String type, String unit, List<CostConf> costs) {
        for (CostConf c : costs) {
            if (c.getCategory().equals(category) && c.getType().equals(type) && c.getUnit().equals(unit)) {
                return c.getCost();
            }
        }
        return 0.0;
    }

    public Graph<String, DefaultWeightedEdge> getGraph() {
        return graph;
    }
}
