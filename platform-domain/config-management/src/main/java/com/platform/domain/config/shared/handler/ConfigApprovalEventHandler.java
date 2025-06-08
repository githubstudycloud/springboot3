package com.platform.domain.config.shared.handler;

import com.platform.domain.config.approval.event.ConfigApprovalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 配置审批事件处理器
 * 处理配置审批相关的领域事件
 * 
 * @author Platform Team
 * @since 1.0.0
 */
@Component
public class ConfigApprovalEventHandler implements DomainEventHandler<ConfigApprovalEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigApprovalEventHandler.class);
    
    @Override
    @EventListener
    public void handle(ConfigApprovalEvent event) {
        logger.info("处理配置审批事件: {}", event);
        
        switch (event.getEventType()) {
            case "APPROVAL_CREATED" -> handleApprovalCreated(event);
            case "APPROVAL_SUBMITTED" -> handleApprovalSubmitted(event);
            case "APPROVAL_APPROVED" -> handleApprovalApproved(event);
            case "APPROVAL_REJECTED" -> handleApprovalRejected(event);
            case "APPROVAL_EXECUTED" -> handleApprovalExecuted(event);
            case "APPROVAL_EXECUTION_FAILED" -> handleApprovalExecutionFailed(event);
            case "APPROVAL_CANCELLED" -> handleApprovalCancelled(event);
            case "APPROVAL_TIMEOUT" -> handleApprovalTimeout(event);
            default -> logger.info("未处理的审批事件类型: {}", event.getEventType());
        }
    }
    
    @Override
    public Class<ConfigApprovalEvent> getEventType() {
        return ConfigApprovalEvent.class;
    }
    
    @Override
    public boolean isAsync() {
        return true;
    }
    
    @Override
    public int getPriority() {
        return 50; // 审批事件优先级较高
    }
    
    /**
     * 处理审批创建事件
     */
    private void handleApprovalCreated(ConfigApprovalEvent event) {
        if (event instanceof ConfigApprovalEvent.ConfigApprovalCreatedEvent createdEvent) {
            logger.info("配置审批已创建: 审批ID={}, 标题={}, 类型={}, 优先级={}, 申请人={}", 
                       event.getAggregateId(), event.getTitle(), 
                       createdEvent.getType(), createdEvent.getPriority(), event.getOperator());
            
            // 发送审批创建通知
            sendNotificationToApplicant("审批申请已创建", 
                                      String.format("您的配置变更申请 %s 已创建，等待提交审批", event.getTitle()));
            
            // 记录审批创建日志
            recordApprovalLog(event, "审批申请创建");
        }
    }
    
    /**
     * 处理审批提交事件
     */
    private void handleApprovalSubmitted(ConfigApprovalEvent event) {
        logger.info("配置审批已提交: 审批ID={}, 标题={}, 审批人={}", 
                   event.getAggregateId(), event.getTitle(), event.getOperator());
        
        // 通知审批人
        sendNotificationToApprover("待审批配置变更", 
                                 String.format("您有新的配置变更申请需要审批: %s", event.getTitle()),
                                 event.getOperator());
        
        // 通知申请人
        sendNotificationToApplicant("审批申请已提交", 
                                   String.format("您的配置变更申请 %s 已提交审批，请等待审批结果", event.getTitle()));
        
        // 记录审批提交日志
        recordApprovalLog(event, "审批申请提交");
    }
    
    /**
     * 处理审批通过事件
     */
    private void handleApprovalApproved(ConfigApprovalEvent event) {
        logger.info("配置审批已通过: 审批ID={}, 标题={}, 审批人={}", 
                   event.getAggregateId(), event.getTitle(), event.getOperator());
        
        // 发送审批通过通知
        sendImportantNotification("配置变更审批通过", 
                                String.format("配置变更申请 %s 已审批通过，准备执行", event.getTitle()));
        
        // 触发自动执行流程
        triggerAutoExecution(event);
        
        // 记录审批通过日志
        recordApprovalLog(event, "审批通过");
    }
    
    /**
     * 处理审批拒绝事件
     */
    private void handleApprovalRejected(ConfigApprovalEvent event) {
        if (event instanceof ConfigApprovalEvent.ConfigApprovalRejectedEvent rejectedEvent) {
            logger.info("配置审批已拒绝: 审批ID={}, 标题={}, 审批人={}, 拒绝原因={}", 
                       event.getAggregateId(), event.getTitle(), 
                       event.getOperator(), rejectedEvent.getReason());
            
            // 发送审批拒绝通知
            sendWarningNotification("配置变更审批被拒绝", 
                                   String.format("配置变更申请 %s 被拒绝，原因: %s", 
                                               event.getTitle(), rejectedEvent.getReason()));
            
            // 记录审批拒绝日志
            recordApprovalLog(event, "审批拒绝");
        }
    }
    
    /**
     * 处理审批执行事件
     */
    private void handleApprovalExecuted(ConfigApprovalEvent event) {
        logger.info("配置审批已执行: 审批ID={}, 标题={}, 执行人={}", 
                   event.getAggregateId(), event.getTitle(), event.getOperator());
        
        // 发送执行成功通知
        sendSuccessNotification("配置变更执行成功", 
                               String.format("配置变更 %s 已成功执行", event.getTitle()));
        
        // 更新相关系统状态
        updateSystemStatus(event);
        
        // 记录执行成功日志
        recordApprovalLog(event, "配置变更执行成功");
    }
    
    /**
     * 处理审批执行失败事件
     */
    private void handleApprovalExecutionFailed(ConfigApprovalEvent event) {
        if (event instanceof ConfigApprovalEvent.ConfigApprovalExecutionFailedEvent failedEvent) {
            logger.error("配置审批执行失败: 审批ID={}, 标题={}, 执行人={}, 错误信息={}", 
                        event.getAggregateId(), event.getTitle(), 
                        event.getOperator(), failedEvent.getErrorMessage());
            
            // 发送执行失败告警
            sendErrorNotification("配置变更执行失败", 
                                String.format("配置变更 %s 执行失败，错误: %s", 
                                            event.getTitle(), failedEvent.getErrorMessage()));
            
            // 触发失败处理流程
            triggerFailureHandling(event, failedEvent.getErrorMessage());
            
            // 记录执行失败日志
            recordApprovalLog(event, "配置变更执行失败");
        }
    }
    
    /**
     * 处理审批撤销事件
     */
    private void handleApprovalCancelled(ConfigApprovalEvent event) {
        logger.info("配置审批已撤销: 审批ID={}, 标题={}, 操作人={}", 
                   event.getAggregateId(), event.getTitle(), event.getOperator());
        
        // 发送撤销通知
        sendNotification("配置变更申请已撤销", 
                        String.format("配置变更申请 %s 已被撤销", event.getTitle()));
        
        // 记录撤销日志
        recordApprovalLog(event, "审批申请撤销");
    }
    
    /**
     * 处理审批超时事件
     */
    private void handleApprovalTimeout(ConfigApprovalEvent event) {
        logger.warn("配置审批已超时: 审批ID={}, 标题={}, 当前审批人={}", 
                   event.getAggregateId(), event.getTitle(), event.getOperator());
        
        // 发送超时告警
        sendUrgentNotification("配置变更审批超时", 
                             String.format("配置变更申请 %s 审批超时，请立即处理", event.getTitle()));
        
        // 触发超时处理流程
        triggerTimeoutHandling(event);
        
        // 记录超时日志
        recordApprovalLog(event, "审批超时");
    }
    
    /**
     * 发送通知给申请人
     */
    @Async
    protected void sendNotificationToApplicant(String title, String content) {
        logger.info("发送申请人通知: {} - {}", title, content);
        // 实际实现中可以根据申请人信息发送通知
    }
    
    /**
     * 发送通知给审批人
     */
    @Async
    protected void sendNotificationToApprover(String title, String content, String approver) {
        logger.info("发送审批人通知给 {}: {} - {}", approver, title, content);
        // 实际实现中可以根据审批人信息发送通知
    }
    
    /**
     * 发送一般通知
     */
    @Async
    protected void sendNotification(String title, String content) {
        logger.info("发送通知: {} - {}", title, content);
    }
    
    /**
     * 发送重要通知
     */
    @Async
    protected void sendImportantNotification(String title, String content) {
        logger.warn("发送重要通知: {} - {}", title, content);
    }
    
    /**
     * 发送警告通知
     */
    @Async
    protected void sendWarningNotification(String title, String content) {
        logger.warn("发送警告通知: {} - {}", title, content);
    }
    
    /**
     * 发送成功通知
     */
    @Async
    protected void sendSuccessNotification(String title, String content) {
        logger.info("发送成功通知: {} - {}", title, content);
    }
    
    /**
     * 发送错误通知
     */
    @Async
    protected void sendErrorNotification(String title, String content) {
        logger.error("发送错误通知: {} - {}", title, content);
    }
    
    /**
     * 发送紧急通知
     */
    @Async
    protected void sendUrgentNotification(String title, String content) {
        logger.error("发送紧急通知: {} - {}", title, content);
    }
    
    /**
     * 触发自动执行流程
     */
    @Async
    protected void triggerAutoExecution(ConfigApprovalEvent event) {
        logger.info("触发自动执行流程: {}", event.getAggregateId());
        // 实际实现中可以调用配置执行服务
    }
    
    /**
     * 更新系统状态
     */
    @Async
    protected void updateSystemStatus(ConfigApprovalEvent event) {
        logger.info("更新系统状态: {}", event.getAggregateId());
        // 实际实现中可以更新相关系统的配置状态
    }
    
    /**
     * 触发失败处理流程
     */
    @Async
    protected void triggerFailureHandling(ConfigApprovalEvent event, String errorMessage) {
        logger.info("触发失败处理流程: 审批ID={}, 错误信息={}", event.getAggregateId(), errorMessage);
        // 实际实现中可以触发回滚或重试流程
    }
    
    /**
     * 触发超时处理流程
     */
    @Async
    protected void triggerTimeoutHandling(ConfigApprovalEvent event) {
        logger.info("触发超时处理流程: {}", event.getAggregateId());
        // 实际实现中可以自动分配给备用审批人或升级处理
    }
    
    /**
     * 记录审批日志
     */
    @Async
    protected void recordApprovalLog(ConfigApprovalEvent event, String operation) {
        logger.info("记录审批日志: 操作={}, 事件ID={}, 聚合根ID={}, 操作人={}", 
                   operation, event.getEventId(), event.getAggregateId(), event.getOperator());
        // 实际实现中可以写入审批日志表
    }
} 