<template>
  <div class="login-page">
    <div class="login-panel">
      <div class="login-copy">
        <span class="eyebrow">Traceability Admin</span>
        <h1>农产品溯源管理平台</h1>
        <p>仅面向管理员与业务员开放，统一管理基地、批次、生产记录、质检报告与召回流程。</p>
      </div>

      <el-form :model="form" class="login-form" @submit.prevent="handleSubmit">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" class="submit-button" @click="handleSubmit">
          登录管理端
        </el-button>
        <div class="form-switch">
          <span>还没有账号？</span>
          <el-link type="primary" @click="router.push('/register')">去注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  username: '',
  password: '',
})

async function handleSubmit() {
  await authStore.login(form)
  await authStore.fetchCurrentUser()
  router.push('/dashboard')
}
</script>

<style scoped>
.form-switch {
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 13px;
  color: #64758a;
}
</style>
