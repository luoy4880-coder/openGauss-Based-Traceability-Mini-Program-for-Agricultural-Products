import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: () => import('../layouts/AdminLayout.vue'),
      children: [
        { path: '', redirect: '/dashboard' },
        { path: 'dashboard', name: 'dashboard', component: () => import('../views/dashboard/DashboardView.vue') },
        { path: 'task-workbench', name: 'task-workbench', component: () => import('../views/task/TaskWorkbenchView.vue') },
        { path: 'risk-center', name: 'risk-center', component: () => import('../views/risk/RiskCenterView.vue') },
        { path: 'bases', name: 'bases', component: () => import('../views/base/BaseListView.vue') },
        { path: 'batches', name: 'batches', component: () => import('../views/batch/BatchListView.vue') },
        { path: 'batches/:id/archive', name: 'batch-archive', component: () => import('../views/batch/BatchArchiveView.vue') },
        { path: 'product-items', name: 'product-items', component: () => import('../views/item/ProductItemView.vue') },
        { path: 'logistics', name: 'logistics', component: () => import('../views/logistics/LogisticsView.vue') },
        { path: 'production-records', name: 'production-records', component: () => import('../views/record/ProductionRecordView.vue') },
        { path: 'inspection-reports', name: 'inspection-reports', component: () => import('../views/report/InspectionReportView.vue') },
        { path: 'crop-quick-import', name: 'crop-quick-import', component: () => import('../views/import/CropQuickImportView.vue') },
        { path: 'feedback-tasks', name: 'feedback-tasks', component: () => import('../views/feedback/FeedbackTrackView.vue') },
        { path: 'recalls', name: 'recalls', component: () => import('../views/recall/RecallView.vue') },
        { path: 'users', name: 'users', component: () => import('../views/user/UserListView.vue') },
        { path: 'profile', name: 'profile', component: () => import('../views/profile/ProfileView.vue') },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (to.meta.public) {
    if ((to.path === '/login' || to.path === '/register') && authStore.token) {
      return '/dashboard'
    }
    return true
  }

  if (!authStore.token) {
    return '/login'
  }

  if (!authStore.user) {
    try {
      await authStore.fetchCurrentUser()
    } catch {
      authStore.logout()
      return '/login'
    }
  }

  if (!authStore.isStaff) {
    authStore.logout()
    return '/login'
  }

  if (to.meta.adminOnly && !authStore.isAdmin) {
    return '/dashboard'
  }

  return true
})

export default router
