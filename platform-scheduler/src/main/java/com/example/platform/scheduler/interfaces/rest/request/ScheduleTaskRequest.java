package com.example.platform.scheduler.interfaces.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Request DTO for scheduling a new task.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleTaskRequest {

    @NotBlank(message = "Job ID is required")
    private String jobId;

    @NotNull(message = "Scheduled time is required")
    @Future(message = "Scheduled time must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime scheduledTime;

    private String parameters;

    /**
     * Gets the job ID.
     * 
     * @return Job ID
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Sets the job ID.
     * 
     * @param jobId Job ID
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * Gets the scheduled execution time.
     * 
     * @return Scheduled time
     */
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    /**
     * Sets the scheduled execution time.
     * 
     * @param scheduledTime Scheduled time
     */
    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    /**
     * Gets the task parameters.
     * 
     * @return Task parameters as JSON string
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * Sets the task parameters.
     * 
     * @param parameters Task parameters as JSON string
     */
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}