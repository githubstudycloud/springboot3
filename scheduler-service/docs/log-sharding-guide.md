# 日志分库分表功能指南

## 概述

分布式调度系统会产生大量任务执行日志，随着系统运行时间的增长和业务规模的扩大，日志表将会存储海量数据。为了解决日志数据量大带来的性能问题，系统实现了日志分库分表功能，通过水平分库和时间分表的方式来分散数据存储压力。

## 实现方案

### 分库策略

日志分库采用任务ID哈希取模的方式，将日志按照任务ID分散到不同的物理数据库中：

1. 根据任务ID计算哈希值
2. 使用哈希值对数据源数量取模，确定数据源索引
3. 将日志存储到对应的数据源中

### 分表策略

日志分表采用按月分表的方式，每个月的日志存储在独立的表中：

1. 表名格式为：task_log_yyyy_MM，例如：task_log_2025_04
2. 系统在每个月初自动创建新的日志表
3. 按照配置的保留策略定期清理过期日志表

## 配置说明

### 应用配置

在application.yml或对应环境的配置文件中设置以下参数：

```yaml
scheduler:
  log:
    datasource:
      count: 2                       # 日志数据源数量
    retention:
      days: 30                       # 日志记录保留天数
      months: 6                      # 日志表保留月数
    export:
      path: ./export/logs            # 日志导出路径
```

### 数据源配置

为每个日志数据源配置连接信息：

```yaml
spring:
  datasource:
    # 日志数据源0
    log0:
      name: log0
      url: jdbc:mysql://localhost:3306/scheduler_log0
      username: root
      password: root
      driver-class-name: com.mysql.cj.jdbc.Driver
    # 日志数据源1
    log1:
      name: log1
      url: jdbc:mysql://localhost:3306/scheduler_log1
      username: root
      password: root
      driver-class-name: com.mysql.cj.jdbc.Driver
```

## 使用指南

### 记录日志

在服务层中使用LogService接口记录日志：

```java
@Autowired
private LogService logService;

// 记录信息日志
TaskLog log = new TaskLog();
log.setTaskId(1001L);
log.setExecutionId(2001L);
log.setNodeId("node123");
log.setMessage("任务开始执行");
logService.logInfo(log);

// 记录警告日志
log.setMessage("任务执行时间超过预期");
logService.logWarning(log);

// 记录错误日志
log.setMessage("任务执行失败: 连接超时");
logService.logError(log);
```

### 查询日志

使用LogService接口查询日志：

```java
@Autowired
private LogService logService;

// 分页查询任务日志
Page<TaskLog> logs = logService.findLogsByTaskId(1001L, PageRequest.of(0, 10));

// 分页查询执行记录日志
Page<TaskLog> executionLogs = logService.findLogsByExecutionId(2001L, PageRequest.of(0, 10));

// 条件查询日志
LogQueryParam queryParam = new LogQueryParam();
queryParam.setTaskId(1001L);
queryParam.setLevel("ERROR");
queryParam.setKeyword("超时");
Page<TaskLog> filteredLogs = logService.findLogs(queryParam, PageRequest.of(0, 10));
```

### 使用自定义注解切换数据源

对于需要手动控制数据源的场景，可以使用@DataSourceSwitch注解：

```java
// 切换到主数据源
@DataSourceSwitch("master")
public void doSomethingWithMasterDb() {
    // ...
}

// 切换到从数据源
@DataSourceSwitch("slave0")
public void doSomethingWithSlaveDb() {
    // ...
}

// 切换到日志数据源，根据taskId自动选择
@DataSourceSwitch(isLogDatabase = true, hashField = "taskId")
public void saveSpecialLog(Long taskId, String message) {
    // ...
}
```

## 维护管理

### 日志表初始化

系统在启动时自动初始化当前月份的日志表，并定期（每天凌晨2点）检查和创建新的月份日志表。

如需手动初始化日志表，可以调用：

```java
@Autowired
private LogServiceImpl logService;

// 初始化当前月份的日志表
boolean success = logService.initCurrentMonthLogTables();
```

### 日志清理

系统会根据配置的保留策略定期清理过期日志：

```java
@Autowired
private LogServiceImpl logService;

// 清理过期日志记录
int cleanedCount = logService.cleanupLogs(30); // 保留30天的日志

// 清理过期日志表
int droppedTables = logService.cleanupExpiredLogTables(6); // 保留6个月的日志表
```

## 注意事项

1. 查询跨多个数据源的日志时，性能会受到影响，尽量使用taskId限定查询范围
2. 导出大量日志数据时，注意控制时间范围，避免内存溢出
3. 确保所有日志数据源都已正确配置并可访问
4. 生产环境建议使用独立的物理服务器部署日志数据库，避免与主业务数据库争用资源

## 扩展方向

1. 支持更多分片策略，如按业务线分库、按日/周分表等
2. 添加日志聚合查询功能，支持跨库统计分析
3. 实现日志数据源动态扩容，支持在线增加数据源
4. 增加日志数据异步写入，提高写入性能
