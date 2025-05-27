package com.framework.excel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.framework.excel.entity.ExcelFieldConfig;
import com.framework.excel.entity.ExcelTemplateConfig;
import com.framework.excel.enums.DataType;
import com.framework.excel.enums.UpdateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * 文件Excel配置源实现
 * 支持从application.yml和独立配置文件读取
 * 
 * @author framework
 * @since 1.0.0
 */
@Component("fileExcelConfigSource")
@ConfigurationProperties(prefix = "excel")
public class FileExcelConfigSource implements ExcelConfigSource {
    
    private static final Logger log = LoggerFactory.getLogger(FileExcelConfigSource.class);
    
    @Value("${excel.framework.template-path:#{null}}")
    private String templatePath;
    
    private Map<String, Object> templates = new HashMap<>();
    private Map<String, ExcelTemplateConfig> configCache = new HashMap<>();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    
    @PostConstruct
    public void init() {
        loadConfigFromYaml();
        loadConfigFromFiles();
    }
    
    /**
     * 从application.yml加载配置
     */
    private void loadConfigFromYaml() {
        try {
            log.debug("从application.yml加载Excel配置, templates size: {}", templates.size());
            
            for (Map.Entry<String, Object> entry : templates.entrySet()) {
                String templateKey = entry.getKey();
                Map<String, Object> templateData = (Map<String, Object>) entry.getValue();
                
                ExcelTemplateConfig config = convertToTemplateConfig(templateKey, templateData);
                if (config != null) {
                    configCache.put(templateKey, config);
                    log.debug("加载YAML配置: {}", templateKey);
                }
            }
        } catch (Exception e) {
            log.error("从application.yml加载Excel配置失败", e);
        }
    }
    
    /**
     * 从独立配置文件加载配置
     */
    private void loadConfigFromFiles() {
        if (templatePath == null || templatePath.trim().isEmpty()) {
            log.debug("未配置模板路径，跳过文件配置加载");
            return;
        }
        
        try {
            File templateDir = new File(templatePath);
            if (!templateDir.exists() || !templateDir.isDirectory()) {
                log.debug("模板目录不存在: {}", templatePath);
                return;
            }
            
            File[] configFiles = templateDir.listFiles((dir, name) -> 
                name.endsWith(".yml") || name.endsWith(".yaml"));
            
            if (configFiles != null) {
                for (File configFile : configFiles) {
                    loadConfigFromFile(configFile);
                }
            }
        } catch (Exception e) {
            log.error("从配置文件加载Excel配置失败", e);
        }
    }
    
    /**
     * 从单个配置文件加载配置
     */
    private void loadConfigFromFile(File configFile) {
        try (InputStream inputStream = new FileInputStream(configFile)) {
            Map<String, Object> configData = yamlMapper.readValue(inputStream, Map.class);
            
            // 支持两种格式：
            // 1. 直接配置 (templateKey.yml)
            // 2. excel.templates.templateKey 格式
            
            String fileName = configFile.getName();
            String templateKey = fileName.substring(0, fileName.lastIndexOf('.'));
            
            ExcelTemplateConfig config = null;
            
            // 格式1: 直接配置
            if (configData.containsKey("entity-class") || configData.containsKey("entityClass")) {
                config = convertToTemplateConfig(templateKey, configData);
            }
            // 格式2: excel.templates.templateKey 格式
            else if (configData.containsKey("excel")) {
                Map<String, Object> excelData = (Map<String, Object>) configData.get("excel");
                if (excelData.containsKey("templates")) {
                    Map<String, Object> templatesData = (Map<String, Object>) excelData.get("templates");
                    for (Map.Entry<String, Object> entry : templatesData.entrySet()) {
                        String key = entry.getKey();
                        Map<String, Object> templateData = (Map<String, Object>) entry.getValue();
                        config = convertToTemplateConfig(key, templateData);
                        if (config != null) {
                            configCache.put(key, config);
                            log.debug("从文件加载配置: {} -> {}", configFile.getName(), key);
                        }
                    }
                    return;
                }
            }
            
            if (config != null) {
                configCache.put(templateKey, config);
                log.debug("从文件加载配置: {} -> {}", configFile.getName(), templateKey);
            }
            
        } catch (Exception e) {
            log.error("加载配置文件失败: {}", configFile.getName(), e);
        }
    }
    
    /**
     * 转换为模板配置对象
     */
    private ExcelTemplateConfig convertToTemplateConfig(String templateKey, Map<String, Object> data) {
        try {
            ExcelTemplateConfig config = new ExcelTemplateConfig();
            config.setTemplateKey(templateKey);
            
            // 基本属性
            config.setTemplateName(getStringValue(data, "template-name", "templateName"));
            config.setEntityClass(getStringValue(data, "entity-class", "entityClass"));
            config.setTableName(getStringValue(data, "table-name", "tableName"));
            config.setSheetName(getStringValue(data, "sheet-name", "sheetName"));
            config.setDescription(getStringValue(data, "description"));
            
            // 主键策略
            Map<String, Object> primaryKeyStrategy = getMapValue(data, "primary-key-strategy", "primaryKeyStrategy");
            if (primaryKeyStrategy != null) {
                List<String> keyFields = getListValue(primaryKeyStrategy, "key-fields", "keyFields");
                config.setPrimaryKeyFields(keyFields);
                
                String updateModeStr = getStringValue(primaryKeyStrategy, "update-mode", "updateMode");
                if (updateModeStr != null) {
                    config.setUpdateMode(UpdateMode.fromCode(updateModeStr));
                }
            }
            
            // 字段配置
            List<Map<String, Object>> fieldsData = getListMapValue(data, "fields");
            if (fieldsData != null) {
                List<ExcelFieldConfig> fields = new ArrayList<>();
                for (int i = 0; i < fieldsData.size(); i++) {
                    ExcelFieldConfig fieldConfig = convertToFieldConfig(fieldsData.get(i), i);
                    if (fieldConfig != null) {
                        fields.add(fieldConfig);
                    }
                }
                config.setFields(fields);
            }
            
            return config;
            
        } catch (Exception e) {
            log.error("转换模板配置失败: {}", templateKey, e);
            return null;
        }
    }
    
    /**
     * 转换为字段配置对象
     */
    private ExcelFieldConfig convertToFieldConfig(Map<String, Object> data, int index) {
        try {
            ExcelFieldConfig config = new ExcelFieldConfig();
            
            config.setFieldName(getStringValue(data, "field-name", "fieldName"));
            config.setColumnName(getStringValue(data, "column-name", "columnName"));
            config.setColumnIndex(getIntValue(data, "column-index", "columnIndex", index));
            
            String dataTypeStr = getStringValue(data, "data-type", "dataType");
            if (dataTypeStr != null) {
                config.setDataType(DataType.fromCode(dataTypeStr));
            }
            
            config.setRequired(getBooleanValue(data, "required", false));
            config.setVisible(getBooleanValue(data, "visible", true));
            config.setWidth(getIntValue(data, "width", 15));
            config.setDateFormat(getStringValue(data, "date-format", "dateFormat"));
            config.setSortOrder(index);
            
            // 下拉配置
            Map<String, Object> dropdownProvider = getMapValue(data, "dropdown-provider", "dropdownProvider");
            if (dropdownProvider != null) {
                config.setDropdownConfig(dropdownProvider);
            }
            
            return config;
            
        } catch (Exception e) {
            log.error("转换字段配置失败", e);
            return null;
        }
    }
    
    // 辅助方法
    private String getStringValue(Map<String, Object> data, String... keys) {
        for (String key : keys) {
            Object value = data.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
    
    private Integer getIntValue(Map<String, Object> data, String key, int defaultValue) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
    
    private Integer getIntValue(Map<String, Object> data, String key1, String key2, int defaultValue) {
        Integer value = getIntValue(data, key1, defaultValue);
        if (value == defaultValue) {
            value = getIntValue(data, key2, defaultValue);
        }
        return value;
    }
    
    private Boolean getBooleanValue(Map<String, Object> data, String key, boolean defaultValue) {
        Object value = data.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
    
    private Map<String, Object> getMapValue(Map<String, Object> data, String... keys) {
        for (String key : keys) {
            Object value = data.get(key);
            if (value instanceof Map) {
                return (Map<String, Object>) value;
            }
        }
        return null;
    }
    
    private List<String> getListValue(Map<String, Object> data, String... keys) {
        for (String key : keys) {
            Object value = data.get(key);
            if (value instanceof List) {
                List<String> result = new ArrayList<>();
                for (Object item : (List) value) {
                    result.add(item.toString());
                }
                return result;
            }
        }
        return null;
    }
    
    private List<Map<String, Object>> getListMapValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof List) {
            return (List<Map<String, Object>>) value;
        }
        return null;
    }
    
    @Override
    public ExcelTemplateConfig getConfig(String templateKey) {
        return configCache.get(templateKey);
    }
    
    @Override
    public List<ExcelTemplateConfig> getAllConfigs() {
        return new ArrayList<>(configCache.values());
    }
    
    @Override
    public boolean saveConfig(ExcelTemplateConfig config) {
        // 文件配置源不支持保存，返回false
        log.warn("文件配置源不支持保存操作: {}", config.getTemplateKey());
        return false;
    }
    
    @Override
    public boolean deleteConfig(String templateKey) {
        // 文件配置源不支持删除，返回false
        log.warn("文件配置源不支持删除操作: {}", templateKey);
        return false;
    }
    
    @Override
    public boolean updateConfig(ExcelTemplateConfig config) {
        // 文件配置源不支持更新，返回false
        log.warn("文件配置源不支持更新操作: {}", config.getTemplateKey());
        return false;
    }
    
    @Override
    public boolean exists(String templateKey) {
        return configCache.containsKey(templateKey);
    }
    
    @Override
    public String getSourceType() {
        return "FILE";
    }
    
    // Setter for templates (Spring Boot configuration)
    public void setTemplates(Map<String, Object> templates) {
        this.templates = templates;
    }
    
    public Map<String, Object> getTemplates() {
        return templates;
    }
} 