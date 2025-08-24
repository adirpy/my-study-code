package main.java.com.example.odngnn.dao.impl;

import main.java.com.example.odngnn.dao.CostConfDAO;
import main.java.com.example.odngnn.model.CostConf;
import main.java.com.example.odngnn.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CostConfDAOImpl implements CostConfDAO {
    private static final String TABLE_NAME = "edns_ai_cost_conf";
    
    @Override
    public List<CostConf> findAll() {
        String sql = "SELECT category, type, unit, cost FROM " + TABLE_NAME;
        List<CostConf> costs = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                costs.add(mapResultSetToCostConf(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all cost configs: " + e.getMessage());
        }
        
        return costs;
    }
    
    // 简化实现其他方法
    @Override public List<CostConf> findByCategory(String category) { return new ArrayList<>(); }
    @Override public CostConf findByCategoryAndType(String category, String type) { return null; }
    @Override public CostConf findByCategoryTypeAndUnit(String category, String type, String unit) { return null; }
    @Override public boolean insert(CostConf costConf) { return false; }
    @Override public boolean update(CostConf costConf) { return false; }
    @Override public boolean delete(String category, String type, String unit) { return false; }
    @Override public int count() { return 0; }
    
    private CostConf mapResultSetToCostConf(ResultSet rs) throws SQLException {
        CostConf cost = new CostConf();
        cost.setCategory(rs.getString("category"));
        cost.setType(rs.getString("type"));
        cost.setUnit(rs.getString("unit"));
        cost.setCost(rs.getDouble("cost"));
        return cost;
    }
}
