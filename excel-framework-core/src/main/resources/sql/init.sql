-- Excel Framework Database Initialization Script

-- Create database
CREATE DATABASE IF NOT EXISTS excel_framework DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE excel_framework;

-- ===========================
-- Configuration Tables
-- ===========================

-- Excel模板配置表
CREATE TABLE excel_template_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_key VARCHAR(64) NOT NULL COMMENT '模板标识',
    template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
    entity_class VARCHAR(255) COMMENT '实体类名',
    table_name VARCHAR(64) NOT NULL COMMENT '表名',
    sheet_name VARCHAR(64) NOT NULL COMMENT 'Sheet名称',
    primary_key_fields VARCHAR(255) NOT NULL COMMENT '主键字段,逗号分隔',
    update_mode VARCHAR(32) DEFAULT 'INSERT_OR_UPDATE' COMMENT '更新模式',
    business_config TEXT COMMENT '业务扩展配置(JSON)',
    description VARCHAR(255) COMMENT '描述',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
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
    dropdown_config TEXT COMMENT '下拉配置(JSON)',
    validation_rules TEXT COMMENT '验证规则(JSON)',
    extra_config TEXT COMMENT '扩展配置(JSON)',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_field (template_id, field_name),
    FOREIGN KEY (template_id) REFERENCES excel_template_config(id) ON DELETE CASCADE
) COMMENT='Excel字段配置表';

-- ===========================
-- Business Tables
-- ===========================

-- 故障分类表
CREATE TABLE fault_classification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT 'Classification name',
    scope_type TINYINT NOT NULL COMMENT '1: Global, 2: Local',
    function_tree_id BIGINT COMMENT 'Function/Domain ID if local',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_function_tree_id (function_tree_id)
) COMMENT='故障分类表';

-- 故障表
CREATE TABLE fault (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL COMMENT 'Hash code of function path + fault name',
    name VARCHAR(128) NOT NULL COMMENT 'Fault name',
    function_id BIGINT NOT NULL COMMENT 'Function ID',
    classification_id BIGINT COMMENT 'Fault classification ID',
    system_element_id BIGINT COMMENT 'System element ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code),
    INDEX idx_function_id (function_id),
    INDEX idx_classification_id (classification_id)
) COMMENT='故障表';

-- 模型分类表
CREATE TABLE model_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL COMMENT 'Category name',
    description TEXT COMMENT 'Category description',
    parent_id BIGINT COMMENT 'Parent category ID',
    level_no TINYINT DEFAULT 1 COMMENT 'Category level',
    sort_order INT DEFAULT 0 COMMENT 'Sort order',
    enabled BOOLEAN DEFAULT TRUE COMMENT 'Is enabled',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent_id (parent_id),
    INDEX idx_enabled (enabled)
) COMMENT='模型分类表';

-- 模型表
CREATE TABLE model (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL COMMENT 'Model code',
    name VARCHAR(128) NOT NULL COMMENT 'Model name',
    category_id BIGINT COMMENT 'Model category ID (可为空)',
    description TEXT COMMENT 'Model description',
    version VARCHAR(32) COMMENT 'Model version',
    model_type VARCHAR(32) DEFAULT 'STANDARD' COMMENT 'Model type',
    status TINYINT DEFAULT 1 COMMENT '1: Active, 0: Inactive',
    priority TINYINT DEFAULT 5 COMMENT 'Priority level 1-10',
    tags JSON COMMENT 'Model tags',
    create_user VARCHAR(64) COMMENT 'Creator',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_model_type (model_type)
) COMMENT='模型表';

-- ===========================
-- Sample Data
-- ===========================

-- 故障分类测试数据
INSERT INTO fault_classification (name, scope_type, function_tree_id) VALUES
('电气故障', 1, NULL),
('机械故障', 1, NULL),
('软件故障', 1, NULL),
('传感器故障', 2, 1001),
('执行器故障', 2, 1001),
('控制器故障', 2, 1002),
('通讯故障', 1, NULL),
('安全故障', 1, NULL);

-- 故障测试数据
INSERT INTO fault (code, name, function_id, classification_id, system_element_id) VALUES
('FAULT_001', '电机过热故障', 1001, 1, 2001),
('FAULT_002', '传感器失效', 1002, 4, 2002),
('FAULT_003', '控制系统异常', 1003, 3, 2003),
('FAULT_004', '齿轮磨损', 1004, 2, 2004),
('FAULT_005', '液压泄漏', 1005, 2, 2005),
('FAULT_006', '温度传感器故障', 1001, 4, 2006),
('FAULT_007', '压力传感器故障', 1001, 4, 2007),
('FAULT_008', '通讯中断', 1002, 7, 2008),
('FAULT_009', '安全回路故障', 1003, 8, 2009),
('FAULT_010', '驱动器故障', 1004, 5, 2010);

-- 模型分类测试数据
INSERT INTO model_category (name, description, parent_id, level_no, sort_order, enabled) VALUES
('控制模型', '控制系统相关模型', NULL, 1, 1, TRUE),
('安全模型', '安全系统相关模型', NULL, 1, 2, TRUE),
('诊断模型', '故障诊断相关模型', NULL, 1, 3, TRUE),
('预测模型', '预测性维护模型', 3, 2, 1, TRUE),
('分类模型', '故障分类模型', 3, 2, 2, TRUE),
('监控模型', '实时监控模型', 1, 2, 1, TRUE),
('优化模型', '性能优化模型', 1, 2, 2, TRUE),
('风险评估', '风险评估模型', 2, 2, 1, TRUE);

-- 模型测试数据
INSERT INTO model (code, name, category_id, description, version, model_type, status, priority, create_user) VALUES
('MODEL_001', 'PID控制器模型', 6, '比例积分微分控制器模型', '1.0.0', 'STANDARD', 1, 8, 'admin'),
('MODEL_002', '故障检测模型', 4, '基于机器学习的故障检测模型', '2.1.0', 'CUSTOM', 1, 9, 'admin'),
('MODEL_003', '安全评估模型', 8, '系统安全性评估模型', '1.5.0', 'STANDARD', 1, 7, 'admin'),
('MODEL_004', '无分类模型', NULL, '未分类的测试模型', '1.0.0', 'TEMPLATE', 1, 5, 'admin'),
('MODEL_005', '预测维护模型', 4, '设备寿命预测模型', '3.0.0', 'CUSTOM', 1, 10, 'admin'),
('MODEL_006', '性能优化模型', 7, '系统性能实时优化', '2.0.0', 'STANDARD', 1, 6, 'admin'),
('MODEL_007', '温度监控模型', 6, '温度实时监控和报警', '1.2.0', 'STANDARD', 1, 7, 'admin'),
('MODEL_008', '振动分析模型', 5, '设备振动模式识别', '1.8.0', 'CUSTOM', 0, 6, 'admin'),
('MODEL_009', '能耗优化模型', 7, '系统能耗分析和优化', '2.5.0', 'STANDARD', 1, 8, 'admin'),
('MODEL_010', '质量检测模型', NULL, '产品质量自动检测', '1.1.0', 'TEMPLATE', 1, 4, 'admin');

-- ===========================
-- Template Configurations
-- ===========================

-- 故障模板配置
INSERT INTO excel_template_config (template_key, template_name, table_name, sheet_name, primary_key_fields, update_mode, business_config) VALUES
('fault', '故障数据模板', 'fault', '故障数据', 'code', 'INSERT_OR_UPDATE', '{"allowedPrimaryKeys": ["code", "id", "name"], "defaultPrimaryKey": "code", "requiredConditions": ["function_id"]}'),
('fault_by_id', '故障数据模板(按ID)', 'fault', '故障数据', 'id', 'INSERT_OR_UPDATE', '{"allowedPrimaryKeys": ["code", "id", "name"], "defaultPrimaryKey": "id"}'),
('fault_by_name', '故障数据模板(按名称)', 'fault', '故障数据', 'name', 'UPDATE_ONLY', '{"allowedPrimaryKeys": ["code", "id", "name"], "defaultPrimaryKey": "name"}');

-- 模型模板配置
INSERT INTO excel_template_config (template_key, template_name, table_name, sheet_name, primary_key_fields, update_mode, business_config) VALUES
('model', '模型数据模板', 'model', '模型数据', 'code', 'INSERT_OR_UPDATE', '{"allowedPrimaryKeys": ["code", "id", "name"], "defaultPrimaryKey": "code", "supportNullCategory": true}'),
('model_simple', '模型简化模板', 'model', '模型数据', 'code', 'INSERT_ONLY', '{"allowedPrimaryKeys": ["code"], "visibleFields": ["code", "name", "category_id", "description"]}');

-- 故障字段配置
INSERT INTO excel_field_config (template_id, field_name, column_name, column_index, data_type, required, visible, width, dropdown_config, sort_order) VALUES
-- 故障模板字段
(1, 'id', 'ID', 0, 'LONG', FALSE, FALSE, 10, NULL, 0),
(1, 'code', '故障编码', 1, 'STRING', TRUE, TRUE, 20, NULL, 1),
(1, 'name', '故障名称', 2, 'STRING', TRUE, TRUE, 30, NULL, 2),
(1, 'function_id', '功能ID', 3, 'LONG', TRUE, FALSE, 15, NULL, 3),
(1, 'classification_id', '故障分类', 4, 'LONG', FALSE, TRUE, 25, '{"type": "RELATED_TABLE", "tableName": "fault_classification", "valueField": "id", "displayField": "name", "whereClause": "1=1", "allowEmpty": true}', 4),
(1, 'system_element_id', '系统元素ID', 5, 'LONG', FALSE, FALSE, 15, NULL, 5),

-- 故障模板(按ID)字段
(2, 'id', 'ID', 0, 'LONG', TRUE, TRUE, 10, NULL, 0),
(2, 'code', '故障编码', 1, 'STRING', FALSE, TRUE, 20, NULL, 1),
(2, 'name', '故障名称', 2, 'STRING', TRUE, TRUE, 30, NULL, 2),
(2, 'function_id', '功能ID', 3, 'LONG', TRUE, FALSE, 15, NULL, 3),
(2, 'classification_id', '故障分类', 4, 'LONG', FALSE, TRUE, 25, '{"type": "RELATED_TABLE", "tableName": "fault_classification", "valueField": "id", "displayField": "name", "whereClause": "1=1", "allowEmpty": true}', 4),

-- 故障模板(按名称)字段
(3, 'name', '故障名称', 0, 'STRING', TRUE, TRUE, 30, NULL, 0),
(3, 'classification_id', '故障分类', 1, 'LONG', FALSE, TRUE, 25, '{"type": "RELATED_TABLE", "tableName": "fault_classification", "valueField": "id", "displayField": "name", "whereClause": "1=1", "allowEmpty": true}', 1),
(3, 'system_element_id', '系统元素ID', 2, 'LONG', FALSE, TRUE, 15, NULL, 2);

-- 模型字段配置
INSERT INTO excel_field_config (template_id, field_name, column_name, column_index, data_type, required, visible, width, dropdown_config, sort_order) VALUES
-- 完整模型模板
(4, 'id', 'ID', 0, 'LONG', FALSE, FALSE, 10, NULL, 0),
(4, 'code', '模型编码', 1, 'STRING', TRUE, TRUE, 20, NULL, 1),
(4, 'name', '模型名称', 2, 'STRING', TRUE, TRUE, 30, NULL, 2),
(4, 'category_id', '模型分类', 3, 'LONG', FALSE, TRUE, 25, '{"type": "RELATED_TABLE", "tableName": "model_category", "valueField": "id", "displayField": "name", "whereClause": "enabled = 1", "allowEmpty": true}', 3),
(4, 'description', '描述', 4, 'STRING', FALSE, TRUE, 40, NULL, 4),
(4, 'version', '版本', 5, 'STRING', FALSE, TRUE, 15, NULL, 5),
(4, 'model_type', '模型类型', 6, 'STRING', FALSE, TRUE, 20, '{"type": "STATIC", "staticOptions": [{"value": "STANDARD", "label": "标准模型"}, {"value": "CUSTOM", "label": "自定义模型"}, {"value": "TEMPLATE", "label": "模板模型"}]}', 6),
(4, 'status', '状态', 7, 'INTEGER', FALSE, TRUE, 10, '{"type": "STATIC", "staticOptions": [{"value": 1, "label": "启用"}, {"value": 0, "label": "禁用"}]}', 7),
(4, 'priority', '优先级', 8, 'INTEGER', FALSE, TRUE, 10, NULL, 8),
(4, 'create_user', '创建人', 9, 'STRING', FALSE, FALSE, 15, NULL, 9),

-- 简化模型模板
(5, 'code', '模型编码', 0, 'STRING', TRUE, TRUE, 20, NULL, 0),
(5, 'name', '模型名称', 1, 'STRING', TRUE, TRUE, 30, NULL, 1),
(5, 'category_id', '模型分类', 2, 'LONG', FALSE, TRUE, 25, '{"type": "RELATED_TABLE", "tableName": "model_category", "valueField": "id", "displayField": "name", "whereClause": "enabled = 1", "allowEmpty": true}', 2),
(5, 'description', '描述', 3, 'STRING', FALSE, TRUE, 40, NULL, 3); 