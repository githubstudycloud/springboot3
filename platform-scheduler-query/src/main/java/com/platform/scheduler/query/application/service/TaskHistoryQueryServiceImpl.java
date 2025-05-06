package com.platform.scheduler.query.application.service;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import com.platform.scheduler.domain.model.task.TaskStatus;
import com.platform.scheduler.query.domain.model.task.TaskInstanceView;
import com.platform.scheduler.query.domain.model.task.TaskQueryCriteria;
import com.platform.scheduler.query.domain.model.task.TaskSearchResult;
import com.platform.scheduler.query.domain.service.TaskHistoryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务历史查询服务实现
 * 
 * @author platform
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskHistoryQueryServiceImpl implements TaskHistoryQueryService {

    /**
     * 任务视图仓储接口，通过构造器注入
     */
    private final com.platform.scheduler.query.application.port.out.TaskInstanceViewRepository taskInstanceViewRepository;
    
    /**
     * 任务日志仓储接口，通过构造器注入
     */
    private final com.platform.scheduler.query.application.port.out.TaskLogRepository taskLogRepository;
    
    /**
     * 任务参数仓储接口，通过构造器注入
     */
    private final com.platform.scheduler.query.application.port.out.TaskParameterRepository taskParameterRepository;
    
    /**
     * 任务归档仓储接口，通过构造器注入
     */
    private final com.platform.scheduler.query.application.port.out.TaskArchiveRepository taskArchiveRepository;
    
    /**
     * 导出服务，通过构造器注入
     */
    private final com.platform.scheduler.query.domain.service.DataExportService dataExportService;

    @Override
    @Transactional(readOnly = true)
    public TaskSearchResult<TaskInstanceView> queryTaskHistory(LocalDateTime startTime, LocalDateTime endTime, TaskQueryCriteria criteria) {
        log.debug("查询历史任务: {} - {}, 条件: {}", startTime, endTime, criteria);
        
        if (startTime == null || endTime == null) {
            log.warn("查询历史任务时时间范围参数无效");
            return TaskSearchResult.empty();
        }
        
        if (criteria == null) {
            // 创建默认查询条件
            criteria = TaskQueryCriteria.createDefault();
        }
        
        try {
            // 创建一个新的查询条件，包含时间范围
            TaskQueryCriteria queryCriteria = TaskQueryCriteria.builder()
                    .jobIds(criteria.getJobIds())
                    .jobNameLike(criteria.getJobNameLike())
                    .statuses(criteria.getStatuses())
                    .executorIds(criteria.getExecutorIds())
                    .createdAtFrom(startTime) // 使用传入的开始时间
                    .createdAtTo(endTime)     // 使用传入的结束时间
                    .onlyFailed(criteria.getOnlyFailed())
                    .onlyTimeout(criteria.getOnlyTimeout())
                    .createdBy(criteria.getCreatedBy())
                    .minPriority(criteria.getMinPriority())
                    .maxPriority(criteria.getMaxPriority())
                    .pageSize(criteria.getPageSize())
                    .pageNumber(criteria.getPageNumber())
                    .sortField(criteria.getSortField() != null ? criteria.getSortField() : "createdAt")
                    .descending(criteria.getDescending() != null ? criteria.getDescending() : true)
                    .includeParameters(criteria.getIncludeParameters())
                    .includeLogs(criteria.getIncludeLogs())
                    .build();
            
            // 调用任务状态查询服务的高级查询接口
            return taskInstanceViewRepository.searchTaskHistory(queryCriteria);
            
        } catch (Exception e) {
            log.error("查询历史任务发生异常", e);
            return TaskSearchResult.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getTaskExecutionTrends(JobId jobId, LocalDate startDate, LocalDate endDate, boolean groupByDay) {
        log.debug("获取任务执行趋势: jobId={}, {} - {}, groupByDay={}", 
                jobId != null ? jobId.getValue() : "all", startDate, endDate, groupByDay);
        
        if (startDate == null || endDate == null) {
            log.warn("获取任务执行趋势时日期范围参数无效");
            return Collections.emptyMap();
        }
        
        try {
            LocalDateTime startTime = startDate.atStartOfDay();
            LocalDateTime endTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
            
            Map<String, Long> trends;
            if (jobId != null) {
                // 查询特定作业的执行趋势
                trends = taskInstanceViewRepository.getTaskExecutionTrendsByJobId(
                        jobId.getValue(), startTime, endTime, groupByDay);
            } else {
                // 查询所有作业的执行趋势
                trends = taskInstanceViewRepository.getTaskExecutionTrends(startTime, endTime, groupByDay);
            }
            
            // 填充没有数据的时间点
            return fillMissingTimePoints(trends, startDate, endDate, groupByDay);
            
        } catch (Exception e) {
            log.error("获取任务执行趋势发生异常", e);
            return Collections.emptyMap();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getTaskStatusDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("获取任务状态分布: {} - {}", startTime, endTime);
        
        if (startTime == null || endTime == null) {
            log.warn("获取任务状态分布时时间范围参数无效");
            return Collections.emptyMap();
        }
        
        try {
            Map<String, Long> distribution = taskInstanceViewRepository.getTaskStatusDistribution(startTime, endTime);
            
            // 确保所有状态都有记录，即使是0
            for (TaskStatus status : TaskStatus.values()) {
                distribution.putIfAbsent(status.getDescription(), 0L);
            }
            
            return distribution;
        } catch (Exception e) {
            log.error("获取任务状态分布发生异常", e);
            return Collections.emptyMap();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskInstanceView> getJobExecutionHistory(JobId jobId, int limit) {
        log.debug("获取作业执行历史: {}, limit={}", jobId, limit);
        
        if (jobId == null) {
            log.warn("获取作业执行历史时作业ID参数无效");
            return Collections.emptyList();
        }
        
        if (limit <= 0) {
            limit = 20; // 默认限制
        }
        
        try {
            return taskInstanceViewRepository.findHistoryByJobId(jobId.getValue(), limit);
        } catch (Exception e) {
            log.error("获取作业执行历史发生异常: {}", jobId, e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getTaskDurationDistribution(LocalDateTime startTime, LocalDateTime endTime, int bucketSizeInSeconds) {
        log.debug("获取任务执行时间分布: {} - {}, bucketSize={}s", startTime, endTime, bucketSizeInSeconds);
        
        if (startTime == null || endTime == null) {
            log.warn("获取任务执行时间分布时时间范围参数无效");
            return Collections.emptyMap();
        }
        
        if (bucketSizeInSeconds <= 0) {
            bucketSizeInSeconds = 60; // 默认1分钟
        }
        
        try {
            // 查询任务执行时间数据
            List<Long> durations = taskInstanceViewRepository.getTaskDurations(startTime, endTime);
            
            // 构建分布图
            return buildDurationDistribution(durations, bucketSizeInSeconds);
        } catch (Exception e) {
            log.error("获取任务执行时间分布发生异常", e);
            return Collections.emptyMap();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Long> getTaskRetryDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("获取任务重试次数分布: {} - {}", startTime, endTime);
        
        if (startTime == null || endTime == null) {
            log.warn("获取任务重试次数分布时时间范围参数无效");
            return Collections.emptyMap();
        }
        
        try {
            return taskInstanceViewRepository.getTaskRetryDistribution(startTime, endTime);
        } catch (Exception e) {
            log.error("获取任务重试次数分布发生异常", e);
            return Collections.emptyMap();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTaskLogs(TaskInstanceId taskInstanceId) {
        log.debug("获取任务日志数据: {}", taskInstanceId);
        
        if (taskInstanceId == null) {
            log.warn("获取任务日志数据时任务ID参数无效");
            return Collections.emptyList();
        }
        
        try {
            return taskLogRepository.getTaskLogs(taskInstanceId.getValue());
        } catch (Exception e) {
            log.error("获取任务日志数据发生异常: {}", taskInstanceId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public byte[] exportTaskHistory(TaskQueryCriteria criteria, String exportType) {
        log.debug("导出任务历史数据, 条件: {}, 格式: {}", criteria, exportType);
        
        if (criteria == null) {
            log.warn("导出任务历史数据时查询条件为空");
            criteria = TaskQueryCriteria.createDefault();
        }
        
        try {
            // 转换导出格式
            com.platform.scheduler.query.domain.service.DataExportService.ExportFormat format;
            switch (exportType.toUpperCase()) {
                case "CSV":
                    format = com.platform.scheduler.query.domain.service.DataExportService.ExportFormat.CSV;
                    break;
                case "EXCEL":
                    format = com.platform.scheduler.query.domain.service.DataExportService.ExportFormat.EXCEL;
                    break;
                case "PDF":
                    format = com.platform.scheduler.query.domain.service.DataExportService.ExportFormat.PDF;
                    break;
                case "JSON":
                    format = com.platform.scheduler.query.domain.service.DataExportService.ExportFormat.JSON;
                    break;
                default:
                    format = com.platform.scheduler.query.domain.service.DataExportService.ExportFormat.EXCEL;
            }
            
            // 调用导出服务生成文件
            return dataExportService.exportTaskHistory(criteria, format);
        } catch (Exception e) {
            log.error("导出任务历史数据发生异常", e);
            return new byte[0];
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTaskExecutionHeatmap(LocalDate startDate, LocalDate endDate) {
        log.debug("获取任务执行热力图数据: {} - {}", startDate, endDate);
        
        if (startDate == null || endDate == null) {
            log.warn("获取任务执行热力图数据时日期范围参数无效");
            return Collections.emptyList();
        }
        
        try {
            LocalDateTime startTime = startDate.atStartOfDay();
            LocalDateTime endTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
            
            return taskInstanceViewRepository.getTaskExecutionHeatmap(startTime, endTime);
        } catch (Exception e) {
            log.error("获取任务执行热力图数据发生异常", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<TaskInstanceView>> getRelatedTaskChain(TaskInstanceId taskInstanceId) {
        log.debug("查询关联的任务执行链: {}", taskInstanceId);
        
        if (taskInstanceId == null) {
            log.warn("查询关联的任务执行链时任务ID参数无效");
            return Collections.emptyMap();
        }
        
        try {
            Map<String, List<TaskInstanceView>> result = new HashMap<>();
            
            // 查询当前任务
            Optional<TaskInstanceView> currentTaskOpt = taskInstanceViewRepository.findById(taskInstanceId.getValue());
            if (currentTaskOpt.isEmpty()) {
                return Collections.emptyMap();
            }
            
            TaskInstanceView currentTask = currentTaskOpt.get();
            result.put("current", List.of(currentTask));
            
            // 查询前置任务(查找与当前任务相关的依赖任务)
            List<TaskInstanceView> predecessors = taskInstanceViewRepository.findPredecessorTasks(taskInstanceId.getValue());
            if (!predecessors.isEmpty()) {
                result.put("predecessors", predecessors);
            }
            
            // 查询后置任务(查找依赖当前任务的任务)
            List<TaskInstanceView> successors = taskInstanceViewRepository.findSuccessorTasks(taskInstanceId.getValue());
            if (!successors.isEmpty()) {
                result.put("successors", successors);
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询关联的任务执行链发生异常: {}", taskInstanceId, e);
            return Collections.emptyMap();
        }
    }

    @Override
    @Transactional
    public int archiveHistoricalTasks(LocalDate beforeDate) {
        log.debug("归档历史任务数据: beforeDate={}", beforeDate);
        
        if (beforeDate == null) {
            log.warn("归档历史任务数据时日期参数无效");
            return 0;
        }
        
        try {
            LocalDateTime cutoffTime = beforeDate.atStartOfDay();
            
            // 首先将历史任务数据转移到归档表
            int archivedCount = taskArchiveRepository.archiveTasksBeforeDate(cutoffTime);
            
            // 如果归档成功，删除原表中的历史数据
            if (archivedCount > 0) {
                taskInstanceViewRepository.deleteHistoricalTasks(cutoffTime);
            }
            
            log.info("成功归档{}条历史任务数据", archivedCount);
            return archivedCount;
        } catch (Exception e) {
            log.error("归档历史任务数据发生异常: {}", beforeDate, e);
            throw new RuntimeException("归档历史任务数据失败", e);
        }
    }
    
    /**
     * 填充趋势数据中缺失的时间点
     */
    private Map<String, Long> fillMissingTimePoints(Map<String, Long> trends, LocalDate startDate, LocalDate endDate, boolean groupByDay) {
        SortedMap<String, Long> result = new TreeMap<>();
        
        if (trends == null || trends.isEmpty()) {
            trends = new HashMap<>();
        }
        
        DateTimeFormatter formatter;
        if (groupByDay) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // 遍历日期范围，填充缺失的日期点
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                String key = current.format(formatter);
                result.put(key, trends.getOrDefault(key, 0L));
                current = current.plusDays(1);
            }
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            
            // 遍历小时范围，填充缺失的小时点
            LocalDateTime current = startDate.atStartOfDay();
            LocalDateTime end = endDate.plusDays(1).atStartOfDay();
            while (current.isBefore(end)) {
                String key = current.format(formatter);
                result.put(key, trends.getOrDefault(key, 0L));
                current = current.plusHours(1);
            }
        }
        
        return result;
    }
    
    /**
     * 构建执行时间分布图
     */
    private Map<String, Long> buildDurationDistribution(List<Long> durations, int bucketSizeInSeconds) {
        SortedMap<String, Long> distribution = new TreeMap<>();
        
        if (durations == null || durations.isEmpty()) {
            return distribution;
        }
        
        // 计算最大执行时间，向上取整到最近的桶大小的倍数
        long maxDuration = durations.stream().max(Long::compare).orElse(0L);
        long maxDurationInSeconds = (maxDuration / 1000) + 1;
        int bucketCount = (int) Math.ceil((double) maxDurationInSeconds / bucketSizeInSeconds);
        
        // 初始化所有桶
        for (int i = 0; i < bucketCount; i++) {
            int lowerBound = i * bucketSizeInSeconds;
            int upperBound = (i + 1) * bucketSizeInSeconds;
            distribution.put(lowerBound + "-" + upperBound + "s", 0L);
        }
        
        // 统计每个桶的任务数量
        for (Long duration : durations) {
            if (duration == null) continue;
            
            int bucketIndex = (int) ((duration / 1000) / bucketSizeInSeconds);
            if (bucketIndex >= bucketCount) {
                bucketIndex = bucketCount - 1; // 最大桶
            }
            
            int lowerBound = bucketIndex * bucketSizeInSeconds;
            int upperBound = (bucketIndex + 1) * bucketSizeInSeconds;
            String bucketKey = lowerBound + "-" + upperBound + "s";
            
            distribution.put(bucketKey, distribution.getOrDefault(bucketKey, 0L) + 1);
        }
        
        return distribution;
    }
}
