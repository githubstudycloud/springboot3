package com.framework.excel.provider;

import com.framework.excel.dto.DropdownOption;

import java.util.List;

/**
 * 下拉数据提供者接口
 *
 * @author framework
 * @since 1.0.0
 */
public interface DropdownProvider {

    /**
     * 获取下拉选项
     *
     * @param params 参数
     * @return 下拉选项列表
     */
    List<DropdownOption> getOptions(Object... params);

    /**
     * 获取提供者类型
     *
     * @return 提供者类型
     */
    String getType();
}