import request from '@/utils/request'

/**
 * 分页查询补贴方案
 */
export function getSubsidyPlansPage(params) {
  return request({
    url: '/api/subsidy/plans/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有补贴方案
 */
export function getAllSubsidyPlans() {
  return request({
    url: '/api/subsidy/plans',
    method: 'get'
  })
}

/**
 * 根据ID获取补贴方案
 */
export function getSubsidyPlanById(id) {
  return request({
    url: `/api/subsidy/plans/${id}`,
    method: 'get'
  })
}

/**
 * 创建补贴方案
 */
export function createSubsidyPlan(data) {
  return request({
    url: '/api/subsidy/plans',
    method: 'post',
    data
  })
}

/**
 * 更新补贴方案
 */
export function updateSubsidyPlan(id, data) {
  return request({
    url: `/api/subsidy/plans/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除补贴方案
 */
export function deleteSubsidyPlan(id) {
  return request({
    url: `/api/subsidy/plans/${id}`,
    method: 'delete'
  })
}

/**
 * 启用/禁用补贴方案
 */
export function toggleSubsidyPlanStatus(id) {
  return request({
    url: `/api/subsidy/plans/${id}/status`,
    method: 'put'
  })
}

/**
 * 应用补贴方案到用户
 */
export function applySubsidyPlan(id) {
  return request({
    url: `/api/subsidy/plans/${id}/apply`,
    method: 'post'
  })
}

/**
 * 获取所有学院列表
 */
export function getAllColleges() {
  return request({
    url: '/api/subsidy/colleges',
    method: 'get'
  })
}

/**
 * 获取所有部门列表
 */
export function getAllDepartments() {
  return request({
    url: '/api/subsidy/departments',
    method: 'get'
  })
} 