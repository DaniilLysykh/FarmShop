<template>
  <q-page class="cart-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">Корзина</h1>
        <p class="page-subtitle" v-if="totalItems > 0">{{ totalItems }} товаров</p>
      </div>
    </div>

    <div class="cart-container">
      <div v-if="cartItems.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">
          <q-icon name="shopping_cart" />
        </div>
        <h2 class="empty-title">Корзина пуста</h2>
        <p class="empty-text">Добавьте товары из каталога</p>
        <q-btn unelevated to="/catalog" class="empty-btn">
          <q-icon name="store" class="q-mr-sm" />
          Перейти в каталог
        </q-btn>
      </div>

      <div v-else class="cart-layout">
        <div class="cart-items">
          <div class="cart-item" v-for="item in cartItems" :key="item.id">
            <q-img 
              :src="item.imageUrl ? `http://26.151.165.100:8080${item.imageUrl}` : 'https://via.placeholder.com/150?text=Нет+фото'" 
              class="item-image"
            />
            
            <div class="item-details">
              <h3 class="item-name">{{ item.productName }}</h3>
              <p class="item-price">{{ item.price }} руб.</p>
            </div>

            <div class="item-quantity">
              <q-btn round dense flat icon="remove" @click="updateQuantity(item, -1)" :disable="item.quantity <= 1" class="qty-btn" />
              <span class="qty-value">{{ item.quantity }}</span>
              <q-btn round dense flat icon="add" @click="updateQuantity(item, 1)" class="qty-btn" />
            </div>

            <div class="item-total">
              {{ (item.price * item.quantity).toFixed(2) }} руб.
            </div>

            <q-btn round flat color="red" icon="delete" @click="removeItem(item.id)" class="delete-btn">
              <q-tooltip>Удалить</q-tooltip>
            </q-btn>
          </div>
        </div>

        <div class="cart-summary">
          <div class="summary-card">
            <h2 class="summary-title">Оформление заказа</h2>
            
            <div class="summary-row">
              <span>Товары ({{ totalItems }} шт.)</span>
              <span class="summary-value">{{ totalPrice }} руб.</span>
            </div>
            
            <q-separator class="q-my-md" />
            
            <div class="summary-total">
              <span>Итого</span>
              <span class="total-value">{{ totalPrice }} руб.</span>
            </div>

            <q-form @submit.prevent="createOrder" class="order-form">
              <div class="form-group">
                <label>Адрес доставки</label>
                <q-input 
                  v-model="orderForm.address" 
                  outlined
                  rounded
                  placeholder="ул. Примерная, д. 1"
                  class="form-input"
                  required 
                >
                  <template v-slot:prepend>
                    <q-icon name="location_on" color="grey-6" />
                  </template>
                </q-input>
              </div>
              
              <div class="form-group">
                <label>Телефон</label>
                <q-input 
                  v-model="orderForm.phone" 
                  outlined
                  rounded
                  placeholder="+7 (999) 123-45-67"
                  mask="+7 (###) ###-##-##"
                  class="form-input"
                  required 
                >
                  <template v-slot:prepend>
                    <q-icon name="phone" color="grey-6" />
                  </template>
                </q-input>
              </div>

              <q-btn 
                type="submit"
                unelevated
                class="order-btn"
                size="lg"
                :loading="orderLoading"
              >
                <q-icon name="check_circle" class="q-mr-sm" />
                Оформить заказ
              </q-btn>
            </q-form>
          </div>
        </div>
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

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0).toFixed(2);
});

const totalItems = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0);
});

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

const updateQuantity = async (item, change) => {
  const newQuantity = item.quantity + change;
  if (newQuantity < 1) return;

  try {
    await api.put(`/cart/update/${item.id}?quantity=${newQuantity}`);
    await loadCart();
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.error || 'Ошибка обновления количества' });
  }
};

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

const createOrder = async () => {
  orderLoading.value = true;
  try {
    await api.post('/orders', orderForm);
    $q.notify({ type: 'positive', message: 'Заказ успешно оформлен!', timeout: 3000 });
    router.push('/profile');
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.error || 'Ошибка оформления заказа' });
  } finally {
    orderLoading.value = false;
  }
};

onMounted(() => {
  loadCart();
});
</script>

<style scoped lang="scss">
.cart-page {
  background: #f5f7f5;
  min-height: 100vh;
}

.page-header {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 40px 24px;
}

.header-content {
  max-width: 1400px;
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

.cart-container {
  max-width: 1400px;
  margin: -40px auto 0;
  padding: 0 24px 60px;
  position: relative;
  z-index: 2;
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
  margin: 0 0 24px;
}

.empty-btn {
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  color: white;
  padding: 14px 32px;
  border-radius: 12px;
  font-weight: 600;
}

.cart-layout {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 32px;
}

.cart-items {
  background: white;
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  
  &:last-child {
    border-bottom: none;
  }
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: 16px;
  object-fit: cover;
}

.item-details {
  flex: 1;
}

.item-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px;
}

.item-price {
  color: #888;
  margin: 0;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f5f7f5;
  padding: 8px 16px;
  border-radius: 12px;
}

.qty-btn {
  color: #2e7d32;
}

.qty-value {
  font-size: 1.1rem;
  font-weight: 600;
  min-width: 30px;
  text-align: center;
}

.item-total {
  font-size: 1.25rem;
  font-weight: 700;
  color: #2e7d32;
  min-width: 120px;
  text-align: right;
}

.delete-btn {
  margin-left: 12px;
}

.cart-summary {
  position: sticky;
  top: 90px;
}

.summary-card {
  background: white;
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.summary-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 24px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  color: #666;
}

.summary-value {
  font-weight: 500;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 24px;
}

.total-value {
  color: #2e7d32;
}

.order-form {
  margin-top: 16px;
}

.form-group {
  margin-bottom: 20px;
  
  label {
    display: block;
    font-weight: 500;
    color: #333;
    margin-bottom: 8px;
    font-size: 0.9rem;
  }
}

.form-input {
  :deep(.q-field__control) {
    border-radius: 12px;
    height: 48px;
  }
}

.order-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  color: white;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  margin-top: 8px;
  box-shadow: 0 4px 20px rgba(46, 125, 50, 0.3);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 25px rgba(46, 125, 50, 0.4);
  }
}

@media (max-width: 1024px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }
  
  .cart-summary {
    position: static;
  }
}

@media (max-width: 768px) {
  .cart-item {
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .item-image {
    width: 80px;
    height: 80px;
  }
  
  .item-total {
    min-width: auto;
  }
}
</style>