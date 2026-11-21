// Lipat/buka sidebar dashboard. Pilihan diingat di localStorage; tetap jalan bila penyimpanan diblokir.
// Bawaan: terbuka di desktop, dilipat di HP (<=720px). Tombol native button = keyboard otomatis.
(function () {
  var dash = document.querySelector('.dash');
  var tombol = document.querySelector('.sidebar__lipat');
  if (!dash || !tombol) {
    return;
  }
  var KUNCI = 'queuecare.sidebar.lipat';
  function baca() {
    try { return localStorage.getItem(KUNCI); } catch { return null; }
  }
  function simpan(nilai) {
    try { localStorage.setItem(KUNCI, nilai); } catch { /* penyimpanan diblokir: abaikan */ }
  }
  function terapkan(lipat) {
    dash.classList.toggle('dash--lipat', lipat);
    tombol.setAttribute('aria-expanded', lipat ? 'false' : 'true');
    tombol.setAttribute('aria-label', lipat ? 'Buka menu' : 'Lipat menu');
    tombol.setAttribute('title', lipat ? 'Buka menu' : 'Lipat menu');
  }
  var tersimpan = baca();
  var awal = tersimpan !== null ? tersimpan === '1' : window.matchMedia('(max-width: 720px)').matches;
  terapkan(awal);
  tombol.addEventListener('click', function () {
    var lipat = !dash.classList.contains('dash--lipat');
    terapkan(lipat);
    simpan(lipat ? '1' : '0');
  });
})();