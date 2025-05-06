package com.platform.scheduler.infrastructure.event;

import com.platform.scheduler.domain.event.common.DomainEvent;
import com.platform.scheduler.domain.event.executor.ExecutorRegisteredEvent;
import com.platform.scheduler.domain.event.executor.ExecutorStatusChangedEvent;
import com.platform.scheduler.domain.event.job.JobCreatedEvent;
import com.platform.scheduler.domain.event.job.JobEnabledEvent;
import com.platform.scheduler.domain.event.job.JobDisabledEvent;
import com.platform.scheduler.domain.event.task.TaskCompletedEvent;
import com.platform.scheduler.domain.event.task.TaskFailedEvent;
import com.platform.scheduler.domain.event.task.TaskStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 领域事件监听器
 * 处理所有领域事件，执行相应的业务逻辑
 *
 * @author platform
 */
@Component
public class DomainEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(DomainEventListener.class);
    
    /**
     * 处理所有领域事件
     *
     * @param wrapper 领域事件包装器
     */
    @EventListener
    public void handleDomainEvent(SpringEventPublisher.DomainEventWrapper wrapper) {
        DomainEvent event = wrapper.getDomainEvent();
        logger.debug("Handling domain event: {}", event.getEventType());
        
        // 根据事件类型分发处理
        if (event instanceof JobCreatedEvent) {
            handleJobCreatedEvent((JobCreatedEvent) event);
        } else if (event instanceof JobEnabledEvent) {
            handleJobEnabledEvent((JobEnabledEvent) event);
        } else if (event instanceof JobDisabledEvent) {
            handleJobDisabledEvent((JobDisabledEvent) event);
        } else if (event instanceof TaskStartedEvent) {
            handleTaskStartedEvent((TaskStartedEvent) event);
        } else if (event instanceof TaskCompletedEvent) {
            handleTaskCompletedEvent((TaskCompletedEvent) event);
        } else if (event instanceof TaskFailedEvent) {
            handleTaskFailedEvent((TaskFailedEvent) event);
        } else if (event instanceof ExecutorRegisteredEvent) {
            handleExecutorRegisteredEvent((ExecutorRegisteredEvent) event);
        } else if (event instanceof ExecutorStatusChangedEvent) {
            handleExecutorStatusChangedEvent((ExecutorStatusChangedEvent) event);
        } else {
            logger.warn("Unhandled domain event type: {}", event.getEventType());
        }
    }
    
    /**
     * 处理作业创建事件
     *
     * @param event 作业创建事件
     */
    private void handleJobCreatedEvent(JobCreatedEvent event) {
        logger.info("Job created: {}, type: {}", event.getJobName(), event.getJobType());
        // 实际业务逻辑将在此实现，例如：
        // 1. 记录作业创建日志
        // 2. 发送作业创建通知
        // 3. 更新相关统计信息
    }
    
    /**
     * 处理作业启用事件
     *
     * @param event 作业启用事件
     */
    private void handleJobEnabledEvent(JobEnabledEvent event) {
        logger.info("Job enabled: {}", event.getJobId().getId());
        // 实际业务逻辑将在此实现，例如：
        // 1. 在调度器中启用该作业
        // 2. 发送作业启用通知
    }
    
    /**
     * 处理作业禁用事件
     *
     * @param event 作业禁用事件
     */
    private void handleJobDisabledEvent(JobDisabledEvent event) {
        logger.info("Job disabled: {}", event.getJobId().getId());
        // 实际业务逻辑将在此实现，例如：
        // 1. 在调度器中禁用该作业
        // 2. 发送作业禁用通知
    }
    
    /**
     * 处理任务开始事件
     *
     * @param event 任务开始事件
     */
    private void handleTaskStartedEvent(TaskStartedEvent event) {
        logger.info("Task started: {}, executor: {}", event.getTaskInstanceId().getId(), event.getExecutorId());
        // 实际业务逻辑将在此实现，例如：
        // 1. 记录任务开始日志
        // 2. 更新任务执行状态
        // 3. 发送任务开始通知
    }
    
    /**
     * 处理任务完成事件
     *
     * @param event 任务完成事件
     */
    private void handleTaskCompletedEvent(TaskCompletedEvent event) {
        logger.info("Task completed: {}, result: {}", event.getTaskInstanceId().getId(), event.getResult());
        // 实际业务逻辑将在此实现，例如：
        // 1. 记录任务完成日志
        // 2. 更新任务执行状态
        // 3. 触发依赖任务
        // 4. 发送任务完成通知
    }
    
    /**
     * 处理任务失败事件
     *
     * @param event 任务失败事件
     */
    private void handleTaskFailedEvent(TaskFailedEvent event) {
        logger.error("Task failed: {}, error: {}", event.getTaskInstanceId().getId(), event.getErrorMessage());
        // 实际业务逻辑将在此实现，例如：
        // 1. 记录任务失败日志
        // 2. 更新任务执行状态
        // 3. 处理失败策略（重试、告警等）
        // 4. 发送任务失败通知
    }
    
    /**
     * 处理执行器注册事件
     *
     * @param event 执行器注册事件
     */
    private void handleExecutorRegisteredEvent(ExecutorRegisteredEvent event) {
        logger.info("Executor registered: {}, type: {}, host: {}:{}", 
                event.getExecutorId().getId(), event.getType(), event.getHost(), event.getPort());
        // 实际业务逻辑将在此实现，例如：
        // 1. 更新执行器状态
        // 2. 发送执行器注册通知
    }
    
    /**
     * 处理执行器状态变更事件
     *
     * @param event 执行器状态变更事件
     */
    private void handleExecutorStatusChangedEvent(ExecutorStatusChangedEvent event) {
        logger.info("Executor status changed: {}, from: {}, to: {}, reason: {}", 
                event.getExecutorId().getId(), event.getOldStatus(), event.getNewStatus(), event.getReason());
        // 实际业务逻辑将在此实现，例如：
        // 1. 更新执行器状态
        // 2. 如果执行器离线，重新分配其任务
        // 3. 发送执行器状态变更通知
    }
}
