package com.framework.excel.mapper;

import com.framework.excel.entity.Model;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 模型信息Mapper接口
 *
 * @author Framework
 * @since 1.0.0
 */
@Mapper
public interface ModelMapper {
    
    /**
     * 根据ID查询模型信息
     *
     * @param id 模型ID
     * @return 模型信息
     */
    Model selectById(@Param("id") Long id);
    
    /**
     * 根据编码查询模型信息
     *
     * @param code 模型编码
     * @return 模型信息
     */
    Model selectByCode(@Param("code") String code);
    
    /**
     * 查询模型列表
     *
     * @param params 查询参数
     * @return 模型列表
     */
    List<Model> selectList(Map<String, Object> params);
    
    /**
     * 插入模型信息
     *
     * @param model 模型信息
     * @return 影响行数
     */
    int insert(Model model);
    
    /**
     * 批量插入模型信息
     *
     * @param models 模型信息列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<Model> models);
    
    /**
     * 更新模型信息
     *
     * @param model 模型信息
     * @return 影响行数
     */
    int update(Model model);
    
    /**
     * 根据编码更新模型信息
     *
     * @param model 模型信息
     * @return 影响行数
     */
    int updateByCode(Model model);
    
    /**
     * 删除模型信息
     *
     * @param id 模型ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量删除模型信息
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);
}
