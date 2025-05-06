# Platform Scheduler Register Module

## 概述

Platform Scheduler Register Module 是平台调度系统的任务注册模块，提供任务注册、配置管理、任务模板和依赖关系管理功能。该模块基于领域驱动设计(DDD)和六边形架构原则实现，确保核心业务逻辑与技术实现分离。

## 核心功能

- **任务模板管理**：创建、更新和管理任务模板，支持模板分类和标签管理
- **任务版本控制**：提供任务版本管理，支持版本历史记录、比较和回滚
- **依赖关系管理**：定义和管理任务间的依赖关系，支持多种依赖类型和条件表达式
- **任务配置验证**：提供任务参数和依赖关系的验证功能，确保任务定义的正确性
- **与核心调度系统集成**：与platform-scheduler核心模块集成，提供完整的任务管理和调度功能

## 架构设计

模块遵循六边形架构（端口与适配器）设计原则，分为四层：

```
┌─────────────────────────────────────────────────────────────────┐
│                       接口层 (Interfaces)                        │
│                                                                  │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐     │
│  │     REST API    │  │   GraphQL API  │  │  Event Handlers │     │
│  └────────────────┘  └────────────────┘  └────────────────┘     │
└───────────────────────────────┬─────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────┐
│                        应用层 (Application)                      │
│                                                                  │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐     │
│  │ 模板应用服务   │  │ 版本应用服务   │  │ 依赖应用服务   │     │
│  └────────────────┘  └────────────────┘  └────────────────┘     │
└───────────────────────────────┬─────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────┐
│                        领域层 (Domain)                           │
│                                                                  │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │
│  │  任务模板  │  │  任务版本  │  │ 依赖定义   │  │ 领域服务   │ │
│  └────────────┘  └────────────┘  └────────────┘  └────────────┘ │
└───────────────────────────────┬─────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────┐
│                     基础设施层 (Infrastructure)                  │
│                                                                  │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │
│  │   仓储实现  │  │ 外部集成   │  │ 配置管理   │  │  事件发布  │ │
│  └────────────┘  └────────────┘  └────────────┘  └────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 1. 领域层 (Domain)

包含核心业务模型和规则：

- **任务模板 (JobTemplate)**：定义了任务的模板和配置
- **任务版本 (JobVersion)**：管理任务定义的版本历史
- **依赖定义 (DependencyDefinition)**：表示任务间的依赖关系
- **领域服务**：实现复杂的业务逻辑和规则验证

### 2. 应用层 (Application)

协调领域对象以完成用例：

- **模板应用服务**：管理任务模板生命周期
- **版本应用服务**：处理版本管理和回滚操作
- **依赖应用服务**：管理依赖关系和验证

### 3. 基础设施层 (Infrastructure)

提供技术实现：

- **仓储实现**：基于JPA的持久化实现
- **外部集成**：与核心调度系统的集成
- **事件发布**：实现领域事件的发布和处理

### 4. 接口层 (Interfaces)

提供与外部系统交互的接口：

- **REST API**：提供HTTP接口
- **GraphQL API**：提供灵活的查询接口
- **事件处理器**：处理来自其他模块的事件

## API接口

### 模板管理API

- `POST /api/v1/templates` - 创建任务模板
- `GET /api/v1/templates` - 获取模板列表
- `GET /api/v1/templates/{id}` - 获取模板详情
- `PUT /api/v1/templates/{id}` - 更新模板
- `DELETE /api/v1/templates/{id}` - 删除模板
- `POST /api/v1/templates/{id}/publish` - 发布模板
- `POST /api/v1/templates/{id}/disable` - 禁用模板
- `POST /api/v1/templates/{id}/parameters` - 添加参数
- `DELETE /api/v1/templates/{id}/parameters/{paramName}` - 删除参数
- `POST /api/v1/templates/{id}/copy` - 复制模板

### 版本管理API

- `POST /api/v1/jobs/{jobId}/versions` - 创建任务版本
- `GET /api/v1/jobs/{jobId}/versions` - 获取版本列表
- `GET /api/v1/versions/{id}` - 获取版本详情
- `POST /api/v1/versions/{id}/rollback` - 回滚到指定版本
- `GET /api/v1/versions/compare` - 比较两个版本
- `PUT /api/v1/versions/{id}/comment` - 更新版本备注

### 依赖管理API

- `POST /api/v1/dependencies` - 创建依赖关系
- `GET /api/v1/dependencies` - 获取依赖关系列表
- `GET /api/v1/dependencies/{id}` - 获取依赖关系详情
- `PUT /api/v1/dependencies/{id}` - 更新依赖关系
- `DELETE /api/v1/dependencies/{id}` - 删除依赖关系
- `POST /api/v1/dependencies/{id}/enable` - 启用依赖关系
- `POST /api/v1/dependencies/{id}/disable` - 禁用依赖关系
- `GET /api/v1/jobs/{jobId}/dependencies/graph` - 获取依赖关系图

## 配置项

主要配置项位于`application.yml`文件中：

```yaml
platform:
  scheduler:
    register:
      # 模板配置
      template:
        default-category: DATA_PROCESSING
        max-labels-per-template: 20
        builtin-templates-location: classpath:templates/
      
      # 版本配置
      version:
        max-versions-per-job: 50
        auto-cleanup-enabled: true
        auto-cleanup-keep-versions: 10
      
      # 依赖配置
      dependency:
        max-dependency-depth: 5
        cycle-detection-enabled: true
      
      # 安全配置
      security:
        enabled: true
        admin-roles: ROLE_ADMIN,ROLE_SCHEDULER_ADMIN
        user-roles: ROLE_USER,ROLE_SCHEDULER_USER
```

## 与核心调度系统集成

本模块与platform-scheduler核心模块紧密集成，通过以下方式进行交互：

1. **领域事件通信**：通过发布和订阅领域事件实现模块间的松耦合通信
2. **共享领域模型**：共享关键领域模型，如Job和TaskInstance
3. **服务调用**：为特定操作提供服务接口
4. **数据同步**：确保任务定义和配置在两个模块间保持一致

## 数据库设计

模块使用以下主要数据库表：

- `job_template` - 存储任务模板信息
- `job_parameter_template` - 存储参数模板信息
- `job_template_label` - 存储模板标签
- `job_version` - 存储任务版本信息
- `job_version_parameter` - 存储版本参数
- `job_version_dependency` - 存储版本依赖关系
- `dependency_definition` - 存储依赖关系定义

表结构通过Flyway管理，位于`src/main/resources/db/migration`目录。

## 开发指南

### 添加新的模板类型

要添加新的模板类型，请按以下步骤操作：

1. 在`JobType`枚举中添加新类型
2. 在`template`目录下创建对应的模板JSON文件
3. 在`JobTemplateFactory`中添加创建逻辑
4. 更新UI以支持新的模板类型

### 自定义依赖条件表达式

条件表达式使用SpEL(Spring Expression Language)，可以访问以下上下文变量：

- `jobResult` - 依赖任务的执行结果
- `jobStatus` - 依赖任务的状态
- `params` - 当前任务的参数
- `varMap` - 任务执行环境变量

表达式示例：
- `jobStatus == 'COMPLETED' && jobResult.contains("SUCCESS")`
- `params.dataDate > jobResult`

## 测试策略

模块测试分为以下几层：

1. **单元测试**：测试领域模型和服务，位于`src/test/java`目录
2. **集成测试**：测试仓储和外部服务集成，使用嵌入式数据库
3. **API测试**：测试REST接口，使用MockMvc和TestContainers
4. **端到端测试**：测试完整业务流程，需要启动完整环境

## 部署指南

### 单独部署

1. 编译打包：`mvn clean package`
2. 运行：`java -jar target/platform-scheduler-register-1.0.0-SNAPSHOT.jar`

### Docker部署

```bash
# 构建镜像
docker build -t platform-scheduler-register .

# 运行容器
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod platform-scheduler-register
```

### Kubernetes部署

配置文件位于`k8s`目录，使用kubectl部署：

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## 监控与运维

- 通过Spring Boot Actuator提供健康检查和指标
- 支持Prometheus指标收集
- 提供基于Grafana的仪表盘模板
- 日志采用结构化格式，支持ELK收集分析

## 未来计划

- 支持任务模板市场
- 实现基于AI的依赖关系推荐
- 提供任务依赖关系可视化编辑器
- 增强任务版本比较功能
- 与平台工作流引擎集成

## 许可证

本模块是平台项目的一部分，使用与平台相同的许可证。

## 贡献指南

欢迎提交问题报告和功能请求。如果您想贡献代码，请按照以下步骤操作：

1. Fork本仓库
2. 创建功能分支
3. 提交更改
4. 创建Pull Request

请确保您的代码符合项目的编码规范，并添加适当的测试。
