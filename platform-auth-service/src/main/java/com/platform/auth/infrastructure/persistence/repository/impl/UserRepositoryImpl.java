package com.platform.auth.infrastructure.persistence.repository.impl;

import com.platform.auth.domain.model.User;
import com.platform.auth.domain.repository.UserRepository;
import com.platform.auth.infrastructure.persistence.entity.UserEntity;
import com.platform.auth.infrastructure.persistence.mapper.UserMapper;
import com.platform.auth.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储实现类
 * <p>
 * 实现领域仓储接口，作为领域层和基础设施层的适配器
 * </p>
 */
@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepository {
    
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;
    
    public UserRepositoryImpl(JpaUserRepository jpaUserRepository, UserMapper userMapper) {
        this.jpaUserRepository = jpaUserRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        entity = jpaUserRepository.save(entity);
        return userMapper.toDomain(entity);
    }

    @Override
    public Optional<User> findById(String id) {
        return jpaUserRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByMobile(String mobile) {
        return jpaUserRepository.findByMobile(mobile)
                .map(userMapper::toDomain);
    }

    @Override
    public List<User> findByTenantId(String tenantId) {
        List<UserEntity> entities = jpaUserRepository.findByTenantId(tenantId);
        return userMapper.toDomainList(entities);
    }

    @Override
    public List<User> findAll(int page, int size) {
        // 注意：JPA分页参数从0开始，而我们的接口从1开始
        PageRequest pageRequest = PageRequest.of(Math.max(0, page - 1), size);
        return userMapper.toDomainList(jpaUserRepository.findAll(pageRequest).getContent());
    }

    @Override
    public long count() {
        return jpaUserRepository.count();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        jpaUserRepository.softDelete(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByMobile(String mobile) {
        return jpaUserRepository.existsByMobile(mobile);
    }
}
