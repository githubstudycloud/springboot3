package com.example.platform.scheduler.interfaces.rest;

import com.example.platform.scheduler.application.command.CreateJobCommand;
import com.example.platform.scheduler.application.command.UpdateJobCommand;
import com.example.platform.scheduler.application.dto.JobDTO;
import com.example.platform.scheduler.application.service.JobManagementService;
import com.example.platform.scheduler.domain.job.JobPriority;
import com.example.platform.scheduler.domain.job.JobType;
import com.example.platform.scheduler.interfaces.rest.dto.ApiResponse;
import com.example.platform.scheduler.interfaces.rest.request.CreateJobRequest;
import com.example.platform.scheduler.interfaces.rest.request.UpdateJobRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for job management operations.
 */
@RestController
@RequestMapping("/api/v1/jobs")
public class JobController extends BaseController {

    private final JobManagementService jobManagementService;

    /**
     * Constructs a new JobController.
     *
     * @param jobManagementService The job management service
     */
    public JobController(JobManagementService jobManagementService) {
        this.jobManagementService = jobManagementService;
    }

    /**
     * Creates a new job.
     *
     * @param request The job creation request
     * @return Job creation response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<JobDTO>> createJob(@Valid @RequestBody CreateJobRequest request) {
        CreateJobCommand command = new CreateJobCommand();
        command.setName(request.getName());
        command.setDescription(request.getDescription());
        command.setJobType(JobType.valueOf(request.getJobType()));
        command.setCronExpression(request.getCronExpression());
        command.setTimeZone(request.getTimeZone());
        command.setPriority(JobPriority.valueOf(request.getPriority()));
        command.setParameters(request.getParameters());
        command.setMaxRetries(request.getMaxRetries());
        command.setRetryDelaySeconds(request.getRetryDelaySeconds());
        command.setRetryStrategy(request.getRetryStrategy());
        command.setDependencies(request.getDependencies());

        JobDTO job = jobManagementService.createJob(command);
        return success(job);
    }

    /**
     * Gets all jobs.
     *
     * @return List of jobs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<JobDTO>>> getAllJobs() {
        List<JobDTO> jobs = jobManagementService.getAllJobs();
        return success(jobs);
    }

    /**
     * Gets all active jobs.
     *
     * @return List of active jobs
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<JobDTO>>> getActiveJobs() {
        List<JobDTO> activeJobs = jobManagementService.getActiveJobs();
        return success(activeJobs);
    }

    /**
     * Gets a job by ID.
     *
     * @param jobId The job ID
     * @return Job details
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobDTO>> getJobById(@PathVariable String jobId) {
        Optional<JobDTO> job = jobManagementService.getJobById(jobId);
        return job.map(this::success)
                .orElseGet(() -> error(HttpStatus.NOT_FOUND, "Job not found with ID: " + jobId));
    }

    /**
     * Updates a job.
     *
     * @param jobId The job ID
     * @param request The job update request
     * @return Update response
     */
    @PutMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> updateJob(
            @PathVariable String jobId,
            @Valid @RequestBody UpdateJobRequest request) {
        
        UpdateJobCommand command = new UpdateJobCommand();
        command.setJobId(jobId);
        command.setDescription(request.getDescription());
        
        if (request.getCronExpression() != null) {
            command.setCronExpression(request.getCronExpression());
        }
        
        if (request.getTimeZone() != null) {
            command.setTimeZone(request.getTimeZone());
        }
        
        if (request.getPriority() != null) {
            command.setPriority(JobPriority.valueOf(request.getPriority()));
        }
        
        if (request.getParameters() != null) {
            command.setParameters(request.getParameters());
        }
        
        if (request.getMaxRetries() != null) {
            command.setMaxRetries(request.getMaxRetries());
        }
        
        if (request.getRetryDelaySeconds() != null) {
            command.setRetryDelaySeconds(request.getRetryDelaySeconds());
        }
        
        if (request.getRetryStrategy() != null) {
            command.setRetryStrategy(request.getRetryStrategy());
        }
        
        if (request.getDependencies() != null) {
            command.setDependencies(request.getDependencies());
        }

        boolean updated = jobManagementService.updateJob(command);
        
        if (updated) {
            return success();
        } else {
            return error(HttpStatus.NOT_FOUND, "Job not found with ID: " + jobId);
        }
    }

    /**
     * Activates a job.
     *
     * @param jobId The job ID
     * @return Activation response
     */
    @PostMapping("/{jobId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateJob(@PathVariable String jobId) {
        boolean activated = jobManagementService.activateJob(jobId);
        
        if (activated) {
            return success();
        } else {
            return error(HttpStatus.NOT_FOUND, "Job not found with ID: " + jobId);
        }
    }

    /**
     * Pauses a job.
     *
     * @param jobId The job ID
     * @param reason The reason for pausing
     * @return Pause response
     */
    @PostMapping("/{jobId}/pause")
    public ResponseEntity<ApiResponse<Void>> pauseJob(
            @PathVariable String jobId,
            @RequestParam(required = false) String reason) {
        
        boolean paused = jobManagementService.pauseJob(jobId, reason);
        
        if (paused) {
            return success();
        } else {
            return error(HttpStatus.NOT_FOUND, "Job not found with ID: " + jobId);
        }
    }

    /**
     * Resumes a paused job.
     *
     * @param jobId The job ID
     * @return Resume response
     */
    @PostMapping("/{jobId}/resume")
    public ResponseEntity<ApiResponse<Void>> resumeJob(@PathVariable String jobId) {
        boolean resumed = jobManagementService.resumeJob(jobId);
        
        if (resumed) {
            return success();
        } else {
            return error(HttpStatus.NOT_FOUND, "Job not found with ID: " + jobId);
        }
    }

    /**
     * Disables a job.
     *
     * @param jobId The job ID
     * @param reason The reason for disabling
     * @return Disable response
     */
    @PostMapping("/{jobId}/disable")
    public ResponseEntity<ApiResponse<Void>> disableJob(
            @PathVariable String jobId,
            @RequestParam @NotBlank String reason) {
        
        boolean disabled = jobManagementService.disableJob(jobId, reason);
        
        if (disabled) {
            return success();
        } else {
            return error(HttpStatus.NOT_FOUND, "Job not found with ID: " + jobId);
        }
    }

    /**
     * Deletes a job.
     *
     * @param jobId The job ID
     * @return Delete response
     */
    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable String jobId) {
        boolean deleted = jobManagementService.deleteJob(jobId);
        
        if (deleted) {
            return success();
        } else {
            return error(HttpStatus.NOT_FOUND, "Job not found with ID: " + jobId);
        }
    }
}