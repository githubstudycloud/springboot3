package com.example.platform.collect.core.infrastructure.adapter;

import java.util.List;
import java.util.Map;

/**
 * 数据治理适配器接口
 * 实现与平台数据治理系统的集成
 */
public interface DataGovernanceAdapter {
    
    /**
     * 应用数据清洗规则
     *
     * @param ruleSetId 规则集ID
     * @param data 待清洗数据
     * @return 清洗结果
     */
    CleansingResult applyCleansingRules(String ruleSetId, Object data);
    
    /**
     * 批量应用数据清洗规则
     *
     * @param ruleSetId 规则集ID
     * @param batch 数据批次
     * @return 清洗结果
     */
    BatchCleansingResult applyCleansingRulesBatch(String ruleSetId, List<Object> batch);
    
    /**
     * 应用数据质量规则进行验证
     *
     * @param ruleSetId 规则集ID
     * @param data 待验证数据
     * @return 验证结果
     */
    ValidationResult validateWithQualityRules(String ruleSetId, Object data);
    
    /**
     * 批量应用数据质量规则进行验证
     *
     * @param ruleSetId 规则集ID
     * @param batch 数据批次
     * @return 验证结果
     */
    BatchValidationResult validateWithQualityRulesBatch(String ruleSetId, List<Object> batch);
    
    /**
     * 注册采集元数据
     *
     * @param metadata 元数据信息
     * @return 注册结果
     */
    RegistrationResult registerMetadata(Map<String, Object> metadata);
    
    /**
     * 记录数据血缘关系
     *
     * @param sourceId 源数据标识
     * @param targetId 目标数据标识
     * @param transformationInfo 转换信息
     * @return 记录结果
     */
    LineageResult recordLineage(String sourceId, String targetId, Map<String, Object> transformationInfo);
    
    /**
     * 获取数据清洗规则集
     *
     * @param ruleSetId 规则集ID
     * @return 规则集
     */
    CleansingRuleSet getCleansingRuleSet(String ruleSetId);
    
    /**
     * 获取数据质量规则集
     *
     * @param ruleSetId 规则集ID
     * @return 规则集
     */
    QualityRuleSet getQualityRuleSet(String ruleSetId);
    
    /**
     * 创建数据质量检查任务
     *
     * @param datasetId 数据集ID
     * @param ruleSetId 规则集ID
     * @param config 检查配置
     * @return 创建结果
     */
    TaskResult createQualityCheckTask(String datasetId, String ruleSetId, Map<String, Object> config);
    
    /**
     * 获取数据质量检查结果
     *
     * @param taskId 任务ID
     * @return 检查结果
     */
    QualityCheckResult getQualityCheckResult(String taskId);
    
    /**
     * 数据清洗结果类
     */
    class CleansingResult {
        private final boolean success;
        private final String message;
        private final Object cleanedData;
        private final List<RuleApplication> appliedRules;
        
        public CleansingResult(boolean success, String message, 
                              Object cleanedData, List<RuleApplication> appliedRules) {
            this.success = success;
            this.message = message;
            this.cleanedData = cleanedData;
            this.appliedRules = appliedRules;
        }
        
        public static CleansingResult success(Object cleanedData, List<RuleApplication> appliedRules) {
            return new CleansingResult(true, "Data cleansing completed successfully", 
                                     cleanedData, appliedRules);
        }
        
        public static CleansingResult failure(String message) {
            return new CleansingResult(false, message, null, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Object getCleanedData() {
            return cleanedData;
        }
        
        public List<RuleApplication> getAppliedRules() {
            return appliedRules;
        }
    }
    
    /**
     * 批量数据清洗结果类
     */
    class BatchCleansingResult {
        private final boolean success;
        private final String message;
        private final List<Object> cleanedBatch;
        private final int totalCount;
        private final int successCount;
        private final int failedCount;
        private final Map<String, Integer> ruleApplicationCounts;
        
        public BatchCleansingResult(boolean success, String message, 
                                 List<Object> cleanedBatch, int totalCount, 
                                 int successCount, int failedCount, 
                                 Map<String, Integer> ruleApplicationCounts) {
            this.success = success;
            this.message = message;
            this.cleanedBatch = cleanedBatch;
            this.totalCount = totalCount;
            this.successCount = successCount;
            this.failedCount = failedCount;
            this.ruleApplicationCounts = ruleApplicationCounts;
        }
        
        public static BatchCleansingResult success(List<Object> cleanedBatch, 
                                               int totalCount, int successCount, 
                                               int failedCount, 
                                               Map<String, Integer> ruleApplicationCounts) {
            return new BatchCleansingResult(true, "Batch cleansing completed", 
                                         cleanedBatch, totalCount, successCount, 
                                         failedCount, ruleApplicationCounts);
        }
        
        public static BatchCleansingResult failure(String message) {
            return new BatchCleansingResult(false, message, null, 0, 0, 0, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public List<Object> getCleanedBatch() {
            return cleanedBatch;
        }
        
        public int getTotalCount() {
            return totalCount;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public int getFailedCount() {
            return failedCount;
        }
        
        public Map<String, Integer> getRuleApplicationCounts() {
            return ruleApplicationCounts;
        }
    }
    
    /**
     * 数据验证结果类
     */
    class ValidationResult {
        private final boolean valid;
        private final String message;
        private final List<RuleViolation> violations;
        private final double qualityScore;
        
        public ValidationResult(boolean valid, String message, 
                              List<RuleViolation> violations, double qualityScore) {
            this.valid = valid;
            this.message = message;
            this.violations = violations;
            this.qualityScore = qualityScore;
        }
        
        public static ValidationResult valid(double qualityScore) {
            return new ValidationResult(true, "Data is valid", null, qualityScore);
        }
        
        public static ValidationResult invalid(String message, 
                                            List<RuleViolation> violations, 
                                            double qualityScore) {
            return new ValidationResult(false, message, violations, qualityScore);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public List<RuleViolation> getViolations() {
            return violations;
        }
        
        public double getQualityScore() {
            return qualityScore;
        }
    }
    
    /**
     * 批量数据验证结果类
     */
    class BatchValidationResult {
        private final boolean allValid;
        private final String message;
        private final int totalCount;
        private final int validCount;
        private final int invalidCount;
        private final List<Integer> invalidIndices;
        private final Map<String, Integer> violationCounts;
        private final double averageQualityScore;
        
        public BatchValidationResult(boolean allValid, String message, 
                                   int totalCount, int validCount, int invalidCount, 
                                   List<Integer> invalidIndices, 
                                   Map<String, Integer> violationCounts, 
                                   double averageQualityScore) {
            this.allValid = allValid;
            this.message = message;
            this.totalCount = totalCount;
            this.validCount = validCount;
            this.invalidCount = invalidCount;
            this.invalidIndices = invalidIndices;
            this.violationCounts = violationCounts;
            this.averageQualityScore = averageQualityScore;
        }
        
        public static BatchValidationResult allValid(int count, double averageQualityScore) {
            return new BatchValidationResult(true, "All data is valid", 
                                          count, count, 0, 
                                          null, null, averageQualityScore);
        }
        
        public static BatchValidationResult hasInvalid(String message, 
                                                    int totalCount, int validCount, 
                                                    int invalidCount, 
                                                    List<Integer> invalidIndices, 
                                                    Map<String, Integer> violationCounts, 
                                                    double averageQualityScore) {
            return new BatchValidationResult(false, message, 
                                          totalCount, validCount, invalidCount, 
                                          invalidIndices, violationCounts, 
                                          averageQualityScore);
        }
        
        public boolean isAllValid() {
            return allValid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getTotalCount() {
            return totalCount;
        }
        
        public int getValidCount() {
            return validCount;
        }
        
        public int getInvalidCount() {
            return invalidCount;
        }
        
        public List<Integer> getInvalidIndices() {
            return invalidIndices;
        }
        
        public Map<String, Integer> getViolationCounts() {
            return violationCounts;
        }
        
        public double getAverageQualityScore() {
            return averageQualityScore;
        }
    }
    
    /**
     * 注册结果类
     */
    class RegistrationResult {
        private final boolean success;
        private final String message;
        private final String registrationId;
        
        public RegistrationResult(boolean success, String message, String registrationId) {
            this.success = success;
            this.message = message;
            this.registrationId = registrationId;
        }
        
        public static RegistrationResult success(String message, String registrationId) {
            return new RegistrationResult(true, message, registrationId);
        }
        
        public static RegistrationResult failure(String message) {
            return new RegistrationResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getRegistrationId() {
            return registrationId;
        }
    }
    
    /**
     * 血缘关系记录结果类
     */
    class LineageResult {
        private final boolean success;
        private final String message;
        private final String lineageId;
        
        public LineageResult(boolean success, String message, String lineageId) {
            this.success = success;
            this.message = message;
            this.lineageId = lineageId;
        }
        
        public static LineageResult success(String message, String lineageId) {
            return new LineageResult(true, message, lineageId);
        }
        
        public static LineageResult failure(String message) {
            return new LineageResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getLineageId() {
            return lineageId;
        }
    }
    
    /**
     * 任务结果类
     */
    class TaskResult {
        private final boolean success;
        private final String message;
        private final String taskId;
        
        public TaskResult(boolean success, String message, String taskId) {
            this.success = success;
            this.message = message;
            this.taskId = taskId;
        }
        
        public static TaskResult success(String message, String taskId) {
            return new TaskResult(true, message, taskId);
        }
        
        public static TaskResult failure(String message) {
            return new TaskResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getTaskId() {
            return taskId;
        }
    }
    
    /**
     * 数据质量检查结果类
     */
    class QualityCheckResult {
        private final boolean success;
        private final String message;
        private final String datasetId;
        private final String ruleSetId;
        private final double qualityScore;
        private final Map<String, Double> dimensionScores;
        private final List<QualityIssue> issues;
        private final Map<String, Object> statistics;
        
        public QualityCheckResult(boolean success, String message, String datasetId, 
                                String ruleSetId, double qualityScore, 
                                Map<String, Double> dimensionScores, 
                                List<QualityIssue> issues, 
                                Map<String, Object> statistics) {
            this.success = success;
            this.message = message;
            this.datasetId = datasetId;
            this.ruleSetId = ruleSetId;
            this.qualityScore = qualityScore;
            this.dimensionScores = dimensionScores;
            this.issues = issues;
            this.statistics = statistics;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getDatasetId() {
            return datasetId;
        }
        
        public String getRuleSetId() {
            return ruleSetId;
        }
        
        public double getQualityScore() {
            return qualityScore;
        }
        
        public Map<String, Double> getDimensionScores() {
            return dimensionScores;
        }
        
        public List<QualityIssue> getIssues() {
            return issues;
        }
        
        public Map<String, Object> getStatistics() {
            return statistics;
        }
    }
    
    /**
     * 清洗规则集类
     */
    class CleansingRuleSet {
        private final String id;
        private final String name;
        private final String version;
        private final List<CleansingRule> rules;
        private final Map<String, Object> metadata;
        
        public CleansingRuleSet(String id, String name, String version, 
                               List<CleansingRule> rules, Map<String, Object> metadata) {
            this.id = id;
            this.name = name;
            this.version = version;
            this.rules = rules;
            this.metadata = metadata;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getVersion() {
            return version;
        }
        
        public List<CleansingRule> getRules() {
            return rules;
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }
    
    /**
     * 清洗规则类
     */
    class CleansingRule {
        private final String id;
        private final String name;
        private final String field;
        private final String action;
        private final String pattern;
        private final String format;
        private final Map<String, Object> config;
        
        public CleansingRule(String id, String name, String field, String action, 
                           String pattern, String format, Map<String, Object> config) {
            this.id = id;
            this.name = name;
            this.field = field;
            this.action = action;
            this.pattern = pattern;
            this.format = format;
            this.config = config;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getField() {
            return field;
        }
        
        public String getAction() {
            return action;
        }
        
        public String getPattern() {
            return pattern;
        }
        
        public String getFormat() {
            return format;
        }
        
        public Map<String, Object> getConfig() {
            return config;
        }
    }
    
    /**
     * 质量规则集类
     */
    class QualityRuleSet {
        private final String id;
        private final String name;
        private final String version;
        private final List<QualityRule> rules;
        private final Map<String, Object> metadata;
        
        public QualityRuleSet(String id, String name, String version, 
                             List<QualityRule> rules, Map<String, Object> metadata) {
            this.id = id;
            this.name = name;
            this.version = version;
            this.rules = rules;
            this.metadata = metadata;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getVersion() {
            return version;
        }
        
        public List<QualityRule> getRules() {
            return rules;
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }
    
    /**
     * 质量规则类
     */
    class QualityRule {
        private final String id;
        private final String name;
        private final String field;
        private final String type;
        private final String severity;
        private final Map<String, Object> parameters;
        
        public QualityRule(String id, String name, String field, String type, 
                         String severity, Map<String, Object> parameters) {
            this.id = id;
            this.name = name;
            this.field = field;
            this.type = type;
            this.severity = severity;
            this.parameters = parameters;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getField() {
            return field;
        }
        
        public String getType() {
            return type;
        }
        
        public String getSeverity() {
            return severity;
        }
        
        public Map<String, Object> getParameters() {
            return parameters;
        }
    }
    
    /**
     * 规则应用类
     */
    class RuleApplication {
        private final String ruleId;
        private final String ruleName;
        private final String field;
        private final Object originalValue;
        private final Object newValue;
        private final boolean applied;
        
        public RuleApplication(String ruleId, String ruleName, String field, 
                             Object originalValue, Object newValue, boolean applied) {
            this.ruleId = ruleId;
            this.ruleName = ruleName;
            this.field = field;
            this.originalValue = originalValue;
            this.newValue = newValue;
            this.applied = applied;
        }
        
        public String getRuleId() {
            return ruleId;
        }
        
        public String getRuleName() {
            return ruleName;
        }
        
        public String getField() {
            return field;
        }
        
        public Object getOriginalValue() {
            return originalValue;
        }
        
        public Object getNewValue() {
            return newValue;
        }
        
        public boolean isApplied() {
            return applied;
        }
    }
    
    /**
     * 规则违反类
     */
    class RuleViolation {
        private final String ruleId;
        private final String ruleName;
        private final String field;
        private final Object value;
        private final String severity;
        private final String message;
        
        public RuleViolation(String ruleId, String ruleName, String field, 
                           Object value, String severity, String message) {
            this.ruleId = ruleId;
            this.ruleName = ruleName;
            this.field = field;
            this.value = value;
            this.severity = severity;
            this.message = message;
        }
        
        public String getRuleId() {
            return ruleId;
        }
        
        public String getRuleName() {
            return ruleName;
        }
        
        public String getField() {
            return field;
        }
        
        public Object getValue() {
            return value;
        }
        
        public String getSeverity() {
            return severity;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * 质量问题类
     */
    class QualityIssue {
        private final String ruleId;
        private final String ruleName;
        private final String field;
        private final String severity;
        private final String message;
        private final int count;
        private final List<Object> sampleValues;
        
        public QualityIssue(String ruleId, String ruleName, String field, 
                          String severity, String message, int count, 
                          List<Object> sampleValues) {
            this.ruleId = ruleId;
            this.ruleName = ruleName;
            this.field = field;
            this.severity = severity;
            this.message = message;
            this.count = count;
            this.sampleValues = sampleValues;
        }
        
        public String getRuleId() {
            return ruleId;
        }
        
        public String getRuleName() {
            return ruleName;
        }
        
        public String getField() {
            return field;
        }
        
        public String getSeverity() {
            return severity;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getCount() {
            return count;
        }
        
        public List<Object> getSampleValues() {
            return sampleValues;
        }
    }
}
