package com.campus.ordering.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 菜品DTO
 */
@Data
public class DishDTO {
    
    @NotBlank(message = "菜品名称不能为空")
    private String name;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;
    
    private String image;
    
    private String description;
    
    @NotNull(message = "状态不能为空")
    private Integer status;
    
    @NotBlank(message = "类型不能为空")
    private String type;
    
    private Integer stock;
    
    private Integer stockAlert;
} 