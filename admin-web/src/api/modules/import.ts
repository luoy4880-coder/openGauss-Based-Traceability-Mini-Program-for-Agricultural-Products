import http from '../http'

export function quickImportCropInfo(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.post('/api/crop-import/quick', formData)
}
