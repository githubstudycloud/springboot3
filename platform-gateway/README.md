# Platform Gateway

## 模块概述
`platform-gateway`模块是基于Spring Cloud Gateway实现的API网关服务，作为整个系统的统一入口，负责请求路由、负载均衡、限流熔断、权限验证等功能。该模块将外部请求正确地转发到相应的微服务，同时提供了横切关注点的统一处理能力。

## 主要功能
- API路由管理
- 请求限流和熔断
- 统一认证入口
- 跨域(CORS)支持
- 请求/响应日志记录
- 动态路由配置
- 全局异常处理

## 技术特点
- 基于Spring Cloud Gateway实现的响应式API网关
- 集成Resilience4j实现熔断和限流
- 支持动态路由配置刷新
- 自定义全局过滤器实现统一处理

## 目录结构
```
platform-gateway
├── src/main/java
│   └── com/example/platform/gateway
│       ├── GatewayApplication.java       // 应用入口
│       ├── config                        // 配置类
│       │   ├── CorsConfig.java           // 跨域配置
│       │   ├── RateLimiterConfig.java    // 限流配置
│       │   └── RouteConfig.java          // 路由配置
│       ├── controller                    // 管理控制器
│       ├── filter                        // 过滤器
│       │   ├── AuthFilter.java           // 认证过滤器
│       │   ├── LoggingFilter.java        // 日志过滤器
│       │   └── ResponseFilter.java       // 响应过滤器
│       └── handler                       // 处理器
│           └── FallbackHandler.java      // 熔断回退处理
├── src/main/resources
│   ├── application.yml                   // 应用配置
│   └── bootstrap.yml                     // 启动配置
└── pom.xml
```

## 配置说明
关键配置项说明（位于application.yml）：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service                # 路由ID
          uri: lb://platform-auth-service # 目标服务URI
          predicates:                     # 路由断言
            - Path=/auth/**               # 路径匹配
          filters:                        # 路由过滤器
            - StripPrefix=1               # 去除前缀
```

## 路由配置
网关支持以下几种路由配置方式：
1. 配置文件静态配置
2. 编程式配置（RouteLocatorBuilder）
3. 数据库动态配置（需自行实现）

## 限流配置
基于Resilience4j实现的限流机制：
```yaml
resilience4j:
  ratelimiter:
    instances:
      default:
        limitForPeriod: 100     # 单位时间内请求数限制
        limitRefreshPeriod: 1s  # 限流刷新周期
        timeoutDuration: 100ms  # 等待获取许可超时时间
```

## 启动说明
1. 确保注册中心服务已启动
2. 启动服务：
   ```bash
   mvn spring-boot:run
   ```
3. 网关默认端口：8080

## 扩展指南
1. 添加新路由时，可通过配置文件或调用管理API
2. 自定义过滤器可继承AbstractGatewayFilterFactory或GlobalFilter
3. 编写熔断处理需实现WebExceptionHandler并注册为Bean
