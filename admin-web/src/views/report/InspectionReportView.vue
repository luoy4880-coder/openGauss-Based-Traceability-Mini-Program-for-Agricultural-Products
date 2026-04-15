<template>
  <section class="page-section">
    <PageHeader title="质检报告" description="质检报告分页、筛选、新增、编辑、删除已经接通。" />

    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="query.batchId" clearable placeholder="批次筛选" style="width: 240px">
          <el-option v-for="item in batchOptions" :key="item.id" :label="batchLabel(item)" :value="item.id" />
        </el-select>
        <el-select v-model="query.resultStatus" clearable placeholder="检测结果" style="width: 180px">
          <el-option label="合格" :value="1" />
          <el-option label="不合格" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreateDialog">新增报告</el-button>
      </div>

      <el-table :data="records" stripe style="width: 100%" v-loading="loading">
        <el-table-column label="批次" min-width="220">
          <template #default="{ row }">{{ batchNameMap[row.batchId] || `批次ID ${row.batchId}` }}</template>
        </el-table-column>
        <el-table-column prop="reportNo" label="报告编号" min-width="160" />
        <el-table-column prop="agencyName" label="检测机构" min-width="180" />
        <el-table-column prop="inspectorName" label="检测人" min-width="120" />
        <el-table-column prop="inspectionTime" label="检测时间" min-width="180" />
        <el-table-column label="结果" min-width="110">
          <template #default="{ row }">
            <el-tag :type="row.resultStatus === 1 ? 'success' : 'danger'">
              {{ row.resultStatus === 1 ? '合格' : '不合格' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="conclusion" label="结论" min-width="240" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增质检报告' : '编辑质检报告'" width="820px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <div class="form-grid">
          <el-form-item label="批次" prop="batchId"><el-select v-model="form.batchId" placeholder="请选择批次" style="width: 100%"><el-option v-for="item in batchOptions" :key="item.id" :label="batchLabel(item)" :value="item.id" /></el-select></el-form-item>
          <el-form-item label="报告编号" prop="reportNo"><el-input v-model="form.reportNo" :disabled="dialogMode === 'edit'" placeholder="请输入报告编号" /></el-form-item>
          <el-form-item label="检测机构" prop="agencyName"><el-input v-model="form.agencyName" placeholder="请输入检测机构" /></el-form-item>
          <el-form-item label="检测人"><el-input v-model="form.inspectorName" placeholder="请输入检测人" /></el-form-item>
          <el-form-item label="检测时间" prop="inspectionTime"><el-date-picker v-model="form.inspectionTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" /></el-form-item>
          <el-form-item label="检测结果" prop="resultStatus"><el-select v-model="form.resultStatus" placeholder="请选择检测结果" style="width: 100%"><el-option label="合格" :value="1" /><el-option label="不合格" :value="0" /></el-select></el-form-item>
          <el-form-item label="报告链接"><el-input v-model="form.reportUrl" placeholder="请输入报告 URL" /></el-form-item>
        </div>
        <el-form-item label="检测结论"><el-input v-model="form.conclusion" type="textarea" :rows="4" placeholder="请输入检测结论" /></el-form-item>
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
import { createInspectionReport, deleteInspectionReport, getInspectionReportPage, updateInspectionReport } from '../../api/modules/report'
import PageHeader from '../../components/PageHeader.vue'

type BatchOption = { id: number; batchCode: string; productName: string }
type InspectionReport = { id: number; batchId: number; reportNo: string; agencyName: string; inspectorName?: string; inspectionTime: string; resultStatus: number; conclusion?: string; reportUrl?: string }

const loading = ref(false)
const submitting = ref(false)
const total = ref(0)
const records = ref<InspectionReport[]>([])
const batchOptions = ref<BatchOption[]>([])
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const query = reactive({ batchId: null as number | null, resultStatus: null as number | null, pageNum: 1, pageSize: 10 })
const form = reactive({ batchId: null as number | null, reportNo: '', agencyName: '', inspectorName: '', inspectionTime: '', resultStatus: 1, conclusion: '', reportUrl: '' })

const rules: FormRules = {
  batchId: [{ required: true, message: '请选择批次', trigger: 'change' }],
  reportNo: [{ required: true, message: '请输入报告编号', trigger: 'blur' }],
  agencyName: [{ required: true, message: '请输入检测机构', trigger: 'blur' }],
  inspectionTime: [{ required: true, message: '请选择检测时间', trigger: 'change' }],
  resultStatus: [{ required: true, message: '请选择检测结果', trigger: 'change' }],
}

const batchNameMap = computed(() => Object.fromEntries(batchOptions.value.map((item) => [item.id, `${item.batchCode} / ${item.productName}`])))
function batchLabel(item: BatchOption) { return `${item.batchCode} / ${item.productName}` }
async function loadBatchOptions() { const list = await getBatchList(); batchOptions.value = (list || []).map((item: any) => ({ id: item.id, batchCode: item.batchCode, productName: item.productName })) }
async function loadData() { loading.value = true; try { const data = await getInspectionReportPage(query); records.value = data.records || []; total.value = data.total || 0 } finally { loading.value = false } }
function resetForm() { form.batchId = null; form.reportNo = ''; form.agencyName = ''; form.inspectorName = ''; form.inspectionTime = ''; form.resultStatus = 1; form.conclusion = ''; form.reportUrl = ''; editingId.value = null }
function openCreateDialog() { dialogMode.value = 'create'; resetForm(); dialogVisible.value = true }
function openEditDialog(row: InspectionReport) { dialogMode.value = 'edit'; editingId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
async function handleSubmit() { if (!formRef.value) return; await formRef.value.validate(); submitting.value = true; try { if (dialogMode.value === 'create') { await createInspectionReport({ ...form }); ElMessage.success('质检报告创建成功') } else if (editingId.value != null) { await updateInspectionReport(editingId.value, { batchId: form.batchId, agencyName: form.agencyName, inspectorName: form.inspectorName, inspectionTime: form.inspectionTime, resultStatus: form.resultStatus, conclusion: form.conclusion, reportUrl: form.reportUrl }); ElMessage.success('质检报告更新成功') } dialogVisible.value = false; loadData() } finally { submitting.value = false } }
async function handleDelete(row: InspectionReport) { await ElMessageBox.confirm('确定删除这条质检报告吗？', '删除确认', { type: 'warning' }); await deleteInspectionReport(row.id); ElMessage.success('质检报告删除成功'); if (records.value.length === 1 && query.pageNum > 1) query.pageNum -= 1; loadData() }
function handleCurrentChange(pageNum: number) { query.pageNum = pageNum; loadData() }
function handleSizeChange(pageSize: number) { query.pageSize = pageSize; query.pageNum = 1; loadData() }
onMounted(async () => { await loadBatchOptions(); await loadData() })
</script>
