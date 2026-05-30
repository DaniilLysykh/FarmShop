<template>
  <q-page class="home-page">
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-badge">
          <q-icon name="eco" />
          Фермерский маркетплейс
        </div>
        <h1 class="hero-title">Свежие продукты<br/>от фермеров</h1>
        <p class="hero-subtitle">Покупайте напрямую у производителей без посредников. Без наценок, без химии, только натуральные продукты.</p>
        
        <div class="hero-actions">
          <q-btn unelevated to="/catalog" class="cta-btn primary">
            <q-icon name="store" class="q-mr-sm" />
            В каталог
          </q-btn>
          <q-btn outline to="/register" class="cta-btn secondary">
            <q-icon name="person_add" class="q-mr-sm" />
            Стать продавцом
          </q-btn>
        </div>
      </div>
      
      <div class="hero-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>
    </div>

    <div class="features-section">
      <div class="features-container">
        <div class="feature-card">
          <div class="feature-icon green">
            <q-icon name="local_shipping" />
          </div>
          <h3>Быстрая доставка</h3>
          <p>Доставляем свежие продукты в день заказа</p>
        </div>
        
        <div class="feature-card">
          <div class="feature-icon blue">
            <q-icon name="verified_user" />
          </div>
          <h3>Проверенные фермеры</h3>
          <p>Все продавцы проходят проверку качества</p>
        </div>
        
        <div class="feature-card">
          <div class="feature-icon orange">
            <q-icon name="eco" />
          </div>
          <h3>Экологически чисто</h3>
          <p>Без ГМО, пестицидов и химических добавок</p>
        </div>
      </div>
    </div>

    <div class="cta-section">
      <div class="cta-content">
        <h2>Присоединяйтесь к Фермерской лавке</h2>
        <p>Создайте свой магазин и начните продавать свежие продукты покупателям рядом</p>
        <q-btn unelevated to="/register" class="cta-btn primary">
          <q-icon name="storefront" class="q-mr-sm" />
          Начать продавать
        </q-btn>
      </div>
    </div>
  </q-page>
</template>

<script setup>
// HomePage component
</script>

<style scoped lang="scss">
.home-page {
  background: var(--bg-page);
  min-height: 100vh;
}

.hero-section {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 100px 24px 120px;
  position: relative;
  overflow: hidden;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
  position: relative;
  z-index: 2;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.15);
  color: white;
  padding: 10px 24px;
  border-radius: 30px;
  font-size: 0.9rem;
  font-weight: 500;
  margin-bottom: 24px;
  backdrop-filter: blur(10px);
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  color: white;
  margin: 0 0 24px;
  line-height: 1.2;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.hero-subtitle {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 40px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-btn {
  padding: 16px 32px;
  border-radius: 14px;
  font-size: 1rem;
  font-weight: 600;
  
  &.primary {
    background: white;
    color: #2e7d32;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
    }
  }
  
  &.secondary {
    color: white;
    border: 2px solid rgba(255, 255, 255, 0.5);
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
      border-color: white;
    }
  }
}

.hero-decoration {
  position: absolute;
  inset: 0;
  z-index: 1;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  
  &.circle-1 {
    width: 500px;
    height: 500px;
    top: -250px;
    right: -150px;
  }
  
  &.circle-2 {
    width: 400px;
    height: 400px;
    bottom: -200px;
    left: -100px;
  }
  
  &.circle-3 {
    width: 250px;
    height: 250px;
    top: 30%;
    left: 5%;
  }
}

.features-section {
  padding: 80px 24px;
}

.features-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 32px;
}

.feature-card {
  background: var(--bg-card);
  border-radius: 24px;
  padding: 40px;
  text-align: center;
  box-shadow: var(--shadow-card);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-8px);
    box-shadow: var(--shadow-hover);
  }
  
  h3 {
    font-size: 1.25rem;
    font-weight: 700;
    color: var(--text-primary);
    margin: 20px 0 12px;
  }
  
  p {
    color: var(--text-secondary);
    font-size: 0.95rem;
    margin: 0;
    line-height: 1.5;
  }
}

.feature-icon {
  width: 80px;
  height: 80px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  
  .q-icon {
    font-size: 40px;
    color: white;
  }
  
  &.green {
    background: linear-gradient(135deg, #4caf50, #2e7d32);
  }
  
  &.blue {
    background: linear-gradient(135deg, #42a5f5, #1976d2);
  }
  
  &.orange {
    background: linear-gradient(135deg, #ff9800, #f57c00);
  }
}

.cta-section {
  background: linear-gradient(135deg, #1b5e20 0%, #2e7d32 100%);
  padding: 80px 24px;
}

.cta-content {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
  color: white;
  
  h2 {
    font-size: 2rem;
    font-weight: 700;
    margin: 0 0 16px;
  }
  
  p {
    opacity: 0.9;
    font-size: 1.1rem;
    margin: 0 0 32px;
    line-height: 1.6;
  }
  
  .cta-btn.primary {
    background: white;
    color: #2e7d32;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
    }
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.25rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .cta-btn {
    width: 100%;
    max-width: 280px;
  }
}
</style>