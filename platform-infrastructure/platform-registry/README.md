# platform-registry

## 项目概述

`platform-registry` 是企业级数据平台的服务注册中心模块，基于Nacos实现，为整个平台提供服务注册发现、健康检查和元数据管理等功能。作为微服务架构的核心基础设施组件，它确保服务间的可靠通信和动态伸缩能力。

## 主要职责

1. **服务注册与发现**：管理平台中所有微服务的注册和发现
2. **健康检查**：监控服务实例的健康状态
3. **元数据管理**：存储和管理服务实例的元数据信息
4. **动态服务列表**：提供实时更新的可用服务列表
5. **跨环境隔离**：支持不同环境（开发、测试、生产）的服务隔离

## 技术架构

`platform-registry` 采用基于Nacos的服务注册中心实现，主要分为服务端和客户端两部分：

1. **服务端（Nacos Server）**：
   - 独立部署的Nacos服务器集群
   - 提供服务注册、发现、健康检查的核心功能
   - 支持高可用集群部署

2. **客户端（Nacos Client）**：
   - 集成到各微服务中的客户端组件
   - 负责服务注册和服务发现的客户端逻辑
   - 提供配置变更监听和服务列表缓存

### 系统架构图

```
                                      ┌─────────────────┐
                                      │                 │
                                      │  负载均衡器      │
                                      │  (Nginx/SLB)    │
                                      │                 │
                                      └────────┬────────┘
                                               │
                                               ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│                 │    │                 │    │                 │    │                 │
│  Nacos Server 1 │◄───┤  Nacos Server 2 │◄───┤  Nacos Server 3 │◄───┤  Nacos Server n │
│                 │    │                 │    │                 │    │                 │
└────────┬────────┘    └────────┬────────┘    └────────┬────────┘    └────────┬────────┘
         │                      │                      │                      │
         └──────────────────────┼──────────────────────┼──────────────────────┘
                                │                      │
                    ┌───────────┴──────────┐ ┌─────────┴───────────┐
                    │                      │ │                     │
                    ▼                      ▼ ▼                     ▼
           ┌─────────────────┐    ┌─────────────────┐     ┌─────────────────┐
           │                 │    │                 │     │                 │
           │   微服务 A      │    │   微服务 B      │     │   微服务 C      │
           │ (Nacos Client)  │    │ (Nacos Client)  │     │ (Nacos Client)  │
           │                 │    │                 │     │                 │
           └─────────────────┘    └─────────────────┘     └─────────────────┘
```

### 模块结构

```
platform-registry/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/platform/registry/
│   │   │       ├── config/             # 服务注册配置
│   │   │       │   ├── NacosConfig.java
│   │   │       │   └── NacosProperties.java
│   │   │       ├── health/             # 健康检查组件
│   │   │       │   ├── HealthCheckController.java
│   │   │       │   └── InstanceHealthIndicator.java
│   │   │       ├── listener/           # 事件监听器
│   │   │       │   ├── NacosInstanceListener.java
│   │   │       │   └── ServiceChangeListener.java
│   │   │       ├── util/               # 工具类
│   │   │       │   └── RegistryUtils.java
│   │   │       ├── RegistryApplication.java # 主应用类
│   │   │       └── StarterController.java   # 启动检查控制器
│   │   └── resources/
│   │       ├── application.yml         # 应用配置
│   │       ├── application-dev.yml     # 开发环境配置
│   │       ├── application-test.yml    # 测试环境配置
│   │       ├── application-prod.yml    # 生产环境配置
│   │       ├── logback-spring.xml      # 日志配置
│   │       └── banner.txt              # 应用启动Banner
│   └── test/                           # 测试代码
├── Dockerfile                          # Docker构建文件
├── docker-compose.yml                  # Docker Compose配置
├── nacos-cluster.yml                   # Nacos集群配置
├── pom.xml                             # Maven项目配置
└── README.md                           # 项目说明
```

## 功能特性

### 1. 服务注册与发现

平台中的所有微服务在启动时自动向Nacos注册中心注册，并在运行期间通过Nacos发现其他服务：

```java
@Configuration
@EnableDiscoveryClient
public class NacosConfig {
    
    @Bean
    @ConditionalOnProperty(name = "spring.cloud.nacos.discovery.enabled", matchIfMissing = true)
    public NacosServiceRegistry nacosServiceRegistry(
            NacosDiscoveryProperties nacosDiscoveryProperties,
            NacosRegistration nacosRegistration) {
        return new NacosServiceRegistry(nacosDiscoveryProperties, nacosRegistration);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public DiscoveryClient nacosDiscoveryClient(
            NacosServiceDiscovery serviceDiscovery) {
        return new NacosDiscoveryClient(serviceDiscovery);
    }
    
    @Bean
    @ConditionalOnProperty(name = "platform.registry.enhanced-protection", havingValue = "true")
    public NacosInstanceProtectionFilter instanceProtectionFilter() {
        return new NacosInstanceProtectionFilter();
    }
}
```

客户端配置示例：

```yaml
spring:
  application:
    name: platform-auth-service  # 服务名称
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}  # Nacos服务地址
        namespace: ${NACOS_NAMESPACE:dev}  # 命名空间，用于环境隔离
        group: ${NACOS_GROUP:DEFAULT_GROUP}  # 服务分组
        cluster-name: ${NACOS_CLUSTER_NAME:DEFAULT}  # 集群名称
        metadata:  # 服务元数据
          version: 1.0.0
          tags: auth,security
        weight: 1  # 服务权重，用于负载均衡
        ephemeral: true  # 临时实例（默认）
        ip: ${NACOS_SERVICE_IP:}  # 可选指定IP地址
        port: ${NACOS_SERVICE_PORT:}  # 可选指定端口
```

### 2. 健康检查机制

Nacos提供多种健康检查模式，确保服务状态的准确性：

1. **客户端心跳**：服务实例定期向Nacos服务端发送心跳包
2. **服务端主动探测**：Nacos服务端通过HTTP、TCP等方式探测服务健康状态

自定义健康检查指示器：

```java
@Component
public class InstanceHealthIndicator implements HealthIndicator {
    
    private final NacosDiscoveryProperties properties;
    private final NacosServiceManager nacosServiceManager;
    
    @Autowired
    public InstanceHealthIndicator(
            NacosDiscoveryProperties properties,
            NacosServiceManager nacosServiceManager) {
        this.properties = properties;
        this.nacosServiceManager = nacosServiceManager;
    }
    
    @Override
    public Health health() {
        try {
            NamingService namingService = nacosServiceManager.getNamingService();
            Instance instance = namingService.selectOneHealthyInstance(
                    properties.getService(),
                    properties.getGroup()
            );
            
            if (instance != null) {
                return Health.up()
                        .withDetail("address", instance.getIp() + ":" + instance.getPort())
                        .withDetail("metadata", instance.getMetadata())
                        .build();
            } else {
                return Health.down()
                        .withDetail("reason", "No healthy instance available")
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

### 3. 服务变更监听

服务注册表变化监听，用于实时响应服务变更：

```java
@Component
@Slf4j
public class ServiceChangeListener {
    
    private final NacosDiscoveryProperties properties;
    private final NacosServiceManager nacosServiceManager;
    
    @Autowired
    public ServiceChangeListener(
            NacosDiscoveryProperties properties,
            NacosServiceManager nacosServiceManager) {
        this.properties = properties;
        this.nacosServiceManager = nacosServiceManager;
    }
    
    @PostConstruct
    public void init() {
        try {
            NamingService namingService = nacosServiceManager.getNamingService();
            // 监听所有服务变更
            namingService.subscribe(properties.getService(), 
                                   properties.getGroup(), 
                                   event -> {
                List<Instance> instances = event.getInstances();
                log.info("Service changed, current instances count: {}", instances.size());
                
                // 处理服务变更逻辑
                processServiceChange(instances);
            });
        } catch (Exception e) {
            log.error("Failed to subscribe service change events", e);
        }
    }
    
    private void processServiceChange(List<Instance> instances) {
        // 处理服务实例变化逻辑，如通知其他组件
        // ...
    }
    
    @PreDestroy
    public void destroy() {
        try {
            NamingService namingService = nacosServiceManager.getNamingService();
            namingService.unsubscribe(properties.getService(), 
                                     properties.getGroup(), 
                                     event -> {});
        } catch (Exception e) {
            log.error("Failed to unsubscribe service change events", e);
        }
    }
}
```

### 4. 多环境支持

通过Nacos的命名空间机制实现多环境隔离：

```java
@Configuration
public class NacosEnvironmentConfig implements EnvironmentAware {
    
    private Environment environment;
    
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    @Bean
    @Primary
    @ConditionalOnProperty(name = "platform.registry.auto-namespace", havingValue = "true", matchIfMissing = true)
    public NacosDiscoveryProperties nacosDiscoveryPropertiesCustomizer(NacosDiscoveryProperties properties) {
        String activeProfile = getActiveProfile();
        
        // 根据激活的环境设置命名空间
        if (StringUtils.isEmpty(properties.getNamespace())) {
            properties.setNamespace(activeProfile);
        }
        
        // 为元数据添加环境信息
        Map<String, String> metadata = properties.getMetadata();
        metadata.put("environment", activeProfile);
        
        return properties;
    }
    
    private String getActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return activeProfiles.length > 0 ? activeProfiles[0] : "dev";
    }
}
```

## Nacos集群部署

### 集群配置

Nacos集群部署采用3节点及以上的集群配置，确保高可用性：

```yaml
# nacos-cluster.yml
version: '3'
services:
  nacos1:
    image: nacos/nacos-server:${NACOS_VERSION}
    container_name: nacos1
    networks:
      - nacos-network
    volumes:
      - ./cluster-logs/nacos1:/home/nacos/logs
      - ./init.d/custom.properties:/home/nacos/init.d/custom.properties
    ports:
      - "8848:8848"
      - "9848:9848"
      - "9555:9555"
    environment:
      - NACOS_SERVERS=nacos1:8848 nacos2:8848 nacos3:8848
      - NACOS_SERVER_IP=nacos1
      - SPRING_DATASOURCE_PLATFORM=mysql
      - MYSQL_SERVICE_HOST=${MYSQL_HOST}
      - MYSQL_SERVICE_PORT=${MYSQL_PORT}
      - MYSQL_SERVICE_DB_NAME=${MYSQL_DB_NAME}
      - MYSQL_SERVICE_USER=${MYSQL_USER}
      - MYSQL_SERVICE_PASSWORD=${MYSQL_PASSWORD}
      - NACOS_AUTH_ENABLE=true
      - NACOS_AUTH_TOKEN_EXPIRE_SECONDS=18000
      - JVM_XMS=1g
      - JVM_XMX=1g
      - JVM_XMN=512m
      - MODE=cluster

  nacos2:
    image: nacos/nacos-server:${NACOS_VERSION}
    container_name: nacos2
    networks:
      - nacos-network
    volumes:
      - ./cluster-logs/nacos2:/home/nacos/logs
      - ./init.d/custom.properties:/home/nacos/init.d/custom.properties
    ports:
      - "8849:8848"
    environment:
      - NACOS_SERVERS=nacos1:8848 nacos2:8848 nacos3:8848
      - NACOS_SERVER_IP=nacos2
      - SPRING_DATASOURCE_PLATFORM=mysql
      - MYSQL_SERVICE_HOST=${MYSQL_HOST}
      - MYSQL_SERVICE_PORT=${MYSQL_PORT}
      - MYSQL_SERVICE_DB_NAME=${MYSQL_DB_NAME}
      - MYSQL_SERVICE_USER=${MYSQL_USER}
      - MYSQL_SERVICE_PASSWORD=${MYSQL_PASSWORD}
      - NACOS_AUTH_ENABLE=true
      - NACOS_AUTH_TOKEN_EXPIRE_SECONDS=18000
      - JVM_XMS=1g
      - JVM_XMX=1g
      - JVM_XMN=512m
      - MODE=cluster

  nacos3:
    image: nacos/nacos-server:${NACOS_VERSION}
    container_name: nacos3
    networks:
      - nacos-network
    volumes:
      - ./cluster-logs/nacos3:/home/nacos/logs
      - ./init.d/custom.properties:/home/nacos/init.d/custom.properties
    ports:
      - "8850:8848"
    environment:
      - NACOS_SERVERS=nacos1:8848 nacos2:8848 nacos3:8848
      - NACOS_SERVER_IP=nacos3
      - SPRING_DATASOURCE_PLATFORM=mysql
      - MYSQL_SERVICE_HOST=${MYSQL_HOST}
      - MYSQL_SERVICE_PORT=${MYSQL_PORT}
      - MYSQL_SERVICE_DB_NAME=${MYSQL_DB_NAME}
      - MYSQL_SERVICE_USER=${MYSQL_USER}
      - MYSQL_SERVICE_PASSWORD=${MYSQL_PASSWORD}
      - NACOS_AUTH_ENABLE=true
      - NACOS_AUTH_TOKEN_EXPIRE_SECONDS=18000
      - JVM_XMS=1g
      - JVM_XMX=1g
      - JVM_XMN=512m
      - MODE=cluster

  mysql:
    image: mysql:${MYSQL_VERSION}
    container_name: mysql
    networks:
      - nacos-network
    volumes:
      - ./mysql:/var/lib/mysql
      - ./init-db:/docker-entrypoint-initdb.d
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - MYSQL_DATABASE=${MYSQL_DB_NAME}
      - MYSQL_USER=${MYSQL_USER}
      - MYSQL_PASSWORD=${MYSQL_PASSWORD}

networks:
  nacos-network:
    driver: bridge
```

### 客户端配置

客户端需要配置集群地址列表，确保高可用：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        # 配置Nacos集群地址，多个地址用逗号分隔
        server-addr: ${NACOS_SERVER_ADDR:nacos1:8848,nacos2:8848,nacos3:8848}
        # 集群名称，用于跨集群部署场景
        cluster-name: ${NACOS_CLUSTER_NAME:PLATFORM}
        # 故障转移超时时间
        heart-beat-timeout: 8000
        # 心跳间隔时间
        heart-beat-interval: 3000
```

## 与其他组件集成

### 1. Spring Cloud集成

```java
@SpringBootApplication
@EnableDiscoveryClient
public class PlatformServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PlatformServiceApplication.class, args);
    }
    
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

使用RestTemplate调用服务：

```java
@Service
public class ServiceCaller {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public UserDTO getUserById(String userId) {
        return restTemplate.getForObject("http://platform-user-service/api/users/{id}", 
                                        UserDTO.class, userId);
    }
}
```

### 2. Feign集成

```java
@FeignClient(name = "platform-user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable("id") String userId);
    
    @PostMapping("/api/users")
    UserDTO createUser(@RequestBody CreateUserRequest request);
}
```

使用Feign客户端：

```java
@Service
public class UserService {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    public UserDTO getUserById(String userId) {
        return userServiceClient.getUserById(userId);
    }
    
    public UserDTO createUser(CreateUserRequest request) {
        return userServiceClient.createUser(request);
    }
}
```

### 3. 负载均衡配置

```java
@Configuration
public class LoadBalancerConfig {
    
    @Bean
    public IRule loadBalancerRule() {
        // 使用加权响应时间负载均衡策略
        return new WeightedResponseTimeRule();
    }
    
    @Bean
    @ConditionalOnProperty(name = "platform.registry.prefer-same-zone", havingValue = "true", matchIfMissing = true)
    public IRule zonePreferenceRule() {
        // 优先选择同区域实例
        ZoneAvoidanceRule rule = new ZoneAvoidanceRule();
        rule.setZoneAvoidancePredicate((server) -> false); // 禁用区域回避
        rule.setZoneAvoidanceEnabled(false);
        return rule;
    }
}
```

## 安全配置

Nacos支持基于用户名/密码的安全认证：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        username: ${NACOS_USERNAME:nacos}
        password: ${NACOS_PASSWORD:nacos}
      config:
        username: ${NACOS_USERNAME:nacos}
        password: ${NACOS_PASSWORD:nacos}
```

服务端安全配置：

```properties
# application.properties
nacos.core.auth.enabled=true
nacos.core.auth.system.type=nacos
nacos.core.auth.plugin.nacos.token.secret.key=${NACOS_AUTH_TOKEN_SECRET_KEY}
nacos.core.auth.server.identity.key=${NACOS_AUTH_IDENTITY_KEY}
nacos.core.auth.server.identity.value=${NACOS_AUTH_IDENTITY_VALUE}
```

## 监控与管理

### 1. 实例健康监控

通过Spring Boot Actuator提供健康检查端点：

```java
@Component
public class NacosHealthIndicator implements HealthIndicator {
    
    private final NacosServiceManager nacosServiceManager;
    
    @Autowired
    public NacosHealthIndicator(NacosServiceManager nacosServiceManager) {
        this.nacosServiceManager = nacosServiceManager;
    }
    
    @Override
    public Health health() {
        try {
            NamingService namingService = nacosServiceManager.getNamingService();
            Map<String, Object> details = new HashMap<>();
            
            // 获取服务状态
            String status = namingService.getServerStatus();
            details.put("status", status);
            
            if ("UP".equals(status)) {
                return Health.up().withDetails(details).build();
            } else {
                return Health.down().withDetails(details).build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

### 2. JMX监控

启用JMX监控：

```yaml
spring:
  jmx:
    enabled: true
```

## 性能优化

### 1. 客户端缓存优化

```yaml
spring:
  cloud:
    nacos:
      discovery:
        # 客户端缓存服务列表刷新间隔
        service-list-refresh-interval-seconds: 30
        # 心跳间隔时间
        heart-beat-interval: 5
        # 实例缓存刷新间隔
        naming-load-cache-at-start: true
```

### 2. 连接池优化

```java
@Configuration
public class NacosThreadPoolConfig {
    
    @Bean
    public ThreadPoolTaskExecutor nacosTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("nacos-client-");
        executor.initialize();
        return executor;
    }
}
```

## 部署与运维

### 1. Kubernetes部署

创建Kubernetes配置文件：

```yaml
# nacos-deployment.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: nacos
  namespace: platform-infra
spec:
  serviceName: nacos-headless
  replicas: 3
  selector:
    matchLabels:
      app: nacos
  template:
    metadata:
      labels:
        app: nacos
    spec:
      containers:
        - name: nacos
          image: nacos/nacos-server:latest
          ports:
            - containerPort: 8848
              name: client
            - containerPort: 9848
              name: client-rpc
            - containerPort: 9849
              name: raft-rpc
          env:
            - name: NACOS_REPLICAS
              value: "3"
            - name: MYSQL_SERVICE_HOST
              value: mysql
            - name: MYSQL_SERVICE_PORT
              value: "3306"
            - name: MYSQL_SERVICE_DB_NAME
              value: nacos
            - name: MYSQL_SERVICE_USER
              value: nacos
            - name: MYSQL_SERVICE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: nacos-mysql-secret
                  key: password
            - name: MODE
              value: "cluster"
            - name: NACOS_SERVER_PORT
              value: "8848"
            - name: NACOS_APPLICATION_PORT
              value: "8848"
            - name: SPRING_DATASOURCE_PLATFORM
              value: "mysql"
          volumeMounts:
            - name: nacos-config
              mountPath: /home/nacos/conf
            - name: nacos-logs
              mountPath: /home/nacos/logs
          resources:
            requests:
              memory: "1Gi"
              cpu: "500m"
            limits:
              memory: "2Gi"
              cpu: "1000m"
          livenessProbe:
            httpGet:
              path: /nacos/v1/console/health/liveness
              port: 8848
            initialDelaySeconds: 30
            periodSeconds: 10
            timeoutSeconds: 5
          readinessProbe:
            httpGet:
              path: /nacos/v1/console/health/readiness
              port: 8848
            initialDelaySeconds: 30
            periodSeconds: 10
            timeoutSeconds: 5
      volumes:
        - name: nacos-config
          configMap:
            name: nacos-config
  volumeClaimTemplates:
    - metadata:
        name: nacos-logs
      spec:
        accessModes: [ "ReadWriteOnce" ]
        storageClassName: "standard"
        resources:
          requests:
            storage: 10Gi
---
apiVersion: v1
kind: Service
metadata:
  name: nacos-headless
  namespace: platform-infra
spec:
  clusterIP: None
  ports:
    - port: 8848
      name: server
      targetPort: 8848
    - port: 9848
      name: client-rpc
      targetPort: 9848
    - port: 9849
      name: raft-rpc
      targetPort: 9849
  selector:
    app: nacos
---
apiVersion: v1
kind: Service
metadata:
  name: nacos
  namespace: platform-infra
spec:
  type: ClusterIP
  ports:
    - port: 8848
      name: server
      targetPort: 8848
  selector:
    app: nacos
```

### 2. 运维管理脚本

创建Nacos管理脚本：

```bash
#!/bin/bash
# nacos-manager.sh

# 配置文件
CONFIG_FILE=".nacos.conf"

# 加载配置
if [ -f "$CONFIG_FILE" ]; then
    source "$CONFIG_FILE"
else
    echo "配置文件不存在，创建默认配置"
    echo "NACOS_HOME=/usr/local/nacos" > "$CONFIG_FILE"
    echo "NACOS_ADDR=localhost:8848" >> "$CONFIG_FILE"
    echo "NACOS_USER=nacos" >> "$CONFIG_FILE"
    echo "NACOS_PASSWORD=nacos" >> "$CONFIG_FILE"
    source "$CONFIG_FILE"
fi

# 获取认证令牌
get_token() {
    curl -s -X POST "http://$NACOS_ADDR/nacos/v1/auth/login" \
        -d "username=$NACOS_USER&password=$NACOS_PASSWORD" | \
        grep -o '"accessToken":"[^"]*"' | cut -d':' -f2 | sed 's/"//g'
}

# 列出所有服务
list_services() {
    local token=$(get_token)
    local namespace=$1
    local group=$2
    
    if [ -z "$namespace" ]; then
        namespace="public"
    fi
    
    if [ -z "$group" ]; then
        group="DEFAULT_GROUP"
    fi
    
    curl -s -H "accessToken:$token" \
        "http://$NACOS_ADDR/nacos/v1/ns/service/list?pageNo=1&pageSize=100&namespaceId=$namespace&groupName=$group" | \
        python -m json.tool
}

# 查看服务实例
get_instances() {
    local token=$(get_token)
    local service=$1
    local namespace=$2
    local group=$3
    
    if [ -z "$service" ]; then
        echo "请指定服务名"
        return 1
    fi
    
    if [ -z "$namespace" ]; then
        namespace="public"
    fi
    
    if [ -z "$group" ]; then
        group="DEFAULT_GROUP"
    fi
    
    curl -s -H "accessToken:$token" \
        "http://$NACOS_ADDR/nacos/v1/ns/instance/list?serviceName=$service&namespaceId=$namespace&groupName=$group" | \
        python -m json.tool
}

# 注销服务实例
deregister_instance() {
    local token=$(get_token)
    local service=$1
    local ip=$2
    local port=$3
    local namespace=$4
    local group=$5
    
    if [ -z "$service" ] || [ -z "$ip" ] || [ -z "$port" ]; then
        echo "请指定服务名、IP和端口"
        return 1
    fi
    
    if [ -z "$namespace" ]; then
        namespace="public"
    fi
    
    if [ -z "$group" ]; then
        group="DEFAULT_GROUP"
    fi
    
    curl -s -X DELETE -H "accessToken:$token" \
        "http://$NACOS_ADDR/nacos/v1/ns/instance?serviceName=$service&ip=$ip&port=$port&namespaceId=$namespace&groupName=$group"
    
    echo "已注销服务 $service 的实例 $ip:$port"
}

# 显示帮助信息
show_help() {
    echo "Nacos管理脚本"
    echo "用法: $0 <命令> [参数]"
    echo ""
    echo "命令:"
    echo "  list [namespace] [group]           列出所有服务"
    echo "  instances <service> [namespace] [group]  查看服务实例"
    echo "  deregister <service> <ip> <port> [namespace] [group]  注销服务实例"
    echo "  help                             显示帮助信息"
}

# 主函数
main() {
    local command=$1
    shift
    
    case "$command" in
        list)
            list_services "$@"
            ;;
        instances)
            get_instances "$@"
            ;;
        deregister)
            deregister_instance "$@"
            ;;
        help|*)
            show_help
            ;;
    esac
}

main "$@"
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
    
    <artifactId>platform-registry</artifactId>
    <packaging>jar</packaging>
    <n>Platform Registry</n>
    <description>Service Registry for Enterprise Data Platform</description>
    
    <properties>
        <nacos.version>2.2.3</nacos.version>
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
        
        <!-- Spring Boot 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Spring Cloud 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter</artifactId>
        </dependency>
        
        <!-- Nacos 依赖 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>
        
        <!-- 可选：Nacos Server依赖，仅当内嵌Nacos服务器时使用 -->
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>${nacos.version}</version>
        </dependency>
        
        <!-- 数据库相关，用于持久化（可选） -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        
        <!-- 测试依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
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
                    <repository>${docker.registry}/platform/registry</repository>
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

## 最佳实践

### 服务注册最佳实践

1. **规范命名**：
   - 服务名使用小写字母，用连字符分隔，如`platform-auth-service`
   - 分组名使用大写，如`PLATFORM_GROUP`
   - 避免特殊字符和空格

2. **元数据设计**：
   - 添加版本信息，方便版本路由
   - 添加部署区域，支持同区域优先路由
   - 添加服务责任人，便于问题追溯

3. **健康检查配置**：
   - 设置合理的健康检查超时时间，避免因网络抖动导致服务被误判为不健康
   - 根据服务性质设置心跳间隔，高可用服务使用更短的心跳间隔

4. **分组和命名空间使用**：
   - 使用命名空间隔离环境：dev、test、prod
   - 使用分组隔离业务域：order、user、payment
   - 不要创建过多命名空间，增加管理复杂度

### 安全与运维最佳实践

1. **安全加固**：
   - 启用认证，设置复杂密码
   - 限制IP访问，仅开放必要端口
   - 定期更换密钥

2. **日志管理**：
   - 收集Nacos服务器日志
   - 监控异常登录和操作
   - 设置日志轮转策略，避免磁盘占满

3. **备份策略**：
   - 定期备份Nacos数据
   - 备份配置和元数据
   - 测试恢复流程

4. **容量规划**：
   - 根据服务数量和实例数估算所需资源
   - 监控Nacos服务器CPU、内存和磁盘使用率
   - 适时扩容集群节点

## 常见问题处理

### 1. 服务注册失败

问题：微服务无法成功注册到Nacos

解决方案：
- 检查Nacos服务器是否正常运行
- 验证网络连接和防火墙设置
- 确认客户端配置正确，特别是命名空间和分组
- 检查日志中的具体错误信息

### 2. 服务发现延迟

问题：新注册的服务需要较长时间才能被其他服务发现

解决方案：
- 调整客户端缓存刷新时间
- 检查心跳配置是否合理
- 确保客户端和服务端时间同步
- 适当调整服务保护阈值

### 3. 高可用问题

问题：Nacos集群节点故障导致服务注册和发现问题

解决方案：
- 部署最少3节点的Nacos集群
- 实现集群节点跨可用区部署
- 配置合理的负载均衡和故障转移策略
- 使用持久化存储，避免内存数据丢失

### 4. 性能问题

问题：大规模服务注册导致Nacos性能下降

解决方案：
- 增加Nacos集群节点数
- 优化JVM参数，增加内存分配
- 将MySQL部署为主从架构，提高读性能
- 合理设置心跳间隔和超时时间，减少网络压力

## 扩展功能

### 1. 服务自动发现与注册监控

```java
@Configuration
@EnableScheduling
public class RegistryMonitorConfig {
    
    @Autowired
    private NacosServiceManager nacosServiceManager;
    
    @Value("${spring.cloud.nacos.discovery.namespace:}")
    private String namespace;
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void monitorServices() {
        try {
            NamingService namingService = nacosServiceManager.getNamingService();
            
            // 获取所有服务
            ListView<String> services = namingService.getServicesOfServer(1, 1000, namespace);
            
            for (String service : services.getData()) {
                // 获取每个服务的实例
                List<Instance> instances = namingService.getAllInstances(service);
                
                // 检查实例健康状态
                int totalCount = instances.size();
                long healthyCount = instances.stream().filter(Instance::isHealthy).count();
                
                // 记录服务健康状态
                log.info("Service: {}, Total: {}, Healthy: {}, Health Ratio: {}%", 
                         service, totalCount, healthyCount, 
                         totalCount > 0 ? (healthyCount * 100 / totalCount) : 0);
                
                // 健康比例低于阈值时告警
                if (totalCount > 0 && healthyCount * 100 / totalCount < 80) {
                    log.warn("Service {} health ratio is below threshold!", service);
                    // 触发告警逻辑
                }
            }
        } catch (Exception e) {
            log.error("Error monitoring services", e);
        }
    }
}
```

### 2. 自定义服务权重计算

```java
@Component
public class CustomWeightCalculator {
    
    @Autowired
    private NacosServiceManager nacosServiceManager;
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    @Value("${spring.cloud.nacos.discovery.namespace:}")
    private String namespace;
    
    /**
     * 根据服务实例性能自动调整权重
     */
    public void adjustInstanceWeight(double cpuUsage, double memoryUsage, int concurrentRequests) {
        try {
            // 计算新权重 (0.1-1.0)
            double newWeight = calculateWeight(cpuUsage, memoryUsage, concurrentRequests);
            
            // 获取当前实例
            NamingService namingService = nacosServiceManager.getNamingService();
            List<Instance> instances = namingService.getAllInstances(applicationName, namespace);
            
            // 查找当前实例
            String localIp = InetAddress.getLocalHost().getHostAddress();
            
            for (Instance instance : instances) {
                if (instance.getIp().equals(localIp)) {
                    // 更新权重
                    instance.setWeight(newWeight);
                    namingService.registerInstance(applicationName, namespace, instance);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Failed to adjust instance weight", e);
        }
    }
    
    private double calculateWeight(double cpuUsage, double memoryUsage, int concurrentRequests) {
        // CPU和内存使用率越高，权重越低
        double cpuFactor = Math.max(0.1, 1.0 - cpuUsage);
        double memoryFactor = Math.max(0.1, 1.0 - memoryUsage);
        
        // 并发请求数越多，权重适当降低
        double concurrencyFactor = Math.max(0.1, 1.0 - (concurrentRequests / 1000.0));
        
        // 综合计算权重，权重范围0.1-1.0
        return Math.max(0.1, Math.min(1.0, (cpuFactor * 0.4 + memoryFactor * 0.4 + concurrencyFactor * 0.2)));
    }
}
```

## 总结

`platform-registry` 模块基于 Nacos 实现了一个稳定可靠的服务注册中心，为整个微服务平台提供服务发现、健康检查和元数据管理功能。通过高可用集群部署、合理的配置和优化，确保了平台服务的可靠注册和发现。该模块与其他微服务组件紧密集成，支持多环境部署，满足了企业级应用对服务治理的高要求。

## 未来规划

1. **多注册中心融合**：支持同时接入多种注册中心，如Nacos、Eureka、Consul
2. **全局流量控制**：基于服务注册信息实现全局流量控制和调度
3. **自动扩缩容集成**：与Kubernetes等容器平台集成，实现基于服务健康度的自动扩缩容
4. **AI辅助服务治理**：引入AI算法，优化服务发现和负载均衡策略
