package com.example.framework.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 基础服务实现类
 * 提供通用的服务层实现
 *
 * @param <T>  实体类型
 * @param <R>  仓储类型
 * @param <ID> 实体ID类型
 */
public abstract class BaseServiceImpl<T extends BaseEntity<ID>, R extends BaseRepository<T, ID>, ID> implements BaseService<T, ID> {

    @Autowired
    protected R repository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public T save(T entity) {
        // 执行保存前的操作
        beforeSave(entity);

        // 保存实体
        T savedEntity = repository.save(entity);

        // 执行保存后的操作
        afterSave(savedEntity);

        return savedEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<T> saveAll(Iterable<T> entities) {
        List<T> result = new ArrayList<>();

        for (T entity : entities) {
            // 执行保存前的操作
            beforeSave(entity);
        }

        // 批量保存实体
        List<T> savedEntities = repository.saveAll(entities);

        for (T savedEntity : savedEntities) {
            // 执行保存后的操作
            afterSave(savedEntity);
            result.add(savedEntity);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(ID id) {
        // 检查实体是否存在
        if (existsById(id)) {
            // 执行删除前的操作
            beforeDelete(id);

            // 删除实体
            repository.deleteById(id);

            // 执行删除后的操作
            afterDelete(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(T entity) {
        // 执行删除前的操作
        beforeDelete(entity);

        // 删除实体
        repository.delete(entity);

        // 执行删除后的操作
        afterDelete(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(Iterable<T> entities) {
        for (T entity : entities) {
            // 执行删除前的操作
            beforeDelete(entity);
        }

        // 批量删除实体
        repository.deleteAll(entities);

        for (T entity : entities) {
            // 执行删除后的操作
            afterDelete(entity);
        }
    }

    /**
     * 保存实体前的操作
     *
     * @param entity 待保存的实体
     */
    protected void beforeSave(T entity) {
        // 默认实现，可由子类重写
    }

    /**
     * 保存实体后的操作
     *
     * @param entity 已保存的实体
     */
    protected void afterSave(T entity) {
        // 默认实现，可由子类重写
    }

    /**
     * 删除实体前的操作
     *
     * @param id 待删除的实体ID
     */
    protected void beforeDelete(ID id) {
        // 默认实现，可由子类重写
    }

    /**
     * 删除实体后的操作
     *
     * @param id 已删除的实体ID
     */
    protected void afterDelete(ID id) {
        // 默认实现，可由子类重写
    }

    /**
     * 删除实体前的操作
     *
     * @param entity 待删除的实体
     */
    protected void beforeDelete(T entity) {
        // 默认实现，可由子类重写
        beforeDelete(entity.getId());
    }

    /**
     * 删除实体后的操作
     *
     * @param entity 已删除的实体
     */
    protected void afterDelete(T entity) {
        // 默认实现，可由子类重写
        afterDelete(entity.getId());
    }
} 