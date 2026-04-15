<template>
  <section class="page-section">
    <PageHeader title="个人信息" description="展示当前登录用户资料，并支持快捷退出登录。" />

    <div class="panel-grid">
      <el-card shadow="never">
        <template #header>账户信息</template>
        <div class="profile-grid">
          <div class="profile-item">
            <span class="profile-label">用户名</span>
            <strong>{{ authStore.user?.username || '-' }}</strong>
          </div>
          <div class="profile-item">
            <span class="profile-label">姓名</span>
            <strong>{{ authStore.user?.realName || '-' }}</strong>
          </div>
          <div class="profile-item">
            <span class="profile-label">手机号</span>
            <strong>{{ authStore.user?.phone || '-' }}</strong>
          </div>
          <div class="profile-item">
            <span class="profile-label">状态</span>
            <el-tag :type="authStore.user?.status === 1 ? 'success' : 'info'">
              {{ authStore.user?.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>角色信息</template>
        <el-space wrap>
          <el-tag v-for="role in authStore.user?.roles || []" :key="role.id" type="primary" effect="plain">
            {{ role.roleName }}
          </el-tag>
        </el-space>
        <div class="profile-actions">
          <el-button plain @click="authStore.fetchCurrentUser()">刷新资料</el-button>
          <el-button type="danger" @click="handleLogout">退出登录</el-button>
        </div>
      </el-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import PageHeader from '../../components/PageHeader.vue'

const authStore = useAuthStore()
const router = useRouter()

async function handleLogout() {
  await ElMessageBox.confirm('确定退出当前账号吗？', '退出确认', {
    type: 'warning',
  })
  authStore.logout()
  router.push('/login')
}
</script>
