package com.campus.ordering.service;

import java.time.LocalDate;
import java.util.Map;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取用餐统计数据
     */
    Map<String, Object> getDiningStatistics(LocalDate date);
    
    /**
     * 获取实时预订统计
     */
    Map<String, Object> getReservationStatistics(LocalDate date);
    
    /**
     * 获取待就餐统计
     */
    Map<String, Object> getPendingDiningStatistics();
    
    /**
     * 获取已完成订单统计
     */
    Map<String, Object> getCompletedOrderStatistics(LocalDate date);
    
    /**
     * 获取热门菜品统计
     */
    Map<String, Object> getPopularDishesStatistics(LocalDate date, int limit);
    
    /**
     * 获取时间段就餐统计
     */
    Map<String, Object> getTimeDistributionStatistics(LocalDate date);
    
    /**
     * 获取菜品销量报表
     */
    Map<String, Object> getDishSalesReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取菜品营收报表
     */
    Map<String, Object> getDishRevenueReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取菜品价格分析报表
     */
    Map<String, Object> getDishPriceAnalysisReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取综合报表数据
     */
    Map<String, Object> getComprehensiveReport(LocalDate startDate, LocalDate endDate);
} 