# B. Verifikasi perbaikan dialog dan token

Perubahan produksi dibatasi tiga baris: tambahan `dialog.modal:not([open]) { display: none; }`, penggantian dua literal hover dengan `var(--warna-bahaya-gelap)`, dan deklarasi `--warna-bahaya-gelap: #8F3022`. Warna hasil hover tetap sama. Aturan dialog.modal B10 tidak diubah.

Document mengikuti reference/document.md mode scan/merge: token dan varian hover ditambahkan ke frontmatter; riwayat body lama dipertahankan utuh dan diberi pembaruan di akhir; snippet sidecar menunjuk token. Tidak ada metafora merek atau pilihan visual baru. Semua heading lama tetap urut.

## Bukti browser

Tes sebelum patch gagal pada dialog `status-293`: display block dan satu client rect walaupun tanpa open.

Sesudah patch, `uji-dialog.py` menjalankan klik asli di dua template Admin/Owner yang mengandung dialog: `/admin/users` dan `/owner/queues`, pada 375px serta 1280px. Enam dialog Admin dan tujuh dialog Owner per tanggal fixture seluruhnya display none dan nol client rect saat tertutup. Pemicu membuka dialog, Kembali menutup, lalu konfirmasi menjalankan POST. Status akun dummy dikembalikan lewat konfirmasi browser; pembatalan Owner diverifikasi pada baris antrean yang sama. Hasil `dialog-setelah.json`. Tidak memakai evaluate showModal atau force click untuk mengganti klik pengguna.

Di server utama 8080 dilakukan pemeriksaan baca: dialog tersembunyi, buka/Kembali, urutan stylesheet dan warna hover pada kedua viewport. Hasil `dialog-utama-readonly.json`. Konfirmasi yang mengubah data hanya pada server salinan 8081.

## Regresi dan isolasi

`regresi8.sh` asli memakai database queuecare, DELETE massal, restart port 8080, scratch scripts, dan DROP database test. Karena data utama harus aman, driver aslinya tidak dijalankan langsung. Seluruh tujuh skrip yang dirujuk driver dijalankan berurutan dari salinan scratch, mengganti hanya port 8080 menjadi 8081, schema SQL menjadi queuecare_impeccable_b, dan lokasi scratch. Assertion dan fixture logic tidak diubah. Sebelum tiap skrip, salinan database dipulihkan dari dump lokal queuecare. Skrip asli dan data utama tidak diubah. Ini penyimpangan eksekusi yang disengaja, bukan klaim driver asli dijalankan tanpa adaptasi.

- F1 `qc_test.sh`: 14 ok, nol GAGAL.
- F3 `qc_fase3b.sh`: exit 0, katalog/filter/detail dan kontrol akses sesuai raw log; nol kebocoran bisnis/layanan nonaktif. Skrip ini bersifat observasi dan masih mencari placeholder lama “Fitur reservasi segera hadir”, sehingga tidak diklaim punya assertion otomatis lengkap.
- F4 `f4c.sh`: 40 OK, 0 GAGAL.
- F5 `f5c.sh`: 32 OK, 0 GAGAL.
- F6 `f6.sh`: 15 OK, 0 GAGAL.
- F7 `f7.sh`: 45 OK, 0 GAGAL.
- F7b `f7b.sh`: 57 OK, 0 GAGAL.
- Maven `-B package` menjalankan 19 tes, nol failure/error/skipped, BUILD SUCCESS.
- `pw_f8.py` dijalankan dari salinan scratch dengan port 8081 dan direktori output terpisah: 48 hasil OK (24 halaman × 2 viewport), nol OVERFLOW, exit 0. Skrip asli tidak memeriksa status HTTP atau keberhasilan pembuatan fixture, sehingga hasil ini dibatasi ke pemeriksaan overflow; fixture service ID 28 yang sudah tidak ada tidak dianggap teruji. Pemeriksaan browser tambahan memvalidasi halaman aktual dan dialog di luar harness lama.
- Detector CSS: exit 0, raw `detector-b.json` berisi array kosong.

Log rinci di `regresi-b/`. Dump SQL, cookie autentikasi, dan data rahasia tidak masuk Git. Dump database queuecare sebelum/sesudah identik memakai cmp.

Dua upaya runner awal gagal sebelum hasil yang sah: Python memilih WSL bash; lalu penggantian schema terlalu luas ikut mengganti domain email. Kedua penyebab diperbaiki di runner scratch, bukan aplikasi atau skrip asli. Run sah memakai Git Bash absolut dan penggantian schema SQL terarah. Tidak menyamarkan kegagalan sebagai PASS.

## Batas

Tidak ada perubahan template, JavaScript, kelas atau ID, urutan CSS, kode Java, maupun scripts/pengujian/. Cadangan DESIGN sebelum Impeccable tetap byte-identik di body baru. Snapshot persiapan lama tetap bukti historis, bukan hasil perbaikan terbaru.
