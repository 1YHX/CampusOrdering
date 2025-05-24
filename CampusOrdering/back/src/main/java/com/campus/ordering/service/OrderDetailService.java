package com.campus.ordering.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.ordering.entity.OrderDetail;

import java.util.List;

/**
 * 订单明细服务接口
 */
public interface OrderDetailService extends IService<OrderDetail> {
    
    /**
     * 根据订单ID查询订单明细
     */
    List<OrderDetail> getDetailsByOrderId(Long orderId);
    
    /**
     * 根据订单ID删除订单明细
     */
    boolean deleteByOrderId(Long orderId);
} 