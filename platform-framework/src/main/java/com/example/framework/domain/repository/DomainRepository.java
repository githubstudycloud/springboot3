package com.example.framework.domain.repository;

import com.example.framework.core.BaseEntity;

import java.util.Optional;

/**
 * 通用领域仓储接口
 * 领域层定义仓储接口，供基础设施层实现
 *
 * @author platform
 * @since 1.0.0
 */
public interface DomainRepository<T extends BaseEntity<ID>, ID> {

    /**
     * 保存实体
     *
     * @param entity 要保存的实体
     * @return 保存后的实体
     */
    T save(T entity);

    /**
     * 根据ID查找实体
     *
     * @param id 实体ID
     * @return 实体Optional
     */
    Optional<T> findById(ID id);

    /**
     * 删除实体
     *
     * @param entity 要删除的实体
     */
    void delete(T entity);

    /**
     * 根据ID删除实体
     *
     * @param id 实体ID
     */
    void deleteById(ID id);
}