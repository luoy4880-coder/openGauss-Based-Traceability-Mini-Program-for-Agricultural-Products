import { getTraceHistory, removeTraceHistory, clearTraceHistory } from '../../services/trace'
import type { TraceHistoryItem } from '../../types/trace'
import { formatTime } from '../../utils/util'

Component({
  data: {
    history: [] as TraceHistoryItem[],
  },
  lifetimes: {
    attached() {
      this.refresh()
    },
  },
  pageLifetimes: {
    show() {
      this.refresh()
    },
  },
  methods: {
    refresh() {
      this.setData({
        history: getTraceHistory(),
      })
    },
    formatTimestamp(value?: number) {
      if (!value) {
        return '暂无'
      }
      return formatTime(new Date(value))
    },
    openTraceDetail(e: WechatMiniprogram.TouchEvent) {
      const { traceId } = e.currentTarget.dataset as { traceId: string }
      wx.navigateTo({
        url: `/pages/trace-detail/index?traceId=${traceId}`,
      })
    },
    removeHistory(e: WechatMiniprogram.TouchEvent) {
      const { traceId } = e.currentTarget.dataset as { traceId: string }
      removeTraceHistory(traceId)
      this.refresh()
      wx.showToast({ title: '已删除', icon: 'none' })
    },
    clearHistory() {
      wx.showModal({
        title: '清空记录',
        content: '确认删除本地查询历史吗？',
        success: ({ confirm }) => {
          if (!confirm) {
            return
          }
          clearTraceHistory()
          this.refresh()
          wx.showToast({ title: '已清空', icon: 'success' })
        },
      })
    },
    goToQuery() {
      wx.switchTab({
        url: '/pages/index/index',
      })
    },
  },
})

