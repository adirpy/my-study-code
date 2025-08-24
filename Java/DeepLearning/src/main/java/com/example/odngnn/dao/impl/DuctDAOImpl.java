package main.java.com.example.odngnn.dao.impl;

import main.java.com.example.odngnn.dao.DuctDAO;
import main.java.com.example.odngnn.model.Duct;
import main.java.com.example.odngnn.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DuctDAOImpl implements DuctDAO {
    private static final String TABLE_NAME = "edn_ai_duct";
    
    @Override
    public List<Duct> findAll() {
        String sql = "SELECT duct_id, name, full_name, a_facility_id, z_facility_id, diameter, length, usage_rate FROM " + TABLE_NAME;
        List<Duct> ducts = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ducts.add(mapResultSetToDuct(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all ducts: " + e.getMessage());
        }
        
        return ducts;
    }
    
    @Override
    public Duct findById(String ductId) {
        // 简化实现
        return null;
    }
    
    // 其他方法简化实现
    @Override public List<Duct> findByAFacility(String aFacilityId) { return new ArrayList<>(); }
    @Override public List<Duct> findByZFacility(String zFacilityId) { return new ArrayList<>(); }
    @Override public List<Duct> findByFacility(String facilityId) { return new ArrayList<>(); }
    @Override public List<Duct> findBetweenFacilities(String facilityId1, String facilityId2) { return new ArrayList<>(); }
    @Override public List<Duct> findByDiameterRange(double minDiameter, double maxDiameter) { return new ArrayList<>(); }
    @Override public List<Duct> findByMaxUsageRate(double maxUsageRate) { return new ArrayList<>(); }
    @Override public boolean insert(Duct duct) { return false; }
    @Override public boolean update(Duct duct) { return false; }
    @Override public boolean delete(String ductId) { return false; }
    @Override public int count() { return 0; }
    
    private Duct mapResultSetToDuct(ResultSet rs) throws SQLException {
        Duct duct = new Duct();
        duct.setDuctId(rs.getString("duct_id"));
        duct.setName(rs.getString("name"));
        duct.setFullName(rs.getString("full_name"));
        duct.setAFacilityId(rs.getString("a_facility_id"));
        duct.setZFacilityId(rs.getString("z_facility_id"));
        duct.setDiameter(rs.getDouble("diameter"));
        duct.setLength(rs.getDouble("length"));
        duct.setUsageRate(rs.getDouble("usage_rate"));
        return duct;
    }
}
