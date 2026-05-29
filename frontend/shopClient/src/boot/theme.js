import { boot } from 'quasar/wrappers';
import { Dark } from 'quasar';

export default boot(() => {
  const saved = localStorage.getItem('farmshop-theme');
  Dark.set(saved === 'dark');
});
