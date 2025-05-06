package com.example.platform.scheduler.interfaces.rest.request;

import com.example.platform.scheduler.domain.job.JobDependency;
import com.example.platform.scheduler.domain.job.JobRetryStrategy;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * Request DTO for updating an existing job.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateJobRequest {

    private String description;

    @Pattern(regexp = "^(\\d+|\\*) (\\d+|\\*) (\\d+|\\*) (\\d+|\\*) (\\d+|\\*) (\\d+|\\*|\\?)$", 
             message = "Invalid cron expression format")
    private String cronExpression;

    private String timeZone;

    private String priority;

    private String parameters;

    @Min(value = 0, message = "Max retries must be at least 0")
    private Integer maxRetries;

    @Min(value = 0, message = "Retry delay must be at least 0 seconds")
    private Integer retryDelaySeconds;

    private JobRetryStrategy retryStrategy;

    private List<JobDependency> dependencies;

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