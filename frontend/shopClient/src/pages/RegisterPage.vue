<template>
  <q-page class="flex flex-center bg-grey-2">
    <q-card style="width: 400px; padding: 20px;">
      <q-card-section>
        <div class="text-h5 text-center text-weight-bold">Регистрация</div>
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

          <q-select 
            v-model="form.role" 
            :options="roleOptions" 
            label="Кто вы?" 
            outlined 
            emit-value 
            map-options 
            class="q-mb-md"
            required 
          />

          <q-btn 
            label="Зарегистрироваться" 
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

const $q = useQuasar(); // Для всплывающих уведомлений
const router = useRouter();

const form = reactive({
  email: '',
  password: '',
  role: 'CUSTOMER'
});

const roleOptions = [
  { label: 'Я Покупатель', value: 'CUSTOMER' },
  { label: 'Я Фермер', value: 'FARMER' }
];

const onSubmit = async () => {
  try {
    // Отправляем POST запрос на наш Java бэкенд
    await api.post('/auth/register', form);
    
    $q.notify({ type: 'positive', message: 'Регистрация прошла успешно! Теперь войдите.' });
    router.push('/login');
  } catch (error) {
    // Если бэкенд вернул ошибку (например, email занят), показываем её
    const errorMsg = error.response?.data?.error || 'Произошла ошибка при регистрации';
    $q.notify({ type: 'negative', message: errorMsg });
  }
};
</script>