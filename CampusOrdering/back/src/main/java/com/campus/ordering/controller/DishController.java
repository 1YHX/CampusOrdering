package com.campus.ordering.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.ordering.common.R;
import com.campus.ordering.dto.DishDTO;
import com.campus.ordering.entity.Dish;
import com.campus.ordering.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 菜品管理控制器
 */
@Slf4j
@Api(tags = "菜品管理")
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @ApiOperation("分页查询菜品")
    @GetMapping("/page")
    public R<IPage<Dish>> pageQuery(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) Long categoryId,
                                    @RequestParam(required = false) Integer status) {
        log.info("分页查询菜品：page={}, size={}, name={}, categoryId={}, status={}", 
                page, size, name, categoryId, status);
        
        IPage<Dish> pageResult = dishService.pageQuery(page, size, name, categoryId, status);
        return R.success(pageResult);
    }

    @ApiOperation("根据分类查询菜品")
    @GetMapping("/category/{categoryId}")
    public R<IPage<Dish>> getDishesByCategory(@PathVariable Long categoryId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        log.info("根据分类查询菜品：categoryId={}, page={}, size={}", categoryId, page, size);
        
        IPage<Dish> pageResult = dishService.getDishesByCategory(page, size, categoryId);
        return R.success(pageResult);
    }

    @ApiOperation("查询所有上架菜品")
    @GetMapping("/available")
    public R<IPage<Dish>> getAvailableDishes(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "50") int size) {
        log.info("查询所有上架菜品：page={}, size={}", page, size);
        
        IPage<Dish> pageResult = dishService.pageQuery(page, size, null, null, 1);
        return R.success(pageResult);
    }

    @ApiOperation("根据ID查询菜品")
    @GetMapping("/{id}")
    public R<Dish> getDishById(@PathVariable Long id) {
        log.info("根据ID查询菜品：id={}", id);
        
        Dish dish = dishService.getById(id);
        return dish != null ? R.success(dish) : R.error("菜品不存在");
    }

    @ApiOperation("添加菜品")
    @PostMapping
    public R<String> addDish(@Valid @RequestBody DishDTO dishDTO) {
        log.info("添加菜品：{}", dishDTO);
        
        boolean result = dishService.addDish(dishDTO);
        return result ? R.success("菜品添加成功") : R.error("菜品添加失败");
    }

    @ApiOperation("更新菜品")
    @PutMapping("/{id}")
    public R<String> updateDish(@PathVariable Long id, @Valid @RequestBody DishDTO dishDTO) {
        log.info("更新菜品：id={}, dish={}", id, dishDTO);
        
        boolean result = dishService.updateDish(id, dishDTO);
        return result ? R.success("菜品更新成功") : R.error("菜品更新失败");
    }

    @ApiOperation("删除菜品")
    @DeleteMapping("/{id}")
    public R<String> deleteDish(@PathVariable Long id) {
        log.info("删除菜品：id={}", id);
        
        boolean result = dishService.deleteDish(id);
        return result ? R.success("菜品删除成功") : R.error("菜品删除失败");
    }

    @ApiOperation("更新菜品状态")
    @PutMapping("/{id}/status")
    public R<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新菜品状态：id={}, status={}", id, status);
        
        boolean result = dishService.updateStatus(id, status);
        return result ? R.success("状态更新成功") : R.error("状态更新失败");
    }
} 