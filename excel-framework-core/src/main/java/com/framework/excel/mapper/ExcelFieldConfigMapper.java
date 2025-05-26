package com.framework.excel.mapper;

import com.framework.excel.entity.ExcelFieldConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Excel字段配置Mapper接口
 *
 * @author Framework
 * @since 1.0.0
 */
@Mapper
public interface ExcelFieldConfigMapper {
    
    /**
     * 根据ID查询字段配置
     *
     * @param id 配置ID
     * @return 字段配置
     */
    ExcelFieldConfig selectById(@Param("id") Long id);
    
    /**
     * 根据模板ID查询字段配置列表
     *
     * @param templateId 模板ID
     * @return 字段配置列表
     */
    List<ExcelFieldConfig> selectByTemplateId(@Param("templateId") Long templateId);
    
    /**
     * 根据模板ID查询可见字段配置列表
     *
     * @param templateId 模板ID
     * @return 可见字段配置列表
     */
    List<ExcelFieldConfig> selectVisibleByTemplateId(@Param("templateId") Long templateId);
    
    /**
     * 插入字段配置
     *
     * @param excelFieldConfig 字段配置
     * @return 影响行数
     */
    int insert(ExcelFieldConfig excelFieldConfig);
    
    /**
     * 批量插入字段配置
     *
     * @param fieldConfigs 字段配置列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<ExcelFieldConfig> fieldConfigs);
    
    /**
     * 更新字段配置
     *
     * @param excelFieldConfig 字段配置
     * @return 影响行数
     */
    int update(ExcelFieldConfig excelFieldConfig);
    
    /**
     * 批量更新字段可见性
     *
     * @param templateId 模板ID
     * @param fieldNames 字段名列表
     * @param visible 是否可见
     * @return 影响行数
     */
    int batchUpdateVisibility(@Param("templateId") Long templateId, 
                            @Param("fieldNames") List<String> fieldNames, 
                            @Param("visible") Boolean visible);
    
    /**
     * 删除字段配置
     *
     * @param id 配置ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据模板ID删除字段配置
     *
     * @param templateId 模板ID
     * @return 影响行数
     */
    int deleteByTemplateId(@Param("templateId") Long templateId);
}
