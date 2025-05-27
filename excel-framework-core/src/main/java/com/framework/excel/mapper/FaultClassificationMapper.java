package com.framework.excel.mapper;

import com.framework.excel.entity.FaultClassification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 故障分类Mapper接口
 *
 * @author framework
 * @since 1.0.0
 */
@Mapper
public interface FaultClassificationMapper {

    /**
     * 根据ID查询故障分类
     *
     * @param id 分类ID
     * @return 故障分类信息
     */
    FaultClassification selectById(@Param("id") Long id);

    /**
     * 查询所有故障分类
     *
     * @return 故障分类列表
     */
    List<FaultClassification> selectAll();

    /**
     * 插入故障分类
     *
     * @param classification 故障分类信息
     * @return 影响行数
     */
    int insert(FaultClassification classification);

    /**
     * 更新故障分类
     *
     * @param classification 故障分类信息
     * @return 影响行数
     */
    int update(FaultClassification classification);

    /**
     * 删除故障分类
     *
     * @param id 分类ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}