package main.java.com.example.odngnn.dao.impl;

import main.java.com.example.odngnn.dao.FacilityDAO;
import main.java.com.example.odngnn.model.Facility;
import main.java.com.example.odngnn.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 人井数据访问实现类
 */
public class FacilityDAOImpl implements FacilityDAO {
    
    private static final String TABLE_NAME = "edn_ai_facility";
    
    @Override
    public List<Facility> findAll() {
        String sql = "SELECT facility_id, name, full_name, mh_type FROM " + TABLE_NAME;
        List<Facility> facilities = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                facilities.add(mapResultSetToFacility(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all facilities: " + e.getMessage());
            e.printStackTrace();
        }
        
        return facilities;
    }
    
    @Override
    public Facility findById(String facilityId) {
        String sql = "SELECT facility_id, name, full_name, mh_type FROM " + TABLE_NAME + 
                    " WHERE facility_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, facilityId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFacility(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding facility by ID: " + facilityId + ", " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Facility> findByType(String mhType) {
        String sql = "SELECT facility_id, name, full_name, mh_type FROM " + TABLE_NAME + 
                    " WHERE mh_type = ?";
        List<Facility> facilities = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, mhType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    facilities.add(mapResultSetToFacility(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding facilities by type: " + mhType + ", " + e.getMessage());
            e.printStackTrace();
        }
        
        return facilities;
    }
    
    @Override
    public boolean insert(Facility facility) {
        String sql = "INSERT INTO " + TABLE_NAME + 
                    " (facility_id, name, full_name, mh_type) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, facility.getFacilityId());
            stmt.setString(2, facility.getName());
            stmt.setString(3, facility.getFullName());
            stmt.setString(4, facility.getMhType());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting facility: " + facility.getFacilityId() + ", " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean update(Facility facility) {
        String sql = "UPDATE " + TABLE_NAME + 
                    " SET name = ?, full_name = ?, mh_type = ? WHERE facility_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, facility.getName());
            stmt.setString(2, facility.getFullName());
            stmt.setString(3, facility.getMhType());
            stmt.setString(4, facility.getFacilityId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating facility: " + facility.getFacilityId() + ", " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean delete(String facilityId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE facility_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, facilityId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting facility: " + facilityId + ", " + e.getMessage());
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
            System.err.println("Error counting facilities: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * 将ResultSet映射为Facility对象
     */
    private Facility mapResultSetToFacility(ResultSet rs) throws SQLException {
        Facility facility = new Facility();
        facility.setFacilityId(rs.getString("facility_id"));
        facility.setName(rs.getString("name"));
        facility.setFullName(rs.getString("full_name"));
        facility.setMhType(rs.getString("mh_type"));
        return facility;
    }
}
