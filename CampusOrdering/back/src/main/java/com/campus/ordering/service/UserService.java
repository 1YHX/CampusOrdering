package com.campus.ordering.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.ordering.dto.ChangePasswordDTO;
import com.campus.ordering.dto.UserProfileDTO;
import com.campus.ordering.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);
    
    /**
     * 获取用户个人信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserProfile(Long userId);
    
    /**
     * 根据角色获取用户个人信息
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return 用户信息
     */
    User getUserProfileByRole(Long userId, String userRole);
    
    /**
     * 更新用户个人信息
     * @param userId 用户ID
     * @param userProfileDTO 用户信息DTO
     * @return 更新结果
     */
    boolean updateUserProfile(Long userId, UserProfileDTO userProfileDTO);
    
    /**
     * 根据角色更新用户个人信息
     * @param userId 用户ID
     * @param userRole 用户角色
     * @param userProfileDTO 用户信息DTO
     * @return 更新结果
     */
    boolean updateUserProfileByRole(Long userId, String userRole, UserProfileDTO userProfileDTO);
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param changePasswordDTO 修改密码DTO
     * @return 修改结果
     */
    boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO);
    
    /**
     * 根据角色修改密码
     * @param userId 用户ID
     * @param userRole 用户角色
     * @param changePasswordDTO 修改密码DTO
     * @return 修改结果
     */
    boolean changePasswordByRole(Long userId, String userRole, ChangePasswordDTO changePasswordDTO);
}
