package com.example.excel.provider;

import java.util.List;

/**
 * 下拉数据提供者接口
 * 用于为Excel字段提供下拉选项
 */
public interface DropdownProvider {
    /**
     * 获取下拉选项列表
     * 
     * @param params 可选参数
     * @return 下拉选项列表
     */
    List<DropdownOption> getOptions(Object... params);
}
