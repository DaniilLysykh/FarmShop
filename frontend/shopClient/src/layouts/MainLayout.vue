<template>
  <q-layout view="hHh lpR fFf">
    <q-header class="main-header">
      <q-toolbar class="toolbar-container">
        <router-link to="/" class="logo-link">
          <div class="logo">
            <q-icon name="eco" class="logo-icon" />
            <span class="logo-text">Фермерская лавка</span>
          </div>
        </router-link>

        <q-space />

        <nav class="nav-links">
          <q-btn flat no-caps to="/catalog" class="nav-btn">
            <q-icon name="grid_view" class="q-mr-xs" />
            Каталог
          </q-btn>
        </nav>

        <div class="header-actions">
          <template v-if="authStore.isLoggedIn">
            <q-btn v-if="authStore.isCustomer" flat round icon="shopping_cart" to="/cart" class="action-btn">
              <q-tooltip>Корзина</q-tooltip>
            </q-btn>
            
            <q-btn flat round icon="notifications" class="action-btn q-mr-sm" to="/notifications">
              <q-badge v-if="notificationsStore.unreadCount > 0" color="orange" floating rounded>
                {{ notificationsStore.unreadCount > 9 ? '9+' : notificationsStore.unreadCount }}
              </q-badge>
              <q-tooltip>Уведомления</q-tooltip>
            </q-btn>
            
            <q-btn flat round icon="person" to="/profile" class="action-btn">
              <q-tooltip>Профиль</q-tooltip>
            </q-btn>
            
            <q-btn unelevated color="white" text-color="green-8" label="Выйти" @click="logout" class="logout-btn q-ml-sm" />
          </template>

          <template v-else>
            <q-btn flat no-caps to="/login" class="auth-btn">
              <q-icon name="login" class="q-mr-xs" />
              Войти
            </q-btn>
            <q-btn unelevated no-caps to="/register" class="register-btn q-ml-sm">
              Регистрация
            </q-btn>
          </template>
        </div>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>

    <q-footer class="main-footer">
      <div class="footer-content">
        <div class="footer-brand">
          <q-icon name="eco" size="24px" class="q-mr-sm" />
          <span>Фермерская лавка 2026</span>
        </div>
        <div class="footer-links">
          <span class="footer-link">О нас</span>
          <span class="footer-link">Контакты</span>
          <span class="footer-link">Доставка</span>
        </div>
      </div>
    </q-footer>
  </q-layout>
</template>

<script setup>
import { onMounted, onUnmounted, watch } from 'vue';
import { useAuthStore } from 'stores/auth';
import { useNotificationsStore } from 'stores/notifications';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const notificationsStore = useNotificationsStore();
const router = useRouter();

let pollTimer = null;

const startPolling = () => {
  if (pollTimer) return;
  notificationsStore.fetchUnreadCount();
  pollTimer = setInterval(() => {
    notificationsStore.fetchUnreadCount();
  }, 60000);
};

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer);
    pollTimer = null;
  }
};

watch(
  () => authStore.isLoggedIn,
  (loggedIn) => {
    if (loggedIn) {
      startPolling();
    } else {
      stopPolling();
      notificationsStore.reset();
    }
  },
  { immediate: true }
);

onMounted(() => {
  if (authStore.isLoggedIn) startPolling();
});

onUnmounted(stopPolling);

const logout = () => {
  authStore.logout();
  notificationsStore.reset();
  router.push('/login');
};
</script>

<style scoped lang="scss">
.main-header {
  background: rgba(46, 125, 50, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
}

.toolbar-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  height: 70px;
}

.logo-link {
  text-decoration: none;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .logo-icon {
    font-size: 32px;
    color: #a5d6a7;
    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
  }
  
  .logo-text {
    font-size: 1.5rem;
    font-weight: 700;
    color: white;
    letter-spacing: -0.5px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-btn {
  color: white;
  font-weight: 500;
  padding: 8px 20px;
  border-radius: 10px;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.15);
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  color: white;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.15);
    transform: scale(1.05);
  }
}

.auth-btn {
  color: white;
  font-weight: 500;
  padding: 10px 20px;
  border-radius: 10px;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.15);
  }
}

.register-btn {
  background: white;
  color: #2e7d32;
  font-weight: 600;
  padding: 10px 24px;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  }
}

.logout-btn {
  font-weight: 600;
  padding: 8px 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:hover {
    transform: scale(1.02);
  }
}

.main-footer {
  background: linear-gradient(135deg, #1b5e20 0%, #2e7d32 100%);
  color: white;
  padding: 20px 24px;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-brand {
  display: flex;
  align-items: center;
  font-weight: 600;
  opacity: 0.9;
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-link {
  font-size: 0.875rem;
  opacity: 0.8;
  cursor: pointer;
  transition: opacity 0.3s ease;
  
  &:hover {
    opacity: 1;
  }
}
</style>