package com.campus.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户补贴关联实体类
 */
@Data
@TableName("user_subsidy")
public class UserSubsidy implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long subsidyId;
    
    private BigDecimal balance;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 