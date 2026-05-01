<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-green-8 text-white">
      <q-toolbar>
        <q-btn flat no-caps to="/" class="text-h6">
          <q-icon name="eco" class="q-mr-sm" />
          Farm Marketplace
        </q-btn>

        <q-space /> <q-btn flat label="Каталог" to="/catalog" />

        <template v-if="authStore.isLoggedIn">
          <q-btn v-if="authStore.isCustomer" flat icon="shopping_cart" to="/cart" />
          <q-btn flat icon="person" to="/profile" />
          <q-btn flat label="Выйти" @click="logout" />
        </template>

        <template v-else>
          <q-btn flat label="Войти" to="/login" />
          <q-btn flat label="Регистрация" to="/register" />
        </template>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { useAuthStore } from 'stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const logout = () => {
  authStore.logout();
  router.push('/login'); // Перекидываем на страницу логина после выхода
};
</script>