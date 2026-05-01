<template>
  <q-page padding>
    <div class="row justify-between items-center q-mb-md">
      <div class="text-h4">Мои товары</div>
      <q-btn color="green-8" icon="add" label="Добавить товар" @click="showAddDialog = true" />
    </div>

    <div class="row q-col-gutter-md">
      <div class="col-12 col-sm-6 col-md-4" v-for="product in products" :key="product.id">
        <q-card>
          <q-img :src="product.imageUrl ? `http://localhost:8080${product.imageUrl}` : 'https://via.placeholder.com/300x200?text=Нет+фото'" height="200px" />
          
          <q-card-section>
            <div class="text-h6">{{ product.name }}</div>
            <div class="text-subtitle2 text-green-8 text-weight-bold">{{ product.price }} руб. / {{ product.unit }}</div>
            <div class="text-caption text-grey">Остаток: {{ product.stock }} {{ product.unit }}</div>
            <q-chip size="sm" color="orange" text-color="white" class="q-mt-sm">{{ product.category }}</q-chip>
          </q-card-section>
        </q-card>
      </div>
    </div>

    <q-dialog v-model="showAddDialog">
      <q-card style="min-width: 400px">
        <q-card-section>
          <div class="text-h6">Новый товар</div>
        </q-card-section>

        <q-card-section>
          <q-form @submit.prevent="saveProduct">
            <q-input v-model="form.name" label="Название" outlined class="q-mb-sm" required />
            <q-input v-model="form.description" label="Описание" type="textarea" outlined class="q-mb-sm" />
            
            <div class="row q-col-gutter-sm q-mb-sm">
              <div class="col-6">
                <q-input v-model="form.price" label="Цена" type="number" outlined required />
              </div>
              <div class="col-6">
                <q-input v-model="form.unit" label="Ед. измерения (кг, л)" outlined required />
              </div>
            </div>

            <div class="row q-col-gutter-sm q-mb-sm">
              <div class="col-6">
                <q-input v-model="form.stock" label="Остаток" type="number" outlined required />
              </div>
              <div class="col-6">
                <q-select v-model="form.category" :options="categories" label="Категория" outlined required />
              </div>
            </div>

            <q-file v-model="imageFile" label="Выберите фото" outlined class="q-mb-md" accept=".jpg, image/*">
              <template v-slot:prepend>
                <q-icon name="attach_file" />
              </template>
            </q-file>

            <q-card-actions align="right">
              <q-btn flat label="Отмена" color="red" v-close-popup />
              <q-btn label="Сохранить" type="submit" color="green-8" />
            </q-card-actions>
          </q-form>
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { api } from 'boot/axios';
import { useQuasar } from 'quasar';

const $q = useQuasar();
const products = ref([]);
const showAddDialog = ref(false);

const categories = ['MILK', 'MEAT', 'VEGETABLES', 'HONEY', 'EGGS', 'BREAD'];

const form = reactive({
  name: '',
  description: '',
  price: null,
  unit: '',
  stock: null,
  category: ''
});

// Отдельная переменная для файла картинки
const imageFile = ref(null);

// Загрузка товаров фермера
const loadProducts = async () => {
  try {
    const response = await api.get('/products/my');
    products.value = response.data;
  } catch (error) {
    console.error('Ошибка при загрузке товаров', error);
  }
};

// Сохранение нового товара и картинки
const saveProduct = async () => {
  try {
    // 1. Сначала отправляем JSON с данными товара
    const response = await api.post('/products', form);
    const productId = response.data.id;

    // 2. Если пользователь выбрал файл, отправляем его отдельным запросом
    if (imageFile.value) {
      const formData = new FormData();
      formData.append('file', imageFile.value);
      
      // Axios сам выставит нужные заголовки multipart/form-data
      await api.post(`/products/${productId}/image`, formData);
    }

    $q.notify({ type: 'positive', message: 'Товар успешно добавлен!' });
    showAddDialog.value = false;
    
    // Сбрасываем форму
    Object.keys(form).forEach(key => form[key] = '');
    imageFile.value = null;
    
    // Обновляем список товаров
    loadProducts();
  } catch (error) {
    $q.notify({ type: 'negative', message: 'Ошибка при сохранении товара' });
    console.error(error);
  }
};

onMounted(() => {
  loadProducts();
});
</script>