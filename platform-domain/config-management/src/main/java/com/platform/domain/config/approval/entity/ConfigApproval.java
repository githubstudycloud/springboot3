package com.platform.domain.config.approval.entity;

import com.platform.domain.config.approval.event.ConfigApprovalEvent;
import com.platform.domain.config.approval.valueobject.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 配置审批聚合根
 * DDD领域模型 - 封装配置变更审批流程的业务逻辑
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "platform_config_approval")
public class ConfigApproval extends AbstractAggregateRoot<ConfigApproval> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private ApprovalId approvalId;
    
    @NotBlank(message = "申请标题不能为空")
    @Size(min = 5, max = 200, message = "申请标题长度必须在5-200个字符之间")
    @Column(nullable = false)
    private String title;
    
    @Size(max = 1000, message = "申请描述长度不能超过1000个字符")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;
    
    @Column(nullable = false)
    private String applicant;
    
    @Column(nullable = false)
    private LocalDateTime appliedAt;
    
    // 配置变更相关信息
    @Column(nullable = false)
    private String application;
    
    @Column(nullable = false)
    private String profile;
    
    @Column(columnDefinition = "TEXT")
    private String oldContent;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String newContent;
    
    @Column(length = 500)
    private String changeReason;
    
    // 审批流程信息
    @OneToMany(mappedBy = "approval", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApprovalStep> approvalSteps = new ArrayList<>();
    
    @Column
    private String currentApprover;
    
    @Column
    private LocalDateTime approvalDeadline;
    
    @Column
    private String approvedBy;
    
    @Column
    private LocalDateTime approvedAt;
    
    @Column
    private String rejectedBy;
    
    @Column
    private LocalDateTime rejectedAt;
    
    @Column(length = 500)
    private String rejectionReason;
    
    // 执行信息
    @Column
    private String executedBy;
    
    @Column
    private LocalDateTime executedAt;
    
    @Column(length = 500)
    private String executionResult;
    
    @Version
    private Long versionNumber;
    
    // JPA required
    protected ConfigApproval() {}
    
    /**
     * 创建新的配置审批申请
     */
    public ConfigApproval(String title, String description, ApprovalType type, Priority priority,
                         String applicant, String application, String profile, 
                         String oldContent, String newContent, String changeReason) {
        this.approvalId = ApprovalId.generate();
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.applicant = applicant;
        this.application = application;
        this.profile = profile;
        this.oldContent = oldContent;
        this.newContent = newContent;
        this.changeReason = changeReason;
        this.status = ApprovalStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
        
        // 设置审批截止时间
        this.approvalDeadline = calculateApprovalDeadline(priority);
        
        // 发布领域事件
        registerEvent(ConfigApprovalEvent.approvalCreated(this.approvalId, title, type, priority, applicant));
    }
    
    /**
     * 提交审批
     */
    public void submit(String approver) {
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("只有待审批状态的申请才能提交审批");
        }
        
        this.status = ApprovalStatus.IN_REVIEW;
        this.currentApprover = approver;
        
        // 创建审批步骤
        ApprovalStep step = new ApprovalStep(this, approver, ApprovalStepType.REVIEW);
        this.approvalSteps.add(step);
        
        registerEvent(ConfigApprovalEvent.approvalSubmitted(this.approvalId, this.title, approver));
    }
    
    /**
     * 审批通过
     */
    public void approve(String approver, String comment) {
        if (this.status != ApprovalStatus.IN_REVIEW) {
            throw new IllegalStateException("只有审批中的申请才能通过");
        }
        
        if (!Objects.equals(this.currentApprover, approver)) {
            throw new IllegalArgumentException("只有当前审批人才能进行审批操作");
        }
        
        this.status = ApprovalStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.currentApprover = null;
        
        // 更新审批步骤
        getCurrentApprovalStep().approve(comment);
        
        registerEvent(ConfigApprovalEvent.approvalApproved(this.approvalId, this.title, approver));
    }
    
    /**
     * 审批拒绝
     */
    public void reject(String approver, String reason) {
        if (this.status != ApprovalStatus.IN_REVIEW) {
            throw new IllegalStateException("只有审批中的申请才能拒绝");
        }
        
        if (!Objects.equals(this.currentApprover, approver)) {
            throw new IllegalArgumentException("只有当前审批人才能进行审批操作");
        }
        
        this.status = ApprovalStatus.REJECTED;
        this.rejectedBy = approver;
        this.rejectedAt = LocalDateTime.now();
        this.rejectionReason = reason;
        this.currentApprover = null;
        
        // 更新审批步骤
        getCurrentApprovalStep().reject(reason);
        
        registerEvent(ConfigApprovalEvent.approvalRejected(this.approvalId, this.title, approver, reason));
    }
    
    /**
     * 执行配置变更
     */
    public void execute(String executor) {
        if (this.status != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("只有已审批通过的申请才能执行");
        }
        
        this.status = ApprovalStatus.EXECUTED;
        this.executedBy = executor;
        this.executedAt = LocalDateTime.now();
        
        // 创建执行步骤
        ApprovalStep step = new ApprovalStep(this, executor, ApprovalStepType.EXECUTE);
        this.approvalSteps.add(step);
        step.complete("配置变更执行成功");
        
        registerEvent(ConfigApprovalEvent.approvalExecuted(this.approvalId, this.title, executor));
    }
    
    /**
     * 执行失败
     */
    public void executionFailed(String executor, String errorMessage) {
        if (this.status != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("只有已审批通过的申请才能标记执行失败");
        }
        
        this.status = ApprovalStatus.EXECUTION_FAILED;
        this.executedBy = executor;
        this.executedAt = LocalDateTime.now();
        this.executionResult = errorMessage;
        
        // 创建执行步骤
        ApprovalStep step = new ApprovalStep(this, executor, ApprovalStepType.EXECUTE);
        this.approvalSteps.add(step);
        step.fail(errorMessage);
        
        registerEvent(ConfigApprovalEvent.approvalExecutionFailed(this.approvalId, this.title, executor, errorMessage));
    }
    
    /**
     * 撤销申请
     */
    public void cancel(String operator) {
        if (this.status == ApprovalStatus.EXECUTED || this.status == ApprovalStatus.CANCELLED) {
            throw new IllegalStateException("已执行或已撤销的申请不能再次撤销");
        }
        
        if (!Objects.equals(this.applicant, operator)) {
            throw new IllegalArgumentException("只有申请人才能撤销申请");
        }
        
        this.status = ApprovalStatus.CANCELLED;
        this.currentApprover = null;
        
        registerEvent(ConfigApprovalEvent.approvalCancelled(this.approvalId, this.title, operator));
    }
    
    /**
     * 检查是否超时
     */
    public boolean isOverdue() {
        return this.approvalDeadline != null && 
               LocalDateTime.now().isAfter(this.approvalDeadline) &&
               (this.status == ApprovalStatus.PENDING || this.status == ApprovalStatus.IN_REVIEW);
    }
    
    /**
     * 获取当前审批步骤
     */
    private ApprovalStep getCurrentApprovalStep() {
        return this.approvalSteps.stream()
                .filter(step -> step.getStatus() == ApprovalStepStatus.IN_PROGRESS)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("没有找到当前审批步骤"));
    }
    
    /**
     * 计算审批截止时间
     */
    private LocalDateTime calculateApprovalDeadline(Priority priority) {
        LocalDateTime now = LocalDateTime.now();
        return switch (priority) {
            case URGENT -> now.plusHours(4);
            case HIGH -> now.plusHours(12);
            case MEDIUM -> now.plusDays(1);
            case LOW -> now.plusDays(3);
        };
    }
    
    /**
     * 检查是否可以编辑
     */
    public boolean canEdit() {
        return this.status == ApprovalStatus.PENDING;
    }
    
    /**
     * 检查是否可以审批
     */
    public boolean canApprove() {
        return this.status == ApprovalStatus.IN_REVIEW;
    }
    
    /**
     * 检查是否可以执行
     */
    public boolean canExecute() {
        return this.status == ApprovalStatus.APPROVED;
    }
    
    // Getters
    public Long getId() { return id; }
    public ApprovalId getApprovalId() { return approvalId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ApprovalType getType() { return type; }
    public ApprovalStatus getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public String getApplicant() { return applicant; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public String getApplication() { return application; }
    public String getProfile() { return profile; }
    public String getOldContent() { return oldContent; }
    public String getNewContent() { return newContent; }
    public String getChangeReason() { return changeReason; }
    public List<ApprovalStep> getApprovalSteps() { return new ArrayList<>(approvalSteps); }
    public String getCurrentApprover() { return currentApprover; }
    public LocalDateTime getApprovalDeadline() { return approvalDeadline; }
    public String getApprovedBy() { return approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getRejectedBy() { return rejectedBy; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public String getExecutedBy() { return executedBy; }
    public LocalDateTime getExecutedAt() { return executedAt; }
    public String getExecutionResult() { return executionResult; }
    public Long getVersionNumber() { return versionNumber; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigApproval that = (ConfigApproval) o;
        return Objects.equals(approvalId, that.approvalId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(approvalId);
    }
    
    @Override
    public String toString() {
        return "ConfigApproval{" +
                "approvalId=" + approvalId +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", priority=" + priority +
                ", applicant='" + applicant + '\'' +
                '}';
    }
} 