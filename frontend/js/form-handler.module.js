export function initFormHandler() {
  const form = document.querySelector('form');
  if (!form) return;

  const emailInput = form.querySelector('input[type="email"]');
  if (!emailInput) return;

  form.addEventListener('submit', (e) => {
    e.preventDefault();

    const email = emailInput.value.trim();
    if (validateEmail(email)) {
      alert(`✅ Subscribed: ${email}`);
      form.reset();
    } else {
      alert('⚠ Please enter a valid email address.');
      emailInput.focus();
    }
  });

  function validateEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }
}
