package main.java.com.example.odngnn.dao;

import main.java.com.example.odngnn.model.CostConf;
import java.util.List;

/**
 * 成本配置数据访问接口
 */
public interface CostConfDAO {
    
    /**
     * 查询所有成本配置
     */
    List<CostConf> findAll();
    
    /**
     * 根据类别查询成本配置
     */
    List<CostConf> findByCategory(String category);
    
    /**
     * 根据类别和类型查询成本配置
     */
    CostConf findByCategoryAndType(String category, String type);
    
    /**
     * 根据类别、类型和单位查询成本配置
     */
    CostConf findByCategoryTypeAndUnit(String category, String type, String unit);
    
    /**
     * 插入成本配置
     */
    boolean insert(CostConf costConf);
    
    /**
     * 更新成本配置
     */
    boolean update(CostConf costConf);
    
    /**
     * 删除成本配置
     */
    boolean delete(String category, String type, String unit);
    
    /**
     * 获取成本配置总数
     */
    int count();
}
