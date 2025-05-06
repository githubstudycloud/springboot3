package com.platform.visualization.infrastructure.repository;

import com.platform.visualization.domain.model.chart.Chart;
import com.platform.visualization.domain.model.dashboard.Dashboard;
import com.platform.visualization.domain.model.dashboard.DashboardItem;
import com.platform.visualization.domain.model.dashboard.Layout;
import com.platform.visualization.domain.model.dashboard.Position;
import com.platform.visualization.domain.model.dashboard.Theme;
import com.platform.visualization.domain.repository.DashboardRepository;
import com.platform.visualization.infrastructure.persistence.ChartEntity;
import com.platform.visualization.infrastructure.persistence.DashboardEntity;
import com.platform.visualization.infrastructure.persistence.DashboardItemEntity;
import com.platform.visualization.infrastructure.persistence.repository.ChartJpaRepository;
import com.platform.visualization.infrastructure.persistence.repository.DashboardJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表板仓储实现
 */
@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

    private final DashboardJpaRepository dashboardJpaRepository;
    private final ChartJpaRepository chartJpaRepository;
    private final ChartRepositoryImpl chartRepository;

    public DashboardRepositoryImpl(DashboardJpaRepository dashboardJpaRepository,
                                ChartJpaRepository chartJpaRepository,
                                ChartRepositoryImpl chartRepository) {
        this.dashboardJpaRepository = dashboardJpaRepository;
        this.chartJpaRepository = chartJpaRepository;
        this.chartRepository = chartRepository;
    }

    @Override
    public Dashboard save(Dashboard dashboard) {
        // 将领域模型转换为实体
        DashboardEntity entity = toEntity(dashboard);
        // 保存实体
        DashboardEntity savedEntity = dashboardJpaRepository.save(entity);
        // 将保存后的实体转换回领域模型
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Dashboard> findById(Dashboard.DashboardId id) {
        return dashboardJpaRepository.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<Dashboard> findAll() {
        return dashboardJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dashboard> findPublic() {
        return dashboardJpaRepository.findByIsPublicTrue().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Dashboard.DashboardId id) {
        dashboardJpaRepository.deleteById(id.getValue());
    }

    /**
     * 将实体转换为领域模型
     * 
     * @param entity 仪表板实体
     * @return 仪表板领域模型
     */
    private Dashboard toDomain(DashboardEntity entity) {
        if (entity == null) {
            return null;
        }

        Dashboard dashboard = new Dashboard();
        dashboard.setId(new Dashboard.DashboardId(entity.getId()));
        dashboard.setName(entity.getName());
        dashboard.setDescription(entity.getDescription());
        
        // 布局设置
        if (entity.getLayoutType() != null) {
            Layout layout = new Layout();
            layout.setType(Layout.LayoutType.valueOf(entity.getLayoutType()));
            layout.setColumns(entity.getColumns());
            layout.setRowHeight(entity.getRowHeight());
            layout.setMargin(entity.getMargin());
            dashboard.setLayout(layout);
        }
        
        if (entity.getTheme() != null) {
            dashboard.setTheme(Theme.valueOf(entity.getTheme()));
        }
        
        dashboard.setPublic(entity.isPublic());
        
        // 仪表板项
        if (entity.getItems() != null && !entity.getItems().isEmpty()) {
            List<DashboardItem> items = new ArrayList<>();
            
            for (DashboardItemEntity itemEntity : entity.getItems()) {
                if (itemEntity.getChart() != null) {
                    Chart chart = chartRepository.findById(
                            new Chart.ChartId(itemEntity.getChart().getId())).orElse(null);
                    
                    if (chart != null) {
                        Position position = new Position(
                                itemEntity.getX(),
                                itemEntity.getY(),
                                itemEntity.getWidth(),
                                itemEntity.getHeight()
                        );
                        
                        DashboardItem item = new DashboardItem(chart, position);
                        // 手动设置ID
                        items.add(item);
                    }
                }
            }
            
            dashboard.setItems(items);
        }
        
        return dashboard;
    }

    /**
     * 将领域模型转换为实体
     * 
     * @param dashboard 仪表板领域模型
     * @return 仪表板实体
     */
    private DashboardEntity toEntity(Dashboard dashboard) {
        if (dashboard == null) {
            return null;
        }

        DashboardEntity entity = new DashboardEntity();
        entity.setId(dashboard.getId().getValue());
        entity.setName(dashboard.getName());
        entity.setDescription(dashboard.getDescription());
        
        // 布局设置
        if (dashboard.getLayout() != null) {
            entity.setLayoutType(dashboard.getLayout().getType().name());
            entity.setColumns(dashboard.getLayout().getColumns());
            entity.setRowHeight(dashboard.getLayout().getRowHeight());
            entity.setMargin(dashboard.getLayout().getMargin());
        }
        
        if (dashboard.getTheme() != null) {
            entity.setTheme(dashboard.getTheme().name());
        }
        
        entity.setPublic(dashboard.isPublic());
        
        // 仪表板项
        if (dashboard.getItems() != null && !dashboard.getItems().isEmpty()) {
            Set<DashboardItemEntity> itemEntities = new HashSet<>();
            
            for (DashboardItem item : dashboard.getItems()) {
                if (item.getChart() != null) {
                    ChartEntity chartEntity = chartJpaRepository
                            .findById(item.getChart().getId().getValue())
                            .orElseThrow(() -> new IllegalArgumentException("图表不存在"));
                    
                    DashboardItemEntity itemEntity = new DashboardItemEntity();
                    itemEntity.setId(item.getId());
                    itemEntity.setDashboard(entity);
                    itemEntity.setChart(chartEntity);
                    
                    if (item.getPosition() != null) {
                        itemEntity.setX(item.getPosition().getX());
                        itemEntity.setY(item.getPosition().getY());
                        itemEntity.setWidth(item.getPosition().getWidth());
                        itemEntity.setHeight(item.getPosition().getHeight());
                    }
                    
                    itemEntities.add(itemEntity);
                }
            }
            
            entity.setItems(itemEntities);
        }
        
        return entity;
    }
}