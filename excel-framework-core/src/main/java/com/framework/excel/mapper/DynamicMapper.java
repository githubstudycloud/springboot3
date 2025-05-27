package com.framework.excel.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 动态查询Mapper接口
 *
 * @author framework
 * @since 1.0.0
 */
@Mapper
public interface DynamicMapper {

    /**
     * 动态查询下拉选项
     *
     * @param tableName    表名
     * @param valueField   值字段
     * @param displayField 显示字段
     * @param whereClause  查询条件
     * @return 下拉选项列表
     */
    List<Map<String, Object>> selectDropdownOptions(
            @Param("tableName") String tableName,
            @Param("valueField") String valueField,
            @Param("displayField") String displayField,
            @Param("whereClause") String whereClause
    );

    /**
     * 动态查询数据
     *
     * @param sql 动态SQL
     * @return 查询结果
     */
    List<Map<String, Object>> selectBySql(@Param("sql") String sql);

    /**
     * 动态执行SQL
     *
     * @param sql 动态SQL
     * @return 影响行数
     */
    int executeSql(@Param("sql") String sql);
}