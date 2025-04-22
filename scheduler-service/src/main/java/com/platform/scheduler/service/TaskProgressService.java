package com.platform.scheduler.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.TaskProgress;

/**
 * 任务进度服务接口
 * 
 * @author platform
 */
public interface TaskProgressService {
    
    /**
     * 创建或更新任务进度
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @param progress 进度百分比(0-100)
     * @param statusDesc 状态描述
     * @return 进度ID
     */
    Long updateProgress(Long taskId, Long executionId, int progress, String statusDesc);
    
    /**
     * 完成任务进度
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @return 是否更新成功
     */
    boolean completeProgress(Long taskId, Long executionId);
    
    /**
     * 根据任务ID和执行ID查询进度
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @return 任务进度
     */
    TaskProgress getProgress(Long taskId, Long executionId);
    
    /**
     * 根据任务ID查询最新进度
     * 
     * @param taskId 任务ID
     * @return 任务进度
     */
    TaskProgress getLatestProgress(Long taskId);
    
    /**
     * 根据执行ID查询进度
     * 
     * @param executionId 执行ID
     * @return 任务进度
     */
    TaskProgress getProgressByExecutionId(Long executionId);
    
    /**
     * 根据任务ID分页查询进度历史
     * 
     * @param taskId 任务ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskProgress> findProgressHistory(Long taskId, Pageable pageable);
    
    /**
     * 清理已完成的进度记录
     * 
     * @param retentionCount 每个任务保留的记录数
     * @return 清理的记录数
     */
    int cleanupCompletedProgress(int retentionCount);
}
