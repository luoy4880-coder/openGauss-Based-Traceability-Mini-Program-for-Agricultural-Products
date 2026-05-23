import type { FavoriteTraceItem } from '../../types/trace'
import { buildTraceDetailPageUrl, getFavoriteTraces, removeFavoriteTrace } from '../../services/trace'

type DisplayFavoriteItem = FavoriteTraceItem & {
  savedAtText: string
}

function formatSavedAt(savedAt: number) {
  if (!savedAt) return '刚刚收藏'
  const date = new Date(savedAt)
  const mm = `${date.getMonth() + 1}`.padStart(2, '0')
  const dd = `${date.getDate()}`.padStart(2, '0')
  const hh = `${date.getHours()}`.padStart(2, '0')
  const mi = `${date.getMinutes()}`.padStart(2, '0')
  return `${mm}-${dd} ${hh}:${mi}`
}

Page({
  data: {
    list: [] as DisplayFavoriteItem[],
  },

  onLoad() {
    this.refresh()
  },

  onShow() {
    this.refresh()
  },

  refresh() {
    this.setData({
      list: getFavoriteTraces().map((item) => ({
        ...item,
        savedAtText: formatSavedAt(item.savedAt),
      })),
    })
  },

  openDetail(e: WechatMiniprogram.TouchEvent) {
    const { traceId, signValue } = e.currentTarget.dataset as { traceId: string; signValue?: string }
    wx.navigateTo({ url: buildTraceDetailPageUrl(traceId, signValue) })
  },

  removeItem(e: WechatMiniprogram.TouchEvent) {
    const { traceId, signValue } = e.currentTarget.dataset as { traceId: string; signValue?: string }
    removeFavoriteTrace(traceId, signValue)
    this.refresh()
    wx.showToast({ title: '已移除', icon: 'success' })
  },

  backToTrace() {
    wx.switchTab({ url: '/pages/index/index' })
  },
})
