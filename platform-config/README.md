# Platform Config（平台配置中心）

## 模块简介

Platform Config是整个平台的配置中心，基于Spring Cloud Config实现，为所有微服务提供集中化、版本化、环境化的配置管理服务。该模块负责管理外部化配置，支持动态刷新配置，确保配置变更能够实时生效，无需重启服务。

## 主要功能

- **集中配置管理**：统一管理所有服务的配置信息
- **多环境支持**：为不同环境（开发、测试、生产）提供不同配置
- **版本控制**：基于Git的配置版本管理
- **动态刷新**：运行时更新配置，无需重启服务
- **密文配置**：支持配置加密和解密
- **高可用部署**：支持配置服务的集群化部署
- **配置审计**：记录配置变更历史
- **权限控制**：基于角色的配置访问控制

## 技术架构

配置中心基于Spring Cloud Config构建，核心组件包括：

1. **配置服务器**：提供REST API供客户端查询配置
2. **配置存储库**：Git仓库存储配置文件
3. **配置客户端**：微服务集成的Config Client
4. **消息总线**：基于消息队列的配置变更通知机制
5. **安全组件**：配置访问的认证和授权

## 目录结构

```
platform-config/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── platform/
│       │           └── config/
│       │               ├── ConfigServerApplication.java  # 应用入口
│       │               ├── config/                        # 配置类
│       │               │   ├── SecurityConfig.java        # 安全配置
│       │               │   └── GitRepositoryConfig.java   # Git仓库配置
│       │               ├── controller/                    # 控制器
│       │               ├── service/                       # 服务类
│       │               └── exception/                     # 异常处理
│       └── resources/
│           ├── application.yml          # 应用配置
│           ├── bootstrap.yml            # 引导配置
│           └── config/                  # 本地配置示例
└── pom.xml                              # Maven配置
```

## 配置存储结构

配置存储在Git仓库中，采用以下目录结构：

```
config-repo/
├── application.yml                # 所有服务共享配置
├── application-dev.yml            # 开发环境共享配置
├── application-test.yml           # 测试环境共享配置
├── application-prod.yml           # 生产环境共享配置
├── platform-gateway.yml           # 特定服务配置
├── platform-gateway-dev.yml       # 特定服务环境配置
├── platform-collect.yml
└── ...
```

## 配置优先级

配置加载遵循以下优先级（从高到低）：

1. 服务特定配置+环境特定（如platform-gateway-dev.yml）
2. 服务特定配置（如platform-gateway.yml）
3. 共享配置+环境特定（application-dev.yml）
4. 共享配置（application.yml）

## API接口

### 配置查询接口

```
GET /{application}/{profile}[/{label}]
GET /{application}-{profile}.yml
GET /{application}-{profile}.json
GET /{application}-{profile}.properties
GET /encrypt
GET /decrypt
```

### 管理接口

```
POST /monitor                      # 监控配置变更
POST /refresh                      # 刷新配置
POST /bus-refresh                  # 通过总线刷新所有服务配置
GET  /health                       # 健康检查
```

## 安全配置

配置中心实现了多层安全防护：

1. **HTTP基本认证**：用户名/密码认证
2. **OAuth2集成**：支持OAuth2授权
3. **HTTPS传输**：加密传输配置
4. **敏感配置加密**：密码等敏感信息加密存储
5. **IP限制**：可配置IP白名单

## 配置示例

### 应用配置（application.yml）

```yaml
server:
  port: 8888
spring:
  application:
    name: platform-config
  cloud:
    config:
      server:
        git:
          uri: ${CONFIG_GIT_URI:https://github.com/company/config-repo.git}
          search-paths: '{application}'
          username: ${CONFIG_GIT_USERNAME:git-user}
          password: ${CONFIG_GIT_PASSWORD:git-password}
          default-label: ${CONFIG_GIT_BRANCH:main}
          clone-on-start: true
          force-pull: true
  security:
    user:
      name: ${CONFIG_USERNAME:config-admin}
      password: ${CONFIG_PASSWORD:config-password}
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:guest}
    password: ${RABBITMQ_PASSWORD:guest}

encrypt:
  key: ${ENCRYPT_KEY:default-encryption-key}

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: when_authorized
```

### 客户端配置（bootstrap.yml示例）

```yaml
spring:
  application:
    name: platform-gateway
  cloud:
    config:
      uri: http://${CONFIG_HOST:localhost}:${CONFIG_PORT:8888}
      username: ${CONFIG_USERNAME:config-admin}
      password: ${CONFIG_PASSWORD:config-password}
      fail-fast: true
      retry:
        initial-interval: 1000
        max-interval: 2000
        max-attempts: 6
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
```

## 动态刷新配置

配置中心支持两种配置刷新方式：

1. **端点刷新**：通过调用`/refresh`端点刷新单个服务
   ```
   curl -X POST http://service-host:port/actuator/refresh
   ```

2. **总线刷新**：通过消息总线刷新所有服务
   ```
   curl -X POST http://config-server:port/actuator/bus-refresh
   ```

## 配置加密解密

敏感配置可以加密存储，配置中心提供加密解密API：

1. **加密配置**
   ```
   curl http://config-server:port/encrypt -d sensitive-value
   ```

2. **在配置文件中使用加密值**
   ```yaml
   database:
     password: '{cipher}AQBMHPVEIasdhuyAGDSadAJHYTGU83yg3nGSQAEUF'
   ```

3. **解密配置**
   ```
   curl http://config-server:port/decrypt -d encrypted-value
   ```

## 监控与告警

配置中心提供多项监控功能：

1. **配置变更监控**：记录所有配置变更
2. **访问监控**：记录配置访问日志
3. **Git仓库监控**：监控Git仓库连接状态
4. **服务健康检查**：定期检查服务健康状态
5. **配置不一致告警**：检测并告警配置不一致情况

## 高可用部署

配置中心支持高可用部署：

1. **多实例部署**：部署多个配置服务实例
2. **负载均衡**：通过负载均衡分发请求
3. **服务发现**：结合服务注册中心实现动态发现
4. **本地缓存**：客户端缓存配置，防止配置中心不可用时服务无法启动
5. **配置备份**：定期备份配置到多个存储

## 最佳实践

### 配置策略

1. **配置分层**：将配置分为全局配置、服务配置、环境配置
2. **环境隔离**：不同环境的配置严格隔离
3. **敏感信息加密**：所有敏感信息必须加密存储
4. **最小权限**：服务只能访问自己所需的配置
5. **配置审计**：所有配置变更必须经过审计

### 客户端使用

1. **优雅降级**：配置中心不可用时使用本地默认配置
2. **重试机制**：添加合理的重试策略
3. **配置验证**：服务启动时验证关键配置
4. **动态刷新**：重要服务实现配置动态刷新
5. **监听变更**：实现配置变更监听器

## 开发扩展

### 自定义环境仓库

可以通过实现`EnvironmentRepository`接口创建自定义配置源：

```java
@Component
public class CustomEnvironmentRepository implements EnvironmentRepository {
    @Override
    public Environment findOne(String application, String profile, String label) {
        // 自定义逻辑获取配置
        Environment environment = new Environment(application, profile);
        // 添加配置属性
        environment.add(new PropertySource("customSource", customProperties));
        return environment;
    }
}
```

### 自定义加密组件

可以通过实现`TextEncryptor`接口自定义加密机制：

```java
@Component
public class CustomEncryptor implements TextEncryptor {
    @Override
    public String encrypt(String text) {
        // 自定义加密逻辑
        return encryptedText;
    }
    
    @Override
    public String decrypt(String encryptedText) {
        // 自定义解密逻辑
        return decryptedText;
    }
}
```

## 故障排除

常见问题及解决方案：

1. **无法获取配置**：检查网络连接和认证信息
2. **配置不更新**：检查标签和分支设置
3. **加密解密失败**：验证加密密钥配置
4. **Git连接错误**：检查Git仓库访问权限
5. **刷新不生效**：确认客户端RefreshScope配置

## 版本历史

- **1.0.0-SNAPSHOT**：初始版本
  - 基础配置服务功能
  - Git仓库集成
  - 配置加密解密
  - 安全认证
  - 动态刷新
