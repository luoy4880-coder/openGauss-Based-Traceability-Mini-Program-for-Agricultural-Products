export function formatDateTime(value?: string | null) {
  if (!value) {
    return '暂无'
  }

  return value.replace('T', ' ').slice(0, 19)
}

export function formatDate(value?: string | null) {
  if (!value) {
    return '暂无'
  }

  return value.slice(0, 10)
}

export function joinAddress(parts: Array<string | undefined | null>) {
  const value = parts.filter(Boolean).join('')
  return value || '暂无'
}

export function resultStatusText(status?: number | null) {
  if (status === 1) {
    return '合格'
  }

  if (status === 0) {
    return '不合格'
  }

  return '待确认'
}

export function recallLevelText(level?: number | null) {
  if (level === 1) {
    return '一级召回'
  }

  if (level === 2) {
    return '二级召回'
  }

  if (level === 3) {
    return '三级召回'
  }

  return '召回信息'
}
