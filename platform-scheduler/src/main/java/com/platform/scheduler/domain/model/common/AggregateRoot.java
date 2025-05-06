package com.platform.scheduler.domain.model.common;

/**
 * 聚合根接口
 * 聚合根是一种特殊的实体，它是聚合的入口，负责维护聚合内部的一致性。
 * 
 * @author platform
 */
public interface AggregateRoot<ID> extends Entity<ID> {
}
