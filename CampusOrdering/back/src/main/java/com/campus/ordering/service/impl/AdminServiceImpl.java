package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.ordering.entity.Admin;
import com.campus.ordering.mapper.AdminMapper;
import com.campus.ordering.service.AdminService;
import org.springframework.stereotype.Service;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

} 