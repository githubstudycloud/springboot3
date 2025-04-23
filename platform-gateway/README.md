# Platform Gateway（平台网关）

## 模块简介

Platform Gateway是整个平台的统一入口，负责路由所有客户端请求到相应的微服务。网关实现了许多横切关注点，如认证、授权、限流、日志记录和监控，避免在每个微服务中重复实现这些功能。

## 主要功能

- **请求路由**：基于路径的动态路由到后端服务
- **负载均衡**：客户端负载均衡
- **认证与授权**：验证用户身份和访问权限
- **速率限制**：防止API滥用和DoS攻击
- **请求/响应转换**：修改请求和响应内容
- **断路器**：防止级联故障
- **日志与监控**：请求日志记录和性能监控
- **灰度发布**：支持金丝雀发布和流量控制

## 目录结构

```
platform-gateway/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── platform/
│       │           └── gateway/
│       │               ├── config/                # 配置类
│       │               │   ├── RouteConfig.java           # 路由配置
│       │               │   └── RateLimiterConfig.java     # 限流配置
│       │               ├── controller/            # 控制器
│       │               │   └── FallbackController.java    # 服务降级控制器
│       │               ├── filter/                # 网关过滤器
│       │               │   └── GlobalRequestFilter.java   # 全局请求过滤器
│       │               └── GatewayApplication.java        # 应用入口类
│       └── resources/
│           └── application.yml        # 应用配置文件
└── pom.xml                            # Maven配置文件
```

## 路由配置

网关将请求路由到不同的微服务，路由配置在`RouteConfig.java`中定义：

```
/api/dashboard/**   -> platform-buss-dashboard
/api/monitor/**     -> platform-monitor-dashboard
/api/collect/**     -> platform-collect
/api/flux/**        -> platform-fluxcore
/api/scheduler/**   -> platform-scheduler
```

## 限流配置

为防止API滥用，网关实现了基于Redis的分布式限流：

- **默认限流**：匿名用户每秒50个请求，突发可达100个
- **认证用户限流**：认证用户每秒100个请求，突发可达200个
- **管理员限流**：管理员用户每秒200个请求，突发可达500个

## 断路器配置

断路器用于防止故障级联传播，主要配置：

- **失败率阈值**：50%
- **慢调用率阈值**：50%
- **半开状态允许调用次数**：5
- **开路状态等待时间**：10秒
- **慢调用时间阈值**：2秒

## 重试机制

网关实现了请求重试功能，对特定HTTP方法和状态码：

- **重试次数**：3次
- **重试的HTTP方法**：GET, POST
- **重试的HTTP状态**：502, 503
- **退避策略**：指数退避，初始50ms，最大500ms

## 服务降级

当后端服务不可用时，网关提供服务降级功能：

- 使用`FallbackController`返回标准错误响应
- 降级响应包含请求路径、时间戳和请求ID
- 所有降级操作都会记录在日志中

## 监控与指标

网关暴露多种监控指标：

- **HTTP请求计数**：按路由、状态码分组
- **请求延迟**：按路由统计的请求处理时间
- **活跃连接**：当前活跃的HTTP连接数
- **断路器状态**：各断路器的当前状态
- **限流计数**：按限流器分组的限流计数

这些指标可通过Spring Boot Actuator端点访问，并被Prometheus收集。

## 部署配置

网关可以水平扩展，建议在生产环境中至少部署两个实例以实现高可用性。配置说明：

- **内存配置**：建议至少2GB堆内存
- **CPU**：建议至少2个CPU核心
- **网络**：建议使用独立的负载均衡器分发流量

## 开发指南

### 添加新路由

要添加新的路由，请修改`RouteConfig.java`：

```java
.route("new-service", r -> r.path("/api/new-service/**")
    .filters(f -> f.stripPrefix(2))
    .uri("lb://platform-new-service"))
```

### 自定义过滤器

要添加自定义过滤器，请创建一个实现`GlobalFilter`接口的类：

```java
@Component
public class CustomFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 过滤器逻辑
        return chain.filter(exchange);
    }
    
    @Override
    public int getOrder() {
        // 过滤器执行顺序
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
```

## 安全注意事项

- 网关是系统的入口点，应特别注意安全配置
- 确保限流设置合理，防止DoS攻击
- 所有敏感信息应加密存储（如API密钥、密码）
- 建议启用HTTPS和TLS终止
- 定期审查日志和异常请求模式

## 故障排除

常见问题及解决方案：

1. **路由找不到**：检查服务注册状态和路由配置
2. **高延迟**：检查后端服务性能和网关资源使用情况
3. **连接拒绝**：可能是限流触发，检查限流配置和请求流量
4. **断路器开路**：检查后端服务健康状态和性能
5. **内存不足**：增加JVM堆大小或扩展网关实例数量

## 版本历史

- **1.0.0-SNAPSHOT**：初始版本
  - 基本路由功能
  - 限流配置
  - 全局过滤器
  - 断路器集成
  - 服务降级
