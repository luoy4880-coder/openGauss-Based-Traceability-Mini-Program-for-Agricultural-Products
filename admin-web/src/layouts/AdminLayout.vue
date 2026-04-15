<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="brand">
        <div class="brand-mark">YJ</div>
        <div>
          <div class="brand-title">溯源管理端</div>
          <div class="brand-subtitle">Traceability Admin</div>
        </div>
      </div>

      <nav class="menu-list">
        <RouterLink v-for="item in menuItems" :key="item.path" class="menu-item" :to="item.path">
          {{ item.label }}
        </RouterLink>
      </nav>
    </aside>

    <div class="admin-main">
      <header class="admin-header">
        <div>
          <div class="header-title">{{ currentTitle }}</div>
          <div class="header-subtitle">管理后台已接入核心业务页面，后续继续完善交互与校验即可。</div>
        </div>

        <div class="header-actions">
          <el-button text @click="router.push('/profile')">
            {{ authStore.user?.realName || '未登录用户' }}
          </el-button>
          <el-button plain @click="handleLogout">退出登录</el-button>
        </div>
      </header>

      <main class="admin-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const menuItems = [
  { path: '/dashboard', label: '仪表盘' },
  { path: '/bases', label: '基地管理' },
  { path: '/batches', label: '批次管理' },
  { path: '/production-records', label: '生产记录' },
  { path: '/inspection-reports', label: '质检报告' },
  { path: '/recalls', label: '召回管理' },
  { path: '/users', label: '用户管理' },
  { path: '/profile', label: '个人信息' },
]

const titleMap: Record<string, string> = {
  '/dashboard': '仪表盘',
  '/bases': '基地管理',
  '/batches': '批次管理',
  '/production-records': '生产记录',
  '/inspection-reports': '质检报告',
  '/recalls': '召回管理',
  '/users': '用户管理',
  '/profile': '个人信息',
}

const currentTitle = computed(() => titleMap[route.path] || '管理后台')

async function handleLogout() {
  await ElMessageBox.confirm('确定退出当前账号吗？', '退出确认', {
    type: 'warning',
  })
  authStore.logout()
  router.push('/login')
}
</script>
