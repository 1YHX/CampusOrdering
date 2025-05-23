package com.campus.ordering.interceptor;

import com.alibaba.fastjson2.JSON;
import com.campus.ordering.common.R;
import com.campus.ordering.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            log.warn("请求未携带有效的Authorization头: {}", request.getRequestURI());
            sendErrorResponse(response, "未授权的访问");
            return false;
        }
        
        // 提取token（去掉Bearer前缀）
        token = token.substring(7);
        
        try {
            // 验证token
            if (!jwtUtil.validateToken(token)) {
                log.warn("无效的token: {}", token);
                sendErrorResponse(response, "无效的token");
                return false;
            }
            
            // 检查token是否过期
            if (jwtUtil.isTokenExpired(token)) {
                log.warn("token已过期: {}", token);
                sendErrorResponse(response, "token已过期");
                return false;
            }
            
            // token有效，继续处理请求
            log.debug("token验证成功，用户ID: {}", jwtUtil.getUserIdFromToken(token));
            return true;
            
        } catch (Exception e) {
            log.error("token验证失败: {}", e.getMessage());
            sendErrorResponse(response, "token验证失败");
            return false;
        }
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        R<String> result = R.error(message);
        response.getWriter().write(JSON.toJSONString(result));
    }
} 