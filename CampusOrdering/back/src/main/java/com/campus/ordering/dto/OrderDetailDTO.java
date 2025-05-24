package com.campus.ordering.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 订单明细DTO
 */
@Data
public class OrderDetailDTO {
    
    @NotNull(message = "菜品ID不能为空")
    private Long dishId;
    
    @NotBlank(message = "菜品名称不能为空")
    private String dishName;
    
    @NotBlank(message = "菜品类型不能为空")
    private String dishType;
    
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    
    @NotNull(message = "数量不能为空")
    private Integer quantity;
    
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;
} 