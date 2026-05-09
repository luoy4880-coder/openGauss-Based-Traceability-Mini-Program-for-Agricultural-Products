<template>
  <section class="page-section">
    <PageHeader title="用户管理" description="管理账号、角色、所属公司与登录状态。" />

    <el-card shadow="never">
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="搜索用户名、姓名或公司" clearable style="max-width: 320px" @keyup.enter="loadData" />
        <el-select v-model="query.status" clearable placeholder="状态筛选" style="width: 180px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreateDialog">新增用户</el-button>
      </div>

      <el-table :data="records" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="realName" label="姓名" min-width="120" />
        <el-table-column prop="companyName" label="所属公司" min-width="180" />
        <el-table-column prop="phone" label="手机号" min-width="160" />
        <el-table-column label="角色" min-width="220">
          <template #default="{ row }">
            <el-space wrap>
              <el-tag v-for="role in row.roles || []" :key="role.id" type="primary" effect="plain">{{ role.roleName }}</el-tag>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ row.createdAt || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="280" fixed="right">
          <template #default="{ row }">
            <el-space>
              <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
              <el-button link type="warning" @click="openPasswordDialog(row)">改密码</el-button>
              <el-button v-if="authStore.isAdmin" link type="danger" @click="handleDelete(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next, sizes" :total="total" :current-page="query.pageNum" :page-size="query.pageSize" :page-sizes="[10, 20, 50, 100]" @current-change="handleCurrentChange" @size-change="handleSizeChange" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增用户' : '编辑用户'" width="760px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <div class="form-grid">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" :disabled="dialogMode === 'edit'" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item v-if="dialogMode === 'create'" label="初始密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入初始密码" />
          </el-form-item>
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="所属公司" prop="companyId">
            <el-select v-model="form.companyId" filterable placeholder="请选择公司" style="width: 100%">
              <el-option v-for="item in companyOptions" :key="item.id" :label="item.companyName" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio :value="1">启用</el-radio>
              <el-radio :value="0">停用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="角色" prop="roleIds">
            <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
              <el-option v-for="role in roleOptions" :key="role.id" :label="role.roleName" :value="role.id" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="480px" destroy-on-close>
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="90px">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="passwordSubmitting" @click="handlePasswordSubmit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { createUser, deleteUser, getRoleList, getUserPage, updateUser, updateUserPassword } from '../../api/modules/user'
import { getCompanyList } from '../../api/modules/company'
import PageHeader from '../../components/PageHeader.vue'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
type RoleItem = { id: number; roleCode: string; roleName: string }
type CompanyItem = { id: number; companyCode: string; companyName: string; status: number }
type UserRecord = {
  id: number
  username: string
  realName: string
  phone?: string
  companyId?: number
  companyName?: string
  status: number
  createdAt?: string
  roles?: RoleItem[]
}

const loading = ref(false)
const submitting = ref(false)
const passwordSubmitting = ref(false)
const total = ref(0)
const records = ref<UserRecord[]>([])
const roleOptions = ref<RoleItem[]>([])
const companyOptions = ref<CompanyItem[]>([])
const dialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number | null>(null)
const passwordUserId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const query = reactive({ keyword: '', status: null as number | null, pageNum: 1, pageSize: 10 })
const form = reactive({ username: '', password: '', realName: '', phone: '', companyId: undefined as number | undefined, status: 1, roleIds: [] as number[] })
const passwordForm = reactive({ newPassword: '' })

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  companyId: [{ required: true, message: '请选择公司', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  roleIds: [{ required: true, message: '请选择角色', trigger: 'change' }],
}
const passwordRules: FormRules = { newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }] }

async function loadRoles() { roleOptions.value = await getRoleList() }
async function loadCompanies() { companyOptions.value = await getCompanyList() }
async function loadData() {
  loading.value = true
  try {
    const data = await getUserPage(query)
    records.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}
function resetForm() {
  form.username = ''
  form.password = ''
  form.realName = ''
  form.phone = ''
  form.companyId = undefined
  form.status = 1
  form.roleIds = []
  editingId.value = null
}
function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}
function openEditDialog(row: UserRecord) {
  dialogMode.value = 'edit'
  editingId.value = row.id
  form.username = row.username
  form.password = ''
  form.realName = row.realName
  form.phone = row.phone || ''
  form.companyId = row.companyId
  form.status = row.status
  form.roleIds = (row.roles || []).map((role) => role.id)
  dialogVisible.value = true
}
function openPasswordDialog(row: UserRecord) {
  passwordUserId.value = row.id
  passwordForm.newPassword = ''
  passwordDialogVisible.value = true
}
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'create') {
      await createUser({ username: form.username, password: form.password, realName: form.realName, phone: form.phone || undefined, companyId: form.companyId!, status: form.status, roleIds: form.roleIds })
      ElMessage.success('用户创建成功')
    } else if (editingId.value != null) {
      await updateUser(editingId.value, { realName: form.realName, phone: form.phone || undefined, companyId: form.companyId!, status: form.status, roleIds: form.roleIds })
      ElMessage.success('用户更新成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}
async function handlePasswordSubmit() {
  if (!passwordFormRef.value || passwordUserId.value == null) return
  await passwordFormRef.value.validate()
  passwordSubmitting.value = true
  try {
    await updateUserPassword(passwordUserId.value, { newPassword: passwordForm.newPassword })
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
  } finally {
    passwordSubmitting.value = false
  }
}
async function handleDelete(row: UserRecord) {
  await ElMessageBox.confirm(`确定删除用户“${row.username}”吗？`, '删除确认', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('用户删除成功')
  if (records.value.length === 1 && query.pageNum > 1) query.pageNum -= 1
  loadData()
}
function handleCurrentChange(pageNum: number) { query.pageNum = pageNum; loadData() }
function handleSizeChange(pageSize: number) { query.pageSize = pageSize; query.pageNum = 1; loadData() }
onMounted(async () => { await Promise.all([loadRoles(), loadCompanies()]); await loadData() })
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
</style>
