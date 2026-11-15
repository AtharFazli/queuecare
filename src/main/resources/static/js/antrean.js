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

  function gambarSmartWait(data) {
    var kotak = document.getElementById('smartwait');
    if (!kotak) {
      return;
    }
    var menit = document.getElementById('smartwait-menit');
    var angka = document.getElementById('smartwait-teks-angka');
    var pesan = document.getElementById('smartwait-teks-pesan');
    var orang = document.getElementById('smartwait-orang');
    var catatan = document.getElementById('smartwait-catatan');
    var adaAngka = data.estimasi !== null && data.estimasi !== undefined;
    // Elemen angka dan pesan tidak selalu ada di halaman, jadi dijaga masing-masing.
    kotak.style.display = (adaAngka || data.pesan) ? '' : 'none';
    if (menit) {
      menit.textContent = adaAngka ? data.estimasi : '-';
    }
    if (angka) {
      angka.style.display = adaAngka ? '' : 'none';
    }
    if (pesan) {
      pesan.style.display = adaAngka ? 'none' : '';
      pesan.textContent = data.pesan || '';
    }
    if (orang) {
      orang.textContent = data.orangDepan;
    }
    if (catatan) {
      catatan.style.display = adaAngka ? '' : 'none';
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
        gambarSmartWait(data);
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
