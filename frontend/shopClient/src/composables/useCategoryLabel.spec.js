import { describe, it, expect } from 'vitest'
import { useCategoryLabel } from './useCategoryLabel'

describe('useCategoryLabel', () => {
  it('маппит известную категорию', () => {
    const { getCategoryLabel } = useCategoryLabel()
    expect(getCategoryLabel('MILK')).toBe('Молочные продукты')
  })

  it('возвращает исходное значение для неизвестной категории', () => {
    const { getCategoryLabel } = useCategoryLabel()
    expect(getCategoryLabel('SOMETHING_ELSE')).toBe('SOMETHING_ELSE')
  })
})

