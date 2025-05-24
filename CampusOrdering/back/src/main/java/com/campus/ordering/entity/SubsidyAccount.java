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
 * 补贴账户表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("subsidy_account")
public class SubsidyAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户类型:student,staff
     */
    private String userType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 补贴方案ID
     */
    private Long subsidyId;

    /**
     * 剩余补贴金额
     */
    private BigDecimal balance;

    /**
     * 累计收到补贴
     */
    private BigDecimal totalReceived;

    /**
     * 累计使用补贴
     */
    private BigDecimal totalUsed;

    /**
     * 最后清零时间
     */
    private LocalDateTime lastResetTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 