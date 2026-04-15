<template>
  <section class="page-section">
    <PageHeader title="生产记录" description="生产记录分页、筛选、新增、编辑、删除已经接通。" />

    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.batchId" clearable placeholder="批次筛选" style="width: 240px">
          <el-option v-for="item in batchOptions" :key="item.id" :label="batchLabel(item)" :value="item.id" />
        </el-select>
        <el-input v-model="query.recordType" placeholder="记录类型筛选" clearable style="max-width: 220px" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreateDialog">新增记录</el-button>
      </div>

      <el-table :data="records" stripe style="width: 100%" v-loading="loading">
        <el-table-column label="批次" min-width="220">
          <template #default="{ row }">{{ batchNameMap[row.batchId] || `批次ID ${row.batchId}` }}</template>
        </el-table-column>
        <el-table-column prop="recordType" label="记录类型" min-width="120" />
        <el-table-column prop="operationTime" label="操作时间" min-width="180" />
        <el-table-column prop="operatorName" label="操作人" min-width="120" />
        <el-table-column prop="materialName" label="投入物" min-width="140" />
        <el-table-column prop="dosage" label="用量" min-width="120" />
        <el-table-column prop="content" label="记录内容" min-width="260" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增生产记录' : '编辑生产记录'" width="820px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <div class="form-grid">
          <el-form-item label="批次" prop="batchId"><el-select v-model="form.batchId" placeholder="请选择批次" style="width: 100%"><el-option v-for="item in batchOptions" :key="item.id" :label="batchLabel(item)" :value="item.id" /></el-select></el-form-item>
          <el-form-item label="记录类型" prop="recordType"><el-input v-model="form.recordType" placeholder="如 施肥、浇水、采收" /></el-form-item>
          <el-form-item label="操作时间" prop="operationTime"><el-date-picker v-model="form.operationTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" /></el-form-item>
          <el-form-item label="操作人"><el-input v-model="form.operatorName" placeholder="请输入操作人" /></el-form-item>
          <el-form-item label="投入物"><el-input v-model="form.materialName" placeholder="请输入投入物名称" /></el-form-item>
          <el-form-item label="用量"><el-input v-model="form.dosage" placeholder="请输入用量说明" /></el-form-item>
          <el-form-item label="附件链接"><el-input v-model="form.attachmentUrl" placeholder="请输入附件 URL" /></el-form-item>
        </div>
        <el-form-item label="记录内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入记录内容" /></el-form-item>
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
import { getBatchList } from '../../api/modules/batch'
import { createProductionRecord, deleteProductionRecord, getProductionRecordPage, updateProductionRecord } from '../../api/modules/record'
import PageHeader from '../../components/PageHeader.vue'

type BatchOption = { id: number; batchCode: string; productName: string }
type ProductionRecord = { id: number; batchId: number; recordType: string; operationTime: string; operatorName?: string; materialName?: string; dosage?: string; content: string; attachmentUrl?: string }

const loading = ref(false)
const submitting = ref(false)
const total = ref(0)
const records = ref<ProductionRecord[]>([])
const batchOptions = ref<BatchOption[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const query = reactive({ batchId: null as number | null, recordType: '', pageNum: 1, pageSize: 10 })
const form = reactive({ batchId: null as number | null, recordType: '', operationTime: '', operatorName: '', materialName: '', dosage: '', content: '', attachmentUrl: '' })

const rules: FormRules = {
  batchId: [{ required: true, message: '请选择批次', trigger: 'change' }],
  recordType: [{ required: true, message: '请输入记录类型', trigger: 'blur' }],
  operationTime: [{ required: true, message: '请选择操作时间', trigger: 'change' }],
  content: [{ required: true, message: '请输入记录内容', trigger: 'blur' }],
}

const batchNameMap = computed(() => Object.fromEntries(batchOptions.value.map((item) => [item.id, `${item.batchCode} / ${item.productName}`])))
function batchLabel(item: BatchOption) { return `${item.batchCode} / ${item.productName}` }
async function loadBatchOptions() { const list = await getBatchList(); batchOptions.value = (list || []).map((item: any) => ({ id: item.id, batchCode: item.batchCode, productName: item.productName })) }
async function loadData() { loading.value = true; try { const data = await getProductionRecordPage(query); records.value = data.records || []; total.value = data.total || 0 } finally { loading.value = false } }
function resetForm() { form.batchId = null; form.recordType = ''; form.operationTime = ''; form.operatorName = ''; form.materialName = ''; form.dosage = ''; form.content = ''; form.attachmentUrl = ''; editingId.value = null }
function openCreateDialog() { dialogMode.value = 'create'; resetForm(); dialogVisible.value = true }
function openEditDialog(row: ProductionRecord) { dialogMode.value = 'edit'; editingId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
async function handleSubmit() { if (!formRef.value) return; await formRef.value.validate(); submitting.value = true; try { if (dialogMode.value === 'create') { await createProductionRecord({ ...form }); ElMessage.success('生产记录创建成功') } else if (editingId.value != null) { await updateProductionRecord(editingId.value, { ...form }); ElMessage.success('生产记录更新成功') } dialogVisible.value = false; loadData() } finally { submitting.value = false } }
async function handleDelete(row: ProductionRecord) { await ElMessageBox.confirm('确定删除这条生产记录吗？', '删除确认', { type: 'warning' }); await deleteProductionRecord(row.id); ElMessage.success('生产记录删除成功'); if (records.value.length === 1 && query.pageNum > 1) query.pageNum -= 1; loadData() }
function handleCurrentChange(pageNum: number) { query.pageNum = pageNum; loadData() }
function handleSizeChange(pageSize: number) { query.pageSize = pageSize; query.pageNum = 1; loadData() }
onMounted(async () => { await loadBatchOptions(); await loadData() })
</script>
