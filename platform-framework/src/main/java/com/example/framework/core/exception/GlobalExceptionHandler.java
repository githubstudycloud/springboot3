package com.example.framework.core.exception;

import com.example.common.constant.SystemConstants;
import com.example.common.exception.BusinessException;
import com.example.common.exception.PlatformException;
import com.example.common.exception.SystemException;
import com.example.common.model.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author platform
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 平台基础异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(PlatformException.class)
    public R<Void> handlePlatformException(PlatformException e, HttpServletRequest request) {
        log.error("Platform exception occurred: {}, URI: {}", e.getMessage(), request.getRequestURI(), e);
        return R.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 业务异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("Business exception occurred: {}, URI: {}", e.getMessage(), request.getRequestURI());
        return R.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 系统异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleSystemException(SystemException e, HttpServletRequest request) {
        log.error("System exception occurred: {}, URI: {}", e.getMessage(), request.getRequestURI(), e);
        return R.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 参数校验异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Method argument validation failed: {}, URI: {}", message, request.getRequestURI());
        return R.error(SystemConstants.BAD_REQUEST_CODE, message);
    }
    
    /**
     * 参数绑定异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Parameter bind failed: {}, URI: {}", message, request.getRequestURI());
        return R.error(SystemConstants.BAD_REQUEST_CODE, message);
    }
    
    /**
     * 参数校验异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("Constraint violation: {}, URI: {}", message, request.getRequestURI());
        return R.error(SystemConstants.BAD_REQUEST_CODE, message);
    }
    
    /**
     * 请求参数类型不匹配异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("Method argument type mismatch: {}, URI: {}", e.getMessage(), request.getRequestURI());
        return R.error(SystemConstants.BAD_REQUEST_CODE, "参数类型不匹配: " + e.getName());
    }
    
    /**
     * 缺少请求参数异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("Missing request parameter: {}, URI: {}", e.getMessage(), request.getRequestURI());
        return R.error(SystemConstants.BAD_REQUEST_CODE, "缺少必要参数: " + e.getParameterName());
    }
    
    /**
     * HTTP请求方法不支持异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("Request method not supported: {}, URI: {}", e.getMessage(), request.getRequestURI());
        String supportedMethods = StringUtils.join(e.getSupportedMethods(), ", ");
        return R.error(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持的请求方法: " + e.getMethod() + ", 支持的方法: " + supportedMethods);
    }
    
    /**
     * 请求体不可读异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("HTTP message not readable: {}, URI: {}", e.getMessage(), request.getRequestURI());
        return R.error(SystemConstants.BAD_REQUEST_CODE, "请求体格式错误");
    }
    
    /**
     * 404异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("No handler found: {}, URI: {}", e.getMessage(), request.getRequestURI());
        return R.error(SystemConstants.NOT_FOUND_CODE, "请求的资源不存在");
    }
    
    /**
     * 默认异常处理
     *
     * @param e       异常
     * @param request 请求
     * @return 响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("Unexpected exception occurred: {}, URI: {}", e.getMessage(), request.getRequestURI(), e);
        return R.error(SystemConstants.ERROR_CODE, "服务器内部错误，请稍后再试");
    }
}