package com.platform.domain.config.approval.entity;

import com.platform.domain.config.approval.valueobject.ApprovalStepStatus;
import com.platform.domain.config.approval.valueobject.ApprovalStepType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 审批步骤实体
 * 记录审批流程中的每个步骤
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Entity
@Table(name = "platform_config_approval_step")
public class ApprovalStep {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_id", nullable = false)
    private ConfigApproval approval;
    
    @Column(nullable = false)
    private String approver;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStepType stepType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStepStatus status;
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime completedAt;
    
    @Size(max = 1000, message = "审批意见长度不能超过1000个字符")
    @Column(length = 1000)
    private String comment;
    
    @Size(max = 500, message = "拒绝原因长度不能超过500个字符")
    @Column(length = 500)
    private String rejectionReason;
    
    @Column
    private Long duration; // 处理耗时（毫秒）
    
    @Version
    private Long versionNumber;
    
    // JPA required
    protected ApprovalStep() {}
    
    /**
     * 创建新的审批步骤
     */
    public ApprovalStep(ConfigApproval approval, String approver, ApprovalStepType stepType) {
        this.approval = approval;
        this.approver = approver;
        this.stepType = stepType;
        this.status = ApprovalStepStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }
    
    /**
     * 审批通过
     */
    public void approve(String comment) {
        if (this.status != ApprovalStepStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有进行中的步骤才能审批通过");
        }
        
        this.status = ApprovalStepStatus.APPROVED;
        this.comment = comment;
        this.completedAt = LocalDateTime.now();
        this.duration = calculateDuration();
    }
    
    /**
     * 审批拒绝
     */
    public void reject(String rejectionReason) {
        if (this.status != ApprovalStepStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有进行中的步骤才能拒绝");
        }
        
        this.status = ApprovalStepStatus.REJECTED;
        this.rejectionReason = rejectionReason;
        this.completedAt = LocalDateTime.now();
        this.duration = calculateDuration();
    }
    
    /**
     * 步骤完成
     */
    public void complete(String comment) {
        if (this.status != ApprovalStepStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有进行中的步骤才能完成");
        }
        
        this.status = ApprovalStepStatus.COMPLETED;
        this.comment = comment;
        this.completedAt = LocalDateTime.now();
        this.duration = calculateDuration();
    }
    
    /**
     * 步骤失败
     */
    public void fail(String errorMessage) {
        if (this.status != ApprovalStepStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有进行中的步骤才能标记失败");
        }
        
        this.status = ApprovalStepStatus.FAILED;
        this.rejectionReason = errorMessage;
        this.completedAt = LocalDateTime.now();
        this.duration = calculateDuration();
    }
    
    /**
     * 跳过步骤
     */
    public void skip(String reason) {
        if (this.status != ApprovalStepStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有进行中的步骤才能跳过");
        }
        
        this.status = ApprovalStepStatus.SKIPPED;
        this.comment = reason;
        this.completedAt = LocalDateTime.now();
        this.duration = calculateDuration();
    }
    
    /**
     * 计算处理耗时
     */
    private Long calculateDuration() {
        if (this.startedAt != null && this.completedAt != null) {
            return java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
        }
        return null;
    }
    
    /**
     * 检查步骤是否已完成
     */
    public boolean isCompleted() {
        return this.status.isCompleted();
    }
    
    /**
     * 检查步骤是否成功
     */
    public boolean isSuccessful() {
        return this.status == ApprovalStepStatus.APPROVED || 
               this.status == ApprovalStepStatus.COMPLETED;
    }
    
    /**
     * 检查步骤是否失败
     */
    public boolean isFailed() {
        return this.status == ApprovalStepStatus.REJECTED || 
               this.status == ApprovalStepStatus.FAILED;
    }
    
    /**
     * 获取处理耗时（秒）
     */
    public Long getDurationInSeconds() {
        return this.duration != null ? this.duration / 1000 : null;
    }
    
    // Getters
    public Long getId() { return id; }
    public ConfigApproval getApproval() { return approval; }
    public String getApprover() { return approver; }
    public ApprovalStepType getStepType() { return stepType; }
    public ApprovalStepStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getComment() { return comment; }
    public String getRejectionReason() { return rejectionReason; }
    public Long getDuration() { return duration; }
    public Long getVersionNumber() { return versionNumber; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApprovalStep that = (ApprovalStep) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ApprovalStep{" +
                "id=" + id +
                ", approver='" + approver + '\'' +
                ", stepType=" + stepType +
                ", status=" + status +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                '}';
    }
} 