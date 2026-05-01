<template>
  <q-page class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <div class="auth-icon">
            <q-icon name="person_add" />
          </div>
          <h1 class="auth-title">Регистрация</h1>
          <p class="auth-subtitle">Создайте аккаунт на Farm Market</p>
        </div>

        <q-form @submit.prevent="onSubmit" class="auth-form">
          <div class="form-group">
            <label class="form-label">Email</label>
            <q-input 
              v-model="form.email" 
              type="email" 
              placeholder="example@mail.ru"
              outlined
              rounded
              class="form-input"
              required 
            >
              <template v-slot:prepend>
                <q-icon name="email" color="grey-6" />
              </template>
            </q-input>
          </div>
          
          <div class="form-group">
            <label class="form-label">Пароль</label>
            <q-input 
              v-model="form.password" 
              :type="showPassword ? 'text' : 'password'" 
              placeholder="Минимум 6 символов"
              outlined
              rounded
              class="form-input"
              required 
            >
              <template v-slot:prepend>
                <q-icon name="lock" color="grey-6" />
              </template>
              <template v-slot:append>
                <q-icon 
                  :name="showPassword ? 'visibility_off' : 'visibility'" 
                  class="cursor-pointer"
                  color="grey-6"
                  @click="showPassword = !showPassword"
                />
              </template>
            </q-input>
          </div>

          <div class="form-group">
            <label class="form-label">Выберите роль</label>
            <q-option-group
              v-model="form.role"
              :options="roleOptions"
              color="green"
              class="role-group"
            />
          </div>

          <q-btn 
            type="submit" 
            unelevated
            class="submit-btn"
            size="lg"
            :loading="loading"
          >
            <q-icon name="how_to_reg" class="q-mr-sm" />
            Зарегистрироваться
          </q-btn>
        </q-form>

        <div class="auth-footer">
          <p class="footer-text">
            Уже есть аккаунт? 
            <router-link to="/login" class="footer-link">Войти</router-link>
          </p>
        </div>
      </div>

      <div class="auth-decoration">
        <div class="decoration-content">
          <q-icon name="storefront" class="decoration-icon" />
          <h2>Присоединяйтесь<br/>к нашему рынку</h2>
          <p>Фермеры продают напрямую покупателям</p>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { api } from 'boot/axios';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';

const $q = useQuasar();
const router = useRouter();
const loading = ref(false);
const showPassword = ref(false);

const form = reactive({
  email: '',
  password: '',
  role: 'CUSTOMER'
});

const roleOptions = [
  { label: 'Я Покупатель - хочу покупать свежие продукты', value: 'CUSTOMER' },
  { label: 'Я Фермер - хочу продавать свои товары', value: 'FARMER' }
];

const onSubmit = async () => {
  loading.value = true;
  try {
    await api.post('/auth/register', form);
    
    $q.notify({ type: 'positive', message: 'Регистрация прошла успешно! Теперь войдите.' });
    router.push('/login');
  } catch (error) {
    const errorMsg = error.response?.data?.error || 'Произошла ошибка при регистрации';
    $q.notify({ type: 'negative', message: errorMsg });
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped lang="scss">
.auth-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e8f5e9 0%, #f5f7f5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.auth-container {
  display: flex;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  max-width: 1000px;
  width: 100%;
}

.auth-card {
  flex: 1;
  padding: 50px 40px;
}

.auth-header {
  text-align: center;
  margin-bottom: 40px;
}

.auth-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  
  .q-icon {
    font-size: 36px;
    color: white;
  }
}

.auth-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 8px;
}

.auth-subtitle {
  color: #666;
  margin: 0;
  font-size: 0.95rem;
}

.auth-form {
  max-width: 360px;
  margin: 0 auto;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.form-input {
  :deep(.q-field__control) {
    border-radius: 12px;
    height: 52px;
    
    &:hover {
      box-shadow: 0 2px 8px rgba(46, 125, 50, 0.1);
    }
  }
  
  :deep(&.q-field--focused .q-field__control) {
    box-shadow: 0 4px 16px rgba(46, 125, 50, 0.2) !important;
  }
}

.role-group {
  :deep(.q-radio) {
    padding: 12px 16px;
    background: #f8f9fa;
    border-radius: 12px;
    margin-bottom: 8px;
    border: 1px solid #e0e0e0;
    transition: all 0.3s ease;
    
    &:hover {
      border-color: #4caf50;
      background: #f0f7f0;
    }
    
    &.q-radio--active {
      border-color: #2e7d32;
      background: #e8f5e9;
    }
  }
  
  :deep(.q-radio__label) {
    font-size: 0.9rem;
    color: #333;
  }
}

.submit-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  color: white;
  margin-top: 10px;
  box-shadow: 0 4px 20px rgba(46, 125, 50, 0.3);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 25px rgba(46, 125, 50, 0.4);
  }
}

.auth-footer {
  text-align: center;
  margin-top: 30px;
}

.footer-text {
  color: #666;
  font-size: 0.9rem;
}

.footer-link {
  color: #2e7d32;
  font-weight: 600;
  text-decoration: none;
  
  &:hover {
    text-decoration: underline;
  }
}

.auth-decoration {
  flex: 1;
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 50px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    top: -100px;
    right: -100px;
  }
  
  &::after {
    content: '';
    position: absolute;
    width: 200px;
    height: 200px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 50%;
    bottom: -50px;
    left: -50px;
  }
}

.decoration-content {
  text-align: center;
  color: white;
  position: relative;
  z-index: 1;
}

.decoration-icon {
  font-size: 80px;
  opacity: 0.9;
  margin-bottom: 20px;
}

.decoration-content h2 {
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0 0 16px;
  line-height: 1.3;
}

.decoration-content p {
  opacity: 0.85;
  font-size: 1rem;
  margin: 0;
}

@media (max-width: 768px) {
  .auth-decoration {
    display: none;
  }
  
  .auth-card {
    padding: 40px 30px;
  }
}
</style>