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
        <RouterLink v-for="item in visibleMenuItems" :key="item.path" class="menu-item" :to="item.path">
          <span class="menu-icon">{{ item.icon }}</span>
          {{ item.label }}
        </RouterLink>
      </nav>
    </aside>

    <div class="admin-main">
      <header class="admin-header">
        <div class="header-title">{{ currentTitle }}</div>

        <div class="header-actions">
          <el-button text @click="router.push('/profile')">
            <span style="margin-right: 6px;">👤</span>
            {{ authStore.user?.realName || '未登录用户' }}
          </el-button>
          <el-button type="primary" plain @click="router.push('/crop-quick-import')">快速导入作物信息</el-button>
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
  { path: '/dashboard', label: '仪表盘', icon: '📊' },
  { path: '/task-workbench', label: '智能工作台', icon: '🧭' },
  { path: '/risk-center', label: '风险中心', icon: '🚨' },
  { path: '/bases', label: '基地管理', icon: '🌱' },
  { path: '/batches', label: '批次管理', icon: '📦' },
  { path: '/product-items', label: '一物一码', icon: '🔖' },
  { path: '/logistics', label: '流通链路', icon: '🚚' },
  { path: '/production-records', label: '生产记录', icon: '📝' },
  { path: '/inspection-reports', label: '质检报告', icon: '✅' },
  { path: '/crop-quick-import', label: '导入信息', icon: '⚡' },
  { path: '/feedback-tasks', label: '反馈追踪', icon: '📬' },
  { path: '/recalls', label: '召回管理', icon: '♻️' },
  { path: '/users', label: '用户管理', icon: '👥' },
  { path: '/profile', label: '个人信息', icon: '🪪' },
]

const visibleMenuItems = computed(() => menuItems.filter((item) => item.path !== '/users' || authStore.isStaff))

const titleMap: Record<string, string> = {
  '/dashboard': '仪表盘',
  '/task-workbench': '智能工作台',
  '/risk-center': '风险中心',
  '/bases': '基地管理',
  '/batches': '批次管理',
  '/product-items': '一物一码',
  '/logistics': '流通链路',
  '/production-records': '生产记录',
  '/inspection-reports': '质检报告',
  '/crop-quick-import': '快速导入作物信息',
  '/feedback-tasks': '用户反馈追踪',
  '/recalls': '召回管理',
  '/users': '用户管理',
  '/profile': '个人信息',
}

const currentTitle = computed(() => {
  if (route.path.startsWith('/batches/') && route.path.endsWith('/archive')) {
    return '批次智能档案'
  }
  return titleMap[route.path] || '溯源管理端'
})

async function handleLogout() {
  await ElMessageBox.confirm('确定退出当前账号吗？', '退出确认', { type: 'warning' })
  authStore.logout()
  router.push('/login')
}
</script>
