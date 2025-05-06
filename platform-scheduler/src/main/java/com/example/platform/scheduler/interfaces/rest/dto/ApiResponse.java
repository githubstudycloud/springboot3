package com.example.platform.scheduler.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard API response format for all REST endpoints.
 *
 * @param <T> The type of data being returned
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private final boolean success;
    private final String message;
    private final T data;
    
    /**
     * Constructs a new API response.
     *
     * @param success Whether the operation was successful
     * @param message Response message
     * @param data Response data (may be null)
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    /**
     * Get whether the operation was successful.
     *
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Get the response message.
     *
     * @return Response message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Get the response data.
     *
     * @return Response data
     */
    public T getData() {
        return data;
    }
}