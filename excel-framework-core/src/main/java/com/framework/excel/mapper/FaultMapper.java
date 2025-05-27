package com.framework.excel.mapper;

import com.framework.excel.entity.Fault;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 故障Mapper接口
 *
 * @author framework
 * @since 1.0.0
 */
@Mapper
public interface FaultMapper {

    /**
     * 根据ID查询故障
     *
     * @param id 故障ID
     * @return 故障信息
     */
    Fault selectById(@Param("id") Long id);

    /**
     * 根据编码查询故障
     *
     * @param code 故障编码
     * @return 故障信息
     */
    Fault selectByCode(@Param("code") String code);

    /**
     * 根据名称查询故障
     *
     * @param name 故障名称
     * @return 故障信息
     */
    Fault selectByName(@Param("name") String name);

    /**
     * 根据条件查询故障列表
     *
     * @param conditions 查询条件
     * @return 故障列表
     */
    List<Fault> selectByConditions(@Param("conditions") Map<String, Object> conditions);

    /**
     * 查询所有故障
     *
     * @return 故障列表
     */
    List<Fault> selectAll();

    /**
     * 插入故障
     *
     * @param fault 故障信息
     * @return 影响行数
     */
    int insert(Fault fault);

    /**
     * 批量插入故障
     *
     * @param faults 故障列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<Fault> faults);

    /**
     * 更新故障
     *
     * @param fault 故障信息
     * @return 影响行数
     */
    int update(Fault fault);

    /**
     * 根据编码更新故障
     *
     * @param fault 故障信息
     * @return 影响行数
     */
    int updateByCode(Fault fault);

    /**
     * 根据名称更新故障
     *
     * @param fault 故障信息
     * @return 影响行数
     */
    int updateByName(Fault fault);

    /**
     * 删除故障
     *
     * @param id 故障ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据编码删除故障
     *
     * @param code 故障编码
     * @return 影响行数
     */
    int deleteByCode(@Param("code") String code);

    /**
     * 统计故障数量
     *
     * @param conditions 查询条件
     * @return 故障数量
     */
    int countByConditions(@Param("conditions") Map<String, Object> conditions);
}