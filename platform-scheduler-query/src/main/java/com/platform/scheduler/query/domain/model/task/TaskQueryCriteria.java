package com.platform.scheduler.query.domain.model.task;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 任务查询条件值对象
 * 用于表示各种任务查询条件的组合
 * 
 * @author platform
 */
@Getter
@Builder
@ToString
public class TaskQueryCriteria {

    /**
     * 作业ID列表
     */
    private final Set<JobId> jobIds;
    
    /**
     * 作业名称（模糊匹配）
     */
    private final String jobNameLike;
    
    /**
     * 任务状态列表
     */
    private final Set<TaskStatus> statuses;
    
    /**
     * 执行器ID列表
     */
    private final Set<String> executorIds;
    
    /**
     * 开始时间范围-起始
     */
    private final LocalDateTime startTimeFrom;
    
    /**
     * 开始时间范围-结束
     */
    private final LocalDateTime startTimeTo;
    
    /**
     * 结束时间范围-起始
     */
    private final LocalDateTime endTimeFrom;
    
    /**
     * 结束时间范围-结束
     */
    private final LocalDateTime endTimeTo;
    
    /**
     * 创建时间范围-起始
     */
    private final LocalDateTime createdAtFrom;
    
    /**
     * 创建时间范围-结束
     */
    private final LocalDateTime createdAtTo;
    
    /**
     * 是否只查询失败的任务
     */
    private final Boolean onlyFailed;
    
    /**
     * 是否只查询超时的任务
     */
    private final Boolean onlyTimeout;
    
    /**
     * 创建者列表
     */
    private final Set<String> createdBy;
    
    /**
     * 最低优先级
     */
    private final Integer minPriority;
    
    /**
     * 最高优先级
     */
    private final Integer maxPriority;
    
    /**
     * 每页大小
     */
    private final Integer pageSize;
    
    /**
     * 页码
     */
    private final Integer pageNumber;
    
    /**
     * 排序字段
     */
    private final String sortField;
    
    /**
     * 是否降序
     */
    private final Boolean descending;
    
    /**
     * 是否包含参数
     */
    private final Boolean includeParameters;
    
    /**
     * 是否包含日志
     */
    private final Boolean includeLogs;

    /**
     * 创建默认查询条件
     *
     * @return 默认查询条件
     */
    public static TaskQueryCriteria createDefault() {
        return TaskQueryCriteria.builder()
                .pageSize(20)
                .pageNumber(1)
                .sortField("createdAt")
                .descending(true)
                .includeParameters(false)
                .includeLogs(false)
                .build();
    }
    
    /**
     * 创建最近24小时的查询条件
     *
     * @return 最近24小时查询条件
     */
    public static TaskQueryCriteria createLast24Hours() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        
        return TaskQueryCriteria.builder()
                .createdAtFrom(oneDayAgo)
                .createdAtTo(now)
                .pageSize(50)
                .pageNumber(1)
                .sortField("createdAt")
                .descending(true)
                .includeParameters(false)
                .includeLogs(false)
                .build();
    }
    
    /**
     * 创建失败任务查询条件
     *
     * @return 失败任务查询条件
     */
    public static TaskQueryCriteria createFailedTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysAgo = now.minusDays(3);
        
        return TaskQueryCriteria.builder()
                .onlyFailed(true)
                .createdAtFrom(threeDaysAgo)
                .createdAtTo(now)
                .pageSize(50)
                .pageNumber(1)
                .sortField("endTime")
                .descending(true)
                .includeParameters(true)
                .includeLogs(true)
                .build();
    }
    
    /**
     * 创建仅包含最近超时任务的查询条件
     *
     * @return 超时任务查询条件
     */
    public static TaskQueryCriteria createTimeoutTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        
        return TaskQueryCriteria.builder()
                .onlyTimeout(true)
                .createdAtFrom(oneDayAgo)
                .createdAtTo(now)
                .pageSize(50)
                .pageNumber(1)
                .sortField("endTime")
                .descending(true)
                .includeParameters(true)
                .includeLogs(true)
                .build();
    }
}
