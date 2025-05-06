package com.example.platform.collect.core.domain.repository;

import com.example.platform.collect.core.domain.model.ExecutionContext;
import com.example.platform.collect.core.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 执行记录仓储接口
 * 定义任务执行历史的持久化操作
 */
public interface ExecutionRepository {
    
    /**
     * 保存执行上下文
     *
     * @param context 执行上下文
     * @return 保存后的执行上下文
     */
    ExecutionContext save(ExecutionContext context);
    
    /**
     * 根据ID查找执行上下文
     *
     * @param id 执行ID
     * @return 执行上下文，如果不存在则返回空
     */
    Optional<ExecutionContext> findById(String id);
    
    /**
     * 根据任务ID查找最近的执行上下文
     *
     * @param taskId 任务ID
     * @param limit 限制数量
     * @return 执行上下文列表
     */
    List<ExecutionContext> findByTaskId(String taskId, int limit);
    
    /**
     * 根据任务ID和状态查找执行上下文
     *
     * @param taskId 任务ID
     * @param status 执行状态
     * @param limit 限制数量
     * @return 执行上下文列表
     */
    List<ExecutionContext> findByTaskIdAndStatus(String taskId, TaskStatus status, int limit);
    
    /**
     * 根据状态查找执行上下文
     *
     * @param status 执行状态
     * @param limit 限制数量
     * @return 执行上下文列表
     */
    List<ExecutionContext> findByStatus(TaskStatus status, int limit);
    
    /**
     * 查找指定时间范围内的执行上下文
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 执行上下文列表
     */
    List<ExecutionContext> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int limit);
    
    /**
     * 根据任务ID查找最后一次成功执行的上下文
     *
     * @param taskId 任务ID
     * @return 执行上下文，如果不存在则返回空
     */
    Optional<ExecutionContext> findLastSuccessful(String taskId);
    
    /**
     * 根据条件查询执行上下文
     *
     * @param criteria 查询条件
     * @param limit 限制数量
     * @return 执行上下文列表
     */
    List<ExecutionContext> findByCriteria(Map<String, Object> criteria, int limit);
    
    /**
     * 删除执行上下文
     *
     * @param id 执行ID
     */
    void deleteById(String id);
    
    /**
     * 删除指定任务的所有执行记录
     *
     * @param taskId 任务ID
     * @return 删除记录数
     */
    int deleteByTaskId(String taskId);
    
    /**
     * 删除指定时间之前的执行记录
     *
     * @param time 时间点
     * @return 删除记录数
     */
    int deleteByTimeBefore(LocalDateTime time);
    
    /**
     * 获取指定任务的执行计数
     *
     * @param taskId 任务ID
     * @return 执行总数
     */
    long countByTaskId(String taskId);
    
    /**
     * 获取指定状态的执行计数
     *
     * @param status 执行状态
     * @return 符合状态的执行数量
     */
    long countByStatus(TaskStatus status);
    
    /**
     * 获取任务的平均执行时间（毫秒）
     *
     * @param taskId 任务ID
     * @param limit 考虑的最近执行次数
     * @return 平均执行时间
     */
    double getAverageExecutionTime(String taskId, int limit);
    
    /**
     * 获取指定任务的执行统计信息
     *
     * @param taskId 任务ID
     * @return 统计信息，包括成功次数、失败次数、平均耗时等
     */
    Map<String, Object> getExecutionStatistics(String taskId);
    
    /**
     * 查找所有正在运行的执行上下文
     *
     * @return 执行上下文列表
     */
    List<ExecutionContext> findRunning();
    
    /**
     * 获取指定任务的最后执行水印
     *
     * @param taskId 任务ID
     * @return 水印信息
     */
    Map<String, Object> getLastWatermarks(String taskId);
    
    /**
     * 保存任务执行的水印信息
     *
     * @param taskId 任务ID
     * @param watermarks 水印信息
     */
    void saveWatermarks(String taskId, Map<String, Object> watermarks);
}
