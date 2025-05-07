CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

-- 部门表
CREATE TABLE IF NOT EXISTS `sys_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `name` varchar(50) NOT NULL COMMENT '部门名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父部门ID',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态(0-禁用,1-正常)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除(0-否,1-是)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

-- 数据源配置表
CREATE TABLE IF NOT EXISTS `data_source_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `name` varchar(100) NOT NULL COMMENT '数据源名称',
  `type` varchar(50) NOT NULL COMMENT '数据源类型',
  `connection_params` text NOT NULL COMMENT '连接参数(JSON格式)',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态(0-禁用,1-正常)',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除(0-否,1-是)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据源配置表';

-- 数据采集任务表
CREATE TABLE IF NOT EXISTS `data_collect_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `name` varchar(100) NOT NULL COMMENT '任务名称',
  `source_id` bigint(20) NOT NULL COMMENT '数据源ID',
  `target_id` bigint(20) NOT NULL COMMENT '目标数据源ID',
  `task_config` text NOT NULL COMMENT '任务配置(JSON格式)',
  `cron_expression` varchar(100) DEFAULT NULL COMMENT 'Cron表达式',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态(0-停止,1-运行)',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除(0-否,1-是)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据采集任务表';

-- 数据质量规则表
CREATE TABLE IF NOT EXISTS `data_quality_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `name` varchar(100) NOT NULL COMMENT '规则名称',
  `rule_type` varchar(50) NOT NULL COMMENT '规则类型',
  `rule_config` text NOT NULL COMMENT '规则配置(JSON格式)',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除(0-否,1-是)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据质量规则表';

-- 数据质量检查结果表
CREATE TABLE IF NOT EXISTS `data_quality_check_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '结果ID',
  `rule_id` bigint(20) NOT NULL COMMENT '规则ID',
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务ID',
  `result` text NOT NULL COMMENT '检查结果(JSON格式)',
  `status` tinyint(4) NOT NULL COMMENT '状态(0-失败,1-成功)',
  `check_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '检查时间',
  PRIMARY KEY (`id`),
  KEY `idx_rule_id` (`rule_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_check_time` (`check_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数据质量检查结果表';

-- 插入管理员用户
INSERT INTO `sys_user` (`username`, `password`, `name`, `email`, `status`) 
VALUES ('admin', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', '系统管理员', 'admin@example.com', 1);

-- 插入基础角色
INSERT INTO `sys_role` (`name`, `code`, `description`) 
VALUES ('管理员', 'ROLE_ADMIN', '系统管理员'),
       ('普通用户', 'ROLE_USER', '普通用户');

-- 关联管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) 
VALUES (1, 1);
