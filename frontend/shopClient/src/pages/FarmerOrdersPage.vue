<template>
  <q-page class="farmer-orders-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">Входящие заказы</h1>
        <p class="page-subtitle" v-if="orders.length > 0">{{ orders.length }} заказов</p>
      </div>
    </div>

    <div class="orders-container">
      <div v-if="loading" class="loading-state">
        <q-spinner color="green" size="3em" />
      </div>

      <div v-else-if="orders.length === 0" class="empty-state">
        <div class="empty-icon">
          <q-icon name="inbox" />
        </div>
        <h2 class="empty-title">Новых заказов пока нет</h2>
        <p class="empty-text">Ожидайте заказов от покупателей</p>
      </div>

      <div v-else class="orders-grid">
        <div class="order-card" v-for="order in orders" :key="order.id">
          <div class="order-header">
            <div class="order-info">
              <h3 class="order-number">Заказ #{{ order.id }}</h3>
              <span class="order-date">{{ formatDate(order.createdAt) }}</span>
            </div>
            <q-select 
              v-model="order.status" 
              :options="statusOptions" 
              emit-value 
              map-options 
              dense 
              outlined
              class="status-select"
              @update:model-value="updateStatus(order.id, $event)"
            />
          </div>

          <div class="order-body">
            <div class="address-row">
              <q-icon name="location_on" />
              <span>{{ order.address }}</span>
            </div>
            
            <div class="items-section">
              <h4 class="items-title">Состав заказа:</h4>
              <div class="item" v-for="item in order.items" :key="item.productId">
                <span class="item-name">{{ item.productName }}</span>
                <span class="item-qty">{{ item.quantity }} шт.</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';
import { useFormatDate } from 'src/composables/useFormatDate';

const { formatDate } = useFormatDate();

const $q = useQuasar();
const orders = ref([]);
const loading = ref(true);

const statusOptions = [
  { label: 'Ожидает', value: 'PENDING' },
  { label: 'Принят', value: 'ACCEPTED' },
  { label: 'Готов', value: 'READY_FOR_PICKUP' },
  { label: 'Выполнен', value: 'DELIVERED' },
  { label: 'Отменен', value: 'CANCELLED' }
];

const loadOrders = async () => {
  try {
    const response = await api.get('/orders/farmer/incoming');
    orders.value = response.data;
  } catch (error) {
    console.error('Ошибка загрузки', error);
  } finally {
    loading.value = false;
  }
};

const updateStatus = async (orderId, newStatus) => {
  try {
    await api.put(`/orders/${orderId}/status?status=${newStatus}`);
    $q.notify({ type: 'positive', message: 'Статус заказа обновлен' });
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Ошибка обновления статуса' });
    loadOrders();
    console.error(error);
  }
};

onMounted(() => loadOrders());
</script>

<style scoped lang="scss">
.farmer-orders-page {
  background: var(--bg-page);
  min-height: 100vh;
}

.page-header {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 40px 24px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: white;
  margin: 0;
}

.page-subtitle {
  color: rgba(255, 255, 255, 0.8);
  margin: 8px 0 0;
}

.orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px 60px;
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.empty-state {
  background: var(--bg-card);
  border-radius: 24px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-color);
}

.empty-icon {
  width: 120px;
  height: 120px;
  background: var(--bg-muted);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  
  .q-icon {
    font-size: 60px;
    color: var(--text-muted);
  }
}

.empty-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.empty-text {
  color: var(--text-muted);
  margin: 0;
}

.orders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
}

.order-card {
  background: var(--bg-card);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: var(--shadow-hover);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding: 20px;
  background: var(--bg-muted);
  border-bottom: 1px solid var(--border-color);
}

.order-info {
  display: flex;
  flex-direction: column;
}

.order-number {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.order-date {
  color: var(--text-muted);
  font-size: 0.875rem;
  margin-top: 4px;
}

.status-select {
  min-width: 150px;
  flex-shrink: 0;
  
  :deep(.q-field__control) {
    border-radius: 10px;
    background: var(--bg-card) !important;
  }

  :deep(.q-field__native),
  :deep(.q-field__input) {
    color: var(--text-primary) !important;
  }

  :deep(.q-field__label) {
    color: var(--text-secondary) !important;
  }
}

.order-body {
  padding: 20px;
}

.address-row {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin-bottom: 16px;
  
  .q-icon {
    color: var(--text-accent);
  }
}

.items-section {
  background: var(--bg-muted);
  border-radius: 12px;
  padding: 16px;
  border: 1px solid var(--border-color);
}

.items-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 12px;
}

.item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid var(--border-color);
  
  &:last-child {
    border-bottom: none;
  }
}

.item-name {
  color: var(--text-secondary);
  flex: 1;
}

.item-qty {
  font-weight: 500;
  color: var(--text-accent);
  white-space: nowrap;
}

@media (max-width: 768px) {
  .orders-grid {
    grid-template-columns: 1fr;
  }
}
</style>