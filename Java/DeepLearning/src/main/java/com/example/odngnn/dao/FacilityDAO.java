package main.java.com.example.odngnn.dao;

import main.java.com.example.odngnn.model.Facility;
import java.util.List;

/**
 * 人井数据访问接口
 */
public interface FacilityDAO {
    
    /**
     * 查询所有人井
     */
    List<Facility> findAll();
    
    /**
     * 根据ID查询人井
     */
    Facility findById(String facilityId);
    
    /**
     * 根据类型查询人井
     */
    List<Facility> findByType(String mhType);
    
    /**
     * 插入人井
     */
    boolean insert(Facility facility);
    
    /**
     * 更新人井
     */
    boolean update(Facility facility);
    
    /**
     * 删除人井
     */
    boolean delete(String facilityId);
    
    /**
     * 获取人井总数
     */
    int count();
}
