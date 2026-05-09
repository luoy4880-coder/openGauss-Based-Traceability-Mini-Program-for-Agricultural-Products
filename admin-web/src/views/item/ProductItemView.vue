<template>
  <section class="page-section">
    <PageHeader title="一物一码" description="按批次批量生成单品溯源码，支持扫码验真与召回联动。" />
    <el-card shadow="never">
      <div class="toolbar">
        <el-select v-model="batchId" filterable placeholder="选择批次" style="width: 320px" @change="loadItems">
          <el-option v-for="item in batches" :key="item.id" :label="`${item.batchCode} - ${item.productName}`" :value="item.id" />
        </el-select>
        <el-input-number v-model="quantity" :min="1" :max="1000" />
        <el-button type="primary" :disabled="!batchId" @click="handleGenerate">批量生成</el-button>
        <el-button :disabled="!batchId" @click="loadItems">刷新</el-button>
      </div>
      <el-table :data="items" stripe v-loading="loading">
        <el-table-column prop="itemCode" label="单品编号" min-width="220" />
        <el-table-column prop="traceId" label="溯源ID" min-width="240" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.itemStatus === 2 ? 'danger' : 'success'">{{ row.itemStatus === 2 ? '风险' : '正常' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="scanCount" label="扫码次数" width="100" />
        <el-table-column prop="lastScannedAt" label="最近扫码" min-width="180" />
        <el-table-column label="二维码" width="120">
          <template #default="{ row }"><el-button link type="primary" @click="showQr(row)">查看</el-button></template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="qrVisible" title="单品二维码" width="360px">
      <div class="qr-box" v-if="currentItem">
        <qrcode-vue :value="fullQrUrl" :size="220" level="H" />
        <div class="qr-code-text">{{ currentItem.itemCode }}</div>
      </div>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import QrcodeVue from 'qrcode.vue'
import { ElMessage } from 'element-plus'
import PageHeader from '../../components/PageHeader.vue'
import { getBatchList } from '../../api/modules/batch'
import { generateProductItems, getProductItems, type ProductItem } from '../../api/modules/item'

const batches = ref<any[]>([])
const batchId = ref<number>()
const quantity = ref(10)
const items = ref<ProductItem[]>([])
const loading = ref(false)
const qrVisible = ref(false)
const currentItem = ref<ProductItem | null>(null)
const fullQrUrl = computed(() => currentItem.value ? `${location.origin}${currentItem.value.qrContent}` : '')

async function loadBatches() { batches.value = await getBatchList() as any[] }
async function loadItems() {
  if (!batchId.value) return
  loading.value = true
  try { items.value = await getProductItems(batchId.value) as ProductItem[] } finally { loading.value = false }
}
async function handleGenerate() {
  if (!batchId.value) return
  const created = await generateProductItems(batchId.value, quantity.value) as ProductItem[]
  ElMessage.success(`已生成 ${created.length} 个单品码`)
  await loadItems()
}
function showQr(row: ProductItem) { currentItem.value = row; qrVisible.value = true }
onMounted(async () => { await loadBatches() })
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; align-items: center; margin-bottom: 16px; flex-wrap: wrap; }
.qr-box { display: flex; flex-direction: column; align-items: center; gap: 12px; }
.qr-code-text { font-weight: 700; color: #334155; word-break: break-all; }
</style>
