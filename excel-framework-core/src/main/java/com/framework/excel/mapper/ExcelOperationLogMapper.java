package com.framework.excel.mapper;

import com.framework.excel.entity.ExcelOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Excel操作日志Mapper接口
 *
 * @author Framework
 * @since 1.0.0
 */
@Mapper
public interface ExcelOperationLogMapper {
    
    /**
     * 根据ID查询操作日志
     *
     * @param id 日志ID
     * @return 操作日志
     */
    ExcelOperationLog selectById(@Param("id") Long id);
    
    /**
     * 查询操作日志列表
     *
     * @param params 查询参数
     * @return 操作日志列表
     */
    List<ExcelOperationLog> selectList(Map<String, Object> params);
    
    /**
     * 插入操作日志
     *
     * @param excelOperationLog 操作日志
     * @return 影响行数
     */
    int insert(ExcelOperationLog excelOperationLog);
    
    /**
     * 更新操作日志
     *
     * @param excelOperationLog 操作日志
     * @return 影响行数
     */
    int update(ExcelOperationLog excelOperationLog);
    
    /**
     * 删除操作日志
     *
     * @param id 日志ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量删除操作日志
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);
}
