package com.framework.excel.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 动态SQL Mapper接口
 * 
 * @author Framework Team
 * @since 1.0.0
 */
@Mapper
public interface DynamicMapper {
    
    /**
     * 动态插入数据
     */
    int insert(@Param("tableName") String tableName, 
               @Param("data") Map<String, Object> data);
    
    /**
     * 批量插入数据
     */
    int insertBatch(@Param("tableName") String tableName, 
                    @Param("list") List<Map<String, Object>> dataList);
    
    /**
     * 动态更新数据
     */
    int update(@Param("tableName") String tableName, 
               @Param("data") Map<String, Object> data,
               @Param("conditions") Map<String, Object> conditions);
    
    /**
     * 动态删除数据
     */
    int delete(@Param("tableName") String tableName, 
               @Param("conditions") Map<String, Object> conditions);
    
    /**
     * 动态查询数据
     */
    List<Map<String, Object>> select(@Param("tableName") String tableName,
                                    @Param("conditions") Map<String, Object> conditions);
    
    /**
     * 动态查询数据（带字段选择和排序）
     */
    List<Map<String, Object>> selectByConditions(@Param("tableName") String tableName,
                                                @Param("conditions") Map<String, Object> conditions,
                                                @Param("selectFields") List<String> selectFields,
                                                @Param("orderBy") String orderBy,
                                                @Param("limit") Integer limit);
    
    /**
     * 统计记录数
     */
    int countByConditions(@Param("tableName") String tableName,
                         @Param("conditions") Map<String, Object> conditions);
    
    /**
     * 检查记录是否存在
     */
    boolean exists(@Param("tableName") String tableName,
                  @Param("conditions") Map<String, Object> conditions);
}
