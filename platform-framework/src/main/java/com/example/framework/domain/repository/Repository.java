package com.example.framework.domain.repository;

import com.example.framework.domain.BaseEntity;

import java.util.Optional;

/**
 * 通用仓储接口
 * 领域层定义仓储接口，供基础设施层实现
 *
 * @author platform
 * @since 1.0.0
 */
public interface Repository {

    /**
     * 保存实体
     *
     * @param entity 要保存的实体
     * @param <T>    实体类型
     * @return 保存后的实体
     */
    <T extends BaseEntity> T save(T entity);

    /**
     * 根据ID查找实体
     *
     * @param entityClass 实体类
     * @param id          实体ID
     * @param <T>         实体类型
     * @return 实体Optional
     */
    <T extends BaseEntity> Optional<T> findById(Class<T> entityClass, Object id);

    /**
     * 删除实体
     *
     * @param entity 要删除的实体
     * @param <T>    实体类型
     */
    <T extends BaseEntity> void delete(T entity);

    /**
     * 根据ID删除实体
     *
     * @param entityClass 实体类
     * @param id          实体ID
     * @param <T>         实体类型
     */
    <T extends BaseEntity> void deleteById(Class<T> entityClass, Object id);
}