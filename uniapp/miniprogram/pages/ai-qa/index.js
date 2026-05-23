const { askUserAi } = require('../../services/trace')

function buildHistoryKey(traceId, signValue) {
  return `ai_qa_history_${traceId}_${signValue || 'default'}`
}

const TEXT = {
  contextTitle: '当前商品公开溯源信息',
  suggestionSafe: '这个产品安全吗？',
  suggestionRisk: '风险提示是什么意思？',
  suggestionQuality: '这个批次的质量情况怎么看？',
  suggestionAction: '如果我担心有问题，应该怎么处理？',
  clearSuccess: '已清空记录',
  enterQuestion: '请输入问题',
  missingTrace: '缺少商品上下文',
  aiUnavailable: 'AI 问答暂时不可用',
  noAnswerPrefix: '暂时没有拿到回答。\n',
}

Page({
  data: {
    traceId: '',
    signValue: '',
    question: '',
    canSubmit: false,
    loading: false,
    history: [],
    contextTitle: TEXT.contextTitle,
    suggestions: [
      TEXT.suggestionSafe,
      TEXT.suggestionRisk,
      TEXT.suggestionQuality,
      TEXT.suggestionAction,
    ],
  },

  onLoad(options) {
    const traceId = options.traceId || ''
    const signValue = options.sign || ''
    this.setData({
      traceId,
      signValue,
      history: wx.getStorageSync(buildHistoryKey(traceId, signValue)) || [],
      canSubmit: false,
    })
  },

  onQuestionInput(e) {
    const question = e.detail.value
    this.setData({
      question,
      canSubmit: !!question.trim(),
    })
  },

  useSuggestion(e) {
    const { question } = e.currentTarget.dataset || {}
    const nextQuestion = question || ''
    this.setData({
      question: nextQuestion,
      canSubmit: !!nextQuestion.trim(),
    })
  },

  clearHistory() {
    const key = buildHistoryKey(this.data.traceId, this.data.signValue)
    wx.removeStorageSync(key)
    this.setData({ history: [] })
    wx.showToast({ title: TEXT.clearSuccess, icon: 'success' })
  },

  persistHistory(history) {
    const key = buildHistoryKey(this.data.traceId, this.data.signValue)
    wx.setStorageSync(key, history.slice(-20))
  },

  async submitQuestion() {
    const question = this.data.question.trim()
    if (!question) {
      wx.showToast({ title: TEXT.enterQuestion, icon: 'none' })
      return
    }
    if (!this.data.traceId) {
      wx.showToast({ title: TEXT.missingTrace, icon: 'none' })
      return
    }

    const nextHistory = [
      ...this.data.history,
      {
        id: Date.now(),
        role: 'user',
        text: question,
      },
    ]

    this.setData({
      loading: true,
      question: '',
      canSubmit: false,
      history: nextHistory,
    })
    this.persistHistory(nextHistory)

    try {
      const answer = await askUserAi(question, this.data.traceId, this.data.signValue)
      const mergedHistory = [
        ...nextHistory,
        {
          id: Date.now() + 1,
          role: 'assistant',
          text: answer.answer,
          references: (answer.references || []).map((item) => item.label),
        },
      ]
      this.setData({
        history: mergedHistory,
        contextTitle: answer.contextTitle || this.data.contextTitle,
      })
      this.persistHistory(mergedHistory)
    } catch (error) {
      const message = error instanceof Error ? error.message : TEXT.aiUnavailable
      const failedHistory = [
        ...nextHistory,
        {
          id: Date.now() + 1,
          role: 'assistant',
          text: `${TEXT.noAnswerPrefix}${message}`,
        },
      ]
      this.setData({
        history: failedHistory,
      })
      this.persistHistory(failedHistory)
      wx.showToast({
        title: message,
        icon: 'none',
        duration: 2500,
      })
    } finally {
      this.setData({ loading: false })
    }
  },
})
