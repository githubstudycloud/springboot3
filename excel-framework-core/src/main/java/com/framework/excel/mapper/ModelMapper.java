package com.framework.excel.mapper;

import com.framework.excel.entity.Model;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 模型Mapper接口
 *
 * @author framework
 * @since 1.0.0
 */
@Mapper
public interface ModelMapper {

    /**
     * 根据ID查询模型
     *
     * @param id 模型ID
     * @return 模型信息
     */
    Model selectById(@Param("id") Long id);

    /**
     * 根据编码查询模型
     *
     * @param code 模型编码
     * @return 模型信息
     */
    Model selectByCode(@Param("code") String code);

    /**
     * 根据名称查询模型
     *
     * @param name 模型名称
     * @return 模型信息
     */
    Model selectByName(@Param("name") String name);

    /**
     * 根据条件查询模型列表
     *
     * @param conditions 查询条件
     * @return 模型列表
     */
    List<Model> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 查询所有模型
     *
     * @return 模型列表
     */
    List<Model> selectAll();

    /**
     * 插入模型
     *
     * @param model 模型信息
     * @return 影响行数
     */
    int insert(Model model);

    /**
     * 批量插入模型
     *
     * @param models 模型列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<Model> models);

    /**
     * 更新模型
     *
     * @param model 模型信息
     * @return 影响行数
     */
    int update(Model model);

    /**
     * 根据编码更新模型
     *
     * @param model 模型信息
     * @return 影响行数
     */
    int updateByCode(Model model);

    /**
     * 根据名称更新模型
     *
     * @param model 模型信息
     * @return 影响行数
     */
    int updateByName(Model model);

    /**
     * 删除模型
     *
     * @param id 模型ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据编码删除模型
     *
     * @param code 模型编码
     * @return 影响行数
     */
    int deleteByCode(@Param("code") String code);

    /**
     * 统计模型数量
     *
     * @param conditions 查询条件
     * @return 模型数量
     */
    int countByConditions(@Param("conditions") Map<String, Object> conditions);
}