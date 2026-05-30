<template>
  <q-page class="notifications-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">Уведомления</h1>
        <p class="page-subtitle" v-if="notifications.length">
          {{ unreadCount }} непрочитанных
        </p>
      </div>
      <q-btn
        v-if="unreadCount > 0"
        flat
        color="white"
        label="Прочитать все"
        icon="done_all"
        class="mark-all-btn"
        @click="markAll"
        :loading="markingAll"
      />
    </div>

    <div class="notifications-container">
      <div v-if="loading" class="loading-state">
        <q-spinner color="green" size="3em" />
      </div>

      <div v-else-if="notifications.length === 0" class="empty-state">
        <q-icon name="notifications_none" class="empty-icon" />
        <h2>Уведомлений нет</h2>
        <p>Здесь появятся сообщения о заказах и отзывах</p>
      </div>

      <div v-else class="notifications-list">
        <div
          v-for="item in notifications"
          :key="item.id"
          class="notification-card"
          :class="{ unread: !item.read }"
          @click="openNotification(item)"
        >
          <div class="notification-icon" :class="typeClass(item.type)">
            <q-icon :name="typeIcon(item.type)" />
          </div>
          <div class="notification-body">
            <div class="notification-title">{{ item.title }}</div>
            <div class="notification-message">{{ item.message }}</div>
            <div class="notification-date">{{ formatDate(item.createdAt) }}</div>
          </div>
          <q-badge v-if="!item.read" color="orange" rounded />
        </div>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useNotificationsStore } from 'stores/notifications';
import { useAuthStore } from 'stores/auth';
import { useFormatDate } from 'src/composables/useFormatDate';

const router = useRouter();
const notificationsStore = useNotificationsStore();
const authStore = useAuthStore();
const { formatDate } = useFormatDate();

const loading = ref(true);
const markingAll = ref(false);

const notifications = computed(() => notificationsStore.items);
const unreadCount = computed(() => notificationsStore.unreadCount);

const typeIcon = (type) => {
  const map = {
    ORDER_NEW: 'shopping_bag',
    ORDER_STATUS: 'local_shipping',
    NEW_REVIEW: 'rate_review',
  };
  return map[type] || 'notifications';
};

const typeClass = (type) => {
  const map = {
    ORDER_NEW: 'green',
    ORDER_STATUS: 'blue',
    NEW_REVIEW: 'amber',
  };
  return map[type] || 'grey';
};

const load = async () => {
  loading.value = true;
  try {
    await notificationsStore.fetchNotifications();
  } finally {
    loading.value = false;
  }
};

const markAll = async () => {
  markingAll.value = true;
  try {
    await notificationsStore.markAllAsRead();
  } finally {
    markingAll.value = false;
  }
};

const openNotification = async (item) => {
  if (!item.read) {
    await notificationsStore.markAsRead(item.id);
  }
  if (item.type === 'ORDER_NEW' && authStore.isFarmer) {
    router.push('/farmer-orders');
  } else if (item.type === 'ORDER_STATUS' && authStore.isCustomer) {
    router.push('/my-orders');
  } else if (item.type === 'NEW_REVIEW' && authStore.isFarmer) {
    router.push('/my-products');
  }
};

onMounted(load);
</script>

<style scoped lang="scss">
.notifications-page {
  background: var(--bg-page);
  min-height: 100vh;
}

.page-header {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 40px 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-content {
  max-width: 900px;
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: white;
  margin: 0;
}

.page-subtitle {
  color: rgba(255, 255, 255, 0.8);
  margin: 8px 0 0;
}

.mark-all-btn {
  margin-top: 8px;
}

.notifications-container {
  max-width: 800px;
  margin: -30px auto 0;
  padding: 0 24px 60px;
  position: relative;
  z-index: 2;
}

.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  background: var(--bg-card);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.2s ease;

  &.unread {
    border-left: 4px solid #ff9800;
  }

  &:hover {
    transform: translateX(4px);
  }
}

.notification-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  .q-icon {
    color: white;
    font-size: 22px;
  }

  &.green {
    background: linear-gradient(135deg, #4caf50, #2e7d32);
  }
  &.blue {
    background: linear-gradient(135deg, #42a5f5, #1976d2);
  }
  &.amber {
    background: linear-gradient(135deg, #ffb74d, #f57c00);
  }
  &.grey {
    background: #9e9e9e;
  }
}

.notification-body {
  flex: 1;
}

.notification-title {
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.notification-message {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.4;
}

.notification-date {
  font-size: 0.75rem;
  color: #aaa;
  margin-top: 8px;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: var(--bg-card);
  border-radius: 20px;

  h2 {
    margin: 16px 0 8px;
    font-size: 1.25rem;
  }

  p {
    color: #888;
    margin: 0;
  }
}

.empty-icon {
  font-size: 64px;
  color: #ccc;
}
</style>
