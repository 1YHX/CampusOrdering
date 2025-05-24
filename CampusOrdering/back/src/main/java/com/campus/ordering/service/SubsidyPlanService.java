package com.campus.ordering.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.ordering.entity.SubsidyPlan;

import java.util.List;
import java.util.Map;

/**
 * 补贴方案服务接口
 */
public interface SubsidyPlanService extends IService<SubsidyPlan> {

    /**
     * 获取所有学院列表
     */
    List<String> getAllColleges();

    /**
     * 获取所有部门列表
     */
    List<String> getAllDepartments();

    /**
     * 批量应用补贴方案到用户
     */
    void applySubsidyToUsers(Long subsidyId);

    /**
     * 获取用户的有效补贴方案
     */
    List<SubsidyPlan> getUserValidSubsidies(String userType, Long userId);

    /**
     * 检查补贴方案冲突
     */
    boolean checkSubsidyConflict(SubsidyPlan subsidyPlan);
} 