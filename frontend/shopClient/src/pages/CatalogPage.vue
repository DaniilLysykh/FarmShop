<template>
  <q-page class="catalog-page">
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-badge">
          <q-icon name="eco" />
          Фермерские продукты
        </div>
        <h1 class="hero-title">Свежие продукты<br/>от местных фермеров</h1>
        <p class="hero-subtitle">Без наценок, прямо с грядки на ваш стол</p>
        
        <div class="search-wrapper">
          <q-input 
            v-model="searchQuery" 
            outlined
            rounded
            placeholder="Поиск товаров..."
            class="search-input"
            @keyup.enter="loadProducts"
          >
            <template v-slot:prepend>
              <q-icon name="search" color="grey-5" />
            </template>
            <template v-slot:append>
              <q-btn round dense flat color="green-8" icon="arrow_forward" @click="loadProducts" class="search-btn" />
            </template>
          </q-input>
        </div>
      </div>
      
      <div class="hero-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>
    </div>

    <div class="catalog-container">
      <div class="catalog-header">
        <div class="catalog-title-section">
          <h2 class="catalog-title">Каталог товаров</h2>
          <span class="product-count">{{ products.length }} товаров</span>
        </div>
        <q-select 
          v-model="selectedCategory" 
          :options="categories" 
          outlined 
          rounded
          dense
          label="Категория"
          clearable 
          class="category-filter"
        />
      </div>

      <div v-if="loading" class="loading-state">
        <q-spinner-dots color="green-6" size="4em" />
      </div>

      <div v-else-if="products.length === 0" class="empty-state">
        <q-icon name="search_off" class="empty-state__icon" />
        <div class="empty-state__title">Товары не найдены</div>
        <div class="empty-state__text">Попробуйте изменить параметры поиска</div>
      </div>

      <div v-else class="products-grid">
        <div class="product-card" v-for="product in products" :key="product.id">
          <div class="product-image-wrapper">
            <q-img 
              :src="product.imageUrl ? `http://26.151.165.100:8080${product.imageUrl}` : 'https://via.placeholder.com/400x300?text=Нет+фото'" 
              class="product-image"
            />
            <q-chip 
              dense 
              class="product-category"
            >
              <q-icon name="category" class="q-mr-xs" />
              {{ product.category }}
            </q-chip>
          </div>
          
          <div class="product-content">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-description">{{ product.description }}</p>
            
            <div class="product-footer">
              <div class="product-price">
                <span class="price-value">{{ product.price }}</span>
                <span class="price-unit">руб / {{ product.unit }}</span>
              </div>
              
              <div class="product-actions" v-if="authStore.isCustomer">
                <q-btn flat round :icon="isFavorite(product.id) ? 'favorite' : 'favorite_border'" :color="isFavorite(product.id) ? 'red' : 'grey-5'" @click="toggleFavorite(product.id)" class="action-btn">
                  <q-tooltip>{{ isFavorite(product.id) ? 'Убрать из избранного' : 'В избранное' }}</q-tooltip>
                </q-btn>
                <q-btn unelevated class="add-to-cart-btn" @click="addToCart(product.id)">
                  <q-icon name="add_shopping_cart" />
                  В корзину
                </q-btn>
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
import { useAuthStore } from 'stores/auth';
import { useQuasar } from 'quasar';

const $q = useQuasar();
const authStore = useAuthStore();
const products = ref([]);
const categories = ref([]);
const loading = ref(false);
const searchQuery = ref('');
const selectedCategory = ref(null);
const favoriteIds = ref([]);

const loadCategories = async () => {
  try {
    const response = await api.get('/categories');
    categories.value = response.data;
  } catch (error) {
    console.error('Ошибка загрузки категорий', error);
  }
};

const loadProducts = async () => {
  loading.value = true;
  try {
    const params = { search: searchQuery.value || null, category: selectedCategory.value || null, size: 50 };
    const response = await api.get('/products/public', { params });
    products.value = response.data.content;
  } catch (error) { console.error(error); } 
  finally { loading.value = false; }
};

const loadFavorites = async () => {
  try {
    const response = await api.get('/favorites');
    favoriteIds.value = response.data.map(item => item.productId);
  } catch (error) {
    console.error('Ошибка загрузки избранного', error);
  }
};

const isFavorite = (productId) => {
  return favoriteIds.value.includes(productId);
};

const toggleFavorite = async (productId) => {
  try {
    if (isFavorite(productId)) {
      // Находим id избранного для удаления
      const favoriteId = favoriteIds.value.find(id => id === productId);
      if (favoriteId) {
        await api.delete(`/favorites/remove/${productId}`);
        favoriteIds.value = favoriteIds.value.filter(id => id !== productId);
        $q.notify({ type: 'info', message: 'Товар убран из избранного' });
      }
    } else {
      await api.post('/favorites/add', { productId: productId });
      favoriteIds.value.push(productId);
      $q.notify({ type: 'positive', message: 'Товар добавлен в избранное' });
    }
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Ошибка обновления избранного' });
  }
};

const addToCart = async (productId) => {
  try {
    await api.post('/cart/add', { productId: productId, quantity: 1 });
    $q.notify({ type: 'positive', message: 'Товар добавлен в корзину' });
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.error || 'Ошибка добавления в корзину' });
  }
};

onMounted(() => { 
  loadCategories(); 
  loadProducts(); 
  if (authStore.isCustomer) {
    loadFavorites();
  }
});
</script>

<style scoped lang="scss">
.catalog-page {
  background: #f5f7f5;
  min-height: 100vh;
}

.hero-section {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 60px 24px 100px;
  position: relative;
  overflow: hidden;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
  position: relative;
  z-index: 2;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.15);
  color: white;
  padding: 8px 20px;
  border-radius: 30px;
  font-size: 0.875rem;
  font-weight: 500;
  margin-bottom: 24px;
  backdrop-filter: blur(10px);
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  color: white;
  margin: 0 0 16px;
  line-height: 1.2;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.hero-subtitle {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 40px;
}

.search-wrapper {
  max-width: 600px;
  margin: 0 auto;
}

.search-input {
  background: white;
  border-radius: 16px;
  
  :deep(.q-field__control) {
    height: 60px;
    border-radius: 16px;
    font-size: 1.1rem;
  }
  
  :deep(.q-field__prepend) {
    padding-left: 20px;
  }
  
  :deep(.q-field__append) {
    padding-right: 12px;
  }
}

.search-btn {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  color: white;
  border-radius: 12px;
  
  &:hover {
    transform: scale(1.05);
  }
}

.hero-decoration {
  position: absolute;
  inset: 0;
  z-index: 1;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  
  &.circle-1 {
    width: 400px;
    height: 400px;
    top: -200px;
    right: -100px;
  }
  
  &.circle-2 {
    width: 300px;
    height: 300px;
    bottom: -150px;
    left: -50px;
  }
  
  &.circle-3 {
    width: 200px;
    height: 200px;
    top: 50%;
    left: 10%;
  }
}

.catalog-container {
  max-width: 1400px;
  margin: -60px auto 0;
  padding: 0 24px 60px;
  position: relative;
  z-index: 2;
}

.catalog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  background: white;
  padding: 24px;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.catalog-title-section {
  display: flex;
  align-items: baseline;
  gap: 16px;
}

.catalog-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.product-count {
  color: #888;
  font-size: 0.9rem;
}

.category-filter {
  min-width: 200px;
  
  :deep(.q-field__control) {
    border-radius: 12px;
  }
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.product-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 16px 40px rgba(46, 125, 50, 0.15);
    
    .product-image {
      transform: scale(1.05);
    }
  }
}

.product-image-wrapper {
  position: relative;
  overflow: hidden;
  height: 220px;
}

.product-image {
  width: 100%;
  height: 100%;
  transition: transform 0.4s ease;
}

.product-category {
  position: absolute;
  top: 12px;
  right: 12px;
  background: white;
  color: #2e7d32;
  font-weight: 600;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.product-content {
  padding: 20px;
}

.product-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-description {
  font-size: 0.875rem;
  color: #666;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 40px;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  display: flex;
  flex-direction: column;
}

.price-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2e7d32;
}

.price-unit {
  font-size: 0.8rem;
  color: #888;
}

.product-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  width: 40px;
  height: 40px;
}

.add-to-cart-btn {
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  color: white;
  padding: 10px 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.85rem;
  box-shadow: 0 4px 15px rgba(46, 125, 50, 0.3);
  transition: all 0.3s ease;
  
  .q-icon {
    margin-right: 6px;
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(46, 125, 50, 0.4);
  }
}

.hover-red:hover {
  color: #f44336 !important;
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 80px 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
  background: white;
  border-radius: 20px;
  
  &__icon {
    font-size: 80px;
    opacity: 0.3;
    color: #888;
  }
  
  &__title {
    font-size: 1.25rem;
    font-weight: 600;
    color: #333;
    margin: 20px 0 8px;
  }
  
  &__text {
    color: #888;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .catalog-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .catalog-title-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>