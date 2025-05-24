package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.ordering.entity.Staff;
import com.campus.ordering.mapper.StaffMapper;
import com.campus.ordering.service.StaffService;
import org.springframework.stereotype.Service;

/**
 * 员工服务实现类
 */
@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {

} 