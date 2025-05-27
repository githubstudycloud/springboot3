-- 创建数据库
CREATE DATABASE IF NOT EXISTS excel_framework CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE excel_framework;

-- 删除表（如果存在）
DROP TABLE IF EXISTS excel_field_config;
DROP TABLE IF EXISTS excel_template_config;
DROP TABLE IF EXISTS fault;
DROP TABLE IF EXISTS fault_classification;
DROP TABLE IF EXISTS model;
DROP TABLE IF EXISTS model_category;

-- 故障分类表
CREATE TABLE fault_classification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT '分类名称',
    scope_type TINYINT NOT NULL COMMENT '1: 全局, 2: 本地',
    function_tree_id BIGINT COMMENT '功能/域ID（如果是本地）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_function_tree_id (function_tree_id)
) COMMENT='故障分类表';

-- 故障表
CREATE TABLE fault (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL COMMENT '功能路径+故障名称的Hash码',
    name VARCHAR(128) NOT NULL COMMENT '故障名称',
    function_id BIGINT NOT NULL COMMENT '功能ID',
    classification_id BIGINT COMMENT '故障分类ID',
    system_element_id BIGINT COMMENT '系统元素ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code),
    INDEX idx_function_id (function_id),
    INDEX idx_classification_id (classification_id),
    FOREIGN KEY (classification_id) REFERENCES fault_classification(id) ON DELETE SET NULL
) COMMENT='故障表';

-- 模型分类表
CREATE TABLE model_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    parent_id BIGINT COMMENT '父分类ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (name),
    INDEX idx_parent_id (parent_id)
) COMMENT='模型分类表';

-- 模型表
CREATE TABLE model (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL COMMENT '模型编码',
    name VARCHAR(128) NOT NULL COMMENT '模型名称',
    category_id BIGINT COMMENT '模型分类ID',
    description TEXT COMMENT '模型描述',
    version VARCHAR(32) COMMENT '模型版本',
    status TINYINT DEFAULT 1 COMMENT '1: 启用, 0: 禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code),
    INDEX idx_category_id (category_id),
    FOREIGN KEY (category_id) REFERENCES model_category(id) ON DELETE SET NULL
) COMMENT='模型表';

-- Excel模板配置表
CREATE TABLE excel_template_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_key VARCHAR(64) NOT NULL COMMENT '模板标识',
    entity_class VARCHAR(255) NOT NULL COMMENT '实体类名',
    table_name VARCHAR(64) NOT NULL COMMENT '表名',
    sheet_name VARCHAR(64) NOT NULL COMMENT 'Sheet名称',
    primary_key_fields JSON COMMENT '主键字段配置',
    update_mode VARCHAR(32) DEFAULT 'INSERT_OR_UPDATE' COMMENT '更新模式',
    description TEXT COMMENT '模板描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_key (template_key)
) COMMENT='Excel模板配置表';

-- Excel字段配置表
CREATE TABLE excel_field_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_id BIGINT NOT NULL COMMENT '模板ID',
    field_name VARCHAR(64) NOT NULL COMMENT '字段名称',
    column_name VARCHAR(64) NOT NULL COMMENT '列名称',
    column_index INT NOT NULL COMMENT '列索引',
    data_type VARCHAR(32) NOT NULL COMMENT '数据类型',
    required BOOLEAN DEFAULT FALSE COMMENT '是否必填',
    visible BOOLEAN DEFAULT TRUE COMMENT '是否可见',
    width INT DEFAULT 15 COMMENT '列宽',
    date_format VARCHAR(32) COMMENT '日期格式',
    dropdown_config JSON COMMENT '下拉配置',
    validators JSON COMMENT '验证器配置',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_field (template_id, field_name),
    FOREIGN KEY (template_id) REFERENCES excel_template_config(id) ON DELETE CASCADE
) COMMENT='Excel字段配置表';

-- 插入测试数据

-- 故障分类测试数据
INSERT INTO fault_classification (name, scope_type, function_tree_id) VALUES
('电气故障', 1, NULL),
('机械故障', 1, NULL),
('软件故障', 1, NULL),
('传感器故障', 2, 1001),
('执行器故障', 2, 1002);

-- 故障测试数据
INSERT INTO fault (code, name, function_id, classification_id, system_element_id) VALUES
('FAULT001', '电机过热', 1001, 1, 2001),
('FAULT002', '传感器失效', 1002, 4, 2002),
('FAULT003', '控制系统异常', 1003, 3, 2003),
('FAULT004', '齿轮磨损', 1004, 2, 2004),
('FAULT005', '液压泄漏', 1005, 2, 2005);

-- 模型分类测试数据
INSERT INTO model_category (name, description, parent_id) VALUES
('控制模型', '控制系统相关模型', NULL),
('安全模型', '安全系统相关模型', NULL),
('诊断模型', '故障诊断相关模型', NULL),
('预测模型', '预测性维护模型', 3),
('分类模型', '故障分类模型', 3);

-- 模型测试数据
INSERT INTO model (code, name, category_id, description, version, status) VALUES
('MODEL001', 'PID控制器模型', 1, '比例积分微分控制器模型', '1.0.0', 1),
('MODEL002', '故障检测模型', 3, '基于机器学习的故障检测模型', '2.1.0', 1),
('MODEL003', '安全评估模型', 2, '系统安全性评估模型', '1.5.0', 1),
('MODEL004', '无分类模型', NULL, '未分类的测试模型', '1.0.0', 1),
('MODEL005', '预测维护模型', 4, '设备寿命预测模型', '3.0.0', 1);