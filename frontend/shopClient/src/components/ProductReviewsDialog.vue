<template>
  <q-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" position="bottom">
    <q-card class="reviews-dialog">
      <q-card-section class="dialog-header">
        <div>
          <div class="dialog-title">{{ product?.name }}</div>
          <div class="dialog-rating" v-if="product?.reviewCount">
            <q-rating :model-value="product.averageRating || 0" max="5" size="20px" color="amber" readonly />
            <span>{{ product.averageRating }} ({{ product.reviewCount }} отзывов)</span>
          </div>
          <div class="dialog-rating muted" v-else>Пока нет отзывов</div>
        </div>
        <q-btn flat round dense icon="close" v-close-popup />
      </q-card-section>

      <q-separator />

      <q-card-section v-if="authStore.isCustomer" class="review-form-section">
        <div class="form-title">{{ myReview ? 'Ваш отзыв' : 'Оставить отзыв' }}</div>
        <q-rating v-model="form.rating" max="5" size="28px" color="amber" />
        <q-input
          v-model="form.comment"
          type="textarea"
          outlined
          rounded
          autogrow
          placeholder="Расскажите о качестве товара..."
          class="q-mt-sm"
        />
        <div class="form-actions">
          <q-btn
            v-if="myReview"
            flat
            color="negative"
            label="Удалить"
            @click="deleteReview"
            :loading="submitting"
          />
          <q-space />
          <q-btn
            unelevated
            color="green-8"
            :label="myReview ? 'Сохранить' : 'Отправить'"
            @click="submitReview"
            :loading="submitting"
          />
        </div>
      </q-card-section>

      <q-card-section class="reviews-list-section">
        <div class="list-title">Все отзывы</div>
        <div v-if="loading" class="loading-row">
          <q-spinner color="green" />
        </div>
        <div v-else-if="reviews.length === 0" class="empty-reviews">Отзывов пока нет. Будьте первым!</div>
        <div v-else class="review-item" v-for="review in reviews" :key="review.id">
          <div class="review-meta">
            <q-rating :model-value="review.rating" max="5" size="16px" color="amber" readonly />
            <span class="review-author">{{ review.userEmail }}</span>
            <span class="review-date">{{ formatDate(review.createdAt) }}</span>
          </div>
          <p class="review-text" v-if="review.comment">{{ review.comment }}</p>
          <p class="review-text muted" v-else>Без комментария</p>
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch } from 'vue';
import { api } from 'boot/axios';
import { useAuthStore } from 'stores/auth';
import { useQuasar } from 'quasar';
import { useFormatDate } from 'src/composables/useFormatDate';

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  product: { type: Object, default: null },
});

const emit = defineEmits(['update:modelValue', 'review-changed']);

const $q = useQuasar();
const authStore = useAuthStore();
const { formatDate } = useFormatDate();

const reviews = ref([]);
const myReview = ref(null);
const loading = ref(false);
const submitting = ref(false);
const form = ref({ rating: 5, comment: '' });

const loadReviews = async () => {
  if (!props.product?.id) return;
  loading.value = true;
  try {
    const response = await api.get(`/reviews/product/${props.product.id}`);
    reviews.value = response.data;
    myReview.value = reviews.value.find((r) => r.ownReview) || null;
    if (myReview.value) {
      form.value = { rating: myReview.value.rating, comment: myReview.value.comment || '' };
    } else {
      form.value = { rating: 5, comment: '' };
    }
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const submitReview = async () => {
  if (!props.product?.id) return;
  submitting.value = true;
  try {
    const payload = {
      productId: props.product.id,
      rating: form.value.rating,
      comment: form.value.comment,
    };
    if (myReview.value) {
      await api.put(`/reviews/${myReview.value.id}`, payload);
      $q.notify({ type: 'positive', message: 'Отзыв обновлён' });
    } else {
      await api.post('/reviews', payload);
      $q.notify({ type: 'positive', message: 'Спасибо за отзыв!' });
    }
    await loadReviews();
    emit('review-changed');
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.error || 'Не удалось сохранить отзыв',
    });
  } finally {
    submitting.value = false;
  }
};

const deleteReview = async () => {
  if (!myReview.value) return;
  submitting.value = true;
  try {
    await api.delete(`/reviews/${myReview.value.id}`);
    $q.notify({ type: 'info', message: 'Отзыв удалён' });
    myReview.value = null;
    form.value = { rating: 5, comment: '' };
    await loadReviews();
    emit('review-changed');
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Не удалось удалить отзыв' });
    console.error(error);
  } finally {
    submitting.value = false;
  }
};

watch(
  () => [props.modelValue, props.product?.id],
  ([open]) => {
    if (open) loadReviews();
  }
);
</script>

<style scoped lang="scss">
.reviews-dialog {
  width: 100%;
  max-width: 560px;
  border-radius: 20px 20px 0 0;
  max-height: 85vh;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.dialog-title {
  font-size: 1.1rem;
  font-weight: 600;
}

.dialog-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
  font-size: 0.85rem;
  color: #666;

  &.muted {
    color: #999;
  }
}

.review-form-section {
  background: var(--bg-muted);
}

.form-title {
  font-weight: 600;
  margin-bottom: 8px;
}

.form-actions {
  display: flex;
  margin-top: 12px;
}

.reviews-list-section {
  max-height: 40vh;
  overflow-y: auto;
}

.list-title {
  font-weight: 600;
  margin-bottom: 12px;
}

.review-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.review-author {
  font-weight: 500;
  font-size: 0.85rem;
}

.review-date {
  font-size: 0.75rem;
  color: #999;
}

.review-text {
  margin: 8px 0 0;
  font-size: 0.9rem;
  color: #444;

  &.muted {
    color: #aaa;
    font-style: italic;
  }
}

.loading-row,
.empty-reviews {
  text-align: center;
  padding: 24px;
  color: #888;
}
</style>
