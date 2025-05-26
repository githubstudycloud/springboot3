-- Excel Framework 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `excel_framework` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `excel_framework`;

-- ===============================================
-- 配置管理表
-- ===============================================

-- Excel模板配置表
CREATE TABLE `excel_template_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `template_key` VARCHAR(64) NOT NULL COMMENT '模板标识',
    `template_n` VARCHAR(128) NOT NULL COMMENT '模板名称',
    `entity_class` VARCHAR(255) NOT NULL COMMENT '实体类全路径',
    `table_n` VARCHAR(64) NOT NULL COMMENT '表名',
    `sheet_n` VARCHAR(64) NOT NULL DEFAULT '数据' COMMENT 'Sheet名称',
    `primary_key_fields` JSON COMMENT '主键字段JSON数组',
    `update_mode` VARCHAR(32) DEFAULT 'INSERT_OR_UPDATE' COMMENT '更新模式',
    `description` TEXT COMMENT '模板描述',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用 1:启用 0:禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_template_key` (`template_key`),
    INDEX `idx_entity_class` (`entity_class`),
    INDEX `idx_table_n` (`table_n`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Excel模板配置表';

-- Excel字段配置表
CREATE TABLE `excel_field_config` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `template_id` BIGINT NOT NULL COMMENT '模板ID',
    `field_n` VARCHAR(64) NOT NULL COMMENT '字段名',
    `column_n` VARCHAR(64) NOT NULL COMMENT '列名',
    `column_index` INT NOT NULL COMMENT '列索引',
    `data_type` VARCHAR(32) NOT NULL DEFAULT 'STRING' COMMENT '数据类型',
    `required` TINYINT(1) DEFAULT 0 COMMENT '是否必填',
    `visible` TINYINT(1) DEFAULT 1 COMMENT '是否可见',
    `width` INT DEFAULT 15 COMMENT '列宽',
    `date_format` VARCHAR(32) COMMENT '日期格式',
    `number_format` VARCHAR(32) COMMENT '数字格式',
    `dropdown_config` JSON COMMENT '下拉配置JSON',
    `validation_rules` JSON COMMENT '验证规则JSON',
    `default_value` VARCHAR(255) COMMENT '默认值',
    `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_template_field` (`template_id`, `field_n`),
    UNIQUE KEY `uk_template_column_index` (`template_id`, `column_index`),
    INDEX `idx_template_id` (`template_id`),
    INDEX `idx_column_index` (`column_index`),
    FOREIGN KEY (`template_id`) REFERENCES `excel_template_config`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Excel字段配置表';

-- ===============================================
-- 示例业务表
-- ===============================================

-- 故障分类表
CREATE TABLE `fault_classification` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `n` VARCHAR(128) NOT NULL COMMENT '分类名称',
    `code` VARCHAR(64) COMMENT '分类编码',
    `scope_type` TINYINT NOT NULL DEFAULT 1 COMMENT '范围类型 1:全局 2:本地',
    `function_tree_id` BIGINT COMMENT '功能/域ID(本地范围时)',
    `parent_id` BIGINT COMMENT '父分类ID',
    `description` TEXT COMMENT '描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_n` (`n`),
    UNIQUE KEY `uk_code` (`code`),
    INDEX `idx_scope_type` (`scope_type`),
    INDEX `idx_function_tree_id` (`function_tree_id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故障分类表';

-- 故障表
CREATE TABLE `fault` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `code` VARCHAR(64) NOT NULL COMMENT '故障编码(功能路径+故障名称的Hash)',
    `n` VARCHAR(128) NOT NULL COMMENT '故障名称',
    `function_id` BIGINT NOT NULL COMMENT '功能ID',
    `classification_id` BIGINT COMMENT '故障分类ID',
    `system_element_id` BIGINT COMMENT '系统元素ID',
    `severity_level` TINYINT DEFAULT 1 COMMENT '严重级别 1:低 2:中 3:高 4:紧急',
    `description` TEXT COMMENT '故障描述',
    `solution` TEXT COMMENT '解决方案',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1:活跃 2:已解决 3:已关闭',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_code` (`code`),
    INDEX `idx_function_id` (`function_id`),
    INDEX `idx_classification_id` (`classification_id`),
    INDEX `idx_system_element_id` (`system_element_id`),
    INDEX `idx_n` (`n`),
    FOREIGN KEY (`classification_id`) REFERENCES `fault_classification`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='故障表';

-- 模型分类表
CREATE TABLE `model_category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `n` VARCHAR(128) NOT NULL COMMENT '分类名称',
    `code` VARCHAR(64) COMMENT '分类编码',
    `description` TEXT COMMENT '分类描述',
    `parent_id` BIGINT COMMENT '父分类ID',
    `level` TINYINT DEFAULT 1 COMMENT '层级',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `icon` VARCHAR(255) COMMENT '图标',
    `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_n` (`n`),
    UNIQUE KEY `uk_code` (`code`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模型分类表';

-- 模型表
CREATE TABLE `model` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `code` VARCHAR(64) NOT NULL COMMENT '模型编码',
    `n` VARCHAR(128) NOT NULL COMMENT '模型名称',
    `category_id` BIGINT COMMENT '模型分类ID',
    `version` VARCHAR(32) COMMENT '模型版本',
    `description` TEXT COMMENT '模型描述',
    `model_type` VARCHAR(32) COMMENT '模型类型',
    `file_path` VARCHAR(512) COMMENT '模型文件路径',
    `file_size` BIGINT COMMENT '文件大小(字节)',
    `checksum` VARCHAR(64) COMMENT '文件校验和',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1:草稿 2:发布 3:已弃用',
    `tags` JSON COMMENT '标签JSON数组',
    `metadata` JSON COMMENT '元数据JSON',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_code` (`code`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_n` (`n`),
    INDEX `idx_status` (`status`),
    INDEX `idx_create_time` (`create_time`),
    FOREIGN KEY (`category_id`) REFERENCES `model_category`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模型表';

-- ===============================================
-- 系统操作日志表
-- ===============================================

-- Excel操作日志表
CREATE TABLE `excel_operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `template_key` VARCHAR(64) NOT NULL COMMENT '模板标识',
    `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型 IMPORT/EXPORT/TEMPLATE_DOWNLOAD',
    `file_n` VARCHAR(255) COMMENT '文件名',
    `file_size` BIGINT COMMENT '文件大小',
    `record_count` INT COMMENT '记录数量',
    `success_count` INT COMMENT '成功数量',
    `fail_count` INT COMMENT '失败数量',
    `error_message` TEXT COMMENT '错误信息',
    `cost_time` BIGINT COMMENT '耗时(毫秒)',
    `operator` VARCHAR(64) COMMENT '操作人',
    `ip_address` VARCHAR(64) COMMENT 'IP地址',
    `user_agent` VARCHAR(512) COMMENT 'User Agent',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1:成功 2:失败 3:部分成功',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_template_key` (`template_key`),
    INDEX `idx_operation_type` (`operation_type`),
    INDEX `idx_operator` (`operator`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Excel操作日志表';
