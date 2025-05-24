package com.campus.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 补贴方案表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("subsidy_plan")
public class SubsidyPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 方案名称
     */
    private String name;

    /**
     * 补贴金额
     */
    private BigDecimal amount;

    /**
     * 补贴周期:daily(每日),weekly(每周),monthly(每月)
     */
    private String period;

    /**
     * 补贴对象:student(学生),staff(员工)
     */
    private String targetType;

    /**
     * 补贴群体:college(学院),department(部门)
     */
    private String targetGroup;

    /**
     * 群体值
     */
    private String groupValue;

    /**
     * 是否清零:0不清零,1清零
     */
    private Integer isReset;

    /**
     * 现金支付比例
     */
    private BigDecimal cashRatio;

    /**
     * 状态:0禁用,1启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 