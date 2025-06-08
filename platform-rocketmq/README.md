# Platform RocketMQ

一个基于Spring Boot的RocketMQ消息工具封装，提供注解驱动的消息监听、完善的异常处理机制和消息持久化功能。

## 特性

- ✅ **注解驱动**：使用`@RocketMQMessageListener`注解轻松定义消息监听器
- ✅ **异常处理**：遵循代码规范，细粒度的异常分类和处理
- ✅ **消息持久化**：自动将消息存储到MySQL，支持消息追踪和审计
- ✅ **自动重试**：支持配置重试次数和重试策略
- ✅ **灵活配置**：支持通过配置文件动态控制消费者行为
- ✅ **监控友好**：详细的日志记录和消息状态跟踪

## 快速开始

### 1. 添加依赖

在您的项目中添加以下依赖：

```xml
<dependency>
    <groupId>com.platform</groupId>
    <artifactId>platform-rocketmq</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置RocketMQ

在`application.yml`中添加配置：

```yaml
rocketmq:
  enabled: true
  endpoints: localhost:8081
  
  producer:
    group: default-producer-group
    send-timeout: 3s
    max-attempts: 3
    
  consumer:
    max-cached-message-count: 1000
    consumption-thread-count: 20
    consume-timeout: 15s
    retry-interval: 30s
    
  message-store:
    enabled: true
    store-body: true
    max-body-length: 65536
    retention-days: 30
    auto-clean: true
    clean-cron: "0 0 2 * * ?"
```

### 3. 创建消息监听器

```java
@Component
@RocketMQMessageListener(
    consumerGroup = "${rocketmq.consumer.order.group:order-consumer-group}",
    topic = "${rocketmq.consumer.order.topic:order-topic}",
    tag = "ORDER_CREATED || ORDER_PAID",
    maxRetryTimes = 5,
    consumeTimeout = 30000
)
public class OrderMessageListener implements MessageListener<OrderMessage> {
    
    @Override
    public void onMessage(OrderMessage message, MessageView messageView) throws MessageProcessException {
        // 处理消息
        try {
            // 业务逻辑
            processOrder(message);
        } catch (IllegalArgumentException e) {
            // 参数错误，不重试
            throw MessageProcessException.nonRetryable("INVALID_PARAM", e.getMessage(), e);
        } catch (Exception e) {
            // 其他错误，需要重试
            throw MessageProcessException.retryable("PROCESS_ERROR", e.getMessage(), e);
        }
    }
    
    @Override
    public Class<OrderMessage> getMessageType() {
        return OrderMessage.class;
    }
}
```

### 4. 发送消息

```java
@Service
public class OrderService {
    
    @Autowired
    private RocketMQProducerTemplate producerTemplate;
    
    public void createOrder(OrderMessage order) {
        // 同步发送
        SendResult result = producerTemplate.send("order-topic", "ORDER_CREATED", order);
        if (result.isSuccess()) {
            log.info("Order message sent: {}", result.getMessageId());
        }
        
        // 异步发送
        CompletableFuture<SendResult> future = producerTemplate.sendAsync(
            "order-topic", "ORDER_CREATED", order
        );
        future.thenAccept(r -> {
            if (r.isSuccess()) {
                log.info("Order message sent asynchronously: {}", r.getMessageId());
            }
        });
    }
}
```

## 异常处理

框架提供了细粒度的异常处理机制，遵循代码规范G.ERR.02：

```java
// 不同类型的异常会被自动分类处理：
- MessageProcessException: 业务处理异常，可控制是否重试
- IllegalArgumentException: 参数异常，不重试
- IllegalStateException: 状态异常，不重试
- IOException: IO异常，自动重试
- NullPointerException: 空指针异常，不重试
- ClassCastException: 类型转换异常，不重试
- RuntimeException: 其他运行时异常，默认重试
```

## 数据库表结构

消息记录会自动存储到`message_record`表中：

```sql
CREATE TABLE message_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(128) NOT NULL UNIQUE,
    topic VARCHAR(255) NOT NULL,
    tag VARCHAR(255),
    consumer_group VARCHAR(255) NOT NULL,
    message_body TEXT,
    message_key VARCHAR(255),
    consume_status VARCHAR(32) NOT NULL,
    retry_times INT DEFAULT 0,
    error_code VARCHAR(64),
    error_message TEXT,
    receive_time TIMESTAMP NOT NULL,
    consume_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_message_id (message_id),
    INDEX idx_status_retry (consume_status, retry_times),
    INDEX idx_create_time (create_time)
);
```

## 消费状态

消息的消费状态包括：
- `PROCESSING`: 处理中
- `SUCCESS`: 消费成功
- `FAILED`: 消费失败（超过最大重试次数）
- `RETRY`: 重试中

## 注解参数说明

`@RocketMQMessageListener`注解支持以下参数：

| 参数 | 说明 | 默认值 |
|------|------|--------|
| consumerGroup | 消费者组 | 必填 |
| topic | 主题 | 必填 |
| tag | 标签，支持表达式 | * |
| consumeMode | 消费模式 | CONCURRENTLY |
| consumeFromWhere | 消费起始位置 | CONSUME_FROM_LAST_OFFSET |
| enabled | 是否启用 | true |
| maxRetryTimes | 最大重试次数 | 3 |
| consumeTimeout | 消费超时时间（毫秒） | 15000 |

## 最佳实践

1. **合理设置重试次数**：根据业务特性设置合适的重试次数
2. **区分异常类型**：明确哪些异常需要重试，哪些不需要
3. **监控消费状态**：定期检查失败的消息，及时处理
4. **消息体大小**：注意控制消息体大小，避免存储压力
5. **幂等性设计**：消费逻辑应该保证幂等性

## 故障排查

1. 查看日志：所有的消息处理都有详细的日志记录
2. 查询数据库：通过`message_record`表查看消息状态
3. 检查配置：确认RocketMQ连接配置正确
4. 网络连通性：确保能够访问RocketMQ服务

## License

Platform Team © 2024