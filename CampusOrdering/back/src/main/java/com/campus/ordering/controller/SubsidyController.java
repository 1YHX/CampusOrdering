package com.campus.ordering.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.ordering.common.R;
import com.campus.ordering.entity.SubsidyPlan;
import com.campus.ordering.service.SubsidyPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 补贴管理控制器
 */
@Slf4j
@Api(tags = "补贴管理")
@RestController
@RequestMapping("/subsidy")
public class SubsidyController {

    @Autowired
    private SubsidyPlanService subsidyPlanService;

    @ApiOperation("分页查询补贴方案")
    @GetMapping("/plans/page")
    public R<Page<SubsidyPlan>> getSubsidyPlansPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Integer status) {
        
        log.info("分页查询补贴方案：page={}, size={}, name={}, targetType={}, period={}, status={}", 
                page, size, name, targetType, period, status);

        Page<SubsidyPlan> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<SubsidyPlan> queryWrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(SubsidyPlan::getName, name);
        }
        if (targetType != null && !targetType.trim().isEmpty()) {
            queryWrapper.eq(SubsidyPlan::getTargetType, targetType);
        }
        if (period != null && !period.trim().isEmpty()) {
            queryWrapper.eq(SubsidyPlan::getPeriod, period);
        }
        if (status != null) {
            queryWrapper.eq(SubsidyPlan::getStatus, status);
        }
        
        queryWrapper.orderByDesc(SubsidyPlan::getCreateTime);
        
        Page<SubsidyPlan> result = subsidyPlanService.page(pageInfo, queryWrapper);
        return R.success(result);
    }

    @ApiOperation("获取所有补贴方案")
    @GetMapping("/plans")
    public R<List<SubsidyPlan>> getAllSubsidyPlans() {
        log.info("获取所有补贴方案");
        
        LambdaQueryWrapper<SubsidyPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubsidyPlan::getStatus, 1)
                   .orderByDesc(SubsidyPlan::getCreateTime);
        
        List<SubsidyPlan> plans = subsidyPlanService.list(queryWrapper);
        return R.success(plans);
    }

    @ApiOperation("根据ID获取补贴方案")
    @GetMapping("/plans/{id}")
    public R<SubsidyPlan> getSubsidyPlanById(@PathVariable Long id) {
        log.info("根据ID获取补贴方案：id={}", id);
        
        SubsidyPlan plan = subsidyPlanService.getById(id);
        if (plan == null) {
            return R.error("补贴方案不存在");
        }
        
        return R.success(plan);
    }

    @ApiOperation("创建补贴方案")
    @PostMapping("/plans")
    public R<String> createSubsidyPlan(@RequestBody SubsidyPlan subsidyPlan) {
        log.info("创建补贴方案：{}", subsidyPlan);
        
        // 检查冲突
        if (subsidyPlanService.checkSubsidyConflict(subsidyPlan)) {
            return R.error("相同条件的补贴方案已存在，请检查补贴对象、周期和群体设置");
        }
        
        boolean success = subsidyPlanService.save(subsidyPlan);
        if (success) {
            return R.success("补贴方案创建成功");
        } else {
            return R.error("补贴方案创建失败");
        }
    }

    @ApiOperation("更新补贴方案")
    @PutMapping("/plans/{id}")
    public R<String> updateSubsidyPlan(@PathVariable Long id, @RequestBody SubsidyPlan subsidyPlan) {
        log.info("更新补贴方案：id={}, plan={}", id, subsidyPlan);
        
        SubsidyPlan existingPlan = subsidyPlanService.getById(id);
        if (existingPlan == null) {
            return R.error("补贴方案不存在");
        }
        
        subsidyPlan.setId(id);
        
        // 检查冲突
        if (subsidyPlanService.checkSubsidyConflict(subsidyPlan)) {
            return R.error("相同条件的补贴方案已存在，请检查补贴对象、周期和群体设置");
        }
        
        boolean success = subsidyPlanService.updateById(subsidyPlan);
        if (success) {
            return R.success("补贴方案更新成功");
        } else {
            return R.error("补贴方案更新失败");
        }
    }

    @ApiOperation("删除补贴方案")
    @DeleteMapping("/plans/{id}")
    public R<String> deleteSubsidyPlan(@PathVariable Long id) {
        log.info("删除补贴方案：id={}", id);
        
        SubsidyPlan plan = subsidyPlanService.getById(id);
        if (plan == null) {
            return R.error("补贴方案不存在");
        }
        
        boolean success = subsidyPlanService.removeById(id);
        if (success) {
            return R.success("补贴方案删除成功");
        } else {
            return R.error("补贴方案删除失败");
        }
    }

    @ApiOperation("启用/禁用补贴方案")
    @PutMapping("/plans/{id}/status")
    public R<String> toggleSubsidyPlanStatus(@PathVariable Long id) {
        log.info("切换补贴方案状态：id={}", id);
        
        SubsidyPlan plan = subsidyPlanService.getById(id);
        if (plan == null) {
            return R.error("补贴方案不存在");
        }
        
        plan.setStatus(plan.getStatus() == 1 ? 0 : 1);
        boolean success = subsidyPlanService.updateById(plan);
        
        if (success) {
            String status = plan.getStatus() == 1 ? "启用" : "禁用";
            return R.success("补贴方案" + status + "成功");
        } else {
            return R.error("操作失败");
        }
    }

    @ApiOperation("应用补贴方案到用户")
    @PostMapping("/plans/{id}/apply")
    public R<String> applySubsidyPlan(@PathVariable Long id) {
        log.info("应用补贴方案到用户：id={}", id);
        
        try {
            subsidyPlanService.applySubsidyToUsers(id);
            return R.success("补贴方案应用成功");
        } catch (Exception e) {
            log.error("应用补贴方案失败：", e);
            return R.error("应用补贴方案失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取所有学院列表")
    @GetMapping("/colleges")
    public R<List<String>> getAllColleges() {
        log.info("获取所有学院列表");
        
        List<String> colleges = subsidyPlanService.getAllColleges();
        return R.success(colleges);
    }

    @ApiOperation("获取所有部门列表")
    @GetMapping("/departments")
    public R<List<String>> getAllDepartments() {
        log.info("获取所有部门列表");
        
        List<String> departments = subsidyPlanService.getAllDepartments();
        return R.success(departments);
    }
} 