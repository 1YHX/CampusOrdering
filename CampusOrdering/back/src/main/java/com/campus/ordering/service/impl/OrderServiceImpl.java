package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.ordering.common.CustomException;
import com.campus.ordering.dto.OrderDTO;
import com.campus.ordering.dto.OrderDetailDTO;
import com.campus.ordering.dto.PaymentDTO;
import com.campus.ordering.entity.Order;
import com.campus.ordering.entity.OrderDetail;
import com.campus.ordering.entity.SubsidyAccount;
import com.campus.ordering.entity.SubsidyPlan;
import com.campus.ordering.mapper.OrderMapper;
import com.campus.ordering.service.OrderDetailService;
import com.campus.ordering.service.OrderService;
import com.campus.ordering.service.DishService;
import com.campus.ordering.service.SubsidyAccountService;
import com.campus.ordering.service.SubsidyPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private OrderDetailService orderDetailService;
    
    @Autowired
    private DishService dishService;
    
    @Autowired
    private SubsidyAccountService subsidyAccountService;
    
    @Autowired
    private SubsidyPlanService subsidyPlanService;
    
    @Override
    @Transactional
    public Order createOrder(Long userId, String userType, OrderDTO orderDTO) {
        // 检查库存并减少库存
        for (OrderDetailDTO detailDTO : orderDTO.getDetails()) {
            dishService.reduceStock(detailDTO.getDishId(), detailDTO.getQuantity());
        }
        
        // 创建订单
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setUserType(userType);
        order.setStatus("pending");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        this.save(order);
        
        // 创建订单明细
        List<OrderDetail> orderDetails = orderDTO.getDetails().stream().map(detailDTO -> {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setDishId(detailDTO.getDishId());
            detail.setDishName(detailDTO.getDishName());
            detail.setDishType(detailDTO.getDishType());
            detail.setPrice(detailDTO.getPrice());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setAmount(detailDTO.getAmount());
            detail.setStatus("pending");
            detail.setCreateTime(LocalDateTime.now());
            return detail;
        }).collect(Collectors.toList());
        
        // 批量保存订单明细
        orderDetailService.saveBatch(orderDetails);
        
        return order;
    }
    
    @Override
    public PaymentDTO calculateSubsidy(Long orderId, Long userId, String userType) {
        // 获取订单信息
        Order order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        
        // 验证订单所有者
        if (!order.getUserId().equals(userId) || !order.getUserType().equals(userType)) {
            throw new CustomException("无权限操作此订单");
        }
        
        // 只有待支付状态的订单可以计算补贴
        if (!"pending".equals(order.getStatus())) {
            throw new CustomException("订单状态不允许支付");
        }
        
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setTotalAmount(order.getAmount());
        
        // 查询用户的补贴账户
        LambdaQueryWrapper<SubsidyAccount> accountQuery = new LambdaQueryWrapper<>();
        accountQuery.eq(SubsidyAccount::getUserType, userType)
                   .eq(SubsidyAccount::getUserId, userId)
                   .gt(SubsidyAccount::getBalance, 0);
        
        List<SubsidyAccount> subsidyAccounts = subsidyAccountService.list(accountQuery);
        
        if (subsidyAccounts.isEmpty()) {
            // 没有可用补贴，全额现金支付
            paymentDTO.setAvailableSubsidy(BigDecimal.ZERO);
            paymentDTO.setUsedSubsidy(BigDecimal.ZERO);
            paymentDTO.setActualAmount(order.getAmount());
            paymentDTO.setPaymentMethod("cash");
            paymentDTO.setRequiredCashRatio(BigDecimal.ONE);
            paymentDTO.setMinCashAmount(order.getAmount());
            paymentDTO.setSubsidyAccounts(new ArrayList<>());
            return paymentDTO;
        }
        
        // 计算补贴
        BigDecimal totalAvailableSubsidy = BigDecimal.ZERO;
        BigDecimal totalUsableSubsidy = BigDecimal.ZERO;
        BigDecimal maxCashRatio = BigDecimal.ZERO;
        List<PaymentDTO.SubsidyAccountInfo> accountInfos = new ArrayList<>();
        
        for (SubsidyAccount account : subsidyAccounts) {
            // 获取补贴方案信息
            SubsidyPlan plan = subsidyPlanService.getById(account.getSubsidyId());
            if (plan == null || plan.getStatus() != 1) {
                continue;
            }
            
            totalAvailableSubsidy = totalAvailableSubsidy.add(account.getBalance());
            
            // 计算该补贴可使用的最大金额（考虑现金支付比例）
            BigDecimal maxSubsidyUse = account.getBalance();
            if (plan.getCashRatio().compareTo(BigDecimal.ZERO) > 0) {
                // 如果有现金支付比例要求，计算最大可用补贴
                BigDecimal maxAllowedSubsidy = order.getAmount()
                    .multiply(BigDecimal.ONE.subtract(plan.getCashRatio()));
                maxSubsidyUse = maxSubsidyUse.min(maxAllowedSubsidy);
            }
            
            totalUsableSubsidy = totalUsableSubsidy.add(maxSubsidyUse);
            maxCashRatio = maxCashRatio.max(plan.getCashRatio());
            
            PaymentDTO.SubsidyAccountInfo accountInfo = new PaymentDTO.SubsidyAccountInfo();
            accountInfo.setAccountId(account.getId());
            accountInfo.setSubsidyName(plan.getName());
            accountInfo.setBalance(account.getBalance());
            accountInfo.setUsedAmount(maxSubsidyUse);
            accountInfo.setCashRatio(plan.getCashRatio());
            accountInfos.add(accountInfo);
        }
        
        // 计算最终使用的补贴金额
        BigDecimal finalUsedSubsidy = totalUsableSubsidy.min(order.getAmount());
        BigDecimal actualAmount = order.getAmount().subtract(finalUsedSubsidy);
        
        // 验证现金支付比例
        BigDecimal minCashAmount = order.getAmount().multiply(maxCashRatio);
        if (actualAmount.compareTo(minCashAmount) < 0) {
            actualAmount = minCashAmount;
            finalUsedSubsidy = order.getAmount().subtract(actualAmount);
        }
        
        paymentDTO.setAvailableSubsidy(totalAvailableSubsidy);
        paymentDTO.setUsedSubsidy(finalUsedSubsidy);
        paymentDTO.setActualAmount(actualAmount);
        paymentDTO.setRequiredCashRatio(maxCashRatio);
        paymentDTO.setMinCashAmount(minCashAmount);
        paymentDTO.setSubsidyAccounts(accountInfos);
        
        // 确定支付方式
        if (finalUsedSubsidy.compareTo(BigDecimal.ZERO) == 0) {
            paymentDTO.setPaymentMethod("cash");
        } else if (actualAmount.compareTo(BigDecimal.ZERO) == 0) {
            paymentDTO.setPaymentMethod("subsidy");
        } else {
            paymentDTO.setPaymentMethod("mixed");
        }
        
        return paymentDTO;
    }
    
    @Override
    @Transactional
    public boolean payOrder(Long orderId, Long userId, String userType, PaymentDTO paymentDTO) {
        // 获取订单信息
        Order order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        
        // 验证订单所有者
        if (!order.getUserId().equals(userId) || !order.getUserType().equals(userType)) {
            throw new CustomException("无权限操作此订单");
        }
        
        // 只有待支付状态的订单可以支付
        if (!"pending".equals(order.getStatus())) {
            throw new CustomException("订单状态不允许支付");
        }
        
        // 验证支付金额
        if (paymentDTO.getTotalAmount().compareTo(order.getAmount()) != 0) {
            throw new CustomException("支付金额与订单金额不符");
        }
        
        // 扣减补贴账户余额
        if (paymentDTO.getUsedSubsidy().compareTo(BigDecimal.ZERO) > 0) {
            for (PaymentDTO.SubsidyAccountInfo accountInfo : paymentDTO.getSubsidyAccounts()) {
                if (accountInfo.getUsedAmount().compareTo(BigDecimal.ZERO) > 0) {
                    SubsidyAccount account = subsidyAccountService.getById(accountInfo.getAccountId());
                    if (account == null || account.getBalance().compareTo(accountInfo.getUsedAmount()) < 0) {
                        throw new CustomException("补贴余额不足");
                    }
                    
                    account.setBalance(account.getBalance().subtract(accountInfo.getUsedAmount()));
                    account.setTotalUsed(account.getTotalUsed().add(accountInfo.getUsedAmount()));
                    account.setUpdateTime(LocalDateTime.now());
                    subsidyAccountService.updateById(account);
                }
            }
        }
        
        // 更新订单支付信息
        order.setSubsidyAmount(paymentDTO.getUsedSubsidy());
        order.setActualAmount(paymentDTO.getActualAmount());
        order.setPaymentMethod(paymentDTO.getPaymentMethod());
        order.setStatus("paid");
        order.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(order);
    }
    
    @Override
    public Order getOrderWithDetails(Long orderId) {
        Order order = this.getById(orderId);
        if (order != null) {
            List<OrderDetail> details = orderDetailService.getDetailsByOrderId(orderId);
            // 这里可以添加一个details字段到Order实体，或者使用VO类
        }
        return order;
    }
    
    @Override
    public IPage<Order> pageQueryUserOrders(Long userId, String userType, int page, int size, String status, String orderType) {
        Page<Order> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(Order::getUserId, userId)
                   .eq(Order::getUserType, userType);
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        if (StringUtils.hasText(orderType)) {
            queryWrapper.eq(Order::getOrderType, orderType);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        return this.page(pageInfo, queryWrapper);
    }
    
    @Override
    public IPage<Order> pageQueryAllOrders(int page, int size, String status, String orderType) {
        Page<Order> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        if (StringUtils.hasText(orderType)) {
            queryWrapper.eq(Order::getOrderType, orderType);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        return this.page(pageInfo, queryWrapper);
    }
    
    @Override
    public boolean updateOrderStatus(Long orderId, String status) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        
        // 如果是准备完成，生成取餐号
        if ("ready".equals(status)) {
            order.setPickupNo(generatePickupNo());
        }
        
        return this.updateById(order);
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId, String userType) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        
        // 验证订单所有者
        if (!order.getUserId().equals(userId) || !order.getUserType().equals(userType)) {
            throw new CustomException("无权限操作此订单");
        }
        
        // 只有待支付和已支付状态的订单可以取消
        if (!"pending".equals(order.getStatus()) && !"paid".equals(order.getStatus())) {
            throw new CustomException("当前状态的订单无法取消");
        }
        
        // 恢复菜品库存
        List<OrderDetail> orderDetails = orderDetailService.getDetailsByOrderId(orderId);
        for (OrderDetail detail : orderDetails) {
            dishService.increaseStock(detail.getDishId(), detail.getQuantity());
        }
        
        order.setStatus("cancelled");
        order.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(order);
    }
    
    @Override
    public boolean completeOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        
        order.setStatus("completed");
        order.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(order);
    }
    
    @Override
    @Transactional
    public boolean deleteOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        
        // 如果订单是已支付、准备中、就餐中状态，恢复菜品库存
        if ("paid".equals(order.getStatus()) || "preparing".equals(order.getStatus()) || "ready".equals(order.getStatus())) {
            List<OrderDetail> orderDetails = orderDetailService.getDetailsByOrderId(orderId);
            for (OrderDetail detail : orderDetails) {
                dishService.increaseStock(detail.getDishId(), detail.getQuantity());
            }
        }
        
        // 先删除订单明细
        orderDetailService.deleteByOrderId(orderId);
        
        // 再删除订单
        return this.removeById(orderId);
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis();
    }
    
    /**
     * 生成取餐号
     */
    private String generatePickupNo() {
        return "P" + String.format("%04d", (int)(Math.random() * 10000));
    }
} 