package main.java.com.example.odngnn.dao.impl;

import main.java.com.example.odngnn.dao.DeviceDAO;
import main.java.com.example.odngnn.model.Device;
import main.java.com.example.odngnn.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备数据访问实现类
 */
public class DeviceDAOImpl implements DeviceDAO {
    
    private static final String TABLE_NAME = "edn_ai_device";
    
    @Override
    public List<Device> findAll() {
        String sql = "SELECT device_id, name, full_name, res_spec_id, facility_id, avail_capacity FROM " + TABLE_NAME;
        List<Device> devices = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                devices.add(mapResultSetToDevice(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all devices: " + e.getMessage());
            e.printStackTrace();
        }
        
        return devices;
    }
    
    @Override
    public Device findById(String deviceId) {
        String sql = "SELECT device_id, name, full_name, res_spec_id, facility_id, avail_capacity FROM " + TABLE_NAME + 
                    " WHERE device_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, deviceId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDevice(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding device by ID: " + deviceId + ", " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Device> findByType(String resSpecId) {
        String sql = "SELECT device_id, name, full_name, res_spec_id, facility_id, avail_capacity FROM " + TABLE_NAME + 
                    " WHERE res_spec_id = ?";
        List<Device> devices = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, resSpecId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    devices.add(mapResultSetToDevice(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding devices by type: " + resSpecId + ", " + e.getMessage());
            e.printStackTrace();
        }
        
        return devices;
    }
    
    @Override
    public List<Device> findByFacilityId(String facilityId) {
        String sql = "SELECT device_id, name, full_name, res_spec_id, facility_id, avail_capacity FROM " + TABLE_NAME + 
                    " WHERE facility_id = ?";
        List<Device> devices = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, facilityId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    devices.add(mapResultSetToDevice(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding devices by facility: " + facilityId + ", " + e.getMessage());
            e.printStackTrace();
        }
        
        return devices;
    }
    
    @Override
    public List<Device> findByTypeAndMinCapacity(String resSpecId, int minCapacity) {
        String sql = "SELECT device_id, name, full_name, res_spec_id, facility_id, avail_capacity FROM " + TABLE_NAME + 
                    " WHERE res_spec_id = ? AND avail_capacity >= ?";
        List<Device> devices = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, resSpecId);
            stmt.setInt(2, minCapacity);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    devices.add(mapResultSetToDevice(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding devices by type and capacity: " + resSpecId + ", " + minCapacity + ", " + e.getMessage());
            e.printStackTrace();
        }
        
        return devices;
    }
    
    @Override
    public List<Device> findAllODF() {
        return findByType("ODF");
    }
    
    @Override
    public List<Device> findByFacilityAndMinCapacity(String facilityId, int minCapacity) {
        String sql = "SELECT device_id, name, full_name, res_spec_id, facility_id, avail_capacity FROM " + TABLE_NAME + 
                    " WHERE facility_id = ? AND avail_capacity >= ?";
        List<Device> devices = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, facilityId);
            stmt.setInt(2, minCapacity);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    devices.add(mapResultSetToDevice(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding devices by facility and capacity: " + facilityId + ", " + minCapacity + ", " + e.getMessage());
            e.printStackTrace();
        }
        
        return devices;
    }
    
    @Override
    public boolean insert(Device device) {
        String sql = "INSERT INTO " + TABLE_NAME + 
                    " (device_id, name, full_name, res_spec_id, facility_id, avail_capacity) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, device.getDeviceId());
            stmt.setString(2, device.getName());
            stmt.setString(3, device.getFullName());
            stmt.setString(4, device.getResSpecId());
            stmt.setString(5, device.getFacilityId());
            stmt.setInt(6, device.getAvailCapacity());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting device: " + device.getDeviceId() + ", " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean update(Device device) {
        String sql = "UPDATE " + TABLE_NAME + 
                    " SET name = ?, full_name = ?, res_spec_id = ?, facility_id = ?, avail_capacity = ? WHERE device_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, device.getName());
            stmt.setString(2, device.getFullName());
            stmt.setString(3, device.getResSpecId());
            stmt.setString(4, device.getFacilityId());
            stmt.setInt(5, device.getAvailCapacity());
            stmt.setString(6, device.getDeviceId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating device: " + device.getDeviceId() + ", " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(String deviceId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE device_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, deviceId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting device: " + deviceId + ", " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting devices: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * 将ResultSet映射为Device对象
     */
    private Device mapResultSetToDevice(ResultSet rs) throws SQLException {
        Device device = new Device();
        device.setDeviceId(rs.getString("device_id"));
        device.setName(rs.getString("name"));
        device.setFullName(rs.getString("full_name"));
        device.setResSpecId(rs.getString("res_spec_id"));
        device.setFacilityId(rs.getString("facility_id"));
        device.setAvailCapacity(rs.getInt("avail_capacity"));
        return device;
    }
}
