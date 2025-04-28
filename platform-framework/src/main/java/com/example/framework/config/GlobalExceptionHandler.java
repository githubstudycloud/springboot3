package com.example.framework.config;

import com.example.common.constant.CommonConstants;
import com.example.common.exception.BaseException;
import com.example.common.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * 全局异常处理器
 */
@Configuration
@ConditionalOnWebApplication
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleBaseException(BaseException e) {
        log.error("业务异常", e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常", e);
        return handleBindingResult(e.getBindingResult());
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        log.error("参数绑定异常", e);
        return handleBindingResult(e.getBindingResult());
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("参数类型不匹配", e);
        return Result.error(CommonConstants.FAIL, String.format("参数类型不匹配，参数%s需要%s类型", 
                e.getName(), e.getRequiredType().getSimpleName()));
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(CommonConstants.INTERNAL_SERVER_ERROR, "系统异常，请联系管理员");
    }

    /**
     * 处理绑定结果
     */
    private Result<Void> handleBindingResult(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder errorMsg = new StringBuilder("校验失败：");
        
        for (int i = 0; i < fieldErrors.size(); i++) {
            if (i > 0) {
                errorMsg.append(", ");
            }
            errorMsg.append(fieldErrors.get(i).getDefaultMessage());
        }
        
        return Result.error(CommonConstants.FAIL, errorMsg.toString());
    }
} 