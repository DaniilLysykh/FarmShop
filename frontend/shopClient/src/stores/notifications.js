import { defineStore } from 'pinia';
import { ref } from 'vue';
import { api } from 'boot/axios';

export const useNotificationsStore = defineStore('notifications', () => {
  const unreadCount = ref(0);
  const items = ref([]);

  async function fetchUnreadCount() {
    try {
      const response = await api.get('/notifications/unread-count');
      unreadCount.value = response.data.count;
    } catch {
      unreadCount.value = 0;
    }
  }

  async function fetchNotifications() {
    const response = await api.get('/notifications');
    items.value = response.data;
    await fetchUnreadCount();
    return items.value;
  }

  async function markAsRead(id) {
    await api.put(`/notifications/${id}/read`);
    const item = items.value.find((n) => n.id === id);
    if (item && !item.read) {
      item.read = true;
      unreadCount.value = Math.max(0, unreadCount.value - 1);
    }
  }

  async function markAllAsRead() {
    await api.put('/notifications/read-all');
    items.value.forEach((n) => {
      n.read = true;
    });
    unreadCount.value = 0;
  }

  function reset() {
    unreadCount.value = 0;
    items.value = [];
  }

  return {
    unreadCount,
    items,
    fetchUnreadCount,
    fetchNotifications,
    markAsRead,
    markAllAsRead,
    reset,
  };
});
