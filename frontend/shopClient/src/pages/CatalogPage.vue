<template>
  <q-page class="bg-grey-1 pb-xl">
    <div class="hero-section bg-green-8 text-white q-py-xl q-px-md text-center shadow-3">
      <div class="text-h3 text-weight-bold q-mb-md" style="letter-spacing: -1px;">Свежие продукты от фермеров</div>
      <div class="text-h6 text-weight-regular text-green-2 q-mb-lg">Без наценок, прямо с грядки на ваш стол</div>
      
      <div class="row justify-center">
        <div class="col-12 col-md-6 col-lg-5">
          <q-input 
            v-model="searchQuery" 
            outlined 
            rounded
            bg-color="white"
            placeholder="Найти молоко, мед, овощи..." 
            class="shadow-2 search-input"
            @keyup.enter="loadProducts"
          >
            <template v-slot:prepend>
              <q-icon name="search" color="grey-6" />
            </template>
            <template v-slot:append>
              <q-btn round dense flat color="green-8" icon="arrow_forward" @click="loadProducts" />
            </template>
          </q-input>
        </div>
      </div>
    </div>

    <div class="container q-pa-md q-mt-lg">
      <div class="row items-center q-mb-xl justify-between">
        <div class="text-h5 text-weight-bold text-dark">Все товары</div>
        <div class="col-auto">
          <q-select 
            v-model="selectedCategory" 
            :options="categories" 
            outlined 
            dense 
            rounded
            bg-color="white"
            label="Категория" 
            clearable 
            style="min-width: 200px"
            @update:model-value="loadProducts" 
          />
        </div>
      </div>

      <div v-if="loading" class="text-center q-pa-xl">
        <q-spinner-dots color="green-7" size="4em" />
      </div>

      <div v-else-if="products.length === 0" class="text-center q-pa-xl">
        <q-icon name="eco" size="6em" color="grey-4" />
        <div class="text-h6 text-grey-6 q-mt-md">К сожалению, по вашему запросу ничего не найдено</div>
      </div>

      <div v-else class="row q-col-gutter-lg">
        <div class="col-12 col-sm-6 col-md-4 col-lg-3" v-for="product in products" :key="product.id">
          <q-card class="product-card full-height column no-shadow border-light">
            <div class="relative-position">
              <q-img 
                :src="product.imageUrl ? `http://localhost:8080${product.imageUrl}` : 'https://via.placeholder.com/300x200?text=Нет+фото'" 
                height="220px" 
                class="card-image"
              />
              <q-chip 
                dense 
                color="white" 
                text-color="green-9" 
                class="absolute-top-right q-ma-sm text-weight-bold shadow-1"
              >
                {{ product.category }}
              </q-chip>
            </div>
            
            <q-card-section class="col q-pt-md">
              <div class="text-h6 text-weight-bold text-dark ellipsis" :title="product.name">{{ product.name }}</div>
              <div class="text-caption text-grey-7 q-mt-xs ellipsis-2-lines" style="height: 40px;">
                {{ product.description }}
              </div>
              <div class="text-h5 text-green-8 text-weight-bolder q-mt-md">
                {{ product.price }} <span class="text-subtitle1 text-weight-medium">руб / {{ product.unit }}</span>
              </div>
            </q-card-section>

            <q-card-actions align="between" class="q-px-md q-pb-md q-pt-none" v-if="authStore.isCustomer">
              <q-btn flat round color="grey-5" icon="favorite_border" @click="addToFavorites(product.id)" class="hover-red">
                <q-tooltip>В избранное</q-tooltip>
              </q-btn>
              <q-btn unelevated rounded color="green-7" icon="add_shopping_cart" label="В корзину" class="q-px-md text-weight-bold" @click="addToCart(product.id)" />
            </q-card-actions>
          </q-card>
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

const addToCart = async (productId) => {
  try {
    await api.post('/cart/add', { productId: productId, quantity: 1 });
    $q.notify({ type: 'positive', message: 'Товар добавлен в корзину' });
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.error || 'Ошибка добавления в корзину' });
  }
};

const addToFavorites = async (productId) => {
  try {
    await api.post('/favorites/add', { productId: productId });
    $q.notify({ type: 'positive', message: 'Товар добавлен в избранное' });
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.error || 'Товар уже в избранном' });
  }
};

onMounted(() => { loadCategories(); loadProducts(); });
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
}

/* Стили для Hero секции */
.hero-section {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  border-bottom-left-radius: 40px;
  border-bottom-right-radius: 40px;
}

.search-input :deep(.q-field__control) {
  height: 56px;
  font-size: 1.1rem;
}

/* Стили для карточек */
.border-light {
  border: 1px solid rgba(0,0,0,0.05);
  border-radius: 16px;
  background: #ffffff;
}

.card-image {
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
}

.product-card {
  transition: all 0.3s ease;
}

/* Главная фишка: анимация при наведении */
.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(46, 125, 50, 0.15) !important;
  border-color: transparent;
}

.ellipsis-2-lines {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hover-red:hover {
  color: #f44336 !important;
}
</style>