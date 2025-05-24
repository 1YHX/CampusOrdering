package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.ordering.entity.Order;
import com.campus.ordering.entity.OrderDetail;
import com.campus.ordering.service.OrderService;
import com.campus.ordering.service.OrderDetailService;
import com.campus.ordering.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    public Map<String, Object> getDiningStatistics(LocalDate date) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);
        
        // 查询当天所有订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Order::getEatTime, startTime, endTime);
        List<Order> orders = orderService.list(queryWrapper);
        
        // 总订单数
        result.put("totalOrders", orders.size());
        
        // 预订订单数（reservation）
        long reservationCount = orders.stream()
                .filter(order -> "reservation".equals(order.getOrderType()))
                .count();
        result.put("reservationOrders", reservationCount);
        
        // 堂食订单数（dine_in）
        long dineInCount = orders.stream()
                .filter(order -> "dine_in".equals(order.getOrderType()))
                .count();
        result.put("dineInOrders", dineInCount);
        
        // 总人数（去重用户）
        long totalUsers = orders.stream()
                .map(order -> order.getUserId() + "_" + order.getUserType())
                .distinct()
                .count();
        result.put("totalUsers", totalUsers);
        
        // 总金额
        BigDecimal totalAmount = orders.stream()
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalAmount", totalAmount);
        
        // 平均订单金额
        BigDecimal avgAmount = orders.size() > 0 ? 
                totalAmount.divide(BigDecimal.valueOf(orders.size()), 2, BigDecimal.ROUND_HALF_UP) : 
                BigDecimal.ZERO;
        result.put("avgOrderAmount", avgAmount);
        
        return result;
    }

    @Override
    public Map<String, Object> getReservationStatistics(LocalDate date) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);
        
        // 查询当天预约订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderType, "reservation")
                   .between(Order::getEatTime, startTime, endTime);
        List<Order> reservationOrders = orderService.list(queryWrapper);
        
        // 预订人数
        result.put("reservationCount", reservationOrders.size());
        
        // 获取所有订单详情
        List<Long> orderIds = reservationOrders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        if (!orderIds.isEmpty()) {
            LambdaQueryWrapper<OrderDetail> detailQuery = new LambdaQueryWrapper<>();
            detailQuery.in(OrderDetail::getOrderId, orderIds);
            List<OrderDetail> orderDetails = orderDetailService.list(detailQuery);
            
            // 菜品数量统计
            int totalDishQuantity = orderDetails.stream()
                    .mapToInt(OrderDetail::getQuantity)
                    .sum();
            result.put("totalDishQuantity", totalDishQuantity);
            
            // 菜品种类统计
            long dishVariety = orderDetails.stream()
                    .map(OrderDetail::getDishId)
                    .distinct()
                    .count();
            result.put("dishVariety", dishVariety);
        } else {
            result.put("totalDishQuantity", 0);
            result.put("dishVariety", 0);
        }
        
        // 按时间段统计预订
        Map<String, Integer> timeSlots = new HashMap<>();
        timeSlots.put("6:00-9:00", 0);
        timeSlots.put("9:00-12:00", 0);
        timeSlots.put("12:00-15:00", 0);
        timeSlots.put("15:00-18:00", 0);
        timeSlots.put("18:00-21:00", 0);
        timeSlots.put("21:00-24:00", 0);
        
        for (Order order : reservationOrders) {
            int hour = order.getEatTime().getHour();
            if (hour >= 6 && hour < 9) {
                timeSlots.put("6:00-9:00", timeSlots.get("6:00-9:00") + 1);
            } else if (hour >= 9 && hour < 12) {
                timeSlots.put("9:00-12:00", timeSlots.get("9:00-12:00") + 1);
            } else if (hour >= 12 && hour < 15) {
                timeSlots.put("12:00-15:00", timeSlots.get("12:00-15:00") + 1);
            } else if (hour >= 15 && hour < 18) {
                timeSlots.put("15:00-18:00", timeSlots.get("15:00-18:00") + 1);
            } else if (hour >= 18 && hour < 21) {
                timeSlots.put("18:00-21:00", timeSlots.get("18:00-21:00") + 1);
            } else if (hour >= 21 || hour < 6) {
                timeSlots.put("21:00-24:00", timeSlots.get("21:00-24:00") + 1);
            }
        }
        result.put("timeSlots", timeSlots);
        
        return result;
    }

    @Override
    public Map<String, Object> getPendingDiningStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        // 查询待就餐订单（已支付、准备中、待取餐）
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Order::getStatus, Arrays.asList("paid", "preparing", "ready"));
        List<Order> pendingOrders = orderService.list(queryWrapper);
        
        // 待就餐人数
        result.put("pendingCount", pendingOrders.size());
        
        // 按状态分类
        Map<String, Long> statusCount = pendingOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        
        result.put("paidCount", statusCount.getOrDefault("paid", 0L));
        result.put("preparingCount", statusCount.getOrDefault("preparing", 0L));
        result.put("readyCount", statusCount.getOrDefault("ready", 0L));
        
        // 按订单类型分类
        Map<String, Long> typeCount = pendingOrders.stream()
                .collect(Collectors.groupingBy(Order::getOrderType, Collectors.counting()));
        
        result.put("pendingReservation", typeCount.getOrDefault("reservation", 0L));
        result.put("pendingDineIn", typeCount.getOrDefault("dine_in", 0L));
        
        // 预计等待时间最长的订单
        Optional<Order> oldestOrder = pendingOrders.stream()
                .min(Comparator.comparing(Order::getCreateTime));
        
        if (oldestOrder.isPresent()) {
            Order order = oldestOrder.get();
            result.put("oldestOrderId", order.getOrderNo());
            result.put("oldestOrderTime", order.getCreateTime());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getCompletedOrderStatistics(LocalDate date) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);
        
        // 查询当天已完成订单（包括paid、preparing、ready、completed状态）
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Order::getStatus, Arrays.asList("paid", "preparing", "ready", "completed"))
                   .between(Order::getUpdateTime, startTime, endTime);
        List<Order> completedOrders = orderService.list(queryWrapper);
        
        // 完成订单数
        result.put("completedCount", completedOrders.size());
        
        // 完成订单总金额
        BigDecimal completedAmount = completedOrders.stream()
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("completedAmount", completedAmount);
        
        // 按订单类型分类
        Map<String, Long> typeCount = completedOrders.stream()
                .collect(Collectors.groupingBy(Order::getOrderType, Collectors.counting()));
        
        result.put("completedReservation", typeCount.getOrDefault("reservation", 0L));
        result.put("completedDineIn", typeCount.getOrDefault("dine_in", 0L));
        
        // 按小时统计完成订单
        Map<Integer, Integer> hourlyCompleted = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            hourlyCompleted.put(i, 0);
        }
        
        for (Order order : completedOrders) {
            int hour = order.getUpdateTime().getHour();
            hourlyCompleted.put(hour, hourlyCompleted.get(hour) + 1);
        }
        result.put("hourlyCompleted", hourlyCompleted);
        
        return result;
    }

    @Override
    public Map<String, Object> getPopularDishesStatistics(LocalDate date, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);
        
        // 查询当天订单
        LambdaQueryWrapper<Order> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.between(Order::getEatTime, startTime, endTime);
        List<Order> orders = orderService.list(orderQuery);
        
        if (orders.isEmpty()) {
            result.put("popularDishes", new ArrayList<>());
            return result;
        }
        
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        // 查询订单详情
        LambdaQueryWrapper<OrderDetail> detailQuery = new LambdaQueryWrapper<>();
        detailQuery.in(OrderDetail::getOrderId, orderIds);
        List<OrderDetail> orderDetails = orderDetailService.list(detailQuery);
        
        // 统计菜品销量
        Map<String, Integer> dishSales = new HashMap<>();
        Map<String, BigDecimal> dishRevenue = new HashMap<>();
        
        for (OrderDetail detail : orderDetails) {
            String dishName = detail.getDishName();
            dishSales.put(dishName, dishSales.getOrDefault(dishName, 0) + detail.getQuantity());
            dishRevenue.put(dishName, dishRevenue.getOrDefault(dishName, BigDecimal.ZERO)
                    .add(detail.getAmount()));
        }
        
        // 排序并获取前N个
        List<Map<String, Object>> popularDishes = dishSales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> dish = new HashMap<>();
                    dish.put("dishName", entry.getKey());
                    dish.put("quantity", entry.getValue());
                    dish.put("revenue", dishRevenue.getOrDefault(entry.getKey(), BigDecimal.ZERO));
                    return dish;
                })
                .collect(Collectors.toList());
        
        result.put("popularDishes", popularDishes);
        
        return result;
    }

    @Override
    public Map<String, Object> getTimeDistributionStatistics(LocalDate date) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(LocalTime.MAX);
        
        // 查询当天订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Order::getEatTime, startTime, endTime);
        List<Order> orders = orderService.list(queryWrapper);
        
        // 按小时统计就餐分布
        Map<Integer, Integer> hourlyDistribution = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            hourlyDistribution.put(i, 0);
        }
        
        for (Order order : orders) {
            int hour = order.getEatTime().getHour();
            hourlyDistribution.put(hour, hourlyDistribution.get(hour) + 1);
        }
        
        result.put("hourlyDistribution", hourlyDistribution);
        
        // 计算高峰时段
        int peakHour = hourlyDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(12);
        
        result.put("peakHour", peakHour);
        result.put("peakCount", hourlyDistribution.get(peakHour));
        
        return result;
    }

    @Override
    public Map<String, Object> getDishSalesReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        // 查询时间范围内的订单
        LambdaQueryWrapper<Order> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.between(Order::getCreateTime, startTime, endTime)
                  .in(Order::getStatus, Arrays.asList("paid", "preparing", "ready", "completed"));
        List<Order> orders = orderService.list(orderQuery);
        
        if (orders.isEmpty()) {
            result.put("dishSalesRanking", new ArrayList<>());
            result.put("totalDishes", 0);
            result.put("totalQuantity", 0);
            return result;
        }
        
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        // 查询订单详情
        LambdaQueryWrapper<OrderDetail> detailQuery = new LambdaQueryWrapper<>();
        detailQuery.in(OrderDetail::getOrderId, orderIds);
        List<OrderDetail> orderDetails = orderDetailService.list(detailQuery);
        
        // 统计菜品销量
        Map<String, Integer> dishQuantityMap = new HashMap<>();
        Map<String, String> dishTypeMap = new HashMap<>();
        
        for (OrderDetail detail : orderDetails) {
            String dishName = detail.getDishName();
            dishQuantityMap.put(dishName, dishQuantityMap.getOrDefault(dishName, 0) + detail.getQuantity());
            dishTypeMap.put(dishName, detail.getDishType());
        }
        
        // 计算总数量
        final int totalQuantity = orderDetails.stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();
        
        // 按销量排序
        List<Map<String, Object>> dishSalesRanking = dishQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> dish = new HashMap<>();
                    dish.put("dishName", entry.getKey());
                    dish.put("quantity", entry.getValue());
                    dish.put("dishType", dishTypeMap.get(entry.getKey()));
                    dish.put("percentage", totalQuantity > 0 ? (double) entry.getValue() / totalQuantity * 100 : 0);
                    return dish;
                })
                .collect(Collectors.toList());
        
        result.put("dishSalesRanking", dishSalesRanking);
        result.put("totalDishes", dishQuantityMap.size());
        result.put("totalQuantity", totalQuantity);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        
        return result;
    }

    @Override
    public Map<String, Object> getDishRevenueReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        // 查询时间范围内的订单
        LambdaQueryWrapper<Order> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.between(Order::getCreateTime, startTime, endTime)
                  .in(Order::getStatus, Arrays.asList("paid", "preparing", "ready", "completed"));
        List<Order> orders = orderService.list(orderQuery);
        
        if (orders.isEmpty()) {
            result.put("dishRevenueRanking", new ArrayList<>());
            result.put("totalRevenue", BigDecimal.ZERO);
            return result;
        }
        
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        // 查询订单详情
        LambdaQueryWrapper<OrderDetail> detailQuery = new LambdaQueryWrapper<>();
        detailQuery.in(OrderDetail::getOrderId, orderIds);
        List<OrderDetail> orderDetails = orderDetailService.list(detailQuery);
        
        // 统计菜品营收
        Map<String, BigDecimal> dishRevenueMap = new HashMap<>();
        Map<String, Integer> dishQuantityMap = new HashMap<>();
        Map<String, String> dishTypeMap = new HashMap<>();
        
        for (OrderDetail detail : orderDetails) {
            String dishName = detail.getDishName();
            BigDecimal revenue = detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
            dishRevenueMap.put(dishName, dishRevenueMap.getOrDefault(dishName, BigDecimal.ZERO).add(revenue));
            dishQuantityMap.put(dishName, dishQuantityMap.getOrDefault(dishName, 0) + detail.getQuantity());
            dishTypeMap.put(dishName, detail.getDishType());
        }
        
        // 计算总营收
        final BigDecimal totalRevenue = dishRevenueMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 按营收排序
        List<Map<String, Object>> dishRevenueRanking = dishRevenueMap.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> dish = new HashMap<>();
                    dish.put("dishName", entry.getKey());
                    dish.put("revenue", entry.getValue());
                    dish.put("quantity", dishQuantityMap.get(entry.getKey()));
                    dish.put("dishType", dishTypeMap.get(entry.getKey()));
                    dish.put("percentage", totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
                            entry.getValue().divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)) : 
                            BigDecimal.ZERO);
                    return dish;
                })
                .collect(Collectors.toList());
        
        result.put("dishRevenueRanking", dishRevenueRanking);
        result.put("totalRevenue", totalRevenue);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        
        return result;
    }

    @Override
    public Map<String, Object> getDishPriceAnalysisReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        // 查询时间范围内的订单
        LambdaQueryWrapper<Order> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.between(Order::getCreateTime, startTime, endTime)
                  .in(Order::getStatus, Arrays.asList("paid", "preparing", "ready", "completed"));
        List<Order> orders = orderService.list(orderQuery);
        
        if (orders.isEmpty()) {
            result.put("priceAnalysis", new ArrayList<>());
            result.put("avgPrice", BigDecimal.ZERO);
            return result;
        }
        
        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        // 查询订单详情
        LambdaQueryWrapper<OrderDetail> detailQuery = new LambdaQueryWrapper<>();
        detailQuery.in(OrderDetail::getOrderId, orderIds);
        List<OrderDetail> orderDetails = orderDetailService.list(detailQuery);
        
        // 按菜品分组分析价格
        Map<String, List<OrderDetail>> dishGroups = orderDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getDishName));
        
        List<Map<String, Object>> priceAnalysis = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalItems = 0;
        
        for (Map.Entry<String, List<OrderDetail>> entry : dishGroups.entrySet()) {
            String dishName = entry.getKey();
            List<OrderDetail> details = entry.getValue();
            
            // 计算该菜品的统计数据
            BigDecimal maxPrice = details.stream()
                    .map(OrderDetail::getPrice)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            
            BigDecimal minPrice = details.stream()
                    .map(OrderDetail::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            
            BigDecimal avgPrice = details.stream()
                    .map(OrderDetail::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(details.size()), 2, BigDecimal.ROUND_HALF_UP);
            
            int quantity = details.stream()
                    .mapToInt(OrderDetail::getQuantity)
                    .sum();
            
            Map<String, Object> dishPrice = new HashMap<>();
            dishPrice.put("dishName", dishName);
            dishPrice.put("dishType", details.get(0).getDishType());
            dishPrice.put("maxPrice", maxPrice);
            dishPrice.put("minPrice", minPrice);
            dishPrice.put("avgPrice", avgPrice);
            dishPrice.put("quantity", quantity);
            dishPrice.put("priceRange", maxPrice.subtract(minPrice));
            
            priceAnalysis.add(dishPrice);
            totalPrice = totalPrice.add(avgPrice);
            totalItems++;
        }
        
        // 按平均价格排序
        priceAnalysis.sort((a, b) -> ((BigDecimal) b.get("avgPrice")).compareTo((BigDecimal) a.get("avgPrice")));
        
        result.put("priceAnalysis", priceAnalysis);
        result.put("avgPrice", totalItems > 0 ? totalPrice.divide(BigDecimal.valueOf(totalItems), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        
        return result;
    }

    @Override
    public Map<String, Object> getComprehensiveReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取各种统计数据
        Map<String, Object> salesReport = getDishSalesReport(startDate, endDate);
        Map<String, Object> revenueReport = getDishRevenueReport(startDate, endDate);
        Map<String, Object> priceReport = getDishPriceAnalysisReport(startDate, endDate);
        
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        
        // 查询时间范围内的订单统计
        LambdaQueryWrapper<Order> orderQuery = new LambdaQueryWrapper<>();
        orderQuery.between(Order::getCreateTime, startTime, endTime)
                  .in(Order::getStatus, Arrays.asList("paid", "preparing", "ready", "completed"));
        List<Order> orders = orderService.list(orderQuery);
        
        // 基础统计
        result.put("totalOrders", orders.size());
        result.put("totalRevenue", revenueReport.get("totalRevenue"));
        result.put("totalQuantity", salesReport.get("totalQuantity"));
        result.put("totalDishes", salesReport.get("totalDishes"));
        result.put("avgOrderAmount", orders.size() > 0 ? 
                ((BigDecimal) revenueReport.get("totalRevenue")).divide(BigDecimal.valueOf(orders.size()), 2, BigDecimal.ROUND_HALF_UP) : 
                BigDecimal.ZERO);
        
        // 订单类型分布
        Map<String, Long> orderTypeCount = orders.stream()
                .collect(Collectors.groupingBy(Order::getOrderType, Collectors.counting()));
        result.put("reservationOrders", orderTypeCount.getOrDefault("reservation", 0L));
        result.put("dineInOrders", orderTypeCount.getOrDefault("dine_in", 0L));
        
        // 各类排行榜（取前10）
        List<Map<String, Object>> salesRanking = (List<Map<String, Object>>) salesReport.get("dishSalesRanking");
        List<Map<String, Object>> revenueRanking = (List<Map<String, Object>>) revenueReport.get("dishRevenueRanking");
        List<Map<String, Object>> priceAnalysis = (List<Map<String, Object>>) priceReport.get("priceAnalysis");
        
        result.put("topSalesDishes", salesRanking.stream().limit(10).collect(Collectors.toList()));
        result.put("topRevenueDishes", revenueRanking.stream().limit(10).collect(Collectors.toList()));
        result.put("topPriceDishes", priceAnalysis.stream().limit(10).collect(Collectors.toList()));
        
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        
        return result;
    }
} 