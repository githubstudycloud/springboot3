package com.example.excel.config;

import java.util.List;

/**
 * 主键策略配置
 * 支持单主键和复合主键
 */
public class PrimaryKeyStrategy {
    /**
     * 主键字段列表(支持复合主键)
     */
    private List<String> keyFields;
    
    /**
     * 更新模式
     */
    private UpdateMode updateMode = UpdateMode.INSERT_OR_UPDATE;

    public List<String> getKeyFields() {
        return keyFields;
    }

    public void setKeyFields(List<String> keyFields) {
        this.keyFields = keyFields;
    }

    public UpdateMode getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(UpdateMode updateMode) {
        this.updateMode = updateMode;
    }
}
