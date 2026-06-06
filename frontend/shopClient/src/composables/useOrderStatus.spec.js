import { describe, it, expect } from 'vitest'
import { useOrderStatus } from './useOrderStatus'

describe('useOrderStatus', () => {
  it('возвращает правильный label/color для COMPLETED', () => {
    const { getStatus } = useOrderStatus()

    expect(getStatus('COMPLETED')).toEqual({
      label: 'Получен',
      color: 'green'
    })
  })

  it('для неизвестного статуса отдаёт grey', () => {
    const { getStatus } = useOrderStatus()

    expect(getStatus('UNKNOWN')).toEqual({
      label: 'UNKNOWN',
      color: 'grey'
    })
  })
})

