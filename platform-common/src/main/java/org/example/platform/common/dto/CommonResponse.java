package org.example.platform.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * Common Response for API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Response code
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
     * Success response with data
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(200, "Success", data);
    }
    
    /**
     * Success response without data
     */
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(200, "Success", null);
    }
    
    /**
     * Error response with message
     */
    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>(500, message, null);
    }
    
    /**
     * Error response with code and message
     */
    public static <T> CommonResponse<T> error(Integer code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}
