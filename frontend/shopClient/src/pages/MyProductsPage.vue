<template>
  <q-page class="products-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="page-title">Мои товары</h1>
          <p class="page-subtitle" v-if="products.length > 0">{{ products.length }} товаров</p>
        </div>
        <q-btn unelevated class="add-btn" @click="showAddDialog = true">
          <q-icon name="add" />
          Добавить товар
        </q-btn>
      </div>
    </div>

    <div class="products-container">
      <div v-if="products.length === 0" class="empty-state">
        <div class="empty-icon">
          <q-icon name="inventory_2" />
        </div>
        <h2 class="empty-title">Товаров пока нет</h2>
        <p class="empty-text">Добавьте первый товар</p>
        <q-btn unelevated class="empty-btn" @click="showAddDialog = true">
          <q-icon name="add" class="q-mr-sm" />
          Добавить товар
        </q-btn>
      </div>

      <div v-else class="products-grid">
        <div class="product-card" v-for="product in products" :key="product.id">
          <div class="card-image-wrapper">
            <q-img 
              :src="product.imageUrl ? `http://26.151.165.100:8080${product.imageUrl}` : 'https://via.placeholder.com/400x300?text=Нет+фото'" 
              class="card-image"
            />
            <q-chip size="sm" class="category-chip">{{ getCategoryLabel(product.category) }}</q-chip>
            <div class="card-overlay-actions">
              <q-btn flat round icon="edit" color="white" class="action-btn edit-btn" @click="openEditDialog(product)">
                <q-tooltip>Редактировать</q-tooltip>
              </q-btn>
              <q-btn flat round icon="delete" color="white" class="action-btn delete-btn" @click="confirmDelete(product)">
                <q-tooltip>Удалить</q-tooltip>
              </q-btn>
            </div>
          </div>
          
          <div class="card-content">
            <h3 class="card-title">{{ product.name }}</h3>
            <p class="card-description">{{ product.description }}</p>
            
            <div class="card-meta">
              <div class="meta-item">
                <q-icon name="sell" />
                <span>{{ product.price }} руб.</span>
              </div>
              <div class="meta-item">
                <q-icon name="inventory" />
                <span>Остаток: {{ product.stock }} {{ product.unit }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <q-dialog v-model="showAddDialog" persistent>
      <div class="dialog-card">
        <div class="dialog-header">
          <h2 class="dialog-title">Новый товар</h2>
          <q-btn round flat icon="close" v-close-popup />
        </div>

        <q-form @submit.prevent="saveProduct" class="dialog-form">
          <div class="form-group">
            <label>Название</label>
            <q-input v-model="form.name" outlined rounded placeholder="Молоко свежее" required />
          </div>
          
          <div class="form-group">
            <label>Описание</label>
            <q-input v-model="form.description" outlined rounded type="textarea" placeholder="Опишите товар..." />
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label>Цена (руб.)</label>
              <q-input v-model.number="form.price" outlined rounded type="number" placeholder="100" required />
            </div>
            <div class="form-group half">
              <label>Ед. измерения</label>
              <q-input v-model="form.unit" outlined rounded placeholder="кг, л, шт" required />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label>Остаток</label>
              <q-input v-model.number="form.stock" outlined rounded type="number" placeholder="10" required />
            </div>
            <div class="form-group half">
            <label>Категория</label>
              <q-select v-model="form.category" :options="categories" emit-value map-options outlined rounded placeholder="Выберите" required />
            </div>
          </div>

          <div class="form-group">
            <label>Фото товара</label>
            <q-file v-model="imageFile" outlined rounded accept=".jpg,image/*" class="file-input">
              <template v-slot:prepend>
                <q-icon name="attach_file" />
              </template>
            </q-file>
          </div>

          <div class="dialog-actions">
            <q-btn flat label="Отмена" color="grey" v-close-popup />
            <q-btn unelevated type="submit" label="Сохранить" class="save-btn" :loading="saving" />
          </div>
        </q-form>
      </div>
    </q-dialog>

    <q-dialog v-model="showEditDialog" persistent>
      <div class="dialog-card">
        <div class="dialog-header">
          <h2 class="dialog-title">Редактирование товара</h2>
          <q-btn round flat icon="close" v-close-popup />
        </div>

        <q-form @submit.prevent="updateProduct" class="dialog-form">
          <div class="form-group">
            <label>Название</label>
            <q-input v-model="editingProduct.name" outlined rounded placeholder="Молоко свежее" required />
          </div>
          
          <div class="form-group">
            <label>Описание</label>
            <q-input v-model="editingProduct.description" outlined rounded type="textarea" placeholder="Опишите товар..." />
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label>Цена (руб.)</label>
              <q-input v-model.number="editingProduct.price" outlined rounded type="number" placeholder="100" required />
            </div>
            <div class="form-group half">
              <label>Ед. измерения</label>
              <q-input v-model="editingProduct.unit" outlined rounded placeholder="кг, л, шт" required />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label>Остаток</label>
              <q-input v-model.number="editingProduct.stock" outlined rounded type="number" placeholder="10" required />
            </div>
            <div class="form-group half">
            <label>Категория</label>
              <q-select v-model="editingProduct.category" :options="categories" emit-value map-options outlined rounded placeholder="Выберите" required />
            </div>
          </div>

          <div class="form-group">
            <label>Фото товара</label>
            <q-file v-model="imageFile" outlined rounded accept=".jpg,image/*" class="file-input">
              <template v-slot:prepend>
                <q-icon name="attach_file" />
              </template>
            </q-file>
          </div>

          <div class="dialog-actions">
            <q-btn flat label="Отмена" color="grey" v-close-popup />
            <q-btn unelevated type="submit" label="Сохранить" class="save-btn" :loading="saving" />
          </div>
        </q-form>
      </div>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';
import { useCategoryLabel } from 'src/composables/useCategoryLabel';

const { getCategoryLabel } = useCategoryLabel();

const $q = useQuasar();
const products = ref([]);
const showAddDialog = ref(false);
const showEditDialog = ref(false);
const editingProduct = ref(null);
const saving = ref(false);

const categories = [
  { label: 'Молочные продукты', value: 'MILK' },
  { label: 'Мясо', value: 'MEAT' },
  { label: 'Овощи', value: 'VEGETABLES' },
  { label: 'Мёд', value: 'HONEY' },
  { label: 'Яйца', value: 'EGGS' },
  { label: 'Хлебобулочные изделия', value: 'BREAD' }
];

const form = reactive({
  name: '',
  description: '',
  price: null,
  unit: '',
  stock: null,
  category: ''
});

const imageFile = ref(null);

const loadProducts = async () => {
  try {
    const response = await api.get('/products/my');
    products.value = response.data;
  } catch (error) {
    console.error('Ошибка при загрузке товаров', error);
  }
};

const openEditDialog = (product) => {
  editingProduct.value = { ...product };
  showEditDialog.value = true;
};

const updateProduct = async () => {
  saving.value = true;
  try {
    await api.put(`/products/${editingProduct.value.id}`, {
      name: editingProduct.value.name,
      description: editingProduct.value.description,
      price: editingProduct.value.price,
      unit: editingProduct.value.unit,
      stock: editingProduct.value.stock,
      category: editingProduct.value.category
    });

    if (imageFile.value) {
      const formData = new FormData();
      formData.append('file', imageFile.value);
      await api.post(`/products/${editingProduct.value.id}/image`, formData);
    }

    $q.notify({ type: 'positive', message: 'Товар обновлен!' });
    showEditDialog.value = false;
    imageFile.value = null;
    loadProducts();
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Ошибка обновления товара' });
    console.error(error);
  } finally {
    saving.value = false;
  }
};

const confirmDelete = (product) => {
  $q.dialog({
    title: 'Удаление товара',
    message: `Вы уверены, что хотите удалить "${product.name}"?`,
    persistent: true,
    ok: { label: 'Удалить', color: 'negative', unelevated: true },
    cancel: { label: 'Отмена', flat: true, color: 'grey' }
  }).onOk(async () => {
    try {
      await api.delete(`/products/${product.id}`);
      $q.notify({ type: 'positive', message: 'Товар удален' });
      loadProducts();
    } catch {
      $q.notify({ type: 'negative', message: 'Ошибка удаления товара' });
    }
  });
};

const saveProduct = async () => {
  saving.value = true;
  try {
    const response = await api.post('/products', form);
    const productId = response.data.id;

    if (imageFile.value) {
      const formData = new FormData();
      formData.append('file', imageFile.value);
      await api.post(`/products/${productId}/image`, formData);
    }

    $q.notify({ type: 'positive', message: 'Товар успешно добавлен!' });
    showAddDialog.value = false;
    Object.keys(form).forEach(key => form[key] = '');
    imageFile.value = null;
    loadProducts();
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Ошибка при сохранении товара' });
    console.error(error);
  } finally {
    saving.value = false;
  }
};

onMounted(() => {
  loadProducts();
});
</script>

<style scoped lang="scss">
.products-page {
  background: #f5f7f5;
  min-height: 100vh;
}

.page-header {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 40px 24px;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.add-btn {
  background: white;
  color: #2e7d32;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 600;
  
  .q-icon {
    margin-right: 8px;
  }
}

.products-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 24px 60px;
}

.empty-state {
  background: white;
  border-radius: 24px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  width: 120px;
  height: 120px;
  background: #f0f0f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  
  .q-icon {
    font-size: 60px;
    color: #ccc;
  }
}

.empty-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
}

.empty-text {
  color: #888;
  margin: 0 0 24px;
}

.empty-btn {
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  color: white;
  padding: 14px 32px;
  border-radius: 12px;
  font-weight: 600;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.product-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
    
    .card-overlay-actions {
      opacity: 1;
    }
  }
}

.card-image-wrapper {
  position: relative;
  height: 200px;
}

.card-image {
  width: 100%;
  height: 100%;
}

.category-chip {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(46, 125, 50, 0.9);
  color: white;
  font-weight: 600;
}

.card-overlay-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
  
  .action-btn {
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(4px);
    
    &:hover {
      background: rgba(0, 0, 0, 0.7);
    }
  }
  
  .edit-btn:hover {
    color: #2196f3 !important;
  }
  
  .delete-btn:hover {
    color: #f44336 !important;
  }
}

.card-content {
  padding: 20px;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px;
}

.card-description {
  font-size: 0.875rem;
  color: #666;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #888;
  font-size: 0.875rem;
  
  .q-icon {
    color: #2e7d32;
  }
}

.dialog-card {
  background: white;
  border-radius: 24px;
  width: 500px;
  max-width: 90vw;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dialog-form {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #eee;
}

.dialog-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.form-group {
  margin-bottom: 20px;
  
  label {
    display: block;
    font-weight: 500;
    color: #333;
    margin-bottom: 8px;
    font-size: 0.9rem;
  }
  
  :deep(.q-field__control) {
    border-radius: 12px;
  }
}

.form-row {
  display: flex;
  gap: 16px;
}

.half {
  flex: 1;
}

.file-input {
  :deep(.q-field__control) {
    height: 48px;
  }
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.save-btn {
  background: linear-gradient(135deg, #4caf50, #2e7d32);
  color: white;
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 600;
}
</style>