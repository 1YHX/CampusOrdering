import request from '@/utils/request'

// 创建订单
export function createOrder(data) {
  return request({
    url: '/api/order',
    method: 'post',
    data,
    headers: {
      'userId': localStorage.getItem('userId'),
      'userType': localStorage.getItem('userType')
    }
  })
}

// 计算订单补贴金额
export function calculateSubsidy(id) {
  return request({
    url: `/api/order/${id}/calculate-subsidy`,
    method: 'get',
    headers: {
      'userId': localStorage.getItem('userId'),
      'userType': localStorage.getItem('userType')
    }
  })
}

// 支付订单
export function payOrder(id, data) {
  return request({
    url: `/api/order/${id}/pay`,
    method: 'post',
    data,
    headers: {
      'userId': localStorage.getItem('userId'),
      'userType': localStorage.getItem('userType')
    }
  })
}

// 查询用户订单
export function getUserOrders(params) {
  return request({
    url: '/api/order/user',
    method: 'get',
    params,
    headers: {
      'userId': localStorage.getItem('userId'),
      'userType': localStorage.getItem('userType')
    }
  })
}

// 查询所有订单（管理员）
export function getAllOrders(params) {
  return request({
    url: '/api/order/admin',
    method: 'get',
    params
  })
}

// 根据ID查询订单详情
export function getOrderById(id) {
  return request({
    url: `/api/order/${id}`,
    method: 'get'
  })
}

// 查询订单明细
export function getOrderDetails(id) {
  return request({
    url: `/api/order/${id}/details`,
    method: 'get'
  })
}

// 更新订单状态
export function updateOrderStatus(id, status) {
  return request({
    url: `/api/order/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 取消订单
export function cancelOrder(id) {
  return request({
    url: `/api/order/${id}/cancel`,
    method: 'put',
    headers: {
      'userId': localStorage.getItem('userId'),
      'userType': localStorage.getItem('userType')
    }
  })
}

// 完成订单
export function completeOrder(id) {
  return request({
    url: `/api/order/${id}/complete`,
    method: 'put'
  })
}

// 删除订单（管理员）
export function deleteOrder(id) {
  return request({
    url: `/api/order/${id}`,
    method: 'delete'
  })
} 