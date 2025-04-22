package com.platform.scheduler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.TaskProgress;
import com.platform.scheduler.repository.base.BaseRepository;

/**
 * 任务进度数据访问层接口
 * 
 * @author platform
 */
public interface TaskProgressRepository extends BaseRepository<TaskProgress, Long> {
    
    /**
     * 根据任务ID查询进度
     * 
     * @param taskId 任务ID
     * @return 进度列表
     */
    List<TaskProgress> findByTaskId(Long taskId);
    
    /**
     * 根据任务ID分页查询进度
     * 
     * @param taskId 任务ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskProgress> findByTaskId(Long taskId, Pageable pageable);
    
    /**
     * 根据执行ID查询进度
     * 
     * @param executionId 执行ID
     * @return 进度
     */
    Optional<TaskProgress> findByExecutionId(Long executionId);
    
    /**
     * 根据任务ID和执行ID查询进度
     * 
     * @param taskId 任务ID
     * @param executionId 执行ID
     * @return 进度
     */
    Optional<TaskProgress> findByTaskIdAndExecutionId(Long taskId, Long executionId);
    
    /**
     * 更新进度
     * 
     * @param progress 进度实体
     * @return 更新后的进度实体
     */
    TaskProgress updateProgress(TaskProgress progress);
    
    /**
     * 删除任务的所有进度
     * 
     * @param taskId 任务ID
     */
    void deleteByTaskId(Long taskId);
    
    /**
     * 删除执行的进度
     * 
     * @param executionId 执行ID
     */
    void deleteByExecutionId(Long executionId);
}
