import { describe, it, expect } from 'vitest'
import { useFormatDate } from './useFormatDate'

describe('useFormatDate', () => {
  it('для пустого значения возвращает пустую строку', () => {
    const { formatDate, formatDateShort } = useFormatDate()
    expect(formatDate(null)).toBe('')
    expect(formatDateShort(undefined)).toBe('')
  })

  it('форматирует дату в ru-RU', () => {
    const { formatDateShort } = useFormatDate()

    // 12:00Z почти всегда гарантирует, что день не “перепрыгнет” из‑за таймзоны
    const formatted = formatDateShort('2026-01-02T12:00:00.000Z')

    expect(formatted).toMatch(/^\d{2}\.\d{2}\.\d{4}$/)
  })
})

