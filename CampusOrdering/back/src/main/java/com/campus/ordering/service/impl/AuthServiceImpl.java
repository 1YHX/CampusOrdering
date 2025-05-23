package com.campus.ordering.service.impl;

import com.campus.ordering.common.Result;
import com.campus.ordering.entity.Student;
import com.campus.ordering.entity.Staff;
import com.campus.ordering.entity.Admin;
import com.campus.ordering.mapper.StudentMapper;
import com.campus.ordering.mapper.StaffMapper;
import com.campus.ordering.mapper.AdminMapper;
import com.campus.ordering.service.AuthService;
import com.campus.ordering.utils.JwtUtil;
import com.campus.ordering.vo.LoginVO;
import com.campus.ordering.vo.RegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Result login(LoginVO loginVO) {
        String role = loginVO.getRole();
        String username = loginVO.getUsername();
        String password = loginVO.getPassword();

        // 根据角色查询用户
        Long userId = null;
        String realName = null;
        String actualUsername = null;

        switch (role) {
            case "student":
                Student student = studentMapper.findByStudentNo(username);
                if (student == null) {
                    // 如果学号找不到，尝试用用户名查找
                    student = studentMapper.findByUsername(username);
                }
                if (student != null && password.equals(student.getPassword())) {
                    userId = student.getId();
                    realName = student.getRealName();
                    actualUsername = student.getUsername();
                }
                break;
            case "staff":
                Staff staff = staffMapper.findByEmployeeNo(username);
                if (staff == null) {
                    // 如果工号找不到，尝试用用户名查找
                    staff = staffMapper.findByUsername(username);
                }
                if (staff != null && password.equals(staff.getPassword())) {
                    userId = staff.getId();
                    realName = staff.getRealName();
                    actualUsername = staff.getUsername();
                }
                break;
            case "admin":
                Admin admin = adminMapper.findByUsername(username);
                if (admin != null && password.equals(admin.getPassword())) {
                    userId = admin.getId();
                    realName = admin.getRealName();
                    actualUsername = admin.getUsername();
                }
                break;
            default:
                return Result.error("无效的用户角色");
        }

        if (userId == null) {
            return Result.error("用户名或密码错误");
        }

        // 生成token
        String token = jwtUtil.generateToken(userId, actualUsername, role);

        // 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("username", actualUsername);
        user.put("role", role);
        user.put("realName", realName);
        data.put("user", user);

        return Result.success(data);
    }

    @Override
    @Transactional
    public Result register(RegisterVO registerVO) {
        String role = registerVO.getRole();
        String username = registerVO.getUsername();

        // 检查角色是否有效
        if (!"student".equals(role) && !"staff".equals(role)) {
            return Result.error("无效的用户角色");
        }

        // 检查用户名是否已存在
        if ("student".equals(role)) {
            if (studentMapper.findByUsername(username) != null) {
                return Result.error("用户名已存在");
            }
            if (studentMapper.findByStudentNo(registerVO.getStudentNo()) != null) {
                return Result.error("学号已存在");
            }
        } else {
            if (staffMapper.findByUsername(username) != null) {
                return Result.error("用户名已存在");
            }
            if (staffMapper.findByEmployeeNo(registerVO.getEmployeeNo()) != null) {
                return Result.error("工号已存在");
            }
        }

        // 根据角色注册用户
        try {
            if ("student".equals(role)) {
                Student student = new Student();
                student.setUsername(registerVO.getUsername());
                student.setPassword(registerVO.getPassword());
                student.setRealName(registerVO.getRealName());
                student.setPhone(registerVO.getPhone());
                student.setEmail(registerVO.getEmail());
                student.setStudentNo(registerVO.getStudentNo());
                student.setCollege(registerVO.getCollege());
                student.setMajor(registerVO.getMajor());
                student.setClassName(registerVO.getClassName());
                student.setGrade(registerVO.getGrade());
                studentMapper.insert(student);
            } else {
                Staff staff = new Staff();
                staff.setUsername(registerVO.getUsername());
                staff.setPassword(registerVO.getPassword());
                staff.setRealName(registerVO.getRealName());
                staff.setPhone(registerVO.getPhone());
                staff.setEmail(registerVO.getEmail());
                staff.setEmployeeNo(registerVO.getEmployeeNo());
                staff.setPosition(registerVO.getPosition());
                staff.setDepartment(registerVO.getDepartment());
                staffMapper.insert(staff);
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }
} 