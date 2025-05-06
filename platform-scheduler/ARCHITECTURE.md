# Platform-Scheduler 架构设计

本文档描述了基于DDD和六边形架构的平台调度系统架构设计，包括核心概念、层次划分和关键组件。

## 1. 整体架构

平台调度系统采用六边形架构（又称端口与适配器架构），将核心领域逻辑与外部技术实现隔离。整个系统分为以下几层：

### 1.1 领域层 (Domain)

领域层包含核心业务逻辑和领域模型，是整个系统的核心。该层不依赖于任何外部框架或技术，确保业务规则的纯粹性和稳定性。

主要组件：
- 领域模型 (domain/model)：Job、TaskInstance、Executor等聚合根
- 领域服务 (domain/service)：定义业务操作的服务接口
- 领域事件 (domain/event)：表示领域状态变化的事件
- 仓储接口 (domain/repository)：定义领域对象持久化接口

### 1.2 应用层 (Application)

应用层协调领域对象，实现特定的应用场景或用例。它依赖于领域层，但不包含具体的业务规则。

主要组件：
- 应用服务 (application/service)：实现用例的服务
- DTO (application/dto)：数据传输对象，用于与接口层交互
- 命令 (application/command)：封装请求的命令对象
- 查询 (application/query)：封装查询操作的对象

### 1.3 基础设施层 (Infrastructure)

基础设施层提供技术实现，包括数据库访问、消息队列、外部服务集成等。它实现领域层定义的接口，为应用提供基础设施服务。

主要组件：
- 持久化实现 (infrastructure/persistence)：实现仓储接口
- 事件发布 (infrastructure/event)：实现事件发布机制
- 分布式锁 (infrastructure/lock)：提供分布式锁实现
- 集群协调 (infrastructure/cluster)：实现集群节点管理
- 技术配置 (infrastructure/config)：配置技术组件

### 1.4 接口层 (Interfaces)

接口层负责与外部系统交互，包括API控制器、消息监听器等。它将外部请求转换为应用层可处理的命令或查询。

主要组件：
- REST控制器 (interfaces/rest)：提供HTTP接口
- 门面 (interfaces/facade)：提供统一的服务入口
- 事件监听器 (interfaces/listener)：处理外部事件

## 2. 核心领域模型

### 2.1 作业 (Job)

作业是调度系统的核心聚合根，定义了需要执行的任务、调度策略和执行参数。

主要属性：
- ID、名称、描述、类型
- 调度策略（CRON、固定延迟、固定频率、一次性）
- 执行参数
- 失败处理策略（重试、超时）
- 依赖关系

主要操作：
- 创建/更新作业
- 启用/禁用作业
- 管理依赖关系

### 2.2 任务实例 (TaskInstance)

任务实例表示作业的一次具体执行，跟踪执行状态、结果和日志。

主要属性：
- ID、关联的作业ID
- 状态（等待、运行、完成、失败等）
- 执行参数
- 执行时间、结果、错误信息
- 日志条目

主要操作：
- 创建任务实例
- 启动/暂停/恢复执行
- 完成/失败处理
- 记录日志

### 2.3 执行器 (Executor)

执行器代表负责执行任务的节点，管理节点资源和状态。

主要属性：
- ID、名称、类型
- 主机地址、端口
- 资源信息（CPU、内存）
- 状态（在线、离线、维护）
- 标签

主要操作：
- 注册/注销执行器
- 更新状态和资源信息
- 分配任务

## 3. 关键技术实现

### 3.1 任务调度

- 基于Quartz实现定时调度
- 支持CRON表达式、固定延迟和固定频率
- 任务状态跟踪和管理
- 失败重试和超时控制

### 3.2 集群协调

- 基于Redis实现分布式锁和主节点选举
- 节点状态监控和心跳检测
- 任务均衡分配
- 故障自动恢复

### 3.3 事件驱动

- 基于领域事件实现组件间通信
- Spring事件机制实现本地事件发布
- Kafka实现事件持久化和跨系统通知

### 3.4 数据存储

- 使用MySQL存储作业定义和任务历史
- JPA实现仓储层
- Flyway进行数据库版本管理

## 4. 扩展点

系统设计了以下扩展点，便于未来功能扩展：

### 4.1 作业处理器

通过JobHandler接口，支持不同类型的作业处理逻辑。

```java
public interface JobHandler {
    TaskResult execute(JobContext context);
}
```

### 4.2 调度策略

支持自定义调度策略，扩展标准的调度类型。

```java
public interface ScheduleStrategy {
    LocalDateTime getNextExecutionTime(LocalDateTime lastExecutionTime);
}
```

### 4.3 执行器选择器

通过ExecutorSelector接口，支持自定义执行器选择策略。

```java
public interface ExecutorSelector {
    Executor selectExecutor(Job job, List<Executor> availableExecutors);
}
```

## 5. 与其他模块的集成

### 5.1 与注册模块集成

- 通过platform-scheduler-register模块管理作业注册和配置
- 支持作业模板和参数配置
- 管理作业依赖关系

### 5.2 与查询模块集成

- 通过platform-scheduler-query模块提供任务查询和统计
- 支持历史记录查询和任务状态追踪
- 提供监控和分析功能

### 5.3 与数据可视化模块集成

- 提供任务执行统计和性能分析数据
- 支持任务状态和依赖关系可视化
- 集成系统监控仪表盘

## 6. 下一步开发计划

1. 完善领域服务实现
   - 实现作业调度服务
   - 实现任务执行服务
   - 实现执行器管理服务

2. 开发仓储层实现
   - 配置JPA实体映射
   - 实现仓储接口

3. 实现REST API接口
   - 作业管理API
   - 任务控制API
   - 执行器管理API

4. 开发监控和统计功能
   - 系统状态监控
   - 任务执行统计
   - 性能分析

5. 集成测试和部署
   - 编写集成测试
   - 配置CI/CD流程
   - 编写部署文档
