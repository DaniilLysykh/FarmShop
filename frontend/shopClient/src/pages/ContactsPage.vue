<template>
  <InfoPageLayout
    title="Контакты"
    subtitle="Свяжитесь с нами по любым вопросам"
    icon="contact_mail"
  >
    <div class="info-row">
      <q-icon name="email" />
      <div>
        <div class="info-row__label">Электронная почта</div>
        <div class="info-row__value">
          <a href="mailto:support@farmshop.ru">support@farmshop.ru</a>
        </div>
      </div>
    </div>

    <div class="info-row">
      <q-icon name="phone" />
      <div>
        <div class="info-row__label">Телефон</div>
        <div class="info-row__value">
          <a href="tel:+78001234567">8 (800) 123-45-67</a>
        </div>
      </div>
    </div>

    <div class="info-row">
      <q-icon name="schedule" />
      <div>
        <div class="info-row__label">Время работы поддержки</div>
        <div class="info-row__value">Пн–Пт: 9:00–18:00, Сб–Вс: 10:00–16:00</div>
      </div>
    </div>

    <div class="info-row">
      <q-icon name="location_on" />
      <div>
        <div class="info-row__label">Офис</div>
        <div class="info-row__value">г. Москва, ул. Сельскохозяйственная, д. 12</div>
      </div>
    </div>

    <h2>Обратная связь</h2>
    <p>
      По вопросам заказов, регистрации фермеров и работы сайта пишите на
      <a href="mailto:support@farmshop.ru">support@farmshop.ru</a>.
      Мы отвечаем в течение одного рабочего дня.
    </p>
  </InfoPageLayout>
</template>

<script setup>
import InfoPageLayout from 'components/InfoPageLayout.vue';
</script>
