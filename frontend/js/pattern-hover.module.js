export function initPatternOverlay() {
  const cards = document.querySelectorAll('.pattern-card');

  cards.forEach(card => {
    const overlay = card.querySelector('.overlay');
    if (!overlay) return;

    card.addEventListener('mouseenter', () => {
      overlay.style.opacity = '1';
    });

    card.addEventListener('mouseleave', () => {
      overlay.style.opacity = '0';
    });
  });
}
