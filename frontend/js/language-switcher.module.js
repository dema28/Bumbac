export function initLanguageSwitcher() {
  const buttons = document.querySelectorAll('[data-lang]');
  const currentLang = localStorage.getItem('lang') || 'en';

  setLanguage(currentLang);

  buttons.forEach(button => {
    button.addEventListener('click', () => {
      const selectedLang = button.getAttribute('data-lang');
      if (!selectedLang) return;

      localStorage.setItem('lang', selectedLang);
      setLanguage(selectedLang);
    });
  });

  function setLanguage(lang) {
    document.documentElement.lang = lang;
    console.log(`🌐 Language set to: ${lang}`);
  }
}
