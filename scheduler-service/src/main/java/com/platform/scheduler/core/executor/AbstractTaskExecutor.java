package com.platform.scheduler.core.executor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.scheduler.core.scheduler.TaskScheduler;
import com.platform.scheduler.model.Task;
import com.platform.scheduler.model.TaskExecution;
import com.platform.scheduler.model.TaskProgress;
import com.platform.scheduler.model.TaskResult;
import com.platform.scheduler.repository.TaskExecutionRepository;
import com.platform.scheduler.repository.TaskProgressRepository;
import com.platform.scheduler.repository.TaskRepository;

/**
 * 任务执行器抽象类
 * 
 * @author platform
 */
public abstract class AbstractTaskExecutor implements TaskExecutor {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    protected TaskRepository taskRepository;
    
    @Autowired
    protected TaskExecutionRepository executionRepository;
    
    @Autowired
    protected TaskProgressRepository progressRepository;
    
    @Autowired
    protected TaskScheduler taskScheduler;
    
    // 存储正在执行的任务Future，用于中断执行
    private final Map<Long, Future<?>> runningTasks = new ConcurrentHashMap<>();
    
    @Override
    public TaskResult execute(Task task, Long executionId) throws Exception {
        logger.info("开始执行任务: {}, 执行ID: {}", task.getId(), executionId);
        
        try {
            // 更新进度为0%
            updateProgress(task.getId(), executionId, 0, "任务开始执行");
            
            // 执行任务逻辑
            TaskResult result = doExecute(task, executionId);
            
            // 更新进度为100%
            updateProgress(task.getId(), executionId, 100, "任务执行完成");
            
            // 更新执行记录
            updateExecutionSuccess(task, executionId, result);
            
            // 计算下次执行时间并更新任务
            updateTaskAfterSuccess(task);
            
            logger.info("任务执行成功: {}, 执行ID: {}", task.getId(), executionId);
            
            return result;
        } catch (Exception e) {
            logger.error("任务执行异常: " + task.getId() + ", 执行ID: " + executionId, e);
            
            // 更新执行记录
            updateExecutionFailure(task, executionId, e);
            
            // 尝试任务重试
            retryTask(task);
            
            throw e;
        } finally {
            // 从运行列表中移除
            runningTasks.remove(executionId);
        }
    }
    
    /**
     * 执行任务具体逻辑，由子类实现
     * 
     * @param task 任务
     * @param executionId 执行ID
     * @return 执行结果
     * @throws Exception 执行异常
     */
    protected abstract TaskResult doExecute(Task task, Long executionId) throws Exception;
    
    @Override
    public boolean terminate(Long executionId) {
        Future<?> future = runningTasks.get(executionId);
        if (future != null && !future.isDone()) {
            logger.info("终止任务执行: {}", executionId);
            return future.cancel(true);
        }
        return false;
    }
    
    @Override
    public void updateProgress(Long taskId, Long executionId, int progress, String statusDesc) {
        try {
            logger.debug("更新任务进度: {}, 执行ID: {}, 进度: {}%, 状态: {}", 
                    taskId, executionId, progress, statusDesc);
            
            // 更新进度记录
            TaskProgress taskProgress = new TaskProgress();
            taskProgress.setTaskId(taskId);
            taskProgress.setExecutionId(executionId);
            taskProgress.setProgress(progress);
            taskProgress.setStatusDesc(statusDesc);
            taskProgress.setUpdatedTime(new Date());
            
            progressRepository.updateProgress(taskProgress);
        } catch (Exception e) {
            logger.error("更新任务进度异常: " + taskId + ", 执行ID: " + executionId, e);
        }
    }
    
    /**
     * 更新执行成功记录
     * 
     * @param task 任务
     * @param executionId 执行ID
     * @param result 执行结果
     */
    protected void updateExecutionSuccess(Task task, Long executionId, TaskResult result) {
        try {
            Optional<TaskExecution> executionOpt = executionRepository.findById(executionId);
            if (executionOpt.isPresent()) {
                TaskExecution execution = executionOpt.get();
                execution.setEndTime(new Date());
                execution.setStatus("SUCCESS");
                execution.setResult(result != null ? (result.getBody() != null ? 
                        result.getBody() : "{}") : "{}");
                executionRepository.save(execution);
            }
        } catch (Exception e) {
            logger.error("更新执行成功记录异常: " + task.getId() + ", 执行ID: " + executionId, e);
        }
    }
    
    /**
     * 更新执行失败记录
     * 
     * @param task 任务
     * @param executionId 执行ID
     * @param e 异常
     */
    protected void updateExecutionFailure(Task task, Long executionId, Exception e) {
        try {
            Optional<TaskExecution> executionOpt = executionRepository.findById(executionId);
            if (executionOpt.isPresent()) {
                TaskExecution execution = executionOpt.get();
                execution.setEndTime(new Date());
                execution.setStatus("FAILED");
                execution.setErrorMessage(e.getMessage());
                executionRepository.save(execution);
            }
        } catch (Exception ex) {
            logger.error("更新执行失败记录异常: " + task.getId() + ", 执行ID: " + executionId, ex);
        }
    }
    
    /**
     * 更新任务成功后的状态
     * 
     * @param task 任务
     */
    protected void updateTaskAfterSuccess(Task task) {
        try {
            // 重置重试次数
            task.setCurrentRetryCount(0);
            
            // 更新任务状态为ENABLED
            task.setStatus("ENABLED");
            
            // 计算下次执行时间
            Date nextExecutionTime = taskScheduler.calculateNextExecutionTime(task);
            task.setNextExecutionTime(nextExecutionTime);
            
            taskRepository.save(task);
        } catch (Exception e) {
            logger.error("更新任务成功后状态异常: " + task.getId(), e);
        }
    }
    
    /**
     * 重试任务
     * 
     * @param task 任务
     */
    protected void retryTask(Task task) {
        try {
            // 交由调度器处理重试逻辑
            if (task.getRetryCount() > 0 && task.getCurrentRetryCount() < task.getRetryCount()) {
                // 增加重试次数
                int currentRetryCount = task.getCurrentRetryCount() + 1;
                
                // 计算下次重试时间（指数退避）
                long retryInterval = task.getRetryInterval() * (1L << (currentRetryCount - 1));
                Date nextExecutionTime = new Date(System.currentTimeMillis() + retryInterval);
                
                // 更新任务状态
                task.setStatus("RETRY_PENDING");
                task.setCurrentRetryCount(currentRetryCount);
                task.setNextExecutionTime(nextExecutionTime);
                
                taskRepository.save(task);
                
                logger.info("任务{}安排第{}次重试，预计时间: {}", task.getId(), currentRetryCount, nextExecutionTime);
            } else {
                // 超过最大重试次数，标记为失败
                task.setStatus("FAILED");
                taskRepository.save(task);
                
                logger.info("任务{}重试{}次后失败", task.getId(), task.getCurrentRetryCount());
            }
        } catch (Exception e) {
            logger.error("重试任务异常: " + task.getId(), e);
        }
    }
    
    /**
     * 注册执行Future
     * 
     * @param executionId 执行ID
     * @param future Future
     */
    protected void registerRunningTask(Long executionId, Future<?> future) {
        runningTasks.put(executionId, future);
    }
}
