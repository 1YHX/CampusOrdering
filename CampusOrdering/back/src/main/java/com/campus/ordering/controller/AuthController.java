package com.campus.ordering.controller;

import com.campus.ordering.common.Result;
import com.campus.ordering.service.AuthService;
import com.campus.ordering.vo.LoginVO;
import com.campus.ordering.vo.RegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginVO loginVO) {
        return authService.login(loginVO);
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterVO registerVO) {
        return authService.register(registerVO);
    }
} 