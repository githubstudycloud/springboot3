package com.example.platform.scheduler.interfaces.rest;

import com.example.platform.scheduler.application.dto.ExecutorDTO;
import com.example.platform.scheduler.application.service.ClusterManagementService;
import com.example.platform.scheduler.interfaces.rest.dto.ApiResponse;
import com.example.platform.scheduler.interfaces.rest.request.RegisterExecutorRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for cluster management operations.
 */
@RestController
@RequestMapping("/api/v1/cluster")
public class ClusterController extends BaseController {

    private final ClusterManagementService clusterManagementService;

    /**
     * Constructs a new ClusterController.
     *
     * @param clusterManagementService The cluster management service
     */
    public ClusterController(ClusterManagementService clusterManagementService) {
        this.clusterManagementService = clusterManagementService;
    }

    /**
     * Registers a new executor in the cluster.
     *
     * @param request The executor registration request
     * @return Registration response
     */
    @PostMapping("/executors")
    public ResponseEntity<ApiResponse<ExecutorDTO>> registerExecutor(@Valid @RequestBody RegisterExecutorRequest request) {
        ExecutorDTO executor = clusterManagementService.registerExecutor(
                request.getExecutorId(),
                request.getNodeInfo(),
                request.getCapabilities()
        );
        return success(executor);
    }

    /**
     * Gets all executors in the cluster.
     *
     * @return List of executors
     */
    @GetMapping("/executors")
    public ResponseEntity<ApiResponse<List<ExecutorDTO>>> getAllExecutors() {
        List<ExecutorDTO> executors = clusterManagementService.getAllExecutors();
        return success(executors);
    }

    /**
     * Gets active executors in the cluster.
     *
     * @return List of active executors
     */
    @GetMapping("/executors/active")
    public ResponseEntity<ApiResponse<List<ExecutorDTO>>> getActiveExecutors() {
        List<ExecutorDTO> activeExecutors = clusterManagementService.getActiveExecutors();
        return success(activeExecutors);
    }

    /**
     * Gets an executor by ID.
     *
     * @param executorId The executor ID
     * @return Executor details
     */
    @GetMapping("/executors/{executorId}")
    public ResponseEntity<ApiResponse<ExecutorDTO>> getExecutorById(@PathVariable String executorId) {
        Optional<ExecutorDTO> executor = clusterManagementService.getExecutorById(executorId);
        return executor.map(this::success)
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, "Executor not found with ID: " + executorId));
    }

    /**
     * Activates an executor.
     *
     * @param executorId The executor ID
     * @return Activation response
     */
    @PostMapping("/executors/{executorId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateExecutor(@PathVariable String executorId) {
        boolean activated = clusterManagementService.activateExecutor(executorId);
        
        if (activated) {
            return success();
        } else {
            return error(HttpStatus.NOT_FOUND, "Executor not found with ID: " + executorId);
        }
    }

    /**
     * Deactivates an executor.
     *
     * @param executorId The executor ID
     * @param reason The deactivation reason
     * @return Deactivation response
     */
    @PostMapping("/executors/{executorId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateExecutor(
            @PathVariable String executorId,
            @RequestParam @NotBlank String reason) {
        
        boolean deactivated = clusterManagementService.deactivateExecutor(executorId, reason);
        
        if (deactivated) {
            return success();
        } else {
            return error(HttpStatus.NOT_FOUND, "Executor not found with ID: " + executorId);
        }
    }

    /**
     * Updates executor heartbeat.
     *
     * @param executorId The executor ID
     * @return Heartbeat update response
     */
    @PostMapping("/executors/{executorId}/heartbeat")
    public ResponseEntity<ApiResponse<Void>> updateExecutorHeartbeat(@PathVariable String executorId) {
        boolean updated = clusterManagementService.updateExecutorHeartbeat(executorId);
        
        if (updated) {
            return success();
        } else {
            return error(HttpStatus.NOT_FOUND, "Executor not found with ID: " + executorId);
        }
    }

    /**
     * Gets the executor assigned to a task.
     *
     * @param taskId The task ID
     * @return Executor ID
     */
    @GetMapping("/tasks/{taskId}/executor")
    public ResponseEntity<ApiResponse<String>> getExecutorForTask(@PathVariable String taskId) {
        Optional<String> executorId = clusterManagementService.getExecutorForTask(taskId);
        return executorId.map(this::success)
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, "No executor found for task with ID: " + taskId));
    }

    /**
     * Detects and marks failed executors.
     *
     * @param timeoutSeconds The heartbeat timeout in seconds
     * @return Number of failed executors detected
     */
    @PostMapping("/maintenance/detect-failed-executors")
    public ResponseEntity<ApiResponse<Integer>> detectFailedExecutors(
            @RequestParam(defaultValue = "60") int timeoutSeconds) {
        
        int failedCount = clusterManagementService.detectFailedExecutors(timeoutSeconds);
        return success(failedCount);
    }
}