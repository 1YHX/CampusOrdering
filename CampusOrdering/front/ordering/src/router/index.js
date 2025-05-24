import { createRouter, createWebHistory } from 'vue-router'
import { getToken, isTokenExpired } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/dashboard/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard'
      },
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardHome.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['student', 'staff', 'admin']
        }
      },
      {
        path: '/advance-order',
        name: 'AdvanceOrder',
        component: () => import('@/views/advance-order/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['student', 'staff', 'admin']
        }
      },
      {
        path: '/dine-in',
        name: 'DineIn',
        component: () => import('@/views/dine-in/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['student', 'staff', 'admin']
        }
      },
      {
        path: '/order-history',
        name: 'OrderHistory',
        component: () => import('@/views/order-history/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['student', 'staff', 'admin']
        }
      },
      {
        path: '/order-payment/:id',
        name: 'OrderPayment',
        component: () => import('@/views/order-payment/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['student', 'staff', 'admin']
        }
      },
      {
        path: '/order-detail/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order-detail/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['student', 'staff', 'admin']
        }
      },
      {
        path: '/order-management',
        name: 'OrderManagement',
        component: () => import('@/views/order-management/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['staff', 'admin']
        }
      },
      {
        path: '/dish-management',
        name: 'DishManagement',
        component: () => import('@/views/dish-management/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['admin']
        }
      },
      {
        path: '/user-management',
        name: 'UserManagement',
        component: () => import('@/views/user-management/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['admin']
        }
      },
      {
        path: '/dining-statistics',
        name: 'DiningStatistics',
        component: () => import('@/views/dining-statistics/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['admin']
        }
      },
      {
        path: '/subsidy-management',
        name: 'SubsidyManagement',
        component: () => import('@/views/subsidy-management/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['admin']
        }
      },
      {
        path: '/reports',
        name: 'Reports',
        component: () => import('@/views/reports/index.vue'),
        meta: { 
          requiresAuth: true,
          allowedRoles: ['admin']
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  console.log('路由守卫 - 目标路由:', to.path)
  console.log('路由守卫 - 来源路由:', from.path)
  
  // 如果是登录或注册页面，直接放行
  if (to.path === '/login' || to.path === '/register') {
    console.log('访问登录/注册页面，直接放行')
    next()
    return
  }
  
  // 检查该路由是否需要登录权限
  if (to.matched.some(record => record.meta.requiresAuth)) {
    console.log('该路由需要登录权限')
    const token = getToken()
    if (!token) {
      console.log('未找到token，跳转到登录页')
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    } else if (isTokenExpired()) {
      console.log('token已过期，跳转到登录页')
      ElMessage.warning('登录已过期，请重新登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
    
    // 检查角色权限
    const userRole = localStorage.getItem('userRole')
    const route = to.matched.find(record => record.meta.allowedRoles)
    
    if (route && route.meta.allowedRoles) {
      if (!route.meta.allowedRoles.includes(userRole)) {
        console.log('用户角色权限不足:', userRole, '需要:', route.meta.allowedRoles)
        ElMessage.error('您没有权限访问该页面')
        next('/dashboard')
        return
      }
    }
    
    console.log('token有效，权限验证通过')
    next()
  } else {
    console.log('该路由不需要登录权限，直接放行')
    next()
  }
})

export default router