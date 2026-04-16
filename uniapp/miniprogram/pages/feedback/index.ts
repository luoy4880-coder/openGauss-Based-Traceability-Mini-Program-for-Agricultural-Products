import { fetchMyFeedback, saveFeedbackLocal, submitFeedbackToServer } from '../../services/trace'

const FEEDBACK_TYPES = [
  '\u529f\u80fd\u5efa\u8bae',
  '\u754c\u9762\u4f53\u9a8c',
  '\u9519\u8bef\u53cd\u9988',
  '\u5176\u4ed6\u610f\u89c1',
]

Component({
  data: {
    typeOptions: FEEDBACK_TYPES,
    typeIndex: 0,
    content: '',
    contact: '',
    submitting: false,
    loggedIn: false,
    myFeedback: [] as any[],
    loadingMyFeedback: false,
  },
  lifetimes: {
    attached() {
      this.refreshLoginStatus()
      this.loadMyFeedback()
    },
  },
  pageLifetimes: {
    show() {
      this.refreshLoginStatus()
      this.loadMyFeedback()
    },
  },
  methods: {
    goToLogin() {
      wx.switchTab({ url: '/pages/profile/index' })
    },
    refreshLoginStatus() {
      const token = wx.getStorageSync('token')
      this.setData({ loggedIn: !!token })
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
          title: '需要登录',
          content: '登录后才可以提交反馈，是否前往登录？',
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
          title: '\u8bf7\u586b\u5199\u53cd\u9988\u5185\u5bb9',
          icon: 'none',
        })
        return
      }

      if (content.length < 5) {
        wx.showToast({
          title: '\u53cd\u9988\u5185\u5bb9\u81f3\u5c11 5 \u4e2a\u5b57',
          icon: 'none',
        })
        return
      }

      this.setData({ submitting: true })

      const payload = {
        type: this.data.typeOptions[this.data.typeIndex],
        content,
        contact: this.data.contact,
      }

      try {
        await submitFeedbackToServer(payload)
        saveFeedbackLocal(payload, 'submitted')

        this.setData({
          content: '',
          contact: '',
          typeIndex: 0,
        })

        wx.showToast({
          title: '\u63d0\u4ea4\u6210\u529f',
          icon: 'success',
        })

        setTimeout(() => {
          wx.navigateBack()
        }, 500)
      } catch (error) {
        saveFeedbackLocal(payload, 'failed')
        wx.showToast({
          title: error instanceof Error ? error.message : '\u63d0\u4ea4\u5931\u8d25',
          icon: 'none',
          duration: 2500,
        })
      } finally {
        this.setData({ submitting: false })
      }
    },
  },
})
