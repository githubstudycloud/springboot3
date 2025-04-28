# Spring Boot 3.2.9 兼容性优化说明

本次更新针对Spring Boot 3.2.9进行了兼容性优化和代码质量改进。主要修改如下：

## 1. 合并重复工具类

- 将 `JsonUtil` 和 `JsonUtils` 合并为一个统一的工具类
- 保留 `JsonUtils` 作为过渡类，标记为 `@Deprecated`，将在下一版本移除
- 统一了JSON处理的异常处理和日志记录方式

## 2. 移除冗余代码

- 删除了重复的全局异常处理器 `com.example.framework.config.GlobalExceptionHandler`
- 统一使用 `com.example.framework.core.exception.GlobalExceptionHandler` 作为项目的异常处理器

## 3. 应用Spring Boot 3.2.9新特性

### 3.1 虚拟线程支持

- 添加了 `AsyncConfig` 配置类，支持Java 21虚拟线程
- 创建了 `application-virtualthread.yml` 配置文件，可通过配置开关启用虚拟线程功能
- 配置示例：`platform.thread.virtual.enabled=true`

### 3.2 优化的依赖注入

- 更新了服务类，使用构造器注入替代字段注入
- 创建 `ExampleService` 示例，展示Spring Boot 3.2+推荐的注入方式
- 移除了不必要的 `@Autowired` 注解，使用更简洁的代码风格

### 3.3 会话事务支持

- 添加了 `SessionConfig` 配置类，启用Spring Session的事务支持
- 配置会话存储机制，支持JDBC持久化
- 配置可通过 `platform.session.jdbc.enabled=true` 开关控制

## 4. 标准化包结构

根据领域驱动设计(DDD)原则，重构了项目包结构：

- 添加了完整的DDD分层架构：
  - `domain`: 领域层，包含实体、值对象、聚合根和领域事件
  - `application`: 应用层，包含应用服务、DTO和用例
  - `interfaces`: 接口层，包含REST控制器和其他外部接口
  - `infrastructure`: 基础设施层，包含仓储实现、消息和集成组件

- 创建了示例类，展示各层的最佳实践：
  - `UserDTO`: 应用层DTO示例
  - `UserApplicationService`: 应用层服务示例
  - `UserController`: 接口层REST控制器示例
  - `JpaRepositoryImpl`: 基础设施层仓储实现示例

- 添加了Web请求日志切面，记录所有接口调用的详细日志

## 5. 如何使用新特性

### 虚拟线程支持

在应用配置中添加：

```yaml
platform:
  thread:
    virtual:
      enabled: true
```

然后在异步方法上添加 `@Async` 注解：

```java
@Async
public CompletableFuture<String> processAsync() {
    // 方法将在虚拟线程中执行
    return CompletableFuture.completedFuture("处理完成");
}
```

### 会话事务支持

在应用配置中添加：

```yaml
platform:
  session:
    jdbc:
      enabled: true
```

这将启用会话的事务支持，确保会话操作的原子性。

### 使用DDD架构

在新项目中，按照以下包结构组织代码：

```
com.example.module
  ├── domain          // 领域层：实体、值对象、聚合根
  ├── application     // 应用层：应用服务、DTO、用例
  ├── interfaces      // 接口层：控制器、外部接口
  └── infrastructure  // 基础设施层：仓储实现、消息、集成
```

## 6. 未来计划

- 增加测试覆盖率，添加JaCoCo测试报告
- 应用更多Spring Boot 3.2.9新特性，如请求响应观测器支持
- 完善DDD架构示例，添加更多最佳实践示例