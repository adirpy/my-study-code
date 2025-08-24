package main.java.com.example.odngnn.dao;

import main.java.com.example.odngnn.model.Cable;
import java.util.List;

/**
 * 光缆数据访问接口
 */
public interface CableDAO {
    
    /**
     * 查询所有光缆
     */
    List<Cable> findAll();
    
    /**
     * 根据ID查询光缆
     */
    Cable findById(String cableId);
    
    /**
     * 根据A端设备查询光缆
     */
    List<Cable> findByADevice(String aDeviceId);
    
    /**
     * 根据Z端设备查询光缆
     */
    List<Cable> findByZDevice(String zDeviceId);
    
    /**
     * 根据设备查询所有相关光缆（A端或Z端）
     */
    List<Cable> findByDevice(String deviceId);
    
    /**
     * 根据容量范围查询光缆
     */
    List<Cable> findByCapacityRange(int minCapacity, int maxCapacity);
    
    /**
     * 查询有可用纤芯的光缆
     */
    List<Cable> findWithAvailableCores();
    
    /**
     * 插入光缆
     */
    boolean insert(Cable cable);
    
    /**
     * 更新光缆
     */
    boolean update(Cable cable);
    
    /**
     * 删除光缆
     */
    boolean delete(String cableId);
    
    /**
     * 获取光缆总数
     */
    int count();
}
