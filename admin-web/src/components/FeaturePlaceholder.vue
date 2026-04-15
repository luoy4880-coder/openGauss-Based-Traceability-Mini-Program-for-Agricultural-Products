<template>
  <el-card shadow="never" class="placeholder-card">
    <div class="placeholder-tag">Next Step</div>
    <h3>页面骨架已准备好</h3>
    <p>下一步直接在这个页面里接真实表格、查询条件、弹窗表单和删除确认。</p>
    <div class="api-tip">建议先对接接口：{{ apiPath }}</div>
  </el-card>
</template>

<script setup lang="ts">
defineProps<{
  apiPath: string
}>()
</script>
