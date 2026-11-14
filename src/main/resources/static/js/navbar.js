// Tombol menu HP di navbar. Tanpa library, cukup toggle class.
document.addEventListener('DOMContentLoaded', function () {
  var tombol = document.querySelector('.navbar__tombol-menu');
  var menu = document.getElementById('menu-utama');
  if (!tombol || !menu) {
    return;
  }
  tombol.addEventListener('click', function () {
    var terbuka = menu.classList.toggle('navbar__menu--terbuka');
    tombol.setAttribute('aria-expanded', terbuka ? 'true' : 'false');
  });
});
