import { boot } from 'quasar/wrappers';
import axios from 'axios';
import { apiBaseUrl } from 'src/config/env';

const api = axios.create({ baseURL: apiBaseUrl });

const token = localStorage.getItem('token');
if (token) {
  api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

export default boot(({ app }) => {
  app.config.globalProperties.$axios = axios;
  app.config.globalProperties.$api = api;
});

export { api };
