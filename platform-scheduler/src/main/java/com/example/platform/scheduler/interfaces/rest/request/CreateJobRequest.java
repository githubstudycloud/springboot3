package com.example.platform.scheduler.interfaces.rest.request;

import com.example.platform.scheduler.domain.job.JobDependency;
import com.example.platform.scheduler.domain.job.JobRetryStrategy;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * Request DTO for creating a new job.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateJobRequest {

    @NotBlank(message = "Job name is required")
    private String name;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotBlank(message = "Job type is required")
    private String jobType;

    @NotBlank(message = "Cron expression is required")
    @Pattern(regexp = "^(\\d+|\\*) (\\d+|\\*) (\\d+|\\*) (\\d+|\\*) (\\d+|\\*) (\\d+|\\*|\\?)$", 
             message = "Invalid cron expression format")
    private String cronExpression;

    @NotBlank(message = "Time zone is required")
    private String timeZone;

    @NotBlank(message = "Priority is required")
    private String priority;

    @NotBlank(message = "Job parameters are required")
    private String parameters;

    @NotNull(message = "Max retries is required")
    @Min(value = 0, message = "Max retries must be at least 0")
    private Integer maxRetries;

    @NotNull(message = "Retry delay is required")
    @Min(value = 0, message = "Retry delay must be at least 0 seconds")
    private Integer retryDelaySeconds;

    @NotNull(message = "Retry strategy is required")
    private JobRetryStrategy retryStrategy;

    private List<JobDependency> dependencies;

    /**
     * Gets the job name.
     * 
     * @return Job name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the job name.
     * 
     * @param name Job name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the job description.
     * 
     * @return Job description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the job description.
     * 
     * @param description Job description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the job type.
     * 
     * @return Job type
     */
    public String getJobType() {
        return jobType;
    }

    /**
     * Sets the job type.
     * 
     * @param jobType Job type
     */
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    /**
     * Gets the cron expression.
     * 
     * @return Cron expression
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * Sets the cron expression.
     * 
     * @param cronExpression Cron expression
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /**
     * Gets the time zone.
     * 
     * @return Time zone
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the time zone.
     * 
     * @param timeZone Time zone
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Gets the job priority.
     * 
     * @return Job priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the job priority.
     * 
     * @param priority Job priority
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Gets the job parameters.
     * 
     * @return Job parameters as JSON string
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * Sets the job parameters.
     * 
     * @param parameters Job parameters as JSON string
     */
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the maximum number of retries.
     * 
     * @return Maximum retries
     */
    public Integer getMaxRetries() {
        return maxRetries;
    }

    /**
     * Sets the maximum number of retries.
     * 
     * @param maxRetries Maximum retries
     */
    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * Gets the retry delay in seconds.
     * 
     * @return Retry delay
     */
    public Integer getRetryDelaySeconds() {
        return retryDelaySeconds;
    }

    /**
     * Sets the retry delay in seconds.
     * 
     * @param retryDelaySeconds Retry delay
     */
    public void setRetryDelaySeconds(Integer retryDelaySeconds) {
        this.retryDelaySeconds = retryDelaySeconds;
    }

    /**
     * Gets the retry strategy.
     * 
     * @return Retry strategy
     */
    public JobRetryStrategy getRetryStrategy() {
        return retryStrategy;
    }

    /**
     * Sets the retry strategy.
     * 
     * @param retryStrategy Retry strategy
     */
    public void setRetryStrategy(JobRetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    /**
     * Gets the job dependencies.
     * 
     * @return List of job dependencies
     */
    public List<JobDependency> getDependencies() {
        return dependencies;
    }

    /**
     * Sets the job dependencies.
     * 
     * @param dependencies List of job dependencies
     */
    public void setDependencies(List<JobDependency> dependencies) {
        this.dependencies = dependencies;
    }
}