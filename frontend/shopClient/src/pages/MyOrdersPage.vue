<template>
  <q-page padding class="bg-grey-2">
    <div class="text-h4 q-mb-md">История заказов</div>

    <div v-if="loading" class="text-center q-pa-xl">
      <q-spinner color="green" size="3em" />
    </div>

    <div v-else-if="orders.length === 0" class="text-center text-grey text-h6 q-pa-xl">
      У вас пока нет заказов.
    </div>

    <div v-else class="row q-col-gutter-md">
      <div class="col-12" v-for="order in orders" :key="order.id">
        <q-card>
          <q-card-section class="bg-grey-3 row justify-between items-center">
            <div class="text-h6">Заказ #{{ order.id }} от {{ formatDate(order.createdAt) }}</div>
            <q-chip :color="statusMap[order.status].color" text-color="white" class="text-weight-bold">
              {{ statusMap[order.status].label }}
            </q-chip>
          </q-card-section>

          <q-card-section>
            <div class="text-subtitle2 q-mb-sm">Адрес доставки: {{ order.address }}</div>
            <q-list bordered separator>
              <q-item v-for="item in order.items" :key="item.productId">
                <q-item-section>
                  <q-item-label>{{ item.productName }}</q-item-label>
                  <q-item-label caption>{{ item.quantity }} шт. x {{ item.priceAtPurchase }} руб.</q-item-label>
                </q-item-section>
                <q-item-section side>
                  <div class="text-weight-bold">{{ (item.quantity * item.priceAtPurchase).toFixed(2) }} руб.</div>
                </q-item-section>
              </q-item>
            </q-list>
          </q-card-section>

          <q-card-actions align="right" class="bg-grey-1">
            <div class="text-h6">Итого: <span class="text-green-8">{{ order.totalPrice }} руб.</span></div>
          </q-card-actions>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from 'boot/axios';

const orders = ref([]);
const loading = ref(true);

// Тот самый словарь (маппинг) статусов
const statusMap = {
  PENDING: { label: 'Ожидает подтверждения', color: 'orange' },
  ACCEPTED: { label: 'Принят в работу', color: 'blue' },
  READY_FOR_PICKUP: { label: 'Готов к выдаче', color: 'purple' },
  DELIVERED: { label: 'Выполнен', color: 'green' },
  CANCELLED: { label: 'Отменен', color: 'red' }
};

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

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('ru-RU', {
    day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'
  });
};

onMounted(() => loadOrders());
</script>