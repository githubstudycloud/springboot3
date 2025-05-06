package com.example.platform.scheduler.interfaces.rest;

import com.example.platform.scheduler.interfaces.rest.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Base controller with common functionality for all REST controllers.
 */
public abstract class BaseController {
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Creates a success response with data.
     *
     * @param data The data to include in the response
     * @param <T> The type of data
     * @return ResponseEntity containing ApiResponse
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", data));
    }
    
    /**
     * Creates a success response without data.
     *
     * @return ResponseEntity containing ApiResponse
     */
    protected ResponseEntity<ApiResponse<Void>> success() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Success", null));
    }
    
    /**
     * Creates an error response.
     *
     * @param status HTTP status code
     * @param message Error message
     * @return ResponseEntity containing ApiResponse
     */
    protected ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(false, message, null));
    }
    
    /**
     * Handles exceptions thrown during request processing.
     *
     * @param ex The exception
     * @return ResponseEntity containing ApiResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred: " + ex.getMessage());
    }
    
    /**
     * Handles IllegalArgumentException.
     *
     * @param ex The exception
     * @return ResponseEntity containing ApiResponse
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid request parameter: {}", ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    
    /**
     * Handles IllegalStateException.
     *
     * @param ex The exception
     * @return ResponseEntity containing ApiResponse
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        logger.warn("Invalid operation: {}", ex.getMessage());
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }
}
