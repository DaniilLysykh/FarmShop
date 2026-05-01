import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useAuthStore = defineStore('auth', () => {
  // --- Состояние (State) ---
  const token = ref(localStorage.getItem('token') || null);
  const userRoles = ref(JSON.parse(localStorage.getItem('roles') || '[]'));
  const userEmail = ref(localStorage.getItem('email') || null);

  // --- Вычисляемые свойства (Getters) ---
  const isLoggedIn = computed(() => token.value !== null);
  const isFarmer = computed(() => userRoles.value.includes('ROLE_FARMER'));
  const isCustomer = computed(() => userRoles.value.includes('ROLE_CUSTOMER'));

  // --- Действия (Actions) ---
  function setAuth(newToken, roles, email) {
    token.value = newToken;
    userRoles.value = roles;
    userEmail.value = email;

    // Сохраняем в localStorage
    localStorage.setItem('token', newToken);
    localStorage.setItem('roles', JSON.stringify(roles));
    localStorage.setItem('email', email);
  }

  function logout() {
    token.value = null;
    userRoles.value = [];
    userEmail.value = null;
    localStorage.removeItem('token');
    localStorage.removeItem('roles');
    localStorage.removeItem('email');
  }

  return {
    token,
    userRoles,
    userEmail,
    isLoggedIn,
    isFarmer,
    isCustomer,
    setAuth,
    logout,
  };
});