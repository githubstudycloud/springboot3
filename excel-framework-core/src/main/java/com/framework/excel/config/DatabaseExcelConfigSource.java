package com.framework.excel.config;

import com.framework.excel.entity.ExcelTemplateConfig;
import com.framework.excel.mapper.ExcelTemplateConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据库Excel配置源实现
 * 
 * @author framework
 * @since 1.0.0
 */
@Component("databaseExcelConfigSource")
public class DatabaseExcelConfigSource implements ExcelConfigSource {
    
    private static final Logger log = LoggerFactory.getLogger(DatabaseExcelConfigSource.class);
    
    @Autowired
    private ExcelTemplateConfigMapper templateConfigMapper;
    
    @Override
    public ExcelTemplateConfig getConfig(String templateKey) {
        try {
            log.debug("从数据库获取Excel配置: {}", templateKey);
            return templateConfigMapper.findByTemplateKey(templateKey);
        } catch (Exception e) {
            log.error("从数据库获取Excel配置失败: {}", templateKey, e);
            return null;
        }
    }
    
    @Override
    public List<ExcelTemplateConfig> getAllConfigs() {
        try {
            log.debug("从数据库获取所有Excel配置");
            return templateConfigMapper.findAllWithFields();
        } catch (Exception e) {
            log.error("从数据库获取所有Excel配置失败", e);
            return null;
        }
    }
    
    @Override
    public boolean saveConfig(ExcelTemplateConfig config) {
        try {
            log.debug("保存Excel配置到数据库: {}", config.getTemplateKey());
            return templateConfigMapper.insert(config) > 0;
        } catch (Exception e) {
            log.error("保存Excel配置到数据库失败: {}", config.getTemplateKey(), e);
            return false;
        }
    }
    
    @Override
    public boolean deleteConfig(String templateKey) {
        try {
            log.debug("从数据库删除Excel配置: {}", templateKey);
            return templateConfigMapper.deleteByTemplateKey(templateKey) > 0;
        } catch (Exception e) {
            log.error("从数据库删除Excel配置失败: {}", templateKey, e);
            return false;
        }
    }
    
    @Override
    public boolean updateConfig(ExcelTemplateConfig config) {
        try {
            log.debug("更新数据库Excel配置: {}", config.getTemplateKey());
            return templateConfigMapper.update(config) > 0;
        } catch (Exception e) {
            log.error("更新数据库Excel配置失败: {}", config.getTemplateKey(), e);
            return false;
        }
    }
    
    @Override
    public boolean exists(String templateKey) {
        try {
            log.debug("检查数据库Excel配置是否存在: {}", templateKey);
            return templateConfigMapper.countByTemplateKey(templateKey) > 0;
        } catch (Exception e) {
            log.error("检查数据库Excel配置是否存在失败: {}", templateKey, e);
            return false;
        }
    }
    
    @Override
    public String getSourceType() {
        return "DATABASE";
    }
} 