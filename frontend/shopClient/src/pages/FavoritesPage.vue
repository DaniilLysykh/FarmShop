<template>
  <q-page padding class="bg-grey-2">
    <div class="text-h4 q-mb-md">Избранное</div>

    <div v-if="loading" class="text-center q-pa-xl">
      <q-spinner color="green" size="3em" />
    </div>

    <div v-else-if="favorites.length === 0" class="text-center q-pa-xl">
      <q-icon name="favorite_border" size="100px" color="grey-4" />
      <div class="text-h6 text-grey-6 q-mt-md">В списке избранного пока ничего нет</div>
      <q-btn color="green-8" label="Вернуться в каталог" to="/catalog" class="q-mt-md" />
    </div>

    <div v-else class="row q-col-gutter-md">
      <div class="col-12 col-sm-6 col-md-4 col-lg-3" v-for="item in favorites" :key="item.id">
        <q-card class="column full-height">
          <q-img 
            :src="item.imageUrl ? `http://localhost:8080${item.imageUrl}` : 'https://via.placeholder.com/300x200?text=Нет+фото'" 
            height="200px" 
          />
          
          <q-card-section class="col">
            <div class="text-h6">{{ item.productName }}</div>
            <div class="text-subtitle1 text-green-8 text-weight-bold">{{ item.price }} руб.</div>
          </q-card-section>

          <q-card-actions align="around">
            <q-btn flat round color="red" icon="delete" @click="removeFromFavorites(item.id)">
              <q-tooltip>Удалить из избранного</q-tooltip>
            </q-btn>
            
            <q-btn flat color="green-8" icon="add_shopping_cart" label="В корзину" @click="addToCart(item.productId)" />
          </q-card-actions>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';

const $q = useQuasar();
const favorites = ref([]);
const loading = ref(true);

// Загрузка списка избранного
const loadFavorites = async () => {
  loading.value = true;
  try {
    const response = await api.get('/favorites');
    favorites.value = response.data;
  } catch (error) {
    console.error('Ошибка загрузки избранного', error);
  } finally {
    loading.value = false;
  }
};

// Удаление из избранного (DELETE запрос)
const removeFromFavorites = async (id) => {
  try {
    await api.delete(`/favorites/remove/${id}`);
    $q.notify({ type: 'info', message: 'Товар удален из избранного' });
    await loadFavorites(); // Обновляем список
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Ошибка при удалении' });
    console.error(error);
  }
};

// Добавление в корзину (повторяем логику из каталога)
const addToCart = async (productId) => {
  try {
    await api.post('/cart/add', { productId: productId, quantity: 1 });
    $q.notify({ type: 'positive', message: 'Товар добавлен в корзину' });
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Ошибка добавления в корзину' });
    console.error(error);
  }
};

onMounted(() => {
  loadFavorites();
});
</script>