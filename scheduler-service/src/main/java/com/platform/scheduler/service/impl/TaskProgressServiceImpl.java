package com.platform.scheduler.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.scheduler.model.TaskProgress;
import com.platform.scheduler.repository.TaskProgressRepository;
import com.platform.scheduler.service.TaskProgressService;

/**
 * 任务进度服务实现类
 * 
 * @author platform
 */
@Service
public class TaskProgressServiceImpl implements TaskProgressService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskProgressServiceImpl.class);
    
    @Autowired
    private TaskProgressRepository progressRepository;
    
    @Override
    @Transactional
    public Long updateProgress(Long taskId, Long executionId, int progress, String statusDesc) {
        // 校验进度值
        if (progress < 0) {
            progress = 0;
        } else if (progress > 100) {
            progress = 100;
        }
        
        // 查询现有进度记录
        TaskProgress existingProgress = progressRepository.findByTaskIdAndExecutionId(taskId, executionId);
        
        // 无记录时创建新记录，有记录时更新
        TaskProgress progressRecord;
        if (existingProgress == null) {
            progressRecord = new TaskProgress();
            progressRecord.setTaskId(taskId);
            progressRecord.setExecutionId(executionId);
            progressRecord.setProgress(progress);
            progressRecord.setStatusDesc(statusDesc);
            progressRecord.setCreatedTime(new Date());
            progressRecord.setUpdatedTime(new Date());
        } else {
            progressRecord = existingProgress;
            progressRecord.setProgress(progress);
            progressRecord.setStatusDesc(statusDesc);
            progressRecord.setUpdatedTime(new Date());
        }
        
        // 当进度为100%时，自动设置为完成状态
        if (progress == 100) {
            progressRecord.setCompleted(true);
            progressRecord.setCompletedTime(new Date());
        }
        
        // 保存进度记录
        TaskProgress savedProgress = progressRepository.save(progressRecord);
        logger.debug("任务进度已更新: 任务ID={}, 执行ID={}, 进度={}%", taskId, executionId, progress);
        
        return savedProgress.getId();
    }
    
    @Override
    @Transactional
    public boolean completeProgress(Long taskId, Long executionId) {
        // 查询现有进度记录
        TaskProgress existingProgress = progressRepository.findByTaskIdAndExecutionId(taskId, executionId);
        
        // 无记录时创建新进度记录，有记录时更新
        TaskProgress progressRecord;
        if (existingProgress == null) {
            progressRecord = new TaskProgress();
            progressRecord.setTaskId(taskId);
            progressRecord.setExecutionId(executionId);
            progressRecord.setProgress(100);
            progressRecord.setStatusDesc("任务已完成");
            progressRecord.setCreatedTime(new Date());
        } else {
            progressRecord = existingProgress;
            progressRecord.setProgress(100);
            if (progressRecord.getStatusDesc() == null) {
                progressRecord.setStatusDesc("任务已完成");
            }
        }
        
        // 设置完成状态
        progressRecord.setCompleted(true);
        progressRecord.setCompletedTime(new Date());
        progressRecord.setUpdatedTime(new Date());
        
        // 保存进度记录
        progressRepository.save(progressRecord);
        logger.debug("任务进度已标记为完成: 任务ID={}, 执行ID={}", taskId, executionId);
        
        return true;
    }
    
    @Override
    public TaskProgress getProgress(Long taskId, Long executionId) {
        return progressRepository.findByTaskIdAndExecutionId(taskId, executionId);
    }
    
    @Override
    public TaskProgress getLatestProgress(Long taskId) {
        return progressRepository.findLatestByTaskId(taskId);
    }
    
    @Override
    public TaskProgress getProgressByExecutionId(Long executionId) {
        return progressRepository.findByExecutionId(executionId);
    }
    
    @Override
    public Page<TaskProgress> findProgressHistory(Long taskId, Pageable pageable) {
        return progressRepository.findByTaskId(taskId, pageable);
    }
    
    @Override
    @Transactional
    public int cleanupCompletedProgress(int retentionCount) {
        // 查询需要清理的任务ID列表
        List<Long> taskIds = progressRepository.findTaskIdsWithCompletedProgress();
        
        int totalDeleted = 0;
        for (Long taskId : taskIds) {
            // 保留每个任务最近的N条记录，删除其余记录
            int deleted = progressRepository.deleteOldCompletedProgress(taskId, retentionCount);
            totalDeleted += deleted;
        }
        
        logger.info("已清理历史进度记录: {} 条，每个任务保留 {} 条最新记录", totalDeleted, retentionCount);
        return totalDeleted;
    }
}
