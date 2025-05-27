package com.framework.excel.entity.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * Excel字段配置实体
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class ExcelFieldConfig {
    private Long id;
    private Long templateId;
    private String fieldName;
    private String columnName;
    private Integer columnIndex;
    private String dataType;
    private Boolean required;
    private Boolean visible;
    private Integer width;
    private String dateFormat;
    private String dropdownConfig;    // JSON格式
    private String validationRules;   // JSON格式
    private String extraConfig;       // JSON格式扩展配置
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;
    
    /**
     * 获取下拉配置对象
     */
    public DropdownConfig getDropdownConfigObject() {
        if (dropdownConfig == null || dropdownConfig.isEmpty()) {
            return null;
        }
        return JSON.parseObject(dropdownConfig, DropdownConfig.class);
    }
    
    /**
     * 设置下拉配置
     */
    public void setDropdownConfigObject(DropdownConfig config) {
        this.dropdownConfig = config != null ? JSON.toJSONString(config) : null;
    }
    
    /**
     * 获取验证规则JSON对象
     */
    public JSONObject getValidationRulesJson() {
        if (validationRules == null || validationRules.isEmpty()) {
            return new JSONObject();
        }
        return JSON.parseObject(validationRules);
    }
    
    /**
     * 获取扩展配置JSON对象
     */
    public JSONObject getExtraConfigJson() {
        if (extraConfig == null || extraConfig.isEmpty()) {
            return new JSONObject();
        }
        return JSON.parseObject(extraConfig);
    }
}
