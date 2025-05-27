package com.framework.excel.entity.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Excel模板配置实体
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Data
public class ExcelTemplateConfig {
    private Long id;
    private String templateKey;
    private String templateName;
    private String entityClass;
    private String tableName;
    private String sheetName;
    private String primaryKeyFields;  // 逗号分隔的字段名
    private String updateMode;
    private String businessConfig;    // JSON格式的业务配置
    private String description;
    private Boolean enabled;
    private Date createTime;
    private Date updateTime;
    
    // 字段配置列表
    private List<ExcelFieldConfig> fields;
    
    /**
     * 获取主键字段列表
     */
    public List<String> getPrimaryKeyFieldList() {
        if (primaryKeyFields == null || primaryKeyFields.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(primaryKeyFields.split(","));
    }
    
    /**
     * 获取业务配置JSON对象
     */
    public JSONObject getBusinessConfigJson() {
        if (businessConfig == null || businessConfig.isEmpty()) {
            return new JSONObject();
        }
        return JSON.parseObject(businessConfig);
    }
    
    /**
     * 设置业务配置
     */
    public void setBusinessConfigJson(JSONObject config) {
        this.businessConfig = config != null ? config.toJSONString() : null;
    }
}
