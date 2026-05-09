<template>
  <section class="page-section">
    <PageHeader title="基地管理" description="管理基地档案，基地编号可自动生成，基地名称中的省市区县可自动解析。" />
    <el-card shadow="never">
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="搜索基地编号或名称" clearable style="max-width: 280px" @keyup.enter="loadData" />
        <el-select v-model="query.status" clearable placeholder="状态筛选" style="width: 160px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openCreateDialog">新增基地</el-button>
      </div>

      <el-table :data="records" stripe v-loading="loading">
        <el-table-column prop="baseCode" label="基地编号" min-width="160" />
        <el-table-column prop="baseName" label="基地名称" min-width="180" />
        <el-table-column prop="managerName" label="负责人" min-width="120" />
        <el-table-column prop="contactPhone" label="联系电话" min-width="140" />
        <el-table-column label="地址" min-width="240">
          <template #default="{ row }">{{ [row.province, row.city, row.district, row.address].filter(Boolean).join('') || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag></template>
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

    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增基地' : '编辑基地'" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="基地编号">
          <el-input v-model="form.baseCode" :disabled="dialogMode === 'edit'" placeholder="留空则系统自动生成，如 BASE-20260506-001" />
        </el-form-item>
        <el-form-item label="基地名称" prop="baseName">
          <el-input v-model="form.baseName" placeholder="如 四川省成都市郫都区雨佳草莓基地" @blur="autoFillAddress" />
        </el-form-item>
        <el-form-item label="负责人"><el-input v-model="form.managerName" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.contactPhone" /></el-form-item>
        <el-form-item label="省市区">
          <el-input v-model="form.province" placeholder="省 / 直辖市" />
          <el-input v-model="form.city" placeholder="市 / 州" style="margin-top: 8px" />
          <el-input v-model="form.district" placeholder="区 / 县" style="margin-top: 8px" />
        </el-form-item>
        <el-form-item label="详细地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="面积"><el-input-number v-model="form.acreage" :min="0" /></el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">停用</el-radio></el-radio-group>
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
import PageHeader from '../../components/PageHeader.vue'
import { createBase, deleteBase, getBasePage, updateBase } from '../../api/modules/base'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const dialogMode = ref<'create' | 'edit'>('create')
const currentId = ref<number>()
const records = ref<any[]>([])
const total = ref(0)
const query = reactive({ keyword: '', status: null as number | null, pageNum: 1, pageSize: 10 })
const form = reactive({ baseCode: '', baseName: '', managerName: '', contactPhone: '', province: '', city: '', district: '', address: '', acreage: 0 as number | null, status: 1 })
const rules: FormRules = {
  baseName: [{ required: true, message: '请输入基地名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

function parseAddress(text: string) {
  const source = (text || '').trim()
  const province = source.match(/([\u4e00-\u9fa5]{2,}(?:省|自治区|特别行政区)|北京市|天津市|上海市|重庆市)/)?.[1] || ''
  const afterProvince = province ? source.slice(source.indexOf(province) + province.length) : source
  const city = afterProvince.match(/([\u4e00-\u9fa5]{2,}(?:市|州|盟|地区))/)?.[1] || ''
  const afterCity = city ? afterProvince.slice(afterProvince.indexOf(city) + city.length) : afterProvince
  const district = afterCity.match(/([\u4e00-\u9fa5]{2,}(?:区|县|市|旗))/)?.[1] || ''
  return { province, city, district }
}

function autoFillAddress() {
  if (dialogMode.value !== 'create') return
  const parsed = parseAddress(`${form.baseName} ${form.address}`)
  let filled = false
  if (!form.province && parsed.province) { form.province = parsed.province; filled = true }
  if (!form.city && parsed.city) { form.city = parsed.city; filled = true }
  if (!form.district && parsed.district) { form.district = parsed.district; filled = true }
  if (filled) ElMessage.success('已自动解析省市区县')
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getBasePage(query)
    records.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}
function resetForm() { Object.assign(form, { baseCode: '', baseName: '', managerName: '', contactPhone: '', province: '', city: '', district: '', address: '', acreage: 0, status: 1 }) }
function openCreateDialog() { dialogMode.value = 'create'; currentId.value = undefined; resetForm(); dialogVisible.value = true }
function openEditDialog(row: any) { dialogMode.value = 'edit'; currentId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  autoFillAddress()
  submitting.value = true
  try {
    if (dialogMode.value === 'create') { await createBase({ ...form }); ElMessage.success('新增成功') }
    else { await updateBase(currentId.value!, { ...form }); ElMessage.success('更新成功') }
    dialogVisible.value = false
    await loadData()
  } finally { submitting.value = false }
}
async function handleDelete(row: any) { await ElMessageBox.confirm(`确定删除基地“${row.baseName}”吗？`, '删除确认', { type: 'warning' }); await deleteBase(row.id); ElMessage.success('删除成功'); await loadData() }
function handleCurrentChange(v: number) { query.pageNum = v; loadData() }
function handleSizeChange(v: number) { query.pageSize = v; query.pageNum = 1; loadData() }
onMounted(loadData)
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; align-items: center; margin-bottom: 16px; flex-wrap: wrap; }
</style>
