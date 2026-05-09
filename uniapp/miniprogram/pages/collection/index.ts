import { getFavoriteTraces, removeFavoriteTrace } from '../../services/trace'
Page({
  data: { list: [] as any[] },
  onLoad() {
    this.refresh()
  },
  onShow() {
    this.refresh()
  },
  refresh() {
    this.setData({ list: getFavoriteTraces() })
  },
  openDetail(e: WechatMiniprogram.TouchEvent) {
    const { traceId } = e.currentTarget.dataset as { traceId: string }
    wx.navigateTo({ url: `/pages/trace-detail/index?traceId=${traceId}` })
  },
  removeItem(e: WechatMiniprogram.TouchEvent) {
    const { traceId } = e.currentTarget.dataset as { traceId: string }
    removeFavoriteTrace(traceId)
    this.refresh()
  },
})
