<template>
  <q-page padding class="bg-grey-2">
    <div class="text-h4 q-mb-md">Моя корзина</div>

    <div v-if="cartItems.length === 0 && !loading" class="text-center q-pa-xl">
      <q-icon name="shopping_cart" size="100px" color="grey-4" />
      <div class="text-h6 text-grey-6 q-mt-md">Ваша корзина пока пуста</div>
      <q-btn color="green-8" label="Перейти в каталог" to="/catalog" class="q-mt-md" />
    </div>

    <div v-else class="row q-col-gutter-lg">
      
      <div class="col-12 col-md-8">
        <q-card class="q-mb-sm" v-for="item in cartItems" :key="item.id">
          <q-card-section horizontal class="items-center">
            <q-img 
              :src="item.imageUrl ? `http://26.151.165.100:8080${item.imageUrl}` : 'https://via.placeholder.com/150?text=Нет+фото'" 
              style="width: 120px; height: 120px; object-fit: cover;" 
            />
            
            <q-card-section class="col">
              <div class="text-h6">{{ item.productName }}</div>
              <div class="text-subtitle1 text-green-8 text-weight-bold">{{ item.price }} руб.</div>
            </q-card-section>

            <q-card-section class="row items-center">
              <q-btn round dense flat icon="remove" @click="updateQuantity(item, -1)" :disable="item.quantity <= 1" />
              <div class="text-h6 q-mx-sm">{{ item.quantity }}</div>
              <q-btn round dense flat icon="add" @click="updateQuantity(item, 1)" />
            </q-card-section>

            <q-card-actions vertical class="justify-around">
              <q-btn flat round color="red" icon="delete" @click="removeItem(item.id)" />
            </q-card-actions>
          </q-card-section>
        </q-card>
      </div>

      <div class="col-12 col-md-4">
        <q-card class="q-pa-md sticky-summary">
          <div class="text-h6 q-mb-md">Сумма заказа</div>
          
          <div class="row justify-between text-subtitle1 q-mb-sm">
            <span>Товары ({{ totalItems }} шт.)</span>
            <span class="text-weight-bold">{{ totalPrice }} руб.</span>
          </div>
          
          <q-separator class="q-my-md" />

          <q-form @submit.prevent="createOrder">
            <q-input 
              v-model="orderForm.address" 
              label="Адрес доставки" 
              outlined 
              dense 
              class="q-mb-md" 
              required 
            />
            <q-input 
              v-model="orderForm.phone" 
              label="Контактный телефон" 
              outlined 
              dense 
              class="q-mb-md" 
              required 
              mask="+7 (###) ###-##-##"
            />

            <q-btn 
              type="submit" 
              color="green-8" 
              class="full-width q-mt-md" 
              size="lg" 
              :loading="orderLoading"
            >
              Оформить заказ
            </q-btn>
          </q-form>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';
import { useRouter } from 'vue-router';

const $q = useQuasar();
const router = useRouter();

const cartItems = ref([]);
const loading = ref(true);
const orderLoading = ref(false);

const orderForm = reactive({
  address: '',
  phone: ''
});

// Вычисляемые свойства для подсчета итогов
const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0).toFixed(2);
});

const totalItems = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0);
});

// Загрузка корзины
const loadCart = async () => {
  loading.value = true;
  try {
    const response = await api.get('/cart');
    cartItems.value = response.data;
  } catch (error) {
    console.error('Ошибка загрузки корзины', error);
  } finally {
    loading.value = false;
  }
};

// Изменение количества товара
const updateQuantity = async (item, change) => {
  const newQuantity = item.quantity + change;
  if (newQuantity < 1) return;

  try {
    // В контроллере у нас параметр запроса @RequestParam Integer quantity
    await api.put(`/cart/update/${item.id}?quantity=${newQuantity}`);
    await loadCart(); // Перезагружаем корзину, чтобы получить актуальные данные
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.error || 'Ошибка обновления количества' });
  }
};

// Удаление из корзины
const removeItem = async (itemId) => {
  try {
    await api.delete(`/cart/remove/${itemId}`);
    $q.notify({ type: 'info', message: 'Товар удален из корзины' });
    await loadCart();
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Ошибка удаления товара' });
    console.error(error);
  }
};

// Оформление заказа
const createOrder = async () => {
  orderLoading.value = true;
  try {
    await api.post('/orders', orderForm);
    $q.notify({ type: 'positive', message: 'Заказ успешно оформлен!', timeout: 3000 });
    
    // После успеха перекидываем в профиль (там мы позже сделаем историю заказов)
    router.push('/profile');
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.error || 'Ошибка оформления заказа' });
    console.error(error);
  } finally {
    orderLoading.value = false;
  }
};

onMounted(() => {
  loadCart();
});
</script>

<style scoped>
/* Делаем так, чтобы правая панель "прилипала" при прокрутке длинного списка товаров */
.sticky-summary {
  position: sticky;
  top: 70px; 
}
</style>