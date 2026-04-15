<template>
  <div class="login-page">
    <div class="login-panel">
      <div class="login-copy">
        <span class="eyebrow">Vue Admin</span>
        <h1>农产品追溯管理平台</h1>
        <p>管理端已接入登录、仪表盘、基地、批次、生产记录、质检、召回和用户管理。</p>
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
  username: 'admin',
  password: '123456',
})

async function handleSubmit() {
  await authStore.login(form)
  await authStore.fetchCurrentUser()
  router.push('/dashboard')
}
</script>
