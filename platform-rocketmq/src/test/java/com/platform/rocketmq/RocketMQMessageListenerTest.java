package com.platform.rocketmq;

import com.platform.rocketmq.annotation.RocketMQMessageListener;
import com.platform.rocketmq.entity.MessageRecord;
import com.platform.rocketmq.exception.MessageProcessException;
import com.platform.rocketmq.listener.MessageListener;
import com.platform.rocketmq.repository.MessageRecordRepository;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * RocketMQ消息监听器测试
 *
 * @author Platform Team
 * @since 1.0.0
 */
@SpringBootTest
@TestPropertySource(properties = {
    "rocketmq.enabled=true",
    "rocketmq.endpoints=localhost:8081",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
@Import(RocketMQMessageListenerTest.TestConfig.class)
public class RocketMQMessageListenerTest {
    
    @Autowired
    private MessageRecordRepository messageRecordRepository;
    
    @Autowired
    private TestMessageListener testMessageListener;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @BeforeEach
    public void setUp() {
        // 清理测试数据
        jdbcTemplate.execute("DELETE FROM message_record");
        testMessageListener.reset();
    }
    
    @Test
    public void testMessageProcessingSuccess() throws Exception {
        // 准备测试数据
        String messageId = "test-message-001";
        TestMessage message = new TestMessage("test-id", "test-data");
        
        // 模拟消息视图
        MessageView messageView = mock(MessageView.class);
        when(messageView.getMessageId()).thenReturn(() -> messageId);
        when(messageView.getTopic()).thenReturn("test-topic");
        when(messageView.getTag()).thenReturn(Optional.of("test-tag"));
        
        // 处理消息
        testMessageListener.onMessage(message, messageView);
        
        // 等待处理完成
        assertTrue(testMessageListener.awaitProcessing(5, TimeUnit.SECONDS));
        
        // 验证处理结果
        assertEquals(1, testMessageListener.getProcessedCount());
        assertNull(testMessageListener.getLastException());
    }
    
    @Test
    public void testMessageProcessingWithRetryableException() {
        // 准备测试数据
        TestMessage message = new TestMessage("error-retry", "test-data");
        MessageView messageView = mock(MessageView.class);
        
        // 处理消息并期望异常
        MessageProcessException exception = assertThrows(
            MessageProcessException.class,
            () -> testMessageListener.onMessage(message, messageView)
        );
        
        // 验证异常
        assertTrue(exception.isNeedRetry());
        assertEquals("PROCESS_ERROR", exception.getErrorCode());
    }
    
    @Test
    public void testMessageProcessingWithNonRetryableException() {
        // 准备测试数据
        TestMessage message = new TestMessage("error-no-retry", "test-data");
        MessageView messageView = mock(MessageView.class);
        
        // 处理消息并期望异常
        MessageProcessException exception = assertThrows(
            MessageProcessException.class,
            () -> testMessageListener.onMessage(message, messageView)
        );
        
        // 验证异常
        assertFalse(exception.isNeedRetry());
        assertEquals("INVALID_DATA", exception.getErrorCode());
    }
    
    @Test
    public void testMessageRecordPersistence() {
        // 创建消息记录
        MessageRecord record = new MessageRecord()
            .setMessageId("test-persist-001")
            .setTopic("test-topic")
            .setTag("test-tag")
            .setConsumerGroup("test-group")
            .setMessageBody("{\"id\":\"test\"}")
            .setConsumeStatus("SUCCESS")
            .setRetryTimes(0)
            .setReceiveTime(java.time.LocalDateTime.now());
        
        // 保存记录
        boolean saved = messageRecordRepository.save(record);
        assertTrue(saved);
        
        // 查询记录
        Optional<MessageRecord> found = messageRecordRepository.findByMessageId("test-persist-001");
        assertTrue(found.isPresent());
        assertEquals("SUCCESS", found.get().getConsumeStatus());
    }
    
    /**
     * 测试配置
     */
    @TestConfiguration
    static class TestConfig {
        
        @Bean
        public MessageRecordRepository messageRecordRepository(DataSource dataSource) {
            return new MessageRecordRepository(dataSource);
        }
    }
    
    /**
     * 测试消息
     */
    public static class TestMessage {
        private String id;
        private String data;
        
        public TestMessage() {}
        
        public TestMessage(String id, String data) {
            this.id = id;
            this.data = data;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getData() {
            return data;
        }
        
        public void setData(String data) {
            this.data = data;
        }
    }
    
    /**
     * 测试消息监听器
     */
    @Component
    @RocketMQMessageListener(
        consumerGroup = "test-consumer-group",
        topic = "test-topic",
        tag = "test-tag"
    )
    public static class TestMessageListener implements MessageListener<TestMessage> {
        
        private final CountDownLatch latch = new CountDownLatch(1);
        private int processedCount = 0;
        private Exception lastException = null;
        
        @Override
        public void onMessage(TestMessage message, MessageView messageView) throws MessageProcessException {
            try {
                if ("error-retry".equals(message.getId())) {
                    throw MessageProcessException.retryable("PROCESS_ERROR", "Simulated retryable error");
                }
                
                if ("error-no-retry".equals(message.getId())) {
                    throw MessageProcessException.nonRetryable("INVALID_DATA", "Simulated non-retryable error");
                }
                
                // 正常处理
                processedCount++;
                
            } finally {
                latch.countDown();
            }
        }
        
        @Override
        public Class<TestMessage> getMessageType() {
            return TestMessage.class;
        }
        
        public boolean awaitProcessing(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }
        
        public void reset() {
            processedCount = 0;
            lastException = null;
        }
        
        public int getProcessedCount() {
            return processedCount;
        }
        
        public Exception getLastException() {
            return lastException;
        }
    }
}