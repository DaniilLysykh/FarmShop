<template>
  <q-page padding class="bg-grey-2">
    <div class="row justify-center">
      <div class="col-12 col-md-8 col-lg-6">
        <q-card class="q-pa-md">
          <q-card-section>
            <div class="text-h5 text-weight-bold">Мой профиль</div>
          </q-card-section>

          <q-card-section v-if="userProfile">
            <div class="text-subtitle1"><strong>Email:</strong> {{ userProfile.email }}</div>
            <div class="text-subtitle1">
              <strong>Роль:</strong> 
              <q-chip 
                :color="isFarmer ? 'green' : 'blue'" 
                text-color="white" 
                class="q-ml-sm"
              >
                {{ isFarmer ? 'Фермер' : 'Покупатель' }}
              </q-chip>
            </div>
            <div class="text-subtitle1"><strong>Дата регистрации:</strong> {{ formatDate(userProfile.createdAt) }}</div>
          </q-card-section>

          <q-card-section v-else class="text-center">
            <q-spinner color="green" size="3em" />
          </q-card-section>

          <q-separator />

          <q-card-actions align="center" v-if="isFarmer" class="q-pt-md">
            <q-btn color="green-8" icon="inventory_2" label="Управление товарами" to="/my-products" />
          </q-card-actions>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { api } from 'boot/axios';
import { useAuthStore } from 'stores/auth';

const authStore = useAuthStore();
const userProfile = ref(null);

// Вычисляемое свойство для удобства
const isFarmer = computed(() => authStore.isFarmer);

// Загружаем данные при открытии страницы
onMounted(async () => {
  try {
    const response = await api.get('/user/me');
    userProfile.value = response.data;
  } catch (error) {
    console.error('Ошибка загрузки профиля', error);
  }
});

// Простая функция для форматирования даты
const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString('ru-RU', {
    day: '2-digit', month: 'long', year: 'numeric'
  });
};
</script>