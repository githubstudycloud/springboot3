package com.platform.scheduler.domain.model.job;

import com.platform.scheduler.domain.model.common.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 作业优先级值对象
 * 定义作业的优先级，范围从1(最低)到10(最高)
 * 
 * @author platform
 */
@Getter
@EqualsAndHashCode
@ToString
public class JobPriority implements ValueObject {
    
    public static final JobPriority LOWEST = new JobPriority(1);
    public static final JobPriority LOW = new JobPriority(3);
    public static final JobPriority NORMAL = new JobPriority(5);
    public static final JobPriority HIGH = new JobPriority(7);
    public static final JobPriority HIGHEST = new JobPriority(10);
    
    private final int value;
    
    public JobPriority(int value) {
        if (value < 1 || value > 10) {
            throw new IllegalArgumentException("Priority must be between 1 and 10");
        }
        this.value = value;
    }
    
    /**
     * 创建默认优先级（正常，值为5）
     *
     * @return 默认优先级
     */
    public static JobPriority defaultPriority() {
        return NORMAL;
    }
    
    /**
     * 比较此优先级是否高于另一个优先级
     *
     * @param other 另一个优先级对象
     * @return 如果此优先级高于另一个，则返回true
     */
    public boolean isHigherThan(JobPriority other) {
        return this.value > other.value;
    }
    
    /**
     * 比较此优先级是否低于另一个优先级
     *
     * @param other 另一个优先级对象
     * @return 如果此优先级低于另一个，则返回true
     */
    public boolean isLowerThan(JobPriority other) {
        return this.value < other.value;
    }
}
