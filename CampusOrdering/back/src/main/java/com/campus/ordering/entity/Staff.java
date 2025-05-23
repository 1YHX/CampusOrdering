package com.campus.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("staff")
public class Staff {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String phone;
    
    private String email;
    
    private String position;
    
    private String department;
    
    private String employeeNo;
    
    private Integer status;
    
    private LocalDateTime lastLoginTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 