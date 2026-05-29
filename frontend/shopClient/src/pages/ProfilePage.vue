<template>
  <q-page class="profile-page">
    <div class="page-header">
      <div class="header-content">
        <div class="profile-avatar">
          <q-icon name="person" />
        </div>
        <h1 class="page-title">Мой профиль</h1>
        <p class="page-subtitle">Управление аккаунтом</p>
      </div>
    </div>

    <div class="profile-container">
      <div class="profile-card" v-if="userProfile">
        <div class="profile-header">
          <div class="profile-icon">
            <q-icon name="account_circle" />
          </div>
          <div class="profile-info">
            <h2 class="profile-email">{{ userProfile.email }}</h2>
            <q-chip :color="isFarmer ? 'green' : 'blue'" text-color="white" class="role-chip">
              <q-icon :name="isFarmer ? 'agriculture' : 'shopping_bag'" class="q-mr-xs" />
              {{ isFarmer ? 'Фермер' : 'Покупатель' }}
            </q-chip>
          </div>
        </div>

        <q-separator />

        <div class="profile-details">
          <div class="detail-row">
            <div class="detail-icon">
              <q-icon name="email" />
            </div>
            <div class="detail-content">
              <span class="detail-label">Email</span>
              <span class="detail-value">{{ userProfile.email }}</span>
            </div>
          </div>
          
          <div class="detail-row">
            <div class="detail-icon">
              <q-icon name="calendar_today" />
            </div>
            <div class="detail-content">
              <span class="detail-label">Дата регистрации</span>
              <span class="detail-value">{{ formatDate(userProfile.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="loading-state">
        <q-spinner color="green" size="3em" />
      </div>

      <div class="profile-actions">
        <h3 class="actions-title">Быстрые действия</h3>

        <div class="action-card" @click="$router.push('/notifications')">
          <div class="action-icon orange">
            <q-icon name="notifications" />
          </div>
          <div class="action-content">
            <h4>Уведомления</h4>
            <p>Заказы, статусы и отзывы</p>
          </div>
          <q-icon name="chevron_right" class="action-arrow" />
        </div>
        
        <template v-if="isFarmer">
          <div class="action-card" @click="$router.push('/my-products')">
            <div class="action-icon green">
              <q-icon name="inventory_2" />
            </div>
            <div class="action-content">
              <h4>Мои товары</h4>
              <p>Управление товарами</p>
            </div>
            <q-icon name="chevron_right" class="action-arrow" />
          </div>
          
          <div class="action-card" @click="$router.push('/farmer-orders')">
            <div class="action-icon blue">
              <q-icon name="local_shipping" />
            </div>
            <div class="action-content">
              <h4>Входящие заказы</h4>
              <p>Обработка заказов</p>
            </div>
            <q-icon name="chevron_right" class="action-arrow" />
          </div>
        </template>
        
        <template v-else>
          <div class="action-card" @click="$router.push('/my-reviews')">
            <div class="action-icon amber">
              <q-icon name="rate_review" />
            </div>
            <div class="action-content">
              <h4>Мои отзывы</h4>
              <p>Ваши оценки товаров</p>
            </div>
            <q-icon name="chevron_right" class="action-arrow" />
          </div>

          <div class="action-card" @click="$router.push('/favorites')">
            <div class="action-icon red">
              <q-icon name="favorite" />
            </div>
            <div class="action-content">
              <h4>Избранное</h4>
              <p>Сохраненные товары</p>
            </div>
            <q-icon name="chevron_right" class="action-arrow" />
          </div>
          
          <div class="action-card" @click="$router.push('/my-orders')">
            <div class="action-icon purple">
              <q-icon name="receipt_long" />
            </div>
            <div class="action-content">
              <h4>История заказов</h4>
              <p>Просмотр заказов</p>
            </div>
            <q-icon name="chevron_right" class="action-arrow" />
          </div>
        </template>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { api } from 'boot/axios';
import { useAuthStore } from 'stores/auth';
import { useFormatDate } from 'src/composables/useFormatDate';

const { formatDate } = useFormatDate();
const authStore = useAuthStore();
const userProfile = ref(null);

const isFarmer = computed(() => authStore.isFarmer);

onMounted(async () => {
  try {
    const response = await api.get('/user/me');
    userProfile.value = response.data;
  } catch (error) {
    console.error('Ошибка загрузки профиля', error);
  }
});
</script>

<style scoped lang="scss">
.profile-page {
  background: #f5f7f5;
  min-height: 100vh;
}

.page-header {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 50px 24px 80px;
  text-align: center;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  
  .q-icon {
    font-size: 40px;
    color: white;
  }
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

.profile-container {
  max-width: 800px;
  margin: -50px auto 0;
  padding: 0 24px 60px;
  position: relative;
  z-index: 2;
}

.profile-card {
  background: white;
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.profile-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .q-icon {
    font-size: 36px;
    color: white;
  }
}

.profile-info {
  flex: 1;
}

.profile-email {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px;
}

.role-chip {
  font-weight: 600;
}

.profile-details {
  padding-top: 24px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  
  &:not(:last-child) {
    border-bottom: 1px solid #f0f0f0;
  }
}

.detail-icon {
  width: 44px;
  height: 44px;
  background: #f5f7f5;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .q-icon {
    font-size: 22px;
    color: #666;
  }
}

.detail-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.detail-label {
  font-size: 0.8rem;
  color: #888;
  margin-bottom: 4px;
}

.detail-value {
  font-weight: 500;
  color: #333;
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 60px;
}

.profile-actions {
  margin-top: 32px;
}

.actions-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transform: translateX(4px);
  }
}

.action-icon {
  width: 50px;
  height: 50px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .q-icon {
    font-size: 24px;
    color: white;
  }
  
  &.green {
    background: linear-gradient(135deg, #4caf50, #2e7d32);
  }
  
  &.blue {
    background: linear-gradient(135deg, #42a5f5, #1976d2);
  }
  
  &.red {
    background: linear-gradient(135deg, #ef5350, #c62828);
  }
  
  &.purple {
    background: linear-gradient(135deg, #ab47bc, #7b1fa2);
  }

  &.orange {
    background: linear-gradient(135deg, #ffb74d, #f57c00);
  }

  &.amber {
    background: linear-gradient(135deg, #ffca28, #ff8f00);
  }
}

.action-content {
  flex: 1;
  
  h4 {
    font-size: 1rem;
    font-weight: 600;
    color: #1a1a1a;
    margin: 0 0 4px;
  }
  
  p {
    font-size: 0.85rem;
    color: #888;
    margin: 0;
  }
}

.action-arrow {
  color: #ccc;
  font-size: 24px;
}
</style>