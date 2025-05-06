package com.platform.auth.infrastructure.persistence.repository.impl;

import com.platform.auth.domain.model.Role;
import com.platform.auth.domain.repository.RoleRepository;
import com.platform.auth.infrastructure.persistence.entity.RoleEntity;
import com.platform.auth.infrastructure.persistence.mapper.RoleMapper;
import com.platform.auth.infrastructure.persistence.repository.JpaRoleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 角色仓储实现类
 * <p>
 * 实现领域仓储接口，作为领域层和基础设施层的适配器
 * </p>
 */
@Repository
@Transactional(readOnly = true)
public class RoleRepositoryImpl implements RoleRepository {
    
    private final JpaRoleRepository jpaRoleRepository;
    private final RoleMapper roleMapper;
    
    public RoleRepositoryImpl(JpaRoleRepository jpaRoleRepository, RoleMapper roleMapper) {
        this.jpaRoleRepository = jpaRoleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public Role save(Role role) {
        RoleEntity entity = roleMapper.toEntity(role);
        entity = jpaRoleRepository.save(entity);
        return roleMapper.toDomain(entity);
    }

    @Override
    public Optional<Role> findById(String id) {
        return jpaRoleRepository.findById(id)
                .map(roleMapper::toDomain);
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return jpaRoleRepository.findByCode(code)
                .map(roleMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRoleRepository.findByName(name)
                .map(roleMapper::toDomain);
    }

    @Override
    public List<Role> findByTenantId(String tenantId) {
        List<RoleEntity> entities = jpaRoleRepository.findByTenantId(tenantId);
        return roleMapper.toDomainList(entities);
    }

    @Override
    public List<Role> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(Math.max(0, page - 1), size);
        return roleMapper.toDomainList(jpaRoleRepository.findAll(pageRequest).getContent());
    }

    @Override
    public long count() {
        return jpaRoleRepository.count();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        jpaRoleRepository.softDelete(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRoleRepository.existsByCode(code);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRoleRepository.existsByName(name);
    }

    @Override
    public Set<Role> findByIdIn(Set<String> ids) {
        return roleMapper.toDomainSet(jpaRoleRepository.findByIdIn(ids));
    }
}
