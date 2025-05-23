package com.campus.ordering.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("登录请求参数")
public class LoginDTO {
    
    @ApiModelProperty("角色:admin(管理员),staff(员工),student(学生)")
    private String role;
    
    @ApiModelProperty("用户名/学号/工号")
    private String username;
    
    @ApiModelProperty("密码")
    private String password;
} 