package com.example.framework.core;

import java.util.List;
import java.util.Optional;

/**
 * 基础服务接口
 * 定义领域服务层的标准操作
 *
 * @param <T>  实体类型
 * @param <ID> 实体ID类型
 */
public interface BaseService<T extends BaseEntity<ID>, ID> {

    /**
     * 保存实体
     *
     * @param entity 待保存的实体
     * @return 保存后的实体
     */
    T save(T entity);

    /**
     * 批量保存实体
     *
     * @param entities 待保存的实体集合
     * @return 保存后的实体集合
     */
    List<T> saveAll(Iterable<T> entities);

    /**
     * 根据ID查找实体
     *
     * @param id 实体ID
     * @return 可能存在的实体
     */
    Optional<T> findById(ID id);

    /**
     * 查找所有实体
     *
     * @return 所有实体
     */
    List<T> findAll();

    /**
     * 判断指定ID的实体是否存在
     *
     * @param id 实体ID
     * @return 存在返回true，否则返回false
     */
    boolean existsById(ID id);

    /**
     * 根据ID删除实体
     *
     * @param id 实体ID
     */
    void deleteById(ID id);

    /**
     * 删除指定实体
     *
     * @param entity 待删除的实体
     */
    void delete(T entity);

    /**
     * 批量删除实体
     *
     * @param entities 待删除的实体集合
     */
    void deleteAll(Iterable<T> entities);
} 