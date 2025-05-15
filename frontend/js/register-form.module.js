export function initRegisterFormValidation() {
  const name = document.getElementById('name');
  const email = document.getElementById('email');
  const password = document.getElementById('password');
  const confirm = document.getElementById('confirm-password');
  const errorText = document.getElementById('password-error');
  const button = document.getElementById('register-btn');

  button.addEventListener('click', () => {
    if (!name || !email || !password || !confirm || !button) return;

    if (password.value !== confirm.value) {
      errorText.classList.remove('hidden');
      confirm.focus();
      return;
    }

    errorText.classList.add('hidden');

    // Временно сохраняем пользователя
    const user = {
      name: name.value,
      email: email.value,
      password: password.value
    };

    localStorage.setItem('registeredUser', JSON.stringify(user));
    localStorage.setItem('isRegistered', 'true');
    localStorage.setItem('isLoggedIn', 'false');

    // Переход на login.html
    window.location.href = 'login.html';
  });
}
