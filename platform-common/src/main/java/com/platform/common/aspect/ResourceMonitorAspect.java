package com.platform.common.aspect;

import com.platform.common.exception.ServerBusyException;
import com.platform.common.utils.ResourceMonitorUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Aspect for monitoring system resources.
 * Rejects requests when system is overloaded.
 */
@Aspect
@Component
@Order(0)
@Slf4j
public class ResourceMonitorAspect {

    /**
     * Define the pointcut for resource monitoring.
     * By default, it applies to all methods in controllers.
     */
    @Pointcut("execution(* com.platform..*.controller..*.*(..))")
    public void controllerPointcut() {
        // Pointcut definition
    }

    /**
     * Check system resources before executing a controller method.
     * Throws ServerBusyException if the system is overloaded.
     *
     * @param joinPoint The join point
     * @return The result of the method execution
     * @throws Throwable If an error occurs during method execution
     */
    @Around("controllerPointcut()")
    public Object checkResources(ProceedingJoinPoint joinPoint) throws Throwable {
        // Check if the system is overloaded
        if (ResourceMonitorUtil.isSystemOverloaded()) {
            log.warn("System resources overloaded, rejecting request: {}.{}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
            
            throw new ServerBusyException();
        }
        
        // System resources are good, proceed with the request
        return joinPoint.proceed();
    }
}
