<template>
  <section class="page-section">
    <PageHeader title="基地管理" description="基地分页、新增、编辑、删除已经接通，可直接联调后台接口。" />

    <el-card shadow="never">
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索基地编码或名称"
          clearable
          style="max-width: 280px"
          @keyup.enter="loadData"
        />

        <el-select v-model="query.status" clearable placeholder="状态筛选" style="width: 180px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>

        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreateDialog">新增基地</el-button>
      </div>

      <el-table :data="records" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="baseCode" label="基地编码" min-width="140" />
        <el-table-column prop="baseName" label="基地名称" min-width="180" />
        <el-table-column prop="managerName" label="负责人" min-width="120" />
        <el-table-column prop="contactPhone" label="联系电话" min-width="140" />
        <el-table-column label="地区" min-width="220">
          <template #default="{ row }">
            {{ [row.province, row.city, row.district].filter(Boolean).join(' / ') || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="acreage" label="面积" min-width="100" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-space>
              <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="total"
          :current-page="query.pageNum"
          :page-size="query.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增基地' : '编辑基地'" width="720px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <div class="form-grid">
          <el-form-item label="基地编码" prop="baseCode">
            <el-input v-model="form.baseCode" :disabled="dialogMode === 'edit'" placeholder="如 BASE-001" />
          </el-form-item>
          <el-form-item label="基地名称" prop="baseName">
            <el-input v-model="form.baseName" placeholder="请输入基地名称" />
          </el-form-item>
          <el-form-item label="负责人">
            <el-input v-model="form.managerName" placeholder="请输入负责人" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="省份">
            <el-input v-model="form.province" placeholder="请输入省份" />
          </el-form-item>
          <el-form-item label="城市">
            <el-input v-model="form.city" placeholder="请输入城市" />
          </el-form-item>
          <el-form-item label="区县">
            <el-input v-model="form.district" placeholder="请输入区县" />
          </el-form-item>
          <el-form-item label="面积">
            <el-input-number v-model="form.acreage" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
        </div>

        <el-form-item label="详细地址">
          <el-input v-model="form.address" type="textarea" :rows="3" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { createBase, deleteBase, getBasePage, updateBase } from '../../api/modules/base'
import PageHeader from '../../components/PageHeader.vue'

type BaseRecord = {
  id: number
  baseCode: string
  baseName: string
  managerName?: string
  contactPhone?: string
  province?: string
  city?: string
  district?: string
  address?: string
  acreage?: number | null
  status: number
}

const loading = ref(false)
const submitting = ref(false)
const total = ref(0)
const records = ref<BaseRecord[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const query = reactive({
  keyword: '',
  status: null as number | null,
  pageNum: 1,
  pageSize: 10,
})

const form = reactive({
  baseCode: '',
  baseName: '',
  managerName: '',
  contactPhone: '',
  province: '',
  city: '',
  district: '',
  address: '',
  acreage: null as number | null,
  status: 1,
})

const rules: FormRules = {
  baseCode: [{ required: true, message: '请输入基地编码', trigger: 'blur' }],
  baseName: [{ required: true, message: '请输入基地名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

async function loadData() {
  loading.value = true
  try {
    const data = await getBasePage(query)
    records.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.baseCode = ''
  form.baseName = ''
  form.managerName = ''
  form.contactPhone = ''
  form.province = ''
  form.city = ''
  form.district = ''
  form.address = ''
  form.acreage = null
  form.status = 1
  editingId.value = null
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row: BaseRecord) {
  dialogMode.value = 'edit'
  editingId.value = row.id
  form.baseCode = row.baseCode
  form.baseName = row.baseName
  form.managerName = row.managerName || ''
  form.contactPhone = row.contactPhone || ''
  form.province = row.province || ''
  form.city = row.city || ''
  form.district = row.district || ''
  form.address = row.address || ''
  form.acreage = row.acreage ?? null
  form.status = row.status
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'create') {
      await createBase({ ...form })
      ElMessage.success('基地新增成功')
    } else if (editingId.value != null) {
      await updateBase(editingId.value, {
        baseName: form.baseName,
        managerName: form.managerName,
        contactPhone: form.contactPhone,
        province: form.province,
        city: form.city,
        district: form.district,
        address: form.address,
        acreage: form.acreage,
        status: form.status,
      })
      ElMessage.success('基地更新成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: BaseRecord) {
  await ElMessageBox.confirm(`确定删除基地“${row.baseName}”吗？`, '删除确认', { type: 'warning' })
  await deleteBase(row.id)
  ElMessage.success('基地删除成功')
  if (records.value.length === 1 && query.pageNum > 1) query.pageNum -= 1
  loadData()
}

function handleCurrentChange(pageNum: number) {
  query.pageNum = pageNum
  loadData()
}

function handleSizeChange(pageSize: number) {
  query.pageSize = pageSize
  query.pageNum = 1
  loadData()
}

onMounted(loadData)
</script>
