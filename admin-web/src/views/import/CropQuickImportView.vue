<template>
  <section class="page-section import-page">
    <PageHeader title="快速导入作物信息" description="导入基地、批次、生产记录、流通链路、质检信息等完整批次包。" />

    <div class="hero-grid">
      <el-card shadow="never" class="hero-card">
        <div class="hero-kicker">Batch Import</div>
        <div class="hero-title">一次完成整批作物档案导入</div>
        <p class="hero-desc">
          适合集中录入新作物批次。导入后会自动创建基地、批次、生产记录、物流链路、质检信息和单品码。
        </p>
        <div class="hero-tags">
          <span class="hero-tag">基地档案</span>
          <span class="hero-tag">生产记录</span>
          <span class="hero-tag">流通链路</span>
          <span class="hero-tag">质检结果</span>
        </div>
      </el-card>

      <el-card shadow="never" class="status-card">
        <div class="status-label">当前状态</div>
        <div class="status-title">{{ form.fileName ? '已选择导入文件' : '等待上传文件' }}</div>
        <p class="status-desc">
          {{ form.fileName ? '文件已就绪，可以直接开始导入。' : '选择一个批次文件后，系统会自动完成整链路创建。' }}
        </p>

        <div v-if="form.fileName" class="selected-file">
          <span class="selected-file-label">当前文件</span>
          <strong>{{ form.fileName }}</strong>
        </div>

        <div class="status-metrics">
          <div class="metric-box">
            <span>支持对象</span>
            <strong>完整批次</strong>
          </div>
          <div class="metric-box">
            <span>导入方式</span>
            <strong>单文件</strong>
          </div>
        </div>
      </el-card>
    </div>

    <el-card shadow="never" class="upload-card">
      <div class="upload-head">
        <div>
          <div class="panel-title">导入文件</div>
          <p class="panel-desc">上传后将统一创建该批次的核心业务数据。</p>
        </div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" class="import-form">
        <el-form-item prop="fileName" class="upload-form-item">
          <el-upload
            class="upload-dropzone"
            drag
            :auto-upload="false"
            :limit="1"
            :show-file-list="true"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
          >
            <el-icon class="upload-icon"><UploadFilled /></el-icon>
            <div class="upload-main-text">拖拽文件到这里，或点击选择文件</div>
            <div class="upload-sub-text">导入成功后会自动生成对应批次档案</div>
          </el-upload>
        </el-form-item>
      </el-form>

      <div class="action-row">
        <div class="action-hint">
          <span class="hint-dot"></span>
          {{ form.fileName ? '文件已准备完成' : '请先选择要导入的文件' }}
        </div>
        <el-button type="primary" size="large" :loading="submitting" :disabled="!form.file" @click="handleSubmit">
          开始导入
        </el-button>
      </div>
    </el-card>

    <el-card v-if="result" shadow="never" class="result-card">
      <div class="result-head">
        <div>
          <div class="result-kicker">Import Result</div>
          <div class="result-title">导入完成</div>
          <p class="result-desc">基地 {{ result.baseCode }} / 批次 {{ result.batchCode }} 已创建。</p>
        </div>
        <el-tag type="success" effect="dark" round>已完成</el-tag>
      </div>

      <div class="result-grid">
        <div class="result-item">
          <span>生产记录</span>
          <strong>{{ result.productionRecordCount }}</strong>
        </div>
        <div class="result-item">
          <span>物流链路</span>
          <strong>{{ result.logisticsRecordCount }}</strong>
        </div>
        <div class="result-item">
          <span>单品码</span>
          <strong>{{ result.itemCount }}</strong>
        </div>
        <div class="result-item">
          <span>质检报告</span>
          <strong>{{ result.inspectionReportNo }}</strong>
        </div>
      </div>

      <el-alert
        v-if="result.createdRiskTask"
        title="该批次质检结果异常，系统已自动生成高优先级处理任务。"
        type="warning"
        :closable="false"
        show-icon
      />
    </el-card>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
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
.import-page {
  display: grid;
  gap: 16px;
}

.hero-grid {
  display: grid;
  grid-template-columns: 1.8fr 1fr;
  gap: 16px;
}

.hero-card,
.status-card,
.upload-card,
.result-card {
  border: 1px solid #e8eef5;
}

.hero-card {
  overflow: hidden;
  background:
    radial-gradient(circle at top right, rgba(125, 152, 88, 0.18), transparent 30%),
    linear-gradient(135deg, #f6fbf4 0%, #eef7f1 100%);
}

.hero-kicker,
.result-kicker {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #6b7f52;
}

.hero-title,
.status-title,
.result-title {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 700;
  line-height: 1.25;
  color: #24374e;
}

.hero-desc,
.status-desc,
.result-desc,
.panel-desc {
  margin: 14px 0 0;
  color: #526277;
  line-height: 1.75;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

.hero-tag {
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(126, 164, 93, 0.14);
  color: #49642a;
  font-size: 13px;
  font-weight: 600;
}

.status-card {
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

.status-label,
.panel-title {
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: #64748b;
}

.selected-file {
  margin-top: 18px;
  padding: 14px 16px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.selected-file-label {
  display: block;
  margin-bottom: 6px;
  font-size: 12px;
  color: #64748b;
}

.selected-file strong {
  display: block;
  color: #24374e;
  word-break: break-all;
}

.status-metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.metric-box,
.result-item {
  padding: 14px 16px;
  border-radius: 14px;
  background: #f8fafc;
}

.metric-box span,
.result-item span {
  display: block;
  font-size: 12px;
  color: #64748b;
}

.metric-box strong,
.result-item strong {
  display: block;
  margin-top: 8px;
  font-size: 20px;
  color: #24374e;
}

.upload-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.import-form {
  margin-top: 8px;
}

:deep(.upload-form-item .el-form-item__content) {
  width: 100%;
}

.upload-dropzone {
  width: 100%;
}

:deep(.upload-dropzone .el-upload),
:deep(.upload-dropzone .el-upload-dragger) {
  width: 100%;
  border-radius: 18px;
}

:deep(.upload-dropzone .el-upload-dragger) {
  padding: 34px 20px;
  border: 1px dashed #b8c6d8;
  background:
    linear-gradient(180deg, #fcfdff 0%, #f7fafc 100%);
  transition: all 0.2s ease;
}

:deep(.upload-dropzone .el-upload-dragger:hover) {
  border-color: #7ea45d;
  background: linear-gradient(180deg, #f7fbf4 0%, #f3f8f4 100%);
}

.upload-icon {
  font-size: 34px;
  color: #7ea45d;
}

.upload-main-text {
  margin-top: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #24374e;
}

.upload-sub-text {
  margin-top: 8px;
  font-size: 13px;
  color: #64748b;
}

.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-top: 18px;
}

.action-hint {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: #526277;
  font-size: 14px;
}

.hint-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #7ea45d;
  box-shadow: 0 0 0 6px rgba(126, 164, 93, 0.12);
}

.result-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.result-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin: 18px 0 16px;
}

.result-item {
  background: linear-gradient(180deg, #eff6ff 0%, #f8fbff 100%);
  color: #1e3a8a;
}

@media (max-width: 960px) {
  .hero-grid,
  .result-grid {
    grid-template-columns: 1fr;
  }

  .status-metrics {
    grid-template-columns: 1fr;
  }

  .action-row,
  .result-head {
    flex-direction: column;
    align-items: stretch;
  }

  .action-row :deep(.el-button) {
    width: 100%;
  }
}
</style>
