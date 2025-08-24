package main.java.com.example.odngnn.dao;

import main.java.com.example.odngnn.model.Link;
import java.util.List;

/**
 * 连接数据访问接口
 */
public interface LinkDAO {
    
    /**
     * 查询所有连接
     */
    List<Link> findAll();
    
    /**
     * 根据资源ID查询连接
     */
    List<Link> findByResourceId(String resId);
    
    /**
     * 根据A端资源查询连接
     */
    List<Link> findByAResource(String aResSpecId, String aResId);
    
    /**
     * 根据Z端资源查询连接
     */
    List<Link> findByZResource(String zResSpecId, String zResId);
    
    /**
     * 根据设备查询所有连接
     */
    List<Link> findByDevice(String deviceId);
    
    /**
     * 根据光缆查询所有连接
     */
    List<Link> findByCable(String cableId);
    
    /**
     * 插入连接
     */
    boolean insert(Link link);
    
    /**
     * 更新连接
     */
    boolean update(Link link);
    
    /**
     * 删除连接
     */
    boolean delete(String resId, String aResId, String zResId);
    
    /**
     * 根据资源删除所有相关连接
     */
    boolean deleteByResource(String resId);
    
    /**
     * 获取连接总数
     */
    int count();
}
