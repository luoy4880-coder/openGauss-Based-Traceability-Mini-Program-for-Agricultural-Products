export type ApiResponse<T> = {
  code: number
  message: string
  data: T
}

export type PageResponse<T> = {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}
