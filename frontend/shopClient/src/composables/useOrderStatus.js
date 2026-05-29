export function useOrderStatus() {
  const statusMap = {
    PENDING: { label: 'Ожидает', color: 'orange' },
    ACCEPTED: { label: 'В работе', color: 'blue' },
    READY_FOR_PICKUP: { label: 'Готов к выдаче', color: 'purple' },
    DELIVERED: { label: 'Передан покупателю', color: 'teal' },
    COMPLETED: { label: 'Получен', color: 'green' },
    CANCELLED: { label: 'Отменён', color: 'red' },
  };

  const farmerStatusOptions = [
    { label: 'Ожидает', value: 'PENDING' },
    { label: 'Принят', value: 'ACCEPTED' },
    { label: 'Готов к выдаче', value: 'READY_FOR_PICKUP' },
    { label: 'Передан покупателю', value: 'DELIVERED' },
    { label: 'Отменён', value: 'CANCELLED' },
  ];

  const getStatus = (status) =>
    statusMap[status] || { label: status, color: 'grey' };

  return { statusMap, farmerStatusOptions, getStatus };
}
