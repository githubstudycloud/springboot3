package com.platform.report.domain.model.common;

/**
 * 聚合根接口
 * 标识领域模型中的聚合根实体
 *
 * @param <ID> 聚合根ID类型
 */
public interface AggregateRoot<ID> {
    
    /**
     * 获取聚合根ID
     *
     * @return 聚合根唯一标识
     */
    ID getId();
}
