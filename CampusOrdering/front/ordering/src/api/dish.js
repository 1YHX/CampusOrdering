import request from '@/utils/request'

// 获取所有菜品分类
export function getDishCategories() {
  return request({
    url: '/api/category/list',
    method: 'get'
  })
}

// 分页查询菜品
export function getDishPage(params) {
  return request({
    url: '/api/dish/page',
    method: 'get',
    params
  })
}

// 根据分类查询菜品
export function getDishesByCategory(categoryId, params = {}) {
  return request({
    url: `/api/dish/category/${categoryId}`,
    method: 'get',
    params
  })
}

// 查询所有上架菜品
export function getAvailableDishes(params = {}) {
  return request({
    url: '/api/dish/available',
    method: 'get',
    params
  })
}

// 根据ID查询菜品详情
export function getDishById(id) {
  return request({
    url: `/api/dish/${id}`,
    method: 'get'
  })
}

// 添加菜品（员工功能）
export function addDish(data) {
  return request({
    url: '/api/dish',
    method: 'post',
    data
  })
}

// 更新菜品（员工功能）
export function updateDish(id, data) {
  return request({
    url: `/api/dish/${id}`,
    method: 'put',
    data
  })
}

// 删除菜品（员工功能）
export function deleteDish(id) {
  return request({
    url: `/api/dish/${id}`,
    method: 'delete'
  })
}

// 更新菜品状态（员工功能）
export function updateDishStatus(id, status) {
  return request({
    url: `/api/dish/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 添加菜品分类（员工功能）
export function addDishCategory(data) {
  return request({
    url: '/api/category',
    method: 'post',
    data
  })
}

// 更新菜品分类（员工功能）
export function updateDishCategory(id, data) {
  return request({
    url: `/api/category/${id}`,
    method: 'put',
    data
  })
}

// 删除菜品分类（员工功能）
export function deleteDishCategory(id) {
  return request({
    url: `/api/category/${id}`,
    method: 'delete'
  })
} 