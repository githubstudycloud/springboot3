package com.platform.scheduler.config.datasource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源切换注解
 * 
 * @author platform
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceSwitch {
    
    /**
     * 数据源名称
     */
    String value() default "master";
    
    /**
     * 是否是日志数据源
     */
    boolean isLogDatabase() default false;
    
    /**
     * 日志数据源哈希字段
     * 用于计算使用哪个日志数据源，仅当isLogDatabase=true时生效
     */
    String hashField() default "";
}
