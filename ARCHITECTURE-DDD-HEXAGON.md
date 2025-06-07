# 🏛️ 企业级数据平台 - DDD六边形架构设计

## 📋 架构概述

基于领域驱动设计(DDD)和六边形架构的企业级数据平台，实现业务逻辑与基础设施的完全解耦，支持插件式外部系统集成。

## 🎯 DDD六边形架构模式

### 🔍 核心概念
```
      外部世界                适配器层              应用层              领域层
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│                 │    │                 │    │                 │    │                 │
│  Web前端        │◄──►│  Web适配器      │◄──►│  应用服务       │◄──►│  领域模型       │
│  消息队列       │◄──►│  消息适配器     │◄──►│  命令处理       │◄──►│  领域服务       │
│  外部API        │◄──►│  HTTP适配器     │◄──►│  查询处理       │◄──►│  仓储接口       │
│  数据库         │◄──►│  持久化适配器   │◄──►│  事件处理       │    │                 │
│                 │    │                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🏗️ 新的模块结构

### 📦 DDD分层模块设计
```
platform-parent/
├── platform-domain/                    # 领域层 🆕
│   ├── platform-collect-domain/        # 采集领域
│   ├── platform-compute-domain/        # 计算领域
│   ├── platform-schedule-domain/       # 调度领域
│   └── platform-audit-domain/          # 审计领域
├── platform-application/               # 应用层 🆕
│   ├── platform-collect-app/           # 采集应用服务
│   ├── platform-compute-app/           # 计算应用服务
│   ├── platform-schedule-app/          # 调度应用服务
│   └── platform-external-api-app/      # 外部API应用服务 🆕
├── platform-adapter/                   # 适配器层 🆕
│   ├── platform-web-adapter/           # Web适配器
│   ├── platform-persistence-adapter/   # 持久化适配器
│   ├── platform-messaging-adapter/     # 消息适配器
│   └── platform-external-adapter/      # 外部系统适配器
└── platform-starter-suite/             # 启动器套件 🆕
    ├── platform-starter-web/           # Web启动器
    ├── platform-starter-mybatis/       # MyBatis启动器
    ├── platform-starter-rocketmq/      # RocketMQ启动器
    ├── platform-starter-external-api/  # 外部API启动器
    └── platform-starter-messaging/     # 消息队列启动器
```

## 🎯 领域模型设计

### 1. 数据采集领域
```java
// 聚合根 - 采集任务
@Entity
public class CollectTask extends AggregateRoot<CollectTaskId> {
    private CollectTaskId id;
    private DataSource dataSource;
    private CollectionStrategy strategy;
    private CollectionStatus status;
    private VersionControl versionControl;
    
    // 领域行为
    public void startCollection() {
        if (!this.status.canStart()) {
            throw new CollectionException("任务状态不允许启动");
        }
        this.status = CollectionStatus.RUNNING;
        this.publishEvent(new CollectionStartedEvent(this.id));
    }
}

// 领域服务
@DomainService
public class CollectionOrchestrator {
    public void orchestrateCollection(CollectTask task) {
        if (task.requiresVersionControl()) {
            task.handleFullCollection();
        } else {
            task.handleIncrementalCollection();
        }
    }
}
```

### 2. 计算处理领域
```java
@Entity
public class ComputeTask extends AggregateRoot<ComputeTaskId> {
    private ComputeTaskId id;
    private ComputeType type;  // SINGLE, COMBINE
    private List<DataInput> inputs;
    private ComputeRule rule;
    
    public ComputeResult executeSingleCompute() {
        if (this.type != ComputeType.SINGLE) {
            throw new ComputeException("计算类型不匹配");
        }
        return this.rule.applySingleCompute(this.inputs);
    }
    
    public ComputeResult executeCombineCompute() {
        return this.rule.applyCombineCompute(this.inputs);
    }
}
```

## 🔌 插件化外部组件设计

### RocketMQ多厂商支持
```java
// 多厂商适配器接口
public interface RocketMQAdapter {
    void sendMessage(String topic, Object message);
    void subscribe(String topic, MessageListener listener);
}

// 阿里云实现
@Component
@ConditionalOnProperty(prefix = "platform.rocketmq", name = "vendor", havingValue = "aliyun")
public class AliyunRocketMQAdapter implements RocketMQAdapter {
    // 阿里云特定实现
}

// 华为云实现
@Component
@ConditionalOnProperty(prefix = "platform.rocketmq", name = "vendor", havingValue = "huawei")
public class HuaweiRocketMQAdapter implements RocketMQAdapter {
    // 华为云特定实现
}
```

### 启动器管理机制
```java
@AutoConfiguration
public class PlatformComponentAutoConfiguration {
    
    @Bean
    @ConditionalOnProperty(prefix = "platform.rocketmq", name = "enabled")
    public RocketMQTemplate rocketMQTemplate(RocketMQProperties properties) {
        return new RocketMQTemplate(properties);
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "platform.external-api", name = "enabled")
    public ExternalApiClient externalApiClient() {
        return ExternalApiClientFactory.create();
    }
}
```

## 🌐 外部HTTP接口管理服务

### platform-external-api-app 设计
```java
// 外部API聚合根
@Entity
public class ExternalApiEndpoint extends AggregateRoot<ApiEndpointId> {
    private String name;
    private String baseUrl;
    private AuthenticationConfig auth;
    private RateLimitConfig rateLimit;
    private CircuitBreakerConfig circuitBreaker;
    
    public ApiResponse callApi(ApiOperation operation, Object request) {
        this.rateLimit.checkLimit();
        
        if (this.circuitBreaker.isOpen()) {
            throw new CircuitBreakerOpenException();
        }
        
        try {
            return operation.execute(request);
        } catch (Exception e) {
            this.circuitBreaker.recordFailure();
            throw e;
        }
    }
}

// 应用服务
@ApplicationService
public class ExternalApiService {
    
    @UseCase
    public ApiResponse callExternalApi(CallApiCommand command) {
        ExternalApiEndpoint endpoint = apiRepository.findById(command.getEndpointId());
        
        ApiCallLog log = ApiCallLog.start(command);
        try {
            ApiResponse response = endpoint.callApi(command.getOperation(), command.getRequest());
            log.recordSuccess(response);
            return response;
        } catch (Exception e) {
            log.recordFailure(e);
            throw e;
        } finally {
            apiCallLogger.log(log);
        }
    }
}
```

## 📨 消息队列隔离架构

### 消息接收与处理分离
```java
// 消息接收器 - 纯接收
@Component
public class MessageReceiver {
    
    @RabbitListener(queues = "business.queue")
    public void receiveFromRabbitMQ(String message) {
        MessageEnvelope envelope = MessageEnvelope.builder()
            .source("rabbitmq")
            .content(message)
            .receivedAt(Instant.now())
            .build();
        messageBuffer.buffer(envelope);
    }
    
    @KafkaListener(topics = "data.stream")
    public void receiveFromKafka(String message) {
        MessageEnvelope envelope = MessageEnvelope.builder()
            .source("kafka")
            .content(message)
            .receivedAt(Instant.now())
            .build();
        messageBuffer.buffer(envelope);
    }
}

// 消息处理器 - 专门处理业务
@Component
public class MessageProcessor {
    
    @Scheduled(fixedDelay = 1000)
    public void processMessages() {
        List<MessageEnvelope> messages = messageBuffer.drain(100);
        
        for (MessageEnvelope message : messages) {
            try {
                processMessage(message);
            } catch (Exception e) {
                handleProcessingError(message, e);
            }
        }
    }
}
```

## 🗃️ MyBatis增强配置

### MyBatis + MyBatis-Plus 集成
```java
@AutoConfiguration
public class PlatformMyBatisAutoConfiguration {
    
    @Bean
    @ConditionalOnProperty(prefix = "platform.mybatis", name = "plus-enabled", havingValue = "true")
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        // 乐观锁插件
        OptimisticLockerInnerInterceptor optimisticLockerInterceptor = new OptimisticLockerInnerInterceptor();
        interceptor.addInnerInterceptor(optimisticLockerInterceptor);
        
        return interceptor;
    }
}
```

### 仓储模式实现
```java
// 仓储接口 - 领域层
public interface CollectTaskRepository {
    CollectTask findById(CollectTaskId id);
    void save(CollectTask task);
    List<CollectTask> findByStatus(CollectionStatus status);
}

// 仓储实现 - 基础设施层
@Repository
public class MyBatisCollectTaskRepository implements CollectTaskRepository {
    
    private final CollectTaskMapper mapper;
    
    @Override
    public CollectTask findById(CollectTaskId id) {
        CollectTaskDO taskDO = mapper.selectById(id.getValue());
        return converter.toDomain(taskDO);
    }
    
    @Override
    public void save(CollectTask task) {
        CollectTaskDO taskDO = converter.toDO(task);
        if (taskDO.getId() == null) {
            mapper.insert(taskDO);
        } else {
            mapper.updateById(taskDO);
        }
    }
}
```

## 📋 编码规范集成

### Excel规则集转换
```java
@Component
public class CodingRuleChecker {
    
    private final List<CodingRule> rules;
    
    public CodingRuleChecker() {
        this.rules = loadRulesFromExcel();
    }
    
    private List<CodingRule> loadRulesFromExcel() {
        ExcelRuleLoader loader = new ExcelRuleLoader();
        return loader.loadRules("classpath:coding-rules.xlsx");
    }
    
    public ValidationResult validateCode(String className, String methodName, Object... params) {
        ValidationResult result = new ValidationResult();
        for (CodingRule rule : rules) {
            if (rule.isApplicable(className, methodName)) {
                result.addResult(rule.check(className, methodName, params));
            }
        }
        return result;
    }
}

// 切面检查
@Aspect
@Component
public class CodingRuleAspect {
    
    @Around("@annotation(CheckCodingRule)")
    public Object checkCodingRule(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        ValidationResult result = ruleChecker.validateCode(className, methodName, joinPoint.getArgs());
        
        if (!result.isValid()) {
            throw new CodingRuleViolationException(result.getViolations());
        }
        
        return joinPoint.proceed();
    }
}
```

## 🎨 设计模式应用

### 1. 工厂模式 - 外部组件创建
```java
public interface ExternalSystemFactory {
    MessageAdapter createMessageAdapter();
    ApiClient createApiClient();
}

@Component
public class AliyunSystemFactory implements ExternalSystemFactory {
    @Override
    public MessageAdapter createMessageAdapter() {
        return new AliyunRocketMQAdapter();
    }
    
    @Override
    public ApiClient createApiClient() {
        return new AliyunApiClient();
    }
}
```

### 2. 策略模式 - 采集策略
```java
public interface CollectionStrategy {
    CollectionResult execute(DataSource dataSource);
}

@Component("fullCollectionStrategy")
public class FullCollectionStrategy implements CollectionStrategy {
    @Override
    public CollectionResult execute(DataSource dataSource) {
        // 全量采集逻辑
        return new CollectionResult();
    }
}

@Component("incrementalCollectionStrategy")
public class IncrementalCollectionStrategy implements CollectionStrategy {
    @Override
    public CollectionResult execute(DataSource dataSource) {
        // 增量采集逻辑
        return new CollectionResult();
    }
}
```

### 3. 适配器模式 - 外部系统集成
```java
public interface MessageSender {
    void send(String topic, Object message);
}

@Component
public class RocketMQSenderAdapter implements MessageSender {
    @Override
    public void send(String topic, Object message) {
        rocketMQTemplate.convertAndSend(topic, message);
    }
}

@Component
public class KafkaSenderAdapter implements MessageSender {
    @Override
    public void send(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }
}
```

## 🚀 快速开始

### 1. 创建领域模型
```bash
# 创建采集领域
mkdir -p platform-domain/platform-collect-domain/src/main/java/com/platform/collect/domain

# 创建应用服务
mkdir -p platform-application/platform-collect-app/src/main/java/com/platform/collect/application

# 创建适配器
mkdir -p platform-adapter/platform-web-adapter/src/main/java/com/platform/adapter/web
```

### 2. 配置启动器
```yaml
# application.yml
platform:
  components:
    enabled: true
  rocketmq:
    enabled: true
    vendor: aliyun  # aliyun, huawei, self-built
  external-api:
    enabled: true
  mybatis:
    plus-enabled: true
    xml-enabled: true
```

### 3. 部署验证
```bash
# 启动基础设施
docker-compose up -d mysql redis nacos rocketmq

# 启动应用
mvn spring-boot:run
```

这个DDD六边形架构设计完全满足您的企业级需求：插件化、消息隔离、设计模式、编码规范等！ 