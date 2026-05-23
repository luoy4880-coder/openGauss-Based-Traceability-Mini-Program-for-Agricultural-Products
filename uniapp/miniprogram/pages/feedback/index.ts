import { fetchMyFeedback, getTraceHistory, saveFeedbackLocal, submitFeedbackToServer } from '../../services/trace'
import type { TraceHistoryItem } from '../../types/trace'

const FEEDBACK_TYPES = ['产品质量', '物流配送', '使用体验', '功能建议', '其他问题']

Page({
  data: {
    typeOptions: FEEDBACK_TYPES,
    typeIndex: 0,
    content: '',
    contact: '',
    submitting: false,
    loggedIn: false,
    myFeedback: [] as any[],
    loadingMyFeedback: false,
    traceOptions: [] as TraceHistoryItem[],
    traceIndex: -1,
  },
  onLoad() {
    this.refreshLoginStatus()
    this.loadTraceOptions()
    this.loadMyFeedback()
  },
  onShow() {
    this.refreshLoginStatus()
    this.loadTraceOptions()
    this.loadMyFeedback()
  },
  goToLogin() {
    wx.switchTab({ url: '/pages/profile/index' })
  },
  refreshLoginStatus() {
    const token = wx.getStorageSync('token')
    this.setData({ loggedIn: !!token })
  },
  loadTraceOptions() {
    this.setData({
      traceOptions: getTraceHistory(),
      traceIndex: -1,
    })
  },
  async loadMyFeedback() {
    if (!wx.getStorageSync('token')) {
      this.setData({ myFeedback: [] })
      return
    }

    this.setData({ loadingMyFeedback: true })
    try {
      const list = await fetchMyFeedback(20)
      this.setData({ myFeedback: list || [] })
    } catch {
      this.setData({ myFeedback: [] })
    } finally {
      this.setData({ loadingMyFeedback: false })
    }
  },
  onTypeChange(e: WechatMiniprogram.PickerChange) {
    this.setData({
      typeIndex: Number(e.detail.value),
    })
  },
  onTraceChange(e: WechatMiniprogram.PickerChange) {
    this.setData({
      traceIndex: Number(e.detail.value),
    })
  },
  onContentInput(e: WechatMiniprogram.Input) {
    this.setData({
      content: e.detail.value,
    })
  },
  onContactInput(e: WechatMiniprogram.Input) {
    this.setData({
      contact: e.detail.value.trim(),
    })
  },
  async submitFeedback() {
    if (!wx.getStorageSync('token')) {
      wx.showModal({
        title: '需要先登录',
        content: '登录后才能提交反馈，是否前往登录？',
        success: ({ confirm }) => {
          if (!confirm) {
            return
          }
          wx.switchTab({ url: '/pages/profile/index' })
        },
      })
      return
    }

    const content = this.data.content.trim()
    if (!content) {
      wx.showToast({
        title: '请先填写反馈内容',
        icon: 'none',
      })
      return
    }

    if (content.length < 5) {
      wx.showToast({
        title: '反馈内容至少 5 个字',
        icon: 'none',
      })
      return
    }

    this.setData({ submitting: true })

    const payload = {
      type: this.data.typeOptions[this.data.typeIndex],
      content,
      contact: this.data.contact,
      traceId: this.data.traceIndex >= 0 ? this.data.traceOptions[this.data.traceIndex]?.traceId : undefined,
      batchId: this.data.traceIndex >= 0 ? this.data.traceOptions[this.data.traceIndex]?.batchId : undefined,
    }

    try {
      await submitFeedbackToServer(payload)
      saveFeedbackLocal(payload, 'submitted')

      this.setData({
        content: '',
        contact: '',
        typeIndex: 0,
        traceIndex: -1,
      })

      wx.showToast({
        title: '提交成功',
        icon: 'success',
      })

      setTimeout(() => {
        wx.navigateBack()
      }, 500)
    } catch (error) {
      saveFeedbackLocal(payload, 'failed')
      wx.showToast({
        title: error instanceof Error ? error.message : '提交失败',
        icon: 'none',
        duration: 2500,
      })
    } finally {
      this.setData({ submitting: false })
    }
  },
})
