package main.java.com.example.odngnn.dao.impl;

import main.java.com.example.odngnn.dao.LinkDAO;
import main.java.com.example.odngnn.model.Link;
import main.java.com.example.odngnn.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LinkDAOImpl implements LinkDAO {
    private static final String TABLE_NAME = "edn_ai_link";
    
    @Override
    public List<Link> findAll() {
        String sql = "SELECT res_spec_id, res_id, a_res_spec_id, a_res_id, a_no, z_res_spec_id, z_res_id, z_no FROM " + TABLE_NAME;
        List<Link> links = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                links.add(mapResultSetToLink(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all links: " + e.getMessage());
        }
        
        return links;
    }
    
    // 简化实现其他方法
    @Override public List<Link> findByResourceId(String resId) { return new ArrayList<>(); }
    @Override public List<Link> findByAResource(String aResSpecId, String aResId) { return new ArrayList<>(); }
    @Override public List<Link> findByZResource(String zResSpecId, String zResId) { return new ArrayList<>(); }
    @Override public List<Link> findByDevice(String deviceId) { return new ArrayList<>(); }
    @Override public List<Link> findByCable(String cableId) { return new ArrayList<>(); }
    @Override public boolean insert(Link link) { return false; }
    @Override public boolean update(Link link) { return false; }
    @Override public boolean delete(String resId, String aResId, String zResId) { return false; }
    @Override public boolean deleteByResource(String resId) { return false; }
    @Override public int count() { return 0; }
    
    private Link mapResultSetToLink(ResultSet rs) throws SQLException {
        Link link = new Link();
        link.setResSpecId(rs.getString("res_spec_id"));
        link.setResId(rs.getString("res_id"));
        link.setAResSpecId(rs.getString("a_res_spec_id"));
        link.setAResId(rs.getString("a_res_id"));
        link.setANo(rs.getInt("a_no"));
        link.setZResSpecId(rs.getString("z_res_spec_id"));
        link.setZResId(rs.getString("z_res_id"));
        link.setZNo(rs.getInt("z_no"));
        return link;
    }
}
