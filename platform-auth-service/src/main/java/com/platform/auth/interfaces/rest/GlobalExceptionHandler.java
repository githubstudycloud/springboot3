package com.platform.auth.interfaces.rest;

import com.platform.common.model.ResponseResult;
import com.platform.common.model.ResultCode;
import com.platform.common.utils.RequestContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 处理认证服务的异常，返回统一格式的异常响应
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理认证异常
     *
     * @param e 异常
     * @param request 请求
     * @return 异常响应
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseResult<Void> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.error("认证异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI(), e);
        return ResponseResult.failure(ResultCode.UNAUTHORIZED, e.getMessage())
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理授权异常
     *
     * @param e 异常
     * @param request 请求
     * @return 异常响应
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseResult<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("授权异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI(), e);
        return ResponseResult.failure(ResultCode.FORBIDDEN, "没有权限访问该资源")
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理参数校验异常
     *
     * @param e 异常
     * @param request 请求
     * @return 异常响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
                
        log.error("参数校验异常: {}, 请求路径: {}", errorMessage, request.getRequestURI());
        return ResponseResult.failure(ResultCode.PARAM_ERROR, errorMessage)
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理参数绑定异常
     *
     * @param e 异常
     * @param request 请求
     * @return 异常响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleBindException(BindException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
                
        log.error("参数绑定异常: {}, 请求路径: {}", errorMessage, request.getRequestURI());
        return ResponseResult.failure(ResultCode.PARAM_ERROR, errorMessage)
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理非法参数异常
     *
     * @param e 异常
     * @param request 请求
     * @return 异常响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        log.error("非法参数异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI());
        return ResponseResult.failure(ResultCode.PARAM_ERROR, e.getMessage())
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理非法状态异常
     *
     * @param e 异常
     * @param request 请求
     * @return 异常响应
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Void> handleIllegalStateException(
            IllegalStateException e, HttpServletRequest request) {
        log.error("非法状态异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI());
        return ResponseResult.failure(ResultCode.BUSINESS_ERROR, e.getMessage())
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理所有其他异常
     *
     * @param e 异常
     * @param request 请求
     * @return 异常响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI(), e);
        return ResponseResult.failure(ResultCode.SYSTEM_ERROR, "系统异常，请联系管理员")
                .setRequestId(RequestContextUtil.getRequestId());
    }
}
