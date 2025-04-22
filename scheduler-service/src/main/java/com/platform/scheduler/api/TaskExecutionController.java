package com.platform.scheduler.api;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.scheduler.model.TaskExecution;
import com.platform.scheduler.model.TaskResult;
import com.platform.scheduler.service.TaskExecutionService;

/**
 * 任务执行记录API控制器
 * 
 * @author platform
 */
@RestController
@RequestMapping("/api/executions")
public class TaskExecutionController {
    
    @Autowired
    private TaskExecutionService taskExecutionService;
    
    /**
     * 创建执行记录
     * 
     * @param taskId 任务ID
     * @param nodeId 节点ID
     * @param triggerType 触发类型
     * @return 执行ID
     */
    @PostMapping
    public ResponseEntity<Long> createExecution(
            @RequestParam Long taskId,
            @RequestParam String nodeId,
            @RequestParam String triggerType) {
        Long executionId = taskExecutionService.createExecution(taskId, nodeId, triggerType);
        return ResponseEntity.ok(executionId);
    }
    
    /**
     * 完成执行
     * 
     * @param executionId 执行ID
     * @param result 执行结果
     * @return 操作结果
     */
    @PutMapping("/{executionId}/complete")
    public ResponseEntity<Void> completeExecution(@PathVariable Long executionId, @RequestBody TaskResult result) {
        boolean completed = taskExecutionService.completeExecution(executionId, result);
        return completed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 标记执行失败
     * 
     * @param executionId 执行ID
     * @param errorMessage 错误信息
     * @return 操作结果
     */
    @PutMapping("/{executionId}/fail")
    public ResponseEntity<Void> failExecution(@PathVariable Long executionId, @RequestParam String errorMessage) {
        boolean failed = taskExecutionService.failExecution(executionId, errorMessage);
        return failed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 标记执行超时
     * 
     * @param executionId 执行ID
     * @return 操作结果
     */
    @PutMapping("/{executionId}/timeout")
    public ResponseEntity<Void> timeoutExecution(@PathVariable Long executionId) {
        boolean timedOut = taskExecutionService.timeoutExecution(executionId);
        return timedOut ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 终止执行
     * 
     * @param executionId 执行ID
     * @return 操作结果
     */
    @PutMapping("/{executionId}/terminate")
    public ResponseEntity<Void> terminateExecution(@PathVariable Long executionId) {
        boolean terminated = taskExecutionService.terminateExecution(executionId);
        return terminated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 根据ID查询执行记录
     * 
     * @param executionId 执行ID
     * @return 执行记录
     */
    @GetMapping("/{executionId}")
    public ResponseEntity<TaskExecution> getExecutionById(@PathVariable Long executionId) {
        TaskExecution execution = taskExecutionService.getExecutionById(executionId);
        return ResponseEntity.ok(execution);
    }
    
    /**
     * 根据任务ID查询执行记录
     * 
     * @param taskId 任务ID
     * @param status 执行状态（可选）
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<Page<TaskExecution>> findExecutionsByTaskId(
            @PathVariable Long taskId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        Page<TaskExecution> executions;
        if (status != null) {
            executions = taskExecutionService.findExecutionsByTaskIdAndStatus(taskId, status, pageable);
        } else {
            executions = taskExecutionService.findExecutionsByTaskId(taskId, pageable);
        }
        return ResponseEntity.ok(executions);
    }
    
    /**
     * 查询任务的最近一次执行记录
     * 
     * @param taskId 任务ID
     * @return 执行记录
     */
    @GetMapping("/task/{taskId}/latest")
    public ResponseEntity<TaskExecution> findLatestExecution(@PathVariable Long taskId) {
        TaskExecution execution = taskExecutionService.findLatestExecution(taskId);
        return ResponseEntity.ok(execution);
    }
    
    /**
     * 根据节点ID查询执行记录
     * 
     * @param nodeId 节点ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping("/node/{nodeId}")
    public ResponseEntity<Page<TaskExecution>> findExecutionsByNodeId(
            @PathVariable String nodeId,
            Pageable pageable) {
        Page<TaskExecution> executions = taskExecutionService.findExecutionsByNodeId(nodeId, pageable);
        return ResponseEntity.ok(executions);
    }
    
    /**
     * 根据状态查询执行记录
     * 
     * @param status 执行状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TaskExecution>> findExecutionsByStatus(
            @PathVariable String status,
            Pageable pageable) {
        Page<TaskExecution> executions = taskExecutionService.findExecutionsByStatus(status, pageable);
        return ResponseEntity.ok(executions);
    }
    
    /**
     * 根据时间范围查询执行记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping("/time-range")
    public ResponseEntity<Page<TaskExecution>> findExecutionsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endTime,
            Pageable pageable) {
        Page<TaskExecution> executions = taskExecutionService.findExecutionsByTimeRange(startTime, endTime, pageable);
        return ResponseEntity.ok(executions);
    }
    
    /**
     * 根据任务ID和时间范围查询执行记录
     * 
     * @param taskId 任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页结果
     */
    @GetMapping("/task/{taskId}/time-range")
    public ResponseEntity<Page<TaskExecution>> findExecutionsByTaskIdAndTimeRange(
            @PathVariable Long taskId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endTime,
            Pageable pageable) {
        Page<TaskExecution> executions = taskExecutionService.findExecutionsByTaskIdAndTimeRange(
                taskId, startTime, endTime, pageable);
        return ResponseEntity.ok(executions);
    }
    
    /**
     * 统计任务执行成功率
     * 
     * @param taskId 任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 成功率（0-100）
     */
    @GetMapping("/task/{taskId}/success-rate")
    public ResponseEntity<Double> calculateSuccessRate(
            @PathVariable Long taskId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endTime) {
        double successRate = taskExecutionService.calculateSuccessRate(taskId, startTime, endTime);
        return ResponseEntity.ok(successRate);
    }
    
    /**
     * 统计任务平均执行时间
     * 
     * @param taskId 任务ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均执行时间（毫秒）
     */
    @GetMapping("/task/{taskId}/average-execution-time")
    public ResponseEntity<Long> calculateAverageExecutionTime(
            @PathVariable Long taskId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endTime) {
        long averageExecutionTime = taskExecutionService.calculateAverageExecutionTime(taskId, startTime, endTime);
        return ResponseEntity.ok(averageExecutionTime);
    }
}
