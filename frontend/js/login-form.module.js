export function initLoginFormValidation() {
  const form = document.getElementById('login-form');
  const email = document.getElementById('email');
  const password = document.getElementById('password');
  const errorText = document.getElementById('login-error');

  form.addEventListener('submit', (e) => {
    const registeredUser = JSON.parse(localStorage.getItem('registeredUser'));

    if (
      !registeredUser ||
      registeredUser.email !== email.value ||
      registeredUser.password !== password.value
    ) {
      e.preventDefault();
      errorText.classList.remove('hidden');
      return;
    }

    errorText.classList.add('hidden');
    localStorage.setItem('isLoggedIn', 'true');

    // Перенаправим на главную
    window.location.href = '/index.html';
  });
}
