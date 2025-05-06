package com.platform.visualization.application.service;

import com.platform.visualization.application.assembler.ChartAssembler;
import com.platform.visualization.application.dto.ChartDTO;
import com.platform.visualization.domain.model.chart.Chart;
import com.platform.visualization.domain.model.dataset.DataSet;
import com.platform.visualization.domain.repository.ChartRepository;
import com.platform.visualization.domain.repository.DataSetRepository;
import com.platform.visualization.domain.service.ChartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 图表应用服务
 */
@Service
public class ChartApplicationService {
    
    private final ChartRepository chartRepository;
    private final DataSetRepository dataSetRepository;
    private final ChartService chartService;
    
    public ChartApplicationService(ChartRepository chartRepository,
                                 DataSetRepository dataSetRepository,
                                 ChartService chartService) {
        this.chartRepository = chartRepository;
        this.dataSetRepository = dataSetRepository;
        this.chartService = chartService;
    }
    
    /**
     * 获取所有图表
     * 
     * @return 图表DTO列表
     */
    @Transactional(readOnly = true)
    public List<ChartDTO> findAll() {
        List<Chart> charts = chartRepository.findAll();
        return ChartAssembler.toDTOList(charts);
    }
    
    /**
     * 根据ID查找图表
     * 
     * @param id 图表ID
     * @return 图表DTO
     */
    @Transactional(readOnly = true)
    public ChartDTO findById(String id) {
        Chart chart = chartRepository.findById(new Chart.ChartId(id))
                .orElseThrow(() -> new IllegalArgumentException("图表不存在"));
        return ChartAssembler.toDTO(chart);
    }
    
    /**
     * 根据数据集查找图表
     * 
     * @param dataSetId 数据集ID
     * @return 图表DTO列表
     */
    @Transactional(readOnly = true)
    public List<ChartDTO> findByDataSet(String dataSetId) {
        List<Chart> charts = chartRepository.findByDataSet(
                new DataSet.DataSetId(dataSetId));
        return ChartAssembler.toDTOList(charts);
    }
    
    /**
     * 创建图表
     * 
     * @param chartDTO 图表DTO
     * @return 创建后的图表DTO
     */
    @Transactional
    public ChartDTO create(ChartDTO chartDTO) {
        // 获取关联的数据集
        DataSet dataSet = dataSetRepository.findById(
                new DataSet.DataSetId(chartDTO.getDataSetId()))
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));
        
        // 将DTO转换为领域模型
        Chart chart = ChartAssembler.toDomain(chartDTO, dataSet);
        
        // 验证图表配置
        if (!chart.validate()) {
            throw new IllegalArgumentException("图表配置无效");
        }
        
        // 保存图表
        Chart savedChart = chartRepository.save(chart);
        
        // 将结果转换回DTO
        return ChartAssembler.toDTO(savedChart);
    }
    
    /**
     * 更新图表
     * 
     * @param id 图表ID
     * @param chartDTO 图表DTO
     * @return 更新后的图表DTO
     */
    @Transactional
    public ChartDTO update(String id, ChartDTO chartDTO) {
        // 查找现有图表
        Chart existingChart = chartRepository.findById(new Chart.ChartId(id))
                .orElseThrow(() -> new IllegalArgumentException("图表不存在"));
        
        // 获取关联的数据集
        DataSet dataSet = dataSetRepository.findById(
                new DataSet.DataSetId(chartDTO.getDataSetId()))
                .orElseThrow(() -> new IllegalArgumentException("数据集不存在"));
        
        // 将DTO转换为领域模型（保留原ID）
        Chart updatedChart = ChartAssembler.toDomain(chartDTO, dataSet);
        updatedChart.setId(existingChart.getId());
        
        // 验证图表配置
        if (!updatedChart.validate()) {
            throw new IllegalArgumentException("图表配置无效");
        }
        
        // 保存更新
        Chart savedChart = chartRepository.save(updatedChart);
        
        // 转换回DTO
        return ChartAssembler.toDTO(savedChart);
    }
    
    /**
     * 删除图表
     * 
     * @param id 图表ID
     */
    @Transactional
    public void delete(String id) {
        chartRepository.delete(new Chart.ChartId(id));
    }
    
    /**
     * 更新图表配置选项
     * 
     * @param id 图表ID
     * @param options 配置选项
     * @return 更新后的图表DTO
     */
    @Transactional
    public ChartDTO updateOptions(String id, Map<String, String> options) {
        // 更新配置选项
        Chart updatedChart = chartService.updateChartOptions(
                new Chart.ChartId(id), options);
        
        // 转换回DTO
        return ChartAssembler.toDTO(updatedChart);
    }
}