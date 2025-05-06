package com.platform.scheduler.query.interfaces.web;

import com.platform.scheduler.domain.model.job.JobId;
import com.platform.scheduler.domain.model.task.TaskInstanceId;
import com.platform.scheduler.query.domain.model.task.TaskInstanceView;
import com.platform.scheduler.query.domain.model.task.TaskQueryCriteria;
import com.platform.scheduler.query.domain.model.task.TaskSearchResult;
import com.platform.scheduler.query.domain.service.TaskStatusQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 任务状态查询控制器
 * 提供任务状态和详情查询的REST API
 * 
 * @author platform
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "任务状态查询", description = "提供任务状态和详情查询接口")
public class TaskStatusQueryController {

    private final TaskStatusQueryService taskStatusQueryService;

    @GetMapping("/{taskId}")
    @Operation(summary = "根据任务ID查询任务详情")
    public ResponseEntity<TaskInstanceView> getTaskById(
            @PathVariable("taskId") 
            @Parameter(description = "任务实例ID", required = true) String taskId) {
        
        Optional<TaskInstanceView> taskOpt = taskStatusQueryService.findTaskById(new TaskInstanceId(taskId));
        return taskOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/batch")
    @Operation(summary = "批量查询任务详情")
    public ResponseEntity<List<TaskInstanceView>> getTasksByIds(
            @RequestBody 
            @Parameter(description = "任务实例ID列表", required = true) List<String> taskIds) {
        
        List<TaskInstanceId> ids = taskIds.stream()
                .map(TaskInstanceId::new)
                .toList();
        
        List<TaskInstanceView> tasks = taskStatusQueryService.findTasksByIds(ids);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/job/{jobId}/latest")
    @Operation(summary = "查询作业最近一次执行的任务")
    public ResponseEntity<TaskInstanceView> getLatestTaskByJobId(
            @PathVariable("jobId") 
            @Parameter(description = "作业ID", required = true) String jobId) {
        
        Optional<TaskInstanceView> taskOpt = taskStatusQueryService.findLatestTaskByJobId(new JobId(jobId));
        return taskOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/job/{jobId}/history")
    @Operation(summary = "查询作业执行历史")
    public ResponseEntity<List<TaskInstanceView>> getTaskHistoryByJobId(
            @PathVariable("jobId") 
            @Parameter(description = "作业ID", required = true) String jobId,
            @RequestParam(value = "limit", defaultValue = "20") 
            @Parameter(description = "最大记录数") int limit) {
        
        List<TaskInstanceView> tasks = taskStatusQueryService.findTaskHistoryByJobId(new JobId(jobId), limit);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/running")
    @Operation(summary = "查询正在运行的任务")
    public ResponseEntity<List<TaskInstanceView>> getRunningTasks(
            @RequestParam(value = "limit", defaultValue = "50") 
            @Parameter(description = "最大记录数") int limit) {
        
        List<TaskInstanceView> tasks = taskStatusQueryService.findRunningTasks(limit);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/failed")
    @Operation(summary = "查询失败的任务")
    public ResponseEntity<List<TaskInstanceView>> getFailedTasks(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime,
            @RequestParam(value = "limit", defaultValue = "50") 
            @Parameter(description = "最大记录数") int limit) {
        
        List<TaskInstanceView> tasks = taskStatusQueryService.findFailedTasks(startTime, endTime, limit);
        return ResponseEntity.ok(tasks);
    }
    
    @PostMapping("/search")
    @Operation(summary = "高级查询任务")
    public ResponseEntity<TaskSearchResult<TaskInstanceView>> searchTasks(
            @RequestBody 
            @Parameter(description = "查询条件", required = true) TaskQueryCriteria criteria) {
        
        TaskSearchResult<TaskInstanceView> result = taskStatusQueryService.searchTasks(criteria);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/waiting-retry")
    @Operation(summary = "查询等待重试的任务")
    public ResponseEntity<List<TaskInstanceView>> getTasksWaitingRetry(
            @RequestParam(value = "limit", defaultValue = "50") 
            @Parameter(description = "最大记录数") int limit) {
        
        List<TaskInstanceView> tasks = taskStatusQueryService.findTasksWaitingRetry(limit);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/executor/{executorId}")
    @Operation(summary = "查询特定执行器的任务")
    public ResponseEntity<List<TaskInstanceView>> getTasksByExecutorId(
            @PathVariable("executorId") 
            @Parameter(description = "执行器ID", required = true) String executorId,
            @RequestParam(value = "limit", defaultValue = "50") 
            @Parameter(description = "最大记录数") int limit) {
        
        List<TaskInstanceView> tasks = taskStatusQueryService.findTasksByExecutorId(executorId, limit);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/timeout")
    @Operation(summary = "查询超时的任务")
    public ResponseEntity<List<TaskInstanceView>> getTimeoutTasks(
            @RequestParam(value = "startTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "开始时间", required = true) LocalDateTime startTime,
            @RequestParam(value = "endTime") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "结束时间", required = true) LocalDateTime endTime,
            @RequestParam(value = "limit", defaultValue = "50") 
            @Parameter(description = "最大记录数") int limit) {
        
        List<TaskInstanceView> tasks = taskStatusQueryService.findTimeoutTasks(startTime, endTime, limit);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{taskId}/details")
    @Operation(summary = "获取任务详情（包含参数和日志）")
    public ResponseEntity<TaskInstanceView> getTaskDetails(
            @PathVariable("taskId") 
            @Parameter(description = "任务实例ID", required = true) String taskId) {
        
        Optional<TaskInstanceView> taskOpt = taskStatusQueryService.getTaskDetails(new TaskInstanceId(taskId));
        return taskOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
