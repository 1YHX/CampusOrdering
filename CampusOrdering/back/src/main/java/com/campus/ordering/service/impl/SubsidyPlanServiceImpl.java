package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.ordering.entity.Staff;
import com.campus.ordering.entity.Student;
import com.campus.ordering.entity.SubsidyAccount;
import com.campus.ordering.entity.SubsidyPlan;
import com.campus.ordering.mapper.SubsidyPlanMapper;
import com.campus.ordering.service.StaffService;
import com.campus.ordering.service.StudentService;
import com.campus.ordering.service.SubsidyAccountService;
import com.campus.ordering.service.SubsidyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 补贴方案服务实现类
 */
@Service
public class SubsidyPlanServiceImpl extends ServiceImpl<SubsidyPlanMapper, SubsidyPlan> implements SubsidyPlanService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private SubsidyAccountService subsidyAccountService;

    @Override
    public List<String> getAllColleges() {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Student::getCollege);
        return studentService.list(queryWrapper).stream()
                .map(Student::getCollege)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllDepartments() {
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Staff::getDepartment);
        return staffService.list(queryWrapper).stream()
                .map(Staff::getDepartment)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void applySubsidyToUsers(Long subsidyId) {
        SubsidyPlan subsidyPlan = getById(subsidyId);
        if (subsidyPlan == null || subsidyPlan.getStatus() != 1) {
            throw new RuntimeException("补贴方案不存在或已禁用");
        }

        if ("student".equals(subsidyPlan.getTargetType())) {
            applyToStudents(subsidyPlan);
        } else if ("staff".equals(subsidyPlan.getTargetType())) {
            applyToStaff(subsidyPlan);
        }
    }

    private void applyToStudents(SubsidyPlan subsidyPlan) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getStatus, 1);

        // 根据补贴群体筛选
        if ("college".equals(subsidyPlan.getTargetGroup()) && subsidyPlan.getGroupValue() != null) {
            queryWrapper.eq(Student::getCollege, subsidyPlan.getGroupValue());
        }

        List<Student> students = studentService.list(queryWrapper);
        for (Student student : students) {
            createOrUpdateSubsidyAccount(subsidyPlan, "student", student.getId());
        }
    }

    private void applyToStaff(SubsidyPlan subsidyPlan) {
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Staff::getStatus, 1);

        // 根据补贴群体筛选
        if ("department".equals(subsidyPlan.getTargetGroup()) && subsidyPlan.getGroupValue() != null) {
            queryWrapper.eq(Staff::getDepartment, subsidyPlan.getGroupValue());
        }

        List<Staff> staffList = staffService.list(queryWrapper);
        for (Staff staff : staffList) {
            createOrUpdateSubsidyAccount(subsidyPlan, "staff", staff.getId());
        }
    }

    private void createOrUpdateSubsidyAccount(SubsidyPlan subsidyPlan, String userType, Long userId) {
        LambdaQueryWrapper<SubsidyAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubsidyAccount::getUserType, userType)
                   .eq(SubsidyAccount::getUserId, userId)
                   .eq(SubsidyAccount::getSubsidyId, subsidyPlan.getId());

        SubsidyAccount account = subsidyAccountService.getOne(queryWrapper);
        if (account == null) {
            // 创建新账户
            account = new SubsidyAccount();
            account.setUserType(userType);
            account.setUserId(userId);
            account.setSubsidyId(subsidyPlan.getId());
            account.setBalance(subsidyPlan.getAmount());
            account.setTotalReceived(subsidyPlan.getAmount());
            account.setTotalUsed(BigDecimal.ZERO);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            subsidyAccountService.save(account);
        } else {
            // 更新现有账户
            account.setBalance(account.getBalance().add(subsidyPlan.getAmount()));
            account.setTotalReceived(account.getTotalReceived().add(subsidyPlan.getAmount()));
            account.setUpdateTime(LocalDateTime.now());
            subsidyAccountService.updateById(account);
        }
    }

    @Override
    public List<SubsidyPlan> getUserValidSubsidies(String userType, Long userId) {
        LambdaQueryWrapper<SubsidyPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubsidyPlan::getStatus, 1)
                   .eq(SubsidyPlan::getTargetType, userType);

        List<SubsidyPlan> allPlans = list(queryWrapper);
        
        // 进一步筛选适用的补贴方案
        return allPlans.stream()
                .filter(plan -> isUserEligible(plan, userType, userId))
                .collect(Collectors.toList());
    }

    private boolean isUserEligible(SubsidyPlan plan, String userType, Long userId) {
        if (plan.getTargetGroup() == null) {
            return true; // 通用补贴
        }

        if ("student".equals(userType)) {
            Student student = studentService.getById(userId);
            if (student == null) return false;

            if ("college".equals(plan.getTargetGroup())) {
                return plan.getGroupValue().equals(student.getCollege());
            }
        } else if ("staff".equals(userType)) {
            Staff staff = staffService.getById(userId);
            if (staff == null) return false;

            if ("department".equals(plan.getTargetGroup())) {
                return plan.getGroupValue().equals(staff.getDepartment());
            }
        }

        return false;
    }

    @Override
    public boolean checkSubsidyConflict(SubsidyPlan subsidyPlan) {
        LambdaQueryWrapper<SubsidyPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubsidyPlan::getTargetType, subsidyPlan.getTargetType())
                   .eq(SubsidyPlan::getPeriod, subsidyPlan.getPeriod())
                   .eq(SubsidyPlan::getStatus, 1);

        if (subsidyPlan.getId() != null) {
            queryWrapper.ne(SubsidyPlan::getId, subsidyPlan.getId());
        }

        if (subsidyPlan.getTargetGroup() != null) {
            queryWrapper.eq(SubsidyPlan::getTargetGroup, subsidyPlan.getTargetGroup())
                       .eq(SubsidyPlan::getGroupValue, subsidyPlan.getGroupValue());
        } else {
            queryWrapper.isNull(SubsidyPlan::getTargetGroup);
        }

        return count(queryWrapper) > 0;
    }
} 