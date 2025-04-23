# 平台微服务架构

## 概述

本项目实现了一个全面的基于微服务的平台，用于分布式数据收集、处理、监控和管理。该架构设计具有可扩展性、弹性和可维护性，重点关注性能和可靠性。

## 架构

该平台基于微服务架构构建，具有以下关键组件：

### 核心服务

- **platform-collect**: 数据摄取和收集服务
- **platform-fluxcore**: 核心数据处理引擎
- **platform-buss-dashboard**: 业务监控和可视化仪表板
- **platform-monitor-dashboard**: 系统健康和性能监控仪表板
- **platform-gateway**: 用于路由和横切关注点的API网关
- **platform-scheduler**: 任务调度和执行服务
- **platform-scheduler-register**: 任务注册和管理服务
- **platform-scheduler-query**: 任务状态和历史查询服务

### 基础设施服务

- **platform-config**: 集中式配置管理
- **platform-registry**: 服务发现和注册
- **platform-common**: 共享工具和组件

### 支持系统

- 日志和审计系统
- 消息队列
- 分布式事务管理
- 身份验证和授权中心
- 告警系统
- 数据存储和缓存
- CI/CD容器化
- 模块通信
- 网关金丝雀部署和回滚
- 文档和API管理

## 技术栈

- **Java 11**: 核心编程语言
- **Spring Boot**: 微服务框架
- **Spring Cloud**: 微服务协调
  - Netflix Eureka: 服务发现
  - Spring Cloud Gateway: API网关
  - Spring Cloud Config: 集中配置
  - Spring Cloud OpenFeign: 声明式REST客户端
  - Spring Cloud Circuit Breaker: 弹性模式
- **数据库**:
  - MySQL: 关系型数据存储
  - Redis: 缓存和速率限制
  - Elasticsearch: 搜索和分析
- **消息**:
  - RabbitMQ: 用于异步通信的消息代理
- **监控**:
  - Prometheus: 指标收集和存储
  - Grafana: 指标可视化
  - Zipkin: 分布式追踪
  - Spring Boot Admin: 应用监控和管理
- **部署**:
  - Docker: 容器化
  - Kubernetes: 容器编排

## 项目结构

项目遵循多模块Maven项目结构。详细信息请参见[项目结构](docs/project-structure.md)。

## 快速开始

### 先决条件

- JDK 11或更高版本
- Maven 3.6.0或更高版本
- Docker和Docker Compose
- IDE（推荐IntelliJ IDEA）

### 构建项目

```bash
# 克隆仓库
git clone https://github.com/yourusername/platform-parent.git
cd platform-parent

# 构建项目
mvn clean install
```

### 使用Docker Compose在本地运行

```bash
# 启动所有服务
cd docker
docker-compose up -d

# 检查服务状态
docker-compose ps

# 查看日志
docker-compose logs -f [service-name]

# 停止所有服务
docker-compose down
```

### 服务访问

- **Eureka仪表板**: http://localhost:8761
- **Spring Boot Admin**: http://localhost:8761/admin
- **API网关**: http://localhost:8080
- **Grafana**: http://localhost:3000
- **Prometheus**: http://localhost:9090
- **Zipkin**: http://localhost:9411
- **RabbitMQ管理界面**: http://localhost:15672

## 开发指南

### 代码风格

项目遵循标准Java代码风格，并有以下附加指南：

- 使用lombok减少样板代码
- 遵循微服务设计模式
- 使用适当的异常处理和自定义异常
- 使用Javadoc记录公共API
- 编写全面的单元和集成测试

### 分支策略

- `main`: 生产就绪代码
- `develop`: 开发集成分支
- `feature/*`: 功能开发分支
- `release/*`: 发布准备分支
- `hotfix/*`: 生产问题热修复分支

### CI/CD流水线

项目使用CI/CD流水线进行自动化测试、构建和部署：

1. **构建和测试**: 编译代码并运行测试
2. **质量分析**: 静态代码分析
3. **Docker构建**: 构建Docker镜像
4. **部署到测试环境**: 部署到测试环境
5. **集成测试**: 运行集成测试
6. **部署到预发环境**: 部署到预发环境
7. **用户验收测试**: 运行UAT
8. **部署到生产环境**: 部署到生产环境

## 监控和运维

### 健康检查

每个服务通过Spring Boot Actuator暴露健康检查端点：

```
GET /actuator/health
```

### 指标

指标由Prometheus收集并在Grafana仪表板中可视化：

- JVM指标（内存、CPU、GC）
- HTTP请求指标（计数、延迟、错误）
- 业务指标（特定于每个服务）

### 日志

日志集中化，可以通过Kibana仪表板或直接从Docker日志访问。

## 主要特性

- **可扩展性**: 服务的水平扩展
- **弹性**: 断路器、重试和超时
- **可观察性**: 全面的日志记录、指标和追踪
- **安全性**: 身份验证、授权和安全通信
- **性能**: 缓存、异步处理和优化的数据访问
- **可维护性**: 模块化设计和关注点明确分离

## 文档

- [架构概述](docs/architecture-overview.md)
- [项目结构](docs/project-structure.md)
- [调度器设计](docs/scheduler-design.md)
- [API文档](docs/api-documentation.md)
- [部署指南](docs/deployment-guide.md)
- [监控指南](docs/monitoring-guide.md)

## 贡献

1. Fork仓库
2. 创建功能分支: `git checkout -b feature/my-new-feature`
3. 提交更改: `git commit -am '添加某功能'`
4. 推送到分支: `git push origin feature/my-new-feature`
5. 提交拉取请求

## 许可证

本项目根据MIT许可证授权 - 详情请参阅LICENSE文件。
