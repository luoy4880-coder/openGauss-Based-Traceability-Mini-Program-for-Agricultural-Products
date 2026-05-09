<template>
  <section class="page-section" v-loading="loading">
    <PageHeader title="批次智能档案" description="查看批次完整档案、风险评分与系统建议动作。" />

    <template v-if="archive && insight">
      <div class="hero-grid">
        <el-card shadow="never" class="hero-card">
          <div class="hero-title">{{ archive.batchInfo.productName }}</div>
          <div class="hero-meta">
            <span>批次：{{ archive.batchInfo.batchCode }}</span>
            <span>基地：{{ archive.batchInfo.baseName }}</span>
          </div>
          <p class="hero-summary">{{ insight.aiSummary }}</p>
        </el-card>
        <el-card shadow="never" class="score-card">
          <div class="score-item"><span>完整度</span><strong>{{ insight.completenessScore }}%</strong></div>
          <div class="score-item"><span>风险评分</span><strong>{{ insight.riskScore }}</strong></div>
          <div class="score-item"><span>风险等级</span><el-tag :type="riskType(insight.riskLevel)">{{ riskText(insight.riskLevel) }}</el-tag></div>
        </el-card>
      </div>

      <div class="main-grid">
        <el-card shadow="never"><template #header>系统建议</template><div class="list-block"><div v-for="item in insight.nextActions" :key="item" class="list-item">{{ item }}</div></div></el-card>
        <el-card shadow="never"><template #header>风险提醒</template><div class="list-block"><div v-for="item in insight.warnings" :key="item" class="list-item warn">{{ item }}</div></div></el-card>
        <el-card shadow="never"><template #header>缺失项</template><div class="list-block"><div v-if="insight.missingItems.length === 0" class="list-item success">未发现关键缺失项</div><div v-for="item in insight.missingItems" :key="item" class="list-item">{{ item }}</div></div></el-card>
        <el-card shadow="never">
          <template #header>档案统计</template>
          <div class="stats-grid">
            <div class="stat-box"><span>生产记录</span><strong>{{ archive.productionRecords.length }}</strong></div>
            <div class="stat-box"><span>质检报告</span><strong>{{ archive.inspectionReports.length }}</strong></div>
            <div class="stat-box"><span>物流节点</span><strong>{{ archive.logisticsRecords.length }}</strong></div>
            <div class="stat-box"><span>单品码</span><strong>{{ archive.productItems.length }}</strong></div>
            <div class="stat-box"><span>用户反馈</span><strong>{{ archive.feedbackCount }}</strong></div>
            <div class="stat-box"><span>异常扫码</span><strong>{{ archive.abnormalScanCount }}</strong></div>
          </div>
        </el-card>
      </div>

      <div class="archive-grid">
        <el-card shadow="never"><template #header>基础信息</template><div class="kv-list"><div class="kv-row"><span>产品品类</span><strong>{{ archive.batchInfo.productCategory || '-' }}</strong></div><div class="kv-row"><span>种植日期</span><strong>{{ archive.batchInfo.plantingDate || '-' }}</strong></div><div class="kv-row"><span>预计采收</span><strong>{{ archive.batchInfo.expectedHarvestDate || '-' }}</strong></div><div class="kv-row"><span>实际采收</span><strong>{{ archive.batchInfo.actualHarvestDate || '-' }}</strong></div><div class="kv-row"><span>负责人</span><strong>{{ archive.baseInfo.managerName || '-' }}</strong></div><div class="kv-row"><span>联系电话</span><strong>{{ archive.baseInfo.contactPhone || '-' }}</strong></div></div></el-card>
        <el-card shadow="never"><template #header>生产记录时间线</template><div v-if="archive.productionRecords.length === 0" class="empty-text">暂无生产记录</div><div v-for="item in archive.productionRecords" :key="item.id" class="timeline-row"><strong>{{ item.recordType }}</strong><span>{{ item.operationTime }}</span><p>{{ item.content }}</p></div></el-card>
        <el-card shadow="never"><template #header>质检报告</template><div v-if="archive.inspectionReports.length === 0" class="empty-text">暂无质检报告</div><div v-for="item in archive.inspectionReports" :key="item.id" class="timeline-row"><strong>{{ item.reportNo }}</strong><span>{{ item.inspectionTime }}</span><p>{{ item.conclusion || '无结论说明' }}</p></div></el-card>
        <el-card shadow="never"><template #header>物流节点</template><div v-if="archive.logisticsRecords.length === 0" class="empty-text">暂无物流节点</div><div v-for="item in archive.logisticsRecords" :key="item.id" class="timeline-row"><strong>{{ item.nodeType }} / {{ item.nodeName }}</strong><span>{{ item.operationTime }}</span><p>{{ item.location || '未填写地点' }}</p></div></el-card>
      </div>
    </template>
  </section>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import { getBatchArchive, getBatchInsight } from '../../api/modules/batch'
const route = useRoute(); const loading = ref(false); const archive = ref<any>(); const insight = ref<any>()
function riskText(level?: string) { if (level === 'HIGH') return '高'; if (level === 'MEDIUM') return '中'; return '低' }
function riskType(level?: string) { if (level === 'HIGH') return 'danger'; if (level === 'MEDIUM') return 'warning'; return 'success' }
async function loadData() { loading.value = true; try { const id = Number(route.params.id); ;[archive.value, insight.value] = await Promise.all([getBatchArchive(id), getBatchInsight(id)]) } finally { loading.value = false } }
onMounted(loadData)
</script>
<style scoped>.hero-grid,.main-grid,.archive-grid{display:grid;gap:16px}.hero-grid{grid-template-columns:2fr 1fr}.main-grid,.archive-grid{margin-top:16px;grid-template-columns:repeat(2,minmax(0,1fr))}.hero-title{font-size:28px;font-weight:700;color:#24374e}.hero-meta{display:flex;gap:12px;margin-top:12px;color:#5b6777;flex-wrap:wrap}.hero-summary{margin:18px 0 0;color:#334155;line-height:1.7}.score-card{display:grid;gap:12px}.score-item{display:flex;justify-content:space-between;align-items:center;background:#f8fafc;padding:12px 14px;border-radius:12px}.score-item strong{font-size:22px;color:#24374e}.list-block,.kv-list{display:grid;gap:10px}.list-item,.kv-row,.timeline-row,.stat-box{padding:12px 14px;border-radius:12px;background:#f8fafc}.list-item.warn{background:#fff7ed;color:#9a3412}.list-item.success{background:#ecfdf5;color:#166534}.stats-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:12px}.stat-box,.kv-row{display:flex;justify-content:space-between;gap:12px}.stat-box strong{font-size:22px}.timeline-row strong,.timeline-row span,.timeline-row p{display:block}.timeline-row span{margin-top:6px;color:#64748b;font-size:13px}.timeline-row p{margin:8px 0 0;color:#334155}.empty-text{color:#64748b}@media (max-width:960px){.hero-grid,.main-grid,.archive-grid{grid-template-columns:1fr}}</style>
