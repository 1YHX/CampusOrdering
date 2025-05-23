package com.campus.ordering.vo;

import lombok.Data;

@Data
public class RegisterVO {
    // 基本信息
    private String role;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;

    // 学生特有信息
    private String studentNo;
    private String college;
    private String major;
    private String className;
    private String grade;

    // 员工特有信息
    private String employeeNo;
    private String position;
    private String department;
} 