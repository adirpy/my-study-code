# ODN GNN 数据库配置指南

## 数据库环境要求

- MySQL 8.0+ 或 MariaDB 10.3+
- 支持UTF8MB4字符集
- 建议配置连接池参数

## 快速启动

### 1. 创建数据库和表结构

```bash
# 连接到MySQL
mysql -u root -p

# 执行建表脚本
source database_schema.sql
```

或者直接导入：
```bash
mysql -u root -p < database_schema.sql
```

### 2. 配置数据库连接

#### 方法一：修改配置文件
编辑 `src/main/java/com/example/odngnn/config/DatabaseConfig.java`，修改以下参数：

```java
public static final String JDBC_URL = "jdbc:mysql://your-host:3306/odn_network?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
public static final String USERNAME = "your-username";
public static final String PASSWORD = "your-password";
```

#### 方法二：使用环境变量（推荐）
```bash
export ODN_DB_URL="jdbc:mysql://localhost:3306/odn_network?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai"
export ODN_DB_USERNAME="root"
export ODN_DB_PASSWORD="your-password"
```

#### 方法三：使用JVM参数
```bash
java -Dodn.db.url="jdbc:mysql://localhost:3306/odn_network" \
     -Dodn.db.username="root" \
     -Dodn.db.password="your-password" \
     -jar your-app.jar
```

### 3. 验证连接

运行程序后，查看控制台输出：
```
=== 数据库连接测试 ===
✓ 数据库连接成功
连接池状态: Pool Status - Active: 1, Idle: 4, Total: 5, Waiting: 0
数据库表统计:
  - 人井表: 4 条记录
  - 设备表: 4 条记录
====================
```

## 数据表结构

### 核心表

1. **edn_ai_facility** - 人井表
   - facility_id: 人井ID（主键）
   - name: 人井名称
   - mh_type: 人井类型（KES/KS/PS/空）

2. **edn_ai_device** - 设备表
   - device_id: 设备ID（主键）
   - res_spec_id: 设备类型（ODF/ODB/F_CLOSURE）
   - facility_id: 所在人井ID
   - avail_capacity: 可用容量

3. **edn_ai_cable** - 光缆表
   - cable_id: 光缆ID（主键）
   - a_device_id: A端设备ID
   - z_device_id: Z端设备ID
   - capacity: 光缆芯数

4. **edn_ai_duct** - 管道表
   - duct_id: 管道ID（主键）
   - a_facility_id: A端人井ID
   - z_facility_id: Z端人井ID
   - length: 管道长度

5. **edns_ai_cost_conf** - 成本配置表
   - category: 成本类别
   - type: 类型细分
   - cost: 单价

### 关系表

6. **edn_ai_cable_duct_rela** - 光缆管道关系表
7. **edn_ai_link** - 连接表

## 数据导入

### 示例数据
数据库脚本已包含基础示例数据，包括：
- 4个人井
- 4个设备（2个ODF，1个ODB，1个F_CLOSURE）
- 3条光缆
- 3条管道
- 完整的成本配置

### 生产数据导入
如果您有生产环境的ODN数据，可以：

1. **CSV导入**：
```sql
LOAD DATA INFILE 'facilities.csv' 
INTO TABLE edn_ai_facility 
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"' 
LINES TERMINATED BY '\n';
```

2. **程序导入**：
使用项目中的DAO类进行数据导入：
```java
FacilityDAO facilityDAO = DAOFactory.getInstance().getFacilityDAO();
facilityDAO.insert(newFacility);
```

## 性能优化

### 索引优化
主要索引已创建：
- facility_id, device_id等主键索引
- res_spec_id, facility_id等外键索引
- 查询频繁的字段索引

### 连接池配置
默认连接池配置：
- 最大连接数：20
- 最小空闲连接：5
- 连接超时：30秒

可在 `DatabaseConfig.java` 中调整。

### 查询优化建议
1. 使用索引字段进行查询
2. 避免SELECT *，只查询需要的字段
3. 使用LIMIT限制结果集大小
4. 合理使用JOIN操作

## 故障排除

### 常见问题

1. **连接被拒绝**
   - 检查MySQL服务是否启动
   - 验证主机名和端口
   - 确认防火墙设置

2. **认证失败**
   - 验证用户名密码
   - 检查用户权限
   - 确认数据库名称正确

3. **字符编码问题**
   - 确保数据库字符集为utf8mb4
   - 检查连接URL中的字符编码参数

4. **连接池耗尽**
   - 检查连接是否正确关闭
   - 调整连接池参数
   - 监控连接池状态

### 日志调试
开启SQL日志：
```bash
java -Dmysql.sql.log=true your-app.jar
```

### 监控命令
```sql
-- 查看连接状态
SHOW PROCESSLIST;

-- 查看表大小
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    ROUND(((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024), 2) AS 'Size (MB)'
FROM 
    information_schema.TABLES 
WHERE 
    TABLE_SCHEMA = 'odn_network';
```

## 备份和恢复

### 备份
```bash
mysqldump -u root -p odn_network > odn_network_backup.sql
```

### 恢复
```bash
mysql -u root -p odn_network < odn_network_backup.sql
```

## 联系支持

如遇到数据库相关问题，请检查：
1. 连接参数配置
2. 数据库服务状态
3. 用户权限设置
4. 网络连接状况
