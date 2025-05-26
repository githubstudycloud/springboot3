package com.example.excel.provider;

/**
 * 下拉选项
 */
public class DropdownOption {
    /**
     * 选项值
     */
    private Object value;
    
    /**
     * 显示文本
     */
    private String display;

    public DropdownOption() {
    }

    public DropdownOption(Object value, String display) {
        this.value = value;
        this.display = display;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
