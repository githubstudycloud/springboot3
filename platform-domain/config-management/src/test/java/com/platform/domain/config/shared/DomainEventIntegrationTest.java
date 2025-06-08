package com.platform.domain.config.shared;

import com.platform.domain.config.template.event.ConfigTemplateEvent;
import com.platform.domain.config.template.valueobject.TemplateId;
import com.platform.domain.config.template.valueobject.TemplateType;
import com.platform.domain.config.approval.event.ConfigApprovalEvent;
import com.platform.domain.config.approval.valueobject.ApprovalId;
import com.platform.domain.config.approval.valueobject.ApprovalType;
import com.platform.domain.config.approval.valueobject.Priority;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * 领域事件集成测试
 * 验证领域事件机制的完整性
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class DomainEventIntegrationTest {
    
    @Autowired
    private DomainEventPublisher domainEventPublisher;
    
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    
    @Test
    public void testConfigTemplateEventPublishing() {
        // Given
        TemplateId templateId = new TemplateId();
        String templateName = "test-template";
        TemplateType templateType = TemplateType.YAML;
        String operator = "test-user";
        
        // When - 发布模板创建事件
        ConfigTemplateEvent.ConfigTemplateCreatedEvent createdEvent = 
            ConfigTemplateEvent.templateCreated(templateId, templateName, templateType, operator);
        
        domainEventPublisher.publish(createdEvent);
        
        // Then - 等待异步处理完成
        await().atMost(5, TimeUnit.SECONDS)
               .untilAsserted(() -> {
                   // 验证事件已被处理
                   assert createdEvent.getEventId() != null;
                   assert createdEvent.getEventType().equals("TEMPLATE_CREATED");
                   assert createdEvent.getAggregateId().equals(templateId.getValue());
               });
    }
    
    @Test
    public void testConfigApprovalEventPublishing() {
        // Given
        ApprovalId approvalId = new ApprovalId();
        String title = "配置变更申请";
        ApprovalType approvalType = ApprovalType.CONFIG_UPDATE;
        Priority priority = Priority.HIGH;
        String applicant = "applicant-user";
        
        // When - 发布审批创建事件
        ConfigApprovalEvent.ConfigApprovalCreatedEvent createdEvent = 
            ConfigApprovalEvent.approvalCreated(approvalId, title, approvalType, priority, applicant);
        
        domainEventPublisher.publish(createdEvent);
        
        // Then - 等待异步处理完成
        await().atMost(5, TimeUnit.SECONDS)
               .untilAsserted(() -> {
                   // 验证事件已被处理
                   assert createdEvent.getEventId() != null;
                   assert createdEvent.getEventType().equals("APPROVAL_CREATED");
                   assert createdEvent.getAggregateId().equals(approvalId.getValue());
               });
    }
    
    @Test
    public void testAsyncEventPublishing() {
        // Given
        TemplateId templateId = new TemplateId();
        String templateName = "async-template";
        String operator = "async-user";
        
        // When - 发布异步事件
        ConfigTemplateEvent.ConfigTemplatePublishedEvent publishedEvent = 
            ConfigTemplateEvent.templatePublished(templateId, templateName, operator);
        
        domainEventPublisher.publishAsync(publishedEvent);
        
        // Then - 验证异步处理
        await().atMost(10, TimeUnit.SECONDS)
               .untilAsserted(() -> {
                   assert publishedEvent.isImportant();
                   assert publishedEvent.isAsyncProcessing();
               });
    }
    
    @Test
    public void testEventInheritance() {
        // Given
        TemplateId templateId = new TemplateId();
        
        // When - 创建具体事件
        ConfigTemplateEvent.ConfigTemplatePublishedEvent event = 
            ConfigTemplateEvent.templatePublished(templateId, "test-template", "test-user");
        
        // Then - 验证继承关系
        assert event instanceof ConfigTemplateEvent;
        assert event instanceof AbstractDomainEvent;
        assert event instanceof DomainEvent;
        
        // 验证重要事件标识
        assert event.isImportant();
        assert event.getEventSource().equals("config-management-domain");
    }
    
    @Test
    public void testEventDataIntegrity() {
        // Given
        ApprovalId approvalId = new ApprovalId();
        String title = "数据完整性测试";
        String approver = "approver-user";
        String reason = "测试拒绝原因";
        
        // When - 创建拒绝事件
        ConfigApprovalEvent.ConfigApprovalRejectedEvent rejectedEvent = 
            ConfigApprovalEvent.approvalRejected(approvalId, title, approver, reason);
        
        // Then - 验证事件数据完整性
        assert rejectedEvent.getEventId() != null;
        assert rejectedEvent.getEventType().equals("APPROVAL_REJECTED");
        assert rejectedEvent.getAggregateId().equals(approvalId.getValue());
        assert rejectedEvent.getAggregateType().equals("ConfigApproval");
        assert rejectedEvent.getTitle().equals(title);
        assert rejectedEvent.getOperator().equals(approver);
        assert rejectedEvent.getReason().equals(reason);
        assert rejectedEvent.getOccurredAt() != null;
        assert rejectedEvent.isImportant();
    }
} 