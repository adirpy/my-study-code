package main.java.com.example.odngnn.dao.impl;

import main.java.com.example.odngnn.dao.CableDuctRelaDAO;
import main.java.com.example.odngnn.model.CableDuctRela;
import main.java.com.example.odngnn.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CableDuctRelaDAOImpl implements CableDuctRelaDAO {
    private static final String TABLE_NAME = "edn_ai_cable_duct_rela";
    
    @Override
    public List<CableDuctRela> findAll() {
        String sql = "SELECT cable_id, duct_id, seq, usage_rate FROM " + TABLE_NAME;
        List<CableDuctRela> relations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                relations.add(mapResultSetToCableDuctRela(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding all cable duct relations: " + e.getMessage());
        }
        
        return relations;
    }
    
    // 简化实现其他方法
    @Override public List<CableDuctRela> findByCableId(String cableId) { return new ArrayList<>(); }
    @Override public List<CableDuctRela> findByDuctId(String ductId) { return new ArrayList<>(); }
    @Override public CableDuctRela findByCableAndDuct(String cableId, String ductId) { return null; }
    @Override public List<CableDuctRela> findByCableIdOrderBySeq(String cableId) { return new ArrayList<>(); }
    @Override public boolean insert(CableDuctRela relation) { return false; }
    @Override public boolean update(CableDuctRela relation) { return false; }
    @Override public boolean delete(String cableId, String ductId) { return false; }
    @Override public boolean deleteByCableId(String cableId) { return false; }
    @Override public boolean deleteByDuctId(String ductId) { return false; }
    @Override public int count() { return 0; }
    
    private CableDuctRela mapResultSetToCableDuctRela(ResultSet rs) throws SQLException {
        CableDuctRela rela = new CableDuctRela();
        rela.setCableId(rs.getString("cable_id"));
        rela.setDuctId(rs.getString("duct_id"));
        rela.setSeq(rs.getInt("seq"));
        rela.setUsageRate(rs.getDouble("usage_rate"));
        return rela;
    }
}
