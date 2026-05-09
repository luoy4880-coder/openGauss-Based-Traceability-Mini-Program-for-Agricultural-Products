<template>
  <section class="dash-page">
    <div class="kpi-grid">
      <article v-for="item in cards" :key="item.label" class="kpi-card">
        <div class="kpi-head">
          <span class="kpi-icon" :style="{ background: item.iconBg, color: item.color }">
            <el-icon><component :is="item.icon" /></el-icon>
          </span>
          <span class="kpi-label">{{ item.label }}</span>
        </div>
        <div class="kpi-value">{{ item.value }}</div>
      </article>
    </div>

    <div class="main-grid">
      <article class="panel panel-main">
        <div class="panel-head">
          <h3>业务规模对比</h3>
          <p>按当前数据量自动归一化</p>
        </div>
        <div class="stack-bars">
          <div v-for="item in chartBars" :key="item.label" class="stack-row">
            <div class="stack-top">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
            </div>
            <div class="stack-track">
              <div class="stack-fill" :style="{ width: `${item.percent}%`, background: item.gradient }"></div>
            </div>
          </div>
        </div>
      </article>

      <article class="panel panel-side">
        <div class="panel-head">
          <h3>质量与风险</h3>
          <p>召回占比越低越健康</p>
        </div>
        <div class="ring-shell">
          <div class="ring" :style="{ background: donutGradient }">
            <div class="ring-inner">
              <span>通过率</span>
              <strong>{{ passRateText }}</strong>
            </div>
          </div>
        </div>
        <div class="risk-list">
          <div class="risk-item">
            <span>质检报告</span>
            <strong>{{ safeNumber(stats.inspectionReportCount) }}</strong>
          </div>
          <div class="risk-item">
            <span>进行中召回</span>
            <strong class="warn">{{ safeNumber(stats.activeRecallCount) }}</strong>
          </div>
          <div class="risk-item">
            <span>风险占比</span>
            <strong>{{ riskRatioText }}</strong>
          </div>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Box, Document, Files, OfficeBuilding, RefreshLeft, Tickets } from '@element-plus/icons-vue'
import { getDashboardStats } from '../../api/modules/dashboard'

type DashboardStats = {
  baseCount: number
  batchCount: number
  traceCodeCount: number
  productionRecordCount: number
  inspectionReportCount: number
  activeRecallCount: number
}

const stats = ref<Partial<DashboardStats>>({})

const safeNumber = (value?: number) => value ?? 0

const cards = computed(() => [
  {
    label: '基地总数',
    value: safeNumber(stats.value.baseCount),
    color: '#6d8f4f',
    iconBg: '#eef6e8',
    icon: OfficeBuilding,
  },
  {
    label: '批次总数',
    value: safeNumber(stats.value.batchCount),
    color: '#9c7a45',
    iconBg: '#f8f1e7',
    icon: Box,
  },
  {
    label: '溯源码总数',
    value: safeNumber(stats.value.traceCodeCount),
    color: '#4f7f87',
    iconBg: '#e9f5f7',
    icon: Tickets,
  },
  {
    label: '生产记录数',
    value: safeNumber(stats.value.productionRecordCount),
    color: '#7a6bb0',
    iconBg: '#f0ecfc',
    icon: Document,
  },
  {
    label: '质检报告数',
    value: safeNumber(stats.value.inspectionReportCount),
    color: '#5b8f62',
    iconBg: '#ebf6ed',
    icon: Files,
  },
  {
    label: '进行中召回',
    value: safeNumber(stats.value.activeRecallCount),
    color: '#bb6a4e',
    iconBg: '#faeee8',
    icon: RefreshLeft,
  },
])

const chartBars = computed(() => {
  const items = [
    { label: '基地', value: safeNumber(stats.value.baseCount), gradient: 'linear-gradient(90deg, #7ea45d 0%, #9fbf79 100%)' },
    { label: '批次', value: safeNumber(stats.value.batchCount), gradient: 'linear-gradient(90deg, #ae8b59 0%, #d2ad72 100%)' },
    { label: '溯源码', value: safeNumber(stats.value.traceCodeCount), gradient: 'linear-gradient(90deg, #5d9198 0%, #86bcc4 100%)' },
    { label: '生产记录', value: safeNumber(stats.value.productionRecordCount), gradient: 'linear-gradient(90deg, #8a7ac2 0%, #b2a7de 100%)' },
    { label: '质检报告', value: safeNumber(stats.value.inspectionReportCount), gradient: 'linear-gradient(90deg, #628f67 0%, #8db493 100%)' },
  ]
  const max = Math.max(...items.map((item) => item.value), 1)
  return items.map((item) => ({
    ...item,
    percent: Math.max(6, Math.round((item.value / max) * 100)),
  }))
})

const riskRatio = computed(() => {
  const inspection = safeNumber(stats.value.inspectionReportCount)
  const recall = safeNumber(stats.value.activeRecallCount)
  const total = inspection + recall
  if (total === 0) {
    return 0
  }
  return Math.round((recall / total) * 100)
})

const riskRatioText = computed(() => `${riskRatio.value}%`)
const passRate = computed(() => 100 - riskRatio.value)
const passRateText = computed(() => `${passRate.value}%`)
const donutGradient = computed(() => `conic-gradient(#5b8f62 ${passRate.value}%, #d18260 ${passRate.value}% 100%)`)

onMounted(async () => {
  try {
    stats.value = await getDashboardStats()
  } catch {
    stats.value = {}
  }
})
</script>

<style scoped>
.dash-page {
  display: grid;
  gap: 18px;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.kpi-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 18px;
  padding: 16px 18px;
  border: 1px solid #f0e8dc;
  box-shadow: 0 10px 26px rgba(33, 53, 70, 0.08);
}

.kpi-head {
  display: flex;
  align-items: center;
  gap: 8px;
}

.kpi-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.kpi-label {
  color: #6b7280;
  font-size: 13px;
}

.kpi-value {
  margin-top: 10px;
  font-size: 34px;
  font-weight: 700;
  color: #24374e;
  line-height: 1;
}

.main-grid {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 14px;
}

.panel {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 22px;
  padding: 18px;
  border: 1px solid #f0e8dc;
  box-shadow: 0 14px 30px rgba(33, 53, 70, 0.08);
}

.panel-head h3 {
  margin: 0;
  font-size: 20px;
  color: #21384d;
}

.panel-head p {
  margin: 6px 0 0;
  color: #718096;
  font-size: 13px;
}

.stack-bars {
  margin-top: 16px;
  display: grid;
  gap: 12px;
}

.stack-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #4b5563;
  margin-bottom: 6px;
}

.stack-top strong {
  color: #23384e;
}

.stack-track {
  height: 12px;
  border-radius: 999px;
  background: #edf2f7;
  overflow: hidden;
}

.stack-fill {
  height: 100%;
  border-radius: inherit;
}

.ring-shell {
  margin-top: 14px;
  display: grid;
  place-items: center;
}

.ring {
  width: 170px;
  height: 170px;
  border-radius: 50%;
  display: grid;
  place-items: center;
}

.ring-inner {
  width: 122px;
  height: 122px;
  border-radius: 50%;
  background: #fff;
  display: grid;
  place-items: center;
  color: #46566a;
  box-shadow: inset 0 0 0 1px #eef2f7;
}

.ring-inner strong {
  font-size: 24px;
  color: #1f3348;
}

.risk-list {
  margin-top: 14px;
  display: grid;
  gap: 8px;
}

.risk-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 12px;
  background: #f7fafc;
  font-size: 13px;
  color: #556272;
}

.risk-item strong {
  color: #24374e;
}

.risk-item .warn {
  color: #b4533c;
}

@media (max-width: 960px) {
  .kpi-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .main-grid {
    grid-template-columns: 1fr;
  }
}
</style>
