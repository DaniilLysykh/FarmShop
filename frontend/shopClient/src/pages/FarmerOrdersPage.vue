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
  }
};

onMounted(() => loadOrders());
</script>

<style scoped lang="scss">
.farmer-orders-page {
  background: #f5f7f5;
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
  background: white;
  border-radius: 24px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  width: 120px;
  height: 120px;
  background: #f0f0f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  
  .q-icon {
    font-size: 60px;
    color: #ccc;
  }
}

.empty-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
}

.empty-text {
  color: #888;
  margin: 0;
}

.orders-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
}

.order-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  flex-direction: column;
}

.order-number {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.order-date {
  color: #888;
  font-size: 0.875rem;
  margin-top: 4px;
}

.status-select {
  min-width: 150px;
  
  :deep(.q-field__control) {
    border-radius: 10px;
  }
}

.order-body {
  padding: 20px;
}

.address-row {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 16px;
  
  .q-icon {
    color: #2e7d32;
  }
}

.items-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
}

.items-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px;
}

.item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
  
  &:last-child {
    border-bottom: none;
  }
}

.item-name {
  color: #555;
}

.item-qty {
  font-weight: 500;
  color: #2e7d32;
}

@media (max-width: 768px) {
  .orders-grid {
    grid-template-columns: 1fr;
  }
}
</style>