package com.campus.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("`order`")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private String userType;
    
    private Long userId;
    
    private BigDecimal amount;
    
    private BigDecimal subsidyAmount;
    
    private BigDecimal actualAmount;
    
    private String paymentMethod;
    
    private String orderType;
    
    private String status;
    
    private LocalDateTime eatTime;
    
    private String pickupNo;
    
    private String remark;
    
    private Long operatorId;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 