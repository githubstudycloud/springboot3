# 平台监控仪表板模块 (platform-monitor-dashboard)

## 模块概述

平台监控仪表板模块是一个完整的微服务监控解决方案，用于实时监控和可视化微服务架构中的各项指标。该模块基于DDD（领域驱动设计）和六边形架构（端口与适配器）设计原则，提供了服务发现、指标收集、健康状态监控、告警管理和可视化仪表板等核心功能。

## 核心功能

1. **服务发现与健康监控**
   - 自动发现和注册服务实例
   - 实时监控服务实例健康状态
   - 服务实例详细信息展示和管理

2. **指标收集与分析**
   - 自动收集系统和应用级别指标
   - 支持多种指标类型（CPU、内存、磁盘、请求数等）
   - 指标历史数据存储和趋势分析

3. **告警管理**
   - 基于规则的告警系统
   - 多级别告警支持
   - 告警生命周期管理（确认、解决、关闭等）

4. **通知系统**
   - 多种通知渠道支持（邮件、短信、WebHook等）
   - 可定制的通知模板
   - 通知历史记录和统计

5. **可视化仪表板**
   - 自定义仪表板和面板
   - 多种展示类型（折线图、柱状图、仪表盘等）
   - 实时数据更新和历史数据查询

## 技术架构

### 领域模型设计

采用DDD战略设计和战术设计方法，将监控系统划分为以下核心限界上下文：

- **服务监控上下文**：负责服务实例的发现、注册和健康状态监控
- **指标监控上下文**：负责指标的收集、存储和分析
- **告警管理上下文**：负责告警规则定义和告警生命周期管理
- **通知管理上下文**：负责通知渠道管理和通知发送
- **仪表板管理上下文**：负责仪表板和面板的定义和展示

### 六边形架构实现

按照六边形架构（端口与适配器）模式，模块被组织为以下几个层次：

- **领域层**：包含核心领域模型、领域服务和领域事件
- **应用层**：包含应用服务和DTO对象
- **接口层**：包含REST API接口和其他适配器
- **基础设施层**：包含仓储实现、消息队列和外部系统集成

## 技术栈

- **基础框架**：Spring Boot 3.x
- **服务注册**：Nacos
- **配置中心**：Nacos Config
- **数据库**：MongoDB (时序数据存储)
- **缓存**：Redis
- **消息队列**：Kafka (用于告警通知)
- **指标收集**：Micrometer + Prometheus
- **接口文档**：SpringDoc OpenAPI

## 集成点

- 与平台注册中心 (`platform-registry`) 集成，用于服务发现
- 与平台配置中心 (`platform-config`) 集成，用于配置管理
- 与平台网关 (`platform-gateway`) 集成，用于API路由

## 快速开始

### 前置条件

- Java 21
- Maven 3.8+
- MongoDB 4.2+
- Redis 7.x
- Kafka 3.x
- Nacos 2.x

### 构建与启动

1. 确保Nacos、MongoDB、Redis、Kafka已启动

2. 构建项目
   ```bash
   mvn clean install
   ```

3. 启动服务
   ```bash
   mvn spring-boot:run
   ```

4. 访问监控仪表板
   ```
   http://localhost:8085/monitor/dashboard
   ```

## API文档

启动服务后，可通过以下地址访问API文档：
```
http://localhost:8085/monitor/swagger-ui.html
```

## 模块设计指南

### 领域模型设计原则

1. **实体与值对象分离**：明确区分实体和值对象
2. **聚合根定义**：每个限界上下文明确定义聚合根
3. **防腐层实现**：为外部系统集成实现防腐层
4. **领域事件使用**：关键操作通过领域事件通知

### 代码组织结构

```
platform-monitor-dashboard
├── domain               # 领域层
│   ├── model            # 领域模型
│   ├── service          # 领域服务
│   ├── repository       # 仓储接口
│   └── event            # 领域事件
├── application          # 应用层
│   ├── service          # 应用服务
│   └── dto              # 数据传输对象
├── infrastructure       # 基础设施层
│   ├── repository       # 仓储实现
│   ├── config           # 配置类
│   ├── metrics          # 指标收集
│   └── alert            # 告警处理
└── interfaces           # 接口层
    ├── rest             # REST控制器
    └── facade           # 外部接口
```

## 使用示例

### 创建告警规则

```http
POST /api/v1/alerts/rules

{
  "name": "CPU使用率告警",
  "description": "当CPU使用率超过80%时触发告警",
  "severity": "HIGH",
  "metricType": "SYSTEM_CPU_USAGE",
  "conditions": [
    {
      "conditionType": "GREATER_THAN",
      "threshold": 80.0,
      "durationSeconds": 300
    }
  ],
  "notificationChannels": ["email-channel-id"],
  "enabled": true,
  "serviceNames": ["platform-auth-service"],
  "tags": ["system", "cpu"]
}
```

### 添加服务监控仪表板

```http
POST /api/v1/dashboards/service/platform-auth-service?createdBy=admin
```

## 后续开发计划

1. 增加更多指标类型和数据源支持
2. 实现分布式链路追踪集成
3. 添加机器学习异常检测
4. 实现自动化运维响应
5. 优化大规模集群下的性能
