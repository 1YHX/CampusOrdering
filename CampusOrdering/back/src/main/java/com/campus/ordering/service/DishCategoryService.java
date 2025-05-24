package com.campus.ordering.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.ordering.dto.DishCategoryDTO;
import com.campus.ordering.entity.DishCategory;

import java.util.List;

/**
 * 菜品分类服务接口
 */
public interface DishCategoryService extends IService<DishCategory> {
    
    /**
     * 获取所有分类
     */
    List<DishCategory> getAllCategories();
    
    /**
     * 添加分类
     */
    boolean addCategory(DishCategoryDTO categoryDTO);
    
    /**
     * 更新分类
     */
    boolean updateCategory(Long id, DishCategoryDTO categoryDTO);
    
    /**
     * 删除分类
     */
    boolean deleteCategory(Long id);
    
    /**
     * 更新分类状态
     */
    boolean updateStatus(Long id, Integer status);
} 