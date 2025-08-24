package main.java.com.example.odngnn.dao;

import main.java.com.example.odngnn.model.Device;
import java.util.List;

/**
 * 设备数据访问接口
 */
public interface DeviceDAO {
    
    /**
     * 查询所有设备
     */
    List<Device> findAll();
    
    /**
     * 根据ID查询设备
     */
    Device findById(String deviceId);
    
    /**
     * 根据设备类型查询设备
     */
    List<Device> findByType(String resSpecId);
    
    /**
     * 根据人井ID查询设备
     */
    List<Device> findByFacilityId(String facilityId);
    
    /**
     * 根据类型和最小容量查询设备
     */
    List<Device> findByTypeAndMinCapacity(String resSpecId, int minCapacity);
    
    /**
     * 查询所有ODF设备
     */
    List<Device> findAllODF();
    
    /**
     * 查询指定人井内容量大于等于指定值的设备
     */
    List<Device> findByFacilityAndMinCapacity(String facilityId, int minCapacity);
    
    /**
     * 插入设备
     */
    boolean insert(Device device);
    
    /**
     * 更新设备
     */
    boolean update(Device device);
    
    /**
     * 删除设备
     */
    boolean delete(String deviceId);
    
    /**
     * 获取设备总数
     */
    int count();
}
