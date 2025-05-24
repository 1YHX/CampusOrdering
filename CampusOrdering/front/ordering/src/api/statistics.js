import request from '@/utils/request'

/**
 * 获取用餐统计数据
 */
export function getDiningStatistics(date) {
  return request({
    url: '/api/statistics/dining',
    method: 'get',
    params: {
      date
    }
  })
}

/**
 * 获取实时预订统计
 */
export function getReservationStatistics(date) {
  return request({
    url: '/api/statistics/reservation',
    method: 'get',
    params: {
      date
    }
  })
}

/**
 * 获取待就餐统计
 */
export function getPendingDiningStatistics() {
  return request({
    url: '/api/statistics/pending',
    method: 'get'
  })
}

/**
 * 获取已完成订单统计
 */
export function getCompletedOrderStatistics(date) {
  return request({
    url: '/api/statistics/completed',
    method: 'get',
    params: {
      date
    }
  })
}

/**
 * 获取热门菜品统计
 */
export function getPopularDishesStatistics(date, limit = 10) {
  return request({
    url: '/api/statistics/popular-dishes',
    method: 'get',
    params: {
      date,
      limit
    }
  })
}

/**
 * 获取时间段就餐统计
 */
export function getTimeDistributionStatistics(date) {
  return request({
    url: '/api/statistics/time-distribution',
    method: 'get',
    params: {
      date
    }
  })
}

/**
 * 获取菜品销量报表
 */
export function getDishSalesReport(startDate, endDate) {
  return request({
    url: '/api/statistics/dish-sales-report',
    method: 'get',
    params: {
      startDate,
      endDate
    }
  })
}

/**
 * 获取菜品营收报表
 */
export function getDishRevenueReport(startDate, endDate) {
  return request({
    url: '/api/statistics/dish-revenue-report',
    method: 'get',
    params: {
      startDate,
      endDate
    }
  })
}

/**
 * 获取菜品价格分析报表
 */
export function getDishPriceAnalysisReport(startDate, endDate) {
  return request({
    url: '/api/statistics/dish-price-analysis',
    method: 'get',
    params: {
      startDate,
      endDate
    }
  })
}

/**
 * 获取综合报表数据
 */
export function getComprehensiveReport(startDate, endDate) {
  return request({
    url: '/api/statistics/comprehensive-report',
    method: 'get',
    params: {
      startDate,
      endDate
    }
  })
} 