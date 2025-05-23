<template>
  <div class="dashboard-home">
    <h1>欢迎使用校园订餐系统</h1>
    <div class="welcome-content">
      <template v-if="userRole === 'student'">
        <p>亲爱的同学，您可以通过系统进行提前订餐或堂食点餐，享受便捷的校园餐饮服务。</p>
        <div class="quick-actions">
          <el-button type="primary" @click="router.push('/advance-order')">
            <el-icon><Timer /></el-icon>
            提前订餐
          </el-button>
          <el-button @click="router.push('/dine-in')">
            <el-icon><Bowl /></el-icon>
            堂食点餐
          </el-button>
          <el-button @click="router.push('/order-history')">
            <el-icon><Document /></el-icon>
            我的订单
          </el-button>
        </div>
      </template>
      
      <template v-if="userRole === 'staff'">
        <p>您好，员工同志！您可以为学生提供订餐服务，包括提前订餐和堂食点餐。</p>
        <div class="quick-actions">
          <el-button type="primary" @click="router.push('/dine-in')">
            <el-icon><Bowl /></el-icon>
            堂食点餐
          </el-button>
          <el-button @click="router.push('/advance-order')">
            <el-icon><Timer /></el-icon>
            提前订餐
          </el-button>
          <el-button @click="router.push('/order-management')">
            <el-icon><List /></el-icon>
            订单管理
          </el-button>
        </div>
      </template>
      
      <template v-if="userRole === 'admin'">
        <p>管理员您好！您可以管理整个订餐系统，包括菜品、订单、用户和数据统计。</p>
        <div class="quick-actions">
          <el-button type="primary" @click="router.push('/dish-management')">
            <el-icon><Collection /></el-icon>
            菜品管理
          </el-button>
          <el-button @click="router.push('/order-management')">
            <el-icon><List /></el-icon>
            订单管理
          </el-button>
          <el-button @click="router.push('/dining-statistics')">
            <el-icon><TrendCharts /></el-icon>
            数据统计
          </el-button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Timer, 
  Bowl, 
  Document,
  List,
  Collection,
  TrendCharts
} from '@element-plus/icons-vue'

const router = useRouter()

// 获取用户角色
const userRole = computed(() => {
  return localStorage.getItem('userRole') || ''
})
</script>

<style scoped>
.dashboard-home {
  background-color: #fff;
  padding: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

h1 {
  color: #1890ff;
  margin-bottom: 20px;
}

.welcome-content p {
  color: #666;
  font-size: 16px;
  margin-bottom: 30px;
}

.quick-actions {
  display: flex;
  gap: 16px;
}

.quick-actions .el-button {
  padding: 12px 24px;
  font-size: 14px;
}
</style> 