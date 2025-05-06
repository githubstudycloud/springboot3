package com.platform.report.domain.event;

import lombok.Getter;

/**
 * 计划事件
 */
public class ScheduleEvents {
    
    /**
     * 计划创建事件
     */
    @Getter
    public static class ScheduleCreated extends DomainEvent {
        private final String scheduleId;
        private final String name;
        private final String templateId;
        private final String createdBy;
        
        public ScheduleCreated(String scheduleId, String name, String templateId, String createdBy) {
            super();
            this.scheduleId = scheduleId;
            this.name = name;
            this.templateId = templateId;
            this.createdBy = createdBy;
        }
    }
    
    /**
     * 计划更新事件
     */
    @Getter
    public static class ScheduleUpdated extends DomainEvent {
        private final String scheduleId;
        private final String name;
        private final String updatedBy;
        
        public ScheduleUpdated(String scheduleId, String name, String updatedBy) {
            super();
            this.scheduleId = scheduleId;
            this.name = name;
            this.updatedBy = updatedBy;
        }
    }
    
    /**
     * 计划暂停事件
     */
    @Getter
    public static class SchedulePaused extends DomainEvent {
        private final String scheduleId;
        private final String name;
        private final String pausedBy;
        
        public SchedulePaused(String scheduleId, String name, String pausedBy) {
            super();
            this.scheduleId = scheduleId;
            this.name = name;
            this.pausedBy = pausedBy;
        }
    }
    
    /**
     * 计划恢复事件
     */
    @Getter
    public static class ScheduleResumed extends DomainEvent {
        private final String scheduleId;
        private final String name;
        private final String resumedBy;
        
        public ScheduleResumed(String scheduleId, String name, String resumedBy) {
            super();
            this.scheduleId = scheduleId;
            this.name = name;
            this.resumedBy = resumedBy;
        }
    }
    
    /**
     * 计划取消事件
     */
    @Getter
    public static class ScheduleCancelled extends DomainEvent {
        private final String scheduleId;
        private final String name;
        private final String cancelledBy;
        
        public ScheduleCancelled(String scheduleId, String name, String cancelledBy) {
            super();
            this.scheduleId = scheduleId;
            this.name = name;
            this.cancelledBy = cancelledBy;
        }
    }
    
    /**
     * 计划执行事件
     */
    @Getter
    public static class ScheduleExecuted extends DomainEvent {
        private final String scheduleId;
        private final String name;
        private final String reportId;
        
        public ScheduleExecuted(String scheduleId, String name, String reportId) {
            super();
            this.scheduleId = scheduleId;
            this.name = name;
            this.reportId = reportId;
        }
    }
    
    /**
     * 计划执行失败事件
     */
    @Getter
    public static class ScheduleExecutionFailed extends DomainEvent {
        private final String scheduleId;
        private final String name;
        private final String errorMessage;
        
        public ScheduleExecutionFailed(String scheduleId, String name, String errorMessage) {
            super();
            this.scheduleId = scheduleId;
            this.name = name;
            this.errorMessage = errorMessage;
        }
    }
}
