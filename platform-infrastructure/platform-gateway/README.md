# platform-gateway

## 项目概述

`platform-gateway` 是企业级数据平台的API网关服务，基于Spring Cloud Gateway实现，作为微服务架构的入口点，提供路由转发、负载均衡、流量控制、身份验证、监控和日志记录等核心功能。该模块遵循六边形架构设计原则，有效隔离业务逻辑与技术实现。

## 主要职责

1. **请求路由转发**：根据路由定义将请求转发到相应的微服务
2. **统一认证鉴权**：集中处理身份验证和权限控制
3. **流量控制**：提供限流、熔断、重试等流量治理功能
4. **请求响应转换**：支持请求和响应数据格式转换
5. **统一日志记录**：集中记录API调用日志
6. **监控与统计**：收集API调用指标，支持调用链追踪
7. **安全防护**：实现跨站请求伪造（CSRF）、跨域资源共享（CORS）等安全特性

## 技术架构

`platform-gateway` 基于Spring Cloud Gateway和Spring Cloud构建，采用响应式编程模型：

1. **核心框架**：
   - Spring Boot 3.2.1
   - Spring Cloud Gateway
   - Project Reactor（响应式编程）
   - Spring Security

2. **功能组件**：
   - Redis：用于限流、缓存等
   - Spring Cloud LoadBalancer：客户端负载均衡
   - Resilience4j：熔断、限流、重试
   - Micrometer：指标收集

### 系统架构图

```
                                    ┌───────────────────────┐
                                    │                       │
                                    │    客户端应用         │
                                    │  (Web/移动/IoT)       │
                                    │                       │
                                    └───────────┬───────────┘
                                                │
                                                ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                              负载均衡器 (如Nginx/SLB)                        │
└────────────────────────────────────┬───────────────────────────────────────┘
                                     │
                                     ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                                                                            │
│                              platform-gateway                              │
│                                                                            │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐                │
│  │   路由定义     │  │    过滤器链    │  │   全局过滤器   │                │
│  └────────────────┘  └────────────────┘  └────────────────┘                │
│                                                                            │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐                │
│  │   限流组件     │  │    认证授权    │  │   监控与日志   │                │
│  └────────────────┘  └────────────────┘  └────────────────┘                │
│                                                                            │
└───────────────────────────────────┬────────────────────────────────────────┘
                                    │
                                    ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                                                                           │
│                              微服务集群                                   │
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │                 │  │                 │  │                 │            │
│  │  认证授权服务   │  │   业务服务 A    │  │   业务服务 B    │            │
│  │                 │  │                 │  │                 │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                           │
└───────────────────────────────────────────────────────────────────────────┘
```

### 模块结构

```
platform-gateway/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/platform/gateway/
│   │   │       ├── application/                 # 应用层
│   │   │       │   ├── service/                 # 应用服务
│   │   │       │   ├── event/                   # 事件处理
│   │   │       │   └── dto/                     # 数据传输对象
│   │   │       ├── domain/                      # 领域层
│   │   │       │   ├── model/                   # 领域模型
│   │   │       │   ├── service/                 # 领域服务
│   │   │       │   └── event/                   # 领域事件
│   │   │       ├── infrastructure/              # 基础设施层
│   │   │       │   ├── config/                  # 配置类
│   │   │       │   │   ├── RouteConfig.java
│   │   │       │   │   ├── SecurityConfig.java
│   │   │       │   │   └── RateLimiterConfig.java
│   │   │       │   ├── filter/                  # 网关过滤器
│   │   │       │   │   ├── global/              # 全局过滤器
│   │   │       │   │   │   ├── AuthFilter.java
│   │   │       │   │   │   ├── LoggingFilter.java
│   │   │       │   │   │   └── RequestIdFilter.java
│   │   │       │   │   └── route/               # 路由过滤器
│   │   │       │   │       ├── RateLimiterFilter.java
│   │   │       │   │       └── RetryFilter.java
│   │   │       │   ├── repository/              # 仓储实现
│   │   │       │   │   └── RedisRouteDefinitionRepository.java
│   │   │       │   ├── limiter/                 # 限流实现
│   │   │       │   │   └── RedisRateLimiter.java
│   │   │       │   └── security/                # 安全实现
│   │   │       │       ├── AuthProvider.java
│   │   │       │       └── JwtUtil.java
│   │   │       ├── interfaces/                  # 接口层
│   │   │       │   ├── rest/                    # REST API
│   │   │       │   │   ├── RouteController.java
│   │   │       │   │   └── SystemController.java
│   │   │       │   └── facade/                  # 内部服务接口
│   │   │       ├── GatewayApplication.java      # 启动类
│   │   │       └── GatewayProperties.java       # 属性配置
│   │   └── resources/
│   │       ├── application.yml                  # 应用配置
│   │       ├── application-dev.yml              # 开发环境配置
│   │       ├── application-test.yml             # 测试环境配置
│   │       ├── application-prod.yml             # 生产环境配置
│   │       ├── bootstrap.yml                    # 引导配置
│   │       ├── logback-spring.xml               # 日志配置
│   │       └── META-INF/
│   │           └── spring.factories             # Spring工厂配置
│   └── test/                                    # 测试代码
├── Dockerfile                                   # Docker构建文件
├── pom.xml                                      # Maven项目配置
└── README.md                                    # 项目说明
```

## 核心功能

### 1. 路由转发

网关负责将请求根据路由定义转发到对应的微服务。路由配置示例：

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 认证服务路由
        - id: auth-service
          uri: lb://platform-auth-service
          predicates:
            - Path=/auth/**
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@ipKeyResolver}"
        
        # 数据治理服务路由
        - id: data-governance-service
          uri: lb://platform-data-governance
          predicates:
            - Path=/governance/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: dataGovernanceCircuitBreaker
                fallbackUri: forward:/fallback/data-governance
        
        # 数据采集服务路由
        - id: data-collect-service
          uri: lb://platform-collect
          predicates:
            - Path=/collect/**
          filters:
            - StripPrefix=1
            - name: Retry
              args:
                retries: 3
                statuses: BAD_GATEWAY
```

动态路由实现：

```java
@Configuration
public class DynamicRouteConfig {
    
    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;
    
    @Autowired
    private ReactiveRedisTemplate<String, RouteDefinition> reactiveRedisTemplate;
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("path_route", r -> r.path("/api/**")
                        .uri("lb://platform-api-service"))
                .route("host_route", r -> r.host("*.platform.com")
                        .uri("lb://platform-host-service"))
                .build();
    }
    
    @Bean
    public RedisRouteDefinitionRepository redisRouteDefinitionRepository() {
        return new RedisRouteDefinitionRepository(reactiveRedisTemplate);
    }
}

/**
 * Redis路由定义仓库
 */
@Slf4j
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {
    private static final String ROUTE_KEY_PREFIX = "platform:gateway:routes:";
    
    private final ReactiveRedisTemplate<String, RouteDefinition> reactiveRedisTemplate;
    
    public RedisRouteDefinitionRepository(ReactiveRedisTemplate<String, RouteDefinition> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }
    
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return reactiveRedisTemplate.keys(ROUTE_KEY_PREFIX + "*")
                .flatMap(key -> reactiveRedisTemplate.opsForValue().get(key));
    }
    
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
            String key = ROUTE_KEY_PREFIX + r.getId();
            return reactiveRedisTemplate.opsForValue().set(key, r)
                    .then();
        });
    }
    
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            String key = ROUTE_KEY_PREFIX + id;
            return reactiveRedisTemplate.opsForValue().delete(key)
                    .then();
        });
    }
}
```

### 2. 统一认证授权

基于JWT的统一认证过滤器：

```java
@Component
@Order(-100)
public class AuthGlobalFilter implements GlobalFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private GatewayProperties gatewayProperties;
    
    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    
    private static final String REDIS_TOKEN_KEY_PREFIX = "platform:token:blacklist:";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 跳过不需要认证的路径
        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }
        
        // 获取token
        String token = getToken(request);
        if (token == null) {
            return unauthorizedResponse(exchange);
        }
        
        // 检查token是否在黑名单中
        return checkBlacklist(token)
                .flatMap(isBlacklisted -> {
                    if (isBlacklisted) {
                        return unauthorizedResponse(exchange);
                    }
                    
                    // 验证token
                    try {
                        Claims claims = jwtUtil.parseToken(token);
                        
                        // 检查token是否过期
                        if (jwtUtil.isTokenExpired(claims)) {
                            return unauthorizedResponse(exchange);
                        }
                        
                        // 将用户信息传递给下游服务
                        ServerHttpRequest newRequest = request.mutate()
                                .header("X-User-Id", claims.getSubject())
                                .header("X-User-Name", claims.get("username", String.class))
                                .header("X-User-Roles", claims.get("roles", String.class))
                                .build();
                        
                        return chain.filter(exchange.mutate().request(newRequest).build());
                    } catch (Exception e) {
                        log.error("Token verification failed", e);
                        return unauthorizedResponse(exchange);
                    }
                });
    }
    
    private boolean isExcludedPath(String path) {
        return gatewayProperties.getAuth().getExcludedPaths().stream()
                .anyMatch(pattern -> PathUtil.match(pattern, path));
    }
    
    private String getToken(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        return null;
    }
    
    private Mono<Boolean> checkBlacklist(String token) {
        String key = REDIS_TOKEN_KEY_PREFIX + token;
        return reactiveRedisTemplate.hasKey(key);
    }
    
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        String body = "{\"code\":401,\"message\":\"Unauthorized\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
```

JWT工具类：

```java
@Component
public class JwtUtil {
    
    @Value("${platform.security.jwt.secret}")
    private String secret;
    
    @Value("${platform.security.jwt.expiration}")
    private long expiration;
    
    /**
     * 解析JWT token
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 验证token是否过期
     */
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
    
    /**
     * 从token中获取用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get("username", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
```

### 3. 流量控制

基于Redis的限流器实现：

```java
@Configuration
public class RateLimiterConfig {
    
    /**
     * IP地址限流解析器
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }
    
    /**
     * 用户ID限流解析器
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            List<String> userIds = exchange.getRequest().getHeaders().get("X-User-Id");
            if (userIds != null && !userIds.isEmpty()) {
                return Mono.just(userIds.get(0));
            }
            return Mono.just("anonymous");
        };
    }
    
    /**
     * 路径限流解析器
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getURI().getPath()
        );
    }
    
    /**
     * 自定义Redis限流器
     */
    @Bean
    public RedisRateLimiter customRedisRateLimiter(
            ReactiveRedisTemplate<String, String> redisTemplate,
            @Qualifier(RedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript,
            ConfigurationService configurationService) {
        return new CustomRedisRateLimiter(redisTemplate, redisScript, configurationService);
    }
}

@Slf4j
public class CustomRedisRateLimiter extends RedisRateLimiter {
    
    public CustomRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                                 RedisScript<List<Long>> script,
                                 ConfigurationService configurationService) {
        super(redisTemplate, script, configurationService);
    }
    
    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        // 自定义限流逻辑，可根据业务需求进行扩展
        // 例如：不同时段采用不同的限流策略
        int hour = LocalDateTime.now().getHour();
        if (hour >= 0 && hour < 6) {
            // 凌晨时段放宽限流
            Config config = getConfig().getOrDefault(routeId, getDefaultConfig());
            int burstCapacity = config.getBurstCapacity() * 2;
            int replenishRate = config.getReplenishRate() * 2;
            Config newConfig = new Config().setBurstCapacity(burstCapacity).setReplenishRate(replenishRate);
            
            return super.isAllowed(routeId, id, newConfig);
        }
        
        return super.isAllowed(routeId, id);
    }
}
```

熔断器配置：

```java
@Configuration
public class CircuitBreakerConfig {
    
    @Bean
    public ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory(
            CircuitBreakerRegistry circuitBreakerRegistry) {
        ReactiveResilience4JCircuitBreakerFactory factory = 
                new ReactiveResilience4JCircuitBreakerFactory();
        factory.configureCircuitBreakerRegistry(circuitBreakerRegistry);
        factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build())
                .build());
        
        // 自定义特定服务的熔断配置
        factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(20)
                        .failureRateThreshold(30)
                        .waitDurationInOpenState(Duration.ofSeconds(20))
                        .permittedNumberOfCallsInHalfOpenState(10)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5))
                        .build()),
                "dataGovernanceCircuitBreaker");
        
        return factory;
    }
    
    /**
     * 熔断回退处理器
     */
    @Bean
    public WebFluxSupportingControllerAdvice webFluxSupportingControllerAdvice() {
        return new WebFluxSupportingControllerAdvice();
    }
    
    /**
     * 熔断后回退处理
     */
    @Controller
    @RequestMapping("/fallback")
    public class FallbackController {
        
        /**
         * 默认熔断回退处理
         */
        @GetMapping("/**")
        @ResponseBody
        public Mono<Map<String, Object>> defaultFallback() {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 503);
            response.put("message", "Service temporarily unavailable");
            response.put("timestamp", System.currentTimeMillis());
            
            return Mono.just(response);
        }
        
        /**
         * 数据治理服务熔断回退处理
         */
        @GetMapping("/data-governance")
        @ResponseBody
        public Mono<Map<String, Object>> dataGovernanceFallback() {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 503);
            response.put("message", "Data Governance Service is currently unavailable");
            response.put("timestamp", System.currentTimeMillis());
            
            return Mono.just(response);
        }
    }
}
```

### 4. 请求日志记录

全局日志过滤器：

```java
@Component
@Order(-2)
@Slf4j
public class LoggingGlobalFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = request.getId();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String remoteAddress = request.getRemoteAddress().getAddress().getHostAddress();
        
        // 记录请求信息
        log.info("Request: [{}] {} {} from {}", method, path, requestId, remoteAddress);
        
        // 记录请求头
        if (log.isDebugEnabled()) {
            request.getHeaders().forEach((name, values) -> {
                values.forEach(value -> log.debug("Request Header: {}: {}", name, value));
            });
        }
        
        long startTime = System.currentTimeMillis();
        
        // 记录响应信息
        return chain.filter(exchange)
                .doOnSuccess(v -> {
                    long duration = System.currentTimeMillis() - startTime;
                    HttpStatus statusCode = exchange.getResponse().getStatusCode();
                    log.info("Response: [{}] {} {} - {} - {}ms", 
                             method, path, requestId, 
                             statusCode != null ? statusCode.value() : "Unknown", 
                             duration);
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.error("Response: [{}] {} {} - Error - {}ms", 
                             method, path, requestId, duration, error);
                });
    }
}
```

结合ELK日志收集：

```yaml
# logback-spring.xml 配置
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>
    
    <springProperty scope="context" name="appName" source="spring.application.name" defaultValue="platform-gateway"/>
    <springProperty scope="context" name="logstashHost" source="logging.logstash.host" defaultValue="localhost"/>
    <springProperty scope="context" name="logstashPort" source="logging.logstash.port" defaultValue="5000"/>
    
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/${appName}.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/${appName}.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- Logstash输出 -->
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>${logstashHost}:${logstashPort}</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"application":"${appName}"}</customFields>
        </encoder>
        <keepAliveDuration>5 minutes</keepAliveDuration>
    </appender>
    
    <!-- 开发环境配置 -->
    <springProfile name="dev">
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
            <appender-ref ref="FILE"/>
        </root>
        <logger name="com.platform" level="DEBUG"/>
    </springProfile>
    
    <!-- 测试环境配置 -->
    <springProfile name="test">
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
            <appender-ref ref="FILE"/>
            <appender-ref ref="LOGSTASH"/>
        </root>
    </springProfile>
    
    <!-- 生产环境配置 -->
    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="FILE"/>
            <appender-ref ref="LOGSTASH"/>
        </root>
    </springProfile>
</configuration>
```

### 5. 跨域资源共享（CORS）

CORS配置：

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的源
        config.addAllowedOrigin("*.platform.com");
        config.addAllowedOrigin("https://*.platform.com");
        
        // 允许的HTTP方法
        config.addAllowedMethod("*");
        
        // 允许的头信息
        config.addAllowedHeader("*");
        
        // 是否允许发送Cookie
        config.setAllowCredentials(true);
        
        // 预检请求的有效期，单位秒
        config.setMaxAge(3600L);
        
        // 匹配所有路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}
```

### 6. 监控与指标收集

Micrometer指标配置：

```java
@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(
            @Value("${spring.application.name}") String applicationName) {
        return registry -> registry.config()
                .commonTags("application", applicationName);
    }
    
    @Bean
    public MetricsWebClientFilterFunction metricsWebClientFilterFunction(MeterRegistry meterRegistry) {
        return new MetricsWebClientFilterFunction(
                meterRegistry,
                new DefaultWebClientExchangeTagsProvider(),
                "gateway.webclient.requests",
                AutoTimer.ENABLED
        );
    }
    
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
```

自定义指标：

```java
@Component
@Slf4j
public class GatewayMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter totalRequestsCounter;
    private final Counter failedRequestsCounter;
    private final Timer responseTimeTimer;
    private final Map<String, Counter> routeCounters = new ConcurrentHashMap<>();
    
    @Autowired
    public GatewayMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // 总请求计数器
        this.totalRequestsCounter = Counter.builder("gateway.requests.total")
                .description("Total number of requests")
                .register(meterRegistry);
        
        // 失败请求计数器
        this.failedRequestsCounter = Counter.builder("gateway.requests.failed")
                .description("Failed requests count")
                .register(meterRegistry);
        
        // 响应时间计时器
        this.responseTimeTimer = Timer.builder("gateway.response.time")
                .description("Request response time")
                .percentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(meterRegistry);
    }
    
    /**
     * 记录请求
     */
    public void recordRequest(String routeId) {
        totalRequestsCounter.increment();
        getOrCreateRouteCounter(routeId).increment();
    }
    
    /**
     * 记录失败请求
     */
    public void recordFailedRequest(String routeId) {
        failedRequestsCounter.increment();
    }
    
    /**
     * 记录响应时间
     */
    public void recordResponseTime(long responseTimeMs) {
        responseTimeTimer.record(responseTimeMs, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 获取或创建路由计数器
     */
    private Counter getOrCreateRouteCounter(String routeId) {
        return routeCounters.computeIfAbsent(routeId, id -> {
            return Counter.builder("gateway.requests.route")
                    .tag("routeId", id)
                    .description("Requests count by route")
                    .register(meterRegistry);
        });
    }
}
```

### 7. 动态配置管理

基于配置中心的动态配置：

```java
@Configuration
@RefreshScope
public class DynamicConfigManager {
    
    @Autowired
    private ApplicationEventPublisher publisher;
    
    @Autowired
    private RouteDefinitionRepository routeDefinitionRepository;
    
    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    
    private static final String CONFIG_CHANNEL = "platform:gateway:config:refresh";
    
    @PostConstruct
    public void init() {
        // 订阅配置变更消息
        reactiveRedisTemplate.listenToChannel(CONFIG_CHANNEL)
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe(message -> {
                    if ("ROUTES_REFRESH".equals(message)) {
                        refreshRoutes();
                    } else if ("RATE_LIMIT_REFRESH".equals(message)) {
                        refreshRateLimiters();
                    } else if ("ALL_REFRESH".equals(message)) {
                        refreshAll();
                    }
                });
    }
    
    /**
     * 刷新路由配置
     */
    public void refreshRoutes() {
        log.info("Refreshing gateway routes");
        // 发布路由刷新事件
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }
    
    /**
     * 刷新限流器配置
     */
    public void refreshRateLimiters() {
        log.info("Refreshing rate limiters");
        this.publisher.publishEvent(new ApplicationEvent(this) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public Object getSource() {
                return "RATE_LIMIT_REFRESH";
            }
        });
    }
    
    /**
     * 刷新所有配置
     */
    public void refreshAll() {
        log.info("Refreshing all gateway configurations");
        refreshRoutes();
        refreshRateLimiters();
    }
    
    /**
     * 添加路由定义
     */
    public Mono<Void> add(RouteDefinition routeDefinition) {
        return routeDefinitionRepository.save(Mono.just(routeDefinition))
                .then(Mono.defer(this::refreshRoutes));
    }
    
    /**
     * 更新路由定义
     */
    public Mono<Void> update(RouteDefinition routeDefinition) {
        return routeDefinitionRepository.delete(Mono.just(routeDefinition.getId()))
                .then(routeDefinitionRepository.save(Mono.just(routeDefinition)))
                .then(Mono.defer(this::refreshRoutes));
    }
    
    /**
     * 删除路由定义
     */
    public Mono<Void> delete(String routeId) {
        return routeDefinitionRepository.delete(Mono.just(routeId))
                .then(Mono.defer(this::refreshRoutes));
    }
    
    private Mono<Void> refreshRoutes() {
        refreshRoutes();
        return Mono.empty();
    }
}
```

### 8. 请求追踪与分布式链路追踪

整合Micrometer Tracing：

```java
@Configuration
public class TracingConfig {
    
    @Bean
    public Tracer tracer(Tracing tracing) {
        return tracing.tracer();
    }
    
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> circuitBreakerTraceCustomizer(
            Tracer tracer, SpanCustomizer spanCustomizer) {
        return factory -> factory.configureCircuitBreakerRegistry(
                CircuitBreakerRegistry.of(
                        CircuitBreakerConfig.custom()
                                .slidingWindowSize(10)
                                .failureRateThreshold(50)
                                .waitDurationInOpenState(Duration.ofSeconds(1))
                                .build()
                )
        );
    }
    
    @Bean
    public TracingGlobalFilter tracingGlobalFilter(Tracer tracer) {
        return new TracingGlobalFilter(tracer);
    }
}

@Component
@Order(-10)
@Slf4j
public class TracingGlobalFilter implements GlobalFilter {
    
    private final Tracer tracer;
    
    public TracingGlobalFilter(Tracer tracer) {
        this.tracer = tracer;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        // 提取上游传递的追踪信息（如果有）
        String traceId = request.getHeaders().getFirst("X-B3-TraceId");
        String spanId = request.getHeaders().getFirst("X-B3-SpanId");
        String parentSpanId = request.getHeaders().getFirst("X-B3-ParentSpanId");
        String sampled = request.getHeaders().getFirst("X-B3-Sampled");
        
        // 创建或继续span
        Span span;
        if (traceId != null && spanId != null) {
            // 继续现有span
            SpanContext parentContext = extractContext(traceId, spanId, parentSpanId, sampled);
            span = tracer.nextSpan(parentContext).name(method + " " + path).start();
        } else {
            // 创建新span
            span = tracer.nextSpan().name(method + " " + path).start();
        }
        
        // 添加通用标签
        span.tag("http.method", method);
        span.tag("http.path", path);
        span.tag("peer.address", request.getRemoteAddress().getHostString());
        
        // 传递到作用域
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            // 执行过滤器链
            return chain.filter(exchange)
                    .doOnSuccess(v -> {
                        HttpStatus status = exchange.getResponse().getStatusCode();
                        if (status != null) {
                            span.tag("http.status_code", String.valueOf(status.value()));
                        }
                        span.finish();
                    })
                    .doOnError(error -> {
                        span.tag("error", error.getMessage());
                        span.finish();
                    });
        } catch (Exception e) {
            span.tag("error", e.getMessage());
            span.finish();
            return Mono.error(e);
        }
    }
    
    private SpanContext extractContext(String traceId, String spanId, String parentSpanId, String sampled) {
        // 简化实现，实际应使用tracing库提供的上下文提取器
        return new SpanContext() {
            @Override
            public String traceId() {
                return traceId;
            }
            
            @Override
            public String parentId() {
                return parentSpanId;
            }
            
            @Override
            public String spanId() {
                return spanId;
            }
            
            @Override
            public Boolean sampled() {
                return sampled != null ? "1".equals(sampled) : null;
            }
        };
    }
}
```

## 管理API

提供网关管理接口：

```java
@RestController
@RequestMapping("/admin/routes")
@PreAuthorize("hasRole('ADMIN')")
public class RouteAdminController {
    
    @Autowired
    private DynamicConfigManager configManager;
    
    /**
     * 获取所有路由
     */
    @GetMapping
    public Mono<List<RouteDefinition>> getRoutes() {
        return configManager.getRouteDefinitions().collectList();
    }
    
    /**
     * 获取指定路由
     */
    @GetMapping("/{id}")
    public Mono<RouteDefinition> getRoute(@PathVariable String id) {
        return configManager.getRouteDefinition(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }
    
    /**
     * 添加路由
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> addRoute(@RequestBody @Valid RouteDefinition routeDefinition) {
        return configManager.add(routeDefinition);
    }
    
    /**
     * 更新路由
     */
    @PutMapping("/{id}")
    public Mono<Void> updateRoute(@PathVariable String id, @RequestBody @Valid RouteDefinition routeDefinition) {
        if (!id.equals(routeDefinition.getId())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Route IDs do not match"));
        }
        return configManager.update(routeDefinition);
    }
    
    /**
     * 删除路由
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteRoute(@PathVariable String id) {
        return configManager.delete(id);
    }
    
    /**
     * 刷新路由
     */
    @PostMapping("/refresh")
    public Mono<Void> refreshRoutes() {
        configManager.refreshRoutes();
        return Mono.empty();
    }
    
    /**
     * 刷新所有配置
     */
    @PostMapping("/refresh-all")
    public Mono<Void> refreshAll() {
        configManager.refreshAll();
        return Mono.empty();
    }
}
```

## 依赖管理

### POM配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.platform</groupId>
        <artifactId>platform-infrastructure</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>platform-gateway</artifactId>
    <packaging>jar</packaging>
    <n>Platform Gateway</n>
    <description>API Gateway for Enterprise Data Platform</description>
    
    <properties>
        <resilience4j.version>2.1.0</resilience4j.version>
    </properties>
    
    <dependencies>
        <!-- 平台公共模块 -->
        <dependency>
            <groupId>com.platform</groupId>
            <artifactId>platform-common-core</artifactId>
        </dependency>
        <dependency>
            <groupId>com.platform</groupId>
            <artifactId>platform-common-web</artifactId>
        </dependency>
        
        <!-- Spring Cloud Gateway -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        
        <!-- Spring Cloud Discovery -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        
        <!-- Spring Cloud Config -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
        
        <!-- Resilience4j -->
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot3</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-reactor</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>
        
        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
        </dependency>
        
        <!-- Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Metrics and Monitoring -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
        
        <!-- Tracing -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-tracing</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-tracing-bridge-brave</artifactId>
        </dependency>
        <dependency>
            <groupId>io.zipkin.reporter2</groupId>
            <artifactId>zipkin-reporter-brave</artifactId>
        </dependency>
        
        <!-- Logging -->
        <dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
            <version>7.4</version>
        </dependency>
        
        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            
            <!-- Docker构建插件 -->
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>dockerfile-maven-plugin</artifactId>
                <version>1.4.13</version>
                <configuration>
                    <repository>${docker.registry}/platform/gateway</repository>
                    <tag>${project.version}</tag>
                    <buildArgs>
                        <JAR_FILE>target/${project.build.finalName}.jar</JAR_FILE>
                    </buildArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## 容器化部署

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q -O /dev/null http://localhost:8080/actuator/health || exit 1

# 时区设置
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 非root用户运行
RUN addgroup -S platform && adduser -S gateway -G platform
USER gateway

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```

### Kubernetes部署配置

```yaml
# gateway-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: platform-gateway
  namespace: platform-infra
  labels:
    app: platform-gateway
spec:
  replicas: 3
  selector:
    matchLabels:
      app: platform-gateway
  strategy:
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
    type: RollingUpdate
  template:
    metadata:
      labels:
        app: platform-gateway
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      containers:
      - name: platform-gateway
        image: ${DOCKER_REGISTRY}/platform/gateway:${IMAGE_TAG}
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: ${ENVIRONMENT}
        - name: SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR
          value: ${NACOS_SERVER_ADDR}
        - name: SPRING_CLOUD_NACOS_DISCOVERY_NAMESPACE
          value: ${NACOS_NAMESPACE}
        - name: SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR
          value: ${NACOS_SERVER_ADDR}
        - name: SPRING_CLOUD_NACOS_CONFIG_NAMESPACE
          value: ${NACOS_NAMESPACE}
        - name: SPRING_REDIS_HOST
          value: ${REDIS_HOST}
        - name: SPRING_REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: redis-secret
              key: password
        - name: PLATFORM_SECURITY_JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: jwt-secret
              key: secret
        resources:
          requests:
            memory: "512Mi"
            cpu: "200m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
          timeoutSeconds: 3
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 3
          failureThreshold: 3
---
apiVersion: v1
kind: Service
metadata:
  name: platform-gateway
  namespace: platform-infra
spec:
  selector:
    app: platform-gateway
  ports:
  - port: 80
    targetPort: 8080
    name: http
  type: ClusterIP
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: platform-gateway-ingress
  namespace: platform-infra
  annotations:
    kubernetes.io/ingress.class: "nginx"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/proxy-body-size: "50m"
    nginx.ingress.kubernetes.io/proxy-connect-timeout: "300"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "300"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "300"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  tls:
  - hosts:
    - api.platform.com
    secretName: platform-api-tls
  rules:
  - host: api.platform.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: platform-gateway
            port:
              number: 80
```

## 性能优化

### 1. 内存优化

JVM参数调优：

```
JAVA_OPTS="-server -Xms1g -Xmx1g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled -XX:ErrorFile=/app/logs/hs_err_pid%p.log"
```

### 2. 连接池优化

Web客户端连接池配置：

```java
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClient(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return WebClient.builder()
                .filter(lbFunction)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> {
                            configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024); // 2MB
                        })
                        .build())
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .build();
    }
    
    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(10))
                .compress(true)
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)))
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .connectionPool(ConnectionProvider.builder("gateway-conn-pool")
                        .maxConnections(1000)
                        .maxIdleTime(Duration.ofSeconds(60))
                        .maxLifeTime(Duration.ofMinutes(30))
                        .pendingAcquireTimeout(Duration.ofSeconds(60))
                        .evictInBackground(Duration.ofSeconds(120))
                        .build());
    }
}
```

### 3. 缓存优化

路由定义缓存：

```java
@Configuration
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withCacheConfiguration("routeDefinitions", 
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("authTokens", 
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .build();
    }
}
```

## 安全配置

### 加密密钥安全管理

```java
@Configuration
public class SecurityKeyConfig {
    
    @Bean
    @ConditionalOnProperty(name = "platform.security.key-rotation.enabled", havingValue = "true")
    public KeyRotationScheduler keyRotationScheduler(
            KeyManager keyManager,
            @Value("${platform.security.key-rotation.interval-days:30}") long intervalDays) {
        return new KeyRotationScheduler(keyManager, intervalDays);
    }
    
    @Bean
    public KeyManager keyManager(
            @Value("${platform.security.jwt.secret}") String initialSecret,
            ReactiveRedisTemplate<String, String> redisTemplate) {
        return new RedisKeyManager(initialSecret, redisTemplate);
    }
}

@Slf4j
public class RedisKeyManager implements KeyManager {
    
    private static final String CURRENT_KEY_REDIS_KEY = "platform:security:current-key";
    private static final String KEY_HISTORY_REDIS_KEY = "platform:security:key-history";
    
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final String initialSecret;
    
    public RedisKeyManager(String initialSecret, ReactiveRedisTemplate<String, String> redisTemplate) {
        this.initialSecret = initialSecret;
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public Mono<String> getCurrentKey() {
        return redisTemplate.opsForValue().get(CURRENT_KEY_REDIS_KEY)
                .switchIfEmpty(Mono.defer(() -> {
                    // 如果Redis中没有密钥，则使用初始密钥并将其保存到Redis
                    return redisTemplate.opsForValue().set(CURRENT_KEY_REDIS_KEY, initialSecret)
                            .then(Mono.just(initialSecret));
                }));
    }
    
    @Override
    public Mono<Void> rotateKey() {
        // 生成新密钥
        String newKey = generateNewKey();
        
        // 将当前密钥添加到历史记录
        return getCurrentKey()
                .flatMap(currentKey -> redisTemplate.opsForList().leftPush(KEY_HISTORY_REDIS_KEY, currentKey))
                // 将新密钥设置为当前密钥
                .then(redisTemplate.opsForValue().set(CURRENT_KEY_REDIS_KEY, newKey))
                // 清理过期的密钥历史（保留最近5个）
                .then(redisTemplate.opsForList().trim(KEY_HISTORY_REDIS_KEY, 0, 4))
                .doOnSuccess(v -> log.info("Security key rotated successfully"))
                .doOnError(e -> log.error("Failed to rotate security key", e));
    }
    
    @Override
    public Mono<List<String>> getKeyHistory() {
        return redisTemplate.opsForList().range(KEY_HISTORY_REDIS_KEY, 0, -1)
                .collectList();
    }
    
    private String generateNewKey() {
        // 生成安全的随机密钥
        byte[] randomBytes = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }
}

@Component
@Slf4j
@ConditionalOnProperty(name = "platform.security.key-rotation.enabled", havingValue = "true")
public class KeyRotationScheduler {
    
    private final KeyManager keyManager;
    private final long intervalDays;
    
    public KeyRotationScheduler(KeyManager keyManager, long intervalDays) {
        this.keyManager = keyManager;
        this.intervalDays = intervalDays;
    }
    
    @Scheduled(cron = "${platform.security.key-rotation.cron:0 0 0 * * ?}")
    public void rotateKeys() {
        // 获取上次轮换时间
        keyManager.getLastRotationTime()
                .flatMap(lastRotation -> {
                    LocalDateTime now = LocalDateTime.now();
                    if (lastRotation.plusDays(intervalDays).isBefore(now)) {
                        // 如果已经过了轮换间隔，则执行轮换
                        log.info("Rotating security keys as per schedule");
                        return keyManager.rotateKey();
                    } else {
                        // 否则跳过轮换
                        log.debug("Skipping key rotation as it's not time yet");
                        return Mono.empty();
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error during scheduled key rotation", e);
                    return Mono.empty();
                })
                .subscribe();
    }
}
```

### 防止CSRF攻击

```java
@Configuration
public class CsrfConfig {
    
    @Bean
    public WebFilter csrfFilter() {
        return (exchange, chain) -> {
            // 只对状态改变请求（POST, PUT, DELETE等）进行CSRF检查
            if (isMutatingMethod(exchange.getRequest().getMethod())) {
                return validateCsrfToken(exchange)
                        .flatMap(isValid -> {
                            if (isValid) {
                                return chain.filter(exchange);
                            } else {
                                return handleInvalidCsrfToken(exchange);
                            }
                        });
            }
            return chain.filter(exchange);
        };
    }
    
    private boolean isMutatingMethod(HttpMethod method) {
        return HttpMethod.POST.equals(method) ||
               HttpMethod.PUT.equals(method) ||
               HttpMethod.DELETE.equals(method) ||
               HttpMethod.PATCH.equals(method);
    }
    
    private Mono<Boolean> validateCsrfToken(ServerWebExchange exchange) {
        // 获取请求中的CSRF令牌
        String requestToken = exchange.getRequest().getHeaders().getFirst("X-CSRF-TOKEN");
        if (requestToken == null) {
            // 也可以从表单参数中获取
            return exchange.getFormData()
                    .flatMap(formData -> {
                        String formToken = formData.getFirst("_csrf");
                        if (formToken == null) {
                            return Mono.just(false);
                        }
                        return validateTokenAgainstSession(exchange, formToken);
                    })
                    .onErrorReturn(false);
        }
        
        // 验证令牌
        return validateTokenAgainstSession(exchange, requestToken);
    }
    
    private Mono<Boolean> validateTokenAgainstSession(ServerWebExchange exchange, String token) {
        // 获取会话中存储的令牌
        return exchange.getSession()
                .flatMap(session -> {
                    Object storedToken = session.getAttribute("CSRF_TOKEN");
                    return Mono.just(storedToken != null && storedToken.equals(token));
                });
    }
    
    private Mono<Void> handleInvalidCsrfToken(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        String body = "{\"code\":403,\"message\":\"Invalid CSRF token\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
```

## 测试策略

### 单元测试示例

```java
@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = RouteAdminController.class)
@Import({SecurityConfig.class, DynamicConfigManager.class})
class RouteAdminControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private RouteDefinitionRepository routeDefinitionRepository;
    
    @MockBean
    private ReactiveRedisTemplate<String, RouteDefinition> reactiveRedisTemplate;
    
    @MockBean
    private ApplicationEventPublisher eventPublisher;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllRoutes() {
        // Arrange
        RouteDefinition route1 = new RouteDefinition();
        route1.setId("test-route-1");
        RouteDefinition route2 = new RouteDefinition();
        route2.setId("test-route-2");
        
        when(routeDefinitionRepository.getRouteDefinitions())
                .thenReturn(Flux.just(route1, route2));
        
        // Act & Assert
        webTestClient.get().uri("/admin/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RouteDefinition.class)
                .hasSize(2)
                .contains(route1, route2);
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAddRoute() {
        // Arrange
        RouteDefinition newRoute = new RouteDefinition();
        newRoute.setId("new-route");
        
        when(routeDefinitionRepository.save(any()))
                .thenReturn(Mono.empty());
        
        // Act & Assert
        webTestClient.post().uri("/admin/routes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newRoute)
                .exchange()
                .expectStatus().isCreated();
        
        verify(routeDefinitionRepository).save(any());
        verify(eventPublisher).publishEvent(any(RefreshRoutesEvent.class));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void shouldRejectNonAdminUsers() {
        // Act & Assert
        webTestClient.get().uri("/admin/routes")
                .exchange()
                .expectStatus().isForbidden();
    }
}
```

### 集成测试示例

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
        "spring.cloud.gateway.discovery.locator.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false",
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false"
})
class GatewayIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private RouteDefinitionRepository routeDefinitionRepository;
    
    @MockBean
    private ReactiveRedisTemplate<String, RouteDefinition> reactiveRedisTemplate;
    
    @MockBean
    private ReactiveRedisTemplate<String, String> stringReactiveRedisTemplate;
    
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("platform.security.jwt.secret", () -> "testsecretkeytestsecretkeytestsecretkey");
        registry.add("platform.security.jwt.expiration", () -> 3600);
    }
    
    @Test
    void shouldReturnUnauthorizedForSecuredEndpoints() {
        webTestClient.get().uri("/api/secured")
                .exchange()
                .expectStatus().isUnauthorized();
    }
    
    @Test
    void shouldAllowAccessToPublicEndpoints() {
        webTestClient.get().uri("/api/public")
                .exchange()
                .expectStatus().isOk();
    }
    
    @Test
    void shouldReturnValidResponseWithToken() {
        // 创建测试用JWT令牌
        String token = Jwts.builder()
                .setSubject("user1")
                .claim("username", "testuser")
                .claim("roles", "USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor("testsecretkeytestsecretkeytestsecretkey".getBytes(StandardCharsets.UTF_8)))
                .compact();
        
        webTestClient.get().uri("/api/secured")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk();
    }
}
```

## 最佳实践

### API网关设计最佳实践

1. **路由设计原则**：
   - 采用业务域名划分路由，如`/auth/**`, `/governance/**`
   - 避免过于细粒度的路由拆分，降低维护复杂度
   - 使用一致的命名约定，便于识别和管理

2. **安全实践**：
   - 实现分层安全防护：网络层、应用层、数据层
   - 定期轮换密钥和证书
   - 启用请求限流，防止DoS攻击
   - 实施最小权限原则，精细化管理API权限

3. **性能优化**：
   - 启用响应压缩，减少网络传输开销
   - 合理配置连接池和超时参数
   - 使用异步非阻塞模型处理请求
   - 实施缓存策略，减少重复处理

4. **可观察性**：
   - 实现统一的日志格式和标准
   - 部署分布式追踪，跟踪请求流转
   - 建立API调用指标监控
   - 配置合理的告警阈值和策略

### 开发与部署流程

1. **开发流程**：
   - 规范Git提交信息，遵循约定式提交规范
   - 实施代码审查，确保代码质量
   - 编写全面的单元测试和集成测试
   - 使用静态代码分析工具检查代码质量

2. **部署流程**：
   - 采用蓝绿部署或金丝雀发布，降低部署风险
   - 实施自动化部署流程，减少人为错误
   - 部署前自动化测试，确保功能正常
   - 配置健康检查和就绪探针，保障服务稳定性

3. **运维管理**：
   - 建立完善的监控和告警机制
   - 制定故障应对预案和回滚策略
   - 定期进行容量规划和性能测试
   - 保持文档的及时更新和准确性

## 常见问题排查

### 1. 路由转发失败

问题：请求无法正确路由到目标服务

排查步骤：
- 检查路由配置是否正确
- 验证目标服务是否正常运行
- 查看日志中的路由断言和过滤器执行情况
- 确认服务注册信息是否正确

解决方案：
- 修正路由配置
- 重启目标服务
- 刷新路由定义
- 检查服务发现配置

### 2. 性能问题

问题：API网关响应缓慢

排查步骤：
- 检查网关服务资源使用情况（CPU、内存、网络）
- 分析请求处理时间分布
- 查看下游服务响应时间
- 检查连接池配置和使用情况

解决方案：
- 增加网关实例数量
- 优化连接池参数
- 调整超时设置
- 实施缓存策略
- 优化下游服务性能

### 3. 认证授权问题

问题：认证失败或权限不足

排查步骤：
- 检查请求中的认证信息
- 验证JWT令牌的有效性和完整性
- 查看令牌解析过程中的错误
- 检查权限配置

解决方案：
- 更新JWT密钥
- 修正权限配置
- 刷新用户权限缓存
- 检查令牌颁发逻辑

## 未来演进方向

1. **服务网格集成**：
   - 与Istio等服务网格集成，增强流量管理能力
   - 实现更细粒度的服务治理
   - 统一安全策略管理

2. **API管理增强**：
   - 开发API门户，提供自服务API管理
   - 集成API文档生成和测试功能
   - 实现API版本管理和生命周期控制

3. **高级安全特性**：
   - 集成API密钥管理
   - 实现零信任安全模型
   - 增强威胁检测和防护能力
   - 集成Web应用防火墙（WAF）功能

4. **智能路由和自适应流量控制**：
   - 基于AI的流量分析和异常检测
   - 智能负载均衡和流量分配
   - 自适应熔断和限流策略

## 总结

`platform-gateway` 作为企业级数据平台的核心入口点，基于Spring Cloud Gateway实现了强大的API网关功能。它提供了请求路由转发、认证授权、流量控制、日志记录和监控等关键能力，保障了整个平台的安全性、可靠性和可扩展性。

该模块采用六边形架构和响应式编程模型，充分利用非阻塞IO和事件驱动设计，能够高效处理并发请求。通过与Nacos服务注册中心和配置中心的集成，实现了服务自动发现和动态配置管理。同时，完善的监控和追踪机制确保了系统的可观察性，便于问题排查和性能优化。

通过容器化部署和Kubernetes编排，`platform-gateway` 能够灵活扩展，适应各种流量负载。完备的测试策略和CI/CD流程，保障了系统的质量和稳定性。随着未来技术的发展，该模块将持续演进，增强API管理能力，深化服务网格集成，并实现更智能的流量控制。
