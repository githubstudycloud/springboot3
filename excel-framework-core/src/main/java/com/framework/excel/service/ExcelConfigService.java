package com.framework.excel.service;

import com.framework.excel.entity.ExcelTemplateConfig;
import com.framework.excel.enums.ConfigSourceType;

import java.util.List;

/**
 * Excel配置管理服务接口
 * 
 * @author framework
 * @since 1.0.0
 */
public interface ExcelConfigService {
    
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
     * 获取启用状态的配置
     * 
     * @return 启用的Excel模板配置
     */
    List<ExcelTemplateConfig> getEnabledConfigs();
    
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
     * 启用配置
     * 
     * @param templateKey 模板标识
     * @return 操作结果
     */
    boolean enableConfig(String templateKey);
    
    /**
     * 禁用配置
     * 
     * @param templateKey 模板标识
     * @return 操作结果
     */
    boolean disableConfig(String templateKey);
    
    /**
     * 检查配置是否存在
     * 
     * @param templateKey 模板标识
     * @return 是否存在
     */
    boolean exists(String templateKey);
    
    /**
     * 切换配置源类型
     * 
     * @param sourceType 配置源类型
     * @return 切换结果
     */
    boolean switchConfigSource(ConfigSourceType sourceType);
    
    /**
     * 获取当前配置源类型
     * 
     * @return 配置源类型
     */
    ConfigSourceType getCurrentConfigSourceType();
    
    /**
     * 获取配置源状态
     * 
     * @return 配置源状态描述
     */
    String getConfigSourceStatus();
    
    /**
     * 从文件同步配置到数据库
     * 
     * @return 同步结果
     */
    boolean syncFileConfigToDatabase();
    
    /**
     * 刷新配置缓存
     * 
     * @return 刷新结果
     */
    boolean refreshCache();
}