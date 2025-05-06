package com.platform.visualization.infrastructure.persistence.repository;

import com.platform.visualization.infrastructure.persistence.DashboardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 仪表板JPA存储库
 */
@Repository
public interface DashboardJpaRepository extends JpaRepository<DashboardEntity, String> {
    
    /**
     * 查询公开的仪表板
     * 
     * @return 公开仪表板实体列表
     */
    List<DashboardEntity> findByIsPublicTrue();
    
    /**
     * 根据名称查找仪表板
     * 
     * @param name 仪表板名称
     * @return 仪表板实体列表
     */
    List<DashboardEntity> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根据主题查找仪表板
     * 
     * @param theme 仪表板主题
     * @return 仪表板实体列表
     */
    List<DashboardEntity> findByTheme(String theme);
}