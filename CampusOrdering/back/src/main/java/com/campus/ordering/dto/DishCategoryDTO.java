package com.campus.ordering.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 菜品分类DTO
 */
@Data
public class DishCategoryDTO {
    
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    private Integer sort;
    
    private Integer status;
} 