package com.framework.excel.mapper;

import com.framework.excel.entity.ExcelTemplateConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Excel模板配置Mapper接口
 * 
 * @author framework
 * @since 1.0.0
 */
@Mapper
public interface ExcelTemplateConfigMapper {
    
    /**
     * 根据模板Key查询配置(包含字段)
     * 
     * @param templateKey 模板标识
     * @return Excel模板配置
     */
    ExcelTemplateConfig findByTemplateKey(@Param("templateKey") String templateKey);
    
    /**
     * 查询所有配置(包含字段)
     * 
     * @return 所有Excel模板配置
     */
    List<ExcelTemplateConfig> findAllWithFields();
    
    /**
     * 查询所有配置(不包含字段)
     * 
     * @return 所有Excel模板配置
     */
    List<ExcelTemplateConfig> findAll();
    
    /**
     * 根据ID查询配置(包含字段)
     * 
     * @param id 主键ID
     * @return Excel模板配置
     */
    ExcelTemplateConfig findById(@Param("id") Long id);
    
    /**
     * 插入配置
     * 
     * @param config Excel模板配置
     * @return 影响行数
     */
    int insert(ExcelTemplateConfig config);
    
    /**
     * 更新配置
     * 
     * @param config Excel模板配置
     * @return 影响行数
     */
    int update(ExcelTemplateConfig config);
    
    /**
     * 根据模板Key删除配置
     * 
     * @param templateKey 模板标识
     * @return 影响行数
     */
    int deleteByTemplateKey(@Param("templateKey") String templateKey);
    
    /**
     * 根据ID删除配置
     * 
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据模板Key统计数量
     * 
     * @param templateKey 模板标识
     * @return 数量
     */
    int countByTemplateKey(@Param("templateKey") String templateKey);
    
    /**
     * 根据状态查询配置
     * 
     * @param status 状态
     * @return Excel模板配置列表
     */
    List<ExcelTemplateConfig> findByStatus(@Param("status") Integer status);
} 