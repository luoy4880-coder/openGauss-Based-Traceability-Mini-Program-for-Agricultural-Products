import type { TraceDetail } from '../../types/trace'
import {
  formatDate,
  formatDateTime,
  joinAddress,
  recallLevelText,
  resultStatusText,
} from '../../utils/format'
import { fetchTraceDetail } from '../../services/trace'

Component({
  data: {
    loading: true,
    traceId: '',
    detail: null as TraceDetail | null,
    errorMessage: '',
  },
  lifetimes: {
    attached() {
      // attached 阶段可能拿不到 options，逻辑移至下面的 methods.onLoad
    },
  },
  methods: {
    // 微信小程序标准页面生命周期
    onLoad(options: { traceId?: string }) {
      const traceId = options.traceId || ''
      this.setData({ traceId })
      this.loadTraceDetail(traceId)
    },
    async loadTraceDetail(traceId: string) {
      if (!traceId) {
        this.setData({
          loading: false,
          errorMessage: '\u7f3a\u5c11\u8ffd\u6eaf\u7801\u53c2\u6570',
        })
        return
      }

      this.setData({
        loading: true,
        errorMessage: '',
      })

      try {
        const detail = await fetchTraceDetail(traceId)
        this.setData({
          detail,
          loading: false,
        })
      } catch (error) {
        this.setData({
          loading: false,
          errorMessage: error instanceof Error ? error.message : '\u52a0\u8f7d\u5931\u8d25',
        })
      }
    },
    retryLoad() {
      this.loadTraceDetail(this.data.traceId)
    },
    previewImage(e: WechatMiniprogram.TouchEvent) {
      const { url } = e.currentTarget.dataset as { url?: string }
      if (!url) {
        return
      }

      wx.previewImage({
        urls: [url],
        current: url,
      })
    },
    formatDate,
    formatDateTime,
    joinAddress,
    recallLevelText,
    resultStatusText,
  },
})
