package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.ordering.common.CustomException;
import com.campus.ordering.dto.DishDTO;
import com.campus.ordering.entity.Dish;
import com.campus.ordering.mapper.DishMapper;
import com.campus.ordering.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品服务实现类
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    
    @Override
    public IPage<Dish> pageQuery(int page, int size, String name, Long categoryId, Integer status) {
        Page<Dish> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据名称模糊查询
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Dish::getName, name);
        }
        
        // 根据分类查询
        if (categoryId != null) {
            queryWrapper.eq(Dish::getCategoryId, categoryId);
        }
        
        // 根据状态查询
        if (status != null) {
            queryWrapper.eq(Dish::getStatus, status);
        }
        
        // 按创建时间倒序
        queryWrapper.orderByDesc(Dish::getCreateTime);
        
        return this.page(pageInfo, queryWrapper);
    }
    
    @Override
    public boolean addDish(DishDTO dishDTO) {
        // 检查菜品名称是否已存在
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getName, dishDTO.getName());
        if (this.count(queryWrapper) > 0) {
            throw new CustomException("菜品名称已存在");
        }
        
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setSold(0);
        dish.setRating(BigDecimal.valueOf(5.0));
        dish.setRatingCount(0);
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        
        return this.save(dish);
    }
    
    @Override
    public boolean updateDish(Long id, DishDTO dishDTO) {
        Dish dish = this.getById(id);
        if (dish == null) {
            throw new CustomException("菜品不存在");
        }
        
        // 检查菜品名称是否已存在（排除当前菜品）
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getName, dishDTO.getName())
                   .ne(Dish::getId, id);
        if (this.count(queryWrapper) > 0) {
            throw new CustomException("菜品名称已存在");
        }
        
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(dish);
    }
    
    @Override
    public boolean deleteDish(Long id) {
        Dish dish = this.getById(id);
        if (dish == null) {
            throw new CustomException("菜品不存在");
        }
        
        return this.removeById(id);
    }
    
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Dish dish = this.getById(id);
        if (dish == null) {
            throw new CustomException("菜品不存在");
        }
        
        dish.setStatus(status);
        dish.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(dish);
    }
    
    @Override
    public IPage<Dish> getDishesByCategory(int page, int size, Long categoryId) {
        Page<Dish> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        
        if (categoryId != null) {
            queryWrapper.eq(Dish::getCategoryId, categoryId);
        }
        
        // 只查询上架的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        
        // 按排序字段和创建时间排序
        queryWrapper.orderByAsc(Dish::getType)
                   .orderByDesc(Dish::getCreateTime);
        
        return this.page(pageInfo, queryWrapper);
    }
    
    @Override
    public boolean reduceStock(Long dishId, Integer quantity) {
        Dish dish = this.getById(dishId);
        if (dish == null) {
            throw new CustomException("菜品不存在");
        }
        
        // 检查库存是否充足
        Integer currentStock = dish.getStock() != null ? dish.getStock() : 0;
        if (currentStock < quantity) {
            throw new CustomException("菜品" + dish.getName() + "库存不足，当前库存：" + currentStock + "，需要：" + quantity);
        }
        
        // 减少库存，增加销量
        dish.setStock(currentStock - quantity);
        dish.setSold((dish.getSold() != null ? dish.getSold() : 0) + quantity);
        dish.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(dish);
    }
    
    @Override
    public boolean increaseStock(Long dishId, Integer quantity) {
        Dish dish = this.getById(dishId);
        if (dish == null) {
            throw new CustomException("菜品不存在");
        }
        
        // 增加库存，减少销量
        Integer currentStock = dish.getStock() != null ? dish.getStock() : 0;
        Integer currentSold = dish.getSold() != null ? dish.getSold() : 0;
        
        dish.setStock(currentStock + quantity);
        dish.setSold(Math.max(0, currentSold - quantity)); // 确保sold不会变成负数
        dish.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(dish);
    }
} 