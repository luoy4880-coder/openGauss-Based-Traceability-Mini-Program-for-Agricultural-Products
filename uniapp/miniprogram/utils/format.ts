export function formatDateTime(value?: string | null) {
  if (!value) {
    return 'N/A'
  }

  return value.replace('T', ' ').slice(0, 19)
}

export function formatDate(value?: string | null) {
  if (!value) {
    return 'N/A'
  }

  return value.slice(0, 10)
}

export function joinAddress(parts: Array<string | undefined | null>) {
  const value = parts.filter(Boolean).join('')
  return value || 'N/A'
}

export function resultStatusText(status?: number | null) {
  if (status === 1) {
    return 'Passed'
  }

  if (status === 0) {
    return 'Failed'
  }

  return 'Pending'
}

export function recallLevelText(level?: number | null) {
  if (level === 1) {
    return 'Level 1'
  }

  if (level === 2) {
    return 'Level 2'
  }

  if (level === 3) {
    return 'Level 3'
  }

  return 'Recall info'
}
