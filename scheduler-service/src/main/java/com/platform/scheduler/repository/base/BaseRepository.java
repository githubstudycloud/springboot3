package com.platform.scheduler.repository.base;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 基础Repository接口
 * 
 * @author platform
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public interface BaseRepository<T, ID extends Serializable> {
    
    /**
     * 保存实体
     * 
     * @param entity 实体
     * @return 保存后的实体
     */
    T save(T entity);
    
    /**
     * 批量保存实体
     * 
     * @param entities 实体列表
     * @return 保存后的实体列表
     */
    List<T> saveAll(Iterable<T> entities);
    
    /**
     * 根据ID查询实体
     * 
     * @param id 主键ID
     * @return 实体Optional包装
     */
    Optional<T> findById(ID id);
    
    /**
     * 根据ID判断实体是否存在
     * 
     * @param id 主键ID
     * @return 是否存在
     */
    boolean existsById(ID id);
    
    /**
     * 查询所有实体
     * 
     * @return 实体列表
     */
    List<T> findAll();
    
    /**
     * 分页查询所有实体
     * 
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<T> findAll(Pageable pageable);
    
    /**
     * 根据ID列表查询实体
     * 
     * @param ids ID列表
     * @return 实体列表
     */
    List<T> findAllById(Iterable<ID> ids);
    
    /**
     * 获取实体总数
     * 
     * @return 实体总数
     */
    long count();
    
    /**
     * 根据ID删除实体
     * 
     * @param id 主键ID
     */
    void deleteById(ID id);
    
    /**
     * 删除实体
     * 
     * @param entity 实体
     */
    void delete(T entity);
    
    /**
     * 批量删除实体
     * 
     * @param entities 实体列表
     */
    void deleteAll(Iterable<T> entities);
    
    /**
     * 删除所有实体
     */
    void deleteAll();
}
