package com.platform.rocketmq.consumer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.platform.rocketmq.annotation.RocketMQMessageListener;
import com.platform.rocketmq.entity.MessageRecord;
import com.platform.rocketmq.exception.MessageProcessException;
import com.platform.rocketmq.listener.MessageListener;
import com.platform.rocketmq.repository.MessageRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.*;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RocketMQ消费者管理器
 * 负责管理所有的消息消费者
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class RocketMQConsumerManager implements DisposableBean {
    
    private final ApplicationContext applicationContext;
    private final Environment environment;
    private final MessageRecordRepository messageRecordRepository;
    private final Map<String, PushConsumer> consumers = new ConcurrentHashMap<>();
    
    @Value("${rocketmq.endpoints:localhost:8081}")
    private String endpoints;
    
    @Value("${rocketmq.consumer.max-cached-message-count:1000}")
    private int maxCachedMessageCount;
    
    @Value("${rocketmq.consumer.consumption-thread-count:20}")
    private int consumptionThreadCount;
    
    public RocketMQConsumerManager(ApplicationContext applicationContext,
                                 Environment environment,
                                 MessageRecordRepository messageRecordRepository) {
        this.applicationContext = applicationContext;
        this.environment = environment;
        this.messageRecordRepository = messageRecordRepository;
    }
    
    @PostConstruct
    public void init() {
        // 扫描所有带有@RocketMQMessageListener注解的Bean
        Map<String, Object> listeners = applicationContext.getBeansWithAnnotation(RocketMQMessageListener.class);
        
        for (Map.Entry<String, Object> entry : listeners.entrySet()) {
            Object bean = entry.getValue();
            if (bean instanceof MessageListener) {
                registerConsumer((MessageListener<?>) bean);
            } else {
                log.warn("Bean {} has @RocketMQMessageListener but doesn't implement MessageListener interface", 
                    entry.getKey());
            }
        }
        
        log.info("Initialized {} RocketMQ consumers", consumers.size());
    }
    
    /**
     * 注册消费者
     *
     * @param listener 消息监听器
     */
    private void registerConsumer(MessageListener<?> listener) {
        RocketMQMessageListener annotation = listener.getClass().getAnnotation(RocketMQMessageListener.class);
        
        // 检查是否启用
        String enabledValue = environment.resolvePlaceholders(annotation.enabled());
        if (!Boolean.parseBoolean(enabledValue)) {
            log.info("Consumer {} is disabled", listener.getClass().getSimpleName());
            return;
        }
        
        try {
            ClientServiceProvider provider = ClientServiceProvider.loadService();
            ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoints)
                .build();
            
            FilterExpression filterExpression = new FilterExpression(
                annotation.tag(), 
                FilterExpressionType.TAG
            );
            
            String consumerGroup = environment.resolvePlaceholders(annotation.consumerGroup());
            String topic = environment.resolvePlaceholders(annotation.topic());
            
            PushConsumer consumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(clientConfiguration)
                .setConsumerGroup(consumerGroup)
                .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                .setMaxCachedMessageCount(maxCachedMessageCount)
                .setConsumptionThreadCount(consumptionThreadCount)
                .setMessageListener(messageView -> processMessage(messageView, listener, annotation))
                .build();
            
            consumers.put(consumerGroup, consumer);
            log.info("Registered consumer for group: {}, topic: {}, tag: {}", 
                consumerGroup, topic, annotation.tag());
            
        } catch (ClientException e) {
            log.error("Failed to create consumer for listener: {}", listener.getClass().getName(), e);
            throw new RuntimeException("Failed to create RocketMQ consumer", e);
        }
    }
    
    /**
     * 处理消息
     *
     * @param messageView 消息视图
     * @param listener 消息监听器
     * @param annotation 注解配置
     * @return 消费结果
     */
    private ConsumeResult processMessage(MessageView messageView, 
                                       MessageListener<?> listener,
                                       RocketMQMessageListener annotation) {
        String messageId = messageView.getMessageId().toString();
        LocalDateTime receiveTime = LocalDateTime.now();
        
        // 创建消息记录
        MessageRecord record = new MessageRecord()
            .setMessageId(messageId)
            .setTopic(messageView.getTopic())
            .setTag(messageView.getTag().orElse(""))
            .setConsumerGroup(annotation.consumerGroup())
            .setMessageKey(String.join(",", messageView.getKeys()))
            .setReceiveTime(receiveTime)
            .setConsumeStatus(MessageRecord.ConsumeStatus.PROCESSING.name())
            .setRetryTimes(0);
        
        try {
            // 获取消息体
            String body = StandardCharsets.UTF_8.decode(messageView.getBody()).toString();
            record.setMessageBody(body);
            
            // 保存接收记录
            messageRecordRepository.save(record);
            
            // 反序列化消息
            Object message = deserializeMessage(body, listener.getMessageType());
            
            // 调用监听器处理消息
            listener.onMessage(message, messageView);
            
            // 更新消费状态为成功
            messageRecordRepository.updateConsumeStatus(
                messageId, 
                MessageRecord.ConsumeStatus.SUCCESS.name(),
                null, 
                null, 
                0
            );
            
            log.info("Successfully consumed message: {}", messageId);
            return ConsumeResult.SUCCESS;
            
        } catch (MessageProcessException e) {
            return handleMessageProcessException(messageId, e, annotation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // 处理业务异常（不需要重试）
            return handleBusinessException(messageId, e);
        } catch (IOException e) {
            // 处理IO异常（需要重试）
            return handleIOException(messageId, e, annotation);
        } catch (NullPointerException e) {
            // 处理空指针异常（记录详细信息但不重试）
            return handleNullPointerException(messageId, e);
        } catch (ClassCastException e) {
            // 处理类型转换异常（不需要重试）
            return handleClassCastException(messageId, e);
        } catch (RuntimeException e) {
            // 处理其他运行时异常
            return handleRuntimeException(messageId, e, annotation);
        } catch (Error e) {
            // 处理严重错误，记录后重新抛出
            log.error("Critical error while processing message: {}", messageId, e);
            throw e;
        }
    }
    
    /**
     * 处理消息处理异常
     */
    private ConsumeResult handleMessageProcessException(String messageId, 
                                                      MessageProcessException e,
                                                      RocketMQMessageListener annotation) {
        log.error("Message processing failed - messageId: {}, errorCode: {}, needRetry: {}", 
            messageId, e.getErrorCode(), e.isNeedRetry(), e);
        
        int currentRetryTimes = getCurrentRetryTimes(messageId);
        
        if (e.isNeedRetry() && currentRetryTimes < annotation.maxRetryTimes()) {
            messageRecordRepository.updateConsumeStatus(
                messageId,
                MessageRecord.ConsumeStatus.RETRY.name(),
                e.getErrorCode(),
                e.getMessage(),
                currentRetryTimes + 1
            );
            return ConsumeResult.FAILURE;
        } else {
            messageRecordRepository.updateConsumeStatus(
                messageId,
                MessageRecord.ConsumeStatus.FAILED.name(),
                e.getErrorCode(),
                e.getMessage(),
                currentRetryTimes
            );
            return ConsumeResult.SUCCESS; // 不再重试
        }
    }
    
    /**
     * 处理业务异常
     */
    private ConsumeResult handleBusinessException(String messageId, RuntimeException e) {
        log.error("Business exception occurred - messageId: {}", messageId, e);
        
        messageRecordRepository.updateConsumeStatus(
            messageId,
            MessageRecord.ConsumeStatus.FAILED.name(),
            "BUSINESS_ERROR",
            e.getMessage(),
            0
        );
        
        return ConsumeResult.SUCCESS; // 业务异常不重试
    }
    
    /**
     * 处理IO异常
     */
    private ConsumeResult handleIOException(String messageId, IOException e, 
                                          RocketMQMessageListener annotation) {
        log.error("IO exception occurred - messageId: {}", messageId, e);
        
        int currentRetryTimes = getCurrentRetryTimes(messageId);
        
        if (currentRetryTimes < annotation.maxRetryTimes()) {
            messageRecordRepository.updateConsumeStatus(
                messageId,
                MessageRecord.ConsumeStatus.RETRY.name(),
                "IO_ERROR",
                e.getMessage(),
                currentRetryTimes + 1
            );
            return ConsumeResult.FAILURE;
        } else {
            messageRecordRepository.updateConsumeStatus(
                messageId,
                MessageRecord.ConsumeStatus.FAILED.name(),
                "IO_ERROR",
                e.getMessage(),
                currentRetryTimes
            );
            return ConsumeResult.SUCCESS;
        }
    }
    
    /**
     * 处理空指针异常
     */
    private ConsumeResult handleNullPointerException(String messageId, NullPointerException e) {
        log.error("Null pointer exception occurred - messageId: {}", messageId, e);
        
        messageRecordRepository.updateConsumeStatus(
            messageId,
            MessageRecord.ConsumeStatus.FAILED.name(),
            "NULL_POINTER",
            "Null value encountered: " + e.getMessage(),
            0
        );
        
        return ConsumeResult.SUCCESS; // 空指针异常不重试
    }
    
    /**
     * 处理类型转换异常
     */
    private ConsumeResult handleClassCastException(String messageId, ClassCastException e) {
        log.error("Class cast exception occurred - messageId: {}", messageId, e);
        
        messageRecordRepository.updateConsumeStatus(
            messageId,
            MessageRecord.ConsumeStatus.FAILED.name(),
            "CLASS_CAST_ERROR",
            e.getMessage(),
            0
        );
        
        return ConsumeResult.SUCCESS; // 类型转换异常不重试
    }
    
    /**
     * 处理其他运行时异常
     */
    private ConsumeResult handleRuntimeException(String messageId, RuntimeException e,
                                               RocketMQMessageListener annotation) {
        log.error("Runtime exception occurred - messageId: {}", messageId, e);
        
        int currentRetryTimes = getCurrentRetryTimes(messageId);
        
        // 对于未知的运行时异常，默认重试
        if (currentRetryTimes < annotation.maxRetryTimes()) {
            messageRecordRepository.updateConsumeStatus(
                messageId,
                MessageRecord.ConsumeStatus.RETRY.name(),
                "RUNTIME_ERROR",
                e.getClass().getSimpleName() + ": " + e.getMessage(),
                currentRetryTimes + 1
            );
            return ConsumeResult.FAILURE;
        } else {
            messageRecordRepository.updateConsumeStatus(
                messageId,
                MessageRecord.ConsumeStatus.FAILED.name(),
                "RUNTIME_ERROR",
                e.getClass().getSimpleName() + ": " + e.getMessage(),
                currentRetryTimes
            );
            return ConsumeResult.SUCCESS;
        }
    }
    
    /**
     * 获取当前重试次数
     */
    private int getCurrentRetryTimes(String messageId) {
        return messageRecordRepository.findByMessageId(messageId)
            .map(MessageRecord::getRetryTimes)
            .orElse(0);
    }
    
    /**
     * 反序列化消息
     */
    private <T> T deserializeMessage(String body, Class<T> clazz) {
        if (String.class.equals(clazz)) {
            return (T) body;
        }
        return JSON.parseObject(body, clazz);
    }
    
    @Override
    public void destroy() {
        log.info("Shutting down RocketMQ consumers...");
        consumers.forEach((group, consumer) -> {
            try {
                consumer.close();
                log.info("Closed consumer for group: {}", group);
            } catch (IOException e) {
                log.error("Failed to close consumer for group: {}", group, e);
            }
        });
    }
}