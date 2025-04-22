package com.platform.scheduler.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.TaskExecution;
import com.platform.scheduler.model.TaskResult;

/**
 * 任务执行服务接口
 * 
 * @author platform
 */
public interface TaskExecutionService {
    
    /**
     * 创建执行记录
     * 
     * @param taskId 任务ID
     * @param nodeId 节点ID
     * @param triggerType 触发类型
     * @return 执行ID
     */
    Long createExecution(Long taskId, String nodeId, String triggerType);
    
    /**
     * 完成执行
     * 
     * @param executionId 执行ID
     * @param result 执行结果
     * @return 是否完成成功
     */
    boolean completeExecution(Long executionId, TaskResult result);
    
    /**
     * 标记执行失败
     * 
     * @param executionId 执行ID
     * @param errorMessage 错误信息
     * @return 是否标记成功
     */
    boolean failExecution(Long executionId, String errorMessage);
    
    /**
     * 标记执行超时
     * 
     * @param executionId 执行ID
     * @return 是否标记成功
     */
    boolean timeoutExecution(Long executionId);
    
    /**
     * 终止执行
     * 
     * @param executionId 执行ID
     * @return 是否终止成功
     */
    boolean terminateExecution(Long executionId);
    
    /**
     * 根据ID查询执行记录
     * 
     * @param executionId 执行ID
     * @return 执行记录
     */
    TaskExecution getExecutionById(Long executionId);
    
    /**
     * 根据任务ID查询执行记录
     * 
     * @param taskId 任务ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findExecutionsByTaskId(Long taskId, Pageable pageable);
    
    /**
     * 根据任务ID和状态查询执行记录
     * 
     * @param taskId 任务ID
     * @param status 执行状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findExecutionsByTaskIdAndStatus(Long taskId, String status, Pageable pageable);
    
    /**
     * 查询任务的最近一次执行记录
     * 
     * @param taskId 任务ID
     * @return 执行记录
     */
    TaskExecution findLatestExecution(Long taskId);
    
    /**
     * 查询正在运行的执行记录
     * 
     * @return 执行记录列表
     */
    List<TaskExecution> findRunningExecutions();
    
    /**
     * 检查并处理超时执行
     * 
     * @param timeoutThreshold 超时阈值（毫秒）
     * @return 处理的记录数
     */
    int handleTimeoutExecutions(long timeoutThreshold);
    
    /**
     * 根据节点ID查询执行记录
     * 
     * @param nodeId 节点ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findExecutionsByNodeId(String nodeId, Pageable pageable);
    
    /**
     * 根据状态查询执行记录
     * 
     * @param status 执行状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findExecutionsByStatus(String status, Pageable pageable);
    
    /**
     * 根据时间范围查询执行记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findExecutionsByTimeRange(Date startTime, Date endTime, Pageable pageable);
    
    /**
     * 根据任务ID和时间范围查询执行记录
     * 
     * @param taskId 任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findExecutionsByTaskIdAndTimeRange(Long taskId, Date startTime, Date endTime, Pageable pageable);
    
    /**
     * 统计任务执行成功率
     * 
     * @param taskId 任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 成功率（0-100）
     */
    double calculateSuccessRate(Long taskId, Date startTime, Date endTime);
    
    /**
     * 统计任务平均执行时间
     * 
     * @param taskId 任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均执行时间（毫秒）
     */
    long calculateAverageExecutionTime(Long taskId, Date startTime, Date endTime);
}
