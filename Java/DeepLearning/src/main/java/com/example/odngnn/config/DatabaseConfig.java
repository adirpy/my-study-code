package main.java.com.example.odngnn.config;

/**
 * 数据库连接配置类
 */
public class DatabaseConfig {
    // 数据库连接配置
    public static final String JDBC_URL = "jdbc:mysql://10.10.20.157:3307/oss_im?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    public static final String USERNAME = "im";
    public static final String PASSWORD = "1jian8Shu!";
    public static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    
    // 连接池配置
    public static final int MAXIMUM_POOL_SIZE = 20;
    public static final int MINIMUM_IDLE = 5;
    public static final long CONNECTION_TIMEOUT = 30000; // 30秒
    public static final long IDLE_TIMEOUT = 600000; // 10分钟
    public static final long MAX_LIFETIME = 1800000; // 30分钟
    
    /**
     * 从环境变量或系统属性获取数据库URL
     */
    public static String getJdbcUrl() {
        String url = System.getenv("ODN_DB_URL");
        if (url == null) {
            url = System.getProperty("odn.db.url", JDBC_URL);
        }
        return url;
    }
    
    /**
     * 从环境变量或系统属性获取数据库用户名
     */
    public static String getUsername() {
        String username = System.getenv("ODN_DB_USERNAME");
        if (username == null) {
            username = System.getProperty("odn.db.username", USERNAME);
        }
        return username;
    }
    
    /**
     * 从环境变量或系统属性获取数据库密码
     */
    public static String getPassword() {
        String password = System.getenv("ODN_DB_PASSWORD");
        if (password == null) {
            password = System.getProperty("odn.db.password", PASSWORD);
        }
        return password;
    }
}
