package com.framework.excel.mapper;

import com.framework.excel.entity.config.ExcelTemplateConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Excel模板配置Mapper接口
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Mapper
public interface ExcelTemplateConfigMapper {
    
    /**
     * 根据ID查询
     */
    ExcelTemplateConfig selectById(@Param("id") Long id);
    
    /**
     * 根据模板key查询
     */
    ExcelTemplateConfig selectByTemplateKey(@Param("templateKey") String templateKey);
    
    /**
     * 查询所有启用的配置
     */
    List<ExcelTemplateConfig> selectEnabledList();
    
    /**
     * 插入配置
     */
    int insert(ExcelTemplateConfig config);
    
    /**
     * 更新配置
     */
    int updateById(ExcelTemplateConfig config);
    
    /**
     * 删除配置
     */
    int deleteById(@Param("id") Long id);
}
