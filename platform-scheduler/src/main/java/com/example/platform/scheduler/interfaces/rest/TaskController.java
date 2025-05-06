package com.example.platform.scheduler.interfaces.rest;

import com.example.platform.scheduler.application.dto.TaskInstanceDTO;
import com.example.platform.scheduler.application.service.TaskExecutionService;
import com.example.platform.scheduler.domain.taskinstance.TaskLogLevel;
import com.example.platform.scheduler.interfaces.rest.dto.ApiResponse;
import com.example.platform.scheduler.interfaces.rest.request.ScheduleTaskRequest;
import com.example.platform.scheduler.interfaces.rest.request.TaskLogRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for task execution operations.
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController extends BaseController {

    private final TaskExecutionService taskExecutionService;

    /**
     * Constructs a new TaskController.
     *
     * @param taskExecutionService The task execution service
     */
    public TaskController(TaskExecutionService taskExecutionService) {
        this.taskExecutionService = taskExecutionService;
    }

    /**
     * Schedules a new task.
     *
     * @param request The task scheduling request
     * @return Task scheduling response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TaskInstanceDTO>> scheduleTask(@Valid @RequestBody ScheduleTaskRequest request) {
        TaskInstanceDTO taskInstance = taskExecutionService.scheduleTask(
                request.getJobId(),
                request.getScheduledTime(),
                request.getParameters()
        );
        return success(taskInstance);
    }

    /**
     * Gets all tasks.
     *
     * @return List of tasks
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskInstanceDTO>>> getAllTasks() {
        List<TaskInstanceDTO> tasks = taskExecutionService.getAllTasks();
        return success(tasks);
    }

    /**
     * Gets tasks by job ID.
     *
     * @param jobId The job ID
     * @return List of tasks for the job
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<ApiResponse<List<TaskInstanceDTO>>> getTasksByJobId(@PathVariable String jobId) {
        List<TaskInstanceDTO> tasks = taskExecutionService.getTasksByJobId(jobId);
        return success(tasks);
    }

    /**
     * Gets tasks by status.
     *
     * @param status The task status
     * @return List of tasks with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TaskInstanceDTO>>> getTasksByStatus(@PathVariable String status) {
        List<TaskInstanceDTO> tasks = taskExecutionService.getTasksByStatus(status);
        return success(tasks);
    }

    /**
     * Gets tasks scheduled for a time range.
     *
     * @param startTime The start time
     * @param endTime The end time
     * @return List of tasks scheduled for the time range
     */
    @GetMapping("/scheduled")
    public ResponseEntity<ApiResponse<List<TaskInstanceDTO>>> getTasksByScheduledTime(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<TaskInstanceDTO> tasks = taskExecutionService.getTasksByScheduledTime(startTime, endTime);
        return success(tasks);
    }

    /**
     * Gets a task by ID.
     *
     * @param taskId The task ID
     * @return Task details
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskInstanceDTO>> getTaskById(@PathVariable String taskId) {
        Optional<TaskInstanceDTO> task = taskExecutionService.getTaskById(taskId);
        return task.map(this::success)
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, "Task not found with ID: " + taskId));
    }

    /**
     * Assigns a task to an executor.
     *
     * @param taskId The task ID
     * @param executorId The executor ID
     * @return Assignment response
     */
    @PostMapping("/{taskId}/assign")
    public ResponseEntity<ApiResponse<Void>> assignTask(
            @PathVariable String taskId,
            @RequestParam @NotBlank String executorId) {
        
        boolean assigned = taskExecutionService.assignTask(taskId, executorId);
        
        if (assigned) {
            return success();
        } else {
            return error(HttpStatus.CONFLICT, "Failed to assign task. Task may be already assigned or not in PENDING state.");
        }
    }

    /**
     * Starts task execution.
     *
     * @param taskId The task ID
     * @param executorId The executor ID
     * @return Start execution response
     */
    @PostMapping("/{taskId}/start")
    public ResponseEntity<ApiResponse<Void>> startTask(
            @PathVariable String taskId,
            @RequestParam @NotBlank String executorId) {
        
        boolean started = taskExecutionService.startTask(taskId, executorId);
        
        if (started) {
            return success();
        } else {
            return error(HttpStatus.CONFLICT, "Failed to start task. Task may not be in ASSIGNED state or assigned to a different executor.");
        }
    }

    /**
     * Completes task execution.
     *
     * @param taskId The task ID
     * @param executorId The executor ID
     * @param result The task result
     * @return Completion response
     */
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeTask(
            @PathVariable String taskId,
            @RequestParam @NotBlank String executorId,
            @RequestBody String result) {
        
        boolean completed = taskExecutionService.completeTask(taskId, executorId, result);
        
        if (completed) {
            return success();
        } else {
            return error(HttpStatus.CONFLICT, "Failed to complete task. Task may not be in RUNNING state or assigned to a different executor.");
        }
    }

    /**
     * Fails task execution.
     *
     * @param taskId The task ID
     * @param executorId The executor ID
     * @param errorMessage The error message
     * @return Failure response
     */
    @PostMapping("/{taskId}/fail")
    public ResponseEntity<ApiResponse<Void>> failTask(
            @PathVariable String taskId,
            @RequestParam @NotBlank String executorId,
            @RequestBody String errorMessage) {
        
        boolean failed = taskExecutionService.failTask(taskId, executorId, errorMessage);
        
        if (failed) {
            return success();
        } else {
            return error(HttpStatus.CONFLICT, "Failed to mark task as failed. Task may not be in RUNNING state or assigned to a different executor.");
        }
    }

    /**
     * Cancels task execution.
     *
     * @param taskId The task ID
     * @param reason The cancellation reason
     * @return Cancellation response
     */
    @PostMapping("/{taskId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelTask(
            @PathVariable String taskId,
            @RequestParam @NotBlank String reason) {
        
        boolean cancelled = taskExecutionService.cancelTask(taskId, reason);
        
        if (cancelled) {
            return success();
        } else {
            return error(HttpStatus.CONFLICT, "Failed to cancel task. Task may already be in a terminal state.");
        }
    }

    /**
     * Adds a log entry to a task.
     *
     * @param taskId The task ID
     * @param request The log entry request
     * @return Log addition response
     */
    @PostMapping("/{taskId}/log")
    public ResponseEntity<ApiResponse<Void>> addTaskLog(
            @PathVariable String taskId,
            @Valid @RequestBody TaskLogRequest request) {
        
        boolean added = taskExecutionService.addTaskLog(
                taskId,
                request.getExecutorId(),
                request.getLogLevel(),
                request.getMessage()
        );
        
        if (added) {
            return success();
        } else {
            return error(HttpStatus.CONFLICT, "Failed to add log entry. Task may not exist or not be assigned to the specified executor.");
        }
    }

    /**
     * Gets task logs.
     *
     * @param taskId The task ID
     * @param level The log level filter (optional)
     * @return Task logs
     */
    @GetMapping("/{taskId}/logs")
    public ResponseEntity<ApiResponse<List<String>>> getTaskLogs(
            @PathVariable String taskId,
            @RequestParam(required = false) String level) {
        
        TaskLogLevel logLevel = null;
        if (level != null && !level.isEmpty()) {
            try {
                logLevel = TaskLogLevel.valueOf(level.toUpperCase());
            } catch (IllegalArgumentException e) {
                return error(HttpStatus.BAD_REQUEST, "Invalid log level: " + level);
            }
        }
        
        List<String> logs = taskExecutionService.getTaskLogs(taskId, logLevel);
        return success(logs);
    }
}