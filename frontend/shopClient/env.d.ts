/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  readonly VITE_PLACEHOLDER_IMAGE_URL: string;
  readonly VITE_PLACEHOLDER_IMAGE_SMALL_URL: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
