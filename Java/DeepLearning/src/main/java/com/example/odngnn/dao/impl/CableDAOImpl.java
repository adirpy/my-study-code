package main.java.com.example.odngnn.dao.impl;

import main.java.com.example.odngnn.dao.CableDAO;
import main.java.com.example.odngnn.model.Cable;
import main.java.com.example.odngnn.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 光缆数据访问实现类
 */
public class CableDAOImpl implements CableDAO {
    
    private static final String TABLE_NAME = "edn_ai_cable";
    
    @Override
    public List<Cable> findAll() {
        String sql = "SELECT cable_id, name, full_name, a_device_id, z_device_id, diameter, length, capacity, z_avail_cores, z_avail_conn_a_cores FROM " + TABLE_NAME;
        List<Cable> cables = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cables.add(mapResultSetToCable(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all cables: " + e.getMessage());
        }
        
        return cables;
    }
    
    @Override
    public Cable findById(String cableId) {
        String sql = "SELECT cable_id, name, full_name, a_device_id, z_device_id, diameter, length, capacity, z_avail_cores, z_avail_conn_a_cores FROM " + TABLE_NAME + 
                    " WHERE cable_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cableId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCable(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding cable by ID: " + cableId + ", " + e.getMessage());
        }
        
        return null;
    }
    
    // 简化实现其他方法
    @Override
    public List<Cable> findByADevice(String aDeviceId) {
        return new ArrayList<>();
    }
    
    @Override
    public List<Cable> findByZDevice(String zDeviceId) {
        return new ArrayList<>();
    }
    
    @Override
    public List<Cable> findByDevice(String deviceId) {
        return new ArrayList<>();
    }
    
    @Override
    public List<Cable> findByCapacityRange(int minCapacity, int maxCapacity) {
        return new ArrayList<>();
    }
    
    @Override
    public List<Cable> findWithAvailableCores() {
        return new ArrayList<>();
    }
    
    @Override
    public boolean insert(Cable cable) {
        return false;
    }
    
    @Override
    public boolean update(Cable cable) {
        return false;
    }
    
    @Override
    public boolean delete(String cableId) {
        return false;
    }
    
    @Override
    public int count() {
        return 0;
    }
    
    /**
     * 将ResultSet映射为Cable对象
     */
    private Cable mapResultSetToCable(ResultSet rs) throws SQLException {
        Cable cable = new Cable();
        cable.setCableId(rs.getString("cable_id"));
        cable.setName(rs.getString("name"));
        cable.setFullName(rs.getString("full_name"));
        cable.setADeviceId(rs.getString("a_device_id"));
        cable.setZDeviceId(rs.getString("z_device_id"));
        cable.setDiameter(rs.getDouble("diameter"));
        cable.setLength(rs.getDouble("length"));
        cable.setCapacity(rs.getInt("capacity"));
        cable.setZAvailCores(rs.getInt("z_avail_cores"));
        cable.setZAvailConnACores(rs.getInt("z_avail_conn_a_cores"));
        return cable;
    }
}
