# Platform Collect Core

## 概述

Platform Collect Core 是数据采集框架的核心模块，定义了整个采集框架的领域模型、接口和基础组件。该模块基于领域驱动设计(DDD)和六边形架构，为整个采集框架提供核心抽象和领域规则。

## 主要组件

### 领域模型 (Domain Model)

- **数据源 (DataSource)**: 定义数据来源及其属性
- **采集任务 (CollectTask)**: 描述需要执行的采集操作
- **采集流水线 (Pipeline)**: 定义数据处理流程和阶段
- **执行上下文 (ExecutionContext)**: 存储执行过程中的状态和数据
- **水印 (Watermark)**: 增量采集时的位置标记

### 领域服务 (Domain Services)

- **采集服务 (CollectService)**: 协调采集流程的核心服务
- **任务执行服务 (TaskExecutionService)**: 管理任务执行和状态
- **监控服务 (MonitorService)**: 监控任务执行情况

### 仓储接口 (Repository Interfaces)

- **数据源仓储 (DataSourceRepository)**: 管理数据源定义
- **任务仓储 (TaskRepository)**: 管理采集任务定义
- **执行记录仓储 (ExecutionRepository)**: 存储执行历史和状态

### 端口 (Ports)

- **连接器 (Connector)**: 与各类数据源交互的接口
- **处理器 (Processor)**: 数据处理组件的接口
- **加载器 (Loader)**: 数据持久化组件的接口

## 使用方式

Core模块定义了框架的核心抽象，其他模块通过实现这些抽象来提供具体功能。使用方式示例:

```java
// 创建数据源
DataSource dataSource = DataSourceBuilder.create()
    .withType(DataSourceType.REST_API)
    .withConfig(apiConfig)
    .build();

// 创建采集任务
CollectTask task = CollectTaskBuilder.create()
    .withName("客户数据采集")
    .withDataSource(dataSource)
    .withPipeline(pipeline)
    .build();

// 执行采集任务
CollectResult result = collectService.execute(task, context);
```

## 模块依赖

- platform-common: 使用通用工具和基础组件
- Spring Boot: 依赖注入和配置管理

## 扩展点

Core模块提供了多个扩展点，支持框架的灵活扩展:

- **DataSourceConnector SPI**: 允许添加新的数据源类型
- **Processor SPI**: 支持自定义数据处理逻辑
- **Loader SPI**: 允许实现自定义数据加载器
- **Pipeline Stage SPI**: 支持自定义流水线阶段

## 高级特性

- **动态配置**: 支持运行时更新配置
- **事件通知**: 完整的事件发布机制
- **插件发现**: 基于Java SPI的插件自动发现
- **指标收集**: 内置的性能和执行指标收集

## 开发指南

查看项目中的`/docs`目录获取详细的开发文档。
