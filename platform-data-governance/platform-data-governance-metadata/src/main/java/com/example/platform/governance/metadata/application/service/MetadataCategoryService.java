package com.example.platform.governance.metadata.application.service;

import com.example.platform.governance.metadata.domain.model.MetadataCategory;

import java.util.List;
import java.util.Optional;

/**
 * 元数据分类服务接口
 */
public interface MetadataCategoryService {
    
    /**
     * 创建顶级分类
     * 
     * @param name 分类名称
     * @param code 分类编码
     * @param description 分类描述
     * @return 创建的分类
     */
    MetadataCategory createRootCategory(String name, String code, String description);
    
    /**
     * 创建子分类
     * 
     * @param name 分类名称
     * @param code 分类编码
     * @param parentId 父分类ID
     * @param description 分类描述
     * @return 创建的分类
     */
    MetadataCategory createChildCategory(String name, String code, String parentId, String description);
    
    /**
     * 更新分类信息
     * 
     * @param id 分类ID
     * @param name 分类名称
     * @param description 分类描述
     * @return 是否更新成功
     */
    boolean updateCategory(String id, String name, String description);
    
    /**
     * 删除分类
     * 
     * @param id 分类ID
     * @return 是否成功删除
     */
    boolean deleteCategory(String id);
    
    /**
     * 获取分类
     * 
     * @param id 分类ID
     * @return 分类的Optional包装
     */
    Optional<MetadataCategory> getCategory(String id);
    
    /**
     * 根据编码获取分类
     * 
     * @param code 分类编码
     * @return 分类的Optional包装
     */
    Optional<MetadataCategory> getCategoryByCode(String code);
    
    /**
     * 获取所有顶级分类
     * 
     * @return 顶级分类列表
     */
    List<MetadataCategory> getRootCategories();
    
    /**
     * 获取子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<MetadataCategory> getChildCategories(String parentId);
    
    /**
     * 获取分类树
     * 
     * @param rootId 根分类ID，如果为null则获取全部树结构
     * @return 包含完整子树的分类
     */
    MetadataCategory getCategoryTree(String rootId);
    
    /**
     * 根据路径获取分类
     * 
     * @param path 分类路径
     * @return 分类的Optional包装
     */
    Optional<MetadataCategory> getCategoryByPath(String path);
    
    /**
     * 搜索分类
     * 
     * @param keyword 关键词
     * @return 分类列表
     */
    List<MetadataCategory> searchCategories(String keyword);
    
    /**
     * 移动分类
     * 
     * @param categoryId 要移动的分类ID
     * @param newParentId 新父分类ID，如果为null则移动到顶级
     * @return 是否移动成功
     */
    boolean moveCategory(String categoryId, String newParentId);
    
    /**
     * 获取分类下的元数据数量
     * 
     * @param categoryId 分类ID
     * @return 元数据数量
     */
    long countMetadataInCategory(String categoryId);
    
    /**
     * 统计分类总数
     * 
     * @return 分类总数
     */
    long countCategories();
}
