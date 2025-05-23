package com.campus.ordering.controller;

import com.campus.ordering.common.R;
import com.campus.ordering.dto.ChangePasswordDTO;
import com.campus.ordering.dto.LoginDTO;
import com.campus.ordering.dto.UserProfileDTO;
import com.campus.ordering.entity.User;
import com.campus.ordering.service.UserService;
import com.campus.ordering.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        log.info("用户登录：username={}", loginDTO.getUsername());
        
        // 登录
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        
        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);
        
        return R.success(map);
    }
    
    @ApiOperation("获取个人信息")
    @GetMapping("/profile")
    public R<User> getUserProfile(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        String userRole = getCurrentUserRole(request);
        log.info("获取个人信息：userId={}, role={}", userId, userRole);
        
        User user = userService.getUserProfileByRole(userId, userRole);
        return R.success(user);
    }
    
    @ApiOperation("更新个人信息")
    @PutMapping("/profile")
    public R<String> updateUserProfile(@Valid @RequestBody UserProfileDTO userProfileDTO, 
                                       HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        String userRole = getCurrentUserRole(request);
        log.info("更新个人信息：userId={}, role={}, userProfile={}", userId, userRole, userProfileDTO);
        
        boolean result = userService.updateUserProfileByRole(userId, userRole, userProfileDTO);
        return result ? R.success("个人信息更新成功") : R.error("个人信息更新失败");
    }
    
    @ApiOperation("修改密码")
    @PutMapping("/password")
    public R<String> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO,
                                    HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        String userRole = getCurrentUserRole(request);
        log.info("修改密码：userId={}, role={}", userId, userRole);
        
        boolean result = userService.changePasswordByRole(userId, userRole, changePasswordDTO);
        return result ? R.success("密码修改成功") : R.error("密码修改失败");
    }
    
    /**
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
    
    /**
     * 从请求中获取当前用户角色
     */
    private String getCurrentUserRole(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getRoleFromToken(token);
    }
}
