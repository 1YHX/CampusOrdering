package com.campus.ordering.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.ordering.dto.DishDTO;
import com.campus.ordering.entity.Dish;

/**
 * 菜品服务接口
 */
public interface DishService extends IService<Dish> {
    
    /**
     * 分页查询菜品
     */
    IPage<Dish> pageQuery(int page, int size, String name, Long categoryId, Integer status);
    
    /**
     * 添加菜品
     */
    boolean addDish(DishDTO dishDTO);
    
    /**
     * 更新菜品
     */
    boolean updateDish(Long id, DishDTO dishDTO);
    
    /**
     * 删除菜品
     */
    boolean deleteDish(Long id);
    
    /**
     * 更新菜品状态
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 根据分类查询菜品
     */
    IPage<Dish> getDishesByCategory(int page, int size, Long categoryId);
    
    /**
     * 减少菜品库存
     */
    boolean reduceStock(Long dishId, Integer quantity);
    
    /**
     * 增加菜品库存（取消订单时使用）
     */
    boolean increaseStock(Long dishId, Integer quantity);
} 