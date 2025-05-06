package com.platform.auth.infrastructure.persistence.repository.impl;

import com.platform.auth.domain.model.Permission;
import com.platform.auth.domain.repository.PermissionRepository;
import com.platform.auth.infrastructure.persistence.entity.PermissionEntity;
import com.platform.auth.infrastructure.persistence.mapper.PermissionMapper;
import com.platform.auth.infrastructure.persistence.repository.JpaPermissionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 权限仓储实现类
 * <p>
 * 实现领域仓储接口，作为领域层和基础设施层的适配器
 * </p>
 */
@Repository
@Transactional(readOnly = true)
public class PermissionRepositoryImpl implements PermissionRepository {
    
    private final JpaPermissionRepository jpaPermissionRepository;
    private final PermissionMapper permissionMapper;
    
    public PermissionRepositoryImpl(JpaPermissionRepository jpaPermissionRepository, PermissionMapper permissionMapper) {
        this.jpaPermissionRepository = jpaPermissionRepository;
        this.permissionMapper = permissionMapper;
    }

    @Override
    @Transactional
    public Permission save(Permission permission) {
        PermissionEntity entity = permissionMapper.toEntity(permission);
        entity = jpaPermissionRepository.save(entity);
        return permissionMapper.toDomain(entity);
    }

    @Override
    public Optional<Permission> findById(String id) {
        return jpaPermissionRepository.findById(id)
                .map(permissionMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        return jpaPermissionRepository.findByCode(code)
                .map(permissionMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return jpaPermissionRepository.findByName(name)
                .map(permissionMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByUrlAndMethod(String url, String method) {
        return jpaPermissionRepository.findByUrlAndMethod(url, method)
                .map(permissionMapper::toDomain);
    }

    @Override
    public List<Permission> findByParentId(String parentId) {
        List<PermissionEntity> entities = jpaPermissionRepository.findByParentIdOrderBySortAsc(parentId);
        return permissionMapper.toDomainList(entities);
    }

    @Override
    public List<Permission> findByType(String type) {
        List<PermissionEntity> entities = jpaPermissionRepository.findByType(type);
        return permissionMapper.toDomainList(entities);
    }

    @Override
    public List<Permission> findByTenantId(String tenantId) {
        List<PermissionEntity> entities = jpaPermissionRepository.findByTenantId(tenantId);
        return permissionMapper.toDomainList(entities);
    }

    @Override
    public List<Permission> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(Math.max(0, page - 1), size);
        return permissionMapper.toDomainList(jpaPermissionRepository.findAll(pageRequest).getContent());
    }

    @Override
    public long count() {
        return jpaPermissionRepository.count();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        jpaPermissionRepository.softDelete(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaPermissionRepository.existsByCode(code);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaPermissionRepository.existsByName(name);
    }

    @Override
    public Set<Permission> findByIdIn(Set<String> ids) {
        return permissionMapper.toDomainSet(jpaPermissionRepository.findByIdIn(ids));
    }
}
