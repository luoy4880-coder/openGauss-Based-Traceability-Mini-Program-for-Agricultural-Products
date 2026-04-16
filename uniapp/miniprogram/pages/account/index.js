const { API_BASE_URL } = require('../../config/env')

Page({
  data: {
    mode: 'login',
    username: '',
    password: '',
    realName: '',
    phone: '',
    submitting: false,
    loggedIn: false,
  },

  onLoad(options) {
    const mode = options && options.mode ? options.mode : 'login'
    this.setData({ mode })
    this.refreshLoginStatus()
  },

  onShow() {
    this.refreshLoginStatus()
  },

  refreshLoginStatus() {
    const token = wx.getStorageSync('token')
    this.setData({ loggedIn: !!token })
  },

  switchMode(e) {
    const { mode } = e.currentTarget.dataset
    this.setData({ mode })
  },

  onUsernameInput(e) {
    this.setData({ username: (e.detail.value || '').trim() })
  },

  onPasswordInput(e) {
    this.setData({ password: (e.detail.value || '').trim() })
  },

  onRealNameInput(e) {
    this.setData({ realName: (e.detail.value || '').trim() })
  },

  onPhoneInput(e) {
    this.setData({ phone: (e.detail.value || '').trim() })
  },

  goToProfile() {
    wx.switchTab({ url: '/pages/profile/index' })
  },

  submit() {
    const { mode, username, password, realName, phone } = this.data

    if (!username) {
      wx.showToast({ title: '请输入用户名', icon: 'none' })
      return
    }
    if (!password) {
      wx.showToast({ title: '请输入密码', icon: 'none' })
      return
    }

    if (mode === 'bind' && !wx.getStorageSync('token')) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }

    this.setData({ submitting: true })

    const api = mode === 'login' ? '/api/auth/login' : mode === 'register' ? '/api/auth/register' : '/api/auth/bind'
    const method = 'POST'
    const data = mode === 'login' ? { username, password } : { username, password, realName, phone }
    const header = {}

    if (mode === 'bind') {
      const token = wx.getStorageSync('token')
      header.Authorization = `Bearer ${token}`
    }

    wx.request({
      url: `${API_BASE_URL}${api}`,
      method,
      data,
      header,
      timeout: 10000,
      success: (res) => {
        const payload = res && res.data
        if (!payload || payload.code !== 200) {
          wx.showToast({ title: (payload && payload.message) || '请求失败', icon: 'none', duration: 2500 })
          return
        }

        if (mode === 'bind') {
          wx.setStorageSync('userInfo', payload.data)
          wx.showToast({ title: '绑定成功', icon: 'success' })
          setTimeout(() => wx.navigateBack(), 300)
          return
        }

        const loginVo = payload.data || {}
        wx.setStorageSync('token', loginVo.token)
        wx.setStorageSync('userInfo', loginVo)
        wx.setStorageSync('login_type', 'password')

        wx.request({
          url: `${API_BASE_URL}/api/auth/me`,
          method: 'GET',
          header: { Authorization: `Bearer ${loginVo.token}` },
          success: (meRes) => {
            const mePayload = meRes && meRes.data
            if (mePayload && mePayload.code === 200) {
              wx.setStorageSync('userInfo', mePayload.data)
            }
          },
          complete: () => {
            wx.showToast({ title: '登录成功', icon: 'success' })
            setTimeout(() => wx.switchTab({ url: '/pages/profile/index' }), 300)
          },
        })
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      },
      complete: () => {
        this.setData({ submitting: false })
      },
    })
  },
})
