import { askUserAi } from '../../services/trace'

type ChatItem = {
  id: number
  role: 'user' | 'assistant'
  text: string
  references?: string[]
}

function buildHistoryKey(traceId: string, signValue: string) {
  return `ai_qa_history_${traceId}_${signValue || 'default'}`
}

const TEXT = {
  contextTitle: '\u5f53\u524d\u5546\u54c1\u516c\u5f00\u6eaf\u6e90\u4fe1\u606f',
  suggestionSafe: '\u8fd9\u4e2a\u4ea7\u54c1\u5b89\u5168\u5417\uff1f',
  suggestionRisk: '\u98ce\u9669\u63d0\u793a\u662f\u4ec0\u4e48\u610f\u601d\uff1f',
  suggestionQuality: '\u8fd9\u4e2a\u6279\u6b21\u7684\u8d28\u91cf\u60c5\u51b5\u600e\u4e48\u770b\uff1f',
  suggestionAction: '\u5982\u679c\u6211\u62c5\u5fc3\u6709\u95ee\u9898\uff0c\u5e94\u8be5\u600e\u4e48\u5904\u7406\uff1f',
  clearSuccess: '\u5df2\u6e05\u7a7a\u8bb0\u5f55',
  enterQuestion: '\u8bf7\u8f93\u5165\u95ee\u9898',
  missingTrace: '\u7f3a\u5c11\u5546\u54c1\u4e0a\u4e0b\u6587',
  aiUnavailable: 'AI \u95ee\u7b54\u6682\u65f6\u4e0d\u53ef\u7528',
  noAnswerPrefix: '\u6682\u65f6\u6ca1\u6709\u62ff\u5230\u56de\u7b54\u3002\n',
} as const

Page({
  data: {
    traceId: '',
    signValue: '',
    question: '',
    canSubmit: false,
    loading: false,
    history: [] as ChatItem[],
    contextTitle: TEXT.contextTitle,
    suggestions: [
      TEXT.suggestionSafe,
      TEXT.suggestionRisk,
      TEXT.suggestionQuality,
      TEXT.suggestionAction,
    ],
  },

  onLoad(options: { traceId?: string; sign?: string }) {
    const traceId = options.traceId || ''
    const signValue = options.sign || ''
    this.setData({
      traceId,
      signValue,
      history: (wx.getStorageSync(buildHistoryKey(traceId, signValue)) as ChatItem[]) || [],
      canSubmit: false,
    })
  },

  onQuestionInput(e: WechatMiniprogram.Input) {
    const question = e.detail.value
    this.setData({
      question,
      canSubmit: !!question.trim(),
    })
  },

  useSuggestion(e: WechatMiniprogram.TouchEvent) {
    const { question } = e.currentTarget.dataset as { question?: string }
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

  persistHistory(history: ChatItem[]) {
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

    const nextHistory: ChatItem[] = [
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
      const mergedHistory: ChatItem[] = [
        ...nextHistory,
        {
          id: Date.now() + 1,
          role: 'assistant',
          text: answer.answer,
          references: answer.references?.map((item) => item.label) || [],
        },
      ]
      this.setData({
        history: mergedHistory,
        contextTitle: answer.contextTitle || this.data.contextTitle,
      })
      this.persistHistory(mergedHistory)
    } catch (error) {
      const message = error instanceof Error ? error.message : TEXT.aiUnavailable
      const failedHistory: ChatItem[] = [
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
