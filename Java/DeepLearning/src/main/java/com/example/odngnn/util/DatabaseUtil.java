package main.java.com.example.odngnn.util;

import main.java.com.example.odngnn.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接工具类，使用HikariCP连接池
 */
public class DatabaseUtil {
    private static HikariDataSource dataSource;
    private static volatile boolean initialized = false;
    
    static {
        try {
            initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 初始化数据库连接池
     */
    private static synchronized void initialize() throws SQLException {
        if (initialized) {
            return;
        }
        
        try {
            // 加载MySQL驱动
            Class.forName(DatabaseConfig.DRIVER_CLASS);
            
            // 配置HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DatabaseConfig.getJdbcUrl());
            config.setUsername(DatabaseConfig.getUsername());
            config.setPassword(DatabaseConfig.getPassword());
            config.setDriverClassName(DatabaseConfig.DRIVER_CLASS);
            
            // 连接池设置
            config.setMaximumPoolSize(DatabaseConfig.MAXIMUM_POOL_SIZE);
            config.setMinimumIdle(DatabaseConfig.MINIMUM_IDLE);
            config.setConnectionTimeout(DatabaseConfig.CONNECTION_TIMEOUT);
            config.setIdleTimeout(DatabaseConfig.IDLE_TIMEOUT);
            config.setMaxLifetime(DatabaseConfig.MAX_LIFETIME);
            
            // 连接池名称
            config.setPoolName("ODN-GNN-Pool");
            
            // 连接测试
            config.setConnectionTestQuery("SELECT 1");
            
            // 额外配置
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            
            dataSource = new HikariDataSource(config);
            initialized = true;
            
            System.out.println("Database connection pool initialized successfully");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found", e);
        }
    }
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        return dataSource.getConnection();
    }
    
    /**
     * 关闭连接（归还给连接池）
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * 测试数据库连接
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取连接池状态信息
     */
    public static String getPoolStatus() {
        if (dataSource == null) {
            return "Connection pool not initialized";
        }
        
        return String.format(
            "Pool Status - Active: %d, Idle: %d, Total: %d, Waiting: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }
    
    /**
     * 关闭连接池
     */
    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            initialized = false;
            System.out.println("Database connection pool shutdown");
        }
    }
}
