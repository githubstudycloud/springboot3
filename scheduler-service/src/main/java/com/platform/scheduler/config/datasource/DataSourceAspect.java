package com.platform.scheduler.config.datasource;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 数据源切换切面
 * 
 * @author platform
 */
@Aspect
@Order(1)
@Component
public class DataSourceAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);
    
    /**
     * 日志数据源数量
     */
    private static final int LOG_DATASOURCE_COUNT = 2;
    
    @Pointcut("@annotation(com.platform.scheduler.config.datasource.DataSourceSwitch) " +
            "|| @within(com.platform.scheduler.config.datasource.DataSourceSwitch)")
    public void dataSourcePointCut() {
    }
    
    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        
        DataSourceSwitch dataSourceSwitch = method.getAnnotation(DataSourceSwitch.class);
        if (dataSourceSwitch == null) {
            // 如果方法上没有注解，则查找类上的注解
            dataSourceSwitch = point.getTarget().getClass().getAnnotation(DataSourceSwitch.class);
        }
        
        if (dataSourceSwitch != null) {
            String dataSourceKey = dataSourceSwitch.value();
            
            // 如果是日志数据源，则根据哈希字段计算使用哪个日志数据源
            if (dataSourceSwitch.isLogDatabase()) {
                String hashField = dataSourceSwitch.hashField();
                if (StringUtils.hasText(hashField)) {
                    // 获取哈希字段的参数值
                    Object hashValue = getHashFieldValue(point, method, hashField);
                    if (hashValue != null) {
                        // 根据哈希值计算使用哪个日志数据源
                        int index = Math.abs(hashValue.hashCode() % LOG_DATASOURCE_COUNT);
                        dataSourceKey = "log" + index;
                    }
                } else {
                    // 未指定哈希字段，默认使用第一个日志数据源
                    dataSourceKey = "log0";
                }
            }
            
            logger.debug("设置数据源: {}", dataSourceKey);
            DynamicDataSource.setDataSource(dataSourceKey);
        }
        
        try {
            return point.proceed();
        } finally {
            // 方法执行完毕后，恢复默认数据源
            DynamicDataSource.clearDataSource();
            logger.debug("清除数据源配置，使用默认数据源");
        }
    }
    
    /**
     * 获取哈希字段的参数值
     */
    private Object getHashFieldValue(ProceedingJoinPoint point, Method method, String hashField) {
        // 获取方法参数名
        String[] paramNames = ((MethodSignature) point.getSignature()).getParameterNames();
        // 获取方法参数值
        Object[] args = point.getArgs();
        
        // 查找哈希字段对应的参数
        for (int i = 0; i < paramNames.length; i++) {
            if (hashField.equals(paramNames[i])) {
                return args[i];
            }
        }
        
        // 没有找到对应的参数
        return null;
    }
}
