package main.java.com.example.odngnn.dao;

import main.java.com.example.odngnn.dao.impl.*;

/**
 * DAO工厂类，提供所有DAO实例
 */
public class DAOFactory {
    
    private static volatile DAOFactory instance;
    
    // DAO实例
    private final FacilityDAO facilityDAO;
    private final DeviceDAO deviceDAO;
    private final CableDAO cableDAO;
    private final DuctDAO ductDAO;
    private final CableDuctRelaDAO cableDuctRelaDAO;
    private final LinkDAO linkDAO;
    private final CostConfDAO costConfDAO;
    
    private DAOFactory() {
        // 初始化所有DAO实例
        this.facilityDAO = new FacilityDAOImpl();
        this.deviceDAO = new DeviceDAOImpl();
        this.cableDAO = new CableDAOImpl();
        this.ductDAO = new DuctDAOImpl();
        this.cableDuctRelaDAO = new CableDuctRelaDAOImpl();
        this.linkDAO = new LinkDAOImpl();
        this.costConfDAO = new CostConfDAOImpl();
    }
    
    /**
     * 获取DAO工厂单例
     */
    public static DAOFactory getInstance() {
        if (instance == null) {
            synchronized (DAOFactory.class) {
                if (instance == null) {
                    instance = new DAOFactory();
                }
            }
        }
        return instance;
    }
    
    public FacilityDAO getFacilityDAO() {
        return facilityDAO;
    }
    
    public DeviceDAO getDeviceDAO() {
        return deviceDAO;
    }
    
    public CableDAO getCableDAO() {
        return cableDAO;
    }
    
    public DuctDAO getDuctDAO() {
        return ductDAO;
    }
    
    public CableDuctRelaDAO getCableDuctRelaDAO() {
        return cableDuctRelaDAO;
    }
    
    public LinkDAO getLinkDAO() {
        return linkDAO;
    }
    
    public CostConfDAO getCostConfDAO() {
        return costConfDAO;
    }
}
