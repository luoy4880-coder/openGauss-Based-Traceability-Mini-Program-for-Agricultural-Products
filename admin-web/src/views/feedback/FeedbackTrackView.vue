<template>
  <section class="page-section">
    <PageHeader
      title="用户反馈追踪"
      description="AI 自动识别反馈分类、风险等级和优先级，并推动高风险反馈进入处理闭环。"
    />

    <el-card shadow="never" class="overview-card">
      <div class="overview-grid">
        <div class="overview-item">
          <span>待处理反馈</span>
          <strong>{{ overview.pendingCount }}</strong>
        </div>
        <div class="overview-item danger">
          <span>高风险未闭环</span>
          <strong>{{ overview.highRiskPendingCount }}</strong>
        </div>
        <div class="overview-item warning">
          <span>紧急反馈</span>
          <strong>{{ overview.urgentPendingCount }}</strong>
        </div>
      </div>
      <div v-if="overview.highRiskPendingCount > 0" class="overview-tip">
        系统检测到仍有高风险反馈未处理，建议优先认领并尽快跟进。
      </div>
    </el-card>

    <el-card shadow="never">
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          clearable
          placeholder="搜索内容 / 联系方式 / 溯源码"
          style="width: 240px"
        />
        <el-select v-model="query.category" clearable placeholder="AI 分类" style="width: 130px">
          <el-option label="质量" value="质量" />
          <el-option label="物流" value="物流" />
          <el-option label="服务" value="服务" />
          <el-option label="其他" value="其他" />
        </el-select>
        <el-select v-model="query.riskLevel" clearable placeholder="风险等级" style="width: 130px">
          <el-option label="高风险" value="HIGH" />
          <el-option label="中风险" value="MEDIUM" />
          <el-option label="低风险" value="LOW" />
        </el-select>
        <el-select v-model="query.priority" clearable placeholder="优先级" style="width: 120px">
          <el-option label="高" :value="1" />
          <el-option label="中" :value="2" />
          <el-option label="低" :value="3" />
        </el-select>
        <el-select v-model="query.status" clearable placeholder="处理状态" style="width: 140px">
          <el-option label="待处理" :value="0" />
          <el-option label="处理中" :value="1" />
          <el-option label="已完成" :value="2" />
        </el-select>
        <el-select v-model="query.assigneeUserId" clearable placeholder="处理人" style="width: 180px">
          <el-option
            v-for="item in assignees"
            :key="item.id"
            :label="staffLabel(item)"
            :value="item.id"
          />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>

      <el-table :data="records" stripe style="width: 100%" v-loading="loading" :row-class-name="resolveRowClassName">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="反馈内容" min-width="280" />
        <el-table-column prop="aiCategory" label="AI 分类" min-width="100">
          <template #default="{ row }">
            <el-tag>{{ row.aiCategory || '其他' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskLevel" label="风险等级" min-width="110">
          <template #default="{ row }">
            <el-tag :type="riskLevelType(row.riskLevel)">{{ riskLevelText(row.riskLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="aiPriority" label="优先级" min-width="100">
          <template #default="{ row }">
            <el-tag :type="priorityType(row.aiPriority)">{{ priorityText(row.aiPriority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
            <el-tag v-if="row.linkedRecallId" type="danger" style="margin-left: 6px">已发起召回</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理人" min-width="160">
          <template #default="{ row }">
            {{ row.assigneeName || assigneeNameMap[row.assigneeUserId] || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="contact" label="联系方式" min-width="140" />
        <el-table-column prop="traceId" label="溯源码" min-width="130" />
        <el-table-column prop="createdAt" label="提交时间" min-width="180" />
        <el-table-column label="操作" min-width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openHandleDialog(row)">处理</el-button>
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

    <el-dialog v-model="dialogVisible" title="处理反馈" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="反馈内容">
          <div class="feedback-content">{{ currentRow?.content || '-' }}</div>
        </el-form-item>
        <el-form-item label="AI 判断">
          <div class="ai-summary">
            <div>分类：{{ currentRow?.aiCategory || '其他' }}</div>
            <div>风险等级：{{ riskLevelText(currentRow?.riskLevel) }}</div>
            <div>建议：{{ currentRow?.aiSummary || '暂无建议' }}</div>
          </div>
        </el-form-item>
        <el-form-item label="处理状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="分派给">
          <el-select v-model="form.assigneeUserId" clearable style="width: 100%">
            <el-option
              v-for="item in assignees"
              :key="item.id"
              :label="staffLabel(item)"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input
            v-model="form.handleNote"
            type="textarea"
            :rows="4"
            placeholder="记录处理结果、回访情况或后续计划"
          />
        </el-form-item>
        <el-form-item label="发起召回">
          <el-switch v-model="form.recallEnabled" />
          <div class="inline-tip">质量风险反馈可直接转入召回管理。</div>
        </el-form-item>
        <template v-if="form.recallEnabled">
          <el-form-item label="召回批次">
            <el-select v-model="form.recallBatchId" clearable style="width: 100%" placeholder="选择召回批次">
              <el-option
                v-for="item in batchOptions"
                :key="item.id"
                :label="batchNameMap[item.id]"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="召回级别">
            <el-select v-model="form.recallLevel" style="width: 100%">
              <el-option label="一级" :value="1" />
              <el-option label="二级" :value="2" />
              <el-option label="三级" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="召回原因">
            <el-input
              v-model="form.recallReason"
              type="textarea"
              :rows="3"
              placeholder="可补充召回原因；留空时默认带入反馈内容。"
            />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getFeedbackAssignees, getFeedbackOverview, getFeedbackTaskPage, handleFeedbackTask } from '../../api/modules/feedback'
import { getBatchList } from '../../api/modules/batch'
import PageHeader from '../../components/PageHeader.vue'

type FeedbackTask = {
  id: number
  content: string
  aiCategory?: string
  aiPriority?: number
  riskLevel?: string
  urgentFlag?: number
  aiSummary?: string
  status: number
  assigneeUserId?: number
  assigneeName?: string
  linkedTaskId?: number
  linkedRecallId?: number
  batchId?: number
  contact?: string
  traceId?: string
  createdAt?: string
  handleNote?: string
}

type StaffOption = {
  id: number
  username: string
  realName: string
}

type FeedbackOverview = {
  pendingCount: number
  highRiskPendingCount: number
  urgentPendingCount: number
  latestHighRiskRecords: FeedbackTask[]
}

const loading = ref(false)
const submitting = ref(false)
const total = ref(0)
const records = ref<FeedbackTask[]>([])
const assignees = ref<StaffOption[]>([])
const batchOptions = ref<any[]>([])
const overview = reactive<FeedbackOverview>({
  pendingCount: 0,
  highRiskPendingCount: 0,
  urgentPendingCount: 0,
  latestHighRiskRecords: [],
})

const dialogVisible = ref(false)
const currentRow = ref<FeedbackTask | null>(null)
const formRef = ref<FormInstance>()
const form = reactive({
  status: 0,
  assigneeUserId: null as number | null,
  handleNote: '',
  recallEnabled: false,
  recallBatchId: null as number | null,
  recallLevel: 1,
  recallReason: '',
})

const rules: FormRules = {
  status: [{ required: true, message: '请选择处理状态', trigger: 'change' }],
}

const query = reactive({
  keyword: '',
  category: '',
  riskLevel: '',
  priority: null as number | null,
  status: null as number | null,
  assigneeUserId: null as number | null,
  pageNum: 1,
  pageSize: 10,
})

const assigneeNameMap = computed(() => Object.fromEntries(assignees.value.map((item) => [item.id, item.realName || item.username])))
const batchNameMap = computed(() => Object.fromEntries(batchOptions.value.map((item: any) => [item.id, `${item.batchCode} / ${item.productName}`])))

function staffLabel(item: StaffOption) {
  return `${item.realName || item.username} (${item.username})`
}

function priorityText(priority?: number) {
  if (priority === 1) return '高'
  if (priority === 2) return '中'
  return '低'
}

function priorityType(priority?: number) {
  if (priority === 1) return 'danger'
  if (priority === 2) return 'warning'
  return 'info'
}

function riskLevelText(riskLevel?: string) {
  if (riskLevel === 'HIGH') return '高风险'
  if (riskLevel === 'MEDIUM') return '中风险'
  return '低风险'
}

function riskLevelType(riskLevel?: string) {
  if (riskLevel === 'HIGH') return 'danger'
  if (riskLevel === 'MEDIUM') return 'warning'
  return 'success'
}

function statusText(status: number) {
  if (status === 0) return '待处理'
  if (status === 1) return '处理中'
  return '已完成'
}

function statusType(status: number) {
  if (status === 0) return 'info'
  if (status === 1) return 'warning'
  return 'success'
}

function resolveRowClassName({ row }: { row: FeedbackTask }) {
  return row.riskLevel === 'HIGH' && row.status !== 2 ? 'high-risk-row' : ''
}

async function loadAssignees() {
  assignees.value = ((await getFeedbackAssignees()) || []) as StaffOption[]
}

async function loadBatchOptions() {
  batchOptions.value = (await getBatchList()) as any[]
}

async function loadOverview(showPopup = false) {
  const data = (await getFeedbackOverview()) as FeedbackOverview
  overview.pendingCount = data.pendingCount || 0
  overview.highRiskPendingCount = data.highRiskPendingCount || 0
  overview.urgentPendingCount = data.urgentPendingCount || 0
  overview.latestHighRiskRecords = data.latestHighRiskRecords || []

  if (showPopup && overview.highRiskPendingCount > 0) {
    const first = overview.latestHighRiskRecords[0]
    const latestText = first ? `最新一条：${first.content}` : ''
    await ElMessageBox.alert(
      `当前有 ${overview.highRiskPendingCount} 条高风险反馈待处理。${latestText}`,
      '高风险反馈提醒',
      { type: 'warning', confirmButtonText: '我知道了' },
    )
  }
}

async function loadData() {
  loading.value = true
  try {
    const data = await getFeedbackTaskPage({
      keyword: query.keyword || undefined,
      category: query.category || undefined,
      riskLevel: query.riskLevel || undefined,
      priority: query.priority,
      status: query.status,
      assigneeUserId: query.assigneeUserId,
      pageNum: query.pageNum,
      pageSize: query.pageSize,
    })
    records.value = (data.records || []) as FeedbackTask[]
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function openHandleDialog(row: FeedbackTask) {
  currentRow.value = row
  form.status = row.status
  form.assigneeUserId = row.assigneeUserId ?? null
  form.handleNote = row.handleNote || ''
  form.recallEnabled = !!row.linkedRecallId
  form.recallBatchId = row.batchId ?? null
  form.recallLevel = 1
  form.recallReason = row.linkedRecallId ? row.handleNote || '' : ''
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!currentRow.value || !formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    await handleFeedbackTask(currentRow.value.id, {
      status: form.status,
      assigneeUserId: form.assigneeUserId,
      handleNote: form.handleNote || undefined,
      recall: form.recallEnabled ? {
        enabled: true,
        batchId: form.recallBatchId,
        recallLevel: form.recallLevel,
        reason: form.recallReason || undefined,
      } : undefined,
    })
    ElMessage.success('处理结果已保存')
    dialogVisible.value = false
    await Promise.all([loadData(), loadOverview(false)])
  } finally {
    submitting.value = false
  }
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

onMounted(async () => {
  await loadAssignees()
  await loadBatchOptions()
  await Promise.all([loadData(), loadOverview(true)])
})
</script>

<style scoped>
.overview-card {
  margin-bottom: 16px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.overview-item {
  padding: 16px 18px;
  border-radius: 14px;
  background: #f8fafc;
  color: #334155;
}

.overview-item span {
  display: block;
  font-size: 13px;
  color: #64748b;
}

.overview-item strong {
  display: block;
  margin-top: 6px;
  font-size: 28px;
}

.overview-item.danger {
  background: #fff1f2;
  color: #9f1239;
}

.overview-item.warning {
  background: #fff7ed;
  color: #9a3412;
}

.overview-tip {
  margin-top: 14px;
  color: #b91c1c;
  font-size: 13px;
}

.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.feedback-content {
  padding: 10px 12px;
  border-radius: 10px;
  background: #f8fafc;
  color: #334155;
  line-height: 1.6;
}

.ai-summary {
  padding: 10px 12px;
  border-radius: 10px;
  background: #fef3c7;
  color: #7c2d12;
  line-height: 1.8;
}

.inline-tip {
  margin-left: 10px;
  color: #64748b;
  font-size: 12px;
}

:deep(.high-risk-row) {
  --el-table-tr-bg-color: #fff1f2;
}
</style>
