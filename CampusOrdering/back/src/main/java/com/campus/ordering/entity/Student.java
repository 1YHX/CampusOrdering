package com.campus.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student")
public class Student {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String studentNo;
    
    private String phone;
    
    private String email;
    
    private String college;
    
    private String major;
    
    private String className;
    
    private String grade;
    
    private Integer status;
    
    private LocalDateTime lastLoginTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 