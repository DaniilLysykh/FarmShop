import { defineStore } from 'pinia';
import { Dark } from 'quasar';

const STORAGE_KEY = 'farmshop-theme';

export const useThemeStore = defineStore('theme', () => {
  function persist() {
    localStorage.setItem(STORAGE_KEY, Dark.isActive ? 'dark' : 'light');
  }

  function applyTheme(dark) {
    Dark.set(dark);
    persist();
  }

  function toggle() {
    Dark.toggle();
    persist();
  }

  function init() {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved === 'dark') {
      Dark.set(true);
    } else if (saved === 'light') {
      Dark.set(false);
    }
  }

  return {
    applyTheme,
    toggle,
    init,
    setLight: () => applyTheme(false),
    setDark: () => applyTheme(true),
  };
});
