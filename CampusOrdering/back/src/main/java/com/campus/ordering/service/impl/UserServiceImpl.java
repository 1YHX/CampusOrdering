package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.ordering.common.CustomException;
import com.campus.ordering.dto.ChangePasswordDTO;
import com.campus.ordering.dto.UserProfileDTO;
import com.campus.ordering.entity.Admin;
import com.campus.ordering.entity.Staff;
import com.campus.ordering.entity.Student;
import com.campus.ordering.entity.User;
import com.campus.ordering.mapper.AdminMapper;
import com.campus.ordering.mapper.StaffMapper;
import com.campus.ordering.mapper.StudentMapper;
import com.campus.ordering.mapper.UserMapper;
import com.campus.ordering.service.UserService;
import com.campus.ordering.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private StaffMapper staffMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public User login(String username, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = this.getOne(queryWrapper);
        
        if (user == null) {
            throw new CustomException("用户不存在");
        }
        
        if (!user.getPassword().equals(password)) {
            throw new CustomException("密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new CustomException("账号已禁用");
        }
        
        return user;
    }
    
    @Override
    public User getUserProfile(Long userId) {
        // 从请求中获取用户角色
        String userRole = getCurrentUserRole();
        
        switch (userRole) {
            case "admin":
                Admin admin = adminMapper.selectById(userId);
                if (admin == null) {
                    throw new CustomException("用户不存在");
                }
                return convertToUser(admin, "admin");
                
            case "student":
                Student student = studentMapper.selectById(userId);
                if (student == null) {
                    throw new CustomException("用户不存在");
                }
                return convertToUser(student, "student");
                
            case "staff":
                Staff staff = staffMapper.selectById(userId);
                if (staff == null) {
                    throw new CustomException("用户不存在");
                }
                return convertToUser(staff, "staff");
                
            default:
                throw new CustomException("无效的用户角色");
        }
    }
    
    @Override
    public User getUserProfileByRole(Long userId, String userRole) {
        switch (userRole) {
            case "admin":
                Admin admin = adminMapper.selectById(userId);
                if (admin == null) {
                    throw new CustomException("用户不存在");
                }
                return convertToUser(admin, "admin");
                
            case "student":
                Student student = studentMapper.selectById(userId);
                if (student == null) {
                    throw new CustomException("用户不存在");
                }
                return convertToUser(student, "student");
                
            case "staff":
                Staff staff = staffMapper.selectById(userId);
                if (staff == null) {
                    throw new CustomException("用户不存在");
                }
                return convertToUser(staff, "staff");
                
            default:
                throw new CustomException("无效的用户角色");
        }
    }
    
    @Override
    public boolean updateUserProfile(Long userId, UserProfileDTO userProfileDTO) {
        String userRole = getCurrentUserRole();
        return updateUserProfileByRole(userId, userRole, userProfileDTO);
    }
    
    @Override
    public boolean updateUserProfileByRole(Long userId, String userRole, UserProfileDTO userProfileDTO) {
        switch (userRole) {
            case "admin":
                Admin admin = adminMapper.selectById(userId);
                if (admin == null) {
                    throw new CustomException("用户不存在");
                }
                admin.setRealName(userProfileDTO.getRealName());
                admin.setPhone(userProfileDTO.getPhone());
                admin.setEmail(userProfileDTO.getEmail());
                admin.setUpdateTime(LocalDateTime.now());
                return adminMapper.updateById(admin) > 0;
                
            case "student":
                Student student = studentMapper.selectById(userId);
                if (student == null) {
                    throw new CustomException("用户不存在");
                }
                student.setRealName(userProfileDTO.getRealName());
                student.setPhone(userProfileDTO.getPhone());
                student.setEmail(userProfileDTO.getEmail());
                // 对于学生，department字段可以映射到college
                if (userProfileDTO.getDepartment() != null) {
                    student.setCollege(userProfileDTO.getDepartment());
                }
                student.setUpdateTime(LocalDateTime.now());
                return studentMapper.updateById(student) > 0;
                
            case "staff":
                Staff staff = staffMapper.selectById(userId);
                if (staff == null) {
                    throw new CustomException("用户不存在");
                }
                staff.setRealName(userProfileDTO.getRealName());
                staff.setPhone(userProfileDTO.getPhone());
                staff.setEmail(userProfileDTO.getEmail());
                if (userProfileDTO.getDepartment() != null) {
                    staff.setDepartment(userProfileDTO.getDepartment());
                }
                staff.setUpdateTime(LocalDateTime.now());
                return staffMapper.updateById(staff) > 0;
                
            default:
                throw new CustomException("无效的用户角色");
        }
    }
    
    @Override
    public boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        String userRole = getCurrentUserRole();
        return changePasswordByRole(userId, userRole, changePasswordDTO);
    }
    
    @Override
    public boolean changePasswordByRole(Long userId, String userRole, ChangePasswordDTO changePasswordDTO) {
        // 验证新密码确认
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new CustomException("两次输入的新密码不一致");
        }
        
        switch (userRole) {
            case "admin":
                Admin admin = adminMapper.selectById(userId);
                if (admin == null) {
                    throw new CustomException("用户不存在");
                }
                if (!admin.getPassword().equals(changePasswordDTO.getOldPassword())) {
                    throw new CustomException("原密码错误");
                }
                admin.setPassword(changePasswordDTO.getNewPassword());
                admin.setUpdateTime(LocalDateTime.now());
                return adminMapper.updateById(admin) > 0;
                
            case "student":
                Student student = studentMapper.selectById(userId);
                if (student == null) {
                    throw new CustomException("用户不存在");
                }
                if (!student.getPassword().equals(changePasswordDTO.getOldPassword())) {
                    throw new CustomException("原密码错误");
                }
                student.setPassword(changePasswordDTO.getNewPassword());
                student.setUpdateTime(LocalDateTime.now());
                return studentMapper.updateById(student) > 0;
                
            case "staff":
                Staff staff = staffMapper.selectById(userId);
                if (staff == null) {
                    throw new CustomException("用户不存在");
                }
                if (!staff.getPassword().equals(changePasswordDTO.getOldPassword())) {
                    throw new CustomException("原密码错误");
                }
                staff.setPassword(changePasswordDTO.getNewPassword());
                staff.setUpdateTime(LocalDateTime.now());
                return staffMapper.updateById(staff) > 0;
                
            default:
                throw new CustomException("无效的用户角色");
        }
    }
    
    /**
     * 将具体实体转换为User对象
     */
    private User convertToUser(Object entity, String role) {
        User user = new User();
        
        if ("admin".equals(role)) {
            Admin admin = (Admin) entity;
            user.setId(admin.getId());
            user.setUsername(admin.getUsername());
            user.setRealName(admin.getRealName());
            user.setPhone(admin.getPhone());
            user.setEmail(admin.getEmail());
            user.setRole(role);
            user.setDepartment("系统管理部"); // 管理员默认部门
            user.setStatus(admin.getStatus());
            user.setCreateTime(admin.getCreateTime());
            user.setUpdateTime(admin.getUpdateTime());
        } else if ("student".equals(role)) {
            Student student = (Student) entity;
            user.setId(student.getId());
            user.setUsername(student.getUsername());
            user.setRealName(student.getRealName());
            user.setPhone(student.getPhone());
            user.setEmail(student.getEmail());
            user.setRole(role);
            user.setDepartment(student.getCollege()); // 学生的学院作为部门
            user.setStatus(student.getStatus());
            user.setCreateTime(student.getCreateTime());
            user.setUpdateTime(student.getUpdateTime());
        } else if ("staff".equals(role)) {
            Staff staff = (Staff) entity;
            user.setId(staff.getId());
            user.setUsername(staff.getUsername());
            user.setRealName(staff.getRealName());
            user.setPhone(staff.getPhone());
            user.setEmail(staff.getEmail());
            user.setRole(role);
            user.setDepartment(staff.getDepartment());
            user.setStatus(staff.getStatus());
            user.setCreateTime(staff.getCreateTime());
            user.setUpdateTime(staff.getUpdateTime());
        }
        
        // 清空密码字段
        user.setPassword(null);
        return user;
    }
    
    /**
     * 获取当前用户角色（这里先简化处理，实际应该从请求上下文获取）
     */
    private String getCurrentUserRole() {
        // TODO: 从当前请求上下文或token中获取用户角色
        // 暂时返回admin，后续需要完善
        return "admin";
    }
}
