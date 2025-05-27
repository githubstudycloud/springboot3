package com.framework.excel.service.impl;

import com.framework.excel.config.ExcelConfigSource;
import com.framework.excel.entity.ExcelTemplateConfig;
import com.framework.excel.enums.ConfigSourceType;
import com.framework.excel.service.ExcelConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Excel配置管理服务实现
 * 
 * @author framework
 * @since 1.0.0
 */
@Service
public class ExcelConfigServiceImpl implements ExcelConfigService {
    
    private static final Logger log = LoggerFactory.getLogger(ExcelConfigServiceImpl.class);
    
    @Value("${excel.framework.config-source:hybrid}")
    private String configSourceType;
    
    @Autowired
    @Qualifier("hybridExcelConfigSource")
    private ExcelConfigSource hybridConfigSource;
    
    @Autowired
    @Qualifier("databaseExcelConfigSource")
    private ExcelConfigSource databaseConfigSource;
    
    @Autowired
    @Qualifier("fileExcelConfigSource")
    private ExcelConfigSource fileConfigSource;
    
    private ExcelConfigSource currentConfigSource;
    
    /**
     * 获取当前配置源
     */
    private ExcelConfigSource getCurrentConfigSource() {
        if (currentConfigSource == null) {
            ConfigSourceType sourceType = ConfigSourceType.fromCode(configSourceType);
            switch (sourceType) {
                case DATABASE:
                    currentConfigSource = databaseConfigSource;
                    break;
                case YAML_FILE:
                    currentConfigSource = fileConfigSource;
                    break;
                case HYBRID:
                default:
                    currentConfigSource = hybridConfigSource;
                    break;
            }
            log.info("初始化配置源: {}", currentConfigSource.getSourceType());
        }
        return currentConfigSource;
    }
    
    @Override
    public ExcelTemplateConfig getConfig(String templateKey) {
        log.debug("获取Excel配置: {}", templateKey);
        return getCurrentConfigSource().getConfig(templateKey);
    }
    
    @Override
    public List<ExcelTemplateConfig> getAllConfigs() {
        log.debug("获取所有Excel配置");
        return getCurrentConfigSource().getAllConfigs();
    }
    
    @Override
    public List<ExcelTemplateConfig> getEnabledConfigs() {
        log.debug("获取启用的Excel配置");
        List<ExcelTemplateConfig> allConfigs = getAllConfigs();
        if (allConfigs == null) {
            return null;
        }
        return allConfigs.stream()
                .filter(config -> config.getStatus() != null && config.getStatus() == 1)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean saveConfig(ExcelTemplateConfig config) {
        log.info("保存Excel配置: {}", config.getTemplateKey());
        return getCurrentConfigSource().saveConfig(config);
    }
    
    @Override
    public boolean deleteConfig(String templateKey) {
        log.info("删除Excel配置: {}", templateKey);
        return getCurrentConfigSource().deleteConfig(templateKey);
    }
    
    @Override
    public boolean updateConfig(ExcelTemplateConfig config) {
        log.info("更新Excel配置: {}", config.getTemplateKey());
        return getCurrentConfigSource().updateConfig(config);
    }
    
    @Override
    public boolean enableConfig(String templateKey) {
        log.info("启用Excel配置: {}", templateKey);
        ExcelTemplateConfig config = getConfig(templateKey);
        if (config != null) {
            config.setStatus(1);
            return updateConfig(config);
        }
        return false;
    }
    
    @Override
    public boolean disableConfig(String templateKey) {
        log.info("禁用Excel配置: {}", templateKey);
        ExcelTemplateConfig config = getConfig(templateKey);
        if (config != null) {
            config.setStatus(0);
            return updateConfig(config);
        }
        return false;
    }
    
    @Override
    public boolean exists(String templateKey) {
        return getCurrentConfigSource().exists(templateKey);
    }
    
    @Override
    public boolean switchConfigSource(ConfigSourceType sourceType) {
        log.info("切换配置源: {} -> {}", getCurrentConfigSourceType(), sourceType);
        
        try {
            switch (sourceType) {
                case DATABASE:
                    currentConfigSource = databaseConfigSource;
                    break;
                case YAML_FILE:
                    currentConfigSource = fileConfigSource;
                    break;
                case HYBRID:
                    currentConfigSource = hybridConfigSource;
                    break;
                default:
                    log.warn("不支持的配置源类型: {}", sourceType);
                    return false;
            }
            
            // 测试新配置源是否可用
            currentConfigSource.getAllConfigs();
            log.info("配置源切换成功: {}", sourceType);
            return true;
            
        } catch (Exception e) {
            log.error("配置源切换失败: {}", sourceType, e);
            // 恢复到混合模式
            currentConfigSource = hybridConfigSource;
            return false;
        }
    }
    
    @Override
    public ConfigSourceType getCurrentConfigSourceType() {
        String sourceType = getCurrentConfigSource().getSourceType();
        return ConfigSourceType.fromCode(sourceType.toLowerCase());
    }
    
    @Override
    public String getConfigSourceStatus() {
        if (currentConfigSource == hybridConfigSource) {
            return ((com.framework.excel.config.HybridExcelConfigSource) hybridConfigSource).getConfigSourceStatus();
        } else {
            return getCurrentConfigSource().getSourceType() + ":UP";
        }
    }
    
    @Override
    public boolean syncFileConfigToDatabase() {
        log.info("开始同步文件配置到数据库");
        
        try {
            List<ExcelTemplateConfig> fileConfigs = fileConfigSource.getAllConfigs();
            if (fileConfigs == null || fileConfigs.isEmpty()) {
                log.info("没有文件配置需要同步");
                return true;
            }
            
            int successCount = 0;
            int totalCount = fileConfigs.size();
            
            for (ExcelTemplateConfig config : fileConfigs) {
                try {
                    // 检查数据库中是否已存在
                    if (databaseConfigSource.exists(config.getTemplateKey())) {
                        // 更新
                        if (databaseConfigSource.updateConfig(config)) {
                            successCount++;
                            log.debug("更新配置到数据库: {}", config.getTemplateKey());
                        }
                    } else {
                        // 插入
                        if (databaseConfigSource.saveConfig(config)) {
                            successCount++;
                            log.debug("保存配置到数据库: {}", config.getTemplateKey());
                        }
                    }
                } catch (Exception e) {
                    log.error("同步配置失败: {}", config.getTemplateKey(), e);
                }
            }
            
            log.info("配置同步完成: {}/{}", successCount, totalCount);
            return successCount == totalCount;
            
        } catch (Exception e) {
            log.error("同步文件配置到数据库失败", e);
            return false;
        }
    }
    
    @Override
    public boolean refreshCache() {
        log.info("刷新配置缓存");
        
        try {
            // 如果是文件配置源，重新初始化
            if (currentConfigSource == fileConfigSource) {
                ((com.framework.excel.config.FileExcelConfigSource) fileConfigSource).init();
            }
            
            log.info("配置缓存刷新成功");
            return true;
            
        } catch (Exception e) {
            log.error("刷新配置缓存失败", e);
            return false;
        }
    }
}