console.log('✅ main.module.js подключён');

// Импорт всех модулей один раз
import { initMobileMenu, initProfileDropdown } from './menu-toggle.module.js';
import { initPatternOverlay } from './pattern-hover.module.js';
import { initProductHover } from './product-hover.module.js';
import { initFormHandler } from './form-handler.module.js';
import { initSwiper } from './swiper-init.module.js';
import { initLanguageSwitcher } from './language-switcher.module.js';
import { updateProfileIcon } from './profile-icon.module.js';
import { initLoginFormValidation } from './login-form.module.js';
import { initRegisterFormValidation } from './register-form-debug.module.js';

window.addEventListener('DOMContentLoaded', () => {
  initMobileMenu();
  initProfileDropdown();
  initPatternOverlay();
  initProductHover();
  initFormHandler();
  initSwiper();
  initLanguageSwitcher();
  updateProfileIcon();
  initRegisterFormValidation();
  initLoginFormValidation();
});
