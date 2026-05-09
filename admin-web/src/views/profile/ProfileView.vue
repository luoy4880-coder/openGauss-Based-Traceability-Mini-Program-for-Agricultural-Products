<template>
  <section class="page-section">
    <PageHeader title="个人信息" description="查看当前账号资料与角色权限。" />

    <div class="profile-layout">
      <el-card shadow="never" class="profile-hero-card">
        <div class="profile-hero">
          <div class="profile-avatar">{{ avatarText }}</div>
          <div class="profile-hero-copy">
            <div class="profile-eyebrow">当前账号</div>
            <h3>{{ authStore.user?.realName || authStore.user?.username || '未登录用户' }}</h3>
            <p>{{ authStore.user?.username ? `账号：${authStore.user.username}` : '当前未获取到账号信息' }}</p>
          </div>
          <div class="profile-status">
            <el-tag :type="authStore.user?.status === 1 ? 'success' : 'info'" size="large">
              {{ authStore.user?.status === 1 ? '启用中' : '已停用' }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="profile-panel">
        <template #header>基础资料</template>
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
            <strong>{{ authStore.user?.phone || '未填写' }}</strong>
          </div>
          <div class="profile-item">
            <span class="profile-label">账号状态</span>
            <el-tag :type="authStore.user?.status === 1 ? 'success' : 'info'">
              {{ authStore.user?.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </div>
          <div class="profile-item">
            <span class="profile-label">角色数量</span>
            <strong>{{ authStore.user?.roles?.length || 0 }}</strong>
          </div>
          <div class="profile-item">
            <span class="profile-label">用户 ID</span>
            <strong>{{ authStore.user?.id ?? '-' }}</strong>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="profile-panel">
        <template #header>角色权限</template>
        <div class="profile-role-section">
          <el-space wrap>
            <el-tag v-for="role in authStore.user?.roles || []" :key="role.id" type="primary" effect="plain">
              {{ role.roleName }}
            </el-tag>
            <span v-if="!(authStore.user?.roles?.length)" class="profile-empty">暂无角色信息</span>
          </el-space>
        </div>
      </el-card>

      <el-card shadow="never" class="profile-panel">
        <template #header>账号操作</template>
        <div class="profile-actions">
          <el-button type="primary" @click="openEditDialog">修改信息</el-button>
          <el-button plain @click="handleRefresh">刷新资料</el-button>
          <el-button type="danger" @click="handleLogout">退出登录</el-button>
        </div>
      </el-card>

      <el-dialog v-model="editDialogVisible" title="修改个人信息" width="500px" destroy-on-close>
        <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="80px">
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="editForm.realName" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" placeholder="请输入手机号" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="submitProfileEdit">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import { updateProfileApi } from '../../api/modules/auth'
import { useAuthStore } from '../../stores/auth'
import PageHeader from '../../components/PageHeader.vue'

const authStore = useAuthStore()
const router = useRouter()
const editDialogVisible = ref(false)
const saving = ref(false)
const editFormRef = ref<FormInstance>()
const editForm = reactive({
  realName: '',
  phone: '',
})
const editRules: FormRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
}

const avatarText = computed(() => {
  const text = authStore.user?.realName || authStore.user?.username || 'U'
  return text.slice(0, 1).toUpperCase()
})

async function handleRefresh() {
  await authStore.fetchCurrentUser()
  ElMessage.success('个人信息已刷新')
}

function openEditDialog() {
  editForm.realName = authStore.user?.realName || ''
  editForm.phone = authStore.user?.phone || ''
  editDialogVisible.value = true
}

async function submitProfileEdit() {
  if (!editFormRef.value) return
  await editFormRef.value.validate()
  saving.value = true
  try {
    await updateProfileApi({
      realName: editForm.realName.trim(),
      phone: editForm.phone.trim(),
    })
    await authStore.fetchCurrentUser()
    editDialogVisible.value = false
    ElMessage.success('个人信息已更新')
  } finally {
    saving.value = false
  }
}

async function handleLogout() {
  await ElMessageBox.confirm('确定退出当前账号吗？', '退出确认', {
    type: 'warning',
  })
  authStore.logout()
  router.push('/login')
}
</script>
