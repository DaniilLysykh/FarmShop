<template>
  <q-btn flat round :icon="icon" class="theme-toggle" @click="onToggle">
    <q-tooltip>{{ label }}</q-tooltip>
  </q-btn>
</template>

<script setup>
import { computed } from 'vue';
import { useQuasar } from 'quasar';
import { useThemeStore } from 'stores/theme';

const $q = useQuasar();
const themeStore = useThemeStore();

const icon = computed(() => ($q.dark.isActive ? 'light_mode' : 'dark_mode'));
const label = computed(() => ($q.dark.isActive ? 'Светлая тема' : 'Тёмная тема'));

const onToggle = () => {
  themeStore.toggle();
};
</script>

<style scoped lang="scss">
.theme-toggle {
  color: white;
}
</style>
