import { buildTraceDetailPageUrl, fetchTraceDetail, getTraceHistory, saveTraceHistory } from '../../services/trace'
import type { TraceDetail, TraceHistoryItem } from '../../types/trace'

Page({
  data: {
    traceId: '',
    signValue: '',
    loading: false,
    history: [] as TraceHistoryItem[],
  },
  onLoad() {
    this.refreshHistory()
  },
  onShow() {
    this.refreshHistory()
  },
  refreshHistory() {
    this.setData({
      history: getTraceHistory(),
    })
  },
  onTraceInput(e: WechatMiniprogram.Input) {
    this.setData({
      traceId: e.detail.value.trim(),
    })
  },
  async submitTraceQuery() {
    const traceId = this.data.traceId.trim()
    const signValue = this.data.signValue.trim()

    if (!traceId) {
      wx.showToast({
        title: '请输入追溯码',
        icon: 'none',
      })
      return
    }

    this.setData({ loading: true })

    try {
      const detail = await fetchTraceDetail(traceId, signValue)
      this.persistHistory(detail, signValue)
      wx.navigateTo({
        url: buildTraceDetailPageUrl(traceId, signValue),
      })
    } catch (error) {
      wx.showToast({
        title: error instanceof Error ? error.message : '查询失败',
        icon: 'none',
        duration: 2500,
      })
    } finally {
      this.setData({ loading: false })
    }
  },
  scanTraceCode() {
    wx.scanCode({
      onlyFromCamera: false,
      success: ({ result }) => {
        const parsed = this.extractTracePayload(result)
        this.setData({
          traceId: parsed.traceId,
          signValue: parsed.signValue,
        })
        this.submitTraceQuery()
      },
      fail: () => {
        wx.showToast({
          title: '未识别到追溯码',
          icon: 'none',
        })
      },
    })
  },
  useHistoryTrace(e: WechatMiniprogram.TouchEvent) {
    const { traceId, signValue } = e.currentTarget.dataset as { traceId: string; signValue?: string }
    this.setData({
      traceId,
      signValue: signValue || '',
    })
    this.submitTraceQuery()
  },
  extractTracePayload(raw: string) {
    const value = raw.trim()
    const [pathPart, queryPart = ''] = value.split('?')
    const segments = pathPart.split('/')
    const traceId = segments[segments.length - 1] || pathPart
    const params = queryPart.split('&').reduce<Record<string, string>>((result, pair) => {
      if (!pair) return result
      const [key, valuePart = ''] = pair.split('=')
      if (key) {
        result[decodeURIComponent(key)] = decodeURIComponent(valuePart)
      }
      return result
    }, {})
    return {
      traceId,
      signValue: params.sign || '',
    }
  },
  persistHistory(detail: TraceDetail, signValue?: string) {
    saveTraceHistory({
      traceId: detail.traceCode.traceId,
      signValue: signValue || detail.traceCode.signValue || '',
      productName: detail.batchInfo.productName || '未知产品',
      batchId: detail.batchInfo.id,
      batchCode: detail.batchInfo.batchCode || '',
      queryTime: Date.now(),
    })
    this.refreshHistory()
  },
})
