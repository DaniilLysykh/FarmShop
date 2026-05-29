<template>
  <q-page class="orders-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">История заказов</h1>
        <p class="page-subtitle" v-if="orders.length > 0">{{ orders.length }} заказов</p>
      </div>
    </div>

    <div class="orders-container">
      <div v-if="loading" class="loading-state">
        <q-spinner color="green" size="3em" />
      </div>

      <div v-else-if="orders.length === 0" class="empty-state">
        <div class="empty-icon">
          <q-icon name="receipt_long" />
        </div>
        <h2 class="empty-title">Заказов пока нет</h2>
        <p class="empty-text">Оформите первый заказ в каталоге</p>
        <q-btn unelevated to="/catalog" class="empty-btn">
          <q-icon name="store" class="q-mr-sm" />
          В каталог
        </q-btn>
      </div>

      <div v-else class="orders-list">
        <div class="order-card" v-for="order in orders" :key="order.id">
          <div class="order-header">
            <div class="order-info">
              <h3 class="order-number">Заказ #{{ order.id }}</h3>
              <span class="order-date">{{ formatDate(order.createdAt) }}</span>
            </div>
            <q-chip :color="getStatus(order.status).color" text-color="white" class="status-chip">
              {{ getStatus(order.status).label }}
            </q-chip>
          </div>

          <div class="order-details">
            <div class="detail-row">
              <q-icon name="location_on" />
              <span>{{ order.address }}</span>
            </div>
          </div>

          <q-separator />

          <div class="order-items">
            <div class="item" v-for="item in order.items" :key="item.productId">
              <div class="item-info">
                <span class="item-name">{{ item.productName }}</span>
                <span class="item-qty">{{ item.quantity }} шт. x {{ item.priceAtPurchase }} руб.</span>
              </div>
              <span class="item-total">{{ (item.quantity * item.priceAtPurchase).toFixed(2) }} руб.</span>
            </div>
          </div>

          <div class="order-footer">
            <div class="total-label">Итого:</div>
            <div class="total-value">{{ order.totalPrice }} руб.</div>
          </div>

          <div v-if="order.canCustomerConfirmReceipt" class="confirm-section">
            <p class="confirm-hint">Фермер передал заказ. Подтвердите, что вы его получили.</p>
            <q-btn
              unelevated
              color="green-8"
              icon="check_circle"
              label="Подтвердить получение"
              class="confirm-btn"
              :loading="confirmingId === order.id"
              @click="confirmReceipt(order.id)"
            />
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
import { useOrderStatus } from 'src/composables/useOrderStatus';

const { formatDate } = useFormatDate();
const { getStatus } = useOrderStatus();
const $q = useQuasar();

const orders = ref([]);
const loading = ref(true);
const confirmingId = ref(null);

const loadOrders = async () => {
  try {
    const response = await api.get('/orders/my');
    orders.value = response.data;
  } catch (error) {
    console.error('Ошибка загрузки заказов', error);
  } finally {
    loading.value = false;
  }
};

const confirmReceipt = (orderId) => {
  $q.dialog({
    title: 'Подтвердить получение',
    message: 'Вы подтверждаете, что получили заказ? После этого статус изменить будет нельзя.',
    cancel: true,
    persistent: true,
  }).onOk(async () => {
    confirmingId.value = orderId;
    try {
      await api.put(`/orders/${orderId}/confirm-receipt`);
      $q.notify({ type: 'positive', message: 'Спасибо! Получение подтверждено.' });
      await loadOrders();
    } catch (error) {
      const msg = error.response?.data?.error || 'Не удалось подтвердить получение';
      $q.notify({ type: 'negative', message: msg });
    } finally {
      confirmingId.value = null;
    }
  });
};

onMounted(() => loadOrders());
</script>

<style scoped lang="scss">
.orders-page {
  background: var(--bg-page);
  min-height: 100vh;
}

.page-header {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 40px 24px;
}

.header-content {
  max-width: 900px;
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
  max-width: 900px;
  margin: -40px auto 0;
  padding: 0 24px 60px;
  position: relative;
  z-index: 2;
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
  margin: 0 0 24px;
}

.empty-btn {
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  color: white;
  padding: 14px 32px;
  border-radius: 12px;
  font-weight: 600;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: var(--bg-card);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.order-info {
  display: flex;
  flex-direction: column;
}

.order-number {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 4px;
}

.order-date {
  color: #888;
  font-size: 0.875rem;
}

.status-chip {
  font-weight: 600;
}

.order-details {
  margin-bottom: 16px;
  
  .detail-row {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #666;
    font-size: 0.9rem;
    
    .q-icon {
      color: #888;
    }
  }
}

.order-items {
  padding: 16px 0;
  
  .item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
  }
  
  .item-info {
    display: flex;
    flex-direction: column;
  }
  
  .item-name {
    font-weight: 500;
    color: #333;
  }
  
  .item-qty {
    font-size: 0.85rem;
    color: #888;
  }
  
  .item-total {
    font-weight: 600;
    color: #2e7d32;
  }
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.total-label {
  font-size: 1rem;
  color: #666;
}

.total-value {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-accent);
}

.confirm-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.confirm-hint {
  margin: 0 0 12px;
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.confirm-btn {
  width: 100%;
  border-radius: 12px;
  font-weight: 600;
}
</style>