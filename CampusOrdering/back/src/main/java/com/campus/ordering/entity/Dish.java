package com.campus.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类
 */
@Data
@TableName("dish")
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Long categoryId;
    
    private BigDecimal price;
    
    private String image;
    
    private String description;
    
    private Integer status;
    
    private String type;
    
    private Integer stock;
    
    private Integer sold;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 