package com.platform.scheduler.query.domain.service;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import com.platform.scheduler.query.domain.model.task.TaskInstanceView;
import com.platform.scheduler.query.domain.model.task.TaskQueryCriteria;
import com.platform.scheduler.query.domain.model.task.TaskSearchResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 任务查询服务接口
 * 定义任务查询相关的领域服务
 * 
 * @author platform
 */
public interface TaskStatusQueryService {

    /**
     * 根据任务实例ID查询任务详情
     *
     * @param taskInstanceId 任务实例ID
     * @return 任务实例视图
     */
    Optional<TaskInstanceView> findTaskById(TaskInstanceId taskInstanceId);
    
    /**
     * 根据多个任务实例ID批量查询任务详情
     *
     * @param taskInstanceIds 任务实例ID列表
     * @return 任务实例视图列表
     */
    List<TaskInstanceView> findTasksByIds(List<TaskInstanceId> taskInstanceIds);
    
    /**
     * 根据作业ID查询最近一次执行的任务实例
     *
     * @param jobId 作业ID
     * @return 任务实例视图
     */
    Optional<TaskInstanceView> findLatestTaskByJobId(JobId jobId);
    
    /**
     * 根据作业ID查询任务历史列表
     *
     * @param jobId 作业ID
     * @param limit 最大记录数
     * @return 任务实例视图列表
     */
    List<TaskInstanceView> findTaskHistoryByJobId(JobId jobId, int limit);
    
    /**
     * 查询正在运行的任务列表
     *
     * @param limit 最大记录数
     * @return 任务实例视图列表
     */
    List<TaskInstanceView> findRunningTasks(int limit);
    
    /**
     * 查询失败的任务列表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 最大记录数
     * @return 任务实例视图列表
     */
    List<TaskInstanceView> findFailedTasks(LocalDateTime startTime, LocalDateTime endTime, int limit);
    
    /**
     * 高级查询任务列表
     *
     * @param criteria 查询条件
     * @return 任务搜索结果
     */
    TaskSearchResult<TaskInstanceView> searchTasks(TaskQueryCriteria criteria);
    
    /**
     * 查询等待重试的任务
     *
     * @param limit 最大记录数
     * @return 任务实例视图列表
     */
    List<TaskInstanceView> findTasksWaitingRetry(int limit);
    
    /**
     * 根据执行器ID查询任务列表
     *
     * @param executorId 执行器ID
     * @param limit 最大记录数
     * @return 任务实例视图列表
     */
    List<TaskInstanceView> findTasksByExecutorId(String executorId, int limit);
    
    /**
     * 查询超时的任务列表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 最大记录数
     * @return 任务实例视图列表
     */
    List<TaskInstanceView> findTimeoutTasks(LocalDateTime startTime, LocalDateTime endTime, int limit);
    
    /**
     * 获取任务实例详情，包括参数和日志
     *
     * @param taskInstanceId 任务实例ID
     * @return 包含完整信息的任务实例视图
     */
    Optional<TaskInstanceView> getTaskDetails(TaskInstanceId taskInstanceId);
}
