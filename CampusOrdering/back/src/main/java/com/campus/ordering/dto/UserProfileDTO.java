package com.campus.ordering.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("用户个人信息DTO")
public class UserProfileDTO {
    
    @ApiModelProperty("真实姓名")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    @ApiModelProperty("手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @ApiModelProperty("邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @ApiModelProperty("部门/院系")
    private String department;
} 