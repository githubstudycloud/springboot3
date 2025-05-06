package com.platform.scheduler.domain.repository;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstance;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import com.platform.scheduler.domain.model.task.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 任务实例仓储接口
 * 定义对任务实例聚合根的持久化操作
 * 
 * @author platform
 */
public interface TaskInstanceRepository {
    
    /**
     * 保存任务实例
     *
     * @param taskInstance 任务实例实体
     * @return 保存后的任务实例实体
     */
    TaskInstance save(TaskInstance taskInstance);
    
    /**
     * 根据ID查找任务实例
     *
     * @param taskInstanceId 任务实例ID
     * @return 任务实例实体，如果不存在则返回空
     */
    Optional<TaskInstance> findById(TaskInstanceId taskInstanceId);
    
    /**
     * 获取所有任务实例
     *
     * @return 任务实例列表
     */
    List<TaskInstance> findAll();
    
    /**
     * 根据状态查找任务实例
     *
     * @param status 任务状态
     * @return 任务实例列表
     */
    List<TaskInstance> findByStatus(TaskStatus status);
    
    /**
     * 根据作业ID查找任务实例
     *
     * @param jobId 作业ID
     * @return 任务实例列表
     */
    List<TaskInstance> findByJobId(JobId jobId);
    
    /**
     * 根据作业ID和状态查找任务实例
     *
     * @param jobId 作业ID
     * @param status 任务状态
     * @return 任务实例列表
     */
    List<TaskInstance> findByJobIdAndStatus(JobId jobId, TaskStatus status);
    
    /**
     * 根据执行器ID查找任务实例
     *
     * @param executorId 执行器ID
     * @return 任务实例列表
     */
    List<TaskInstance> findByExecutorId(String executorId);
    
    /**
     * 查找指定时间段内创建的任务实例
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务实例列表
     */
    List<TaskInstance> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找等待重试的任务实例
     *
     * @param currentTime 当前时间
     * @return 等待重试的任务实例列表
     */
    List<TaskInstance> findWaitingRetryTasks(LocalDateTime currentTime);
    
    /**
     * 查找运行中但已超时的任务实例
     *
     * @param currentTime 当前时间
     * @return 超时的任务实例列表
     */
    List<TaskInstance> findTimedOutTasks(LocalDateTime currentTime);
    
    /**
     * 获取作业最近一次执行的任务实例
     *
     * @param jobId 作业ID
     * @return 最近一次执行的任务实例，如果不存在则返回空
     */
    Optional<TaskInstance> findLatestByJobId(JobId jobId);
    
    /**
     * 获取作业最近一次成功执行的任务实例
     *
     * @param jobId 作业ID
     * @return 最近一次成功执行的任务实例，如果不存在则返回空
     */
    Optional<TaskInstance> findLatestSuccessByJobId(JobId jobId);
    
    /**
     * 删除任务实例
     *
     * @param taskInstanceId 任务实例ID
     */
    void delete(TaskInstanceId taskInstanceId);
    
    /**
     * 清理指定时间之前的历史任务实例
     *
     * @param beforeTime 清理该时间之前的任务实例
     * @param excludeStatuses 排除这些状态的任务实例
     * @return 清理的任务实例数量
     */
    int cleanHistoricalTasks(LocalDateTime beforeTime, List<TaskStatus> excludeStatuses);
}
