# Platform Registry（平台注册中心）

## 模块简介

Platform Registry是平台的服务注册与发现中心，同时集成了Spring Boot Admin提供服务监控和管理功能。该模块基于Netflix Eureka实现，使各个微服务能够注册自己并发现其他服务，从而实现服务间的通信。

## 主要功能

- **服务注册**：允许微服务在启动时将自己注册到注册中心
- **服务发现**：允许微服务查询和发现其他可用的服务实例
- **负载均衡**：支持客户端负载均衡，分配请求到多个服务实例
- **健康检查**：监控服务实例的健康状态
- **服务监控**：通过Spring Boot Admin提供的监控界面查看服务状态
- **配置管理**：查看和修改服务配置

## 目录结构

```
platform-registry/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── platform/
│       │           └── registry/
│       │               ├── config/                # 配置类
│       │               │   └── SecurityConfig.java        # 安全配置
│       │               └── RegistryApplication.java       # 应用入口类
│       └── resources/
│           └── application.yml        # 应用配置文件
└── pom.xml                            # Maven配置文件
```

## 组件说明

### Eureka Server

Netflix Eureka是一个RESTful服务，主要用于AWS云中定位服务，以实现中间层服务器的负载均衡和故障转移。在本平台中，Eureka用于：

- 服务注册：服务启动时向Eureka Server注册
- 服务续约：服务定期发送心跳以保持注册状态
- 服务下线：服务关闭时从Eureka Server注销
- 服务清理：Eureka Server定期清理已下线的服务

### Spring Boot Admin

Spring Boot Admin是一个用于管理和监控Spring Boot应用程序的Web应用程序。在本平台中，Spring Boot Admin用于：

- 服务健康状态监控
- 详细的健康信息和指标
- 环境配置查看和修改
- 日志级别调整
- JVM和线程状态监控
- HTTP跟踪

## 配置说明

主要配置参数说明：

```yaml
eureka:
  instance:
    hostname: localhost                    # Eureka服务器的主机名
    prefer-ip-address: false               # 是否使用IP地址而不是主机名
    lease-renewal-interval-in-seconds: 10  # 实例续约间隔（秒）
    lease-expiration-duration-in-seconds: 30 # 实例过期时间（秒）
  server:
    enable-self-preservation: false        # 是否启用自我保护模式
    eviction-interval-timer-in-ms: 5000    # 清理间隔时间（毫秒）
    response-cache-update-interval-ms: 5000 # 响应缓存更新间隔（毫秒）
  client:
    register-with-eureka: false            # 是否向Eureka注册自己
    fetch-registry: false                  # 是否从Eureka获取注册信息
```

## 安全设置

注册中心配置了基本的HTTP身份验证，保护Eureka仪表板和Spring Boot Admin界面：

- 默认用户名：`admin`（可通过环境变量`REGISTRY_USERNAME`设置）
- 默认密码：`admin`（可通过环境变量`REGISTRY_PASSWORD`设置）

在生产环境中，强烈建议修改默认凭据并使用更强的密码。

## 使用指南

### 访问Eureka仪表板

Eureka仪表板提供了所有注册服务的可视化视图，可通过以下URL访问：

```
http://<hostname>:8761/
```

需要使用配置的用户名和密码进行认证。

### 访问Spring Boot Admin

Spring Boot Admin提供了所有服务的详细监控信息，可通过以下URL访问：

```
http://<hostname>:8761/admin
```

需要使用与Eureka相同的用户名和密码进行认证。

### 服务注册配置

其他微服务要向注册中心注册，需在其`application.yml`中添加以下配置：

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://${REGISTRY_USERNAME:admin}:${REGISTRY_PASSWORD:admin}@${REGISTRY_HOST:localhost}:${REGISTRY_PORT:8761}/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${random.uuid}
```

## 高可用部署

在生产环境中，建议部署多个注册中心实例以实现高可用性：

1. 配置多个注册中心实例，每个实例指向其他实例
2. 在每个微服务中配置所有注册中心实例的地址
3. 使用负载均衡器分发注册请求

示例配置（双节点集群）：

```yaml
# 注册中心1
eureka:
  client:
    service-url:
      defaultZone: http://user:password@registry2:8761/eureka/

# 注册中心2
eureka:
  client:
    service-url:
      defaultZone: http://user:password@registry1:8761/eureka/
```

## 监控与告警

注册中心自身的健康状态监控：

- 通过Actuator `/actuator/health`端点监控
- 配置Prometheus抓取指标
- 设置Grafana仪表板显示关键指标
- 配置告警阈值：实例数下降、高错误率、高延迟

## 故障排除

常见问题及解决方案：

1. **服务无法注册**：检查网络连接和认证信息
2. **实例状态显示为DOWN**：检查服务健康检查配置
3. **注册信息过期**：调整续约间隔和过期时间
4. **注册中心响应慢**：检查JVM内存配置和实例数量
5. **服务显示为UNKNOWN**：检查健康检查实现和超时设置

## 版本历史

- **1.0.0-SNAPSHOT**：初始版本
  - Eureka服务注册与发现
  - Spring Boot Admin集成
  - 基本安全认证
  - 健康检查监控
