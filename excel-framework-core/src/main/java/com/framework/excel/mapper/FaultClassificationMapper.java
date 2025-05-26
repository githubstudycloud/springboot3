package com.framework.excel.mapper;

import com.framework.excel.entity.FaultClassification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 故障分类Mapper接口
 *
 * @author Framework
 * @since 1.0.0
 */
@Mapper
public interface FaultClassificationMapper {
    
    /**
     * 根据ID查询故障分类
     *
     * @param id 分类ID
     * @return 故障分类
     */
    FaultClassification selectById(@Param("id") Long id);
    
    /**
     * 查询故障分类列表
     *
     * @param params 查询参数
     * @return 故障分类列表
     */
    List<FaultClassification> selectList(Map<String, Object> params);
    
    /**
     * 查询所有启用的故障分类
     *
     * @return 故障分类列表
     */
    List<FaultClassification> selectAllEnabled();
    
    /**
     * 插入故障分类
     *
     * @param faultClassification 故障分类
     * @return 影响行数
     */
    int insert(FaultClassification faultClassification);
    
    /**
     * 更新故障分类
     *
     * @param faultClassification 故障分类
     * @return 影响行数
     */
    int update(FaultClassification faultClassification);
    
    /**
     * 删除故障分类
     *
     * @param id 分类ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
