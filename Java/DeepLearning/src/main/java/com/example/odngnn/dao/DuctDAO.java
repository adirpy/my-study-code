package main.java.com.example.odngnn.dao;

import main.java.com.example.odngnn.model.Duct;
import java.util.List;

/**
 * 管道数据访问接口
 */
public interface DuctDAO {
    
    /**
     * 查询所有管道
     */
    List<Duct> findAll();
    
    /**
     * 根据ID查询管道
     */
    Duct findById(String ductId);
    
    /**
     * 根据A端人井查询管道
     */
    List<Duct> findByAFacility(String aFacilityId);
    
    /**
     * 根据Z端人井查询管道
     */
    List<Duct> findByZFacility(String zFacilityId);
    
    /**
     * 根据人井查询所有相关管道（A端或Z端）
     */
    List<Duct> findByFacility(String facilityId);
    
    /**
     * 根据两个人井查询连接管道
     */
    List<Duct> findBetweenFacilities(String facilityId1, String facilityId2);
    
    /**
     * 根据直径范围查询管道
     */
    List<Duct> findByDiameterRange(double minDiameter, double maxDiameter);
    
    /**
     * 查询占用率低于指定值的管道
     */
    List<Duct> findByMaxUsageRate(double maxUsageRate);
    
    /**
     * 插入管道
     */
    boolean insert(Duct duct);
    
    /**
     * 更新管道
     */
    boolean update(Duct duct);
    
    /**
     * 删除管道
     */
    boolean delete(String ductId);
    
    /**
     * 获取管道总数
     */
    int count();
}
