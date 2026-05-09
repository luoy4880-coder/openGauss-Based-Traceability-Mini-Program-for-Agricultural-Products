<template>
  <section class="page-section">
    <PageHeader title="召回管理" description="发起、跟踪和关闭问题批次召回。" />

    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.batchId" clearable placeholder="批次筛选" style="width: 240px">
          <el-option v-for="item in batchOptions" :key="item.id" :label="batchLabel(item)" :value="item.id" />
        </el-select>
        <el-select v-model="query.recallStatus" clearable placeholder="召回状态" style="width: 180px">
          <el-option label="进行中" :value="1" />
          <el-option label="已关闭" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="danger" @click="openCreateDialog">发起召回</el-button>
      </div>

      <el-table :data="records" stripe style="width: 100%" v-loading="loading">
        <el-table-column label="批次" min-width="220">
          <template #default="{ row }">
            {{ batchNameMap[row.batchId] || `批次ID ${row.batchId}` }}
          </template>
        </el-table-column>
        <el-table-column label="召回级别" min-width="100">
          <template #default="{ row }">级别 {{ row.recallLevel }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="召回原因" min-width="260" show-overflow-tooltip />
        <el-table-column label="状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="row.recallStatus === 1 ? 'danger' : 'success'">
              {{ row.recallStatus === 1 ? '进行中' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="通知时间" min-width="170">
          <template #default="{ row }">{{ row.noticeTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="关闭时间" min-width="170">
          <template #default="{ row }">{{ row.closedAt || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="240" fixed="right">
          <template #default="{ row }">
            <el-space>
              <el-button v-if="authStore.isAdmin" link type="warning" :disabled="row.recallStatus === 0" @click="handleClose(row)">
                关闭召回
              </el-button>
              <el-button v-if="authStore.isAdmin" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" title="发起召回" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="批次" prop="batchId">
          <el-select v-model="form.batchId" placeholder="请选择批次" style="width: 100%">
            <el-option v-for="item in batchOptions" :key="item.id" :label="batchLabel(item)" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="召回级别" prop="recallLevel">
          <el-select v-model="form.recallLevel" placeholder="请选择召回级别" style="width: 100%">
            <el-option label="一级" :value="1" />
            <el-option label="二级" :value="2" />
            <el-option label="三级" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="召回原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请输入召回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确认发起</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getBatchList } from '../../api/modules/batch'
import { closeRecall, createRecall, deleteRecall, getRecallPage } from '../../api/modules/recall'
import PageHeader from '../../components/PageHeader.vue'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
type BatchOption = { id: number; batchCode: string; productName: string }
type RecallRecord = { id: number; batchId: number; recallLevel: number; reason: string; recallStatus: number; noticeTime?: string; closedAt?: string }

const loading = ref(false)
const submitting = ref(false)
const total = ref(0)
const records = ref<RecallRecord[]>([])
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const batchOptions = ref<BatchOption[]>([])

const query = reactive({ batchId: null as number | null, recallStatus: null as number | null, pageNum: 1, pageSize: 10 })
const form = reactive({ batchId: null as number | null, recallLevel: 1, reason: '' })
const rules: FormRules = {
  batchId: [{ required: true, message: '请选择批次', trigger: 'change' }],
  recallLevel: [{ required: true, message: '请选择召回级别', trigger: 'change' }],
  reason: [{ required: true, message: '请输入召回原因', trigger: 'blur' }],
}

const batchNameMap = computed(() => Object.fromEntries(batchOptions.value.map((item) => [item.id, `${item.batchCode} / ${item.productName}`])))

function batchLabel(item: BatchOption) {
  return `${item.batchCode} / ${item.productName}`
}

async function loadBatchOptions() {
  const list = await getBatchList()
  batchOptions.value = (list || []).map((item: any) => ({ id: item.id, batchCode: item.batchCode, productName: item.productName }))
}

async function loadData() {
  loading.value = true
  try {
    const data = await getRecallPage(query)
    records.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  form.batchId = null
  form.recallLevel = 1
  form.reason = ''
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    await createRecall({ ...form })
    ElMessage.success('召回创建成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleClose(row: RecallRecord) {
  await ElMessageBox.confirm('确定关闭这条召回记录吗？', '关闭确认', { type: 'warning' })
  await closeRecall(row.id)
  ElMessage.success('召回已关闭')
  loadData()
}

async function handleDelete(row: RecallRecord) {
  await ElMessageBox.confirm('确定删除这条召回记录吗？', '删除确认', { type: 'warning' })
  await deleteRecall(row.id)
  ElMessage.success('召回记录已删除')
  if (records.value.length === 1 && query.pageNum > 1) query.pageNum -= 1
  loadData()
}

function handleCurrentChange(pageNum: number) { query.pageNum = pageNum; loadData() }
function handleSizeChange(pageSize: number) { query.pageSize = pageSize; query.pageNum = 1; loadData() }

onMounted(async () => { await loadBatchOptions(); await loadData() })
</script>
