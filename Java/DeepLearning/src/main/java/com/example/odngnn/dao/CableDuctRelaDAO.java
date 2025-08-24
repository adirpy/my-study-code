package main.java.com.example.odngnn.dao;

import main.java.com.example.odngnn.model.CableDuctRela;
import java.util.List;

/**
 * 光缆管道关系数据访问接口
 */
public interface CableDuctRelaDAO {
    
    /**
     * 查询所有光缆管道关系
     */
    List<CableDuctRela> findAll();
    
    /**
     * 根据光缆ID查询关系
     */
    List<CableDuctRela> findByCableId(String cableId);
    
    /**
     * 根据管道ID查询关系
     */
    List<CableDuctRela> findByDuctId(String ductId);
    
    /**
     * 根据光缆ID和管道ID查询关系
     */
    CableDuctRela findByCableAndDuct(String cableId, String ductId);
    
    /**
     * 根据光缆ID查询按序号排序的管道关系
     */
    List<CableDuctRela> findByCableIdOrderBySeq(String cableId);
    
    /**
     * 插入光缆管道关系
     */
    boolean insert(CableDuctRela relation);
    
    /**
     * 更新光缆管道关系
     */
    boolean update(CableDuctRela relation);
    
    /**
     * 删除光缆管道关系
     */
    boolean delete(String cableId, String ductId);
    
    /**
     * 根据光缆ID删除所有关系
     */
    boolean deleteByCableId(String cableId);
    
    /**
     * 根据管道ID删除所有关系
     */
    boolean deleteByDuctId(String ductId);
    
    /**
     * 获取关系总数
     */
    int count();
}
