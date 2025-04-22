# 分布式定时任务系统数据库设计

## 1. 数据库表结构

### 1.1 任务表 (task)

存储任务的基本信息和调度配置。

```sql
CREATE TABLE `task` (
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
```

### 1.2 任务执行记录表 (task_execution)

记录任务的每次执行情况。

```sql
CREATE TABLE `task_execution` (
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
```

### 1.3 任务日志表 (task_log_{yyyy_MM})

记录任务执行过程中的详细日志，按月分表。

```sql
CREATE TABLE `task_log_2025_04` (
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
```

### 1.4 调度节点表 (scheduler_node)

记录调度集群中的节点信息。

```sql
CREATE TABLE `scheduler_node` (
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
```

### 1.5 分布式锁表 (distributed_lock)

用于实现分布式锁，保证任务不重复执行。

```sql
CREATE TABLE `distributed_lock` (
  `lock_key` varchar(255) NOT NULL COMMENT '锁键',
  `lock_value` varchar(255) NOT NULL COMMENT '锁值',
  `node_id` varchar(100) NOT NULL COMMENT '持有节点',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`lock_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分布式锁表';
```

### 1.6 任务进度表 (task_progress)

记录长时间运行任务的执行进度。

```sql
CREATE TABLE `task_progress` (
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
```

## 2. 表关系图

```
+-------------------+       +-------------------+       +-------------------+
|       task        | ----> |  task_execution   | ----> |    task_log       |
+-------------------+       +-------------------+       +-------------------+
        |                           |
        |                           |
        v                           v
+-------------------+       +-------------------+
|  scheduler_node   |       |   task_progress   |
+-------------------+       +-------------------+
        |
        |
        v
+-------------------+
| distributed_lock  |
+-------------------+
```

## 3. 数据库索引说明

### 3.1 任务表索引

- `PRIMARY KEY (id)`: 主键索引，用于唯一标识任务
- `INDEX idx_status (status)`: 状态索引，用于快速查询特定状态的任务
- `INDEX idx_next_execution_time (next_execution_time)`: 下次执行时间索引，用于调度器扫描需要执行的任务
- `INDEX idx_name (name)`: 任务名称索引，用于按名称搜索任务

### 3.2 任务执行记录表索引

- `PRIMARY KEY (id)`: 主键索引，用于唯一标识执行记录
- `INDEX idx_task_id (task_id)`: 任务ID索引，用于查询特定任务的执行记录
- `INDEX idx_status (status)`: 状态索引，用于查询特定状态的执行记录
- `INDEX idx_start_time (start_time)`: 开始时间索引，用于按时间查询执行记录

### 3.3 任务日志表索引

- `PRIMARY KEY (id)`: 主键索引，用于唯一标识日志记录
- `INDEX idx_task_id (task_id)`: 任务ID索引，用于查询特定任务的日志
- `INDEX idx_execution_id (execution_id)`: 执行ID索引，用于查询特定执行的日志
- `INDEX idx_created_time (created_time)`: 创建时间索引，用于按时间查询日志
- `INDEX idx_level (level)`: 日志级别索引，用于按级别查询日志

## 4. 分库分表策略

### 4.1 任务日志表分表策略

任务日志表按月分表，表名格式为`task_log_{yyyy_MM}`，如`task_log_2025_04`表示2025年4月的日志。

分表规则：
- 按日志创建时间的年月进行分表
- 写入操作根据日志的`created_time`字段确定目标表
- 查询操作根据`startDate`和`endDate`参数确定需要查询的表

### 4.2 未来扩展的分库策略

当系统规模进一步扩大时，可考虑按照以下策略进行分库：

- 任务表（task）：按任务ID范围或取模分库
- 执行记录表（task_execution）：与任务表保持相同的分库策略
- 任务日志表（task_log_{yyyy_MM}）：首先按月分表，然后按任务ID范围或取模分库

分库键：任务ID（task_id）
