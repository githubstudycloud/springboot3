package com.framework.excel.config;

import com.framework.excel.entity.ExcelTemplateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 混合Excel配置源实现
 * 优先使用数据库，降级到文件配置
 * 
 * @author framework
 * @since 1.0.0
 */
@Component("hybridExcelConfigSource")
public class HybridExcelConfigSource implements ExcelConfigSource {
    
    private static final Logger log = LoggerFactory.getLogger(HybridExcelConfigSource.class);
    
    @Autowired
    @Qualifier("databaseExcelConfigSource")
    private ExcelConfigSource databaseConfigSource;
    
    @Autowired
    @Qualifier("fileExcelConfigSource")
    private ExcelConfigSource fileConfigSource;
    
    @Override
    public ExcelTemplateConfig getConfig(String templateKey) {
        log.debug("混合模式获取配置: {}", templateKey);
        
        // 优先从数据库获取
        try {
            ExcelTemplateConfig config = databaseConfigSource.getConfig(templateKey);
            if (config != null) {
                log.debug("从数据库获取到配置: {}", templateKey);
                return config;
            }
        } catch (Exception e) {
            log.warn("数据库配置源获取失败，降级到文件配置: {}", templateKey, e);
        }
        
        // 降级到文件配置
        try {
            ExcelTemplateConfig config = fileConfigSource.getConfig(templateKey);
            if (config != null) {
                log.debug("从文件获取到配置: {}", templateKey);
                return config;
            }
        } catch (Exception e) {
            log.error("文件配置源获取失败: {}", templateKey, e);
        }
        
        log.warn("未找到配置: {}", templateKey);
        return null;
    }
    
    @Override
    public List<ExcelTemplateConfig> getAllConfigs() {
        log.debug("混合模式获取所有配置");
        
        Set<String> configKeys = new HashSet<>();
        List<ExcelTemplateConfig> allConfigs = new ArrayList<>();
        
        // 优先从数据库获取
        try {
            List<ExcelTemplateConfig> dbConfigs = databaseConfigSource.getAllConfigs();
            if (dbConfigs != null && !dbConfigs.isEmpty()) {
                for (ExcelTemplateConfig config : dbConfigs) {
                    if (config != null && config.getTemplateKey() != null) {
                        configKeys.add(config.getTemplateKey());
                        allConfigs.add(config);
                    }
                }
                log.debug("从数据库获取到{}个配置", dbConfigs.size());
            }
        } catch (Exception e) {
            log.warn("数据库配置源获取所有配置失败", e);
        }
        
        // 补充文件配置(避免重复)
        try {
            List<ExcelTemplateConfig> fileConfigs = fileConfigSource.getAllConfigs();
            if (fileConfigs != null && !fileConfigs.isEmpty()) {
                for (ExcelTemplateConfig config : fileConfigs) {
                    if (config != null && config.getTemplateKey() != null 
                        && !configKeys.contains(config.getTemplateKey())) {
                        allConfigs.add(config);
                        configKeys.add(config.getTemplateKey());
                    }
                }
                log.debug("从文件补充{}个配置", fileConfigs.size());
            }
        } catch (Exception e) {
            log.warn("文件配置源获取所有配置失败", e);
        }
        
        log.debug("混合模式共获取到{}个配置", allConfigs.size());
        return allConfigs;
    }
    
    @Override
    public boolean saveConfig(ExcelTemplateConfig config) {
        log.debug("混合模式保存配置: {}", config.getTemplateKey());
        
        // 优先保存到数据库
        try {
            boolean success = databaseConfigSource.saveConfig(config);
            if (success) {
                log.debug("配置已保存到数据库: {}", config.getTemplateKey());
                return true;
            }
        } catch (Exception e) {
            log.error("保存配置到数据库失败: {}", config.getTemplateKey(), e);
        }
        
        // 文件配置源不支持保存
        log.warn("混合模式保存配置失败，文件配置源不支持保存: {}", config.getTemplateKey());
        return false;
    }
    
    @Override
    public boolean deleteConfig(String templateKey) {
        log.debug("混合模式删除配置: {}", templateKey);
        
        // 只能从数据库删除
        try {
            boolean success = databaseConfigSource.deleteConfig(templateKey);
            if (success) {
                log.debug("配置已从数据库删除: {}", templateKey);
                return true;
            }
        } catch (Exception e) {
            log.error("从数据库删除配置失败: {}", templateKey, e);
        }
        
        // 文件配置源不支持删除
        log.warn("混合模式删除配置失败，文件配置源不支持删除: {}", templateKey);
        return false;
    }
    
    @Override
    public boolean updateConfig(ExcelTemplateConfig config) {
        log.debug("混合模式更新配置: {}", config.getTemplateKey());
        
        // 优先更新数据库
        try {
            boolean success = databaseConfigSource.updateConfig(config);
            if (success) {
                log.debug("配置已在数据库中更新: {}", config.getTemplateKey());
                return true;
            }
        } catch (Exception e) {
            log.error("更新数据库配置失败: {}", config.getTemplateKey(), e);
        }
        
        // 文件配置源不支持更新
        log.warn("混合模式更新配置失败，文件配置源不支持更新: {}", config.getTemplateKey());
        return false;
    }
    
    @Override
    public boolean exists(String templateKey) {
        log.debug("混合模式检查配置是否存在: {}", templateKey);
        
        // 优先检查数据库
        try {
            if (databaseConfigSource.exists(templateKey)) {
                log.debug("数据库中存在配置: {}", templateKey);
                return true;
            }
        } catch (Exception e) {
            log.warn("检查数据库配置存在性失败: {}", templateKey, e);
        }
        
        // 检查文件配置
        try {
            if (fileConfigSource.exists(templateKey)) {
                log.debug("文件中存在配置: {}", templateKey);
                return true;
            }
        } catch (Exception e) {
            log.warn("检查文件配置存在性失败: {}", templateKey, e);
        }
        
        log.debug("配置不存在: {}", templateKey);
        return false;
    }
    
    @Override
    public String getSourceType() {
        return "HYBRID";
    }
    
    /**
     * 获取可用的配置源状态
     * 
     * @return 配置源状态描述
     */
    public String getConfigSourceStatus() {
        StringBuilder status = new StringBuilder("HYBRID[");
        
        try {
            databaseConfigSource.getAllConfigs();
            status.append("DB:UP");
        } catch (Exception e) {
            status.append("DB:DOWN");
        }
        
        status.append(",");
        
        try {
            fileConfigSource.getAllConfigs();
            status.append("FILE:UP");
        } catch (Exception e) {
            status.append("FILE:DOWN");
        }
        
        status.append("]");
        return status.toString();
    }
} 