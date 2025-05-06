-- Platform Scheduler核心表结构
-- 适用于MySQL 5.7
-- 基于DDD设计的领域模型表结构

-- 作业定义表
CREATE TABLE `scheduler_job` (
  `id` VARCHAR(36) NOT NULL COMMENT '作业ID',
  `name` VARCHAR(100) NOT NULL COMMENT '作业名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '作业描述',
  `type` VARCHAR(50) NOT NULL COMMENT '作业类型',
  `status` VARCHAR(20) NOT NULL COMMENT '作业状态',
  `priority` TINYINT NOT NULL DEFAULT 5 COMMENT '作业优先级(1-10)',
  `schedule_type` VARCHAR(20) NOT NULL COMMENT '调度类型',
  `cron_expression` VARCHAR(100) DEFAULT NULL COMMENT 'Cron表达式',
  `fixed_delay` BIGINT DEFAULT NULL COMMENT '固定延迟(秒)',
  `fixed_rate` BIGINT DEFAULT NULL COMMENT '固定频率(秒)',
  `start_at` DATETIME DEFAULT NULL COMMENT '首次执行时间',
  `end_at` DATETIME DEFAULT NULL COMMENT '结束时间',
  `max_executions` INT DEFAULT NULL COMMENT '最大执行次数',
  `handler_name` VARCHAR(100) NOT NULL COMMENT '处理器名称',
  `max_retry_count` INT DEFAULT NULL COMMENT '最大重试次数',
  `retry_interval` INT DEFAULT NULL COMMENT '重试间隔(秒)',
  `timeout` INT DEFAULT NULL COMMENT '超时时间(秒)',
  `created_by` VARCHAR(50) NOT NULL COMMENT '创建者',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `last_modified_by` VARCHAR(50) DEFAULT NULL COMMENT '最后修改者',
  `last_modified_at` DATETIME DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scheduler_job_name` (`name`),
  KEY `idx_scheduler_job_status` (`status`),
  KEY `idx_scheduler_job_type` (`type`),
  KEY `idx_scheduler_job_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业定义表';

-- 作业参数表
CREATE TABLE `scheduler_job_parameter` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '参数ID',
  `job_id` VARCHAR(36) NOT NULL COMMENT '作业ID',
  `name` VARCHAR(50) NOT NULL COMMENT '参数名称',
  `value` VARCHAR(1000) DEFAULT NULL COMMENT '参数值',
  `type` VARCHAR(20) DEFAULT 'STRING' COMMENT '参数类型',
  `required` TINYINT(1) DEFAULT 0 COMMENT '是否必需',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '参数描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_job_param_name` (`job_id`, `name`),
  KEY `idx_job_parameter_job_id` (`job_id`),
  CONSTRAINT `fk_job_parameter_job_id` FOREIGN KEY (`job_id`) REFERENCES `scheduler_job` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='作业参数表';

-- 作业依赖关系表
CREATE TABLE `scheduler_job_dependency` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '依赖ID',
  `job_id` VARCHAR(36) NOT NULL COMMENT '作业ID',
  `dependency_job_id` VARCHAR(36) NOT NULL COMMENT '依赖的作业ID',
  `type` VARCHAR(20) NOT NULL COMMENT '依赖类型',
  `condition` VARCHAR(500) DEFAULT NULL COMMENT '依赖条件表达式',
  `required` TINYINT(1) DEFAULT 1 COMMENT '是否强制依赖',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_job_dependency` (`job_id`, `dependency_job_id`),
  KEY `idx_job_dependency_job_id` (`job_id`),
  KEY `idx_job_dependency_dep_job_id` (`dependency_job_id`),
  CONSTRAINT `fk_job_dependency_job_id` FOREIGN KEY (`job_id`) REFERENCES `scheduler_job` (`id`),
  CONSTRAINT `fk_job_dependency_dep_job_id` FOREIGN KEY (`dependency_job_id`) REFERENCES `scheduler_job` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='作业依赖关系表';

-- 任务实例表
CREATE TABLE `scheduler_task_instance` (
  `id` VARCHAR(36) NOT NULL COMMENT '任务实例ID',
  `job_id` VARCHAR(36) NOT NULL COMMENT '作业ID',
  `job_name` VARCHAR(100) NOT NULL COMMENT '作业名称',
  `schedule_plan` VARCHAR(200) DEFAULT NULL COMMENT '调度计划',
  `executor_id` VARCHAR(36) DEFAULT NULL COMMENT '执行器ID',
  `status` VARCHAR(20) NOT NULL COMMENT '任务状态',
  `retry_count` INT DEFAULT 0 COMMENT '重试次数',
  `max_retry_count` INT DEFAULT NULL COMMENT '最大重试次数',
  `retry_interval` INT DEFAULT NULL COMMENT '重试间隔(秒)',
  `timeout` INT DEFAULT NULL COMMENT '超时时间(秒)',
  `priority` TINYINT DEFAULT 5 COMMENT '优先级',
  `scheduled_start_time` DATETIME DEFAULT NULL COMMENT '计划开始时间',
  `actual_start_time` DATETIME DEFAULT NULL COMMENT '实际开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `duration_in_millis` BIGINT DEFAULT NULL COMMENT '持续时间(毫秒)',
  `result` TEXT DEFAULT NULL COMMENT '执行结果',
  `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
  `created_by` VARCHAR(50) NOT NULL COMMENT '创建者',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_instance_job_id` (`job_id`),
  KEY `idx_task_instance_status` (`status`),
  KEY `idx_task_instance_executor_id` (`executor_id`),
  KEY `idx_task_instance_created_at` (`created_at`),
  KEY `idx_task_instance_start_time` (`actual_start_time`),
  KEY `idx_task_instance_end_time` (`end_time`),
  CONSTRAINT `fk_task_instance_job_id` FOREIGN KEY (`job_id`) REFERENCES `scheduler_job` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务实例表';

-- 任务参数表
CREATE TABLE `scheduler_task_parameter` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '参数ID',
  `task_instance_id` VARCHAR(36) NOT NULL COMMENT '任务实例ID',
  `name` VARCHAR(50) NOT NULL COMMENT '参数名称',
  `value` VARCHAR(1000) DEFAULT NULL COMMENT '参数值',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_param_name` (`task_instance_id`, `name`),
  KEY `idx_task_parameter_task_id` (`task_instance_id`),
  CONSTRAINT `fk_task_parameter_task_id` FOREIGN KEY (`task_instance_id`) REFERENCES `scheduler_task_instance` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='任务参数表';

-- 任务日志表
CREATE TABLE `scheduler_task_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `task_instance_id` VARCHAR(36) NOT NULL COMMENT '任务实例ID',
  `level` VARCHAR(10) NOT NULL COMMENT '日志级别',
  `message` TEXT NOT NULL COMMENT '日志内容',
  `timestamp` DATETIME NOT NULL COMMENT '日志时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_log_task_id` (`task_instance_id`),
  KEY `idx_task_log_level` (`level`),
  KEY `idx_task_log_timestamp` (`timestamp`),
  CONSTRAINT `fk_task_log_task_id` FOREIGN KEY (`task_instance_id`) REFERENCES `scheduler_task_instance` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='任务日志表';

-- 执行器表
CREATE TABLE `scheduler_executor` (
  `id` VARCHAR(36) NOT NULL COMMENT '执行器ID',
  `name` VARCHAR(100) NOT NULL COMMENT '执行器名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '执行器描述',
  `type` VARCHAR(50) NOT NULL COMMENT '执行器类型',
  `status` VARCHAR(20) NOT NULL COMMENT '执行器状态',
  `host` VARCHAR(100) NOT NULL COMMENT '主机地址',
  `port` INT NOT NULL COMMENT '端口号',
  `version` VARCHAR(20) DEFAULT NULL COMMENT '版本号',
  `max_concurrency` INT DEFAULT NULL COMMENT '最大并发数',
  `current_load` INT DEFAULT 0 COMMENT '当前负载',
  `total_memory` BIGINT DEFAULT NULL COMMENT '总内存(字节)',
  `free_memory` BIGINT DEFAULT NULL COMMENT '空闲内存(字节)',
  `cpu_cores` INT DEFAULT NULL COMMENT 'CPU核心数',
  `cpu_usage` DOUBLE DEFAULT NULL COMMENT 'CPU使用率',
  `last_heartbeat_time` DATETIME DEFAULT NULL COMMENT '最后心跳时间',
  `registration_time` DATETIME NOT NULL COMMENT '注册时间',
  `created_by` VARCHAR(50) NOT NULL COMMENT '创建者',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `last_modified_by` VARCHAR(50) DEFAULT NULL COMMENT '最后修改者',
  `last_modified_at` DATETIME DEFAULT NULL COMMENT '最后修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_executor_name` (`name`),
  UNIQUE KEY `uk_executor_host_port` (`host`, `port`),
  KEY `idx_executor_status` (`status`),
  KEY `idx_executor_type` (`type`),
  KEY `idx_executor_heartbeat` (`last_heartbeat_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行器表';

-- 执行器标签表
CREATE TABLE `scheduler_executor_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `executor_id` VARCHAR(36) NOT NULL COMMENT '执行器ID',
  `tag` VARCHAR(50) NOT NULL COMMENT '标签名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_executor_tag` (`executor_id`, `tag`),
  KEY `idx_executor_tag_executor_id` (`executor_id`),
  KEY `idx_executor_tag_tag` (`tag`),
  CONSTRAINT `fk_executor_tag_executor_id` FOREIGN KEY (`executor_id`) REFERENCES `scheduler_executor` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='执行器标签表';

-- 集群事件表
CREATE TABLE `scheduler_cluster_event` (
  `id` VARCHAR(36) NOT NULL COMMENT '事件ID',
  `event_type` VARCHAR(50) NOT NULL COMMENT '事件类型',
  `event_time` DATETIME NOT NULL COMMENT '事件时间',
  `node_id` VARCHAR(36) DEFAULT NULL COMMENT '节点ID',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '事件描述',
  `properties` TEXT DEFAULT NULL COMMENT '事件属性(JSON格式)',
  PRIMARY KEY (`id`),
  KEY `idx_cluster_event_type` (`event_type`),
  KEY `idx_cluster_event_time` (`event_time`),
  KEY `idx_cluster_event_node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集群事件表';

-- 分布式锁表
CREATE TABLE `scheduler_distributed_lock` (
  `lock_key` VARCHAR(100) NOT NULL COMMENT '锁键',
  `lock_holder` VARCHAR(36) NOT NULL COMMENT '锁持有者',
  `acquire_time` DATETIME NOT NULL COMMENT '获取时间',
  `expire_time` DATETIME NOT NULL COMMENT '过期时间',
  PRIMARY KEY (`lock_key`),
  KEY `idx_distributed_lock_holder` (`lock_holder`),
  KEY `idx_distributed_lock_expire` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分布式锁表';
