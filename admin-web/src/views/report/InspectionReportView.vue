<template>
  <section class="page-section">
    <PageHeader title="质检报告" description="这里只管理质检报告，不包含批次导入。"/>
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.batchId" clearable placeholder="按批次筛选" style="width: 280px">
          <el-option v-for="batch in batches" :key="batch.id" :label="batchLabel(batch)" :value="batch.id" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreateDialog">新增报告</el-button>
      </div>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="reportNo" label="报告编号" min-width="180" />
        <el-table-column label="批次" min-width="220">
          <template #default="{ row }">{{ batchNameMap[row.batchId] || row.batchId }}</template>
        </el-table-column>
        <el-table-column prop="agencyName" label="检测机构" min-width="160" />
        <el-table-column prop="inspectionTime" label="检测时间" min-width="180" />
        <el-table-column label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.resultStatus === 1 ? 'success' : 'danger'">{{ row.resultStatus === 1 ? '合格' : '不合格' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button v-if="authStore.isAdmin" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination background layout="total, prev, pager, next, sizes" :total="total" :current-page="query.pageNum" :page-size="query.pageSize" @current-change="handleCurrentChange" @size-change="handleSizeChange" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增质检报告' : '编辑质检报告'" width="780px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属批次" prop="batchId"><el-select v-model="form.batchId" style="width:100%"><el-option v-for="batch in batches" :key="batch.id" :label="batchLabel(batch)" :value="batch.id" /></el-select></el-form-item>
        <el-form-item label="报告编号"><el-input v-model="form.reportNo" :disabled="dialogMode === 'edit'" placeholder="留空则系统自动生成" /></el-form-item>
        <el-form-item label="检测机构" prop="agencyName"><el-input v-model="form.agencyName" /></el-form-item>
        <el-form-item label="检测员"><el-input v-model="form.inspectorName" /></el-form-item>
        <el-form-item label="检测时间" prop="inspectionTime"><el-date-picker v-model="form.inspectionTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
        <el-form-item label="检测结果" prop="resultStatus"><el-radio-group v-model="form.resultStatus"><el-radio :value="1">合格</el-radio><el-radio :value="0">不合格</el-radio></el-radio-group></el-form-item>
        <el-form-item label="结论"><el-input v-model="form.conclusion" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="报告地址"><el-input v-model="form.reportUrl" placeholder="可填写文件地址或上传后的链接" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button></template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import PageHeader from '../../components/PageHeader.vue'
import { getBatchList } from '../../api/modules/batch'
import { createInspectionReport, deleteInspectionReport, getInspectionReportPage, updateInspectionReport } from '../../api/modules/report'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const dialogMode = ref<'create' | 'edit'>('create')
const currentId = ref<number>()
const records = ref<any[]>([])
const batches = ref<any[]>([])
const total = ref(0)
const query = reactive({ batchId: null as number | null, resultStatus: null as number | null, pageNum: 1, pageSize: 10 })
const form = reactive({ batchId: null as number | null, reportNo: '', agencyName: '', inspectorName: '', inspectionTime: '', resultStatus: 1, conclusion: '', reportUrl: '' })
const rules: FormRules = {
  batchId: [{ required: true, message: '请选择批次', trigger: 'change' }],
  agencyName: [{ required: true, message: '请输入检测机构', trigger: 'blur' }],
  inspectionTime: [{ required: true, message: '请选择检测时间', trigger: 'change' }],
  resultStatus: [{ required: true, message: '请选择检测结果', trigger: 'change' }],
}

const batchNameMap = computed(() => Object.fromEntries(batches.value.map((item: any) => [item.id, batchLabel(item)])))

function batchLabel(batch: any) {
  return `${batch.batchCode} / ${batch.productName}`
}

async function loadBatches() {
  batches.value = (await getBatchList()) as any[]
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getInspectionReportPage(query)
    records.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function resetForm() {
  Object.assign(form, { batchId: null, reportNo: '', agencyName: '', inspectorName: '', inspectionTime: '', resultStatus: 1, conclusion: '', reportUrl: '' })
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
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'create') {
      await createInspectionReport({ ...form })
      ElMessage.success('新增成功')
    } else {
      await updateInspectionReport(currentId.value!, { ...form })
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定删除这条质检报告吗？', '删除确认', { type: 'warning' })
  await deleteInspectionReport(row.id)
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

onMounted(async () => {
  await loadBatches()
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
