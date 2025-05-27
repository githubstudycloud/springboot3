package com.framework.excel.mapper;

import com.framework.excel.entity.ModelCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模型分类Mapper接口
 *
 * @author framework
 * @since 1.0.0
 */
@Mapper
public interface ModelCategoryMapper {

    /**
     * 根据ID查询模型分类
     *
     * @param id 分类ID
     * @return 模型分类信息
     */
    ModelCategory selectById(@Param("id") Long id);

    /**
     * 查询所有模型分类
     *
     * @return 模型分类列表
     */
    List<ModelCategory> selectAll();

    /**
     * 插入模型分类
     *
     * @param category 模型分类信息
     * @return 影响行数
     */
    int insert(ModelCategory category);

    /**
     * 更新模型分类
     *
     * @param category 模型分类信息
     * @return 影响行数
     */
    int update(ModelCategory category);

    /**
     * 删除模型分类
     *
     * @param id 分类ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}