package com.platform.auth.infrastructure.persistence.repository.impl;

import com.platform.auth.domain.model.Tenant;
import com.platform.auth.domain.repository.TenantRepository;
import com.platform.auth.infrastructure.persistence.entity.TenantEntity;
import com.platform.auth.infrastructure.persistence.mapper.TenantMapper;
import com.platform.auth.infrastructure.persistence.repository.JpaTenantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 租户仓储实现类
 * <p>
 * 实现领域仓储接口，作为领域层和基础设施层的适配器
 * </p>
 */
@Repository
@Transactional(readOnly = true)
public class TenantRepositoryImpl implements TenantRepository {
    
    private final JpaTenantRepository jpaTenantRepository;
    private final TenantMapper tenantMapper;
    
    public TenantRepositoryImpl(JpaTenantRepository jpaTenantRepository, TenantMapper tenantMapper) {
        this.jpaTenantRepository = jpaTenantRepository;
        this.tenantMapper = tenantMapper;
    }

    @Override
    @Transactional
    public Tenant save(Tenant tenant) {
        TenantEntity entity = tenantMapper.toEntity(tenant);
        entity = jpaTenantRepository.save(entity);
        return tenantMapper.toDomain(entity);
    }

    @Override
    public Optional<Tenant> findById(String id) {
        return jpaTenantRepository.findById(id)
                .map(tenantMapper::toDomain);
    }

    @Override
    public Optional<Tenant> findByCode(String code) {
        return jpaTenantRepository.findByCode(code)
                .map(tenantMapper::toDomain);
    }

    @Override
    public Optional<Tenant> findByName(String name) {
        return jpaTenantRepository.findByName(name)
                .map(tenantMapper::toDomain);
    }

    @Override
    public List<Tenant> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(Math.max(0, page - 1), size);
        return tenantMapper.toDomainList(jpaTenantRepository.findAll(pageRequest).getContent());
    }

    @Override
    public long count() {
        return jpaTenantRepository.count();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        jpaTenantRepository.softDelete(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaTenantRepository.existsByCode(code);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaTenantRepository.existsByName(name);
    }

    @Override
    public List<Tenant> findAllEnabled() {
        return tenantMapper.toDomainList(jpaTenantRepository.findAllEnabled(LocalDateTime.now()));
    }

    @Override
    public List<Tenant> findByStatus(Integer status) {
        return tenantMapper.toDomainList(jpaTenantRepository.findByStatus(status));
    }

    @Override
    @Transactional
    public void updateUserCount(String id, Integer count) {
        jpaTenantRepository.updateUserCount(id, count);
    }
}
