<template>
  <q-page class="info-page">
    <div class="page-header">
      <div class="header-icon">
        <q-icon :name="icon" />
      </div>
      <h1 class="page-title">{{ title }}</h1>
      <p class="page-subtitle" v-if="subtitle">{{ subtitle }}</p>
    </div>

    <div class="info-container">
      <div class="info-card">
        <slot />
      </div>
    </div>
  </q-page>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  icon: { type: String, default: 'info' },
});
</script>

<style scoped lang="scss">
.info-page {
  background: #f5f7f5;
  min-height: 100vh;
}

.page-header {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 48px 24px 72px;
  text-align: center;
}

.header-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;

  .q-icon {
    font-size: 36px;
    color: white;
  }
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: white;
  margin: 0;
}

.page-subtitle {
  color: rgba(255, 255, 255, 0.85);
  margin: 10px 0 0;
  font-size: 1rem;
}

.info-container {
  max-width: 800px;
  margin: -40px auto 0;
  padding: 0 24px 60px;
  position: relative;
  z-index: 2;
}

.info-card {
  background: white;
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  line-height: 1.65;
  color: #444;

  :deep(h2) {
    font-size: 1.15rem;
    font-weight: 600;
    color: #1a1a1a;
    margin: 28px 0 12px;

    &:first-child {
      margin-top: 0;
    }
  }

  :deep(p) {
    margin: 0 0 12px;
  }

  :deep(ul),
  :deep(ol) {
    margin: 0 0 16px;
    padding-left: 20px;
  }

  :deep(li) {
    margin-bottom: 8px;
  }

  :deep(.info-row) {
    display: flex;
    align-items: flex-start;
    gap: 14px;
    padding: 14px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .q-icon {
      color: #2e7d32;
      font-size: 22px;
      margin-top: 2px;
    }
  }

  :deep(.info-row__label) {
    font-size: 0.8rem;
    color: #888;
    margin-bottom: 2px;
  }

  :deep(.info-row__value) {
    font-weight: 500;
    color: #333;
  }

  :deep(a) {
    color: #2e7d32;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
