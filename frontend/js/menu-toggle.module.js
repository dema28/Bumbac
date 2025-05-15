export function initMobileMenu() {
  const menuButton = document.querySelector('.ri-menu-line')?.parentElement;
  const navLinks = document.querySelector('.md\\:flex');
  if (!menuButton || !navLinks) return;

  // Переключение меню
  menuButton.addEventListener('click', () => {
    navLinks.classList.toggle('hidden');
    navLinks.classList.toggle('flex');
    navLinks.classList.toggle('flex-col');
    navLinks.classList.toggle('absolute');
    navLinks.classList.toggle('top-16');
    navLinks.classList.toggle('left-0');
    navLinks.classList.toggle('right-0');
    navLinks.classList.toggle('bg-white');
    navLinks.classList.toggle('p-6');
    navLinks.classList.toggle('shadow-md');
  });

  // При клике по ссылке — закрыть
  navLinks.querySelectorAll('a').forEach(link => {
    link.addEventListener('click', () => closeMenu());
  });

  // При клике вне меню — закрыть
  document.addEventListener('click', (e) => {
    if (!menuButton.contains(e.target) && !navLinks.contains(e.target) && navLinks.classList.contains('flex')) {
      closeMenu();
    }
  });

  // Закрытие меню
  function closeMenu() {
    navLinks.classList.add('hidden');
    navLinks.classList.remove(
      'flex',
      'flex-col',
      'absolute',
      'top-16',
      'left-0',
      'right-0',
      'bg-white',
      'p-6',
      'shadow-md'
    );
  }
}
export function initProfileDropdown() {
  const button = document.getElementById('profile-btn');
  const menu = document.getElementById('dropdown-menu');
  const container = document.getElementById('profile-dropdown');

  if (!button || !menu) return;

  button.addEventListener('click', (e) => {
    e.stopPropagation();
    menu.classList.toggle('hidden');
  });

  // Закрыть при клике вне
  document.addEventListener('click', (e) => {
    if (!container.contains(e.target)) {
      menu.classList.add('hidden');
    }
  });
}

