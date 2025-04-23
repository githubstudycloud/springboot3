package com.platform.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Standard response format for all API responses.
 * @param <T> Type of data returned
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Response code, 200 for success, other values for failure
     */
    private Integer code;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private T data;

    /**
     * Request ID for tracing
     */
    private String requestId;

    /**
     * Response timestamp
     */
    private long timestamp;
    
    /**
     * Create a success response
     * @param <T> Data type
     * @return Result with success code and null data
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * Create a success response with data
     * @param data Response data
     * @param <T> Data type
     * @return Result with success code and data
     */
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(200)
                .message("Success")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Create a failure response
     * @param code Error code
     * @param message Error message
     * @param <T> Data type
     * @return Result with error code and message
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Create a server error response
     * @param message Error message
     * @param <T> Data type
     * @return Result with server error code and message
     */
    public static <T> Result<T> error(String message) {
        return fail(500, message);
    }

    /**
     * Create a client error response
     * @param message Error message
     * @param <T> Data type
     * @return Result with client error code and message
     */
    public static <T> Result<T> clientError(String message) {
        return fail(400, message);
    }

    /**
     * Create an unauthorized response
     * @param <T> Data type
     * @return Result with unauthorized error code
     */
    public static <T> Result<T> unauthorized() {
        return fail(401, "Unauthorized");
    }

    /**
     * Create a forbidden response
     * @param <T> Data type
     * @return Result with forbidden error code
     */
    public static <T> Result<T> forbidden() {
        return fail(403, "Forbidden");
    }

    /**
     * Create a not found response
     * @param <T> Data type
     * @return Result with not found error code
     */
    public static <T> Result<T> notFound() {
        return fail(404, "Not Found");
    }

    /**
     * Create a busy server response
     * @param <T> Data type
     * @return Result indicating server is too busy
     */
    public static <T> Result<T> busy() {
        return fail(503, "Server is busy, please try again later");
    }

    /**
     * Check if this result is a success
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return code != null && code == 200;
    }
}
