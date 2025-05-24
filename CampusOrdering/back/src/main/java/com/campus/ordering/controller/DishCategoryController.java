package com.campus.ordering.controller;

import com.campus.ordering.common.R;
import com.campus.ordering.dto.DishCategoryDTO;
import com.campus.ordering.entity.DishCategory;
import com.campus.ordering.service.DishCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 菜品分类管理控制器
 */
@Slf4j
@Api(tags = "菜品分类管理")
@RestController
@RequestMapping("/category")
public class DishCategoryController {

    @Autowired
    private DishCategoryService categoryService;

    @ApiOperation("获取所有分类")
    @GetMapping("/list")
    public R<List<DishCategory>> getAllCategories() {
        log.info("获取所有分类");
        
        List<DishCategory> categories = categoryService.getAllCategories();
        return R.success(categories);
    }

    @ApiOperation("根据ID查询分类")
    @GetMapping("/{id}")
    public R<DishCategory> getCategoryById(@PathVariable Long id) {
        log.info("根据ID查询分类：id={}", id);
        
        DishCategory category = categoryService.getById(id);
        return category != null ? R.success(category) : R.error("分类不存在");
    }

    @ApiOperation("添加分类")
    @PostMapping
    public R<String> addCategory(@Valid @RequestBody DishCategoryDTO categoryDTO) {
        log.info("添加分类：{}", categoryDTO);
        
        boolean result = categoryService.addCategory(categoryDTO);
        return result ? R.success("分类添加成功") : R.error("分类添加失败");
    }

    @ApiOperation("更新分类")
    @PutMapping("/{id}")
    public R<String> updateCategory(@PathVariable Long id, @Valid @RequestBody DishCategoryDTO categoryDTO) {
        log.info("更新分类：id={}, category={}", id, categoryDTO);
        
        boolean result = categoryService.updateCategory(id, categoryDTO);
        return result ? R.success("分类更新成功") : R.error("分类更新失败");
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    public R<String> deleteCategory(@PathVariable Long id) {
        log.info("删除分类：id={}", id);
        
        boolean result = categoryService.deleteCategory(id);
        return result ? R.success("分类删除成功") : R.error("分类删除失败");
    }

    @ApiOperation("更新分类状态")
    @PutMapping("/{id}/status")
    public R<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新分类状态：id={}, status={}", id, status);
        
        boolean result = categoryService.updateStatus(id, status);
        return result ? R.success("状态更新成功") : R.error("状态更新失败");
    }
} 