package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.ordering.entity.OrderDetail;
import com.campus.ordering.mapper.OrderDetailMapper;
import com.campus.ordering.service.OrderDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单明细服务实现类
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    
    @Override
    public List<OrderDetail> getDetailsByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        return this.list(queryWrapper);
    }
    
    @Override
    public boolean deleteByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        return this.remove(queryWrapper);
    }
} 