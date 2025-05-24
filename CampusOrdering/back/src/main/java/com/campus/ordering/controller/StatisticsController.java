package com.campus.ordering.controller;

import com.campus.ordering.common.R;
import com.campus.ordering.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 用餐统计控制器
 */
@Slf4j
@Api(tags = "用餐统计")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("获取用餐统计数据")
    @GetMapping("/dining")
    public R<Map<String, Object>> getDiningStatistics(@RequestParam(required = false) String date) {
        log.info("获取用餐统计数据：date={}", date);
        
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        Map<String, Object> statistics = statisticsService.getDiningStatistics(targetDate);
        
        return R.success(statistics);
    }

    @ApiOperation("获取实时预订统计")
    @GetMapping("/reservation")
    public R<Map<String, Object>> getReservationStatistics(@RequestParam(required = false) String date) {
        log.info("获取实时预订统计：date={}", date);
        
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        Map<String, Object> statistics = statisticsService.getReservationStatistics(targetDate);
        
        return R.success(statistics);
    }

    @ApiOperation("获取待就餐统计")
    @GetMapping("/pending")
    public R<Map<String, Object>> getPendingDiningStatistics() {
        log.info("获取待就餐统计");
        
        Map<String, Object> statistics = statisticsService.getPendingDiningStatistics();
        
        return R.success(statistics);
    }

    @ApiOperation("获取已完成订单统计")
    @GetMapping("/completed")
    public R<Map<String, Object>> getCompletedOrderStatistics(@RequestParam(required = false) String date) {
        log.info("获取已完成订单统计：date={}", date);
        
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        Map<String, Object> statistics = statisticsService.getCompletedOrderStatistics(targetDate);
        
        return R.success(statistics);
    }

    @ApiOperation("获取热门菜品统计")
    @GetMapping("/popular-dishes")
    public R<Map<String, Object>> getPopularDishesStatistics(@RequestParam(required = false) String date,
                                                             @RequestParam(defaultValue = "10") int limit) {
        log.info("获取热门菜品统计：date={}, limit={}", date, limit);
        
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        Map<String, Object> statistics = statisticsService.getPopularDishesStatistics(targetDate, limit);
        
        return R.success(statistics);
    }

    @ApiOperation("获取时间段就餐统计")
    @GetMapping("/time-distribution")
    public R<Map<String, Object>> getTimeDistributionStatistics(@RequestParam(required = false) String date) {
        log.info("获取时间段就餐统计：date={}", date);
        
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        Map<String, Object> statistics = statisticsService.getTimeDistributionStatistics(targetDate);
        
        return R.success(statistics);
    }

    @ApiOperation("获取菜品销量报表")
    @GetMapping("/dish-sales-report")
    public R<Map<String, Object>> getDishSalesReport(@RequestParam(required = false) String startDate,
                                                     @RequestParam(required = false) String endDate) {
        log.info("获取菜品销量报表：startDate={}, endDate={}", startDate, endDate);
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        Map<String, Object> report = statisticsService.getDishSalesReport(start, end);
        return R.success(report);
    }

    @ApiOperation("获取菜品营收报表")
    @GetMapping("/dish-revenue-report")
    public R<Map<String, Object>> getDishRevenueReport(@RequestParam(required = false) String startDate,
                                                       @RequestParam(required = false) String endDate) {
        log.info("获取菜品营收报表：startDate={}, endDate={}", startDate, endDate);
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        Map<String, Object> report = statisticsService.getDishRevenueReport(start, end);
        return R.success(report);
    }

    @ApiOperation("获取菜品价格分析报表")
    @GetMapping("/dish-price-analysis")
    public R<Map<String, Object>> getDishPriceAnalysisReport(@RequestParam(required = false) String startDate,
                                                             @RequestParam(required = false) String endDate) {
        log.info("获取菜品价格分析报表：startDate={}, endDate={}", startDate, endDate);
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        Map<String, Object> report = statisticsService.getDishPriceAnalysisReport(start, end);
        return R.success(report);
    }

    @ApiOperation("获取综合报表数据")
    @GetMapping("/comprehensive-report")
    public R<Map<String, Object>> getComprehensiveReport(@RequestParam(required = false) String startDate,
                                                         @RequestParam(required = false) String endDate) {
        log.info("获取综合报表数据：startDate={}, endDate={}", startDate, endDate);
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusDays(7);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        
        Map<String, Object> report = statisticsService.getComprehensiveReport(start, end);
        return R.success(report);
    }
} 