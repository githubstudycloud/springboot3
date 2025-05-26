package com.framework.excel.mapper;

import com.framework.excel.entity.ModelCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 模型分类Mapper接口
 *
 * @author Framework
 * @since 1.0.0
 */
@Mapper
public interface ModelCategoryMapper {
    
    /**
     * 根据ID查询模型分类
     *
     * @param id 分类ID
     * @return 模型分类
     */
    ModelCategory selectById(@Param("id") Long id);
    
    /**
     * 查询模型分类列表
     *
     * @param params 查询参数
     * @return 模型分类列表
     */
    List<ModelCategory> selectList(Map<String, Object> params);
    
    /**
     * 查询所有启用的模型分类
     *
     * @return 模型分类列表
     */
    List<ModelCategory> selectAllEnabled();
    
    /**
     * 插入模型分类
     *
     * @param modelCategory 模型分类
     * @return 影响行数
     */
    int insert(ModelCategory modelCategory);
    
    /**
     * 更新模型分类
     *
     * @param modelCategory 模型分类
     * @return 影响行数
     */
    int update(ModelCategory modelCategory);
    
    /**
     * 删除模型分类
     *
     * @param id 分类ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
