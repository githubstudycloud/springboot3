package com.platform.scheduler.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.scheduler.core.scheduler.TaskScheduler;
import com.platform.scheduler.exception.TaskExecutionException;
import com.platform.scheduler.exception.TaskNotFoundException;
import com.platform.scheduler.model.Task;
import com.platform.scheduler.model.TaskExecution;
import com.platform.scheduler.model.TaskResult;
import com.platform.scheduler.repository.TaskExecutionRepository;
import com.platform.scheduler.service.TaskExecutionService;
import com.platform.scheduler.service.TaskService;

/**
 * 任务执行服务实现类
 * 
 * @author platform
 */
@Service
public class TaskExecutionServiceImpl implements TaskExecutionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutionServiceImpl.class);
    
    @Autowired
    private TaskExecutionRepository taskExecutionRepository;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskScheduler taskScheduler;
    
    @Override
    @Transactional
    public Long createExecution(Long taskId, String nodeId, String triggerType) {
        // 检查任务是否存在
        if (!taskService.getTaskById(taskId).isExecutable()) {
            throw new TaskExecutionException("任务不可执行: " + taskId);
        }
        
        // 创建执行记录
        TaskExecution execution = new TaskExecution();
        execution.setTaskId(taskId);
        execution.setNodeId(nodeId);
        execution.setTriggerType(triggerType);
        execution.setStatus("CREATED");
        execution.setStartTime(new Date());
        execution.setCreatedTime(new Date());
        execution.setUpdatedTime(new Date());
        
        TaskExecution savedExecution = taskExecutionRepository.save(execution);
        logger.info("执行记录创建成功: {}, 任务ID: {}", savedExecution.getId(), taskId);
        
        // 更新任务状态为运行中
        taskService.updateTaskStatus(taskId, "RUNNING");
        
        return savedExecution.getId();
    }
    
    @Override
    @Transactional
    public boolean completeExecution(Long executionId, TaskResult result) {
        // 获取执行记录
        Optional<TaskExecution> executionOpt = taskExecutionRepository.findById(executionId);
        if (!executionOpt.isPresent()) {
            throw new TaskExecutionException("执行记录不存在: " + executionId);
        }
        
        TaskExecution execution = executionOpt.get();
        
        // 仅允许处理运行中或创建状态的执行记录
        if (!"RUNNING".equals(execution.getStatus()) && !"CREATED".equals(execution.getStatus())) {
            logger.warn("执行记录状态不正确，无法完成: {}, 当前状态: {}", executionId, execution.getStatus());
            return false;
        }
        
        // 更新执行记录
        execution.setStatus("COMPLETED");
        execution.setEndTime(new Date());
        execution.setResult(result != null ? result.getResult() : null);
        execution.setDuration(getDuration(execution.getStartTime(), execution.getEndTime()));
        execution.setUpdatedTime(new Date());
        
        taskExecutionRepository.save(execution);
        logger.info("执行记录完成: {}", executionId);
        
        // 计算下次执行时间并更新任务状态
        updateTaskAfterExecution(execution.getTaskId(), true);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean failExecution(Long executionId, String errorMessage) {
        // 获取执行记录
        Optional<TaskExecution> executionOpt = taskExecutionRepository.findById(executionId);
        if (!executionOpt.isPresent()) {
            throw new TaskExecutionException("执行记录不存在: " + executionId);
        }
        
        TaskExecution execution = executionOpt.get();
        
        // 仅允许处理运行中或创建状态的执行记录
        if (!"RUNNING".equals(execution.getStatus()) && !"CREATED".equals(execution.getStatus())) {
            logger.warn("执行记录状态不正确，无法标记失败: {}, 当前状态: {}", executionId, execution.getStatus());
            return false;
        }
        
        // 更新执行记录
        execution.setStatus("FAILED");
        execution.setEndTime(new Date());
        execution.setErrorMessage(errorMessage);
        execution.setDuration(getDuration(execution.getStartTime(), execution.getEndTime()));
        execution.setUpdatedTime(new Date());
        
        taskExecutionRepository.save(execution);
        logger.info("执行记录失败: {}, 错误: {}", executionId, errorMessage);
        
        // 处理任务重试或更新状态
        handleTaskRetry(execution.getTaskId());
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean timeoutExecution(Long executionId) {
        // 获取执行记录
        Optional<TaskExecution> executionOpt = taskExecutionRepository.findById(executionId);
        if (!executionOpt.isPresent()) {
            throw new TaskExecutionException("执行记录不存在: " + executionId);
        }
        
        TaskExecution execution = executionOpt.get();
        
        // 仅允许处理运行中或创建状态的执行记录
        if (!"RUNNING".equals(execution.getStatus()) && !"CREATED".equals(execution.getStatus())) {
            logger.warn("执行记录状态不正确，无法标记超时: {}, 当前状态: {}", executionId, execution.getStatus());
            return false;
        }
        
        // 更新执行记录
        execution.setStatus("TIMEOUT");
        execution.setEndTime(new Date());
        execution.setErrorMessage("执行超时");
        execution.setDuration(getDuration(execution.getStartTime(), execution.getEndTime()));
        execution.setUpdatedTime(new Date());
        
        taskExecutionRepository.save(execution);
        logger.info("执行记录超时: {}", executionId);
        
        // 处理任务重试或更新状态
        handleTaskRetry(execution.getTaskId());
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean terminateExecution(Long executionId) {
        // 获取执行记录
        Optional<TaskExecution> executionOpt = taskExecutionRepository.findById(executionId);
        if (!executionOpt.isPresent()) {
            throw new TaskExecutionException("执行记录不存在: " + executionId);
        }
        
        TaskExecution execution = executionOpt.get();
        
        // 仅允许处理运行中或创建状态的执行记录
        if (!"RUNNING".equals(execution.getStatus()) && !"CREATED".equals(execution.getStatus())) {
            logger.warn("执行记录状态不正确，无法终止: {}, 当前状态: {}", executionId, execution.getStatus());
            return false;
        }
        
        // 终止执行
        boolean terminated = taskScheduler.terminateExecution(executionId);
        if (!terminated) {
            logger.warn("无法终止任务执行: {}", executionId);
            return false;
        }
        
        // 更新执行记录
        execution.setStatus("TERMINATED");
        execution.setEndTime(new Date());
        execution.setErrorMessage("执行被手动终止");
        execution.setDuration(getDuration(execution.getStartTime(), execution.getEndTime()));
        execution.setUpdatedTime(new Date());
        
        taskExecutionRepository.save(execution);
        logger.info("执行记录已终止: {}", executionId);
        
        // 计算下次执行时间并更新任务状态
        updateTaskAfterExecution(execution.getTaskId(), false);
        
        return true;
    }
    
    @Override
    public TaskExecution getExecutionById(Long executionId) {
        Optional<TaskExecution> executionOpt = taskExecutionRepository.findById(executionId);
        if (!executionOpt.isPresent()) {
            throw new TaskExecutionException("执行记录不存在: " + executionId);
        }
        return executionOpt.get();
    }
    
    @Override
    public Page<TaskExecution> findExecutionsByTaskId(Long taskId, Pageable pageable) {
        // 检查任务是否存在
        if (!taskService.getTaskById(taskId).getId().equals(taskId)) {
            throw new TaskNotFoundException("任务不存在: " + taskId);
        }
        
        return taskExecutionRepository.findByTaskId(taskId, pageable);
    }
    
    @Override
    public Page<TaskExecution> findExecutionsByTaskIdAndStatus(Long taskId, String status, Pageable pageable) {
        // 检查任务是否存在
        if (!taskService.getTaskById(taskId).getId().equals(taskId)) {
            throw new TaskNotFoundException("任务不存在: " + taskId);
        }
        
        return taskExecutionRepository.findByTaskIdAndStatus(taskId, status, pageable);
    }
    
    @Override
    public TaskExecution findLatestExecution(Long taskId) {
        // 检查任务是否存在
        if (!taskService.getTaskById(taskId).getId().equals(taskId)) {
            throw new TaskNotFoundException("任务不存在: " + taskId);
        }
        
        return taskExecutionRepository.findLatestByTaskId(taskId);
    }
    
    @Override
    public List<TaskExecution> findRunningExecutions() {
        return taskExecutionRepository.findByStatus("RUNNING");
    }
    
    @Override
    @Transactional
    public int handleTimeoutExecutions(long timeoutThreshold) {
        Date thresholdTime = new Date(System.currentTimeMillis() - timeoutThreshold);
        List<TaskExecution> timeoutExecutions = taskExecutionRepository.findTimeoutExecutions(thresholdTime);
        
        int count = 0;
        for (TaskExecution execution : timeoutExecutions) {
            if (timeoutExecution(execution.getId())) {
                count++;
            }
        }
        
        logger.info("处理超时执行记录: {} 条", count);
        return count;
    }
    
    @Override
    public Page<TaskExecution> findExecutionsByNodeId(String nodeId, Pageable pageable) {
        return taskExecutionRepository.findByNodeId(nodeId, pageable);
    }
    
    @Override
    public Page<TaskExecution> findExecutionsByStatus(String status, Pageable pageable) {
        return taskExecutionRepository.findByStatus(status, pageable);
    }
    
    @Override
    public Page<TaskExecution> findExecutionsByTimeRange(Date startTime, Date endTime, Pageable pageable) {
        return taskExecutionRepository.findByTimeRange(startTime, endTime, pageable);
    }
    
    @Override
    public Page<TaskExecution> findExecutionsByTaskIdAndTimeRange(Long taskId, Date startTime, Date endTime,
            Pageable pageable) {
        // 检查任务是否存在
        if (!taskService.getTaskById(taskId).getId().equals(taskId)) {
            throw new TaskNotFoundException("任务不存在: " + taskId);
        }
        
        return taskExecutionRepository.findByTaskIdAndTimeRange(taskId, startTime, endTime, pageable);
    }
    
    @Override
    public double calculateSuccessRate(Long taskId, Date startTime, Date endTime) {
        // 检查任务是否存在
        if (!taskService.getTaskById(taskId).getId().equals(taskId)) {
            throw new TaskNotFoundException("任务不存在: " + taskId);
        }
        
        int totalCount = taskExecutionRepository.countByTaskIdAndTimeRange(taskId, startTime, endTime);
        if (totalCount == 0) {
            return 0.0;
        }
        
        int successCount = taskExecutionRepository.countByTaskIdAndStatusAndTimeRange(
                taskId, "COMPLETED", startTime, endTime);
        
        return (double) successCount / totalCount * 100;
    }
    
    @Override
    public long calculateAverageExecutionTime(Long taskId, Date startTime, Date endTime) {
        // 检查任务是否存在
        if (!taskService.getTaskById(taskId).getId().equals(taskId)) {
            throw new TaskNotFoundException("任务不存在: " + taskId);
        }
        
        Double avgDuration = taskExecutionRepository.calculateAverageDuration(
                taskId, "COMPLETED", startTime, endTime);
        
        return avgDuration != null ? Math.round(avgDuration) : 0;
    }
    
    /**
     * 计算执行时长
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时长（毫秒）
     */
    private long getDuration(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return endTime.getTime() - startTime.getTime();
    }
    
    /**
     * 处理任务重试逻辑
     * 
     * @param taskId 任务ID
     */
    private void handleTaskRetry(Long taskId) {
        // 获取任务信息
        Task task = taskService.getTaskById(taskId);
        
        // 检查是否需要重试
        Integer retryCount = task.getRetryCount();
        Integer currentRetryCount = task.getCurrentRetryCount();
        
        if (retryCount != null && retryCount > 0 && currentRetryCount < retryCount) {
            // 更新当前重试次数
            task.setCurrentRetryCount(currentRetryCount + 1);
            task.setStatus("RETRY_PENDING");
            
            // 计算重试延迟时间
            Long retryInterval = task.getRetryInterval();
            Date nextExecutionTime = new Date();
            if (retryInterval != null && retryInterval > 0) {
                nextExecutionTime = new Date(System.currentTimeMillis() + retryInterval);
            }
            task.setNextExecutionTime(nextExecutionTime);
            
            // 保存任务状态
            taskService.updateTask(task);
            logger.info("任务将进行重试: {}, 当前重试次数: {}/{}", taskId, task.getCurrentRetryCount(), task.getRetryCount());
        } else {
            // 不再重试，标记任务失败
            taskService.updateTaskStatus(taskId, "FAILED");
            logger.info("任务执行失败且不再重试: {}", taskId);
        }
    }
    
    /**
     * 任务执行完成后更新任务状态和下次执行时间
     * 
     * @param taskId 任务ID
     * @param success 是否执行成功
     */
    private void updateTaskAfterExecution(Long taskId, boolean success) {
        // 获取任务信息
        Task task = taskService.getTaskById(taskId);
        
        // 更新上次执行时间
        task.setLastExecutionTime(new Date());
        
        // 重置重试计数
        if (success) {
            task.setCurrentRetryCount(0);
        }
        
        // 计算下次执行时间
        if (task.isEnabled()) {
            Date nextExecutionTime = taskService.calculateNextExecutionTime(task);
            task.setNextExecutionTime(nextExecutionTime);
            task.setStatus("ENABLED");
        } else {
            task.setStatus(success ? "COMPLETED" : "FAILED");
        }
        
        // 保存任务状态
        taskService.updateTask(task);
        logger.info("任务执行完成后状态已更新: {}, 状态: {}", taskId, task.getStatus());
    }
}
