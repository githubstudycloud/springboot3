package com.platform.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.platform.common.model.ResponseResult;
import com.platform.common.model.ResultCode;
import com.platform.common.utils.RequestContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseResult<Object> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {}, 请求路径: {}, 异常原因: {}", e.getResultCode(), request.getRequestURI(), e.getMessage());
        return ResponseResult.failure(e.getResultCode(), e.getMessage(), e.getErrorData())
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理参数校验异常 (JSR303 @Valid 注解校验)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
                
        log.warn("参数校验异常: {}, 请求路径: {}", errorMessage, request.getRequestURI());
        return ResponseResult.failure(ResultCode.VALIDATION_ERROR, errorMessage)
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理数据绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Object> handleBindException(BindException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
                
        log.warn("数据绑定异常: {}, 请求路径: {}", errorMessage, request.getRequestURI());
        return ResponseResult.failure(ResultCode.VALIDATION_ERROR, errorMessage)
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理参数校验异常 (方法参数注解校验)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Object> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
                
        log.warn("参数约束异常: {}, 请求路径: {}", errorMessage, request.getRequestURI());
        return ResponseResult.failure(ResultCode.VALIDATION_ERROR, errorMessage)
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少请求参数异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI());
        return ResponseResult.failure(ResultCode.MISSING_PARAMETER, "缺少请求参数: " + e.getParameterName())
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("参数类型不匹配异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI());
        return ResponseResult.failure(ResultCode.PARAM_ERROR, "参数类型不匹配: " + e.getName())
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseResult<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI());
        return ResponseResult.failure(ResultCode.OPERATION_DENIED, "不支持 " + e.getMethod() + " 请求方法")
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.warn("文件上传大小超限异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI());
        return ResponseResult.failure(ResultCode.FILE_UPLOAD_ERROR, "上传文件过大")
                .setRequestId(RequestContextUtil.getRequestId());
    }
    
    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<Object> handleException(Throwable e, HttpServletRequest request) {
        log.error("系统异常: {}, 请求路径: {}", e.getMessage(), request.getRequestURI(), e);
        return ResponseResult.failure(ResultCode.SYSTEM_ERROR, "系统异常，请联系管理员")
                .setRequestId(RequestContextUtil.getRequestId());
    }
}
