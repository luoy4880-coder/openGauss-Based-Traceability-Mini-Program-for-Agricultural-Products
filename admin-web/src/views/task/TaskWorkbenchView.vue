<template>
  <section class="page-section">
    <PageHeader title="智能工作台" description="查看系统自动生成的待办任务，优先处理高风险事项。" />
    <el-card shadow="never">
      <div class="toolbar">
        <el-input v-model="query.keyword" clearable placeholder="搜索任务标题或描述" style="width: 260px" />
        <el-select v-model="query.status" clearable placeholder="任务状态" style="width: 160px">
          <el-option label="全部状态" :value="null" />
          <el-option label="待处理" :value="0" />
          <el-option label="处理中" :value="1" />
          <el-option label="已完成" :value="2" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="title" label="任务标题" min-width="220" />
        <el-table-column prop="description" label="任务描述" min-width="320" show-overflow-tooltip />
        <el-table-column label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="priorityType(row.priority)">{{ priorityText(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理人" width="160">
          <template #default="{ row }">{{ row.assigneeName || '未认领' }}</template>
        </el-table-column>
        <el-table-column label="认领时间" min-width="170">
          <template #default="{ row }">{{ formatDateTime(row.claimedAt) }}</template>
        </el-table-column>
        <el-table-column label="完成人" width="160">
          <template #default="{ row }">{{ row.completedByName || '-' }}</template>
        </el-table-column>
        <el-table-column label="完成时间" min-width="170">
          <template #default="{ row }">{{ formatDateTime(row.completedAt) }}</template>
        </el-table-column>
        <el-table-column prop="dueAt" label="截止时间" min-width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="warning" @click="handleClaim(row)">认领</el-button>
            <el-button v-if="row.status !== 2" link type="success" @click="handleComplete(row)">完成</el-button>
            <el-button v-if="row.status === 2" link type="info" @click="handleReopen(row)">重新打开</el-button>
            <el-button v-if="row.bizType === 'BATCH'" link type="primary" @click="openBatch(row.bizId)">查看批次</el-button>
            <el-button v-if="row.bizType === 'FEEDBACK'" link type="primary" @click="openFeedbackPage()">查看反馈</el-button>
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
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import { claimTask, completeTask, getTaskPage, reopenTask } from '../../api/modules/task'

const router = useRouter()
const loading = ref(false)
const total = ref(0)
const records = ref<any[]>([])
const query = reactive({ keyword: '', status: null as number | null, pageNum: 1, pageSize: 10 })

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

function statusText(status?: number) {
  if (status === 1) return '处理中'
  if (status === 2) return '已完成'
  return '待处理'
}

function statusType(status?: number) {
  if (status === 1) return 'warning'
  if (status === 2) return 'success'
  return 'info'
}

function formatDateTime(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 19)
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getTaskPage(query)
    records.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

async function handleClaim(row: any) {
  await claimTask(row.id)
  ElMessage.success('已认领，任务已进入处理中')
  query.status = null
  loadData()
}

async function handleComplete(row: any) {
  await completeTask(row.id)
  ElMessage.success('任务已完成')
  loadData()
}

async function handleReopen(row: any) {
  await reopenTask(row.id)
  ElMessage.success('任务已重新打开')
  query.status = null
  loadData()
}

function openBatch(id: number) {
  router.push(`/batches/${id}/archive`)
}

function openFeedbackPage() {
  router.push('/feedback-tasks')
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

onMounted(loadData)
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
