<template>
  <q-page class="reviews-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">Мои отзывы</h1>
        <p class="page-subtitle" v-if="reviews.length">{{ reviews.length }} отзывов</p>
      </div>
    </div>

    <div class="reviews-container">
      <div v-if="loading" class="loading-state">
        <q-spinner color="green" size="3em" />
      </div>

      <div v-else-if="reviews.length === 0" class="empty-state">
        <q-icon name="rate_review" class="empty-icon" />
        <h2>Вы ещё не оставляли отзывов</h2>
        <p>Оцените товары в каталоге после покупки</p>
        <q-btn unelevated color="green-8" to="/catalog" label="В каталог" class="q-mt-md" />
      </div>

      <div v-else class="reviews-list">
        <div class="review-card" v-for="review in reviews" :key="review.id">
          <div class="review-header">
            <h3>{{ review.productName }}</h3>
            <q-rating :model-value="review.rating" max="5" size="20px" color="amber" readonly />
          </div>
          <p class="review-comment" v-if="review.comment">{{ review.comment }}</p>
          <p class="review-comment muted" v-else>Без комментария</p>
          <div class="review-footer">
            <span class="review-date">{{ formatDate(review.createdAt) }}</span>
            <q-btn flat dense color="negative" label="Удалить" @click="removeReview(review.id)" />
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

const $q = useQuasar();
const { formatDate } = useFormatDate();
const reviews = ref([]);
const loading = ref(true);

const loadReviews = async () => {
  loading.value = true;
  try {
    const response = await api.get('/reviews/my');
    reviews.value = response.data;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const removeReview = async (id) => {
  try {
    await api.delete(`/reviews/${id}`);
    reviews.value = reviews.value.filter((r) => r.id !== id);
    $q.notify({ type: 'info', message: 'Отзыв удалён' });
  } catch {
    $q.notify({ type: 'negative', message: 'Не удалось удалить отзыв' });
  }
};

onMounted(loadReviews);
</script>

<style scoped lang="scss">
.reviews-page {
  background: #f5f7f5;
  min-height: 100vh;
}

.page-header {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 40px 24px;
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

.reviews-container {
  max-width: 800px;
  margin: -30px auto 0;
  padding: 0 24px 60px;
}

.review-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;

  h3 {
    margin: 0;
    font-size: 1rem;
  }
}

.review-comment {
  margin: 0 0 12px;
  color: #444;

  &.muted {
    color: #aaa;
    font-style: italic;
  }
}

.review-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.review-date {
  font-size: 0.8rem;
  color: #999;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 20px;
}

.empty-icon {
  font-size: 64px;
  color: #ccc;
}
</style>
