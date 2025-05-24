package com.campus.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体类
 */
@Data
@TableName("order_detail")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long dishId;
    
    private String dishName;
    
    private String dishType;
    
    private BigDecimal price;
    
    private Integer quantity;
    
    private BigDecimal amount;
    
    private String status;
    
    private LocalDateTime createTime;
} 