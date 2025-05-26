package com.framework.excel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Excel配置类
 * 
 * @author Excel Framework Team
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "excel")
public class ExcelConfig {
    
    /**
     * 临时文件目录
     */
    private String tempDir = System.getProperty("java.io.tmpdir") + "/excel";
    
    /**
     * 最大导入行数
     */
    private Integer maxImportRows = 10000;
    
    /**
     * 模板缓存时间(秒)
     */
    private Integer templateCacheTime = 3600;
    
    /**
     * 模板配置
     */
    private Map<String, Object> templates;
    
    /**
     * 是否启用缓存
     */
    private Boolean enableCache = true;
    
    /**
     * 默认日期格式
     */
    private String defaultDateFormat = "yyyy-MM-dd";
    
    /**
     * 默认时间格式
     */
    private String defaultDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 批量处理大小
     */
    private Integer batchSize = 1000;
}
