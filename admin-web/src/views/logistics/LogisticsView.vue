<template>
  <section class="page-section">
    <PageHeader title="流通链路" description="记录入库、出库、运输、中转、签收等节点，并支持新增、修改和删除。" />
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="batchId" filterable placeholder="选择批次" style="width: 320px" @change="loadRecords">
          <el-option v-for="item in batches" :key="item.id" :label="`${item.batchCode} - ${item.productName}`" :value="item.id" />
        </el-select>
        <el-button type="primary" :disabled="!batchId" @click="openCreate">新增节点</el-button>
        <el-button :disabled="!batchId" @click="loadRecords">刷新</el-button>
      </div>

      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="logisticsCode" label="流通单号" min-width="180" />
        <el-table-column prop="nodeType" label="节点类型" width="120" />
        <el-table-column prop="nodeName" label="节点名称" min-width="160" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="operationTime" label="操作时间" min-width="180" />
        <el-table-column prop="location" label="地点" min-width="220" />
        <el-table-column prop="temperature" label="温度" width="100" />
        <el-table-column prop="humidity" label="湿度" width="100" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">修改</el-button>
            <el-button v-if="authStore.isAdmin" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增流通节点' : '修改流通节点'" width="680px" destroy-on-close>
      <el-form :model="form" label-width="96px">
        <el-form-item label="节点类型">
          <el-select v-model="form.nodeType" filterable allow-create default-first-option placeholder="请选择节点类型">
            <el-option v-for="item in nodeTypeOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="节点名称">
          <el-input v-model="form.nodeName" placeholder="例如 仓库A / 配送中心 / 门店前台" />
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker v-model="form.operationTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="form.operatorName" placeholder="默认当前登录用户" />
        </el-form-item>
        <el-form-item label="地点">
          <div class="location-row">
            <el-input v-model="form.location" placeholder="可手填，或使用当前位置" />
            <el-button :loading="locating" @click="fillCurrentLocation">定位当前位置</el-button>
          </div>
          <div class="field-tip">浏览器授权后会自动填入当前位置经纬度。</div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="补充说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '../../components/PageHeader.vue'
import { getBatchList } from '../../api/modules/batch'
import { createLogisticsRecord, deleteLogisticsRecord, getLogisticsRecords, type LogisticsRecord, updateLogisticsRecord } from '../../api/modules/logistics'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const batches = ref<any[]>([])
const batchId = ref<number>()
const records = ref<LogisticsRecord[]>([])
const loading = ref(false)
const locating = ref(false)
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number | null>(null)
const nodeTypeOptions = ['入库', '出库', '运输', '中转', '到店', '签收', '退货', '异常处理']
const form = reactive({
  batchId: 0,
  nodeType: '',
  nodeName: '',
  operationTime: '',
  operatorName: '',
  location: '',
  remark: '',
})

function toDateTimeValue(date = new Date()) {
  const pad = (value: number) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

function defaultOperatorName() {
  return authStore.user?.realName || authStore.user?.username || ''
}

function resetForm() {
  form.batchId = batchId.value || 0
  form.nodeType = nodeTypeOptions[0]
  form.nodeName = ''
  form.operationTime = toDateTimeValue()
  form.operatorName = defaultOperatorName()
  form.location = ''
  form.remark = ''
}

async function loadBatches() {
  batches.value = (await getBatchList()) as any[]
}

async function loadRecords() {
  if (!batchId.value) return
  loading.value = true
  try {
    records.value = (await getLogisticsRecords({ batchId: batchId.value })) as LogisticsRecord[]
  } finally {
    loading.value = false
  }
}

function openCreate() {
  if (!batchId.value) return
  dialogMode.value = 'create'
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: LogisticsRecord) {
  dialogMode.value = 'edit'
  editingId.value = row.id
  form.batchId = row.batchId
  form.nodeType = row.nodeType
  form.nodeName = row.nodeName
  form.operationTime = row.operationTime
  form.operatorName = row.operatorName || ''
  form.location = row.location || ''
  form.remark = row.remark || ''
  dialogVisible.value = true
}

async function fillCurrentLocation() {
  if (!navigator.geolocation) {
    ElMessage.error('当前浏览器不支持定位')
    return
  }
  locating.value = true
  navigator.geolocation.getCurrentPosition(
    (position) => {
      const { latitude, longitude, accuracy } = position.coords
      form.location = `纬度 ${latitude.toFixed(6)}, 经度 ${longitude.toFixed(6)}`
      if (accuracy) {
        form.location += `, 精度约 ${Math.round(accuracy)} 米`
      }
      locating.value = false
      ElMessage.success('已填入当前位置')
    },
    (error) => {
      locating.value = false
      ElMessage.error(error.message || '定位失败，请检查浏览器定位权限')
    },
    { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 },
  )
}

async function submit() {
  const payload = { ...form, batchId: form.batchId }
  if (dialogMode.value === 'create') {
    await createLogisticsRecord(payload)
    ElMessage.success('保存成功')
  } else if (editingId.value != null) {
    await updateLogisticsRecord(editingId.value, payload)
    ElMessage.success('修改成功')
  }
  dialogVisible.value = false
  await loadRecords()
}

async function handleDelete(row: LogisticsRecord) {
  await ElMessageBox.confirm('确定删除这条流通节点吗？', '删除确认', { type: 'warning' })
  await deleteLogisticsRecord(row.id)
  ElMessage.success('删除成功')
  await loadRecords()
}

onMounted(loadBatches)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.location-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 12px;
  width: 100%;
}

.field-tip {
  margin-top: 8px;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.5;
}
</style>
