import { apiOrigin, placeholderImageUrl, placeholderImageSmallUrl } from 'src/config/env';

export function useMediaUrl() {
  const mediaUrl = (path) => {
    if (!path) return null;
    if (path.startsWith('http')) return path;
    return `${apiOrigin}${path}`;
  };

  const productImage = (path, size = 'large') => {
    const resolved = mediaUrl(path);
    if (resolved) return resolved;
    return size === 'small' ? placeholderImageSmallUrl : placeholderImageUrl;
  };

  return {
    apiOrigin,
    mediaUrl,
    productImage,
    placeholderImageUrl,
    placeholderImageSmallUrl,
  };
}
