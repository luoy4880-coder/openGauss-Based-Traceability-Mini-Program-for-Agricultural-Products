<template>
  <section class="page-section">
    <PageHeader title="快速导入作物信息" description="单独导入基地、批次、生产记录、流通链路、质检信息等完整批次包。" />

    <el-card shadow="never">
      <div class="import-intro">
        <div class="intro-block">
          <strong>导入内容</strong>
          <span>基地、批次、生产记录、物流链路、质检报告、一物一码数量</span>
        </div>
        <div class="intro-block">
          <strong>文件格式</strong>
          <span>上传 `full-batch-import-*.json` 结构化文件</span>
        </div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="import-form">
        <el-form-item label="导入文件" prop="fileName">
          <el-upload :auto-upload="false" :limit="1" :show-file-list="true" :on-change="handleFileChange" :on-remove="handleFileRemove">
            <el-button type="primary">选择 JSON 文件</el-button>
          </el-upload>
          <div class="upload-tip">
            推荐直接使用 [docs/test-imports](F:/GraduationProject-yujia/yujia-test/docs/test-imports/README.md) 里的完整批次导入包。
          </div>
        </el-form-item>
      </el-form>

      <div class="action-row">
        <el-button type="primary" :loading="submitting" @click="handleSubmit">开始导入</el-button>
      </div>

      <el-result
        v-if="result"
        icon="success"
        title="导入完成"
        :sub-title="`基地 ${result.baseCode} / 批次 ${result.batchCode} 已创建`"
      >
        <template #extra>
          <div class="result-grid">
            <div class="result-item"><span>生产记录</span><strong>{{ result.productionRecordCount }}</strong></div>
            <div class="result-item"><span>物流链路</span><strong>{{ result.logisticsRecordCount }}</strong></div>
            <div class="result-item"><span>单品码</span><strong>{{ result.itemCount }}</strong></div>
            <div class="result-item"><span>质检报告</span><strong>{{ result.inspectionReportNo }}</strong></div>
          </div>
          <el-alert v-if="result.createdRiskTask" title="该批次质检结果异常，系统已自动生成高优先级处理任务。" type="warning" :closable="false" show-icon />
        </template>
      </el-result>
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import PageHeader from '../../components/PageHeader.vue'
import { quickImportCropInfo } from '../../api/modules/import'

const formRef = ref<FormInstance>()
const submitting = ref(false)
const result = ref<any | null>(null)
const form = reactive({
  file: null as File | null,
  fileName: '',
})

const rules: FormRules = {
  fileName: [{ required: true, message: '请先选择导入文件', trigger: 'change' }],
}

function handleFileChange(file: UploadFile) {
  form.file = file.raw || null
  form.fileName = file.name || ''
}

function handleFileRemove() {
  form.file = null
  form.fileName = ''
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  if (!form.file) return
  submitting.value = true
  try {
    result.value = await quickImportCropInfo(form.file)
    ElMessage.success('作物信息导入完成')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.import-intro {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.intro-block {
  padding: 16px 18px;
  border-radius: 14px;
  background: #f8fafc;
  color: #334155;
}

.intro-block strong {
  display: block;
  margin-bottom: 8px;
}

.import-form {
  margin-top: 8px;
}

.upload-tip {
  margin-top: 8px;
  color: #64748b;
  font-size: 12px;
}

.action-row {
  margin-top: 8px;
}

.result-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.result-item {
  padding: 14px 16px;
  border-radius: 12px;
  background: #eff6ff;
  color: #1e3a8a;
}

.result-item span {
  display: block;
  font-size: 12px;
}

.result-item strong {
  display: block;
  margin-top: 6px;
  font-size: 20px;
}
</style>
