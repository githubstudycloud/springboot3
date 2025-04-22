-- 创建数据库
CREATE DATABASE IF NOT EXISTS scheduler DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE scheduler;

-- 任务表
CREATE TABLE IF NOT EXISTS `task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `name` varchar(100) NOT NULL COMMENT '任务名称',
  `description` varchar(500) DEFAULT NULL COMMENT '任务描述',
  `type` varchar(50) NOT NULL COMMENT '任务类型(HTTP/SCRIPT/CLASS等)',
  `target` varchar(1000) NOT NULL COMMENT '任务目标(URL/脚本路径/类名)',
  `parameters` text DEFAULT NULL COMMENT '任务参数(JSON格式)',
  `schedule_type` varchar(20) NOT NULL COMMENT '调度类型(CRON/FIXED_RATE/FIXED_DELAY)',
  `cron_expression` varchar(100) DEFAULT NULL COMMENT 'Cron表达式',
  `fixed_rate` bigint(20) DEFAULT NULL COMMENT '固定频率(毫秒)',
  `fixed_delay` bigint(20) DEFAULT NULL COMMENT '固定延迟(毫秒)',
  `initial_delay` bigint(20) DEFAULT NULL COMMENT '初始延迟(毫秒)',
  `timeout` bigint(20) DEFAULT NULL COMMENT '超时时间(毫秒)',
  `retry_count` int(11) DEFAULT 0 COMMENT '重试次数',
  `retry_interval` bigint(20) DEFAULT NULL COMMENT '重试间隔(毫秒)',
  `status` varchar(20) NOT NULL COMMENT '任务状态(CREATED/ENABLED/DISABLED/PAUSED/RUNNING/FAILED)',
  `current_retry_count` int(11) DEFAULT 0 COMMENT '当前重试次数',
  `last_execution_time` datetime DEFAULT NULL COMMENT '上次执行时间',
  `next_execution_time` datetime DEFAULT NULL COMMENT '下次执行时间',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_next_execution_time` (`next_execution_time`),
  INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务定义表';

-- 任务执行记录表
CREATE TABLE IF NOT EXISTS `task_execution` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '执行ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `node_id` varchar(100) NOT NULL COMMENT '执行节点ID',
  `trigger_type` varchar(20) NOT NULL COMMENT '触发类型(SCHEDULED/MANUAL)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` varchar(20) NOT NULL COMMENT '执行状态(RUNNING/SUCCESS/FAILED/TIMEOUT/TERMINATED)',
  `result` text DEFAULT NULL COMMENT '执行结果',
  `error_message` text DEFAULT NULL COMMENT '错误信息',
  PRIMARY KEY (`id`),
  INDEX `idx_task_id` (`task_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行记录表';

-- 调度节点表
CREATE TABLE IF NOT EXISTS `scheduler_node` (
  `id` varchar(100) NOT NULL COMMENT '节点ID',
  `host` varchar(100) NOT NULL COMMENT '主机名',
  `ip` varchar(50) NOT NULL COMMENT 'IP地址',
  `port` int(11) NOT NULL COMMENT '端口',
  `status` varchar(20) NOT NULL COMMENT '节点状态(ONLINE/OFFLINE)',
  `last_heartbeat` datetime NOT NULL COMMENT '最后心跳时间',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_last_heartbeat` (`last_heartbeat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调度节点表';

-- 任务进度表
CREATE TABLE IF NOT EXISTS `task_progress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `execution_id` bigint(20) NOT NULL COMMENT '执行ID',
  `progress` int(11) NOT NULL DEFAULT 0 COMMENT '进度百分比(0-100)',
  `status_desc` varchar(500) DEFAULT NULL COMMENT '状态描述',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_execution` (`task_id`,`execution_id`),
  INDEX `idx_execution_id` (`execution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务进度表';

-- 任务日志表（按月分表示例，实际由程序动态创建）
CREATE TABLE IF NOT EXISTS `task_log_2025_04` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `execution_id` bigint(20) NOT NULL COMMENT '执行ID',
  `node_id` varchar(100) NOT NULL COMMENT '节点ID',
  `level` varchar(10) NOT NULL COMMENT '日志级别(INFO/WARN/ERROR)',
  `message` text NOT NULL COMMENT '日志内容',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_task_id` (`task_id`),
  INDEX `idx_execution_id` (`execution_id`),
  INDEX `idx_created_time` (`created_time`),
  INDEX `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务日志表(按月分表)';

-- 分布式锁表（备用，优先使用Redis实现分布式锁）
CREATE TABLE IF NOT EXISTS `distributed_lock` (
  `lock_key` varchar(255) NOT NULL COMMENT '锁键',
  `lock_value` varchar(255) NOT NULL COMMENT '锁值',
  `node_id` varchar(100) NOT NULL COMMENT '持有节点',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`lock_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分布式锁表';
