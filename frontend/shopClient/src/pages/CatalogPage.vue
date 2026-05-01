<template>
  <q-page padding class="bg-grey-2">
    <div class="text-h4 q-mb-md">Каталог фермерских продуктов</div>

    <div class="row q-col-gutter-md q-mb-md">
      <div class="col-12 col-md-8">
        <q-input 
          v-model="searchQuery" 
          outlined 
          dense 
          bg-color="white"
          placeholder="Поиск по названию (нажмите Enter)..." 
          @keyup.enter="loadProducts"
        >
          <template v-slot:append>
            <q-icon name="search" class="cursor-pointer" @click="loadProducts" />
          </template>
        </q-input>
      </div>
      
      <div class="col-12 col-md-4">
        <q-select 
          v-model="selectedCategory" 
          :options="categories" 
          outlined 
          dense 
          bg-color="white"
          label="Все категории" 
          clearable 
          @update:model-value="loadProducts" 
        />
      </div>
    </div>

    <div v-if="loading" class="text-center q-pa-xl">
      <q-spinner color="green" size="3em" />
    </div>

    <div v-else class="row q-col-gutter-md">
      <div class="col-12 col-sm-6 col-md-4 col-lg-3" v-for="product in products" :key="product.id">
        <q-card class="column full-height">
          <q-img 
            :src="product.imageUrl ? `http://localhost:8080${product.imageUrl}` : 'https://via.placeholder.com/300x200?text=Нет+фото'" 
            height="200px" 
          />
          
          <q-card-section class="col">
            <div class="text-h6">{{ product.name }}</div>
            <div class="text-subtitle1 text-green-8 text-weight-bold">{{ product.price }} руб. / {{ product.unit }}</div>
            <div class="text-caption text-grey q-mb-sm">{{ product.description }}</div>
            <q-chip size="sm" color="orange" text-color="white">{{ product.category }}</q-chip>
          </q-card-section>

          <q-card-actions align="around" v-if="authStore.isCustomer">
            <q-btn flat round color="red" icon="favorite_border" @click="addToFavorites(product.id)" />
            <q-btn flat color="green-8" icon="add_shopping_cart" label="В корзину" @click="addToCart(product.id)" />
          </q-card-actions>
        </q-card>
      </div>
    </div>

    <div v-if="!loading && products.length === 0" class="text-center text-h6 text-grey q-pa-xl">
      Товары не найдены. Попробуйте изменить параметры поиска.
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

// Загрузка списка категорий
const loadCategories = async () => {
  try {
    const response = await api.get('/categories');
    categories.value = response.data;
  } catch (error) {
    console.error('Ошибка загрузки категорий', error);
  }
};

// Загрузка товаров с учетом фильтров
const loadProducts = async () => {
  loading.value = true;
  try {
    const params = {
      search: searchQuery.value || null,
      category: selectedCategory.value || null,
      size: 50 // Пока загружаем до 50 товаров без сложной пагинации
    };

    const response = await api.get('/products/public', { params });
    // Бэкенд возвращает объект Page, поэтому товары лежат в массиве content
    products.value = response.data.content;
  } catch (error) {
    console.error('Ошибка загрузки каталога', error);
  } finally {
    loading.value = false;
  }
};

// Добавление в корзину
const addToCart = async (productId) => {
  try {
    await api.post('/cart/add', { productId: productId, quantity: 1 });
    $q.notify({ type: 'positive', message: 'Товар добавлен в корзину' });
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.error || 'Ошибка добавления в корзину' });
  }
};

// Добавление в избранное
const addToFavorites = async (productId) => {
  try {
    await api.post('/favorites/add', { productId: productId });
    $q.notify({ type: 'positive', message: 'Товар добавлен в избранное' });
  } catch (error) {
    $q.notify({ type: 'negative', message: error.response?.data?.error || 'Товар уже в избранном' });
  }
};

onMounted(() => {
  loadCategories();
  loadProducts();
});
</script>