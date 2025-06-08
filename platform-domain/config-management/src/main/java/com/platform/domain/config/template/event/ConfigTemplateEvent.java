package com.platform.domain.config.template.event;

import com.platform.domain.config.shared.AbstractDomainEvent;
import com.platform.domain.config.template.valueobject.TemplateId;
import com.platform.domain.config.template.valueobject.TemplateType;

/**
 * 配置模板领域事件
 * DDD领域事件 - 记录配置模板的重要业务变化
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public abstract class ConfigTemplateEvent extends AbstractDomainEvent {
    
    private final String templateName;
    
    protected ConfigTemplateEvent(String eventType, TemplateId templateId, 
                                String templateName, String operator) {
        super(eventType, templateId.getValue(), "ConfigTemplate", operator);
        this.templateName = templateName;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    @Override
    public boolean isImportant() {
        return getEventType().contains("PUBLISHED") || getEventType().contains("DELETED");
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
    
    // 具体事件类型
    
    public static class ConfigTemplateCreatedEvent extends ConfigTemplateEvent {
        private final TemplateType type;
        
        public ConfigTemplateCreatedEvent(TemplateId templateId, String templateName, 
                                        TemplateType type, String operator) {
            super("TEMPLATE_CREATED", templateId, templateName, operator);
            this.type = type;
        }
        
        public TemplateType getType() { return type; }
    }
    
    public static class ConfigTemplateContentUpdatedEvent extends ConfigTemplateEvent {
        public ConfigTemplateContentUpdatedEvent(TemplateId templateId, String templateName, String operator) {
            super("TEMPLATE_CONTENT_UPDATED", templateId, templateName, operator);
        }
    }
    
    public static class ConfigTemplateVariableAddedEvent extends ConfigTemplateEvent {
        private final String variableName;
        
        public ConfigTemplateVariableAddedEvent(TemplateId templateId, String templateName, String variableName) {
            super("TEMPLATE_VARIABLE_ADDED", templateId, templateName, "SYSTEM");
            this.variableName = variableName;
        }
        
        public String getVariableName() { return variableName; }
    }
    
    public static class ConfigTemplateVariableRemovedEvent extends ConfigTemplateEvent {
        private final String variableName;
        
        public ConfigTemplateVariableRemovedEvent(TemplateId templateId, String templateName, String variableName) {
            super("TEMPLATE_VARIABLE_REMOVED", templateId, templateName, "SYSTEM");
            this.variableName = variableName;
        }
        
        public String getVariableName() { return variableName; }
    }
    
    public static class ConfigTemplatePublishedEvent extends ConfigTemplateEvent {
        public ConfigTemplatePublishedEvent(TemplateId templateId, String templateName, String operator) {
            super("TEMPLATE_PUBLISHED", templateId, templateName, operator);
        }
        
        @Override
        public boolean isImportant() {
            return true;
        }
    }
    
    public static class ConfigTemplateDeactivatedEvent extends ConfigTemplateEvent {
        public ConfigTemplateDeactivatedEvent(TemplateId templateId, String templateName, String operator) {
            super("TEMPLATE_DEACTIVATED", templateId, templateName, operator);
        }
    }
    
    public static class ConfigTemplateReactivatedEvent extends ConfigTemplateEvent {
        public ConfigTemplateReactivatedEvent(TemplateId templateId, String templateName, String operator) {
            super("TEMPLATE_REACTIVATED", templateId, templateName, operator);
        }
    }
    
    public static class ConfigTemplateVersionCreatedEvent extends ConfigTemplateEvent {
        private final String version;
        
        public ConfigTemplateVersionCreatedEvent(TemplateId templateId, String templateName, 
                                                String version, String operator) {
            super("TEMPLATE_VERSION_CREATED", templateId, templateName, operator);
            this.version = version;
        }
        
        public String getVersion() { return version; }
    }
} 