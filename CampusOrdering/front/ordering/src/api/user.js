import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

// 获取个人信息
export function getUserProfile() {
  return request({
    url: '/api/user/profile',
    method: 'get'
  })
}

// 更新个人信息
export function updateUserProfile(data) {
  return request({
    url: '/api/user/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/api/user/password',
    method: 'put',
    data
  })
} 