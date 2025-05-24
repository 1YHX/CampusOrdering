package com.campus.ordering.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.ordering.common.R;
import com.campus.ordering.dto.OrderDTO;
import com.campus.ordering.dto.PaymentDTO;
import com.campus.ordering.entity.Order;
import com.campus.ordering.entity.OrderDetail;
import com.campus.ordering.service.OrderDetailService;
import com.campus.ordering.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 订单管理控制器
 */
@Slf4j
@Api(tags = "订单管理")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderDetailService orderDetailService;

    @ApiOperation("创建订单")
    @PostMapping
    public R<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        log.info("创建订单：{}", orderDTO);
        
        // 从请求头获取用户信息（这里简化处理，实际应该从JWT token中获取）
        Long userId = Long.valueOf(request.getHeader("userId"));
        String userType = request.getHeader("userType");
        
        Order order = orderService.createOrder(userId, userType, orderDTO);
        return R.success(order);
    }

    @ApiOperation("计算订单补贴金额")
    @GetMapping("/{id}/calculate-subsidy")
    public R<PaymentDTO> calculateSubsidy(@PathVariable Long id, HttpServletRequest request) {
        log.info("计算订单补贴：orderId={}", id);
        
        Long userId = Long.valueOf(request.getHeader("userId"));
        String userType = request.getHeader("userType");
        
        PaymentDTO paymentDTO = orderService.calculateSubsidy(id, userId, userType);
        return R.success(paymentDTO);
    }

    @ApiOperation("支付订单")
    @PostMapping("/{id}/pay")
    public R<String> payOrder(@PathVariable Long id, 
                             @Valid @RequestBody PaymentDTO paymentDTO, 
                             HttpServletRequest request) {
        log.info("支付订单：orderId={}, payment={}", id, paymentDTO);
        
        Long userId = Long.valueOf(request.getHeader("userId"));
        String userType = request.getHeader("userType");
        
        boolean result = orderService.payOrder(id, userId, userType, paymentDTO);
        return result ? R.success("支付成功") : R.error("支付失败");
    }

    @ApiOperation("查询用户订单")
    @GetMapping("/user")
    public R<IPage<Order>> getUserOrders(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(required = false) String orderType,
                                        HttpServletRequest request) {
        log.info("查询用户订单：page={}, size={}, status={}, orderType={}", page, size, status, orderType);
        
        Long userId = Long.valueOf(request.getHeader("userId"));
        String userType = request.getHeader("userType");
        
        IPage<Order> pageResult = orderService.pageQueryUserOrders(userId, userType, page, size, status, orderType);
        return R.success(pageResult);
    }

    @ApiOperation("查询所有订单（管理员）")
    @GetMapping("/admin")
    public R<IPage<Order>> getAllOrders(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false) String orderType) {
        log.info("查询所有订单：page={}, size={}, status={}, orderType={}", page, size, status, orderType);
        
        IPage<Order> pageResult = orderService.pageQueryAllOrders(page, size, status, orderType);
        return R.success(pageResult);
    }

    @ApiOperation("根据ID查询订单")
    @GetMapping("/{id}")
    public R<Order> getOrderById(@PathVariable Long id) {
        log.info("根据ID查询订单：id={}", id);
        
        Order order = orderService.getById(id);
        return order != null ? R.success(order) : R.error("订单不存在");
    }

    @ApiOperation("查询订单明细")
    @GetMapping("/{id}/details")
    public R<List<OrderDetail>> getOrderDetails(@PathVariable Long id) {
        log.info("查询订单明细：orderId={}", id);
        
        List<OrderDetail> details = orderDetailService.getDetailsByOrderId(id);
        return R.success(details);
    }

    @ApiOperation("更新订单状态")
    @PutMapping("/{id}/status")
    public R<String> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("更新订单状态：id={}, status={}", id, status);
        
        boolean result = orderService.updateOrderStatus(id, status);
        return result ? R.success("状态更新成功") : R.error("状态更新失败");
    }

    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    public R<String> cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        log.info("取消订单：id={}", id);
        
        Long userId = Long.valueOf(request.getHeader("userId"));
        String userType = request.getHeader("userType");
        
        boolean result = orderService.cancelOrder(id, userId, userType);
        return result ? R.success("订单取消成功") : R.error("订单取消失败");
    }

    @ApiOperation("完成订单")
    @PutMapping("/{id}/complete")
    public R<String> completeOrder(@PathVariable Long id) {
        log.info("完成订单：id={}", id);
        
        boolean result = orderService.completeOrder(id);
        return result ? R.success("订单完成") : R.error("订单完成失败");
    }

    @ApiOperation("删除订单（管理员）")
    @DeleteMapping("/{id}")
    public R<String> deleteOrder(@PathVariable Long id) {
        log.info("删除订单：id={}", id);
        
        boolean result = orderService.deleteOrder(id);
        return result ? R.success("订单删除成功") : R.error("订单删除失败");
    }
} 