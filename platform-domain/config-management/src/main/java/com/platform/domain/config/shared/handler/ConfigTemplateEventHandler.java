package com.platform.domain.config.shared.handler;

import com.platform.domain.config.template.event.ConfigTemplateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 配置模板事件处理器
 * 处理配置模板相关的领域事件
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Component
public class ConfigTemplateEventHandler implements DomainEventHandler<ConfigTemplateEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigTemplateEventHandler.class);
    
    @Override
    @EventListener
    public void handle(ConfigTemplateEvent event) {
        logger.info("处理配置模板事件: {}", event);
        
        switch (event.getEventType()) {
            case "TEMPLATE_CREATED" -> handleTemplateCreated(event);
            case "TEMPLATE_PUBLISHED" -> handleTemplatePublished(event);
            case "TEMPLATE_DEACTIVATED" -> handleTemplateDeactivated(event);
            case "TEMPLATE_CONTENT_UPDATED" -> handleTemplateContentUpdated(event);
            case "TEMPLATE_VERSION_CREATED" -> handleTemplateVersionCreated(event);
            default -> logger.info("未处理的模板事件类型: {}", event.getEventType());
        }
    }
    
    @Override
    public Class<ConfigTemplateEvent> getEventType() {
        return ConfigTemplateEvent.class;
    }
    
    @Override
    public boolean isAsync() {
        return true;
    }
    
    /**
     * 处理模板创建事件
     */
    private void handleTemplateCreated(ConfigTemplateEvent event) {
        logger.info("配置模板已创建: 模板ID={}, 模板名称={}, 操作人={}", 
                   event.getAggregateId(), event.getTemplateName(), event.getOperator());
        
        // 发送通知给相关人员
        sendNotification("模板创建通知", 
                        String.format("用户 %s 创建了新的配置模板: %s", event.getOperator(), event.getTemplateName()));
        
        // 记录审计日志
        recordAuditLog(event, "配置模板创建");
    }
    
    /**
     * 处理模板发布事件
     */
    private void handleTemplatePublished(ConfigTemplateEvent event) {
        logger.info("配置模板已发布: 模板ID={}, 模板名称={}, 操作人={}", 
                   event.getAggregateId(), event.getTemplateName(), event.getOperator());
        
        // 发送重要通知
        sendImportantNotification("模板发布通知", 
                                String.format("配置模板 %s 已发布，可以使用", event.getTemplateName()));
        
        // 更新缓存
        updateTemplateCache(event);
        
        // 记录审计日志
        recordAuditLog(event, "配置模板发布");
    }
    
    /**
     * 处理模板停用事件
     */
    private void handleTemplateDeactivated(ConfigTemplateEvent event) {
        logger.info("配置模板已停用: 模板ID={}, 模板名称={}, 操作人={}", 
                   event.getAggregateId(), event.getTemplateName(), event.getOperator());
        
        // 发送警告通知
        sendWarningNotification("模板停用警告", 
                               String.format("配置模板 %s 已被停用，请检查依赖系统", event.getTemplateName()));
        
        // 清理缓存
        clearTemplateCache(event);
        
        // 记录审计日志
        recordAuditLog(event, "配置模板停用");
    }
    
    /**
     * 处理模板内容更新事件
     */
    private void handleTemplateContentUpdated(ConfigTemplateEvent event) {
        logger.info("配置模板内容已更新: 模板ID={}, 模板名称={}, 操作人={}", 
                   event.getAggregateId(), event.getTemplateName(), event.getOperator());
        
        // 记录审计日志
        recordAuditLog(event, "配置模板内容更新");
    }
    
    /**
     * 处理模板版本创建事件
     */
    private void handleTemplateVersionCreated(ConfigTemplateEvent event) {
        if (event instanceof ConfigTemplateEvent.ConfigTemplateVersionCreatedEvent versionEvent) {
            logger.info("配置模板新版本已创建: 模板ID={}, 模板名称={}, 版本={}, 操作人={}", 
                       event.getAggregateId(), event.getTemplateName(), 
                       versionEvent.getVersion(), event.getOperator());
            
            // 发送版本通知
            sendNotification("模板版本创建通知", 
                           String.format("配置模板 %s 创建了新版本: %s", 
                                       event.getTemplateName(), versionEvent.getVersion()));
            
            // 记录审计日志
            recordAuditLog(event, "配置模板版本创建");
        }
    }
    
    /**
     * 发送通知
     */
    @Async
    protected void sendNotification(String title, String content) {
        logger.info("发送通知: {} - {}", title, content);
        // 实际实现中可以集成邮件、短信、企微等通知渠道
    }
    
    /**
     * 发送重要通知
     */
    @Async
    protected void sendImportantNotification(String title, String content) {
        logger.warn("发送重要通知: {} - {}", title, content);
        // 实际实现中可以增加通知级别和多渠道推送
    }
    
    /**
     * 发送警告通知
     */
    @Async
    protected void sendWarningNotification(String title, String content) {
        logger.error("发送警告通知: {} - {}", title, content);
        // 实际实现中可以增加警告级别处理
    }
    
    /**
     * 更新模板缓存
     */
    @Async
    protected void updateTemplateCache(ConfigTemplateEvent event) {
        logger.info("更新模板缓存: {}", event.getAggregateId());
        // 实际实现中可以更新Redis缓存或本地缓存
    }
    
    /**
     * 清理模板缓存
     */
    @Async
    protected void clearTemplateCache(ConfigTemplateEvent event) {
        logger.info("清理模板缓存: {}", event.getAggregateId());
        // 实际实现中可以清理相关缓存
    }
    
    /**
     * 记录审计日志
     */
    @Async
    protected void recordAuditLog(ConfigTemplateEvent event, String operation) {
        logger.info("记录审计日志: 操作={}, 事件ID={}, 聚合根ID={}, 操作人={}", 
                   operation, event.getEventId(), event.getAggregateId(), event.getOperator());
        // 实际实现中可以写入审计日志表
    }
} 