package com.campus.ordering.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.Map;

/**
 * 用户管理服务接口
 */
public interface UserManagementService {
    
    /**
     * 分页查询所有用户
     */
    IPage<Map<String, Object>> pageQueryAllUsers(int page, int size, String userType, String keyword);
    
    /**
     * 根据用户类型和ID获取用户详情
     */
    Map<String, Object> getUserDetail(String userType, Long userId);
    
    /**
     * 创建用户
     */
    boolean createUser(String userType, Map<String, Object> userData);
    
    /**
     * 更新用户信息
     */
    boolean updateUser(String userType, Long userId, Map<String, Object> userData);
    
    /**
     * 删除用户
     */
    boolean deleteUser(String userType, Long userId);
    
    /**
     * 启用/禁用用户
     */
    boolean updateUserStatus(String userType, Long userId, Integer status);
    
    /**
     * 重置用户密码
     */
    boolean resetPassword(String userType, Long userId, String newPassword);
} 