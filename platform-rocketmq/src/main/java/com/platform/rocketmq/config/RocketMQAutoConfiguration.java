package com.platform.rocketmq.config;

import com.platform.rocketmq.consumer.RocketMQConsumerManager;
import com.platform.rocketmq.producer.RocketMQProducerTemplate;
import com.platform.rocketmq.repository.MessageRecordRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * RocketMQ自动配置类
 *
 * @author Platform Team
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.apache.rocketmq.client.apis.ClientServiceProvider")
@ConditionalOnProperty(prefix = "rocketmq", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RocketMQProperties.class)
@ComponentScan(basePackages = "com.platform.rocketmq")
public class RocketMQAutoConfiguration {
    
    /**
     * 消息记录仓库
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageRecordRepository messageRecordRepository(DataSource dataSource) {
        return new MessageRecordRepository(dataSource);
    }
    
    /**
     * 消费者管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public RocketMQConsumerManager rocketMQConsumerManager(
            ApplicationContext applicationContext,
            Environment environment,
            MessageRecordRepository messageRecordRepository) {
        return new RocketMQConsumerManager(applicationContext, environment, messageRecordRepository);
    }
    
    /**
     * 生产者模板
     */
    @Bean
    @ConditionalOnMissingBean
    public RocketMQProducerTemplate rocketMQProducerTemplate(
            RocketMQProperties properties,
            MessageRecordRepository messageRecordRepository) {
        return new RocketMQProducerTemplate(properties, messageRecordRepository);
    }
}