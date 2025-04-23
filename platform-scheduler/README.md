# Platform Scheduler（平台调度器）

## 模块简介

Platform Scheduler是平台的核心调度服务，负责管理和执行所有定时任务和计划作业。该模块基于Quartz Scheduler实现，提供高可靠性的分布式任务调度功能，支持任务触发、执行流程管理、状态跟踪和失败处理。

## 主要功能

- **任务调度**：基于cron表达式的定时调度
- **任务执行**：管理任务执行生命周期
- **状态管理**：跟踪任务执行状态和历史
- **失败处理**：任务失败重试和异常处理
- **资源限制**：CPU和内存使用监控，防止系统过载
- **优先级管理**：支持任务优先级和抢占

## 与其他调度服务的协作

Platform Scheduler服务群由三个独立但协作的微服务组成：

1. **platform-scheduler**（本模块）：核心调度引擎，负责任务触发和执行
2. **platform-scheduler-register**：任务注册服务，负责任务创建和管理
3. **platform-scheduler-query**：任务查询服务，负责状态查询和历史记录

这种分离设计使每个服务可以根据其特定负载特性独立扩展。

## 目录结构

```
platform-scheduler/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── platform/
│       │           └── scheduler/
│       │               ├── config/                # 配置类
│       │               ├── controller/            # REST控制器
│       │               ├── service/               # 业务逻辑
│       │               ├── repository/            # 数据访问
│       │               ├── model/                 # 数据模型
│       │               ├── executor/              # 任务执行器
│       │               ├── listener/              # 事件监听器
│       │               └── exception/             # 异常处理
│       └── resources/
│           └── application.yml        # 应用配置文件
└── pom.xml                            # Maven配置文件
```

## 技术细节

### 数据库设计

调度器服务使用两个主要数据表：

1. **scheduler_tasks**：存储任务定义
   - id：任务唯一标识符
   - name：任务名称
   - description：任务描述
   - cron_expression：调度表达式
   - task_class：执行类名
   - task_data：任务参数（JSON格式）
   - status：任务状态
   - priority：任务优先级
   - retry_count：当前重试次数
   - max_retry_count：最大重试次数
   - timeout_seconds：任务超时时间

2. **task_execution_history**：记录任务执行历史
   - id：执行记录ID
   - task_id：任务ID
   - start_time：开始时间
   - end_time：结束时间
   - status：执行状态
   - result：执行结果
   - error_message：错误信息
   - node_id：执行节点
   - execution_time_ms：执行时长

### 任务执行流程

1. 调度器定期扫描待执行任务
2. 根据cron表达式计算下次执行时间
3. 到达执行时间时，将任务发送到执行队列
4. 执行器从队列获取任务并执行
5. 记录执行结果和状态
6. 根据结果决定是否重试或标记完成

### 自我保护机制

调度器实现了自我保护机制，通过监控系统资源使用情况防止过载：

1. **CPU监控**：当CPU使用率超过80%时，暂缓非关键任务执行
2. **内存监控**：当内存使用率超过85%时，拒绝新任务
3. **外部流控**：支持通过外部信号进行流量控制
4. **自适应调度**：根据系统负载自动调整任务执行频率

## API接口

### 任务管理接口

```
GET  /api/v1/tasks/{taskId}             # 获取任务详情
POST /api/v1/tasks/{taskId}/execute     # 手动执行任务
PUT  /api/v1/tasks/{taskId}/pause       # 暂停任务
PUT  /api/v1/tasks/{taskId}/resume      # 恢复任务
PUT  /api/v1/tasks/{taskId}/terminate   # 终止正在执行的任务
```

### 系统管理接口

```
GET  /api/v1/scheduler/status           # 获取调度器状态
PUT  /api/v1/scheduler/pause            # 暂停所有调度活动
PUT  /api/v1/scheduler/resume           # 恢复所有调度活动
GET  /api/v1/scheduler/metrics          # 获取调度器指标
```

## 监控指标

调度器服务暴露以下监控指标：

- **任务执行计数**：按状态分组的任务执行统计
- **执行时长**：任务执行时间分布
- **失败率**：任务失败比例
- **重试计数**：任务重试统计
- **队列深度**：当前等待执行的任务数量
- **节点负载**：各节点的负载分布

## 高可用部署

调度器支持集群部署以实现高可用：

1. **多节点部署**：多个实例并行运行
2. **分布式锁**：使用数据库或Redis实现任务执行锁
3. **状态同步**：通过数据库或消息队列同步状态
4. **负载均衡**：任务在集群节点间均衡分配

## 最佳实践

### 任务设计

1. **幂等性**：任务应设计为幂等的，以支持重试
2. **参数设计**：使用JSON格式传递任务参数
3. **超时控制**：设置合理的任务超时时间
4. **异常处理**：捕获并妥善处理异常

### 调度策略

1. **错峰调度**：避免任务同时触发导致资源竞争
2. **优先级分配**：为关键任务设置更高优先级
3. **资源预留**：为突发任务预留系统资源
4. **热点时间避让**：避免在系统高峰期调度大量任务

## 开发指南

### 自定义任务开发

创建自定义任务需实现`ScheduledTask`接口：

```java
public class CustomTask implements ScheduledTask {
    @Override
    public TaskResult execute(TaskContext context) {
        // 任务执行逻辑
        return TaskResult.success("Task completed");
    }
    
    @Override
    public void onFailure(TaskContext context, Exception e) {
        // 失败处理逻辑
    }
}
```

### 任务注册

通过API或配置注册任务：

```json
{
  "name": "Daily Data Cleanup",
  "description": "Removes temporary data older than 7 days",
  "cronExpression": "0 0 0 * * ?",
  "taskClass": "com.platform.tasks.CleanupTask",
  "taskData": "{\"retentionDays\": 7, \"batchSize\": 1000}",
  "priority": 3,
  "maxRetryCount": 3,
  "timeoutSeconds": 1800
}
```

## 故障排除

常见问题及解决方案：

1. **任务未执行**：检查cron表达式和任务状态
2. **执行超时**：检查任务逻辑和超时配置
3. **频繁失败**：分析失败原因和异常日志
4. **系统过载**：调整任务调度频率和并发数
5. **节点负载不均**：检查集群配置和任务分配策略

## 版本历史

- **1.0.0-SNAPSHOT**：初始版本
  - 基本任务调度功能
  - 任务执行管理
  - 失败重试机制
  - 资源监控
  - 集群支持
