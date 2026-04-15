<template>
  <section class="page-section">
    <PageHeader title="批次管理" description="批次分页、新增、编辑、删除已经接通，支持按基地和状态筛选。" />

    <el-card shadow="never">
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索批次编码或产品名称"
          clearable
          style="max-width: 280px"
          @keyup.enter="loadData"
        />
        <el-select v-model="query.baseId" clearable placeholder="基地筛选" style="width: 220px">
          <el-option v-for="item in baseOptions" :key="item.id" :label="item.baseName" :value="item.id" />
        </el-select>
        <el-select v-model="query.batchStatus" clearable placeholder="批次状态" style="width: 180px">
          <el-option label="待生产" :value="0" />
          <el-option label="生产中" :value="1" />
          <el-option label="已完成" :value="2" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreateDialog">新增批次</el-button>
      </div>

      <el-table :data="records" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="batchCode" label="批次编码" min-width="150" />
        <el-table-column prop="productName" label="产品名称" min-width="180" />
        <el-table-column prop="productCategory" label="品类" min-width="120" />
        <el-table-column prop="baseName" label="所属基地" min-width="160" />
        <el-table-column label="种植日期" min-width="120">
          <template #default="{ row }">{{ row.plantingDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="预计采收" min-width="120">
          <template #default="{ row }">{{ row.expectedHarvestDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="产量" min-width="120">
          <template #default="{ row }">{{ row.quantity ? `${row.quantity} ${row.unit || ''}` : '-' }}</template>
        </el-table-column>
        <el-table-column label="批次状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="batchStatusTag(row.batchStatus)">{{ batchStatusText(row.batchStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="召回状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="row.recallStatus === 1 ? 'danger' : 'success'">
              {{ row.recallStatus === 1 ? '召回中' : '正常' }}
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
        <el-pagination background layout="total, prev, pager, next, sizes" :total="total" :current-page="query.pageNum" :page-size="query.pageSize" :page-sizes="[10, 20, 50, 100]" @current-change="handleCurrentChange" @size-change="handleSizeChange" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增批次' : '编辑批次'" width="860px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <div class="form-grid">
          <el-form-item label="批次编码" prop="batchCode">
            <el-input v-model="form.batchCode" :disabled="dialogMode === 'edit'" placeholder="如 BATCH-001" />
          </el-form-item>
          <el-form-item label="所属基地" prop="baseId">
            <el-select v-model="form.baseId" placeholder="请选择基地" style="width: 100%">
              <el-option v-for="item in baseOptions" :key="item.id" :label="item.baseName" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="产品名称" prop="productName">
            <el-input v-model="form.productName" placeholder="请输入产品名称" />
          </el-form-item>
          <el-form-item label="产品品类">
            <el-input v-model="form.productCategory" placeholder="请输入产品品类" />
          </el-form-item>
          <el-form-item label="种植日期">
            <el-date-picker v-model="form.plantingDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="预计采收">
            <el-date-picker v-model="form.expectedHarvestDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="实际采收">
            <el-date-picker v-model="form.actualHarvestDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="产量">
            <el-input-number v-model="form.quantity" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="单位">
            <el-input v-model="form.unit" placeholder="如 kg、箱" />
          </el-form-item>
          <el-form-item label="批次状态" prop="batchStatus">
            <el-select v-model="form.batchStatus" placeholder="请选择批次状态" style="width: 100%">
              <el-option label="待生产" :value="0" />
              <el-option label="生产中" :value="1" />
              <el-option label="已完成" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="召回状态" prop="recallStatus">
            <el-select v-model="form.recallStatus" placeholder="请选择召回状态" style="width: 100%">
              <el-option label="正常" :value="0" />
              <el-option label="召回中" :value="1" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { createBatch, deleteBatch, getBatchPage, updateBatch } from '../../api/modules/batch'
import { getBaseList } from '../../api/modules/base'
import PageHeader from '../../components/PageHeader.vue'

type BaseOption = { id: number; baseName: string }
type BatchRecord = {
  id: number
  batchCode: string
  baseId: number
  baseName: string
  productName: string
  productCategory?: string
  plantingDate?: string
  expectedHarvestDate?: string
  actualHarvestDate?: string
  quantity?: number | null
  unit?: string
  batchStatus: number
  recallStatus: number
  remark?: string
}

const loading = ref(false)
const submitting = ref(false)
const total = ref(0)
const records = ref<BatchRecord[]>([])
const baseOptions = ref<BaseOption[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const query = reactive({ keyword: '', baseId: null as number | null, batchStatus: null as number | null, pageNum: 1, pageSize: 10 })
const form = reactive({
  batchCode: '',
  baseId: null as number | null,
  productName: '',
  productCategory: '',
  plantingDate: '',
  expectedHarvestDate: '',
  actualHarvestDate: '',
  quantity: null as number | null,
  unit: '',
  batchStatus: 1,
  recallStatus: 0,
  remark: '',
})

const rules: FormRules = {
  batchCode: [{ required: true, message: '请输入批次编码', trigger: 'blur' }],
  baseId: [{ required: true, message: '请选择所属基地', trigger: 'change' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  batchStatus: [{ required: true, message: '请选择批次状态', trigger: 'change' }],
  recallStatus: [{ required: true, message: '请选择召回状态', trigger: 'change' }],
}

async function loadBaseOptions() {
  baseOptions.value = (await getBaseList({ status: 1 })) || []
}

async function loadData() {
  loading.value = true
  try {
    const data = await getBatchPage(query)
    records.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function resetForm() {
  Object.assign(form, {
    batchCode: '',
    baseId: null,
    productName: '',
    productCategory: '',
    plantingDate: '',
    expectedHarvestDate: '',
    actualHarvestDate: '',
    quantity: null,
    unit: '',
    batchStatus: 1,
    recallStatus: 0,
    remark: '',
  })
  editingId.value = null
}

function openCreateDialog() {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row: BatchRecord) {
  dialogMode.value = 'edit'
  editingId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'create') {
      await createBatch({ ...form })
      ElMessage.success('批次新增成功')
    } else if (editingId.value != null) {
      await updateBatch(editingId.value, { ...form })
      ElMessage.success('批次更新成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: BatchRecord) {
  await ElMessageBox.confirm(`确定删除批次“${row.batchCode}”吗？`, '删除确认', { type: 'warning' })
  await deleteBatch(row.id)
  ElMessage.success('批次删除成功')
  if (records.value.length === 1 && query.pageNum > 1) query.pageNum -= 1
  loadData()
}

function handleCurrentChange(pageNum: number) { query.pageNum = pageNum; loadData() }
function handleSizeChange(pageSize: number) { query.pageSize = pageSize; query.pageNum = 1; loadData() }
function batchStatusText(status: number) { return status === 0 ? '待生产' : status === 1 ? '生产中' : status === 2 ? '已完成' : '未知' }
function batchStatusTag(status: number) { return status === 2 ? 'success' : status === 1 ? 'warning' : 'info' }

onMounted(async () => { await loadBaseOptions(); await loadData() })
</script>
