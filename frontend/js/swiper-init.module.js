export function initSwiper() {
  if (typeof window.Swiper === 'undefined') {
    console.warn('⚠️ Swiper.js not loaded');
    return;
  }

  const swiperEl = document.querySelector('.swiper');
  if (!swiperEl) {
    console.warn('⚠️ .swiper element not found');
    return;
  }

  new Swiper(swiperEl, {
    loop: true,
    pagination: {
      el: '.swiper-pagination',
      clickable: true
    },
    navigation: {
      nextEl: '.swiper-button-next',
      prevEl: '.swiper-button-prev'
    }
  });
}
