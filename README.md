# V6平台 - 基于Spring Boot 3.x的现代化微服务平台

## 项目介绍

V6平台是基于Spring Boot 3.x和Spring Cloud的现代化微服务平台，采用超模块化微服务架构，融合了六边形架构、CQRS、事件驱动和响应式编程等先进模式，旨在提供高效、智能、安全、可扩展的技术基础设施。

## 核心特性

- **超模块化微服务架构**：更细粒度的服务划分，提升灵活性和可维护性
- **六边形架构**：严格分离业务逻辑与技术实现
- **事件驱动模型**：增强的事件处理和异步通信
- **响应式编程**：与JDK 21虚拟线程结合提升并发性能
- **零信任安全框架**：深化零信任模型，构建全方位安全防护
- **可持续软件设计**：优化资源使用和能源效率

## 项目结构

```
platform-parent/
├── platform-dependencies/  # 依赖管理
├── platform-common/        # 通用工具和模型
├── platform-framework/     # 核心框架抽象
├── platform-gateway/       # API网关（计划中）
├── platform-registry/      # 服务注册与发现（计划中）
├── platform-config/        # 配置中心（计划中）
└── platform-security/      # 安全中心（计划中）
```

## 技术栈

- Java 21 LTS
- Spring Boot 3.2.x
- Spring Cloud 2023.x
- Spring WebFlux / Project Reactor

## 开发指南

### 环境要求

- JDK 21+
- Maven 3.9+
- IDE（推荐IntelliJ IDEA）

### 构建项目

```bash
# 克隆项目
git clone https://github.com/your-org/platform-v6.git

# 进入项目目录
cd platform-v6

# 构建项目
mvn clean install
```

### 创建新服务

1. 新服务应遵循六边形架构，推荐的目录结构如下：

```
service-name/
├── src/main/java/com/example/service/
│   ├── application/        # 应用服务层（用例实现）
│   ├── domain/             # 领域层（领域模型、服务、事件）
│   ├── infrastructure/     # 基础设施层（适配器实现）
│   │   ├── config/         # 配置类
│   │   ├── repository/     # 仓库实现
│   │   ├── messaging/      # 消息实现
│   │   └── external/       # 外部服务适配器
│   └── interfaces/         # 接口层
│       ├── api/            # REST API控制器
│       ├── dto/            # 数据传输对象
│       └── listener/       # 消息监听器
├── src/main/resources/     # 配置文件
└── src/test/              # 测试代码
```

2. 在服务pom.xml中引入平台依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>platform-dependencies</artifactId>
    <version>${platform.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<dependency>
    <groupId>com.example</groupId>
    <artifactId>platform-framework</artifactId>
    <version>${platform.version}</version>
</dependency>
```

## 最佳实践

- 遵循领域驱动设计(DDD)原则
- 使用事件驱动模式实现服务间通信
- 采用CQRS模式分离读写操作
- 遵循单一职责原则(SRP)
- 使用虚拟线程和响应式编程提高并发性能

## 贡献指南

欢迎贡献代码，提交问题或改进建议。请遵循以下步骤：

1. Fork项目
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交变更 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建Pull Request

## 版本历史

- **1.0.0-SNAPSHOT** - 初始版本，基础功能实现

## 许可证

本项目采用 [MIT 许可证](LICENSE)。 