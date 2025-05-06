# SpringBoot 3.x 微服务平台

## 项目概述
本项目是一个基于SpringBoot 3.x和Vue 3.x的全栈微服务平台，采用DDD（领域驱动设计）和六边形架构（端口与适配器）设计原则。该平台提供了一套完整的微服务基础架构，包括服务注册与发现、配置中心、API网关、统一认证授权等核心功能。

## 技术栈
- **后端**: Java 21, SpringBoot 3.x, MyBatis+XML
- **数据库**: MySQL 5.7, MongoDB 4.2, Redis 7.x
- **消息队列**: Kafka, RabbitMQ
- **前端**: Vue 3.x
- **服务治理**: Nacos, Spring Cloud Gateway

## 项目结构
```
platform-parent (父项目)
├── platform-dependency (依赖管理)
├── platform-common (通用工具和组件)
├── platform-registry (服务注册中心，基于Nacos)
├── platform-gateway (API网关)
├── platform-auth-service (认证授权服务)
└── platform-config (配置中心)
```

## 模块说明

### platform-dependency
集中管理所有第三方依赖，统一版本控制和依赖管理，避免依赖冲突。

### platform-common
提供通用工具类、异常处理机制、响应结构等基础组件，供其他模块共享使用。

### platform-registry
基于Nacos实现的服务注册中心，负责服务注册、发现和健康检查。

### platform-gateway
API网关服务，基于Spring Cloud Gateway实现，提供路由管理、请求限流、熔断降级等功能。

### platform-auth-service
统一认证授权服务，实现基于JWT的认证机制、用户权限和角色管理、单点登录等功能。

### platform-config
配置中心服务，基于Nacos实现，提供配置集中管理、版本控制、加密存储和变更通知等功能。

## 架构设计原则

### 1. 框架与业务隔离
- 领域模型与框架无关，不继承或依赖任何框架类
- 使用抽象接口隔离基础设施实现细节
- 核心业务逻辑不包含框架API调用
- 创建防腐层保护领域模型

### 2. 领域驱动设计(DDD)实践
- 区分战略设计和战术设计
- 使用限界上下文明确划分业务边界
- 通过上下文映射定义子域间关系
- 构建统一语言确保业务术语一致性

### 3. 六边形架构(端口与适配器)
- 核心领域逻辑位于中心，与外部技术实现隔离
- 通过端口定义领域与外部世界的交互契约
- 使用适配器实现与外部系统的集成
- 区分输入适配器和输出适配器

## 开发环境要求
- Java 21
- Maven 3.8+
- MySQL 5.7+
- Redis 7.x
- Node.js 16+

## 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/yourusername/springboot3-platform.git
cd springboot3-platform
```

### 2. 构建项目
```bash
mvn clean install
```

### 3. 启动服务
按以下顺序启动服务：
1. 启动Nacos服务器（外部依赖）
2. 启动Redis服务器（外部依赖）
3. 启动MySQL服务器（外部依赖）
4. 启动注册中心服务
```bash
cd platform-registry
mvn spring-boot:run
```
5. 启动认证服务
```bash
cd platform-auth-service
mvn spring-boot:run
```
6. 启动网关服务
```bash
cd platform-gateway
mvn spring-boot:run
```
7. 启动配置中心服务
```bash
cd platform-config
mvn spring-boot:run
```

## 贡献指南
欢迎贡献代码，请遵循以下步骤：
1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建Pull Request

## 许可证
[MIT License](LICENSE)
