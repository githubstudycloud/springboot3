# Platform Config

## 模块概述
`platform-config`模块是基于Nacos实现的配置中心，负责系统配置的集中管理、版本控制、加密存储和变更通知。该模块提供了多环境配置管理、配置加密解密、配置版本回滚等核心功能，是整个微服务架构的中枢神经系统。

## 主要功能

### 配置管理
- 多环境配置支持（开发、测试、生产等）
- 配置项的创建、更新、删除、查询
- 配置分组管理
- 配置搜索功能

### 配置版本控制
- 配置变更历史记录
- 配置版本回滚
- 版本对比功能
- 配置变更审计

### 配置加密
- 敏感配置自动加密存储
- 解密展示功能
- 加密密钥管理
- 动态加密策略

### 变更通知
- 配置变更事件发布
- 实时变更推送机制
- 变更历史查询
- 操作审计日志

## 技术特点
- 基于Nacos实现配置管理核心功能
- 采用DDD(领域驱动设计)和六边形架构
- 完全分离领域逻辑与技术实现
- 支持配置数据的持久化和版本控制
- 实现了配置加密和解密能力

## 目录结构
```
platform-config
├── src/main/java
│   └── com/example/platform/config
│       ├── ConfigApplication.java             // 应用入口
│       ├── application                        // 应用层
│       │   ├── dto                            // 数据传输对象
│       │   └── service                        // 应用服务
│       ├── domain                             // 领域层
│       │   ├── model                          // 领域模型
│       │   ├── repository                     // 仓储接口
│       │   └── service                        // 领域服务
│       ├── infrastructure                     // 基础设施层
│       │   ├── config                         // 配置类
│       │   ├── mapper                         // 对象映射器
│       │   └── persistence                    // 持久化实现
│       │       ├── entity                     // 数据库实体
│       │       └── repository                 // 仓储实现
│       └── interfaces                         // 接口层
│           ├── controller                     // REST控制器
│           └── handler                        // 异常处理器
└── src/main/resources
    ├── application.yml                        // 应用配置
    └── bootstrap.yml                          // 启动配置
```

## API接口

### 配置管理
- `POST /api/config` - 创建配置
- `PUT /api/config/{dataId}/{group}/{environment}` - 更新配置
- `DELETE /api/config/{dataId}/{group}/{environment}` - 删除配置
- `GET /api/config/{dataId}/{group}/{environment}` - 获取配置
- `GET /api/config/group/{group}/{environment}` - 获取分组下的配置
- `GET /api/config/environment/{environment}` - 获取环境下的配置
- `GET /api/config/search` - 搜索配置

### 版本管理
- `GET /api/config/{dataId}/{group}/{environment}/versions` - 获取配置版本历史
- `POST /api/config/{dataId}/{group}/{environment}/rollback` - 回滚配置

### 变更事件
- `GET /api/config/events/recent` - 获取最近的变更事件
- `GET /api/config/{dataId}/{group}/{environment}/events` - 获取配置的变更事件

## 配置示例

```yaml
spring:
  application:
    name: platform-config
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER:127.0.0.1:8848}
        namespace: ${NACOS_NAMESPACE:public}
      config:
        server-addr: ${NACOS_SERVER:127.0.0.1:8848}
        namespace: ${NACOS_NAMESPACE:public}
        file-extension: yaml
```

## 使用方式
其他微服务通过以下步骤接入配置中心：

1. 添加Nacos配置依赖
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

2. 创建bootstrap.yml配置
```yaml
spring:
  application:
    name: your-service-name
  cloud:
    nacos:
      config:
        server-addr: ${NACOS_SERVER:127.0.0.1:8848}
        namespace: ${NACOS_NAMESPACE:public}
        file-extension: yaml
```

3. 使用@Value或@ConfigurationProperties注入配置
```java
@Value("${your.config.property}")
private String configProperty;
```

4. 使用@RefreshScope实现配置热更新
```java
@RefreshScope
@Component
public class YourComponent {
    // 配置属性会在配置更新时自动刷新
}
```
