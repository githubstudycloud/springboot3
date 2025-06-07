package com.platform.config.exception;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 全局异常处理器
 *
 * @author Platform Team
 * @since 1.0.0
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 处理配置未找到异常
     */
    @ExceptionHandler(ConfigNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleConfigNotFound(ConfigNotFoundException e) {
        log.warn("配置未找到: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code("CONFIG_NOT_FOUND")
            .message(e.getMessage())
            .details(List.of(
                String.format("应用: %s", e.getApplication()),
                String.format("环境: %s", e.getProfile())
            ))
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * 处理Git同步异常
     */
    @ExceptionHandler(GitSyncException.class)
    public ResponseEntity<ErrorResponse> handleGitSync(GitSyncException e) {
        log.error("Git同步失败: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code("GIT_SYNC_FAILED")
            .message(e.getMessage())
            .details(List.of(
                String.format("操作: %s", e.getOperation()),
                String.format("Git仓库: %s", e.getGitUri())
            ))
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理配置验证异常
     */
    @ExceptionHandler(ConfigValidationException.class)
    public ResponseEntity<ErrorResponse> handleConfigValidation(ConfigValidationException e) {
        log.warn("配置验证失败: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code("CONFIG_VALIDATION_FAILED")
            .message(e.getMessage())
            .details(e.getValidationErrors())
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 处理一般运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code("INTERNAL_ERROR")
            .message("内部服务错误")
            .details(List.of(e.getMessage()))
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理一般异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("未处理的异常: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code("UNKNOWN_ERROR")
            .message("未知错误")
            .details(List.of("请联系系统管理员"))
            .timestamp(LocalDateTime.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 错误响应实体
     */
    @Data
    @Builder
    public static class ErrorResponse {
        private String code;
        private String message;
        private List<String> details;
        private LocalDateTime timestamp;
    }
} 