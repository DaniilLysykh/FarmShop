<template>
  <q-page class="favorites-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">Избранное</h1>
        <p class="page-subtitle" v-if="favorites.length > 0">{{ favorites.length }} товаров</p>
      </div>
    </div>

    <div class="favorites-container">
      <div v-if="loading" class="loading-state">
        <q-spinner color="green" size="3em" />
      </div>

      <div v-else-if="favorites.length === 0" class="empty-state">
        <div class="empty-icon">
          <q-icon name="favorite_border" />
        </div>
        <h2 class="empty-title">В избранном пусто</h2>
        <p class="empty-text">Добавляйте понравившиеся товары</p>
        <q-btn unelevated to="/catalog" class="empty-btn">
          <q-icon name="store" class="q-mr-sm" />
          В каталог
        </q-btn>
      </div>

      <div v-else class="favorites-grid">
        <div class="favorite-card" v-for="item in favorites" :key="item.id">
          <div class="card-image-wrapper">
            <q-img 
              :src="item.imageUrl ? `http://26.151.165.100:8080${item.imageUrl}` : 'https://via.placeholder.com/400x300?text=Нет+фото'" 
              class="card-image"
            />
            <q-btn round flat icon="favorite" color="red" class="fav-btn" @click="removeFromFavorites(item.id)">
              <q-tooltip>Удалить</q-tooltip>
            </q-btn>
          </div>
          
          <div class="card-content">
            <h3 class="card-title">{{ item.productName }}</h3>
            <div class="card-price">{{ item.price }} руб.</div>
            
            <q-btn unelevated class="add-btn" @click="addToCart(item.productId)">
              <q-icon name="add_shopping_cart" />
              В корзину
            </q-btn>
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

const $q = useQuasar();
const favorites = ref([]);
const loading = ref(true);

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

const removeFromFavorites = async (id) => {
  try {
    await api.delete(`/favorites/remove/${id}`);
    $q.notify({ type: 'info', message: 'Товар удален из избранного' });
    await loadFavorites();
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Ошибка при удалении' });
    console.error(error);
  }
};

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

<style scoped lang="scss">
.favorites-page {
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

.favorites-container {
  max-width: 1400px;
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
  background: white;
  border-radius: 24px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  width: 120px;
  height: 120px;
  background: #fff0f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  
  .q-icon {
    font-size: 60px;
    color: #ef5350;
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

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.favorite-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
  }
}

.card-image-wrapper {
  position: relative;
  height: 200px;
}

.card-image {
  width: 100%;
  height: 100%;
}

.fav-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

.card-content {
  padding: 20px;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px;
}

.card-price {
  font-size: 1.25rem;
  font-weight: 700;
  color: #2e7d32;
  margin-bottom: 16px;
}

.add-btn {
  width: 100%;
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  color: white;
  padding: 12px;
  border-radius: 12px;
  font-weight: 600;
  
  .q-icon {
    margin-right: 8px;
  }
  
  &:hover {
    box-shadow: 0 4px 15px rgba(46, 125, 50, 0.3);
  }
}
</style>