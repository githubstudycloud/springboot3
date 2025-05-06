# Platform Collect - 高级数据采集框架

## 概述

Platform Collect 是一个灵活、可扩展的数据采集框架，设计用于从各种异构数据源高效采集数据。框架采用领域驱动设计(DDD)和六边形架构，支持多种采集模式、复杂的处理流水线和精细化的采集控制。

## 核心特性

- **多源数据采集**：支持API、数据库、XML、文件等多种数据源
- **采集流水线**：基于流水线模式的数据处理架构，支持复杂的多阶段处理
- **插件化设计**：各类连接器和处理器采用插件化设计，便于扩展
- **精细化控制**：支持版本化的采集配置和字段级别的采集控制
- **两阶段处理**：支持分阶段数据处理和持久化，提高处理灵活性
- **增量采集**：高级增量采集机制，支持多字段组合水印管理
- **错误处理**：多级错误处理和重试机制，确保数据可靠性
- **与调度系统集成**：与平台统一调度系统无缝集成
- **与数据治理集成**：与数据治理模块协同工作，确保数据质量

## 模块结构

- **platform-collect-core**：核心接口和领域模型
- **platform-collect-connectors**：各类数据源连接器
  - platform-collect-api：API数据源连接器
  - platform-collect-xml：XML数据源连接器
  - platform-collect-rdbms：关系型数据库连接器
  - platform-collect-mongodb：MongoDB连接器
- **platform-collect-processors**：数据处理器
  - platform-collect-filter：数据过滤处理器
  - platform-collect-transform：数据转换处理器
  - platform-collect-validator：数据验证处理器
- **platform-collect-loaders**：数据加载器
  - platform-collect-mongo-loader：MongoDB加载器
  - platform-collect-db-loader：数据库加载器
- **platform-collect-admin**：管理界面和API

## 架构设计

Platform Collect 遵循六边形架构原则：

```
+----------------------------------------------------------+
|                      接口适配器层                         |
|  +----------------+  +----------------+  +-------------+ |
|  |    REST API    |  |   调度适配器    |  |   管理界面   | |
|  +----------------+  +----------------+  +-------------+ |
+----------------------------------------------------------+
|                        应用服务层                         |
|  +----------------+  +----------------+  +-------------+ |
|  |  采集任务服务   |  |   执行引擎服务  |  |  监控服务   | |
|  +----------------+  +----------------+  +-------------+ |
+----------------------------------------------------------+
|                         领域层                           |
|  +----------------+  +----------------+  +-------------+ |
|  |    数据源      |  |    采集器      |  |  处理流水线  | |
|  +----------------+  +----------------+  +-------------+ |
+----------------------------------------------------------+
|                       基础设施层                         |
|  +----------------+  +----------------+  +-------------+ |
|  |  数据源连接器   |  |    存储服务    |  |  消息队列   | |
|  +----------------+  +----------------+  +-------------+ |
+----------------------------------------------------------+
```

## 使用场景

Platform Collect 适用于以下场景：

1. **多系统数据整合**：从多个业务系统采集数据进行统一管理
2. **版本化数据采集**：对不同版本的系统实施不同的采集策略
3. **复杂API数据采集**：支持分步骤采集和批量处理
4. **XML/非结构化数据处理**：解析和转换复杂的XML或其他格式数据
5. **两阶段数据加载**：通过中间存储进行数据清洗和转换
6. **数据质量控制**：与数据治理系统集成，确保数据质量
7. **增量数据同步**：精细化控制增量采集策略

## 已完成工作

1. **核心领域模型**：已定义完整的领域模型，包括 DataSource、CollectTask、PipelineStage、ExecutionContext 和 WatermarkConfig 等
2. **核心接口**：已定义完整的端口接口，包括 DataSourceConnector、Processor、Loader 等，以及领域服务接口
3. **仓储接口**：已定义 DataSourceRepository、TaskRepository、ExecutionRepository 等仓储接口
4. **适配器接口**：已实现 SchedulerAdapter 和 DataGovernanceAdapter 接口
5. **自动配置**：已提供 Spring Boot 自动配置类和配置属性类

## 快速开始

### 环境要求

- Java 21+
- Maven 3.8+
- MongoDB 4.4+
- Redis 6.2+

### 构建与安装

```bash
# 克隆仓库
git clone https://github.com/yourusername/platform-collect.git

# 构建项目
cd platform-collect
mvn clean install
```

### 引入依赖

```xml
<!-- 引入核心模块 -->
<dependency>
    <groupId>com.example.platform</groupId>
    <artifactId>platform-collect-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- 引入需要的连接器 -->
<dependency>
    <groupId>com.example.platform</groupId>
    <artifactId>platform-collect-api</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- 引入需要的处理器 -->
<dependency>
    <groupId>com.example.platform</groupId>
    <artifactId>platform-collect-transform</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- 引入需要的加载器 -->
<dependency>
    <groupId>com.example.platform</groupId>
    <artifactId>platform-collect-db-loader</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 配置数据源

```yaml
platform:
  collect:
    datasources:
      - id: api-source
        type: REST_API
        config:
          baseUrl: https://api.example.com
          authType: OAUTH2
          credentials:
            clientId: ${API_CLIENT_ID}
            clientSecret: ${API_CLIENT_SECRET}
```

### 配置采集任务

```yaml
platform:
  collect:
    tasks:
      - id: task-001
        name: "客户数据采集"
        datasource: api-source
        pipeline:
          - stageName: fetch-ids
            stageType: API_CALL
            config:
              endpoint: /customers
              method: GET
          - stageName: fetch-details
            stageType: BATCH_API_CALL
            config:
              endpoint: /customers/details
              method: POST
              batchSize: 200
```

## 与其他模块集成

### 与调度系统(platform-scheduler)集成

Platform Collect 利用平台统一定时器(platform-scheduler)进行任务调度，无需单独实现定时调度功能。

```java
// 使用调度系统的注解进行任务调度
@SchedulableTask(name = "customer-data-collect")
public class CustomerDataCollectTask implements ScheduledTaskHandler {
    @Override
    public TaskResult execute(TaskContext context) {
        // 启动采集流程
        return collectService.executeTask("task-001", context.getParams());
    }
}
```

### 与数据治理(platform-data-governance)集成

采集框架与数据治理系统无缝集成，在采集流程中应用数据质量规则。

```yaml
platform:
  collect:
    tasks:
      - id: task-001
        name: "客户数据采集"
        # 其他配置...
        qualityControl:
          enabled: true
          rulesetId: customer-data-quality
          failOnQualityIssues: true
```

## 可扩展性设计

### 添加新的数据源连接器

实现`DataSourceConnector`接口并注册为Spring组件：

```java
@Component
@DataSourceType("CUSTOM_API")
public class CustomApiConnector implements DataSourceConnector {
    @Override
    public ConnectionResult connect(DataSourceConfig config) {
        // 实现连接逻辑
    }
    
    @Override
    public FetchResult fetch(FetchRequest request) {
        // 实现数据获取逻辑
    }
}
```

### 添加新的处理器

实现`Processor`接口并注册为Spring组件：

```java
@Component
@ProcessorType("CUSTOM_TRANSFORM")
public class CustomTransformer implements Processor {
    @Override
    public ProcessResult process(ProcessRequest request) {
        // 实现数据处理逻辑
    }
}
```

## 两阶段采集流程

对于复杂API采集场景，框架设计了两阶段采集流程：

1. **阶段一：ID获取**
   - 通过初始查询获取对象ID列表
   - 支持分页和条件过滤
   - 实现ID去重和合并

2. **阶段二：详情获取**
   - 基于ID列表，批量获取详情数据
   - 动态调整批量大小，避免请求过大
   - 并行处理和错误恢复
   - 增量策略应用

## 高级增量采集

框架支持高级增量采集机制，基于以下设计：

1. **多字段水印**
   - 支持基于时间戳字段的水印跟踪
   - 支持基于版本号的水印跟踪
   - 支持多字段组合水印
   - 支持水印比较策略定制

2. **水印管理**
   - 水印存储和恢复
   - 水印版本控制
   - 水印回滚支持
   - 增量条件生成

## 贡献指南

我们欢迎社区贡献，请参阅[CONTRIBUTING.md](CONTRIBUTING.md)了解贡献流程。

## 许可证

本项目采用 [MIT 许可证](LICENSE)。
