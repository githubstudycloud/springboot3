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
import com.platform.scheduler.exception.TaskNotFoundException;
import com.platform.scheduler.model.Task;
import com.platform.scheduler.repository.TaskRepository;
import com.platform.scheduler.service.TaskService;
import com.platform.scheduler.util.CronUtils;

/**
 * 任务服务实现类
 * 
 * @author platform
 */
@Service
public class TaskServiceImpl implements TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskScheduler taskScheduler;
    
    @Override
    @Transactional
    public Task createTask(Task task) {
        // 设置任务状态为已创建
        if (task.getStatus() == null) {
            task.setStatus("CREATED");
        }
        
        // 设置创建时间和更新时间
        Date now = new Date();
        task.setCreatedTime(now);
        task.setUpdatedTime(now);
        
        // 设置初始重试次数
        if (task.getCurrentRetryCount() == null) {
            task.setCurrentRetryCount(0);
        }
        
        // 计算下次执行时间
        Date nextExecutionTime = calculateNextExecutionTime(task);
        task.setNextExecutionTime(nextExecutionTime);
        
        // 保存任务
        Task savedTask = taskRepository.save(task);
        logger.info("任务创建成功: {}", savedTask.getId());
        
        return savedTask;
    }
    
    @Override
    @Transactional
    public Task updateTask(Task task) {
        // 检查任务是否存在
        Optional<Task> existingTaskOpt = taskRepository.findById(task.getId());
        if (!existingTaskOpt.isPresent()) {
            throw new TaskNotFoundException("任务不存在: " + task.getId());
        }
        Task existingTask = existingTaskOpt.get();
        
        // 保留原始创建时间
        task.setCreatedTime(existingTask.getCreatedTime());
        
        // 设置更新时间
        task.setUpdatedTime(new Date());
        
        // 如果调度相关参数发生变化，重新计算下次执行时间
        if (isScheduleChanged(existingTask, task)) {
            Date nextExecutionTime = calculateNextExecutionTime(task);
            task.setNextExecutionTime(nextExecutionTime);
        }
        
        // 保存任务
        Task updatedTask = taskRepository.save(task);
        logger.info("任务更新成功: {}", updatedTask.getId());
        
        return updatedTask;
    }
    
    /**
     * 判断任务调度参数是否发生变化
     * 
     * @param oldTask 原任务
     * @param newTask 新任务
     * @return 是否变化
     */
    private boolean isScheduleChanged(Task oldTask, Task newTask) {
        if (!equals(oldTask.getScheduleType(), newTask.getScheduleType())) {
            return true;
        }
        
        if ("CRON".equals(newTask.getScheduleType())) {
            return !equals(oldTask.getCronExpression(), newTask.getCronExpression());
        } else if ("FIXED_RATE".equals(newTask.getScheduleType())) {
            return !equals(oldTask.getFixedRate(), newTask.getFixedRate()) || 
                   !equals(oldTask.getInitialDelay(), newTask.getInitialDelay());
        } else if ("FIXED_DELAY".equals(newTask.getScheduleType())) {
            return !equals(oldTask.getFixedDelay(), newTask.getFixedDelay()) || 
                   !equals(oldTask.getInitialDelay(), newTask.getInitialDelay());
        }
        
        return false;
    }
    
    /**
     * 比较两个对象是否相等，处理null情况
     */
    private boolean equals(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }
    
    @Override
    public Task getTaskById(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new TaskNotFoundException("任务不存在: " + taskId);
        }
        return taskOpt.get();
    }
    
    @Override
    @Transactional
    public boolean deleteTask(Long taskId) {
        // 检查任务是否存在
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("任务不存在: " + taskId);
        }
        
        taskRepository.deleteById(taskId);
        logger.info("任务删除成功: {}", taskId);
        
        return true;
    }
    
    @Override
    @Transactional
    public Task enableTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setStatus("ENABLED");
        task.setUpdatedTime(new Date());
        
        // 如果下次执行时间为空，则计算下次执行时间
        if (task.getNextExecutionTime() == null) {
            Date nextExecutionTime = calculateNextExecutionTime(task);
            task.setNextExecutionTime(nextExecutionTime);
        }
        
        Task enabledTask = taskRepository.save(task);
        logger.info("任务已启用: {}", taskId);
        
        return enabledTask;
    }
    
    @Override
    @Transactional
    public Task disableTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setStatus("DISABLED");
        task.setUpdatedTime(new Date());
        
        Task disabledTask = taskRepository.save(task);
        logger.info("任务已禁用: {}", taskId);
        
        return disabledTask;
    }
    
    @Override
    @Transactional
    public Task pauseTask(Long taskId) {
        Task task = getTaskById(taskId);
        task.setStatus("PAUSED");
        task.setUpdatedTime(new Date());
        
        Task pausedTask = taskRepository.save(task);
        logger.info("任务已暂停: {}", taskId);
        
        return pausedTask;
    }
    
    @Override
    @Transactional
    public Task resumeTask(Long taskId) {
        Task task = getTaskById(taskId);
        
        // 只有暂停状态的任务可以恢复
        if (!"PAUSED".equals(task.getStatus())) {
            logger.warn("只有暂停状态的任务可以恢复: {}, 当前状态: {}", taskId, task.getStatus());
            return task;
        }
        
        task.setStatus("ENABLED");
        task.setUpdatedTime(new Date());
        
        // 如果下次执行时间为空或已过期，则重新计算下次执行时间
        Date now = new Date();
        if (task.getNextExecutionTime() == null || task.getNextExecutionTime().before(now)) {
            Date nextExecutionTime = calculateNextExecutionTime(task);
            task.setNextExecutionTime(nextExecutionTime);
        }
        
        Task resumedTask = taskRepository.save(task);
        logger.info("任务已恢复: {}", taskId);
        
        return resumedTask;
    }
    
    @Override
    @Transactional
    public Long triggerTask(Long taskId) {
        // 检查任务是否存在
        if (!taskRepository.existsById(taskId)) {
            throw new TaskNotFoundException("任务不存在: " + taskId);
        }
        
        // 立即执行任务
        Long executionId = taskScheduler.executeTask(taskId);
        logger.info("任务触发成功: {}, 执行ID: {}", taskId, executionId);
        
        return executionId;
    }
    
    @Override
    public Page<Task> findTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }
    
    @Override
    public Page<Task> findTasksByStatus(String status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }
    
    @Override
    public Page<Task> findTasksByName(String name, Pageable pageable) {
        return taskRepository.findByNameContaining(name, pageable);
    }
    
    @Override
    public Page<Task> findTasksByStatusAndName(String status, String name, Pageable pageable) {
        return taskRepository.findByStatusAndNameContaining(status, name, pageable);
    }
    
    @Override
    public Page<Task> findTasksByType(String type, Pageable pageable) {
        return taskRepository.findByType(type, pageable);
    }
    
    @Override
    public Page<Task> findTasksByStatusAndType(String status, String type, Pageable pageable) {
        return taskRepository.findByStatusAndType(status, type, pageable);
    }
    
    @Override
    public Page<Task> findTasksByStatusAndTypeAndName(String status, String type, String name, Pageable pageable) {
        return taskRepository.findByStatusAndTypeAndNameContaining(status, type, name, pageable);
    }
    
    @Override
    public List<Task> findTasksDueForExecution() {
        return taskRepository.findTasksDueForExecution(new Date());
    }
    
    @Override
    @Transactional
    public boolean updateTaskStatus(Long taskId, String status) {
        int rows = taskRepository.updateStatus(taskId, status);
        boolean result = rows > 0;
        if (result) {
            logger.info("任务状态更新成功: {}, 状态: {}", taskId, status);
        } else {
            logger.warn("任务状态更新失败: {}, 状态: {}", taskId, status);
        }
        return result;
    }
    
    @Override
    @Transactional
    public boolean updateNextExecutionTime(Long taskId, Date nextExecutionTime) {
        int rows = taskRepository.updateNextExecutionTime(taskId, nextExecutionTime);
        boolean result = rows > 0;
        if (result) {
            logger.info("任务下次执行时间更新成功: {}, 时间: {}", taskId, nextExecutionTime);
        } else {
            logger.warn("任务下次执行时间更新失败: {}, 时间: {}", taskId, nextExecutionTime);
        }
        return result;
    }
    
    @Override
    public Date calculateNextExecutionTime(Task task) {
        return taskScheduler.calculateNextExecutionTime(task);
    }
}
