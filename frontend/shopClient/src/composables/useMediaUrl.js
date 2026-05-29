import { api } from 'boot/axios';

export function useMediaUrl() {
  const serverBase = (api.defaults.baseURL || '').replace(/\/api\/?$/, '');

  const mediaUrl = (path) => {
    if (!path) return null;
    if (path.startsWith('http')) return path;
    return `${serverBase}${path}`;
  };

  return { serverBase, mediaUrl };
}
