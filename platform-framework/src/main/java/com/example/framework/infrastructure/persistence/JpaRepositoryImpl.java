package com.example.framework.infrastructure.persistence;

import com.example.framework.core.BaseEntity;
import com.example.framework.domain.repository.DomainRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

/**
 * JPA仓储实现
 * 基础设施层示例，实现领域层的仓储接口
 *
 * @author platform
 * @since 1.0.0
 */
@Component
public class JpaRepositoryImpl<T extends BaseEntity<ID>, ID> implements DomainRepository<T, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaRepositoryImpl.class);

    /**
     * JPA实体管理器
     */
    @PersistenceContext
    protected EntityManager entityManager;
    
    /**
     * 实体类Class
     */
    protected final Class<T> entityClass;
    
    /**
     * 构造函数
     */
    @SuppressWarnings("unchecked")
    public JpaRepositoryImpl() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    @Override
    public T save(final T entity) {
        LOGGER.info("保存实体: {}", entity);
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    @Override
    public Optional<T> findById(final ID id) {
        LOGGER.info("根据ID查找实体: {}, 实体类: {}", id, entityClass.getSimpleName());
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public void delete(final T entity) {
        LOGGER.info("删除实体: {}", entity);
        entityManager.remove(entity);
    }

    @Override
    public void deleteById(final ID id) {
        LOGGER.info("根据ID删除实体: {}, 实体类: {}", id, entityClass.getSimpleName());
        findById(id).ifPresent(this::delete);
    }
}