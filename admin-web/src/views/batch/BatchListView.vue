<template>
  <section class="page-section">
    <PageHeader title="批次管理" description="管理产品批次，并快速查看档案完整度与风险情况。" />
    <el-card shadow="never">
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索批次编号或产品名称"
          clearable
          style="max-width: 280px"
          @keyup.enter="loadData"
        />
        <el-select v-model="query.baseId" clearable placeholder="基地筛选" style="width: 220px">
          <el-option v-for="base in bases" :key="base.id" :label="base.baseName" :value="base.id" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreateDialog">新增批次</el-button>
      </div>

      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="batchCode" label="批次编号" min-width="180" />
        <el-table-column prop="productName" label="产品名称" min-width="160" />
        <el-table-column prop="productCategory" label="品类" width="120" />
        <el-table-column prop="baseName" label="所属基地" min-width="160" />
        <el-table-column label="完整度" width="110">
          <template #default="{ row }">
            <el-tag :type="completenessType(row._insight?.completenessScore)">
              {{ row._insight?.completenessScore ?? '-' }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="风险" width="120">
          <template #default="{ row }">
            <el-tag :type="riskType(row._insight?.riskLevel)">
              {{ riskText(row._insight?.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="召回" width="100">
          <template #default="{ row }">
            <el-tag :type="row.recallStatus === 1 ? 'danger' : 'success'">
              {{ row.recallStatus === 1 ? '召回中' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openArchive(row)">查看档案</el-button>
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button v-if="authStore.isAdmin" link type="danger" @click="handleDelete(row)">删除</el-button>
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
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增批次' : '编辑批次'" width="820px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="批次编号">
          <el-input v-model="form.batchCode" :disabled="dialogMode === 'edit'" placeholder="留空则系统自动生成" />
        </el-form-item>
        <el-form-item label="所属基地" prop="baseId">
          <el-select v-model="form.baseId" style="width: 100%">
            <el-option v-for="base in bases" :key="base.id" :label="base.baseName" :value="base.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="form.productName" />
        </el-form-item>
        <el-form-item label="产品品类">
          <el-input v-model="form.productCategory" />
        </el-form-item>
        <el-form-item label="种植日期">
          <el-date-picker v-model="form.plantingDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="预计采收">
          <el-date-picker v-model="form.expectedHarvestDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="实际采收">
          <el-date-picker v-model="form.actualHarvestDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="0" />
          <el-input v-model="form.unit" placeholder="单位" style="width: 120px; margin-left: 8px" />
        </el-form-item>
        <el-form-item label="批次状态">
          <el-radio-group v-model="form.batchStatus">
            <el-radio :value="0">待生产</el-radio>
            <el-radio :value="1">生产中</el-radio>
            <el-radio :value="2">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="召回状态">
          <el-alert
            title="召回状态由召回流程自动驱动，这里不允许手工修改。"
            type="info"
            :closable="false"
            show-icon
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
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
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import { createBatch, deleteBatch, getBatchInsight, getBatchPage, updateBatch } from '../../api/modules/batch'
import { getBaseList } from '../../api/modules/base'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const dialogMode = ref<'create' | 'edit'>('create')
const currentId = ref<number>()
const records = ref<any[]>([])
const bases = ref<any[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  baseId: null as number | null,
  batchStatus: null as number | null,
  pageNum: 1,
  pageSize: 10,
})

const form = reactive({
  batchCode: '',
  baseId: null as number | null,
  productName: '',
  productCategory: '',
  plantingDate: '',
  expectedHarvestDate: '',
  actualHarvestDate: '',
  quantity: 0 as number | null,
  unit: '',
  batchStatus: 0,
  remark: '',
})

const rules: FormRules = {
  baseId: [{ required: true, message: '请选择基地', trigger: 'change' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
}

async function loadBases() {
  bases.value = await (getBaseList() as any)
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getBatchPage(query)
    const rows = res.records || []
    records.value = rows
    total.value = res.total || 0
    await Promise.all(
      rows.map(async (row: any) => {
        try {
          row._insight = await getBatchInsight(row.id)
        } catch {
          row._insight = null
        }
      }),
    )
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
    quantity: 0,
    unit: '',
    batchStatus: 0,
    remark: '',
  })
}

function openCreateDialog() {
  dialogMode.value = 'create'
  currentId.value = undefined
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row: any) {
  dialogMode.value = 'edit'
  currentId.value = row.id
  Object.assign(form, {
    batchCode: row.batchCode,
    baseId: row.baseId,
    productName: row.productName,
    productCategory: row.productCategory,
    plantingDate: row.plantingDate,
    expectedHarvestDate: row.expectedHarvestDate,
    actualHarvestDate: row.actualHarvestDate,
    quantity: row.quantity,
    unit: row.unit,
    batchStatus: row.batchStatus,
    remark: row.remark,
  })
  dialogVisible.value = true
}

function openArchive(row: any) {
  router.push(`/batches/${row.id}/archive`)
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'create') {
      await createBatch({ ...form })
      ElMessage.success('新增成功')
    } else {
      await updateBatch(currentId.value!, { ...form })
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确定删除批次“${row.batchCode}”吗？`, '删除确认', { type: 'warning' })
  await deleteBatch(row.id)
  ElMessage.success('删除成功')
  await loadData()
}

function handleCurrentChange(page: number) {
  query.pageNum = page
  loadData()
}

function handleSizeChange(size: number) {
  query.pageSize = size
  query.pageNum = 1
  loadData()
}

function riskText(level?: string) {
  if (level === 'HIGH') return '高'
  if (level === 'MEDIUM') return '中'
  if (level === 'LOW') return '低'
  return '-'
}

function riskType(level?: string) {
  if (level === 'HIGH') return 'danger'
  if (level === 'MEDIUM') return 'warning'
  return 'success'
}

function completenessType(score?: number) {
  if ((score ?? 0) < 70) return 'danger'
  if ((score ?? 0) < 85) return 'warning'
  return 'success'
}

onMounted(async () => {
  await loadBases()
  await loadData()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
</style>
