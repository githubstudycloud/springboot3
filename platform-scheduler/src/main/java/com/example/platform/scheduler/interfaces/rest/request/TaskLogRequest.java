package com.example.platform.scheduler.interfaces.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for adding a log entry to a task.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskLogRequest {

    @NotBlank(message = "Executor ID is required")
    private String executorId;

    @NotNull(message = "Log level is required")
    private String logLevel;

    @NotBlank(message = "Log message is required")
    private String message;

    /**
     * Gets the executor ID.
     * 
     * @return Executor ID
     */
    public String getExecutorId() {
        return executorId;
    }

    /**
     * Sets the executor ID.
     * 
     * @param executorId Executor ID
     */
    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    /**
     * Gets the log level.
     * 
     * @return Log level
     */
    public String getLogLevel() {
        return logLevel;
    }

    /**
     * Sets the log level.
     * 
     * @param logLevel Log level
     */
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Gets the log message.
     * 
     * @return Log message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the log message.
     * 
     * @param message Log message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}