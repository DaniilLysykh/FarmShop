import { boot } from 'quasar/wrappers';
import axios from 'axios';

// Указываем базовый URL нашего Java-сервера
const api = axios.create({ baseURL: 'http://localhost:8080/api' });

export default boot(({ app }) => {
  // Позволяет использовать this.$axios и this.$api внутри Vue компонентов (Options API)
  app.config.globalProperties.$axios = axios;
  app.config.globalProperties.$api = api;
});

export { api };