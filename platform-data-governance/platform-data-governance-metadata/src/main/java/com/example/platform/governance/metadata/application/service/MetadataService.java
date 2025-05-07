package com.example.platform.governance.metadata.application.service;

import com.example.platform.governance.metadata.domain.model.MetadataItem;
import com.example.platform.governance.metadata.domain.model.MetadataStatus;
import com.example.platform.governance.metadata.domain.model.MetadataType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 元数据服务接口
 */
public interface MetadataService {
    
    /**
     * 创建元数据项
     * 
     * @param name 元数据名称
     * @param code 元数据编码
     * @param type 元数据类型
     * @param description 元数据描述
     * @return 创建的元数据项
     */
    MetadataItem createMetadata(String name, String code, MetadataType type, String description);
    
    /**
     * 更新元数据项
     * 
     * @param id 元数据项ID
     * @param name 元数据名称
     * @param description 元数据描述
     * @return 是否更新成功
     */
    boolean updateMetadata(String id, String name, String description);
    
    /**
     * 更新元数据项状态
     * 
     * @param id 元数据项ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateStatus(String id, MetadataStatus status);
    
    /**
     * 删除元数据项
     * 
     * @param id 元数据项ID
     * @return 是否成功删除
     */
    boolean deleteMetadata(String id);
    
    /**
     * 获取元数据项
     * 
     * @param id 元数据项ID
     * @return 元数据项的Optional包装
     */
    Optional<MetadataItem> getMetadata(String id);
    
    /**
     * 根据编码获取元数据项
     * 
     * @param code 元数据项编码
     * @return 元数据项的Optional包装
     */
    Optional<MetadataItem> getMetadataByCode(String code);
    
    /**
     * 获取所有元数据项
     * 
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 元数据项列表
     */
    List<MetadataItem> getAllMetadata(int page, int size);
    
    /**
     * 根据类型获取元数据项
     * 
     * @param type 元数据类型
     * @return 元数据项列表
     */
    List<MetadataItem> getMetadataByType(MetadataType type);
    
    /**
     * 根据状态获取元数据项
     * 
     * @param status 元数据状态
     * @return 元数据项列表
     */
    List<MetadataItem> getMetadataByStatus(MetadataStatus status);
    
    /**
     * 根据分类获取元数据项
     * 
     * @param categoryId 分类ID
     * @return 元数据项列表
     */
    List<MetadataItem> getMetadataByCategory(String categoryId);
    
    /**
     * 搜索元数据项
     * 
     * @param keyword 关键词
     * @return 元数据项列表
     */
    List<MetadataItem> searchMetadata(String keyword);
    
    /**
     * 添加元数据属性
     * 
     * @param id 元数据项ID
     * @param key 属性键
     * @param value 属性值
     * @return 是否添加成功
     */
    boolean addProperty(String id, String key, String value);
    
    /**
     * 移除元数据属性
     * 
     * @param id 元数据项ID
     * @param key 属性键
     * @return 是否移除成功
     */
    boolean removeProperty(String id, String key);
    
    /**
     * 设置元数据所有者
     * 
     * @param id 元数据项ID
     * @param ownerId 所有者ID
     * @param ownerName 所有者名称
     * @return 是否设置成功
     */
    boolean setOwner(String id, String ownerId, String ownerName);
    
    /**
     * 设置元数据分类
     * 
     * @param id 元数据项ID
     * @param categoryId 分类ID
     * @param categoryName 分类名称
     * @return 是否设置成功
     */
    boolean setCategory(String id, String categoryId, String categoryName);
    
    /**
     * 统计元数据数量
     * 
     * @return 元数据总数
     */
    long countMetadata();
}
