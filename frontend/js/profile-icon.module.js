
export function updateProfileIcon() {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
  const icon = document.getElementById('profile-icon');

  if (isLoggedIn && icon) {
    icon.classList.remove('ri-user-line');
    icon.classList.add('ri-user-follow-line');
  }
}
