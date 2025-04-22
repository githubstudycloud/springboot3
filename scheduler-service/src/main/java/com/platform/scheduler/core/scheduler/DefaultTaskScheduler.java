package com.platform.scheduler.core.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler as SpringTaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.platform.scheduler.core.executor.TaskExecutorFactory;
import com.platform.scheduler.core.lock.DistributedLockManager;
import com.platform.scheduler.model.Task;
import com.platform.scheduler.model.TaskExecution;
import com.platform.scheduler.repository.TaskExecutionRepository;
import com.platform.scheduler.repository.TaskRepository;

/**
 * 默认任务调度器实现
 * 
 * @author platform
 */
@Component
public class DefaultTaskScheduler implements TaskScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultTaskScheduler.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskExecutionRepository executionRepository;
    
    @Autowired
    private DistributedLockManager lockManager;
    
    @Autowired
    private TaskExecutorFactory executorFactory;
    
    @Autowired
    @Qualifier("schedulerExecutor")
    private ThreadPoolTaskExecutor schedulerExecutor;
    
    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    
    @Autowired
    private SpringTaskScheduler springTaskScheduler;
    
    @Value("${scheduler.task.scan-rate:5000}")
    private long scanRate;
    
    @Value("${scheduler.node.id}")
    private String nodeId;
    
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean paused = new AtomicBoolean(false);
    
    private ScheduledFuture<?> scheduledFuture;
    
    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            logger.info("启动任务调度器");
            paused.set(false);
            // 启动定时扫描
            scheduledFuture = springTaskScheduler.scheduleAtFixedRate(this::scheduleTasks, scanRate);
        }
    }
    
    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            logger.info("停止任务调度器");
            if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(false);
            }
        }
    }
    
    @Override
    public void pause() {
        if (running.get() && paused.compareAndSet(false, true)) {
            logger.info("暂停任务调度器");
        }
    }
    
    @Override
    public void resume() {
        if (running.get() && paused.compareAndSet(true, false)) {
            logger.info("恢复任务调度器");
        }
    }
    
    @Override
    public List<Task> scanTasks() {
        // 查询待执行任务
        return taskRepository.findTasksDueForExecution(new Date());
    }
    
    /**
     * 任务调度主方法，定时扫描并执行到期任务
     */
    @Scheduled(fixedRateString = "${scheduler.task.scan-rate:5000}")
    public void scheduleTasks() {
        if (!running.get() || paused.get()) {
            return;
        }
        
        logger.debug("开始扫描待执行任务");
        
        // 查询待执行任务
        List<Task> dueTasks = scanTasks();
        
        if (dueTasks.isEmpty()) {
            logger.debug("没有待执行任务");
            return;
        }
        
        logger.info("发现{}个待执行任务", dueTasks.size());
        
        // 执行任务
        for (Task task : dueTasks) {
            // 为每个任务使用独立的锁避免相互影响
            String lockKey = "task_lock:" + task.getId();
            
            try {
                // 尝试获取分布式锁
                if (lockManager.tryLock(lockKey, 30000)) {
                    try {
                        // 再次检查任务状态，确保任务仍然可执行
                        Optional<Task> latestTask = taskRepository.findById(task.getId());
                        if (latestTask.isPresent() && latestTask.get().isExecutable()) {
                            // 提交任务异步执行
                            schedulerExecutor.submit(() -> executeTaskInternal(latestTask.get()));
                        }
                    } finally {
                        // 释放锁
                        lockManager.unlock(lockKey);
                    }
                } else {
                    logger.debug("获取任务锁失败，跳过任务: {}", task.getId());
                }
            } catch (Exception e) {
                logger.error("处理任务异常: " + task.getId(), e);
            }
        }
    }
    
    /**
     * 内部执行任务方法
     * 
     * @param task 任务
     */
    private void executeTaskInternal(Task task) {
        logger.info("准备执行任务: {}", task.getId());
        
        // 1. 更新任务状态为RUNNING
        taskRepository.updateStatus(task.getId(), "RUNNING");
        
        // 2. 创建执行记录
        TaskExecution execution = new TaskExecution();
        execution.setTaskId(task.getId());
        execution.setNodeId(nodeId);
        execution.setTriggerType("SCHEDULED");
        execution.setStartTime(new Date());
        execution.setStatus("RUNNING");
        
        execution = executionRepository.save(execution);
        
        // 3. 设置任务当前执行ID
        task.setCurrentExecutionId(execution.getId());
        
        // 4. 异步执行任务
        taskExecutor.submit(() -> {
            try {
                // 获取对应的任务执行器
                com.platform.scheduler.core.executor.TaskExecutor executor = 
                        executorFactory.getExecutor(task.getType());
                
                // 执行任务
                executor.execute(task, execution.getId());
            } catch (Exception e) {
                logger.error("执行任务异常: " + task.getId(), e);
                
                // 更新执行记录状态为FAILED
                execution.setEndTime(new Date());
                execution.setStatus("FAILED");
                execution.setErrorMessage(e.getMessage());
                executionRepository.save(execution);
                
                // 处理重试逻辑
                handleRetry(task);
            }
        });
    }
    
    /**
     * 处理任务重试
     * 
     * @param task 任务
     */
    private void handleRetry(Task task) {
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
    }
    
    @Override
    public Date calculateNextExecutionTime(Task task) {
        try {
            if ("CRON".equals(task.getScheduleType()) && task.getCronExpression() != null) {
                // Cron表达式调度
                CronExpression cron = new CronExpression(task.getCronExpression());
                Date baseTime = task.getLastExecutionTime() != null ? task.getLastExecutionTime() : new Date();
                return cron.getNextValidTimeAfter(baseTime);
            } else if ("FIXED_RATE".equals(task.getScheduleType()) && task.getFixedRate() != null) {
                // 固定频率调度
                Date baseTime = task.getLastExecutionTime() != null ? task.getLastExecutionTime() : new Date();
                return new Date(baseTime.getTime() + task.getFixedRate());
            } else if ("FIXED_DELAY".equals(task.getScheduleType()) && task.getFixedDelay() != null) {
                // 固定延迟调度
                Date baseTime = task.getLastExecutionTime() != null ? task.getLastExecutionTime() : new Date();
                // 如果是固定延迟，则应该基于任务执行完成的时间
                TaskExecution lastExecution = executionRepository.findLatestByTaskId(task.getId());
                if (lastExecution != null && lastExecution.getEndTime() != null) {
                    baseTime = lastExecution.getEndTime();
                }
                return new Date(baseTime.getTime() + task.getFixedDelay());
            }
        } catch (Exception e) {
            logger.error("计算任务下次执行时间异常: " + task.getId(), e);
        }
        
        return null;
    }
    
    @Override
    public Long executeTask(Long taskId) {
        try {
            // 查询任务
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (!taskOpt.isPresent()) {
                logger.warn("任务不存在: {}", taskId);
                return null;
            }
            
            Task task = taskOpt.get();
            
            // 获取锁
            String lockKey = "task_lock:" + task.getId();
            if (!lockManager.tryLock(lockKey, 30000)) {
                logger.warn("获取任务锁失败，可能任务正在执行中: {}", taskId);
                return null;
            }
            
            try {
                // 创建执行记录
                TaskExecution execution = new TaskExecution();
                execution.setTaskId(task.getId());
                execution.setNodeId(nodeId);
                execution.setTriggerType("MANUAL");
                execution.setStartTime(new Date());
                execution.setStatus("RUNNING");
                
                execution = executionRepository.save(execution);
                
                // 设置任务当前执行ID
                task.setCurrentExecutionId(execution.getId());
                
                // 异步执行任务
                taskExecutor.submit(() -> {
                    try {
                        // 获取对应的任务执行器
                        com.platform.scheduler.core.executor.TaskExecutor executor = 
                                executorFactory.getExecutor(task.getType());
                        
                        // 执行任务
                        executor.execute(task, execution.getId());
                    } catch (Exception e) {
                        logger.error("手动执行任务异常: " + task.getId(), e);
                        
                        // 更新执行记录状态为FAILED
                        execution.setEndTime(new Date());
                        execution.setStatus("FAILED");
                        execution.setErrorMessage(e.getMessage());
                        executionRepository.save(execution);
                    }
                });
                
                return execution.getId();
            } finally {
                lockManager.unlock(lockKey);
            }
        } catch (Exception e) {
            logger.error("手动执行任务异常: " + taskId, e);
            return null;
        }
    }
    
    @Override
    public boolean terminateExecution(Long executionId) {
        try {
            // 查询执行记录
            Optional<TaskExecution> executionOpt = executionRepository.findById(executionId);
            if (!executionOpt.isPresent()) {
                logger.warn("执行记录不存在: {}", executionId);
                return false;
            }
            
            TaskExecution execution = executionOpt.get();
            
            // 检查执行状态
            if (!"RUNNING".equals(execution.getStatus())) {
                logger.warn("执行记录状态不是RUNNING，无法终止: {}, 当前状态: {}", executionId, execution.getStatus());
                return false;
            }
            
            // 更新执行记录状态为TERMINATED
            execution.setEndTime(new Date());
            execution.setStatus("TERMINATED");
            execution.setErrorMessage("用户手动终止");
            executionRepository.save(execution);
            
            // 如果任务状态是RUNNING，则更新为ENABLED
            Optional<Task> taskOpt = taskRepository.findById(execution.getTaskId());
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                if ("RUNNING".equals(task.getStatus())) {
                    task.setStatus("ENABLED");
                    // 计算下次执行时间
                    Date nextExecutionTime = calculateNextExecutionTime(task);
                    task.setNextExecutionTime(nextExecutionTime);
                    taskRepository.save(task);
                }
            }
            
            return true;
        } catch (Exception e) {
            logger.error("终止任务执行异常: " + executionId, e);
            return false;
        }
    }
    
    @Override
    public boolean isRunning() {
        return running.get();
    }
}
