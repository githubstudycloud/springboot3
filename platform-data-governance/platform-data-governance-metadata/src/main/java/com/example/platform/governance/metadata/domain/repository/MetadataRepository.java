package com.example.platform.governance.metadata.domain.repository;

import com.example.platform.governance.metadata.domain.model.MetadataItem;
import com.example.platform.governance.metadata.domain.model.MetadataStatus;
import com.example.platform.governance.metadata.domain.model.MetadataType;

import java.util.List;
import java.util.Optional;

/**
 * 元数据存储库接口
 * 
 * 定义元数据管理所需的存储操作
 */
public interface MetadataRepository {
    
    /**
     * 保存元数据项
     * 
     * @param metadata 元数据项
     * @return 保存后的元数据项
     */
    MetadataItem save(MetadataItem metadata);
    
    /**
     * 根据ID查找元数据项
     * 
     * @param id 元数据项ID
     * @return 包含元数据项的Optional，如果不存在则为空
     */
    Optional<MetadataItem> findById(String id);
    
    /**
     * 根据编码查找元数据项
     * 
     * @param code 元数据项编码
     * @return 包含元数据项的Optional，如果不存在则为空
     */
    Optional<MetadataItem> findByCode(String code);
    
    /**
     * 查找所有元数据项
     * 
     * @return 元数据项列表
     */
    List<MetadataItem> findAll();
    
    /**
     * 根据类型查找元数据项
     * 
     * @param type 元数据类型
     * @return 元数据项列表
     */
    List<MetadataItem> findByType(MetadataType type);
    
    /**
     * 根据状态查找元数据项
     * 
     * @param status 元数据状态
     * @return 元数据项列表
     */
    List<MetadataItem> findByStatus(MetadataStatus status);
    
    /**
     * 根据分类ID查找元数据项
     * 
     * @param categoryId 分类ID
     * @return 元数据项列表
     */
    List<MetadataItem> findByCategoryId(String categoryId);
    
    /**
     * 根据名称模糊查找元数据项
     * 
     * @param nameLike 名称模糊匹配字符串
     * @return 元数据项列表
     */
    List<MetadataItem> findByNameLike(String nameLike);
    
    /**
     * 根据属性查找元数据项
     * 
     * @param propertyKey 属性键
     * @param propertyValue 属性值
     * @return 元数据项列表
     */
    List<MetadataItem> findByProperty(String propertyKey, String propertyValue);
    
    /**
     * 分页查询元数据项
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 元数据项列表
     */
    List<MetadataItem> findWithPagination(int offset, int limit);
    
    /**
     * 统计元数据项总数
     * 
     * @return 元数据项总数
     */
    long count();
    
    /**
     * 根据ID删除元数据项
     * 
     * @param id 元数据项ID
     * @return 是否成功删除
     */
    boolean deleteById(String id);
}
