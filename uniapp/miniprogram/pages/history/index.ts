import { getTraceHistory, removeTraceHistory, clearTraceHistory } from '../../services/trace'
import type { TraceHistoryItem } from '../../types/trace'
import { formatTime } from '../../utils/util'

type HistoryChartBar = {
  day: string
  shortLabel: string
  count: number
  percent: number
}

function buildHistoryChart(history: TraceHistoryItem[]): HistoryChartBar[] {
  const now = new Date()
  now.setHours(0, 0, 0, 0)

  const bars: HistoryChartBar[] = []
  for (let i = 6; i >= 0; i--) {
    const date = new Date(now)
    date.setDate(now.getDate() - i)
    const day = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(
      date.getDate(),
    ).padStart(2, '0')}`
    bars.push({
      day,
      shortLabel: `${date.getMonth() + 1}/${date.getDate()}`,
      count: 0,
      percent: 0,
    })
  }

  history.forEach((item) => {
    const date = new Date(item.queryTime)
    date.setHours(0, 0, 0, 0)
    const day = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(
      date.getDate(),
    ).padStart(2, '0')}`
    const target = bars.find((bar) => bar.day === day)
    if (target) {
      target.count += 1
    }
  })

  const max = Math.max(...bars.map((item) => item.count), 1)
  return bars.map((item) => ({
    ...item,
    percent: item.count === 0 ? 6 : Math.max(12, Math.round((item.count / max) * 100)),
  }))
}

Component({
  data: {
    history: [] as TraceHistoryItem[],
    chartBars: [] as HistoryChartBar[],
    totalCount: 0,
    weekCount: 0,
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
      const history = getTraceHistory()
      const chartBars = buildHistoryChart(history)
      const weekCount = chartBars.reduce((sum, item) => sum + item.count, 0)
      this.setData({
        history,
        chartBars,
        totalCount: history.length,
        weekCount,
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
