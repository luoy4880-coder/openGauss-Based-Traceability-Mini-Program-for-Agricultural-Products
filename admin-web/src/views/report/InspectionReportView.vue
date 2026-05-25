<template>
  <section class="page-section">
    <PageHeader title="质检报告" description="支持上传报告文件、在线预览和批次筛选。" />

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
        <el-table-column label="所属批次" min-width="220">
          <template #default="{ row }">
            {{ batchNameMap[row.batchId] || `批次ID ${row.batchId}` }}
          </template>
        </el-table-column>
        <el-table-column prop="agencyName" label="检测机构" min-width="160" />
        <el-table-column prop="inspectionTime" label="检测时间" min-width="180" />
        <el-table-column label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.resultStatus === 1 ? 'success' : 'danger'">
              {{ row.resultStatus === 1 ? '合格' : '不合格' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报告文件" min-width="180">
          <template #default="{ row }">
            <el-button v-if="row.reportUrl" link type="primary" @click="openPreview(row)">预览</el-button>
            <span v-else>未上传</span>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新增质检报告' : '编辑质检报告'"
      width="780px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属批次" prop="batchId">
          <el-select v-model="form.batchId" placeholder="请选择批次" style="width: 100%">
            <el-option v-for="batch in batches" :key="batch.id" :label="batchLabel(batch)" :value="batch.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报告编号">
          <el-input v-model="form.reportNo" :disabled="dialogMode === 'edit'" placeholder="留空则由系统自动生成" />
        </el-form-item>
        <el-form-item label="检测机构" prop="agencyName">
          <el-input v-model="form.agencyName" />
        </el-form-item>
        <el-form-item label="检测员">
          <el-input v-model="form.inspectorName" />
        </el-form-item>
        <el-form-item label="检测时间" prop="inspectionTime">
          <el-date-picker
            v-model="form.inspectionTime"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="检测结果" prop="resultStatus">
          <el-radio-group v-model="form.resultStatus">
            <el-radio :value="1">合格</el-radio>
            <el-radio :value="0">不合格</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="结论">
          <el-input v-model="form.conclusion" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="报告文件">
          <el-upload :auto-upload="false" :show-file-list="false" :on-change="handleFileChange" :limit="1">
            <el-button>选择文件</el-button>
          </el-upload>
          <div v-if="selectedFileName" class="file-hint">{{ selectedFileName }}</div>
          <div v-else class="file-hint">支持 PDF 或图片，保存时自动上传。</div>
          <div v-if="form.reportUrl" class="file-actions">
            <el-button link type="primary" @click="openPreview({ reportUrl: form.reportUrl })">预览当前文件</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="previewVisible" title="报告预览" width="900px" destroy-on-close>
      <div v-if="previewUrl" class="preview-wrap">
        <img v-if="previewMode === 'image'" :src="previewUrl" alt="报告预览" class="preview-image" />
        <iframe v-else-if="previewMode === 'pdf'" :src="previewUrl" class="preview-frame" />
        <div v-else class="preview-fallback">
          <p>当前文件暂不支持内嵌预览，请在新窗口打开查看。</p>
          <el-link :href="previewUrl" target="_blank" type="primary">打开文件</el-link>
        </div>
      </div>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import PageHeader from '../../components/PageHeader.vue'
import { getBatchList } from '../../api/modules/batch'
import {
  createInspectionReport,
  deleteInspectionReport,
  getInspectionReportPage,
  updateInspectionReport,
  uploadInspectionReportFile,
} from '../../api/modules/report'
import { useAuthStore } from '../../stores/auth'

type BatchOption = { id: number; batchCode: string; productName: string }
type InspectionReport = {
  id: number
  batchId: number
  reportNo?: string
  agencyName: string
  inspectorName?: string
  inspectionTime: string
  resultStatus: number
  conclusion?: string
  reportUrl?: string
}

const authStore = useAuthStore()
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const previewVisible = ref(false)
const formRef = ref<FormInstance>()
const dialogMode = ref<'create' | 'edit'>('create')
const currentId = ref<number>()
const selectedFile = ref<File | null>(null)
const selectedFileName = ref('')
const records = ref<InspectionReport[]>([])
const batches = ref<BatchOption[]>([])
const total = ref(0)
const previewUrl = ref('')
const previewMode = ref<'image' | 'pdf' | 'other'>('other')

const query = reactive({
  batchId: null as number | null,
  resultStatus: null as number | null,
  pageNum: 1,
  pageSize: 10,
})

const form = reactive({
  batchId: null as number | null,
  reportNo: '',
  agencyName: '',
  inspectorName: '',
  inspectionTime: '',
  resultStatus: 1,
  conclusion: '',
  reportUrl: '',
})

const rules: FormRules = {
  batchId: [{ required: true, message: '请选择批次', trigger: 'change' }],
  agencyName: [{ required: true, message: '请输入检测机构', trigger: 'blur' }],
  inspectionTime: [{ required: true, message: '请选择检测时间', trigger: 'change' }],
  resultStatus: [{ required: true, message: '请选择检测结果', trigger: 'change' }],
}

const batchNameMap = computed(() =>
  Object.fromEntries(batches.value.map((item) => [item.id, batchLabel(item)])),
)

function batchLabel(batch: BatchOption) {
  return `${batch.batchCode} / ${batch.productName}`
}

function fileAccessUrl(url?: string) {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  const baseURL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/$/, '')
  return `${baseURL}${url.startsWith('/') ? url : `/${url}`}`
}

function detectPreviewMode(url: string) {
  const normalized = url.toLowerCase().split('?')[0]
  if (normalized.endsWith('.pdf')) return 'pdf'
  if (/\.(png|jpg|jpeg|gif|webp|bmp)$/i.test(normalized)) return 'image'
  return 'other'
}

async function loadBatches() {
  const list = await getBatchList()
  batches.value = (list || []).map((item: any) => ({
    id: item.id,
    batchCode: item.batchCode,
    productName: item.productName,
  }))
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
  Object.assign(form, {
    batchId: null,
    reportNo: '',
    agencyName: '',
    inspectorName: '',
    inspectionTime: '',
    resultStatus: 1,
    conclusion: '',
    reportUrl: '',
  })
  selectedFile.value = null
  selectedFileName.value = ''
}

function openCreateDialog() {
  dialogMode.value = 'create'
  currentId.value = undefined
  resetForm()
  dialogVisible.value = true
}

function openEditDialog(row: InspectionReport) {
  dialogMode.value = 'edit'
  currentId.value = row.id
  resetForm()
  Object.assign(form, row)
  dialogVisible.value = true
}

function handleFileChange(uploadFile: UploadFile) {
  selectedFile.value = uploadFile.raw || null
  selectedFileName.value = uploadFile.name
}

function openPreview(row: { reportUrl?: string }) {
  if (!row.reportUrl) {
    ElMessage.warning('当前报告还没有上传文件')
    return
  }
  previewUrl.value = fileAccessUrl(row.reportUrl)
  previewMode.value = detectPreviewMode(previewUrl.value)
  previewVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    const payload = { ...form }
    if (selectedFile.value) {
      const uploadResult: any = await uploadInspectionReportFile(selectedFile.value)
      payload.reportUrl = uploadResult.url
    }

    if (dialogMode.value === 'create') {
      await createInspectionReport(payload)
      ElMessage.success('质检报告创建成功')
    } else if (currentId.value != null) {
      await updateInspectionReport(currentId.value, payload)
      ElMessage.success('质检报告更新成功')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: InspectionReport) {
  await ElMessageBox.confirm('确定删除这条质检报告吗？', '删除确认', { type: 'warning' })
  await deleteInspectionReport(row.id)
  ElMessage.success('质检报告删除成功')
  if (records.value.length === 1 && query.pageNum > 1) {
    query.pageNum -= 1
  }
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

.file-hint {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
}

.file-actions {
  margin-top: 8px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.preview-wrap {
  min-height: 60vh;
}

.preview-frame {
  width: 100%;
  height: 70vh;
  border: none;
}

.preview-image {
  display: block;
  max-width: 100%;
  max-height: 70vh;
  margin: 0 auto;
}

.preview-fallback {
  min-height: 240px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: var(--el-text-color-secondary);
}
</style>
