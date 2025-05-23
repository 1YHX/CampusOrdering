package com.campus.ordering.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel("修改密码DTO")
public class ChangePasswordDTO {
    
    @ApiModelProperty("原密码")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    @ApiModelProperty("新密码")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String newPassword;
    
    @ApiModelProperty("确认新密码")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
} 