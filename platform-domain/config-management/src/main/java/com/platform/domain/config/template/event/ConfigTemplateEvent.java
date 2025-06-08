package com.platform.domain.config.template.event;

import com.platform.domain.config.template.valueobject.TemplateId;
import com.platform.domain.config.template.valueobject.TemplateType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 配置模板领域事件
 * DDD领域事件 - 记录配置模板的重要业务变化
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public abstract class ConfigTemplateEvent {
    
    private final TemplateId templateId;
    private final String templateName;
    private final String eventType;
    private final String operator;
    private final LocalDateTime occurredAt;
    
    protected ConfigTemplateEvent(TemplateId templateId, String templateName, 
                                String eventType, String operator) {
        this.templateId = templateId;
        this.templateName = templateName;
        this.eventType = eventType;
        this.operator = operator;
        this.occurredAt = LocalDateTime.now();
    }
    
    // 模板创建事件
    public static ConfigTemplateCreatedEvent templateCreated(TemplateId templateId, String templateName, 
                                                           TemplateType type, String operator) {
        return new ConfigTemplateCreatedEvent(templateId, templateName, type, operator);
    }
    
    // 模板内容更新事件
    public static ConfigTemplateContentUpdatedEvent templateContentUpdated(TemplateId templateId, 
                                                                          String templateName, String operator) {
        return new ConfigTemplateContentUpdatedEvent(templateId, templateName, operator);
    }
    
    // 模板变量添加事件
    public static ConfigTemplateVariableAddedEvent templateVariableAdded(TemplateId templateId, 
                                                                        String templateName, String variableName) {
        return new ConfigTemplateVariableAddedEvent(templateId, templateName, variableName);
    }
    
    // 模板变量移除事件
    public static ConfigTemplateVariableRemovedEvent templateVariableRemoved(TemplateId templateId, 
                                                                            String templateName, String variableName) {
        return new ConfigTemplateVariableRemovedEvent(templateId, templateName, variableName);
    }
    
    // 模板发布事件
    public static ConfigTemplatePublishedEvent templatePublished(TemplateId templateId, 
                                                               String templateName, String operator) {
        return new ConfigTemplatePublishedEvent(templateId, templateName, operator);
    }
    
    // 模板停用事件
    public static ConfigTemplateDeactivatedEvent templateDeactivated(TemplateId templateId, 
                                                                    String templateName, String operator) {
        return new ConfigTemplateDeactivatedEvent(templateId, templateName, operator);
    }
    
    // 模板重新激活事件
    public static ConfigTemplateReactivatedEvent templateReactivated(TemplateId templateId, 
                                                                    String templateName, String operator) {
        return new ConfigTemplateReactivatedEvent(templateId, templateName, operator);
    }
    
    // 模板版本创建事件
    public static ConfigTemplateVersionCreatedEvent templateVersionCreated(TemplateId templateId, 
                                                                          String templateName, String version, String operator) {
        return new ConfigTemplateVersionCreatedEvent(templateId, templateName, version, operator);
    }
    
    // Getters
    public TemplateId getTemplateId() { return templateId; }
    public String getTemplateName() { return templateName; }
    public String getEventType() { return eventType; }
    public String getOperator() { return operator; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigTemplateEvent that = (ConfigTemplateEvent) o;
        return Objects.equals(templateId, that.templateId) &&
               Objects.equals(eventType, that.eventType) &&
               Objects.equals(occurredAt, that.occurredAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(templateId, eventType, occurredAt);
    }
    
    @Override
    public String toString() {
        return "ConfigTemplateEvent{" +
                "templateId=" + templateId +
                ", templateName='" + templateName + '\'' +
                ", eventType='" + eventType + '\'' +
                ", operator='" + operator + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
    
    // 具体事件类型
    
    public static class ConfigTemplateCreatedEvent extends ConfigTemplateEvent {
        private final TemplateType type;
        
        public ConfigTemplateCreatedEvent(TemplateId templateId, String templateName, 
                                        TemplateType type, String operator) {
            super(templateId, templateName, "TEMPLATE_CREATED", operator);
            this.type = type;
        }
        
        public TemplateType getType() { return type; }
    }
    
    public static class ConfigTemplateContentUpdatedEvent extends ConfigTemplateEvent {
        public ConfigTemplateContentUpdatedEvent(TemplateId templateId, String templateName, String operator) {
            super(templateId, templateName, "TEMPLATE_CONTENT_UPDATED", operator);
        }
    }
    
    public static class ConfigTemplateVariableAddedEvent extends ConfigTemplateEvent {
        private final String variableName;
        
        public ConfigTemplateVariableAddedEvent(TemplateId templateId, String templateName, String variableName) {
            super(templateId, templateName, "TEMPLATE_VARIABLE_ADDED", "SYSTEM");
            this.variableName = variableName;
        }
        
        public String getVariableName() { return variableName; }
    }
    
    public static class ConfigTemplateVariableRemovedEvent extends ConfigTemplateEvent {
        private final String variableName;
        
        public ConfigTemplateVariableRemovedEvent(TemplateId templateId, String templateName, String variableName) {
            super(templateId, templateName, "TEMPLATE_VARIABLE_REMOVED", "SYSTEM");
            this.variableName = variableName;
        }
        
        public String getVariableName() { return variableName; }
    }
    
    public static class ConfigTemplatePublishedEvent extends ConfigTemplateEvent {
        public ConfigTemplatePublishedEvent(TemplateId templateId, String templateName, String operator) {
            super(templateId, templateName, "TEMPLATE_PUBLISHED", operator);
        }
    }
    
    public static class ConfigTemplateDeactivatedEvent extends ConfigTemplateEvent {
        public ConfigTemplateDeactivatedEvent(TemplateId templateId, String templateName, String operator) {
            super(templateId, templateName, "TEMPLATE_DEACTIVATED", operator);
        }
    }
    
    public static class ConfigTemplateReactivatedEvent extends ConfigTemplateEvent {
        public ConfigTemplateReactivatedEvent(TemplateId templateId, String templateName, String operator) {
            super(templateId, templateName, "TEMPLATE_REACTIVATED", operator);
        }
    }
    
    public static class ConfigTemplateVersionCreatedEvent extends ConfigTemplateEvent {
        private final String version;
        
        public ConfigTemplateVersionCreatedEvent(TemplateId templateId, String templateName, 
                                                String version, String operator) {
            super(templateId, templateName, "TEMPLATE_VERSION_CREATED", operator);
            this.version = version;
        }
        
        public String getVersion() { return version; }
    }
} 