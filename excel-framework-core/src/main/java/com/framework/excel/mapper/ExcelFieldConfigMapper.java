package com.framework.excel.mapper;

import com.framework.excel.entity.config.ExcelFieldConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Excel字段配置Mapper接口
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Mapper
public interface ExcelFieldConfigMapper {
    
    /**
     * 根据模板ID查询字段列表
     */
    List<ExcelFieldConfig> selectByTemplateId(@Param("templateId") Long templateId);
    
    /**
     * 根据ID查询
     */
    ExcelFieldConfig selectById(@Param("id") Long id);
    
    /**
     * 插入字段配置
     */
    int insert(ExcelFieldConfig fieldConfig);
    
    /**
     * 批量插入字段配置
     */
    int insertBatch(@Param("list") List<ExcelFieldConfig> fieldConfigs);
    
    /**
     * 更新字段配置
     */
    int updateById(ExcelFieldConfig fieldConfig);
    
    /**
     * 根据模板ID删除所有字段配置
     */
    int deleteByTemplateId(@Param("templateId") Long templateId);
    
    /**
     * 删除字段配置
     */
    int deleteById(@Param("id") Long id);
}
