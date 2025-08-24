-- ODN网络数据库表结构
-- 创建数据库
CREATE DATABASE IF NOT EXISTS odn_network CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE odn_network;

-- 1. 人井表
CREATE TABLE IF NOT EXISTS edn_ai_facility (
    facility_id VARCHAR(50) PRIMARY KEY COMMENT '人井ID',
    name VARCHAR(100) NOT NULL COMMENT '人井名称',
    full_name VARCHAR(200) COMMENT '人井完整编码',
    mh_type VARCHAR(20) COMMENT '人井类型：KES/KS/PS/空',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_mh_type (mh_type),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人井表';

-- 2. 设备表
CREATE TABLE IF NOT EXISTS edn_ai_device (
    device_id VARCHAR(50) PRIMARY KEY COMMENT '设备ID',
    name VARCHAR(100) NOT NULL COMMENT '设备名称',
    full_name VARCHAR(200) COMMENT '设备完整编码',
    res_spec_id VARCHAR(20) NOT NULL COMMENT '设备类型：ODF/ODB/F_CLOSURE',
    facility_id VARCHAR(50) NOT NULL COMMENT '所在人井ID',
    avail_capacity INT DEFAULT 0 COMMENT '可用容量（纤芯数）',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_res_spec_id (res_spec_id),
    INDEX idx_facility_id (facility_id),
    INDEX idx_avail_capacity (avail_capacity),
    FOREIGN KEY (facility_id) REFERENCES edn_ai_facility(facility_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- 3. 光缆表
CREATE TABLE IF NOT EXISTS edn_ai_cable (
    cable_id VARCHAR(50) PRIMARY KEY COMMENT '光缆ID',
    name VARCHAR(100) NOT NULL COMMENT '光缆名称',
    full_name VARCHAR(200) COMMENT '光缆完整编码',
    a_device_id VARCHAR(50) NOT NULL COMMENT 'A端设备ID',
    z_device_id VARCHAR(50) NOT NULL COMMENT 'Z端设备ID',
    diameter DECIMAL(8,2) DEFAULT 0 COMMENT '光缆直径（毫米）',
    length DECIMAL(10,2) DEFAULT 0 COMMENT '光缆长度（米）',
    capacity INT DEFAULT 0 COMMENT '光缆芯数',
    z_avail_cores INT DEFAULT 0 COMMENT 'Z端可用纤芯数',
    z_avail_conn_a_cores INT DEFAULT 0 COMMENT 'Z端未连接的芯数',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_a_device (a_device_id),
    INDEX idx_z_device (z_device_id),
    INDEX idx_capacity (capacity),
    FOREIGN KEY (a_device_id) REFERENCES edn_ai_device(device_id) ON DELETE CASCADE,
    FOREIGN KEY (z_device_id) REFERENCES edn_ai_device(device_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='光缆表';

-- 4. 管道表
CREATE TABLE IF NOT EXISTS edn_ai_duct (
    duct_id VARCHAR(50) PRIMARY KEY COMMENT '管道ID',
    name VARCHAR(100) NOT NULL COMMENT '管道名称',
    full_name VARCHAR(200) COMMENT '管道完整编码',
    a_facility_id VARCHAR(50) NOT NULL COMMENT 'A端人井ID',
    z_facility_id VARCHAR(50) NOT NULL COMMENT 'Z端人井ID',
    diameter DECIMAL(8,2) DEFAULT 0 COMMENT '管道直径（毫米）',
    length DECIMAL(10,2) DEFAULT 0 COMMENT '管道长度（米）',
    usage_rate DECIMAL(5,4) DEFAULT 0 COMMENT '管道占用率（0-1）',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_a_facility (a_facility_id),
    INDEX idx_z_facility (z_facility_id),
    INDEX idx_usage_rate (usage_rate),
    FOREIGN KEY (a_facility_id) REFERENCES edn_ai_facility(facility_id) ON DELETE CASCADE,
    FOREIGN KEY (z_facility_id) REFERENCES edn_ai_facility(facility_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管道表';

-- 5. 光缆管道关系表
CREATE TABLE IF NOT EXISTS edn_ai_cable_duct_rela (
    cable_id VARCHAR(50) NOT NULL COMMENT '光缆ID',
    duct_id VARCHAR(50) NOT NULL COMMENT '管道ID',
    seq INT NOT NULL COMMENT '管道顺序',
    usage_rate DECIMAL(5,4) DEFAULT 0 COMMENT '在该管道中的占用率',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (cable_id, duct_id),
    INDEX idx_cable_seq (cable_id, seq),
    INDEX idx_duct_id (duct_id),
    FOREIGN KEY (cable_id) REFERENCES edn_ai_cable(cable_id) ON DELETE CASCADE,
    FOREIGN KEY (duct_id) REFERENCES edn_ai_duct(duct_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='光缆管道关系表';

-- 6. 连接表
CREATE TABLE IF NOT EXISTS edn_ai_link (
    res_spec_id VARCHAR(20) NOT NULL COMMENT '连接所在设备类型',
    res_id VARCHAR(50) NOT NULL COMMENT '连接所在设备ID',
    a_res_spec_id VARCHAR(20) NOT NULL COMMENT 'A端资源类型',
    a_res_id VARCHAR(50) NOT NULL COMMENT 'A端资源ID',
    a_no INT NOT NULL COMMENT 'A端端口/纤芯序号',
    z_res_spec_id VARCHAR(20) NOT NULL COMMENT 'Z端资源类型',
    z_res_id VARCHAR(50) NOT NULL COMMENT 'Z端资源ID',
    z_no INT NOT NULL COMMENT 'Z端端口/纤芯序号',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (res_id, a_res_id, z_res_id),
    INDEX idx_a_resource (a_res_spec_id, a_res_id),
    INDEX idx_z_resource (z_res_spec_id, z_res_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='连接表';

-- 7. 成本配置表
CREATE TABLE IF NOT EXISTS edns_ai_cost_conf (
    category VARCHAR(20) NOT NULL COMMENT '成本类别：NEW_DUCT/NEW_OSC/NEW_F_CABLE/OPEN_MANHOLE/SPLICE_PRE/SPLICE',
    type VARCHAR(20) NOT NULL COMMENT '类型细分',
    unit VARCHAR(10) NOT NULL COMMENT '计费单位：m/pcs',
    cost DECIMAL(10,2) NOT NULL COMMENT '单价',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (category, type, unit),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成本配置表';

-- 插入示例成本数据
INSERT INTO edns_ai_cost_conf (category, type, unit, cost) VALUES
-- 管道成本
('NEW_DUCT', '50', 'm', 150.00),
('NEW_DUCT', '100', 'm', 200.00),
('NEW_DUCT', '150', 'm', 250.00),
-- 光缆成本
('NEW_F_CABLE', '12', 'm', 8.00),
('NEW_F_CABLE', '24', 'm', 12.00),
('NEW_F_CABLE', '48', 'm', 20.00),
('NEW_F_CABLE', '96', 'm', 35.00),
('NEW_F_CABLE', '144', 'm', 50.00),
('NEW_F_CABLE', '288', 'm', 85.00),
-- 设备成本
('NEW_OSC', 'ODF', 'pcs', 8000.00),
('NEW_OSC', 'ODB', 'pcs', 3000.00),
('NEW_OSC', 'F_CLOSURE', 'pcs', 500.00),
-- 开井盖成本
('OPEN_MANHOLE', 'KES', 'pcs', 200.00),
('OPEN_MANHOLE', 'KS', 'pcs', 150.00),
('OPEN_MANHOLE', 'PS', 'pcs', 100.00),
('OPEN_MANHOLE', '', 'pcs', 80.00),
-- 连接成本
('SPLICE_PRE', 'default', 'pcs', 50.00),
('SPLICE', 'default', 'pcs', 10.00)
ON DUPLICATE KEY UPDATE cost = VALUES(cost);

-- 插入示例数据（可选）
-- 示例人井数据
INSERT INTO edn_ai_facility (facility_id, name, full_name, mh_type) VALUES
('FAC_0001', '人井1', 'MH_0001', 'KES'),
('FAC_0002', '人井2', 'MH_0002', 'KS'),
('FAC_0003', '人井3', 'MH_0003', 'PS'),
('FAC_0004', '人井4', 'MH_0004', '')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 示例设备数据
INSERT INTO edn_ai_device (device_id, name, full_name, res_spec_id, facility_id, avail_capacity) VALUES
('DEV_0001', 'ODF设备1', 'DEVICE_0001', 'ODF', 'FAC_0001', 144),
('DEV_0002', 'ODB设备1', 'DEVICE_0002', 'ODB', 'FAC_0002', 48),
('DEV_0003', 'F_CLOSURE设备1', 'DEVICE_0003', 'F_CLOSURE', 'FAC_0003', 24),
('DEV_0004', 'ODF设备2', 'DEVICE_0004', 'ODF', 'FAC_0004', 288)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 示例光缆数据
INSERT INTO edn_ai_cable (cable_id, name, full_name, a_device_id, z_device_id, diameter, length, capacity, z_avail_cores, z_avail_conn_a_cores) VALUES
('CAB_0001', '光缆1', 'CABLE_0001', 'DEV_0001', 'DEV_0002', 15.5, 1000.0, 48, 24, 12),
('CAB_0002', '光缆2', 'CABLE_0002', 'DEV_0002', 'DEV_0003', 12.0, 500.0, 24, 12, 6),
('CAB_0003', '光缆3', 'CABLE_0003', 'DEV_0003', 'DEV_0004', 18.0, 1500.0, 96, 48, 24)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 示例管道数据
INSERT INTO edn_ai_duct (duct_id, name, full_name, a_facility_id, z_facility_id, diameter, length, usage_rate) VALUES
('DUCT_0001', '管道1', 'DUCT_0001', 'FAC_0001', 'FAC_0002', 100.0, 800.0, 0.3),
('DUCT_0002', '管道2', 'DUCT_0002', 'FAC_0002', 'FAC_0003', 80.0, 600.0, 0.4),
('DUCT_0003', '管道3', 'DUCT_0003', 'FAC_0003', 'FAC_0004', 120.0, 1200.0, 0.2)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 查询验证
SELECT 'Facilities' as Table_Name, COUNT(*) as Count FROM edn_ai_facility
UNION ALL
SELECT 'Devices', COUNT(*) FROM edn_ai_device
UNION ALL
SELECT 'Cables', COUNT(*) FROM edn_ai_cable
UNION ALL
SELECT 'Ducts', COUNT(*) FROM edn_ai_duct
UNION ALL
SELECT 'Cost_Configs', COUNT(*) FROM edns_ai_cost_conf;
