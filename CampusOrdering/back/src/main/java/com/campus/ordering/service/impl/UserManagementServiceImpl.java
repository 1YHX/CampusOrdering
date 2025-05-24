package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.ordering.common.CustomException;
import com.campus.ordering.entity.Admin;
import com.campus.ordering.entity.Staff;
import com.campus.ordering.entity.Student;
import com.campus.ordering.service.AdminService;
import com.campus.ordering.service.StaffService;
import com.campus.ordering.service.StudentService;
import com.campus.ordering.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户管理服务实现类
 */
@Service
public class UserManagementServiceImpl implements UserManagementService {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private StaffService staffService;
    
    @Autowired
    private AdminService adminService;
    
    @Override
    public IPage<Map<String, Object>> pageQueryAllUsers(int page, int size, String userType, String keyword) {
        Page<Map<String, Object>> resultPage = new Page<>(page, size);
        List<Map<String, Object>> allUsers = new ArrayList<>();
        
        // 根据用户类型筛选
        if (userType == null || "student".equals(userType)) {
            List<Student> students = getStudents(keyword);
            for (Student student : students) {
                Map<String, Object> userMap = convertStudentToMap(student);
                allUsers.add(userMap);
            }
        }
        
        if (userType == null || "staff".equals(userType)) {
            List<Staff> staffList = getStaffList(keyword);
            for (Staff staff : staffList) {
                Map<String, Object> userMap = convertStaffToMap(staff);
                allUsers.add(userMap);
            }
        }
        
        if (userType == null || "admin".equals(userType)) {
            List<Admin> admins = getAdmins(keyword);
            for (Admin admin : admins) {
                Map<String, Object> userMap = convertAdminToMap(admin);
                allUsers.add(userMap);
            }
        }
        
        // 排序 - 按创建时间倒序
        allUsers.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("createTime");
            LocalDateTime timeB = (LocalDateTime) b.get("createTime");
            return timeB.compareTo(timeA);
        });
        
        // 手动分页
        int total = allUsers.size();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, total);
        
        List<Map<String, Object>> pageData = new ArrayList<>();
        if (startIndex < total) {
            pageData = allUsers.subList(startIndex, endIndex);
        }
        
        resultPage.setRecords(pageData);
        resultPage.setTotal(total);
        
        return resultPage;
    }
    
    @Override
    public Map<String, Object> getUserDetail(String userType, Long userId) {
        switch (userType) {
            case "student":
                Student student = studentService.getById(userId);
                if (student == null) {
                    throw new CustomException("学生不存在");
                }
                return convertStudentToMap(student);
                
            case "staff":
                Staff staff = staffService.getById(userId);
                if (staff == null) {
                    throw new CustomException("员工不存在");
                }
                return convertStaffToMap(staff);
                
            case "admin":
                Admin admin = adminService.getById(userId);
                if (admin == null) {
                    throw new CustomException("管理员不存在");
                }
                return convertAdminToMap(admin);
                
            default:
                throw new CustomException("无效的用户类型");
        }
    }
    
    @Override
    public boolean createUser(String userType, Map<String, Object> userData) {
        switch (userType) {
            case "student":
                return createStudent(userData);
            case "staff":
                return createStaff(userData);
            case "admin":
                return createAdmin(userData);
            default:
                throw new CustomException("无效的用户类型");
        }
    }
    
    @Override
    public boolean updateUser(String userType, Long userId, Map<String, Object> userData) {
        switch (userType) {
            case "student":
                return updateStudent(userId, userData);
            case "staff":
                return updateStaff(userId, userData);
            case "admin":
                return updateAdmin(userId, userData);
            default:
                throw new CustomException("无效的用户类型");
        }
    }
    
    @Override
    public boolean deleteUser(String userType, Long userId) {
        switch (userType) {
            case "student":
                return studentService.removeById(userId);
            case "staff":
                return staffService.removeById(userId);
            case "admin":
                return adminService.removeById(userId);
            default:
                throw new CustomException("无效的用户类型");
        }
    }
    
    @Override
    public boolean updateUserStatus(String userType, Long userId, Integer status) {
        switch (userType) {
            case "student":
                Student student = studentService.getById(userId);
                if (student == null) return false;
                student.setStatus(status);
                student.setUpdateTime(LocalDateTime.now());
                return studentService.updateById(student);
                
            case "staff":
                Staff staff = staffService.getById(userId);
                if (staff == null) return false;
                staff.setStatus(status);
                staff.setUpdateTime(LocalDateTime.now());
                return staffService.updateById(staff);
                
            case "admin":
                Admin admin = adminService.getById(userId);
                if (admin == null) return false;
                admin.setStatus(status);
                admin.setUpdateTime(LocalDateTime.now());
                return adminService.updateById(admin);
                
            default:
                throw new CustomException("无效的用户类型");
        }
    }
    
    @Override
    public boolean resetPassword(String userType, Long userId, String newPassword) {
        switch (userType) {
            case "student":
                Student student = studentService.getById(userId);
                if (student == null) return false;
                student.setPassword(newPassword);
                student.setUpdateTime(LocalDateTime.now());
                return studentService.updateById(student);
                
            case "staff":
                Staff staff = staffService.getById(userId);
                if (staff == null) return false;
                staff.setPassword(newPassword);
                staff.setUpdateTime(LocalDateTime.now());
                return staffService.updateById(staff);
                
            case "admin":
                Admin admin = adminService.getById(userId);
                if (admin == null) return false;
                admin.setPassword(newPassword);
                admin.setUpdateTime(LocalDateTime.now());
                return adminService.updateById(admin);
                
            default:
                throw new CustomException("无效的用户类型");
        }
    }
    
    // 私有方法
    private List<Student> getStudents(String keyword) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Student::getUsername, keyword)
                .or().like(Student::getRealName, keyword)
                .or().like(Student::getStudentNo, keyword)
                .or().like(Student::getPhone, keyword)
                .or().like(Student::getEmail, keyword)
            );
        }
        queryWrapper.orderByDesc(Student::getCreateTime);
        return studentService.list(queryWrapper);
    }
    
    private List<Staff> getStaffList(String keyword) {
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Staff::getUsername, keyword)
                .or().like(Staff::getRealName, keyword)
                .or().like(Staff::getEmployeeNo, keyword)
                .or().like(Staff::getPhone, keyword)
                .or().like(Staff::getEmail, keyword)
            );
        }
        queryWrapper.orderByDesc(Staff::getCreateTime);
        return staffService.list(queryWrapper);
    }
    
    private List<Admin> getAdmins(String keyword) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Admin::getUsername, keyword)
                .or().like(Admin::getRealName, keyword)
                .or().like(Admin::getPhone, keyword)
                .or().like(Admin::getEmail, keyword)
            );
        }
        queryWrapper.orderByDesc(Admin::getCreateTime);
        return adminService.list(queryWrapper);
    }
    
    private Map<String, Object> convertStudentToMap(Student student) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", student.getId());
        map.put("username", student.getUsername());
        map.put("realName", student.getRealName());
        map.put("phone", student.getPhone());
        map.put("email", student.getEmail());
        map.put("userType", "student");
        map.put("userTypeText", "学生");
        map.put("status", student.getStatus());
        map.put("createTime", student.getCreateTime());
        map.put("updateTime", student.getUpdateTime());
        
        // 学生特有字段
        map.put("studentNo", student.getStudentNo());
        map.put("college", student.getCollege());
        map.put("major", student.getMajor());
        map.put("className", student.getClassName());
        map.put("grade", student.getGrade());
        
        return map;
    }
    
    private Map<String, Object> convertStaffToMap(Staff staff) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", staff.getId());
        map.put("username", staff.getUsername());
        map.put("realName", staff.getRealName());
        map.put("phone", staff.getPhone());
        map.put("email", staff.getEmail());
        map.put("userType", "staff");
        map.put("userTypeText", "员工");
        map.put("status", staff.getStatus());
        map.put("createTime", staff.getCreateTime());
        map.put("updateTime", staff.getUpdateTime());
        
        // 员工特有字段
        map.put("employeeNo", staff.getEmployeeNo());
        map.put("position", staff.getPosition());
        map.put("department", staff.getDepartment());
        
        return map;
    }
    
    private Map<String, Object> convertAdminToMap(Admin admin) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", admin.getId());
        map.put("username", admin.getUsername());
        map.put("realName", admin.getRealName());
        map.put("phone", admin.getPhone());
        map.put("email", admin.getEmail());
        map.put("userType", "admin");
        map.put("userTypeText", "管理员");
        map.put("status", admin.getStatus());
        map.put("createTime", admin.getCreateTime());
        map.put("updateTime", admin.getUpdateTime());
        
        return map;
    }
    
    private boolean createStudent(Map<String, Object> userData) {
        Student student = new Student();
        student.setUsername((String) userData.get("username"));
        student.setPassword((String) userData.getOrDefault("password", "123456"));
        student.setRealName((String) userData.get("realName"));
        student.setPhone((String) userData.get("phone"));
        student.setEmail((String) userData.get("email"));
        student.setStudentNo((String) userData.get("studentNo"));
        student.setCollege((String) userData.get("college"));
        student.setMajor((String) userData.get("major"));
        student.setClassName((String) userData.get("className"));
        student.setGrade((String) userData.get("grade"));
        student.setStatus(1);
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        
        return studentService.save(student);
    }
    
    private boolean createStaff(Map<String, Object> userData) {
        Staff staff = new Staff();
        staff.setUsername((String) userData.get("username"));
        staff.setPassword((String) userData.getOrDefault("password", "123456"));
        staff.setRealName((String) userData.get("realName"));
        staff.setPhone((String) userData.get("phone"));
        staff.setEmail((String) userData.get("email"));
        staff.setEmployeeNo((String) userData.get("employeeNo"));
        staff.setPosition((String) userData.get("position"));
        staff.setDepartment((String) userData.get("department"));
        staff.setStatus(1);
        staff.setCreateTime(LocalDateTime.now());
        staff.setUpdateTime(LocalDateTime.now());
        
        return staffService.save(staff);
    }
    
    private boolean createAdmin(Map<String, Object> userData) {
        Admin admin = new Admin();
        admin.setUsername((String) userData.get("username"));
        admin.setPassword((String) userData.getOrDefault("password", "123456"));
        admin.setRealName((String) userData.get("realName"));
        admin.setPhone((String) userData.get("phone"));
        admin.setEmail((String) userData.get("email"));
        admin.setStatus(1);
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        
        return adminService.save(admin);
    }
    
    private boolean updateStudent(Long userId, Map<String, Object> userData) {
        Student student = studentService.getById(userId);
        if (student == null) return false;
        
        if (userData.containsKey("username")) student.setUsername((String) userData.get("username"));
        if (userData.containsKey("realName")) student.setRealName((String) userData.get("realName"));
        if (userData.containsKey("phone")) student.setPhone((String) userData.get("phone"));
        if (userData.containsKey("email")) student.setEmail((String) userData.get("email"));
        if (userData.containsKey("studentNo")) student.setStudentNo((String) userData.get("studentNo"));
        if (userData.containsKey("college")) student.setCollege((String) userData.get("college"));
        if (userData.containsKey("major")) student.setMajor((String) userData.get("major"));
        if (userData.containsKey("className")) student.setClassName((String) userData.get("className"));
        if (userData.containsKey("grade")) student.setGrade((String) userData.get("grade"));
        
        student.setUpdateTime(LocalDateTime.now());
        
        return studentService.updateById(student);
    }
    
    private boolean updateStaff(Long userId, Map<String, Object> userData) {
        Staff staff = staffService.getById(userId);
        if (staff == null) return false;
        
        if (userData.containsKey("username")) staff.setUsername((String) userData.get("username"));
        if (userData.containsKey("realName")) staff.setRealName((String) userData.get("realName"));
        if (userData.containsKey("phone")) staff.setPhone((String) userData.get("phone"));
        if (userData.containsKey("email")) staff.setEmail((String) userData.get("email"));
        if (userData.containsKey("employeeNo")) staff.setEmployeeNo((String) userData.get("employeeNo"));
        if (userData.containsKey("position")) staff.setPosition((String) userData.get("position"));
        if (userData.containsKey("department")) staff.setDepartment((String) userData.get("department"));
        
        staff.setUpdateTime(LocalDateTime.now());
        
        return staffService.updateById(staff);
    }
    
    private boolean updateAdmin(Long userId, Map<String, Object> userData) {
        Admin admin = adminService.getById(userId);
        if (admin == null) return false;
        
        if (userData.containsKey("username")) admin.setUsername((String) userData.get("username"));
        if (userData.containsKey("realName")) admin.setRealName((String) userData.get("realName"));
        if (userData.containsKey("phone")) admin.setPhone((String) userData.get("phone"));
        if (userData.containsKey("email")) admin.setEmail((String) userData.get("email"));
        
        admin.setUpdateTime(LocalDateTime.now());
        
        return adminService.updateById(admin);
    }
} 