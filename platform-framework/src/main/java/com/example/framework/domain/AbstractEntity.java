package com.example.framework.domain;

import com.example.framework.core.BaseEntity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 抽象实体基类
 * 为所有领域实体提供通用属性和行为
 *
 * @param <ID> 实体ID类型
 */
@MappedSuperclass
public abstract class AbstractEntity<ID> implements BaseEntity<ID> {
    private static final long serialVersionUID = 1L;
    
    /**
     * 实体ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 创建者
     */
    private String createBy;
    
    /**
     * 更新者
     */
    private String updateBy;
    
    /**
     * 删除标记（0: 正常, 1: 已删除）
     */
    private Integer delFlag;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 实体初始化
     */
    public AbstractEntity() {
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
        this.delFlag = 0;
    }
    
    @Override
    public ID getId() {
        return id;
    }
    
    @Override
    public void setId(ID id) {
        this.id = id;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getCreateBy() {
        return createBy;
    }
    
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
    
    public String getUpdateBy() {
        return updateBy;
    }
    
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
    
    public Integer getDelFlag() {
        return delFlag;
    }
    
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    /**
     * 逻辑删除
     */
    public void markAsDeleted() {
        this.delFlag = 1;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 是否已删除
     *
     * @return 已删除返回true，否则返回false
     */
    public boolean isDeleted() {
        return delFlag != null && delFlag == 1;
    }
    
    /**
     * 是否有效（未删除）
     *
     * @return 有效返回true，否则返回false
     */
    @Override
    public boolean isValid() {
        return !isDeleted();
    }
    
    /**
     * 更新实体时调用，自动更新修改时间
     */
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 