package com.platform.scheduler.core.status;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.platform.scheduler.model.Task;
import com.platform.scheduler.model.TaskExecution;
import com.platform.scheduler.model.TaskProgress;
import com.platform.scheduler.model.TaskStatus;
import com.platform.scheduler.repository.TaskExecutionRepository;
import com.platform.scheduler.repository.TaskProgressRepository;
import com.platform.scheduler.repository.TaskRepository;

/**
 * 任务状态管理器
 * 
 * @author platform
 */
@Component
public class TaskStatusManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskStatusManager.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskExecutionRepository executionRepository;
    
    @Autowired
    private TaskProgressRepository progressRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 获取任务状态
     * 
     * @param taskId 任务ID
     * @return 任务状态
     */
    public TaskStatus getTaskStatus(Long taskId) {
        // 先从Redis获取
        String key = "task_status:" + taskId;
        boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        
        if (hasKey) {
            // Redis中有缓存，直接获取
            TaskStatus status = new TaskStatus();
            status.setTaskId(taskId);
            status.setStatus((String) redisTemplate.opsForHash().get(key, "status"));
            status.setCurrentProgress((Integer) redisTemplate.opsForHash().get(key, "progress"));
            status.setCurrentStatusDesc((String) redisTemplate.opsForHash().get(key, "statusDesc"));
            status.setUpdateTime(new Date((Long) redisTemplate.opsForHash().get(key, "updateTime")));
            
            // 获取最近执行ID
            Long executionId = (Long) redisTemplate.opsForHash().get(key, "executionId");
            status.setCurrentExecutionId(executionId);
            
            return status;
        } else {
            // Redis中没有缓存，查询数据库
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (!taskOpt.isPresent()) {
                logger.warn("获取任务状态失败，任务不存在: {}", taskId);
                return null;
            }
            
            Task task = taskOpt.get();
            
            // 查询最新执行记录
            TaskExecution latestExecution = executionRepository.findLatestByTaskId(taskId);
            
            // 查询执行进度
            TaskProgress progress = null;
            if (latestExecution != null) {
                Optional<TaskProgress> progressOpt = 
                        progressRepository.findByTaskIdAndExecutionId(taskId, latestExecution.getId());
                if (progressOpt.isPresent()) {
                    progress = progressOpt.get();
                }
            }
            
            // 构建状态
            TaskStatus status = new TaskStatus();
            status.setTaskId(taskId);
            status.setStatus(task.getStatus());
            status.setLastExecutionTime(task.getLastExecutionTime());
            status.setNextExecutionTime(task.getNextExecutionTime());
            status.setUpdateTime(task.getUpdatedTime());
            
            if (latestExecution != null) {
                status.setCurrentExecutionId(latestExecution.getId());
            }
            
            if (progress != null) {
                status.setCurrentProgress(progress.getProgress());
                status.setCurrentStatusDesc(progress.getStatusDesc());
            }
            
            // 更新Redis缓存
            updateStatusCache(status);
            
            return status;
        }
    }
    
    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 状态
     * @return 更新后的任务状态
     */
    public TaskStatus updateTaskStatus(Long taskId, String status) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            logger.warn("更新任务状态失败，任务不存在: {}", taskId);
            return null;
        }
        
        Task task = taskOpt.get();
        task.setStatus(status);
        task.setUpdatedTime(new Date());
        
        taskRepository.save(task);
        
        // 更新缓存
        TaskStatus taskStatus = getTaskStatus(taskId);
        updateStatusCache(taskStatus);
        
        return taskStatus;
    }
    
    /**
     * 更新任务进度
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param progress 进度
     * @param statusDesc 状态描述
     * @return 更新后的任务状态
     */
    public TaskStatus updateTaskProgress(Long taskId, Long executionId, Integer progress, String statusDesc) {
        // 更新进度记录
        TaskProgress taskProgress = new TaskProgress();
        taskProgress.setTaskId(taskId);
        taskProgress.setExecutionId(executionId);
        taskProgress.setProgress(progress);
        taskProgress.setStatusDesc(statusDesc);
        taskProgress.setUpdatedTime(new Date());
        
        progressRepository.updateProgress(taskProgress);
        
        // 更新缓存
        TaskStatus status = new TaskStatus();
        status.setTaskId(taskId);
        status.setCurrentExecutionId(executionId);
        status.setCurrentProgress(progress);
        status.setCurrentStatusDesc(statusDesc);
        status.setUpdateTime(new Date());
        
        updateStatusCache(status);
        
        return getTaskStatus(taskId);
    }
    
    /**
     * 更新任务状态缓存
     * 
     * @param status 任务状态
     */
    private void updateStatusCache(TaskStatus status) {
        String key = "task_status:" + status.getTaskId();
        
        try {
            // 构建缓存数据
            redisTemplate.opsForHash().put(key, "taskId", status.getTaskId());
            
            if (status.getStatus() != null) {
                redisTemplate.opsForHash().put(key, "status", status.getStatus());
            }
            
            if (status.getCurrentExecutionId() != null) {
                redisTemplate.opsForHash().put(key, "executionId", status.getCurrentExecutionId());
            }
            
            if (status.getCurrentProgress() != null) {
                redisTemplate.opsForHash().put(key, "progress", status.getCurrentProgress());
            }
            
            if (status.getCurrentStatusDesc() != null) {
                redisTemplate.opsForHash().put(key, "statusDesc", status.getCurrentStatusDesc());
            }
            
            redisTemplate.opsForHash().put(key, "updateTime", status.getUpdateTime() != null ? 
                    status.getUpdateTime().getTime() : System.currentTimeMillis());
            
            // 设置缓存过期时间（24小时）
            redisTemplate.expire(key, 24 * 60 * 60, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("更新任务状态缓存异常: " + status.getTaskId(), e);
        }
    }
    
    /**
     * 清除任务状态缓存
     * 
     * @param taskId 任务ID
     */
    public void clearStatusCache(Long taskId) {
        String key = "task_status:" + taskId;
        redisTemplate.delete(key);
    }
    
    /**
     * 检查并处理超时任务
     */
    public void checkTimeoutTasks() {
        List<TaskExecution> timeoutExecutions = executionRepository.findTimeout(30 * 60 * 1000); // 30分钟超时
        
        for (TaskExecution execution : timeoutExecutions) {
            logger.warn("检测到超时任务执行: {}, 任务ID: {}", execution.getId(), execution.getTaskId());
            
            // 更新执行记录
            execution.setEndTime(new Date());
            execution.setStatus("TIMEOUT");
            execution.setErrorMessage("任务执行超时");
            executionRepository.save(execution);
            
            // 更新任务状态
            Optional<Task> taskOpt = taskRepository.findById(execution.getTaskId());
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                
                if ("RUNNING".equals(task.getStatus())) {
                    task.setStatus("FAILED");
                    task.setUpdatedTime(new Date());
                    taskRepository.save(task);
                    
                    // 清除缓存
                    clearStatusCache(task.getId());
                }
            }
        }
    }
}
