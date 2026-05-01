import { boot } from 'quasar/wrappers';
import axios from 'axios';

const api = axios.create({ baseURL: 'http://26.151.165.100:8080/api' });

// Если токен уже есть в браузере (после перезагрузки), прикрепляем его
const token = localStorage.getItem('token');
if (token) {
  api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

export default boot(({ app }) => {
  app.config.globalProperties.$axios = axios;
  app.config.globalProperties.$api = api;
});

export { api };