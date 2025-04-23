根据您的项目结构和最佳实践，我为您的平台微服务项目创建了一份详细的项目提示，帮助AI更好地理解和协助您的开发工作：

# 平台微服务架构项目提示

## 项目概述
```
项目名称: Platform Parent
项目路径: C:\Users\John\Documents\IdeaProject\platform-parent\platform-parent
项目类型: 微服务架构平台
开发语言: Java
主要框架: Spring Boot, Spring Cloud
```

## 项目架构
本项目基于微服务架构设计，由以下主要组件构成：

### 核心服务
- **platform-common**: 共享组件库，提供所有服务共用的工具类和模型
- **platform-collect**: 数据采集服务，负责从多种来源收集数据
- **platform-fluxcore**: 核心数据处理引擎，实现数据流处理和业务规则
- **platform-buss-dashboard**: 业务仪表盘，提供业务指标可视化
- **platform-monitor-dashboard**: 系统监控仪表盘，提供系统性能监控

### 基础设施服务
- **platform-gateway**: API网关，处理路由、负载均衡和安全控制
- **platform-registry**: 服务注册中心，基于Eureka实现服务发现
- **platform-config**: 配置中心，统一管理所有服务配置
- **platform-scheduler**: 调度服务集群，包含以下组件：
    - **platform-scheduler**: 调度引擎核心
    - **platform-scheduler-register**: 任务注册服务
    - **platform-scheduler-query**: 任务查询服务

## 项目目录结构
```
platform-parent/
├── docs/                           # 项目文档
│   ├── architecture-overview.md    # 架构概述
│   ├── design-decisions.md         # 设计决策说明
│   ├── project-structure.md        # 项目结构文档
│   └── scheduler-design.md         # 调度器设计文档
├── platform-common/                # 公共组件库
├── platform-collect/               # 数据采集服务
├── platform-fluxcore/              # 核心处理引擎
├── platform-buss-dashboard/        # 业务仪表盘
├── platform-monitor-dashboard/     # 监控仪表盘
├── platform-gateway/               # API网关
├── platform-registry/              # 服务注册中心
├── platform-config/                # 配置中心
├── platform-scheduler/             # 调度引擎
├── platform-scheduler-register/    # 任务注册服务
├── platform-scheduler-query/       # 任务查询服务
├── docker/                         # Docker配置文件
├── kubernetes/                     # Kubernetes部署配置
└── pom.xml                         # 父POM文件
```

## 开发规范

### 代码风格
- **类名**: 使用PascalCase (例如: `UserService`)
- **方法/变量**: 使用camelCase (例如: `getUserById`)
- **常量**: 使用UPPER_SNAKE_CASE (例如: `MAX_RETRY_COUNT`)
- **包名**: 全小写，点分隔 (例如: `com.platform.common.utils`)
- **缩进**: 4个空格
- **行宽**: 最大120个字符
- **括号风格**:
  ```java
  if (condition) {
      // 代码
  } else {
      // 代码
  }
  ```

### 类结构规范
- 每个类应当有清晰的单一职责
- 类注释应描述类的用途和职责
- 公共方法需要JavaDoc注释
- 遵循SOLID原则，特别是单一职责原则和依赖倒置原则

### 包结构规范
每个微服务模块应遵循以下包结构：
```
com.platform.[服务名]/
├── config/        # 配置类
├── controller/    # 控制器类
├── service/       # 服务接口与实现
├── repository/    # 数据访问层
├── model/         # 数据模型
│   ├── entity/    # 实体类
│   └── dto/       # 数据传输对象
├── exception/     # 自定义异常
└── util/          # 工具类
```

## 核心技术规范

### 微服务通信
- **同步通信**: 使用REST API或OpenFeign
- **异步通信**: 使用消息队列(RabbitMQ/Kafka)
- **API网关**: 所有外部请求通过Gateway路由

### 服务注册与发现
- 使用Eureka服务注册中心
- 服务实例应配置健康检查
- 服务应实现优雅下线

### 配置管理
- 使用Spring Cloud Config集中管理配置
- 环境特定配置使用profiles区分
- 敏感配置信息加密存储

### 调度系统
- 任务调度基于Quartz实现
- 调度任务注册与执行分离
- 实现任务失败重试和补偿机制

### 数据存储
- 每个服务独立管理自己的数据库
- 使用JPA/MyBatis进行数据访问
- 实现数据库分片和读写分离

### 安全规范
- 使用JWT进行认证授权
- API接口权限控制
- 敏感数据加密

### 监控与日志
- 集中式日志收集(ELK)
- 服务监控指标(Prometheus + Grafana)
- 分布式链路追踪(Zipkin/Sleuth)

## 设计模式和最佳实践

### 微服务设计模式
- **API网关模式**: 统一入口处理路由、认证和限流
- **断路器模式**: 防止服务级联失败
- **SAGA模式**: 分布式事务管理
- **CQRS模式**: 读写分离，提高性能
- **事件溯源模式**: 基于事件的状态管理
- **服务发现模式**: 动态定位服务实例

### DevOps最佳实践
- **持续集成/持续部署**: 通过CI/CD流水线自动化构建与部署
- **基础设施即代码**: 使用Docker和Kubernetes实现容器化部署
- **自动化测试**: 单元测试、集成测试和端到端测试
- **金丝雀发布**: 逐步部署新版本，降低风险

## 变更管理流程

### 变更类型分类
1. **功能新增**: 新增功能或特性
2. **缺陷修复**: 修复现有功能问题
3. **重构优化**: 改进代码结构与质量
4. **性能优化**: 提升系统性能
5. **文档更新**: 更新技术文档

### 变更记录格式
对于每次重要变更，应在`docs/changes/`目录下创建一个变更记录文件：
- 文件命名: `YYYY-MM-DD-简要描述.md`
- 内容格式:
```markdown
# [变更标题]

## 基本信息
- **类型**: [功能新增/缺陷修复/重构优化/性能优化/文档更新]
- **日期**: YYYY-MM-DD
- **作者**: [姓名]
- **影响范围**: [受影响的服务/模块]

## 变更内容
[详细描述变更内容，包括目的和实现方式]

## 技术细节
[实现的技术细节，包括关键代码修改]

## 测试方法
[验证此变更的测试方法]

## 回滚方案
[如需回滚此变更的操作步骤]
```

## 与Claude协作指南

### 请求帮助的格式
当向Claude请求协助时，请提供以下信息：
```
我正在处理 [功能/问题描述]。
当前文件: [文件路径]
我需要 [具体需求]。

相关代码:
```java
// 粘贴相关代码
```

需要注意的点：[特别说明]
```

### 常见任务请求示例

1. **代码审查**:
```
请对以下代码进行审查，检查是否有潜在问题、性能优化空间或不符合项目编码规范的地方。

当前文件: platform-common/src/main/java/com/platform/common/utils/ResourceMonitorUtil.java

代码:
```java
// 粘贴代码
```
```

2. **功能实现**:
```
我需要在platform-collect服务中实现一个新的数据源连接器，用于从MongoDB收集数据。

需要实现以下功能:
1. 连接到MongoDB数据库
2. 根据配置的查询条件获取数据
3. 将数据转换为标准格式

当前目录: platform-collect/src/main/java/com/platform/collect/connector/
```

3. **问题诊断**:
```
我在platform-scheduler服务中遇到了以下异常，请帮我分析可能的原因和解决方案：

异常堆栈:
```
// 粘贴异常堆栈
```

相关代码:
```java
// 粘贴相关代码
```
```

4. **架构设计咨询**:
```
我正在考虑如何优化platform-fluxcore的数据处理流程，以提高大数据量处理的性能。
目前的实现是:
[描述当前实现]

我考虑使用以下方案:
[描述新方案]

请分析这两种方案的优缺点，并提供建议。
```

### Claude响应指南
Claude在协助开发时应:
1. 遵循项目的代码风格和命名约定
2. 应用项目中采用的架构和设计模式
3. 提供清晰的解释和必要的注释
4. 考虑性能、可维护性和安全性
5. 在提供解决方案时，考虑与现有系统的整合

## 常见问题 (FAQ)

1. **如何启动本地开发环境?**
   - 首先启动基础设施服务: registry, config
   - 然后启动核心业务服务
   - 最后启动gateway

2. **如何添加新的微服务模块?**
   - 在根目录创建新的模块目录
   - 添加合适的pom.xml配置
   - 在父pom.xml中添加模块引用
   - 实现服务注册和配置读取

3. **服务间如何通信?**
   - 同步通信使用Feign客户端
   - 异步通信使用消息队列
   - 事件驱动场景使用事件总线

4. **如何处理分布式事务?**
   - 优先考虑最终一致性方案
   - 对于跨服务事务，使用SAGA模式
   - 实现补偿事务确保数据一致性

5. **如何进行服务监控?**
   - 每个服务暴露Actuator端点
   - 使用Prometheus收集指标
   - 通过Grafana可视化监控数据

---

希望这份项目提示能够帮助Claude更好地理解您的平台微服务架构，提供更加精准和有价值的协助。您可以根据项目的实际情况对这份提示进行调整和扩展。