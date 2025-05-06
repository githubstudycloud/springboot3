package com.platform.report.domain.event;

import lombok.Getter;

/**
 * 模板事件
 */
public class TemplateEvents {
    
    /**
     * 模板创建事件
     */
    @Getter
    public static class TemplateCreated extends DomainEvent {
        private final String templateId;
        private final String name;
        private final String createdBy;
        
        public TemplateCreated(String templateId, String name, String createdBy) {
            super();
            this.templateId = templateId;
            this.name = name;
            this.createdBy = createdBy;
        }
    }
    
    /**
     * 模板更新事件
     */
    @Getter
    public static class TemplateUpdated extends DomainEvent {
        private final String templateId;
        private final String name;
        private final String updatedBy;
        
        public TemplateUpdated(String templateId, String name, String updatedBy) {
            super();
            this.templateId = templateId;
            this.name = name;
            this.updatedBy = updatedBy;
        }
    }
    
    /**
     * 模板发布事件
     */
    @Getter
    public static class TemplatePublished extends DomainEvent {
        private final String templateId;
        private final String name;
        private final String publishedBy;
        
        public TemplatePublished(String templateId, String name, String publishedBy) {
            super();
            this.templateId = templateId;
            this.name = name;
            this.publishedBy = publishedBy;
        }
    }
    
    /**
     * 模板归档事件
     */
    @Getter
    public static class TemplateArchived extends DomainEvent {
        private final String templateId;
        private final String name;
        private final String archivedBy;
        
        public TemplateArchived(String templateId, String name, String archivedBy) {
            super();
            this.templateId = templateId;
            this.name = name;
            this.archivedBy = archivedBy;
        }
    }
    
    /**
     * 模板删除事件
     */
    @Getter
    public static class TemplateDeleted extends DomainEvent {
        private final String templateId;
        private final String name;
        private final String deletedBy;
        
        public TemplateDeleted(String templateId, String name, String deletedBy) {
            super();
            this.templateId = templateId;
            this.name = name;
            this.deletedBy = deletedBy;
        }
    }
}
