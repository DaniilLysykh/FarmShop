<template>
  <q-page class="flex flex-center bg-grey-2">
    <q-card style="width: 400px; padding: 20px;">
      <q-card-section>
        <div class="text-h5 text-center text-weight-bold">Вход в систему</div>
      </q-card-section>

      <q-card-section>
        <q-form @submit.prevent="onSubmit">
          <q-input 
            v-model="form.email" 
            label="Email" 
            type="email" 
            outlined 
            class="q-mb-md"
            required 
          />
          
          <q-input 
            v-model="form.password" 
            label="Пароль" 
            type="password" 
            outlined 
            class="q-mb-md"
            required 
          />

          <q-btn 
            label="Войти" 
            type="submit" 
            color="green-8" 
            class="full-width" 
            size="lg" 
          />
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { reactive } from 'vue';
import { api } from 'boot/axios';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';
import { useAuthStore } from 'stores/auth';

const $q = useQuasar();
const router = useRouter();
const authStore = useAuthStore();

const form = reactive({
  email: '',
  password: ''
});

const onSubmit = async () => {
  try {
    const response = await api.post('/auth/login', form);
    
    // Бэкенд вернул токен, email и массив ролей
    const { token, roles, email } = response.data;
    
    // Сохраняем это в Pinia
    authStore.setAuth(token, roles, email);
    
    // Добавляем токен ко всем будущим запросам Axios по умолчанию
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;

    $q.notify({ type: 'positive', message: 'Добро пожаловать!' });
    
    // Перекидываем в профиль
    router.push('/profile');
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Неверный email или пароль' });
  }
};
</script>