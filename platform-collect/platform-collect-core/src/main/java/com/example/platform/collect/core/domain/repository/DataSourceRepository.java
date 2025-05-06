package com.example.platform.collect.core.domain.repository;

import com.example.platform.collect.core.domain.model.DataSource;
import com.example.platform.collect.core.domain.model.DataSourceType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 数据源仓储接口
 * 定义数据源的持久化操作
 */
public interface DataSourceRepository {
    
    /**
     * 保存数据源
     *
     * @param dataSource 数据源
     * @return 保存后的数据源
     */
    DataSource save(DataSource dataSource);
    
    /**
     * 根据ID查找数据源
     *
     * @param id 数据源ID
     * @return 数据源，如果不存在则返回空
     */
    Optional<DataSource> findById(String id);
    
    /**
     * 根据名称查找数据源
     *
     * @param name 数据源名称
     * @return 数据源，如果不存在则返回空
     */
    Optional<DataSource> findByName(String name);
    
    /**
     * 查找所有数据源
     *
     * @return 数据源列表
     */
    List<DataSource> findAll();
    
    /**
     * 根据条件查询数据源
     *
     * @param criteria 查询条件
     * @return 数据源列表
     */
    List<DataSource> findByCriteria(Map<String, Object> criteria);
    
    /**
     * 根据类型查找数据源
     *
     * @param type 数据源类型
     * @return 数据源列表
     */
    List<DataSource> findByType(DataSourceType type);
    
    /**
     * 根据标签查找数据源
     *
     * @param tagKey 标签键
     * @param tagValue 标签值
     * @return 数据源列表
     */
    List<DataSource> findByTag(String tagKey, String tagValue);
    
    /**
     * 删除数据源
     *
     * @param id 数据源ID
     */
    void deleteById(String id);
    
    /**
     * 检查数据源名称是否已存在
     *
     * @param name 数据源名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 获取数据源计数
     *
     * @return 数据源总数
     */
    long count();
    
    /**
     * 根据条件获取数据源计数
     *
     * @param criteria 查询条件
     * @return 符合条件的数据源数量
     */
    long countByCriteria(Map<String, Object> criteria);
}
