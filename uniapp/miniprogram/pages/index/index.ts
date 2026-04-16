import { fetchTraceDetail, getTraceHistory, saveTraceHistory } from '../../services/trace'
import type { TraceDetail, TraceHistoryItem } from '../../types/trace'

Component({
  data: {
    traceId: '',
    loading: false,
    history: [] as TraceHistoryItem[],
  },
  lifetimes: {
    attached() {
      this.refreshHistory()
    },
  },
  pageLifetimes: {
    show() {
      this.refreshHistory()
    },
  },
  methods: {
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

      if (!traceId) {
        wx.showToast({
          title: '\u8bf7\u8f93\u5165\u8ffd\u6eaf\u7801',
          icon: 'none',
        })
        return
      }

      this.setData({ loading: true })

      try {
        const detail = await fetchTraceDetail(traceId)
        this.persistHistory(detail)
        wx.navigateTo({
          url: `/pages/trace-detail/index?traceId=${traceId}`,
        })
      } catch (error) {
        wx.showToast({
          title: error instanceof Error ? error.message : '\u67e5\u8be2\u5931\u8d25',
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
          const traceId = this.extractTraceId(result)
          this.setData({ traceId })
          this.submitTraceQuery()
        },
        fail: () => {
          wx.showToast({
            title: '\u672a\u8bc6\u522b\u5230\u8ffd\u6eaf\u7801',
            icon: 'none',
          })
        },
      })
    },
    useHistoryTrace(e: WechatMiniprogram.TouchEvent) {
      const { traceId } = e.currentTarget.dataset as { traceId: string }
      this.setData({ traceId })
      this.submitTraceQuery()
    },
    extractTraceId(raw: string) {
      const value = raw.trim()
      const segments = value.split('/')
      return segments[segments.length - 1] || value
    },
    persistHistory(detail: TraceDetail) {
      saveTraceHistory({
        traceId: detail.traceCode.traceId,
        productName: detail.batchInfo.productName || '\u672a\u77e5\u4ea7\u54c1',
        queryTime: Date.now(),
      })
      this.refreshHistory()
    },
  },
})
