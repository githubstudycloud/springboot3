-- Excel Framework 示例数据脚本

USE `excel_framework`;

-- ===============================================
-- 插入故障分类示例数据
-- ===============================================

INSERT INTO `fault_classification` (`n`, `code`, `scope_type`, `description`, `sort_order`) VALUES
('硬件故障', 'HARDWARE', 1, '硬件设备相关故障', 1),
('软件故障', 'SOFTWARE', 1, '软件系统相关故障', 2),
('网络故障', 'NETWORK', 1, '网络连接相关故障', 3),
('数据故障', 'DATA', 1, '数据相关故障', 4),
('性能故障', 'PERFORMANCE', 1, '系统性能相关故障', 5),
('安全故障', 'SECURITY', 1, '安全相关故障', 6);

-- ===============================================
-- 插入模型分类示例数据
-- ===============================================

INSERT INTO `model_category` (`n`, `code`, `description`, `level`, `sort_order`) VALUES
('机器学习模型', 'ML', '机器学习相关模型', 1, 1),
('深度学习模型', 'DL', '深度学习相关模型', 1, 2),
('自然语言处理', 'NLP', '自然语言处理模型', 1, 3),
('计算机视觉', 'CV', '计算机视觉模型', 1, 4),
('推荐系统', 'RS', '推荐系统模型', 1, 5),
('时间序列', 'TS', '时间序列分析模型', 1, 6);

-- ===============================================
-- 插入Excel模板配置示例数据
-- ===============================================

-- 故障表模板配置
INSERT INTO `excel_template_config` 
(`template_key`, `template_n`, `entity_class`, `table_n`, `sheet_n`, `primary_key_fields`, `update_mode`, `description`) 
VALUES 
('fault', '故障数据模板', 'com.framework.excel.entity.Fault', 'fault', '故障数据', '["code"]', 'INSERT_OR_UPDATE', '故障信息导入导出模板');

-- 故障表字段配置
INSERT INTO `excel_field_config` 
(`template_id`, `field_n`, `column_n`, `column_index`, `data_type`, `required`, `visible`, `width`, `sort_order`) 
VALUES
((SELECT id FROM excel_template_config WHERE template_key = 'fault'), 'id', 'ID', 0, 'LONG', 0, 0, 10, 0),
((SELECT id FROM excel_template_config WHERE template_key = 'fault'), 'code', '故障编码', 1, 'STRING', 1, 1, 20, 1),
((SELECT id FROM excel_template_config WHERE template_key = 'fault'), 'n', '故障名称', 2, 'STRING', 1, 1, 25, 2),
((SELECT id FROM excel_template_config WHERE template_key = 'fault'), 'functionId', '功能ID', 3, 'LONG', 1, 1, 15, 3),
((SELECT id FROM excel_template_config WHERE template_key = 'fault'), 'classificationId', '故障分类', 4, 'LONG', 0, 1, 20, 4),
((SELECT id FROM excel_template_config WHERE template_key = 'fault'), 'severityLevel', '严重级别', 5, 'INTEGER', 0, 1, 15, 5),
((SELECT id FROM excel_template_config WHERE template_key = 'fault'), 'description', '故障描述', 6, 'STRING', 0, 1, 30, 6),
((SELECT id FROM excel_template_config WHERE template_key = 'fault'), 'solution', '解决方案', 7, 'STRING', 0, 1, 30, 7),
((SELECT id FROM excel_template_config WHERE template_key = 'fault'), 'status', '状态', 8, 'INTEGER', 0, 1, 15, 8);

-- 故障分类字段配置（下拉框）
UPDATE `excel_field_config` 
SET `dropdown_config` = JSON_OBJECT(
    'type', 'RELATED_TABLE',
    'tableName', 'fault_classification',
    'valueField', 'id',
    'displayField', 'n',
    'allowEmpty', true,
    'whereClause', 'enabled = 1'
)
WHERE `template_id` = (SELECT id FROM excel_template_config WHERE template_key = 'fault') 
AND `field_n` = 'classificationId';

-- 严重级别字段配置（静态下拉框）
UPDATE `excel_field_config` 
SET `dropdown_config` = JSON_OBJECT(
    'type', 'STATIC',
    'options', JSON_ARRAY(
        JSON_OBJECT('value', 1, 'label', '低'),
        JSON_OBJECT('value', 2, 'label', '中'),
        JSON_OBJECT('value', 3, 'label', '高'),
        JSON_OBJECT('value', 4, 'label', '紧急')
    )
)
WHERE `template_id` = (SELECT id FROM excel_template_config WHERE template_key = 'fault') 
AND `field_n` = 'severityLevel';

-- 状态字段配置（静态下拉框）
UPDATE `excel_field_config` 
SET `dropdown_config` = JSON_OBJECT(
    'type', 'STATIC',
    'options', JSON_ARRAY(
        JSON_OBJECT('value', 1, 'label', '活跃'),
        JSON_OBJECT('value', 2, 'label', '已解决'),
        JSON_OBJECT('value', 3, 'label', '已关闭')
    )
)
WHERE `template_id` = (SELECT id FROM excel_template_config WHERE template_key = 'fault') 
AND `field_n` = 'status';

-- 模型表模板配置
INSERT INTO `excel_template_config` 
(`template_key`, `template_n`, `entity_class`, `table_n`, `sheet_n`, `primary_key_fields`, `update_mode`, `description`) 
VALUES 
('model', '模型数据模板', 'com.framework.excel.entity.Model', 'model', '模型数据', '["code"]', 'INSERT_OR_UPDATE', '模型信息导入导出模板');

-- 模型表字段配置
INSERT INTO `excel_field_config` 
(`template_id`, `field_n`, `column_n`, `column_index`, `data_type`, `required`, `visible`, `width`, `sort_order`) 
VALUES
((SELECT id FROM excel_template_config WHERE template_key = 'model'), 'id', 'ID', 0, 'LONG', 0, 0, 10, 0),
((SELECT id FROM excel_template_config WHERE template_key = 'model'), 'code', '模型编码', 1, 'STRING', 1, 1, 20, 1),
((SELECT id FROM excel_template_config WHERE template_key = 'model'), 'n', '模型名称', 2, 'STRING', 1, 1, 25, 2),
((SELECT id FROM excel_template_config WHERE template_key = 'model'), 'categoryId', '模型分类', 3, 'LONG', 0, 1, 20, 3),
((SELECT id FROM excel_template_config WHERE template_key = 'model'), 'version', '版本', 4, 'STRING', 0, 1, 15, 4),
((SELECT id FROM excel_template_config WHERE template_key = 'model'), 'description', '描述', 5, 'STRING', 0, 1, 30, 5),
((SELECT id FROM excel_template_config WHERE template_key = 'model'), 'modelType', '模型类型', 6, 'STRING', 0, 1, 20, 6),
((SELECT id FROM excel_template_config WHERE template_key = 'model'), 'status', '状态', 7, 'INTEGER', 0, 1, 15, 7),
((SELECT id FROM excel_template_config WHERE template_key = 'model'), 'createBy', '创建人', 8, 'STRING', 0, 1, 15, 8);

-- 模型分类字段配置（下拉框）
UPDATE `excel_field_config` 
SET `dropdown_config` = JSON_OBJECT(
    'type', 'RELATED_TABLE',
    'tableName', 'model_category',
    'valueField', 'id',
    'displayField', 'n',
    'allowEmpty', true,
    'whereClause', 'enabled = 1'
)
WHERE `template_id` = (SELECT id FROM excel_template_config WHERE template_key = 'model') 
AND `field_n` = 'categoryId';

-- 模型类型字段配置（静态下拉框）
UPDATE `excel_field_config` 
SET `dropdown_config` = JSON_OBJECT(
    'type', 'STATIC',
    'options', JSON_ARRAY(
        JSON_OBJECT('value', 'CLASSIFICATION', 'label', '分类模型'),
        JSON_OBJECT('value', 'REGRESSION', 'label', '回归模型'),
        JSON_OBJECT('value', 'CLUSTERING', 'label', '聚类模型'),
        JSON_OBJECT('value', 'NEURAL_NETWORK', 'label', '神经网络'),
        JSON_OBJECT('value', 'ENSEMBLE', 'label', '集成模型')
    )
)
WHERE `template_id` = (SELECT id FROM excel_template_config WHERE template_key = 'model') 
AND `field_n` = 'modelType';

-- 模型状态字段配置（静态下拉框）
UPDATE `excel_field_config` 
SET `dropdown_config` = JSON_OBJECT(
    'type', 'STATIC',
    'options', JSON_ARRAY(
        JSON_OBJECT('value', 1, 'label', '草稿'),
        JSON_OBJECT('value', 2, 'label', '发布'),
        JSON_OBJECT('value', 3, 'label', '已弃用')
    )
)
WHERE `template_id` = (SELECT id FROM excel_template_config WHERE template_key = 'model') 
AND `field_n` = 'status';

-- ===============================================
-- 插入一些示例业务数据
-- ===============================================

-- 插入故障示例数据
INSERT INTO `fault` (`code`, `n`, `function_id`, `classification_id`, `severity_level`, `description`, `status`) VALUES
('FAULT_001', 'CPU过热故障', 1001, 1, 3, 'CPU温度超过安全阈值', 1),
('FAULT_002', '内存不足', 1002, 1, 2, '系统内存使用率超过90%', 1),
('FAULT_003', '数据库连接超时', 1003, 2, 2, '数据库连接响应时间超过30秒', 2),
('FAULT_004', '网络延迟过高', 1004, 3, 2, '网络延迟超过1000ms', 1),
('FAULT_005', '磁盘空间不足', 1005, 1, 3, '磁盘使用率超过95%', 1);

-- 插入模型示例数据
INSERT INTO `model` (`code`, `n`, `category_id`, `version`, `description`, `model_type`, `status`, `create_by`) VALUES
('MODEL_001', '用户行为分类模型', 1, 'v1.0.0', '基于用户行为数据的分类模型', 'CLASSIFICATION', 2, 'admin'),
('MODEL_002', '销售预测回归模型', 1, 'v1.2.0', '预测未来销售趋势的回归模型', 'REGRESSION', 2, 'admin'),
('MODEL_003', '客户聚类分析模型', 1, 'v2.0.0', '客户细分聚类分析模型', 'CLUSTERING', 1, 'admin'),
('MODEL_004', '图像识别CNN模型', 2, 'v1.0.0', '基于卷积神经网络的图像识别模型', 'NEURAL_NETWORK', 2, 'admin'),
('MODEL_005', '文本情感分析模型', 3, 'v1.1.0', 'BERT基础的文本情感分析模型', 'NEURAL_NETWORK', 1, 'admin');

COMMIT;
