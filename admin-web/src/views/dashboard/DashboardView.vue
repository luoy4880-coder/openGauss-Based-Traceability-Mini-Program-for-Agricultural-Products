<template>
  <section class="page-section">
    <div class="stats-grid">
      <article v-for="item in cards" :key="item.label" class="stats-card">
        <span class="stats-label">{{ item.label }}</span>
        <strong class="stats-value">{{ item.value }}</strong>
      </article>
    </div>

    <div class="panel-grid">
      <div class="panel-card">
        <h3>开发建议</h3>
        <p>后续优先补表单校验、枚举映射和接口类型，最后再统一处理组件复用与视觉收口。</p>
      </div>
      <div class="panel-card">
        <h3>当前状态</h3>
        <p>管理端核心业务页面已经接通，已具备联调与演示条件。</p>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
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

const cards = computed(() => [
  { label: '基地总数', value: stats.value.baseCount ?? 0 },
  { label: '批次总数', value: stats.value.batchCount ?? 0 },
  { label: '溯源码总数', value: stats.value.traceCodeCount ?? 0 },
  { label: '生产记录数', value: stats.value.productionRecordCount ?? 0 },
  { label: '质检报告数', value: stats.value.inspectionReportCount ?? 0 },
  { label: '进行中召回', value: stats.value.activeRecallCount ?? 0 },
])

onMounted(async () => {
  try {
    stats.value = await getDashboardStats()
  } catch {
    stats.value = {}
  }
})
</script>
