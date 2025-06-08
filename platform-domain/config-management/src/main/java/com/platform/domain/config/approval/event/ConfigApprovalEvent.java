package com.platform.domain.config.approval.event;

import com.platform.domain.config.approval.valueobject.ApprovalId;
import com.platform.domain.config.approval.valueobject.ApprovalType;
import com.platform.domain.config.approval.valueobject.Priority;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 配置审批领域事件
 * DDD领域事件 - 记录配置审批流程的重要业务变化
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public abstract class ConfigApprovalEvent {
    
    private final ApprovalId approvalId;
    private final String title;
    private final String eventType;
    private final String operator;
    private final LocalDateTime occurredAt;
    
    protected ConfigApprovalEvent(ApprovalId approvalId, String title, 
                                String eventType, String operator) {
        this.approvalId = approvalId;
        this.title = title;
        this.eventType = eventType;
        this.operator = operator;
        this.occurredAt = LocalDateTime.now();
    }
    
    // 审批创建事件
    public static ConfigApprovalCreatedEvent approvalCreated(ApprovalId approvalId, String title, 
                                                           ApprovalType type, Priority priority, String applicant) {
        return new ConfigApprovalCreatedEvent(approvalId, title, type, priority, applicant);
    }
    
    // 审批提交事件
    public static ConfigApprovalSubmittedEvent approvalSubmitted(ApprovalId approvalId, 
                                                               String title, String approver) {
        return new ConfigApprovalSubmittedEvent(approvalId, title, approver);
    }
    
    // 审批通过事件
    public static ConfigApprovalApprovedEvent approvalApproved(ApprovalId approvalId, 
                                                             String title, String approver) {
        return new ConfigApprovalApprovedEvent(approvalId, title, approver);
    }
    
    // 审批拒绝事件
    public static ConfigApprovalRejectedEvent approvalRejected(ApprovalId approvalId, String title, 
                                                             String approver, String reason) {
        return new ConfigApprovalRejectedEvent(approvalId, title, approver, reason);
    }
    
    // 审批执行事件
    public static ConfigApprovalExecutedEvent approvalExecuted(ApprovalId approvalId, 
                                                             String title, String executor) {
        return new ConfigApprovalExecutedEvent(approvalId, title, executor);
    }
    
    // 审批执行失败事件
    public static ConfigApprovalExecutionFailedEvent approvalExecutionFailed(ApprovalId approvalId, 
                                                                            String title, String executor, String errorMessage) {
        return new ConfigApprovalExecutionFailedEvent(approvalId, title, executor, errorMessage);
    }
    
    // 审批撤销事件
    public static ConfigApprovalCancelledEvent approvalCancelled(ApprovalId approvalId, 
                                                               String title, String operator) {
        return new ConfigApprovalCancelledEvent(approvalId, title, operator);
    }
    
    // 审批超时事件
    public static ConfigApprovalTimeoutEvent approvalTimeout(ApprovalId approvalId, 
                                                           String title, String currentApprover) {
        return new ConfigApprovalTimeoutEvent(approvalId, title, currentApprover);
    }
    
    // Getters
    public ApprovalId getApprovalId() { return approvalId; }
    public String getTitle() { return title; }
    public String getEventType() { return eventType; }
    public String getOperator() { return operator; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigApprovalEvent that = (ConfigApprovalEvent) o;
        return Objects.equals(approvalId, that.approvalId) &&
               Objects.equals(eventType, that.eventType) &&
               Objects.equals(occurredAt, that.occurredAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(approvalId, eventType, occurredAt);
    }
    
    @Override
    public String toString() {
        return "ConfigApprovalEvent{" +
                "approvalId=" + approvalId +
                ", title='" + title + '\'' +
                ", eventType='" + eventType + '\'' +
                ", operator='" + operator + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
    
    // 具体事件类型
    
    public static class ConfigApprovalCreatedEvent extends ConfigApprovalEvent {
        private final ApprovalType type;
        private final Priority priority;
        
        public ConfigApprovalCreatedEvent(ApprovalId approvalId, String title, 
                                        ApprovalType type, Priority priority, String applicant) {
            super(approvalId, title, "APPROVAL_CREATED", applicant);
            this.type = type;
            this.priority = priority;
        }
        
        public ApprovalType getType() { return type; }
        public Priority getPriority() { return priority; }
    }
    
    public static class ConfigApprovalSubmittedEvent extends ConfigApprovalEvent {
        public ConfigApprovalSubmittedEvent(ApprovalId approvalId, String title, String approver) {
            super(approvalId, title, "APPROVAL_SUBMITTED", approver);
        }
    }
    
    public static class ConfigApprovalApprovedEvent extends ConfigApprovalEvent {
        public ConfigApprovalApprovedEvent(ApprovalId approvalId, String title, String approver) {
            super(approvalId, title, "APPROVAL_APPROVED", approver);
        }
    }
    
    public static class ConfigApprovalRejectedEvent extends ConfigApprovalEvent {
        private final String reason;
        
        public ConfigApprovalRejectedEvent(ApprovalId approvalId, String title, String approver, String reason) {
            super(approvalId, title, "APPROVAL_REJECTED", approver);
            this.reason = reason;
        }
        
        public String getReason() { return reason; }
    }
    
    public static class ConfigApprovalExecutedEvent extends ConfigApprovalEvent {
        public ConfigApprovalExecutedEvent(ApprovalId approvalId, String title, String executor) {
            super(approvalId, title, "APPROVAL_EXECUTED", executor);
        }
    }
    
    public static class ConfigApprovalExecutionFailedEvent extends ConfigApprovalEvent {
        private final String errorMessage;
        
        public ConfigApprovalExecutionFailedEvent(ApprovalId approvalId, String title, 
                                                String executor, String errorMessage) {
            super(approvalId, title, "APPROVAL_EXECUTION_FAILED", executor);
            this.errorMessage = errorMessage;
        }
        
        public String getErrorMessage() { return errorMessage; }
    }
    
    public static class ConfigApprovalCancelledEvent extends ConfigApprovalEvent {
        public ConfigApprovalCancelledEvent(ApprovalId approvalId, String title, String operator) {
            super(approvalId, title, "APPROVAL_CANCELLED", operator);
        }
    }
    
    public static class ConfigApprovalTimeoutEvent extends ConfigApprovalEvent {
        public ConfigApprovalTimeoutEvent(ApprovalId approvalId, String title, String currentApprover) {
            super(approvalId, title, "APPROVAL_TIMEOUT", currentApprover);
        }
    }
} 