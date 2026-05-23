import type { TraceSummary } from '../../types/trace'
import { fetchTraceSummary } from '../../services/trace'

Page({
  data: {
    traceId: '',
    signValue: '',
    summary: null as TraceSummary | null,
    loading: true,
    errorMessage: '',
  },

  onLoad(options: { traceId?: string; sign?: string }) {
    const traceId = options.traceId || ''
    const signValue = options.sign || ''
    this.setData({ traceId, signValue })
    this.loadSummary(traceId, signValue)
  },

  async loadSummary(traceId: string, signValue?: string) {
    if (!traceId) {
      this.setData({ loading: false, errorMessage: '缺少溯源码参数' })
      return
    }
    this.setData({ loading: true, errorMessage: '' })
    try {
      this.setData({
        summary: await fetchTraceSummary(traceId, signValue),
        loading: false,
      })
    } catch (error) {
      this.setData({
        loading: false,
        errorMessage: error instanceof Error ? error.message : '加载失败',
      })
    }
  },

  retryLoad() {
    this.loadSummary(this.data.traceId, this.data.signValue)
  },
})
