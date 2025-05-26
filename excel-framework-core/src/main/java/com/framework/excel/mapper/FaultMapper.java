package com.framework.excel.mapper;

import com.framework.excel.entity.Fault;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 故障信息Mapper接口
 *
 * @author Framework
 * @since 1.0.0
 */
@Mapper
public interface FaultMapper {
    
    /**
     * 根据ID查询故障信息
     *
     * @param id 故障ID
     * @return 故障信息
     */
    Fault selectById(@Param("id") Long id);
    
    /**
     * 根据编码查询故障信息
     *
     * @param code 故障编码
     * @return 故障信息
     */
    Fault selectByCode(@Param("code") String code);
    
    /**
     * 查询故障列表
     *
     * @param params 查询参数
     * @return 故障列表
     */
    List<Fault> selectList(Map<String, Object> params);
    
    /**
     * 插入故障信息
     *
     * @param fault 故障信息
     * @return 影响行数
     */
    int insert(Fault fault);
    
    /**
     * 批量插入故障信息
     *
     * @param faults 故障信息列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<Fault> faults);
    
    /**
     * 更新故障信息
     *
     * @param fault 故障信息
     * @return 影响行数
     */
    int update(Fault fault);
    
    /**
     * 根据编码更新故障信息
     *
     * @param fault 故障信息
     * @return 影响行数
     */
    int updateByCode(Fault fault);
    
    /**
     * 删除故障信息
     *
     * @param id 故障ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量删除故障信息
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);
}
