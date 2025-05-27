package com.framework.excel.config;

import com.framework.excel.entity.ExcelTemplateConfig;

import java.util.List;

/**
 * Excel配置源接口
 * 
 * @author framework
 * @since 1.0.0
 */
public interface ExcelConfigSource {
    
    /**
     * 根据模板Key获取配置
     * 
     * @param templateKey 模板标识
     * @return Excel模板配置
     */
    ExcelTemplateConfig getConfig(String templateKey);
    
    /**
     * 获取所有配置
     * 
     * @return 所有Excel模板配置
     */
    List<ExcelTemplateConfig> getAllConfigs();
    
    /**
     * 保存配置
     * 
     * @param config Excel模板配置
     * @return 保存结果
     */
    boolean saveConfig(ExcelTemplateConfig config);
    
    /**
     * 删除配置
     * 
     * @param templateKey 模板标识
     * @return 删除结果
     */
    boolean deleteConfig(String templateKey);
    
    /**
     * 更新配置
     * 
     * @param config Excel模板配置
     * @return 更新结果
     */
    boolean updateConfig(ExcelTemplateConfig config);
    
    /**
     * 检查配置是否存在
     * 
     * @param templateKey 模板标识
     * @return 是否存在
     */
    boolean exists(String templateKey);
    
    /**
     * 获取配置源类型名称
     * 
     * @return 配置源类型
     */
    String getSourceType();
} 