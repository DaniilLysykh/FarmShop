const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/HomePage.vue') },
      { path: 'catalog', component: () => import('pages/CatalogPage.vue') },
      { path: 'cart', component: () => import('pages/CartPage.vue') },
      { path: 'profile', component: () => import('pages/ProfilePage.vue') },
      { path: 'login', component: () => import('pages/LoginPage.vue') },
      { path: 'register', component: () => import('pages/RegisterPage.vue') },
      { path: 'my-products', component: () => import('pages/MyProductsPage.vue') },
    ],
  },

  // Всегда оставляй этот роут последним, он отвечает за ошибку 404
  {
    path: '/:catchAll(.)',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
