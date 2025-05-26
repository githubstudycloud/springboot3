package com.framework.excel.mapper;

import com.framework.excel.entity.ExcelTemplateConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Excel模板配置Mapper接口
 *
 * @author Framework
 * @since 1.0.0
 */
@Mapper
public interface ExcelTemplateConfigMapper {
    
    /**
     * 根据ID查询模板配置
     *
     * @param id 配置ID
     * @return 模板配置
     */
    ExcelTemplateConfig selectById(@Param("id") Long id);
    
    /**
     * 根据模板Key查询模板配置
     *
     * @param templateKey 模板Key
     * @return 模板配置
     */
    ExcelTemplateConfig selectByTemplateKey(@Param("templateKey") String templateKey);
    
    /**
     * 查询模板配置列表
     *
     * @param params 查询参数
     * @return 模板配置列表
     */
    List<ExcelTemplateConfig> selectList(Map<String, Object> params);
    
    /**
     * 插入模板配置
     *
     * @param excelTemplateConfig 模板配置
     * @return 影响行数
     */
    int insert(ExcelTemplateConfig excelTemplateConfig);
    
    /**
     * 更新模板配置
     *
     * @param excelTemplateConfig 模板配置
     * @return 影响行数
     */
    int update(ExcelTemplateConfig excelTemplateConfig);
    
    /**
     * 删除模板配置
     *
     * @param id 配置ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
