package com.platform.domain.config.approval.event;

import com.platform.domain.config.shared.AbstractDomainEvent;
import com.platform.domain.config.approval.valueobject.ApprovalId;
import com.platform.domain.config.approval.valueobject.ApprovalType;
import com.platform.domain.config.approval.valueobject.Priority;

/**
 * 配置审批领域事件
 * DDD领域事件 - 记录配置审批流程的重要业务变化
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public abstract class ConfigApprovalEvent extends AbstractDomainEvent {
    
    private final String title;
    
    protected ConfigApprovalEvent(String eventType, ApprovalId approvalId, 
                                String title, String operator) {
        super(eventType, approvalId.getValue(), "ConfigApproval", operator);
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    @Override
    public boolean isImportant() {
        return getEventType().contains("APPROVED") || 
               getEventType().contains("REJECTED") || 
               getEventType().contains("EXECUTED");
    }
    
    @Override
    public boolean isAsyncProcessing() {
        // 审批事件通常需要立即处理通知
        return !getEventType().contains("TIMEOUT");
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
    
    // 具体事件类型
    
    public static class ConfigApprovalCreatedEvent extends ConfigApprovalEvent {
        private final ApprovalType type;
        private final Priority priority;
        
        public ConfigApprovalCreatedEvent(ApprovalId approvalId, String title, 
                                        ApprovalType type, Priority priority, String applicant) {
            super("APPROVAL_CREATED", approvalId, title, applicant);
            this.type = type;
            this.priority = priority;
        }
        
        public ApprovalType getType() { return type; }
        public Priority getPriority() { return priority; }
    }
    
    public static class ConfigApprovalSubmittedEvent extends ConfigApprovalEvent {
        public ConfigApprovalSubmittedEvent(ApprovalId approvalId, String title, String approver) {
            super("APPROVAL_SUBMITTED", approvalId, title, approver);
        }
    }
    
    public static class ConfigApprovalApprovedEvent extends ConfigApprovalEvent {
        public ConfigApprovalApprovedEvent(ApprovalId approvalId, String title, String approver) {
            super("APPROVAL_APPROVED", approvalId, title, approver);
        }
        
        @Override
        public boolean isImportant() {
            return true;
        }
    }
    
    public static class ConfigApprovalRejectedEvent extends ConfigApprovalEvent {
        private final String reason;
        
        public ConfigApprovalRejectedEvent(ApprovalId approvalId, String title, String approver, String reason) {
            super("APPROVAL_REJECTED", approvalId, title, approver);
            this.reason = reason;
        }
        
        public String getReason() { return reason; }
        
        @Override
        public boolean isImportant() {
            return true;
        }
    }
    
    public static class ConfigApprovalExecutedEvent extends ConfigApprovalEvent {
        public ConfigApprovalExecutedEvent(ApprovalId approvalId, String title, String executor) {
            super("APPROVAL_EXECUTED", approvalId, title, executor);
        }
        
        @Override
        public boolean isImportant() {
            return true;
        }
    }
    
    public static class ConfigApprovalExecutionFailedEvent extends ConfigApprovalEvent {
        private final String errorMessage;
        
        public ConfigApprovalExecutionFailedEvent(ApprovalId approvalId, String title, 
                                                String executor, String errorMessage) {
            super("APPROVAL_EXECUTION_FAILED", approvalId, title, executor);
            this.errorMessage = errorMessage;
        }
        
        public String getErrorMessage() { return errorMessage; }
        
        @Override
        public boolean isImportant() {
            return true;
        }
    }
    
    public static class ConfigApprovalCancelledEvent extends ConfigApprovalEvent {
        public ConfigApprovalCancelledEvent(ApprovalId approvalId, String title, String operator) {
            super("APPROVAL_CANCELLED", approvalId, title, operator);
        }
    }
    
    public static class ConfigApprovalTimeoutEvent extends ConfigApprovalEvent {
        public ConfigApprovalTimeoutEvent(ApprovalId approvalId, String title, String currentApprover) {
            super("APPROVAL_TIMEOUT", approvalId, title, currentApprover);
        }
        
        @Override
        public boolean isAsyncProcessing() {
            return false; // 超时事件需要立即处理
        }
        
        @Override
        public boolean isImportant() {
            return true;
        }
    }
} 