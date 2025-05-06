package com.platform.report.infrastructure.service;

import com.platform.report.domain.model.report.ReportContent;
import com.platform.report.domain.model.template.ReportTemplate;
import com.platform.report.infrastructure.exception.ReportGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * 基于JasperReports的报表引擎服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JasperReportEngineService {
    
    /**
     * 根据模板和数据生成PDF报表
     *
     * @param template  报表模板
     * @param data      报表数据
     * @param parameters 报表参数
     * @return 报表内容
     */
    public ReportContent generatePdfReport(ReportTemplate template, Collection<?> data, Map<String, Object> parameters) {
        try {
            // 创建JasperDesign（在实际应用中可能会从模板JSON或XML生成）
            // 这里简化处理，假设已经有了编译好的JasperReport
            JasperReport jasperReport = compileReportFromTemplate(template);
            
            // 创建数据源
            JRDataSource dataSource = new JRBeanCollectionDataSource(data);
            
            // 填充报表
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // 导出为PDF
            return exportToPdf(jasperPrint, template.getName());
            
        } catch (Exception e) {
            log.error("Failed to generate PDF report", e);
            throw new ReportGenerationException("Failed to generate PDF report: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据模板和数据生成Excel报表
     *
     * @param template  报表模板
     * @param data      报表数据
     * @param parameters 报表参数
     * @return 报表内容
     */
    public ReportContent generateExcelReport(ReportTemplate template, Collection<?> data, Map<String, Object> parameters) {
        try {
            // 创建JasperDesign（在实际应用中可能会从模板JSON或XML生成）
            JasperReport jasperReport = compileReportFromTemplate(template);
            
            // 创建数据源
            JRDataSource dataSource = new JRBeanCollectionDataSource(data);
            
            // 填充报表
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // 导出为Excel
            return exportToExcel(jasperPrint, template.getName());
            
        } catch (Exception e) {
            log.error("Failed to generate Excel report", e);
            throw new ReportGenerationException("Failed to generate Excel report: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据模板编译JasperReport
     * 这里是示例实现，实际应用中需要根据模板定义生成JasperDesign
     */
    private JasperReport compileReportFromTemplate(ReportTemplate template) {
        // 此处应该有将模板定义转换为JasperDesign的逻辑
        // 简化处理，返回null，实际应用需要实现
        return null;
    }
    
    /**
     * 导出为PDF
     */
    private ReportContent exportToPdf(JasperPrint jasperPrint, String reportName) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setCreatingBatchModeBookmarks(true);
        exporter.setConfiguration(configuration);
        
        exporter.exportReport();
        
        byte[] reportBytes = outputStream.toByteArray();
        String fileName = reportName + ".pdf";
        
        return new ReportContent(reportBytes, "application/pdf", reportBytes.length, fileName);
    }
    
    /**
     * 导出为Excel
     */
    private ReportContent exportToExcel(JasperPrint jasperPrint, String reportName) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        
        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setDetectCellType(true);
        exporter.setConfiguration(configuration);
        
        exporter.exportReport();
        
        byte[] reportBytes = outputStream.toByteArray();
        String fileName = reportName + ".xlsx";
        
        return new ReportContent(reportBytes, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 
                reportBytes.length, fileName);
    }
}
