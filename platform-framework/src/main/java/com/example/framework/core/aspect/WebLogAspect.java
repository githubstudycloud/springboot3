package com.example.framework.core.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.UUID;

/**
 * Web请求日志切面
 * 记录所有Web请求的进入和退出日志
 *
 * @author platform
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    /**
     * 线程本地变量，用于存储请求开始时间
     */
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 线程本地变量，用于存储请求ID
     */
    private final ThreadLocal<String> requestId = new ThreadLocal<>();

    /**
     * 定义切点：所有接口类中的所有方法
     */
    @Pointcut("execution(* com.example.*.interfaces..*.*(..))")
    public void webLog() {
        // 方法内部不需要实现
    }

    /**
     * 前置通知：在接口方法执行前记录请求信息
     *
     * @param joinPoint 切点
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        requestId.set(UUID.randomUUID().toString().replace("-", ""));

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // 记录请求信息
            log.info("RequestId: {}, URL: {}, HTTP Method: {}, IP: {}, Class Method: {}.{}, Args: {}",
                    requestId.get(),
                    request.getRequestURL().toString(),
                    request.getMethod(),
                    request.getRemoteAddr(),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        } else {
            log.info("RequestId: {}, Method: {}.{}, Args: {}",
                    requestId.get(),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
    }

    /**
     * 后置通知：在接口方法执行后记录响应信息
     *
     * @param result 方法返回值
     */
    @AfterReturning(returning = "result", pointcut = "webLog()")
    public void doAfterReturning(Object result) {
        // 计算执行时间
        long executionTime = System.currentTimeMillis() - startTime.get();

        // 记录响应信息
        log.info("RequestId: {}, Response: {}, Execution Time: {} ms",
                requestId.get(),
                result,
                executionTime);

        // 清理线程本地变量
        startTime.remove();
        requestId.remove();
    }
}