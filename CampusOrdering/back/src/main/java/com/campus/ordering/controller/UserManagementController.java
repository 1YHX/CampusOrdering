package com.campus.ordering.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.ordering.common.R;
import com.campus.ordering.service.UserManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/admin/user-management")
@Api(tags = "用户管理")
@CrossOrigin
public class UserManagementController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @GetMapping
    @ApiOperation("分页查询所有用户")
    public R<IPage<Map<String, Object>>> pageQueryUsers(
            @ApiParam("页码") @RequestParam(defaultValue = "1") int page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") int size,
            @ApiParam("用户类型") @RequestParam(required = false) String userType,
            @ApiParam("搜索关键字") @RequestParam(required = false) String keyword
    ) {
        try {
            IPage<Map<String, Object>> result = userManagementService.pageQueryAllUsers(page, size, userType, keyword);
            return R.success(result);
        } catch (Exception e) {
            return R.error("查询用户列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{userType}/{userId}")
    @ApiOperation("获取用户详情")
    public R<Map<String, Object>> getUserDetail(
            @ApiParam("用户类型") @PathVariable String userType,
            @ApiParam("用户ID") @PathVariable Long userId
    ) {
        try {
            Map<String, Object> user = userManagementService.getUserDetail(userType, userId);
            return R.success(user);
        } catch (Exception e) {
            return R.error("获取用户详情失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/{userType}")
    @ApiOperation("创建用户")
    public R<String> createUser(
            @ApiParam("用户类型") @PathVariable String userType,
            @RequestBody Map<String, Object> userData
    ) {
        try {
            boolean success = userManagementService.createUser(userType, userData);
            if (success) {
                return R.success("用户创建成功");
            } else {
                return R.error("用户创建失败");
            }
        } catch (Exception e) {
            return R.error("用户创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{userType}/{userId}")
    @ApiOperation("更新用户信息")
    public R<String> updateUser(
            @ApiParam("用户类型") @PathVariable String userType,
            @ApiParam("用户ID") @PathVariable Long userId,
            @RequestBody Map<String, Object> userData
    ) {
        try {
            boolean success = userManagementService.updateUser(userType, userId, userData);
            if (success) {
                return R.success("用户更新成功");
            } else {
                return R.error("用户更新失败");
            }
        } catch (Exception e) {
            return R.error("用户更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{userType}/{userId}")
    @ApiOperation("删除用户")
    public R<String> deleteUser(
            @ApiParam("用户类型") @PathVariable String userType,
            @ApiParam("用户ID") @PathVariable Long userId
    ) {
        try {
            boolean success = userManagementService.deleteUser(userType, userId);
            if (success) {
                return R.success("用户删除成功");
            } else {
                return R.error("用户删除失败");
            }
        } catch (Exception e) {
            return R.error("用户删除失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{userType}/{userId}/status")
    @ApiOperation("更新用户状态")
    public R<String> updateUserStatus(
            @ApiParam("用户类型") @PathVariable String userType,
            @ApiParam("用户ID") @PathVariable Long userId,
            @RequestBody Map<String, Object> statusData
    ) {
        try {
            Integer status = (Integer) statusData.get("status");
            boolean success = userManagementService.updateUserStatus(userType, userId, status);
            if (success) {
                return R.success("用户状态更新成功");
            } else {
                return R.error("用户状态更新失败");
            }
        } catch (Exception e) {
            return R.error("用户状态更新失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{userType}/{userId}/password")
    @ApiOperation("重置用户密码")
    public R<String> resetPassword(
            @ApiParam("用户类型") @PathVariable String userType,
            @ApiParam("用户ID") @PathVariable Long userId,
            @RequestBody Map<String, Object> passwordData
    ) {
        try {
            String newPassword = (String) passwordData.getOrDefault("password", "123456");
            boolean success = userManagementService.resetPassword(userType, userId, newPassword);
            if (success) {
                return R.success("密码重置成功");
            } else {
                return R.error("密码重置失败");
            }
        } catch (Exception e) {
            return R.error("密码重置失败: " + e.getMessage());
        }
    }
} 