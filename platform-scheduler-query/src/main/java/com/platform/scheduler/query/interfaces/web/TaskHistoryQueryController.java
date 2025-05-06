package com.platform.scheduler.query.interfaces.web;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import com.platform.scheduler.query.domain.model.task.TaskInstanceView;
import com.platform.scheduler.query.domain.model.task.TaskQueryCriteria;
import com.platform.scheduler.query.domain.model.task.TaskSearchResult;
import com.platform.scheduler.query.domain.service.TaskHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务历史查询控制器
 * 提供任务历史记录查询的REST API
 * 
 * @author platform
 */
@RestController
@RequestMapping("/api/v1/task-history")
@RequiredArgsConstructor
@Tag(name = "任务历史查询", description = "提供任务执行历史查询和统计接口")
public class TaskHistoryQueryController {

    private final TaskHistoryQueryService taskHistoryQueryService;

    @PostMapping("/query")
    @Operation(summary = "根据时间范围查询历史任务")
    public ResponseEntity<TaskSearchResult<TaskInstanceView>> queryTaskHistory(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime,
            @RequestBody(required = false) 
            @Parameter(description = "额外查询条件") TaskQueryCriteria criteria) {
        
        TaskSearchResult<TaskInstanceView> result = taskHistoryQueryService.queryTaskHistory(startTime, endTime, criteria);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/trends")
    @Operation(summary = "获取任务执行趋势数据")
    public ResponseEntity<Map<String, Long>> getTaskExecutionTrends(
            @RequestParam(value = "jobId", required = false) 
            @Parameter(description = "作业ID") String jobId,
            @RequestParam(value = "startDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "开始日期", required = true) LocalDate startDate,
            @RequestParam(value = "endDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "结束日期", required = true) LocalDate endDate,
            @RequestParam(value = "groupByDay", defaultValue = "true") 
            @Parameter(description = "是否按天分组") boolean groupByDay) {
        
        JobId jobIdObj = jobId != null ? new JobId(jobId) : null;
        Map<String, Long> trends = taskHistoryQueryService.getTaskExecutionTrends(jobIdObj, startDate, endDate, groupByDay);
        return ResponseEntity.ok(trends);
    }
    
    @GetMapping("/status-distribution")
    @Operation(summary = "获取任务状态分布")
    public ResponseEntity<Map<String, Long>> getTaskStatusDistribution(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime) {
        
        Map<String, Long> distribution = taskHistoryQueryService.getTaskStatusDistribution(startTime, endTime);
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/job/{jobId}/history")
    @Operation(summary = "获取作业执行历史记录")
    public ResponseEntity<List<TaskInstanceView>> getJobExecutionHistory(
            @PathVariable("jobId") 
            @Parameter(description = "作业ID", required = true) String jobId,
            @RequestParam(value = "limit", defaultValue = "20") 
            @Parameter(description = "最大记录数") int limit) {
        
        List<TaskInstanceView> history = taskHistoryQueryService.getJobExecutionHistory(new JobId(jobId), limit);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/duration-distribution")
    @Operation(summary = "获取任务执行时间分布")
    public ResponseEntity<Map<String, Long>> getTaskDurationDistribution(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime,
            @RequestParam(value = "bucketSize", defaultValue = "60") 
            @Parameter(description = "时间分布桶大小(秒)") int bucketSizeInSeconds) {
        
        Map<String, Long> distribution = taskHistoryQueryService.getTaskDurationDistribution(
                startTime, endTime, bucketSizeInSeconds);
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/retry-distribution")
    @Operation(summary = "获取任务重试次数分布")
    public ResponseEntity<Map<Integer, Long>> getTaskRetryDistribution(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime) {
        
        Map<Integer, Long> distribution = taskHistoryQueryService.getTaskRetryDistribution(startTime, endTime);
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/task/{taskId}/logs")
    @Operation(summary = "获取任务日志数据")
    public ResponseEntity<List<Map<String, Object>>> getTaskLogs(
            @PathVariable("taskId") 
            @Parameter(description = "任务实例ID", required = true) String taskId) {
        
        List<Map<String, Object>> logs = taskHistoryQueryService.getTaskLogs(new TaskInstanceId(taskId));
        return ResponseEntity.ok(logs);
    }
    
    @PostMapping("/export")
    @Operation(summary = "导出任务历史数据")
    public ResponseEntity<byte[]> exportTaskHistory(
            @RequestBody 
            @Parameter(description = "查询条件", required = true) TaskQueryCriteria criteria,
            @RequestParam(value = "exportType", defaultValue = "EXCEL") 
            @Parameter(description = "导出类型(CSV, EXCEL, PDF)") String exportType) {
        
        byte[] data = taskHistoryQueryService.exportTaskHistory(criteria, exportType);
        
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        String filename = "task_history_export_" + LocalDate.now() + "." + getFileExtension(exportType);
        headers.setContentDispositionFormData("attachment", filename);
        
        // 设置媒体类型
        MediaType mediaType = getMediaType(exportType);
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(data);
    }
    
    @GetMapping("/heatmap")
    @Operation(summary = "获取任务执行热力图数据")
    public ResponseEntity<List<Map<String, Object>>> getTaskExecutionHeatmap(
            @RequestParam(value = "startDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "开始日期", required = true) LocalDate startDate,
            @RequestParam(value = "endDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "结束日期", required = true) LocalDate endDate) {
        
        List<Map<String, Object>> heatmapData = taskHistoryQueryService.getTaskExecutionHeatmap(startDate, endDate);
        return ResponseEntity.ok(heatmapData);
    }
    
    @GetMapping("/task/{taskId}/chain")
    @Operation(summary = "查询关联的任务执行链")
    public ResponseEntity<Map<String, List<TaskInstanceView>>> getRelatedTaskChain(
            @PathVariable("taskId") 
            @Parameter(description = "任务实例ID", required = true) String taskId) {
        
        Map<String, List<TaskInstanceView>> chain = taskHistoryQueryService.getRelatedTaskChain(new TaskInstanceId(taskId));
        return ResponseEntity.ok(chain);
    }
    
    @PostMapping("/archive")
    @Operation(summary = "归档历史任务数据")
    public ResponseEntity<Map<String, Object>> archiveHistoricalTasks(
            @RequestParam(value = "beforeDate") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "指定日期之前的数据将被归档", required = true) LocalDate beforeDate) {
        
        int archivedCount = taskHistoryQueryService.archiveHistoricalTasks(beforeDate);
        
        Map<String, Object> result = Map.of(
                "success", true,
                "archivedCount", archivedCount,
                "beforeDate", beforeDate
        );
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取文件扩展名
     *
     * @param exportType 导出类型
     * @return 文件扩展名
     */
    private String getFileExtension(String exportType) {
        switch (exportType.toUpperCase()) {
            case "CSV":
                return "csv";
            case "EXCEL":
                return "xlsx";
            case "PDF":
                return "pdf";
            case "JSON":
                return "json";
            default:
                return "xlsx";
        }
    }
    
    /**
     * 获取媒体类型
     *
     * @param exportType 导出类型
     * @return 媒体类型
     */
    private MediaType getMediaType(String exportType) {
        switch (exportType.toUpperCase()) {
            case "CSV":
                return MediaType.parseMediaType("text/csv");
            case "EXCEL":
                return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "PDF":
                return MediaType.parseMediaType("application/pdf");
            case "JSON":
                return MediaType.APPLICATION_JSON;
            default:
                return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
    }
}
