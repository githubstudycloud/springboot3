package com.platform.scheduler.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.scheduler.model.TaskExecution;
import com.platform.scheduler.repository.base.BaseRepository;

/**
 * 任务执行记录数据访问层接口
 * 
 * @author platform
 */
public interface TaskExecutionRepository extends BaseRepository<TaskExecution, Long> {
    
    /**
     * 根据任务ID查询执行记录
     * 
     * @param taskId 任务ID
     * @return 执行记录列表
     */
    List<TaskExecution> findByTaskId(Long taskId);
    
    /**
     * 根据任务ID分页查询执行记录
     * 
     * @param taskId 任务ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findByTaskId(Long taskId, Pageable pageable);
    
    /**
     * 根据任务ID和状态查询执行记录
     * 
     * @param taskId 任务ID
     * @param status 执行状态
     * @return 执行记录列表
     */
    List<TaskExecution> findByTaskIdAndStatus(Long taskId, String status);
    
    /**
     * 根据任务ID和状态分页查询执行记录
     * 
     * @param taskId 任务ID
     * @param status 执行状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findByTaskIdAndStatus(Long taskId, String status, Pageable pageable);
    
    /**
     * 根据状态查询执行记录
     * 
     * @param status 执行状态
     * @return 执行记录列表
     */
    List<TaskExecution> findByStatus(String status);
    
    /**
     * 根据状态分页查询执行记录
     * 
     * @param status 执行状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findByStatus(String status, Pageable pageable);
    
    /**
     * 根据节点ID查询执行记录
     * 
     * @param nodeId 节点ID
     * @return 执行记录列表
     */
    List<TaskExecution> findByNodeId(String nodeId);
    
    /**
     * 根据节点ID分页查询执行记录
     * 
     * @param nodeId 节点ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findByNodeId(String nodeId, Pageable pageable);
    
    /**
     * 查询正在运行的执行记录
     * 
     * @return 执行记录列表
     */
    List<TaskExecution> findRunning();
    
    /**
     * 查询任务的最近一次执行记录
     * 
     * @param taskId 任务ID
     * @return 执行记录
     */
    TaskExecution findLatestByTaskId(Long taskId);
    
    /**
     * 查询超时的执行记录
     * 
     * @param timeout 超时时间（毫秒）
     * @return 执行记录列表
     */
    List<TaskExecution> findTimeout(long timeout);
    
    /**
     * 更新执行记录状态
     * 
     * @param executionId 执行ID
     * @param status 执行状态
     * @return 更新行数
     */
    int updateStatus(Long executionId, String status);
    
    /**
     * 更新执行结束时间和状态
     * 
     * @param executionId 执行ID
     * @param endTime 结束时间
     * @param status 执行状态
     * @return 更新行数
     */
    int updateEndTimeAndStatus(Long executionId, Date endTime, String status);
    
    /**
     * 更新执行结果
     * 
     * @param executionId 执行ID
     * @param result 执行结果
     * @return 更新行数
     */
    int updateResult(Long executionId, String result);
    
    /**
     * 更新错误信息
     * 
     * @param executionId 执行ID
     * @param errorMessage 错误信息
     * @return 更新行数
     */
    int updateErrorMessage(Long executionId, String errorMessage);
    
    /**
     * 根据时间范围查询执行记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findByStartTimeBetween(Date startTime, Date endTime, Pageable pageable);
    
    /**
     * 根据任务ID和时间范围查询执行记录
     * 
     * @param taskId 任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<TaskExecution> findByTaskIdAndStartTimeBetween(Long taskId, Date startTime, Date endTime, Pageable pageable);
}
