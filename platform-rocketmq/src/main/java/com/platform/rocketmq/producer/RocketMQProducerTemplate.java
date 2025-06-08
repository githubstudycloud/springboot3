package com.platform.rocketmq.producer;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.platform.rocketmq.config.RocketMQProperties;
import com.platform.rocketmq.entity.MessageRecord;
import com.platform.rocketmq.repository.MessageRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.springframework.beans.factory.DisposableBean;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * RocketMQ生产者模板
 * 提供消息发送功能
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Slf4j
public class RocketMQProducerTemplate implements DisposableBean {
    
    private final RocketMQProperties properties;
    private final MessageRecordRepository messageRecordRepository;
    private Producer producer;
    
    public RocketMQProducerTemplate(RocketMQProperties properties,
                                  MessageRecordRepository messageRecordRepository) {
        this.properties = properties;
        this.messageRecordRepository = messageRecordRepository;
    }
    
    @PostConstruct
    public void init() {
        try {
            ClientServiceProvider provider = ClientServiceProvider.loadService();
            
            ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints(properties.getEndpoints())
                .build();
            
            this.producer = provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                .build();
            
            log.info("RocketMQ producer initialized successfully");
        } catch (ClientException e) {
            log.error("Failed to initialize RocketMQ producer", e);
            throw new RuntimeException("Failed to initialize RocketMQ producer", e);
        }
    }
    
    /**
     * 同步发送消息
     *
     * @param topic 主题
     * @param message 消息内容
     * @return 发送结果
     */
    public SendResult send(String topic, Object message) {
        return send(topic, null, message);
    }
    
    /**
     * 同步发送消息（带标签）
     *
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @return 发送结果
     */
    public SendResult send(String topic, String tag, Object message) {
        return send(topic, tag, null, message);
    }
    
    /**
     * 同步发送消息（完整参数）
     *
     * @param topic 主题
     * @param tag 标签
     * @param key 消息键
     * @param message 消息内容
     * @return 发送结果
     */
    public SendResult send(String topic, String tag, String key, Object message) {
        String messageId = IdUtil.fastSimpleUUID();
        
        try {
            // 构建消息
            Message rocketMessage = buildMessage(topic, tag, key, message);
            
            // 记录发送前的消息
            if (properties.getMessageStore().isEnabled()) {
                saveSendRecord(messageId, topic, tag, key, message);
            }
            
            // 发送消息
            SendReceipt receipt = producer.send(rocketMessage);
            
            // 更新发送结果
            if (properties.getMessageStore().isEnabled()) {
                updateSendResult(messageId, true, null);
            }
            
            log.info("Message sent successfully - messageId: {}, topic: {}, tag: {}", 
                receipt.getMessageId(), topic, tag);
            
            return SendResult.success(receipt.getMessageId().toString());
            
        } catch (ClientException e) {
            log.error("Failed to send message - topic: {}, tag: {}", topic, tag, e);
            
            if (properties.getMessageStore().isEnabled()) {
                updateSendResult(messageId, false, e.getMessage());
            }
            
            return SendResult.failure(e.getMessage());
        } catch (IOException e) {
            log.error("IO error while sending message - topic: {}, tag: {}", topic, tag, e);
            
            if (properties.getMessageStore().isEnabled()) {
                updateSendResult(messageId, false, "IO_ERROR: " + e.getMessage());
            }
            
            return SendResult.failure("IO_ERROR: " + e.getMessage());
        }
    }
    
    /**
     * 异步发送消息
     *
     * @param topic 主题
     * @param message 消息内容
     * @return 发送结果Future
     */
    public CompletableFuture<SendResult> sendAsync(String topic, Object message) {
        return sendAsync(topic, null, message);
    }
    
    /**
     * 异步发送消息（带标签）
     *
     * @param topic 主题
     * @param tag 标签
     * @param message 消息内容
     * @return 发送结果Future
     */
    public CompletableFuture<SendResult> sendAsync(String topic, String tag, Object message) {
        return sendAsync(topic, tag, null, message);
    }
    
    /**
     * 异步发送消息（完整参数）
     *
     * @param topic 主题
     * @param tag 标签
     * @param key 消息键
     * @param message 消息内容
     * @return 发送结果Future
     */
    public CompletableFuture<SendResult> sendAsync(String topic, String tag, String key, Object message) {
        String messageId = IdUtil.fastSimpleUUID();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 构建消息
                Message rocketMessage = buildMessage(topic, tag, key, message);
                
                // 记录发送前的消息
                if (properties.getMessageStore().isEnabled()) {
                    saveSendRecord(messageId, topic, tag, key, message);
                }
                
                // 异步发送
                return producer.sendAsync(rocketMessage)
                    .thenApply(receipt -> {
                        if (properties.getMessageStore().isEnabled()) {
                            updateSendResult(messageId, true, null);
                        }
                        
                        log.info("Message sent asynchronously - messageId: {}, topic: {}, tag: {}", 
                            receipt.getMessageId(), topic, tag);
                        
                        return SendResult.success(receipt.getMessageId().toString());
                    })
                    .exceptionally(throwable -> {
                        log.error("Failed to send message asynchronously - topic: {}, tag: {}", 
                            topic, tag, throwable);
                        
                        if (properties.getMessageStore().isEnabled()) {
                            updateSendResult(messageId, false, throwable.getMessage());
                        }
                        
                        return SendResult.failure(throwable.getMessage());
                    })
                    .join();
                    
            } catch (IOException e) {
                log.error("IO error while sending message asynchronously - topic: {}, tag: {}", 
                    topic, tag, e);
                    
                if (properties.getMessageStore().isEnabled()) {
                    updateSendResult(messageId, false, "IO_ERROR: " + e.getMessage());
                }
                
                return SendResult.failure("IO_ERROR: " + e.getMessage());
            }
        });
    }
    
    /**
     * 构建RocketMQ消息
     */
    private Message buildMessage(String topic, String tag, String key, Object content) throws IOException {
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        
        // 序列化消息内容
        byte[] body;
        if (content instanceof String) {
            body = ((String) content).getBytes(StandardCharsets.UTF_8);
        } else {
            body = JSON.toJSONString(content).getBytes(StandardCharsets.UTF_8);
        }
        
        // 构建消息
        var messageBuilder = provider.newMessageBuilder()
            .setTopic(topic)
            .setBody(body);
        
        if (tag != null && !tag.isEmpty()) {
            messageBuilder.setTag(tag);
        }
        
        if (key != null && !key.isEmpty()) {
            messageBuilder.setKeys(key);
        }
        
        return messageBuilder.build();
    }
    
    /**
     * 保存发送记录
     */
    private void saveSendRecord(String messageId, String topic, String tag, String key, Object message) {
        try {
            MessageRecord record = new MessageRecord()
                .setMessageId(messageId)
                .setTopic(topic)
                .setTag(tag)
                .setConsumerGroup(properties.getProducer().getGroup())
                .setMessageKey(key)
                .setReceiveTime(LocalDateTime.now())
                .setConsumeStatus("SENDING")
                .setRetryTimes(0);
            
            if (properties.getMessageStore().isStoreBody()) {
                String body = message instanceof String ? (String) message : JSON.toJSONString(message);
                if (body.length() > properties.getMessageStore().getMaxBodyLength()) {
                    body = body.substring(0, properties.getMessageStore().getMaxBodyLength()) + "...";
                }
                record.setMessageBody(body);
            }
            
            messageRecordRepository.save(record);
        } catch (Exception e) {
            log.warn("Failed to save send record for messageId: {}", messageId, e);
        }
    }
    
    /**
     * 更新发送结果
     */
    private void updateSendResult(String messageId, boolean success, String errorMessage) {
        try {
            messageRecordRepository.updateConsumeStatus(
                messageId,
                success ? "SENT" : "SEND_FAILED",
                success ? null : "SEND_ERROR",
                errorMessage,
                0
            );
        } catch (Exception e) {
            log.warn("Failed to update send result for messageId: {}", messageId, e);
        }
    }
    
    @Override
    public void destroy() {
        if (producer != null) {
            try {
                producer.close();
                log.info("RocketMQ producer closed successfully");
            } catch (IOException e) {
                log.error("Failed to close RocketMQ producer", e);
            }
        }
    }
    
    /**
     * 发送结果
     */
    public static class SendResult {
        private final boolean success;
        private final String messageId;
        private final String errorMessage;
        
        private SendResult(boolean success, String messageId, String errorMessage) {
            this.success = success;
            this.messageId = messageId;
            this.errorMessage = errorMessage;
        }
        
        public static SendResult success(String messageId) {
            return new SendResult(true, messageId, null);
        }
        
        public static SendResult failure(String errorMessage) {
            return new SendResult(false, null, errorMessage);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessageId() {
            return messageId;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}