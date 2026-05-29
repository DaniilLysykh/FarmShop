const DEFAULT_API_BASE = 'http://localhost:8080/api';
const DEFAULT_PLACEHOLDER = 'https://via.placeholder.com/400x300?text=Нет+фото';
const DEFAULT_PLACEHOLDER_SMALL = 'https://via.placeholder.com/150?text=Нет+фото';

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || DEFAULT_API_BASE;

export const apiOrigin = apiBaseUrl.replace(/\/api\/?$/, '');

export const placeholderImageUrl =
  import.meta.env.VITE_PLACEHOLDER_IMAGE_URL || DEFAULT_PLACEHOLDER;

export const placeholderImageSmallUrl =
  import.meta.env.VITE_PLACEHOLDER_IMAGE_SMALL_URL || DEFAULT_PLACEHOLDER_SMALL;
