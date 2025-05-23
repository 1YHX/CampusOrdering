package com.campus.ordering.service;

import com.campus.ordering.common.Result;
import com.campus.ordering.vo.LoginVO;
import com.campus.ordering.vo.RegisterVO;

public interface AuthService {
    Result login(LoginVO loginVO);
    Result register(RegisterVO registerVO);
} 