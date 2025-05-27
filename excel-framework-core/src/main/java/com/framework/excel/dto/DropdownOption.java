package com.framework.excel.dto;

import lombok.Data;

/**
 * 下拉选项
 *
 * @author framework
 * @since 1.0.0
 */
@Data
public class DropdownOption {

    /**
     * 选项值
     */
    private Object value;

    /**
     * 显示文本
     */
    private String label;

    /**
     * 是否被选中
     */
    private Boolean selected;

    /**
     * 扩展属性
     */
    private Object extra;

    public DropdownOption() {
        this.selected = false;
    }

    public DropdownOption(Object value, String label) {
        this.value = value;
        this.label = label;
        this.selected = false;
    }

    public DropdownOption(Object value, String label, Boolean selected) {
        this.value = value;
        this.label = label;
        this.selected = selected;
    }
}