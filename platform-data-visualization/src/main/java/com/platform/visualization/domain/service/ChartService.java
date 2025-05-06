package com.platform.visualization.domain.service;

import com.platform.visualization.domain.model.chart.Chart;
import com.platform.visualization.domain.repository.ChartRepository;

import java.util.Map;

/**
 * 图表领域服务
 */
public class ChartService {
    private final ChartRepository chartRepository;

    public ChartService(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
    }

    /**
     * 更新图表配置
     * 
     * @param chartId 图表ID
     * @param options 新配置选项
     * @return 更新后的图表
     */
    public Chart updateChartOptions(Chart.ChartId chartId, Map<String, String> options) {
        // 领域逻辑
        Chart chart = chartRepository.findById(chartId)
                .orElseThrow(() -> new IllegalArgumentException("图表不存在"));
        chart.updateOptions(options);
        if (!chart.validate()) {
            throw new IllegalArgumentException("图表配置无效");
        }
        return chartRepository.save(chart);
    }
}