<template>
  <q-page padding>
    <div class="text-h4 q-mb-md">Входящие заказы</div>

    <div v-if="loading" class="text-center q-pa-xl">
      <q-spinner color="green" size="3em" />
    </div>

    <div v-else-if="orders.length === 0" class="text-center text-grey text-h6 q-pa-xl">
      Новых заказов пока нет.
    </div>

    <div v-else class="row q-col-gutter-md">
      <div class="col-12 col-md-6" v-for="order in orders" :key="order.id">
        <q-card bordered>
          <q-card-section class="row justify-between items-center">
            <div class="text-h6">Заказ #{{ order.id }}</div>
            
            <q-select 
              v-model="order.status" 
              :options="statusOptions" 
              emit-value 
              map-options 
              dense 
              outlined
              @update:model-value="updateStatus(order.id, $event)"
              style="min-width: 200px"
            />
          </q-card-section>

          <q-card-section>
            <div class="text-subtitle2 text-grey-8">От: {{ formatDate(order.createdAt) }}</div>
            <div class="text-subtitle2 q-mb-sm">Куда: {{ order.address }}</div>
            
            <q-list dense>
              <q-item v-for="item in order.items" :key="item.productId">
                <q-item-section>{{ item.productName }} ({{ item.quantity }} шт.)</q-item-section>
              </q-item>
            </q-list>
          </q-card-section>
        </q-card>
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
  { label: 'Ожидает подтверждения', value: 'PENDING' },
  { label: 'Принят в работу', value: 'ACCEPTED' },
  { label: 'Готов к выдаче', value: 'READY_FOR_PICKUP' },
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
    loadOrders(); // Откатываем статус на экране, если сервер выдал ошибку
  }
};

onMounted(() => loadOrders());
</script>