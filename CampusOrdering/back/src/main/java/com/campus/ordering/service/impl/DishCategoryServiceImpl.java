package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.ordering.common.CustomException;
import com.campus.ordering.dto.DishCategoryDTO;
import com.campus.ordering.entity.DishCategory;
import com.campus.ordering.mapper.DishCategoryMapper;
import com.campus.ordering.service.DishCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜品分类服务实现类
 */
@Service
public class DishCategoryServiceImpl extends ServiceImpl<DishCategoryMapper, DishCategory> implements DishCategoryService {
    
    @Override
    public List<DishCategory> getAllCategories() {
        LambdaQueryWrapper<DishCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishCategory::getStatus, 1)
                   .orderByAsc(DishCategory::getSort)
                   .orderByAsc(DishCategory::getCreateTime);
        return this.list(queryWrapper);
    }
    
    @Override
    public boolean addCategory(DishCategoryDTO categoryDTO) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<DishCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishCategory::getName, categoryDTO.getName());
        if (this.count(queryWrapper) > 0) {
            throw new CustomException("分类名称已存在");
        }
        
        DishCategory category = new DishCategory();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        
        return this.save(category);
    }
    
    @Override
    public boolean updateCategory(Long id, DishCategoryDTO categoryDTO) {
        DishCategory category = this.getById(id);
        if (category == null) {
            throw new CustomException("分类不存在");
        }
        
        // 检查分类名称是否已存在（排除当前分类）
        LambdaQueryWrapper<DishCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishCategory::getName, categoryDTO.getName())
                   .ne(DishCategory::getId, id);
        if (this.count(queryWrapper) > 0) {
            throw new CustomException("分类名称已存在");
        }
        
        BeanUtils.copyProperties(categoryDTO, category);
        category.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(category);
    }
    
    @Override
    public boolean deleteCategory(Long id) {
        DishCategory category = this.getById(id);
        if (category == null) {
            throw new CustomException("分类不存在");
        }
        
        return this.removeById(id);
    }
    
    @Override
    public boolean updateStatus(Long id, Integer status) {
        DishCategory category = this.getById(id);
        if (category == null) {
            throw new CustomException("分类不存在");
        }
        
        category.setStatus(status);
        category.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(category);
    }
} 