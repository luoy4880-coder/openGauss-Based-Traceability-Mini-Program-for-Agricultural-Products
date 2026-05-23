<template>
  <section class="page-section" v-loading="loading">
    <PageHeader title="风险中心" description="聚合高风险批次、异常扫码、待处理反馈与档案完整度。" />
    <div class="kpi-grid" v-if="overview">
      <el-card shadow="never" class="kpi-card"><span>开放任务</span><strong>{{ overview.openTaskCount }}</strong></el-card>
      <el-card shadow="never" class="kpi-card"><span>高风险批次</span><strong>{{ overview.highRiskBatchCount }}</strong></el-card>
      <el-card shadow="never" class="kpi-card"><span>低完整度批次</span><strong>{{ overview.lowCompletenessBatchCount }}</strong></el-card>
      <el-card shadow="never" class="kpi-card"><span>异常扫码</span><strong>{{ overview.abnormalScanCount }}</strong></el-card>
      <el-card shadow="never" class="kpi-card"><span>待处理反馈</span><strong>{{ overview.pendingFeedbackCount }}</strong></el-card>
      <el-card shadow="never" class="kpi-card"><span>高优反馈批次</span><strong>{{ overview.highPriorityFeedbackBatchCount }}</strong></el-card>
    </div>

    <div class="overview-grid" v-if="overview">
      <el-card shadow="never">
        <template #header>风险来源拆分</template>
        <div class="source-list">
          <div v-for="item in overview.riskSources" :key="item.source" class="source-item"><span>{{ item.source }}</span><strong>{{ item.count }}</strong></div>
        </div>
      </el-card>
      <el-card shadow="never">
        <template #header>异常扫码 TOP</template>
        <div class="mini-list">
          <div v-for="item in overview.topAbnormalScanBatches" :key="item.batchId" class="mini-row"><strong>{{ item.batchCode }}</strong><span>{{ item.summary }}</span></div>
        </div>
      </el-card>
      <el-card shadow="never">
        <template #header>低完整度批次</template>
        <div class="mini-list">
          <div v-for="item in overview.lowCompletenessBatches" :key="item.batchId" class="mini-row"><strong>{{ item.batchCode }}</strong><span>{{ item.completenessScore }}%</span></div>
        </div>
      </el-card>
    </div>

    <el-card shadow="never" v-if="overview">
      <template #header>高风险批次排行</template>
      <el-table :data="overview.topRiskBatches" stripe>
        <el-table-column prop="batchCode" label="批次编号" min-width="180" />
        <el-table-column prop="productName" label="产品名称" min-width="160" />
        <el-table-column prop="baseName" label="基地" min-width="140" />
        <el-table-column prop="completenessScore" label="完整度" width="110" />
        <el-table-column prop="riskScore" label="风险分" width="100" />
        <el-table-column label="风险等级" width="110"><template #default="{ row }"><el-tag :type="riskType(row.riskLevel)">{{ riskText(row.riskLevel) }}</el-tag></template></el-table-column>
        <el-table-column prop="summary" label="系统摘要" min-width="320" />
        <el-table-column label="操作" width="120"><template #default="{ row }"><el-button link type="primary" @click="router.push(`/batches/${row.batchId}/archive`)">查看档案</el-button></template></el-table-column>
      </el-table>
    </el-card>
  </section>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import { getRiskOverview } from '../../api/modules/risk'
const router = useRouter(); const loading = ref(false); const overview = ref<any>()
function riskText(level?: string) { if (level === 'HIGH') return '高'; if (level === 'MEDIUM') return '中'; return '低' }
function riskType(level?: string) { if (level === 'HIGH') return 'danger'; if (level === 'MEDIUM') return 'warning'; return 'success' }
async function loadData() { loading.value = true; try { overview.value = await getRiskOverview() } finally { loading.value = false } }
onMounted(loadData)
</script>
<style scoped>.kpi-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:16px;margin-bottom:16px}.kpi-card{display:grid;gap:8px}.kpi-card span{color:#64748b}.kpi-card strong{font-size:30px;color:#24374e}.overview-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:16px;margin-bottom:16px}.source-list,.mini-list{display:grid;gap:10px}.source-item,.mini-row{display:flex;justify-content:space-between;align-items:center;padding:12px 14px;border-radius:12px;background:#f8fafc}.mini-row strong{color:#24374e}@media (max-width:960px){.kpi-grid,.overview-grid{grid-template-columns:1fr}}</style>
