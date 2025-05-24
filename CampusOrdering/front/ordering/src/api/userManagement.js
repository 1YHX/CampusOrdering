import request from '@/utils/request'

// 分页查询所有用户
export function pageQueryUsers(params) {
  return request({
    url: '/api/admin/user-management',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserDetail(userType, userId) {
  return request({
    url: `/api/admin/user-management/${userType}/${userId}`,
    method: 'get'
  })
}

// 创建用户
export function createUser(userType, userData) {
  return request({
    url: `/api/admin/user-management/${userType}`,
    method: 'post',
    data: userData
  })
}

// 更新用户信息
export function updateUser(userType, userId, userData) {
  return request({
    url: `/api/admin/user-management/${userType}/${userId}`,
    method: 'put',
    data: userData
  })
}

// 删除用户
export function deleteUser(userType, userId) {
  return request({
    url: `/api/admin/user-management/${userType}/${userId}`,
    method: 'delete'
  })
}

// 更新用户状态
export function updateUserStatus(userType, userId, status) {
  return request({
    url: `/api/admin/user-management/${userType}/${userId}/status`,
    method: 'put',
    data: { status }
  })
}

// 重置用户密码
export function resetPassword(userType, userId, password = '123456') {
  return request({
    url: `/api/admin/user-management/${userType}/${userId}/password`,
    method: 'put',
    data: { password }
  })
} 