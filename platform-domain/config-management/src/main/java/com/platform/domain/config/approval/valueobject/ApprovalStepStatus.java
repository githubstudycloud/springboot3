package com.platform.domain.config.approval.valueobject;

/**
 * 审批步骤状态枚举
 * 定义审批步骤的状态
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public enum ApprovalStepStatus {
    
    /**
     * 进行中
     */
    IN_PROGRESS("进行中", "步骤正在处理中", false),
    
    /**
     * 已通过
     */
    APPROVED("已通过", "步骤审批通过", true),
    
    /**
     * 已拒绝
     */
    REJECTED("已拒绝", "步骤审批被拒绝", true),
    
    /**
     * 已完成
     */
    COMPLETED("已完成", "步骤执行完成", true),
    
    /**
     * 执行失败
     */
    FAILED("执行失败", "步骤执行失败", true),
    
    /**
     * 已跳过
     */
    SKIPPED("已跳过", "步骤被跳过", true),
    
    /**
     * 超时
     */
    TIMEOUT("超时", "步骤处理超时", true);
    
    private final String displayName;
    private final String description;
    private final boolean isCompleted;
    
    ApprovalStepStatus(String displayName, String description, boolean isCompleted) {
        this.displayName = displayName;
        this.description = description;
        this.isCompleted = isCompleted;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    /**
     * 检查是否为成功状态
     */
    public boolean isSuccess() {
        return this == APPROVED || this == COMPLETED;
    }
    
    /**
     * 检查是否为失败状态
     */
    public boolean isFailure() {
        return this == REJECTED || this == FAILED || this == TIMEOUT;
    }
    
    /**
     * 检查是否为进行中状态
     */
    public boolean isInProgress() {
        return this == IN_PROGRESS;
    }
    
    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        return this == FAILED || this == TIMEOUT;
    }
    
    /**
     * 检查是否可以跳过
     */
    public boolean canSkip() {
        return this == IN_PROGRESS || this == FAILED || this == TIMEOUT;
    }
    
    /**
     * 获取状态优先级（用于排序）
     */
    public int getPriority() {
        return switch (this) {
            case IN_PROGRESS -> 1;
            case APPROVED, COMPLETED -> 2;
            case SKIPPED -> 3;
            case REJECTED, FAILED, TIMEOUT -> 4;
        };
    }
} 