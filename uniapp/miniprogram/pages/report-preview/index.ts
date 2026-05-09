const IMAGE_EXT_REG = /\.(png|jpe?g|gif|bmp|webp|svg)$/i
const PDF_EXT_REG = /\.pdf$/i

function isImageUrl(url: string) {
  return IMAGE_EXT_REG.test((url.split('?')[0] || '').toLowerCase())
}

function isPdfUrl(url: string) {
  return PDF_EXT_REG.test((url.split('?')[0] || '').toLowerCase())
}

Component({
  data: {
    reportUrl: '',
    previewUrl: '',
    reportName: '检验报告',
    isImage: false,
    isWeb: false,
    errorMessage: '',
  },
  methods: {
    onLoad(options: { url?: string; name?: string }) {
      const reportUrl = decodeURIComponent(options.url || '')
      const reportName = decodeURIComponent(options.name || '检验报告')

      if (!reportUrl) {
        this.setData({
          errorMessage: '缺少报告文件地址',
        })
        return
      }

      const image = isImageUrl(reportUrl)
      const pdf = isPdfUrl(reportUrl)

      if (!image && !pdf) {
        this.setData({
          reportUrl,
          reportName,
          isImage: false,
          isWeb: false,
          errorMessage: '当前仅支持图片和 PDF 报告预览，请上传图片或 PDF 报告。',
        })
        return
      }

      this.setData({
        reportUrl,
        previewUrl: reportUrl,
        reportName,
        isImage: image,
        isWeb: !image && pdf,
      })
    },
    closePreview() {
      wx.navigateBack({
        delta: 1,
      })
    },
    handleWebError() {
      this.setData({
        isWeb: false,
        errorMessage: '当前文件无法内嵌预览，请检查文件链接或小程序业务域名配置。',
      })
      wx.showToast({
        title: '预览失败',
        icon: 'none',
      })
    },
  },
})
