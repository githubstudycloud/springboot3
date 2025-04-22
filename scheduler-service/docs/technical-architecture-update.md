# 分布式定时任务系统技术架构设计（更新版）

## 1. 已实现组件概述

本文档针对已实现的核心组件进行更新说明，确保设计文档与代码实现保持一致。

### 1.1 核心组件架构

```
+----------------------------------------+
|               TaskScheduler            |
|  (负责任务扫描、触发执行和状态管理)        |
+----------------+-----+----------------+
                 |
        +--------v--------+
        | DistributedLock |
        | (任务执行锁控制)  |
        +-----------------+
                 |
+----------------v-----------------+
|          TaskExecutorFactory     |
| (根据任务类型获取对应的执行器)      |
+-+-------------+----------------+-+
  |             |                |
+-v------+  +---v----+    +------v---+
|HTTP任务|  |脚本任务 |    |其他类型任务|
|执行器  |  |执行器   |    |执行器     |
+--------+  +--------+    +----------+
```

### 1.2 数据流向图

```
+--------------+     +----------------+     +----------------+
| 任务配置定义  | --> | TaskRepository | --> | 数据库持久化    |
+--------------+     +----------------+     +----------------+
                              |
                     +--------v--------+
                     |  TaskScheduler  |
                     +-----------------+
                              |
              +---------------v---------------+
              |                               |
    +---------v----------+     +--------------v-------------+
    | TaskExecutorFactory|     |    DistributedLockManager  |
    +--------------------+     +--------------------------+
              |                               |
              |                     +---------v----------+
    +---------v----------+          |     Redis/MySQL    |
    |    TaskExecutor    |          +--------------------+
    +--------------------+
              |
    +---------v----------+     +------------------+
    |   执行任务处理      | --> |  目标系统/服务    |
    +--------------------+     +------------------+
              |
    +---------v----------+     +-----------------+
    | TaskStatusManager  | --> |  状态更新缓存    |
    +--------------------+     +-----------------+
              |
    +---------v----------+     +-----------------+
    |    LogManager      | --> |  日志分表存储    |
    +--------------------+     +-----------------+
```

## 2. 已实现核心组件详解

### 2.1 任务调度器 (TaskScheduler)

已实现的任务调度器(DefaultTaskScheduler)提供以下功能：

- **任务扫描**：定时扫描待执行的任务
- **分布式锁控制**：确保集群环境下任务不被重复执行
- **多种调度策略支持**：
  - Cron表达式调度
  - 固定频率调度
  - 固定延迟调度
- **任务控制**：
  - 启动/停止/暂停/恢复调度器
  - 手动执行任务
  - 终止正在执行的任务
- **状态管理**：
  - 更新任务状态
  - 计算下次执行时间
  - 处理任务重试

关键代码示例：
```java
@Scheduled(fixedRateString = "${scheduler.task.scan-rate:5000}")
public void scheduleTasks() {
    if (!running.get() || paused.get()) {
        return;
    }
    
    logger.debug("开始扫描待执行任务");
    
    // 查询待执行任务
    List<Task> dueTasks = scanTasks();
    
    // 执行任务逻辑...
}
```

### 2.2 分布式锁管理器 (DistributedLockManager)

实现了两种分布式锁机制：

- **RedisDistributedLockManager**：基于Redis的分布式锁实现
  - 使用Redisson客户端
  - 支持锁超时和自动续期
  - 安全的锁释放机制

- **MysqlDistributedLockManager**：基于MySQL的分布式锁备选实现
  - 使用数据库表记录锁信息
  - 提供Redis不可用时的备选方案

关键代码示例：
```java
@Override
public boolean tryLock(String lockKey, long timeoutMillis) {
    RLock lock = redissonClient.getLock(lockKey);
    try {
        return lock.tryLock(0, timeoutMillis, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
        logger.warn("获取分布式锁被中断: {}", lockKey, e);
        Thread.currentThread().interrupt();
        return false;
    }
}
```

### 2.3 任务执行器 (TaskExecutor)

实现了可扩展的任务执行器框架：

- **AbstractTaskExecutor**：任务执行器基类，提供共享功能
  - 执行生命周期管理
  - 状态和进度更新
  - 异常处理和重试逻辑
  - 日志记录

- **HttpTaskExecutor**：HTTP任务执行器
  - 支持各种HTTP方法(GET/POST/PUT/DELETE等)
  - 自定义请求头和请求体
  - 超时控制
  - 响应处理

- **TaskExecutorFactory**：任务执行器工厂
  - 自动发现和注册执行器
  - 根据任务类型获取对应执行器

关键代码示例：
```java
@Override
public TaskResult execute(Task task, Long executionId) throws Exception {
    logger.info("开始执行任务: {}, 执行ID: {}", task.getId(), executionId);
    
    try {
        // 更新进度为0%
        updateProgress(task.getId(), executionId, 0, "任务开始执行");
        
        // 执行任务逻辑
        TaskResult result = doExecute(task, executionId);
        
        // 更新进度为100%
        updateProgress(task.getId(), executionId, 100, "任务执行完成");
        
        // 更新执行记录和状态...
        
        return result;
    } catch (Exception e) {
        // 异常处理...
        throw e;
    }
}
```

### 2.4 任务状态管理器 (TaskStatusManager)

实现了集中式的状态管理：

- **缓存加速**：
  - 使用Redis缓存任务状态
  - 减少数据库查询压力
  - 支持状态实时更新

- **状态同步**：
  - 数据库和缓存双写
  - 确保状态一致性
  - 缓存过期策略

- **进度跟踪**：
  - 支持任务进度百分比更新
  - 支持状态描述文本更新
  - 适用于长时间运行的任务

关键代码示例：
```java
public TaskStatus getTaskStatus(Long taskId) {
    // 先从Redis获取
    String key = "task_status:" + taskId;
    boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
    
    if (hasKey) {
        // Redis中有缓存，直接获取
        // ...
    } else {
        // Redis中没有缓存，查询数据库
        // ...
        
        // 更新Redis缓存
        updateStatusCache(status);
    }
}
```

### 2.5 日志管理器 (LogManager)

实现了统一的日志管理：

- **分表存储**：
  - 按月自动分表
  - 表名格式：task_log_{yyyy_MM}
  - 动态表创建和切换

- **多级日志**：
  - 支持INFO/WARN/ERROR三级日志
  - 每条日志关联任务和执行ID
  - 详细时间记录

- **日志清理**：
  - 自动清理过期日志表
  - 保留策略可配置
  - 预创建未来日志表

关键代码示例：
```java
public void info(Long taskId, Long executionId, String message) {
    log(taskId, executionId, "INFO", message);
}

private void log(Long taskId, Long executionId, String level, String message) {
    try {
        // 创建日志记录
        TaskLog log = new TaskLog();
        // 设置日志属性...
        
        // 获取当前日志表名
        String tableName = getCurrentLogTableName();
        
        // 确保日志表存在
        ensureLogTableExists(tableName);
        
        // 保存日志
        logRepository.save(log, tableName);
    } catch (Exception e) {
        logger.error("记录任务日志异常", e);
    }
}
```

## 3. 数据访问层实现

### 3.1 Repository接口设计

已实现5个核心Repository接口：

- **TaskRepository**：任务数据访问
- **TaskExecutionRepository**：执行记录数据访问
- **LogRepository**：日志数据访问
- **SchedulerNodeRepository**：调度节点数据访问
- **TaskProgressRepository**：进度数据访问

### 3.2 JDBC实现

所有Repository均基于JDBC实现，提供以下特性：

- **高性能**：直接使用SQL语句，避免ORM框架开销
- **灵活性**：支持复杂查询和特殊操作
- **分表支持**：特别是LogRepository支持动态表名

关键代码示例：
```java
public List<Task> findTasksDueForExecution(Date currentTime) {
    String sql = "SELECT * FROM task WHERE status IN ('ENABLED', 'RETRY_PENDING') " +
            "AND next_execution_time <= ? ORDER BY next_execution_time";
    
    return jdbcTemplate.query(sql, rowMapper, currentTime);
}
```

## 4. 未来扩展计划

### 4.1 计划实现的组件

- **业务服务层**：
  - TaskService
  - TaskControlService
  - TaskStatusService
  - LogService

- **API控制器**：
  - TaskController
  - TaskControlController
  - TaskStatusController
  - LogController

### 4.2 高级功能扩展

- **告警通知**：任务失败自动告警
- **集群管理**：增强节点管理和负载均衡
- **任务依赖**：支持任务之间的依赖关系
- **更多任务类型**：增加更多类型的执行器
- **任务分组**：支持任务分组管理
- **权限控制**：增加细粒度的权限控制

## 5. 配置项说明

系统配置项已调整为：

```yaml
scheduler:
  task:
    scan-rate: 5000  # 任务扫描频率(毫秒)
    max-parallel-tasks: 100  # 最大并行任务数
    default-retry-count: 3  # 默认重试次数
    default-retry-interval: 60000  # 默认重试间隔(毫秒)
  node:
    id: ${spring.application.name}-${server.port}-${random.uuid}  # 节点ID
    heartbeat-rate: 30000  # 心跳频率(毫秒)
```

## 6. 总结

系统已完成核心组件的实现，提供了可靠的分布式任务调度能力。接下来将重点实现业务服务层和API层，使系统具备完整的对外服务能力。
