package com.platform.domain.config.approval.valueobject;

/**
 * 审批状态枚举
 * 定义配置审批的生命周期状态
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public enum ApprovalStatus {
    
    /**
     * 待提交 - 申请已创建但未提交审批
     */
    PENDING("待提交", "申请已创建，等待提交审批", false, true),
    
    /**
     * 审批中 - 正在审批流程中
     */
    IN_REVIEW("审批中", "正在进行审批", false, false),
    
    /**
     * 已通过 - 审批通过，等待执行
     */
    APPROVED("已通过", "审批通过，等待执行", false, false),
    
    /**
     * 已拒绝 - 审批被拒绝
     */
    REJECTED("已拒绝", "审批被拒绝", true, false),
    
    /**
     * 已执行 - 配置变更已执行
     */
    EXECUTED("已执行", "配置变更已成功执行", true, false),
    
    /**
     * 执行失败 - 配置变更执行失败
     */
    EXECUTION_FAILED("执行失败", "配置变更执行失败", true, false),
    
    /**
     * 已撤销 - 申请被撤销
     */
    CANCELLED("已撤销", "申请已被撤销", true, false),
    
    /**
     * 已超时 - 审批超时
     */
    TIMEOUT("已超时", "审批流程超时", true, false);
    
    private final String displayName;
    private final String description;
    private final boolean isFinal;
    private final boolean canEdit;
    
    ApprovalStatus(String displayName, String description, boolean isFinal, boolean canEdit) {
        this.displayName = displayName;
        this.description = description;
        this.isFinal = isFinal;
        this.canEdit = canEdit;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
    
    public boolean canEdit() {
        return canEdit;
    }
    
    /**
     * 检查是否可以提交审批
     */
    public boolean canSubmit() {
        return this == PENDING;
    }
    
    /**
     * 检查是否可以审批
     */
    public boolean canApprove() {
        return this == IN_REVIEW;
    }
    
    /**
     * 检查是否可以执行
     */
    public boolean canExecute() {
        return this == APPROVED;
    }
    
    /**
     * 检查是否可以撤销
     */
    public boolean canCancel() {
        return this == PENDING || this == IN_REVIEW;
    }
    
    /**
     * 检查是否为成功状态
     */
    public boolean isSuccess() {
        return this == EXECUTED;
    }
    
    /**
     * 检查是否为失败状态
     */
    public boolean isFailure() {
        return this == REJECTED || this == EXECUTION_FAILED || this == TIMEOUT;
    }
    
    /**
     * 检查是否为进行中状态
     */
    public boolean isInProgress() {
        return this == PENDING || this == IN_REVIEW || this == APPROVED;
    }
    
    /**
     * 获取下一个可能的状态
     */
    public ApprovalStatus[] getNextPossibleStatuses() {
        return switch (this) {
            case PENDING -> new ApprovalStatus[]{IN_REVIEW, CANCELLED};
            case IN_REVIEW -> new ApprovalStatus[]{APPROVED, REJECTED, TIMEOUT};
            case APPROVED -> new ApprovalStatus[]{EXECUTED, EXECUTION_FAILED};
            case REJECTED, EXECUTED, EXECUTION_FAILED, CANCELLED, TIMEOUT -> new ApprovalStatus[]{};
        };
    }
} 