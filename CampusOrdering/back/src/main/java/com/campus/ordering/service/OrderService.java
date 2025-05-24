package com.campus.ordering.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.ordering.dto.OrderDTO;
import com.campus.ordering.dto.PaymentDTO;
import com.campus.ordering.entity.Order;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     */
    Order createOrder(Long userId, String userType, OrderDTO orderDTO);
    
    /**
     * 分页查询用户订单
     */
    IPage<Order> pageQueryUserOrders(Long userId, String userType, int page, int size, String status, String orderType);
    
    /**
     * 分页查询所有订单（管理员）
     */
    IPage<Order> pageQueryAllOrders(int page, int size, String status, String orderType);
    
    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long orderId, String status);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId, Long userId, String userType);
    
    /**
     * 完成订单
     */
    boolean completeOrder(Long orderId);
    
    /**
     * 计算订单补贴金额
     */
    PaymentDTO calculateSubsidy(Long orderId, Long userId, String userType);
    
    /**
     * 支付订单
     */
    boolean payOrder(Long orderId, Long userId, String userType, PaymentDTO paymentDTO);
    
    /**
     * 获取订单详情（包含明细）
     */
    Order getOrderWithDetails(Long orderId);
    
    /**
     * 删除订单（管理员）
     */
    boolean deleteOrder(Long orderId);
} 