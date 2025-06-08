package com.platform.domain.config.approval.valueobject;

/**
 * 审批步骤类型枚举
 * 定义审批流程中的不同步骤类型
 * 
 * @author Platform Team
 * @since 1.0.0
 */
public enum ApprovalStepType {
    
    /**
     * 审核步骤
     */
    REVIEW("审核", "审核配置变更内容", 1),
    
    /**
     * 技术审批
     */
    TECHNICAL_APPROVAL("技术审批", "技术负责人审批", 2),
    
    /**
     * 业务审批
     */
    BUSINESS_APPROVAL("业务审批", "业务负责人审批", 3),
    
    /**
     * 安全审批
     */
    SECURITY_APPROVAL("安全审批", "安全团队审批", 4),
    
    /**
     * 最终审批
     */
    FINAL_APPROVAL("最终审批", "最终审批人审批", 5),
    
    /**
     * 执行步骤
     */
    EXECUTE("执行", "执行配置变更", 6),
    
    /**
     * 验证步骤
     */
    VERIFY("验证", "验证配置变更结果", 7),
    
    /**
     * 通知步骤
     */
    NOTIFY("通知", "通知相关人员", 8);
    
    private final String displayName;
    private final String description;
    private final int order;
    
    ApprovalStepType(String displayName, String description, int order) {
        this.displayName = displayName;
        this.description = description;
        this.order = order;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getOrder() {
        return order;
    }
    
    /**
     * 检查是否为审批类型步骤
     */
    public boolean isApprovalStep() {
        return this == REVIEW || 
               this == TECHNICAL_APPROVAL || 
               this == BUSINESS_APPROVAL || 
               this == SECURITY_APPROVAL || 
               this == FINAL_APPROVAL;
    }
    
    /**
     * 检查是否为执行类型步骤
     */
    public boolean isExecutionStep() {
        return this == EXECUTE || this == VERIFY;
    }
    
    /**
     * 检查是否为通知类型步骤
     */
    public boolean isNotificationStep() {
        return this == NOTIFY;
    }
    
    /**
     * 检查是否需要人工干预
     */
    public boolean requiresHumanIntervention() {
        return isApprovalStep();
    }
    
    /**
     * 检查是否可以自动执行
     */
    public boolean canAutoExecute() {
        return this == EXECUTE || this == VERIFY || this == NOTIFY;
    }
    
    /**
     * 获取下一个步骤类型
     */
    public ApprovalStepType getNextStepType() {
        ApprovalStepType[] values = ApprovalStepType.values();
        for (int i = 0; i < values.length - 1; i++) {
            if (values[i] == this) {
                return values[i + 1];
            }
        }
        return null;
    }
    
    /**
     * 根据审批类型获取必需的步骤
     */
    public static ApprovalStepType[] getRequiredStepsForType(ApprovalType approvalType) {
        return switch (approvalType) {
            case CONFIG_CREATE, CONFIG_UPDATE -> 
                new ApprovalStepType[]{REVIEW, TECHNICAL_APPROVAL, EXECUTE, VERIFY};
            case CONFIG_DELETE -> 
                new ApprovalStepType[]{REVIEW, TECHNICAL_APPROVAL, BUSINESS_APPROVAL, EXECUTE, VERIFY};
            case SENSITIVE_CONFIG_UPDATE, PRODUCTION_CONFIG_UPDATE -> 
                new ApprovalStepType[]{REVIEW, TECHNICAL_APPROVAL, SECURITY_APPROVAL, FINAL_APPROVAL, EXECUTE, VERIFY};
            case CONFIG_ROLLBACK, EMERGENCY_CONFIG_UPDATE -> 
                new ApprovalStepType[]{REVIEW, EXECUTE, VERIFY};
            case CONFIG_BATCH_UPDATE, CONFIG_MIGRATION -> 
                new ApprovalStepType[]{REVIEW, TECHNICAL_APPROVAL, BUSINESS_APPROVAL, EXECUTE, VERIFY};
            default -> 
                new ApprovalStepType[]{REVIEW, EXECUTE, VERIFY};
        };
    }
} 