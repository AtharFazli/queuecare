// Memperbarui status dan posisi antrean tiap 10 detik tanpa muat ulang halaman (R8).
// Berhenti saat tab tersembunyi atau status antrean sudah final.
(function () {
  var root = document.querySelector('[data-antrean-url]');
  if (!root) {
    return;
  }
  var url = root.getAttribute('data-antrean-url');
  var statusEl = document.getElementById('antrean-status');
  var posisiEl = document.getElementById('antrean-posisi');
  var keteranganEl = document.getElementById('antrean-keterangan');
  var muatEl = document.getElementById('antrean-muat');
  var STATUS_FINAL = ['COMPLETED', 'CANCELLED', 'NO_SHOW'];
  var timer = null;

  function berhenti() {
    if (timer !== null) {
      clearInterval(timer);
      timer = null;
    }
  }

  function muat() {
    if (document.hidden) {
      return;
    }
    fetch(url)
      .then(function (respon) { return respon.json(); })
      .then(function (data) {
        statusEl.textContent = data.status;
        statusEl.className = 'lencana lencana--' + data.status.toLowerCase().replace(/_/g, '-');
        posisiEl.textContent = data.posisi;
        keteranganEl.textContent = data.keterangan;
        if (STATUS_FINAL.indexOf(data.status) >= 0) {
          berhenti();
          if (muatEl) {
            muatEl.textContent = 'Status antrean sudah final.';
          }
        }
      })
      .catch(function () {
        // Abaikan, dicoba lagi pada putaran berikutnya.
      });
  }

  timer = setInterval(muat, 10000);
  document.addEventListener('visibilitychange', muat);
})();
