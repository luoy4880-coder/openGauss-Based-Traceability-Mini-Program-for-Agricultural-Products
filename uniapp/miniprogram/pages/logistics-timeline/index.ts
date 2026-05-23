import type { LogisticsRecord, TraceDetail } from '../../types/trace'
import { API_BASE_URL } from '../../config/env'
import { fetchTraceDetail } from '../../services/trace'
import { formatDateTime } from '../../utils/format'

type DisplayRecord = LogisticsRecord & {
  operationTimeText: string
  attachmentFullUrl: string
}

function toAbsoluteUrl(rawUrl: string) {
  const value = (rawUrl || '').trim()
  if (!value) return ''
  if (/^https?:\/\//i.test(value)) return value
  const base = API_BASE_URL.replace(/\/$/, '')
  return `${base}${value.startsWith('/') ? value : `/${value}`}`
}

function buildRecords(records: LogisticsRecord[]) {
  return [...(records || [])]
    .sort((a, b) => `${b.operationTime || ''}`.localeCompare(`${a.operationTime || ''}`))
    .map((item) => ({
      ...item,
      operationTimeText: formatDateTime(item.operationTime),
      attachmentFullUrl: toAbsoluteUrl(item.attachmentUrl || ''),
    }))
}

Page({
  data: {
    traceId: '',
    signValue: '',
    productName: '',
    batchCode: '',
    list: [] as DisplayRecord[],
    loading: true,
    errorMessage: '',
  },

  onLoad(options: { traceId?: string; sign?: string }) {
    const traceId = options.traceId || ''
    const signValue = options.sign || ''
    this.setData({ traceId, signValue })
    this.loadData(traceId, signValue)
  },

  async loadData(traceId: string, signValue?: string) {
    if (!traceId) {
      this.setData({ loading: false, errorMessage: '缺少溯源码参数' })
      return
    }
    this.setData({ loading: true, errorMessage: '' })
    try {
      const detail: TraceDetail = await fetchTraceDetail(traceId, signValue)
      this.setData({
        productName: detail.batchInfo.productName || '当前商品',
        batchCode: detail.batchInfo.batchCode || '',
        list: buildRecords(detail.logisticsRecords || []),
        loading: false,
      })
    } catch (error) {
      this.setData({
        list: [],
        loading: false,
        errorMessage: error instanceof Error ? error.message : '加载失败',
      })
    }
  },

  retryLoad() {
    this.loadData(this.data.traceId, this.data.signValue)
  },

  previewAttachment(e: WechatMiniprogram.TouchEvent) {
    const { url, name } = e.currentTarget.dataset as { url?: string; name?: string }
    if (!url) {
      wx.showToast({ title: '附件地址无效', icon: 'none' })
      return
    }
    wx.navigateTo({
      url: `/pages/report-preview/index?url=${encodeURIComponent(url)}&name=${encodeURIComponent(name || '物流附件')}`,
    })
  },
})
