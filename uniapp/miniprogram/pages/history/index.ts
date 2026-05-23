import { buildTraceDetailPageUrl, clearTraceHistory, getTraceHistory, removeTraceHistory } from '../../services/trace'
import type { TraceHistoryItem } from '../../types/trace'

type DisplayHistoryItem = TraceHistoryItem & {
  queryTimeText: string
}

type ChartBar = {
  day: string
  shortLabel: string
  count: number
  percent: number
}

function formatTimestamp(value?: number) {
  if (!value) return '暂无'

  const date = new Date(value)
  const pad = (input: number) => `${input}`.padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

function buildChartBars(history: TraceHistoryItem[]): ChartBar[] {
  const counts = new Map<string, number>()
  const days: ChartBar[] = []
  const today = new Date()

  for (let i = 6; i >= 0; i -= 1) {
    const current = new Date(today)
    current.setHours(0, 0, 0, 0)
    current.setDate(current.getDate() - i)
    const day = `${current.getFullYear()}-${`${current.getMonth() + 1}`.padStart(2, '0')}-${`${current.getDate()}`.padStart(2, '0')}`
    counts.set(day, 0)
    days.push({
      day,
      shortLabel: `${current.getMonth() + 1}/${current.getDate()}`,
      count: 0,
      percent: 8,
    })
  }

  history.forEach((item) => {
    const day = formatTimestamp(item.queryTime).slice(0, 10)
    if (counts.has(day)) {
      counts.set(day, (counts.get(day) || 0) + 1)
    }
  })

  const max = Math.max(...Array.from(counts.values()), 1)
  return days.map((item) => {
    const count = counts.get(item.day) || 0
    return {
      ...item,
      count,
      percent: count === 0 ? 8 : Math.max(16, Math.round((count / max) * 100)),
    }
  })
}

Page({
  data: {
    history: [] as DisplayHistoryItem[],
    totalCount: 0,
    weekCount: 0,
    chartBars: [] as ChartBar[],
  },
  onLoad() {
    this.refreshHistory()
  },
  onShow() {
    this.refreshHistory()
  },
  refreshHistory() {
    const history = getTraceHistory()
    const chartBars = buildChartBars(history)
    this.setData({
      history: history.map((item) => ({
        ...item,
        queryTimeText: formatTimestamp(item.queryTime),
      })),
      totalCount: history.length,
      weekCount: chartBars.reduce((sum, item) => sum + item.count, 0),
      chartBars,
    })
  },
  openTraceDetail(e: WechatMiniprogram.TouchEvent) {
    const { traceId, signValue } = e.currentTarget.dataset as { traceId: string; signValue?: string }
    wx.navigateTo({
      url: buildTraceDetailPageUrl(traceId, signValue),
    })
  },
  removeHistory(e: WechatMiniprogram.TouchEvent) {
    const { traceId, signValue } = e.currentTarget.dataset as { traceId: string; signValue?: string }
    removeTraceHistory(traceId, signValue)
    this.refreshHistory()
    wx.showToast({ title: '已删除', icon: 'success' })
  },
  clearHistory() {
    wx.showModal({
      title: '清空记录',
      content: '确认清空全部查询记录吗？',
      success: ({ confirm }) => {
        if (!confirm) return
        clearTraceHistory()
        this.refreshHistory()
        wx.showToast({ title: '已清空', icon: 'success' })
      },
    })
  },
  goToQuery() {
    wx.switchTab({ url: '/pages/index/index' })
  },
})
