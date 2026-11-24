# Hasil Pengujian QueueCare

Tanggal: 15 November 2026. Aplikasi dijalankan di `http://localhost:8080`, MySQL 8.0.30 (Laragon), JDK 21, Maven 3.9.16.

Cara mengulang: lihat kolom "Cara menguji". Skrip ada di `scripts/pengujian/` (salinan dari scratch) dan nama file disebut di kolom rujukan.

Status: LOLOS, GAGAL, atau BELUM DIUJI. Tidak ada hasil yang dibuat-buat; yang tidak dijalankan ditulis BELUM DIUJI.

Status terakhir (diperbarui 24 Nov 2026): `mvn.cmd -B test` 35 tes lolos, 0 gagal; checklist C1–C33 seluruhnya LOLOS (C28 LOLOS sesudah perbaikan konkurensi dan uji ulang). Baris lama di tabel di bawah sengaja dipertahankan sebagai riwayat.

## Enam skenario pengujian

### S1 Autentikasi

| Langkah | Hasil yang diharapkan | Hasil nyata | Status | Rujukan |
|---|---|---|---|---|
| Buka `/login` | Halaman login tampil | HTTP 200, judul "Masuk" | LOLOS | `docs/screenshots/login-375.png`, `login-1280.png` |
| Daftar akun baru lewat `/register` | Akun Customer dibuat, diarahkan ke login | 302 ke `/login`, flash "Akun dibuat" | LOLOS | `scripts/pengujian/qc_test.sh` (14 ok) |
| Login dengan email tidak terdaftar | Pesan generik, tidak membocorkan existence | "Email atau password salah." | LOLOS | `f7b.sh` bagian 12 |
| Login dengan password salah | Pesan generik yang sama | Sama | LOLOS | `f7b.sh` bagian 12 |
| Login akun nonaktif | Ditolak dengan pesan khusus | Redirect `/login?disabled`, pesan "Akun dinonaktifkan. Hubungi Administrator." | LOLOS | `f7b.sh` bagian 7 |
| Sesi akun nonaktif di request berikutnya | Ditolak, sesi dibuang | `GET /customer` → redirect `/login?disabled` | LOLOS | `f7b.sh` bagian 7 |
| Logout | Kembali ke `/login?logout` | 302, pesan "Kamu sudah keluar." | LOLOS | `qc_test.sh` |
| Ubah profil (nama dan email) | Tersimpan, email harus unik | Nama dan email berubah di DB; email dipakai login | LOLOS | `f7b.sh` bagian 8 |
| Ubah email lalu login | Email baru bisa dipakai, email lama ditolang | 302 sukses; email lama ditolak | LOLOS | `f7b.sh` bagian 8 |
| Email duplikat saat ubah profil | Ditolak dengan pesan | "sudah dipakai pengguna lain" | LOLOS | `f7b.sh` bagian 8 |
| Ganti password: password lama salah | Ditolak | "Password lama tidak cocok", hash tidak berubah | LOLOS | `f7b.sh` bagian 9 |
| Ganti password: konfirmasi berbeda | Ditolak | "Konfirmasi password tidak sama" | LOLOS | `f7b.sh` bagian 9 |
| Ganti password: < 8 karakter | Ditolak | "minimal 8 karakter" | LOLOS | `f7b.sh` bagian 9 |
| Ganti password: sama dengan lama | Ditolak | "tidak boleh sama" | LOLOS | `f7b.sh` bagian 9 |
| Ganti password berhasil | Hash BCrypt berubah, login baru bisa, login lama ditolak, sesi tetap | Terverifikasi | LOLOS | `f7b.sh` bagian 9 |
| Profil untuk Owner dan Admin | Halaman profil tampil dengan peran benar | OWNER dan ADMIN tampil | LOLOS | `f7b.sh` bagian 10 |
| Halaman 403 untuk role yang salah | 403 | Customer dan Owner ke `/admin/users` → 403; Owner ke `/admin/*` → 403; Customer ke `/owner/*` → 403 | LOLOS | `f7b.sh` bagian 11, `f5c.sh` |

### S2 Kelola bisnis (Owner dan Admin)

| Langkah | Hasil yang diharapkan | Hasil nyata | Status | Rujukan |
|---|---|---|---|---|
| Owner membuka dashboard | Statistik bisnis aktif tampil | 4 angka tampil, cocok dengan SQL | LOLOS | `docs/screenshots/fase7-owner-1280.png` |
| Owner menambah bisnis | Bisnis baru tersimpan | Tersimpan, muncul di daftar | LOLOS | `qc_fase3b.sh` |
| Owner mengubah layanan, jadwal, kapasitas | Tersimpan dengan validasi | Harga negatif dan durasi 0 ditolak | LOLOS | `qc_fase3b.sh` |
| Owner memilih bisnis aktif | Pilihan disimpan dan dicek ulang | Beralih bisnis mengubah data yang tampil | LOLOS | `f4c.sh`, `f7.sh` |
| Owner lain membuka bisnis orang lain | 403 | 403 | LOLOS | `f4c.sh` |
| Katalog publik hanya bisnis ACTIVE | PENDING/INACTIVE tidak bocor | 0 bocor | LOLOS | `qc_fase3b.sh` |
| Layanan nonaktif tidak bocor | Tidak tampil di katalog | 0 bocor | LOLOS | `qc_fase3b.sh` |
| Admin mengelola kategori | Tambah dan hapus kategori | Kategori dipakai tidak bisa dihapus | LOLOS | `qc_test.sh` |
| Admin mengaktifkan dan menonaktifkan bisnis | Status berubah | Berubah, katalog ikut berubah | LOLOS | `qc_fase3b.sh` |
| Admin mengelola akun: tambah, ubah, nonaktif, aktif | Semua bekerja | Terverifikasi | LOLOS | `f7b.sh` bagian 4–7 |
| Pencarian dan filter pengguna | Menyaring sesuai nama/email/role | Terverifikasi | LOLOS | `f7b.sh` bagian 2 |
| Paginasi daftar pengguna (20 per halaman) | Halaman 1 dan 2 terbaca | Terverifikasi dengan 26 pengguna | LOLOS | `f7b.sh` bagian 3 |
| Admin tidak bisa menonaktifkan akun sendiri | Ditolak | "tidak bisa menonaktifkan akun sendiri", akun tetap aktif | LOLOS | `f7b.sh` bagian 6 |
| Admin tidak bisa mengubah role akun sendiri | Ditolak | "tidak bisa mengubah role akun sendiri", role tetap ADMIN | LOLOS | `f7b.sh` bagian 6 |
| Owner ber-bisnis tidak bisa diubah role | Ditolak | "masih memegang bisnis", role tetap OWNER | LOLOS | `f7b.sh` bagian 6 |
| Owner tanpa bisnis bisa diubah role | Diizinkan | Berubah jadi CUSTOMER | LOLOS | `f7b.sh` bagian 6 |
| Menonaktifkan Owner tidak menghapus bisnis | Bisnis tetap ada dan tampil | 3 bisnis tetap, "Barbershop Senja" tampil di katalog | LOLOS | `f7b.sh` bagian 7 |
| Isian form pengguna tetap saat validasi gagal | Nama, email, dan role tetap | Terverifikasi | LOLOS | `docs/pengujian.md` C16, `docs/screenshots/fase8-form-pengguna-1280.png` |

### S3 Reservasi

| Langkah | Hasil yang diharapkan | Hasil nyata | Status | Rujukan |
|---|---|---|---|---|
| Customer memilih tanggal dan jam | Hanya slot dalam jadwal dan kapasitas yang tampil | Terverifikasi | LOLOS | `f4.sh`, `f4b.sh` |
| Reservasi di luar jadwal | Ditolak | Ditolak dengan pesan | LOLOS | `f4.sh` |
| Kapasitas penuh, dua request bersamaan | Satu masuk, satu ditolak | Satu 302, satu 200 ditolak | LOLOS | `f4.sh` (bisnis kapasitas 5) |
| Kode reservasi format `QC-YYYYMMDD-XXXX` | Sesuai format dan unik | `QC-20261116-0001` dst. | LOLOS | `f4.sh` |
| Nomor antrean berurutan per bisnis per tanggal | 1, 2, 3, ... | Terverifikasi | LOLOS | `f4.sh` |
| Kegagalan di tengah transaksi | Tidak ada data setengah tersimpan | Tidak ada reservasi tertinggal | LOLOS | `ReservationAtomicTest` (unit test) |
| Riwayat reservasi | Daftar milik sendiri | Hanya milik sendiri | LOLOS | `f4c.sh` |
| Buka reservasi orang lain | 404 (existence tidak bocor) | 404 | LOLOS | `f4c.sh` |
| Batalkan reservasi WAITING | Dibatalkan | Status CANCELLED | LOLOS | `f4c.sh` |
| Batalkan reservasi yang sudah dilayani | Ditolak | Ditolak | LOLOS | `f4c.sh` |

### S4 Operasi antrean

| Langkah | Hasil yang diharapkan | Hasil nyata | Status | Rujukan |
|---|---|---|---|---|
| Owner melihat antrean hari ini | Tabel nomor, nama, layanan, jam, status | Tampil | LOLOS | `docs/screenshots/fase5-owner-queues-1280.png` |
| WAITING → CALLED | Status dan `called_at` berubah | Terverifikasi | LOLOS | `f5.sh` |
| CALLED → IN_SERVICE | Status dan `started_at` berubah | Terverifikasi | LOLOS | `f5.sh` |
| IN_SERVICE → COMPLETED | Status dan `completed_at` berubah | Terverifikasi | LOLOS | `f5.sh` |
| Loncat status (WAITING → COMPLETED) | Ditolak | Ditolak dengan daftar status yang diizinkan | LOLOS | `f5.sh` |
| Dua klik "Panggil" bersamaan | Hanya satu masuk | Satu berhasil, satu ditolak | LOLOS | `f5.sh` |
| NO_SHOW | Antrean dan reservasi jadi CANCELLED | Terverifikasi | LOLOS | `f5.sh` |
| Customer membuka `/queue/{id}` | Status dan posisi tampil | Posisi turun 3 → 1 | LOLOS | `f5b.sh`, `pw_f5b.py` |
| Pembaruan berkala | Status berubah tanpa muat ulang | Terverifikasi dengan browser | LOLOS | `pw_f5b.py` |
| Buka antrean orang lain | 404 | 404 | LOLOS | `f5c.sh` |
| Owner lain mengubah antrean | 403 | 403 | LOLOS | `f5c.sh` |

### S5 SmartWait

| Langkah | Hasil yang diharapkan | Hasil nyata | Status | Rujukan |
|---|---|---|---|---|
| Contoh master planning (4 × 15 ÷ 2) | 30 menit | 30 | LOLOS | `SmartWaitServiceTest` |
| Petugas aktif = 0 | "Estimasi belum tersedia", tanpa error | Terverifikasi | LOLOS | `f6.sh`, `SmartWaitServiceTest` |
| Tidak ada pelanggan di depan | "Kamu berikutnya" | Terverifikasi | LOLOS | `f6.sh` |
| Layanan berdurasi berbeda | Total durasi dihitung per layanan | Terverifikasi | LOLOS | `SmartWaitServiceTest` |
| Pembulatan ke atas (25 ÷ 2) | 13 | 13 | LOLOS | `SmartWaitServiceTest` |
| Satu petugas | Hasil sesuai durasi | Terverifikasi | LOLOS | `SmartWaitServiceTest` |
| Angka di `/queue/{id}` cocok hitungan manual | Cocok | Dijelaskan di laporan Fase 6 | LOLOS | `f6.sh` |
| Status antrean di depan berubah | Estimasi turun tanpa muat ulang | Terverifikasi dengan browser | LOLOS | `pw_f6.py` |
| Catatan "perkiraan, bukan jaminan" | Selalu tampil bila ada estimasi | Terverifikasi | LOLOS | `f6.sh` |
| Pesan khusus CALLED dan final | Tampil benar | Terverifikasi | LOLOS | `f6.sh` |

### S6 Otorisasi

| Langkah | Hasil yang diharapkan | Hasil nyata | Status | Rujukan |
|---|---|---|---|---|
| Customer ke `/admin/*` | 403 | 403 | LOLOS | `f7b.sh` bagian 11 |
| Owner ke `/admin/*` | 403 | 403 | LOLOS | `f7b.sh` bagian 11 |
| Admin ke `/owner/*` | 403 | 403 | LOLOS | `f7.sh` |
| Admin ke `/reservation/*`, `/reservations/*`, `/queue/*` | 403 | 403 | LOLOS | `f7.sh` |
| Customer ke `/owner/*` | 403 | 403 | LOLOS | `f5c.sh` |
| Owner silang bisnis | 403 di lapisan service | 403 | LOLOS | `f4c.sh` |
| Customer silang reservasi/antrean | 404 | 404 | LOLOS | `f4c.sh`, `f5c.sh` |
| `/profile` untuk semua role | Bisa diakses, hanya akun sendiri | Terverifikasi | LOLOS | `f7b.sh` bagian 10 |
| `/admin/users` hanya Admin | 403 untuk selain Admin | 403 | LOLOS | `f7b.sh` bagian 11 |

## Checklist kelengkapan

Master planning bagian 11 tidak ada di repository, jadi daftar ini disusun ulang dari R1–R14 dan langkah user flow di `PRD.md` bagian 4. Butir yang tidak bisa dibuktikan diberi status BELUM DIUJI.

| # | Butir | Hasil yang diharapkan | Hasil nyata | Status | Rujukan |
|---|---|---|---|---|---|
| C1 | R1 tiga role dan hak akses di backend | 403/404 di service, bukan hanya tampilan | Terverifikasi | LOLOS | S6 |
| C2 | R2 registrasi, login, logout, profil, password, email unik, hash | Semua terverifikasi | Terverifikasi | LOLOS | S1 |
| C3 | R3 katalog tanpa login, cari nama, filter kategori | Terverifikasi | Terverifikasi | LOLOS | `qc_fase3b.sh` |
| C4 | R4 Owner mengelola bisnis sendiri, tidak bisa bisnis orang lain | Terverifikasi | Terverifikasi | LOLOS | S2 |
| C5 | R5 reservasi satu layanan, cek jadwal dan kapasitas | Terverifikasi | Terverifikasi | LOLOS | S3 |
| C6 | R6 kode `QC-YYYYMMDD-XXXX`, nomor unik per bisnis per tanggal | Terverifikasi | Terverifikasi | LOLOS | S3 |
| C7 | R7 alur status WAITING→CALLED→IN_SERVICE→COMPLETED, CANCELLED, NO_SHOW | Terverifikasi | Terverifikasi | LOLOS | S4 |
| C8 | R8 monitoring posisi dan status dengan polling | Terverifikasi | Terverifikasi | LOLOS | S4 |
| C9 | R9 SmartWait dan kasus petugas 0 | Terverifikasi | Terverifikasi | LOLOS | S5 |
| C10 | R10 riwayat dan pembatalan | Terverifikasi | Terverifikasi | LOLOS | S3 |
| C11 | R11 dashboard dan laporan Owner serta Admin | Angka cocok SQL | Terverifikasi | LOLOS | S2, `f7.sh` |
| C12 | R12 Admin mengelola akun, kategori, status bisnis | Terverifikasi | Terverifikasi | LOLOS | S2 |
| C13 | R13 reservasi dan antrean satu transaksi | Unit test membuktikan tidak ada data setengah tersimpan | Terverifikasi | LOLOS | `ReservationAtomicTest` |
| C14 | R14 responsif dan jalan di lokal | 375px dan 1280px tanpa scroll horizontal | Terverifikasi | LOLOS | `docs/screenshots/` |
| C15 | Filter kategori dan status di halaman Admin | Bekerja | Terverifikasi | LOLOS | `f7.sh` |
| C16 | Isian form pengguna tidak hilang saat validasi gagal | Nama, email, role tetap | Terverifikasi | LOLOS | `fase8-form-pengguna-1280.png` |
| C17 | Daftar reservasi Admin: filter tanggal, bisnis, status, paginasi | Bekerja, termasuk gabungan | Terverifikasi | LOLOS | `f7.sh` |
| C18 | Laporan kosong pada rentang tanpa data | "Belum ada data" | Terverifikasi | LOLOS | `f7.sh` |
| C19 | Tanggal awal setelah tanggal akhir ditolak | Pesan jelas | Terverifikasi | LOLOS | `f7.sh` |
| C20 | Layanan terpopuler urut dan hanya bisnis aktif | Cocok dengan SQL | Terverifikasi | LOLOS | `f7.sh` |
| C21 | Owner hanya melihat angka bisnisnya | Owner2 mendapat angka bisnisnya sendiri | Terverifikasi | LOLOS | `f7.sh` |
| C22 | Akun demo tiap role terdokumentasi | Lihat `README.md` | Terverifikasi | LOLOS | `README.md` |
| C23 | Reset data demo | `scripts/reset-demo.sh` berjalan, seed terisi untuk hari ini | Terverifikasi | LOLOS | `docs/pengujian.md` bagian reset |
| C24 | Diagram ERD, class, use case | Sumber dan gambar tersedia | Terverifikasi | LOLOS | `docs/diagram/` |
| C25 | SonarQube | Hasil nyata atau "belum dijalankan" | Belum dijalankan | BELUM DIUJI | `docs/sonarqube/README.md` |
| C26 | Alur tambah, ubah, nonaktif pengguna lewat browser | Diuji dengan browser | `pw_f8_browser.py` 6/6: login, tambah, email duplikat dengan isian tetap, ubah, nonaktif lewat modal, login akun nonaktif ditolak | LOLOS | `docs/screenshots/fase8-tambah-pengguna-1280.png`, `fase8-form-pengguna-1280.png`, `fase8-nonaktif-pengguna-1280.png`, `fase8-login-nonaktif-375.png` |
| C27 | Unit test `AkunNonaktifFilter` | Ada unit test | Belum ada; perilakunya diuji lewat `f7b.sh` bagian 7 dan `pw_f8_browser.py` | BELUM DIUJI | `docs/daftar-bug.md` |
| C28 | Uji beban / banyak pengguna bersamaan di luar kapasitas | Diuji | Hanya dua request bersamaan di kapasitas penuh | BELUM DIUJI | `f4.sh` |
| C29 | Uji di tablet (lebar 768px) | Tanpa scroll horizontal | Hanya 375px dan 1280px | BELUM DIUJI | `docs/screenshots/` |
| C30 | Uji linter/analisis statik selain SonarQube | Dilakukan | Hanya `mvn test` | BELUM DIUJI | - |
| C31 | Modal konfirmasi tampil dan bisa dipakai | Modal terbuka, tombol bisa diklik | `dialog.modal` tampil di tengah, `dialog[open]` = 1, alur nonaktif lewat modal 6/6 | LOLOS | `docs/screenshots/fase8-modal-pengguna-1280.png` |
| C32 | Reset data demo | `scripts/reset-demo.sh` berjalan, seed terisi untuk hari ini | Terverifikasi | LOLOS | `docs/pengujian.md` bagian reset |
| C33 | Impor `database/queuecare.sql` | Database kosong bisa diimpor dan aplikasi jalan | Diimpor ke `queuecare_import`, aplikasi jalan di port 8081, login admin berhasil | LOLOS | `docs/pengujian.md` bagian impor |

Catatan 24 Nov 2026 untuk baris C28 di atas: baris itu adalah status lama. Sesudah perbaikan konkurensi `2c197bb`, C28 diuji ulang enam putaran tulis: 0 HTTP 500 dan jumlah sukses = min(N, kapasitas 6); lihat bagian "Uji ulang C28 tulis setelah perbaikan konkurensi". Baris lama sengaja tidak diubah.

## Ringkasan

- LOLOS: 6 skenario utama, 28 butir checklist (C1–C24, C26, C31, C32, C33).
- GAGAL: 0.
- BELUM DIUJI: 5 butir (C25, C27, C28, C29, C30).
- Diperbarui 24 Nov 2026: C27 LOLOS, C28 LOLOS (uji ulang), C29 LOLOS, C30 LOLOS, SonarQube Passed. Checklist C1–C33 seluruhnya LOLOS; 0 GAGAL; 0 BELUM DIUJI.

## Uji regresi

Semua skrip uji fase sebelumnya dijalankan ulang pada 15 November 2026:

| Skrip | Hasil |
|---|---|
| `qc_test.sh` (Fase 1) | 14 ok |
| `qc_fase3b.sh` (Fase 3) | 0 bocor |
| `f4c.sh` (Fase 4) | 40/40 |
| `f5c.sh` (Fase 5) | 32/32 |
| `f6.sh` (Fase 6) | 15/15 |
| `f7.sh` (Fase 7) | 45/45 |
| `f7b.sh` (Fase 7b) | 57/57 |
| `mvn -B test` | 19 tes, BUILD SUCCESS |
| `mvn clean package` | BUILD SUCCESS |

## Impor `database/queuecare.sql` (pengujian C33)

1. `CREATE DATABASE queuecare_import` lalu `mysql -u root queuecare_import < database/queuecare.sql`.
2. Hasil: 6 pengguna, 6 bisnis, 19 layanan, 9 antrean, 9 reservasi.
3. Aplikasi dijalankan dengan `SERVER_PORT=8081 SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/queuecare_import` → `Tomcat started on port 8081`, `GET /login` = 200, login admin = 302 ke `/admin`.

Status: LOLOS.

## Reset data demo (pengujian C23)

1. `bash scripts/reset-demo.sh` → `queues` dan `reservations` kosong, `dummy1`/`dummy2` dibuat ulang.
2. Restart aplikasi.
3. Hasil: 9 antrean dan 9 reservasi dengan `queue_date` hari ini (atau hari buka berikutnya), status campur WAITING, CALLED, IN_SERVICE, COMPLETED, NO_SHOW, CANCELLED.

Status: LOLOS. Diuji pada Minggu, 15 November 2026 pukul 13.28; karena Minggu libur pada jadwal demo, `queue_date` jatuh pada Senin 16 November 2026 (7 antrean) dan Selasa 17 November 2026 (2 antrean). Perilaku ini memang aturan `DummyDataSeeder`.

## Penutupan pengujian C27–C30 dan SonarQube (21 November 2026)

Bagian ini menambah hasil terbaru. Baris C25 dan C27–C30 di tabel checklist di atas tidak diubah; status di bawah ini yang terbaru. Cabang `penutupan-pengujian` dibuat dari `main` (`bb49d41`), tidak di-push dan tidak di-merge.

Status ringkas: C27 LOLOS, C28 GAGAL, C29 LOLOS, C30 LOLOS, SonarQube TIDAK DAPAT DIJALANKAN.

Diperbarui 24 Nov 2026: C28 yang tercatat GAGAL di sini sudah diperbaiki (`2c197bb`) lalu diuji ulang enam putaran tulis — 0 HTTP 500, sukses = min(N, kapasitas 6) — sehingga status terakhirnya LOLOS. SonarQube kemudian dijalankan dan quality gate Passed; lihat bagian analisis SonarQube di dokumen ini. Isi lama di atas sengaja tidak dihapus.

### C27 — unit test `AkunNonaktifFilter` — LOLOS

- Berkas baru: `src/test/java/com/queuecare/config/AkunNonaktifFilterTest.java` (5 tes, tanpa mengubah kode produksi).
- `mvn.cmd -B test` → `Tests run: 24, Failures: 0, Errors: 0` (19 tes lama + 5 baru), `BUILD SUCCESS`.
- Bukti tes tidak kosong: kondisi produksi diubah sementara dari `.map(user -> !user.isActive())` menjadi `.map(user -> user.isActive())`, lalu `mvn.cmd -B test` → `Tests run: 24, Failures: 2`. Berkas produksi dikembalikan persis dari cadangan; `git diff` produksi kosong dan `mvn.cmd -B test` kembali 24/24.
- Perilaku yang diuji: akun nonaktif ditolak + sesi diputus + redirect `/login?disabled` + rantai dihentikan; akun aktif, tidak login, anonymous, dan email tidak ditemukan tetap lanjut.
- Catatan temuan (tidak diubah): filter memeriksa `getSession(false)` lalu memutus sesi lewat `getSession()` tanpa argumen.

### C28 — uji beban — GAGAL

> Catatan 24 Nov 2026: bagian ini menyimpan hasil pertama (GAGAL). Status terakhir C28 adalah LOLOS setelah perbaikan `2c197bb` dan uji ulang; lihat bagian "Uji ulang C28 tulis setelah perbaikan konkurensi" di bawah.

Bagian (a) baca-only LOLOS. Skrip Python `requests` + `ThreadPoolExecutor`, 10/25/50 worker serentak, 340 request total, login customer valid (dashboard `GET /customer` = 200). Semua response 200, laju error 0,000.

| Route | p50 10 / p95 10 | p50 25 / p95 25 | p50 50 / p95 50 | Error |
|---|---|---|---|---|
| `/` | 11,2 / 14,2 ms | 17,6 / 22,0 ms | 25,4 / 35,7 ms | 0/10, 0/25, 0/50 |
| `/businesses` | 15,2 / 16,3 ms | 23,8 / 32,5 ms | 35,5 / 53,1 ms | 0/10, 0/25, 0/50 |
| `/login` | 9,8 / 12,8 ms | 17,0 / 21,7 ms | 27,1 / 40,0 ms | 0/10, 0/25, 0/50 |
| `/customer` (login) | 15,8 / 18,1 ms | 24,6 / 31,9 ms | 33,8 / 48,6 ms | 0/10, 0/25, 0/50 |

Bukti mentah: `docs/bukti-pengujian/c28-baca.json`.

Bagian (b) tulis GAGAL. 12 sesi pelanggan bersamaan memesan layanan 42 (bisnis 20), tanggal 2026-11-23 jam 09:00, pada database terpisah `queuecare_load` di port 8081 (kapasitas slot 6). Hasil: `302` = 4, `500` = 8 (laju error 8/12 = 66,7%). Syarat "tidak ada error 500" tidak terpenuhi, sehingga C28 berstatus GAGAL meski dua syarat lain terpenuhi:

- Nomor antrean unik: `dup_queue_number = 0`; urutan `1,2,3,4`; constraint `uk_queues_nomor (business_id, queue_date, queue_number)` ada di DB.
- Tidak ada reservasi ganda: `dup_reservation_code = 0`; hanya 4 reservasi berhasil (di bawah kapasitas 6), total `reservations` 10 → 14 dan `queues` 10 → 14 di DB uji.
- Penyebab 500 (dari log aplikasi): `Duplicate entry 'QC-20261123-0002'`/`'0003' for key 'reservations.UK6g1uj544xgjuyhj6kjh6pka6l'`. Percobaan ulang pada `ReservationService.buat` habis, `DataIntegrityViolationException` tidak ditangkap `ReservationController.buat`, sehingga keluar sebagai HTTP 500.

Bukti mentah: `docs/bukti-pengujian/c28-tulis.json`. Setelah uji, database `queuecare_load` di-drop. Database demo `queuecare` tidak pernah dipakai untuk uji tulis; jumlah baris sebelum dan sesudah identik: users 6, reservations 10, queues 10, businesses 6, services 19, schedules 42.

### C29 — tampilan tablet 768px — LOLOS

- Harness Playwright/Edge pada route dan role yang sama dengan uji asap: 38 route × 4 role = 152 sel, lebar 768×1024 → 152 sesuai harapan, 0 deviasi.
- Dari 58 halaman yang diharapkan 200: overflow horizontal 0, tabel sempit 0, grid sempit 0.
- Layout 768px memakai desktop: `.dash` = `248px 1fr` (sidebar 248px, konten 488–520px), menu sidebar terlihat, navbar 3–5 tautan dan tombol akun hadir. `statistik-grid` 2 kolom, `kartu-grid` 2 kolom.
- Target sentuh: 57 dari 58 halaman masih memiliki elemen <44px (terutama tautan navbar 40px) — dicatat sebagai temuan, tidak diubah karena menyentuh tinggi navbar global. Satu masalah jelas diperbaiki: tombol lipat sidebar 32×32px di 768px padahal aturan ≤720px sudah menetapkan 44×44px → `components.css` diubah ke 44×44px.
- Bukti: 10 screenshot di `docs/screenshots/tablet-768/`.
- Uji asap ulang setelah perubahan CSS dan JS: `total sel: 191 | DEVIASI: 0 | SEMUA SESUAI HARAPAN`.

### C30 — linter selain SonarQube — LOLOS

- Checkstyle (plugin dijalankan dari CLI, `pom.xml` tidak diubah):
  `mvn.cmd -B org.apache.maven.plugins:maven-checkstyle-plugin:3.6.0:check -Dcheckstyle.config.location=google_checks.xml -Dcheckstyle.failOnViolation=false -Dcheckstyle.consoleOutput=true`
  → 0 error, 2.907 warning: Indentation 2.618, CustomImportOrder 99, MissingJavadocMethod 78, LineLength 63, MissingJavadocType 42, VariableDeclarationUsageDistance 5, OverloadMethodsDeclarationOrder 2. Mayoritas adalah beda konvensi (proyek 4 spasi vs Google 2 spasi), tidak diubah.
- ESLint 9 via `npx` dengan konfigurasi sementara di luar repo: awalnya 2 warning `no-unused-vars` (`catch (e)` tak terpakai di `sidebar.js`). Keduanya diperbaiki menjadi `catch {}`; ulang → 0 temuan.
- Stylelint 16 via `npx` dengan konfigurasi sementara di luar repo untuk `src/main/resources/static/**/*.css` → 0 temuan.
- `mvn.cmd -B test` setelah perubahan JS → 24/24 lolos; uji asap 191 sel, 0 deviasi.

### SonarQube — TIDAK DAPAT DIJALANKAN

- `docker version`: client 28.3.2 tersedia, tetapi server error `open //./pipe/dockerDesktopLinuxEngine: The system cannot find the file specified`. `Get-Service DockerDesktopService` tidak mengembalikan layanan. Tidak ada server SonarQube dan tidak ada token; tidak ada angka quality gate, bug, vulnerability, code smell, security hotspot, coverage, atau duplikasi yang bisa dilaporkan tanpa dibuat-buat.
- Langkah persis yang perlu dilakukan pemilik mesin:
  1. Jalankan Docker Desktop dan tunggu sampai `docker info` berhasil (butuh WSL2).
  2. `docker run -d --name sonarqube -p 9000:9000 sonarqube:community`, lalu buka `http://localhost:9000`.
  3. Login dengan kredensial administrator SonarQube yang Anda tetapkan, lalu buat token di My Account → Security. Jangan simpan token atau kata sandi di repo.
  4. Hasilkan coverage: `mvn.cmd -B org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report` (dibutuhkan oleh `sonar.coverage.jacoco.xmlReportPaths` di `docs/sonarqube/sonar-project.properties`).
  5. Jalankan analisis dengan token yang dibuat di langkah 3: `mvn.cmd -B sonar:sonar -Dsonar.projectKey=queuecare -Dsonar.host.url=http://localhost:9000 -Dsonar.token=TOKEN_DARI_LANGKAH_3`.
  6. Baca status quality gate dan jumlah issue di UI SonarQube, lalu catat di berkas ini.

### Commit pada cabang ini

- `b76b013` — test(config): uji unit AkunNonaktifFilter untuk akun nonaktif, aktif, anonim, dan sesi.
- `fb7f1c7` — test(ui): bukti screenshot tampilan tablet 768px untuk semua role.
- `190ecb4` — fix(ui): target sentuh tombol lipat sidebar 44px juga di lebar tablet 768.
- `a8a3600` — test(beban): bukti uji beban baca 340 request dan uji tulis 12 pemesanan bersamaan.
- `8d3e9c7` — refactor(js): hapus binding catch tak terpakai di sidebar.js.

## Analisis SonarQube lokal (21 November 2026)

Status: **LOLOS** — analisis selesai dan semua angka diambil langsung dari API SonarQube, bukan perkiraan. Quality gate: **Passed** (API mengembalikan status `OK` dengan daftar kondisi kosong; dasbor menampilkan "Passed"). Catatan: quality gate lolos walaupun ada 2 vulnerability; laporan ini tidak menyembunyikannya. Branch `perbaikan-konkurensi` (`a90b5de`) memang ada dan tidak disentuh; pekerjaan ini di `penutupan-pengujian` (`014a01d`).

- Versi: SonarQube Community **26.11.0.134175** (H2 embedded, hanya untuk evaluasi), container `sonarqube-qc`, status `Up`, port `9000`. `vm.max_map_count = 262144`; tidak ada masalah start.
- Perintah tanpa token (token dan kata sandi hanya ada di variabel lingkungan sesi dan berkas `C:\Users\MyBook Hype AMD\AppData\Local\hermes\cache\scratch\sonar\creds.env`, di luar repo):

  1. `docker run -d --name sonarqube-qc -p 9000:9000 -v sonarqube-qc-data:/opt/sonarqube/data -v sonarqube-qc-logs:/opt/sonarqube/logs -v sonarqube-qc-ext:/opt/sonarqube/extensions sonarqube:community`
  2. Tunggu `GET /api/system/status` mengembalikan `UP` (sekitar 40 detik).
  3. Ganti kata sandi admin dan buat token lewat `/api/users/change_password` serta `/api/user_tokens/generate`.
  4. `mvn.cmd -B org.jacoco:jacoco-maven-plugin:0.8.12:prepare-agent test org.jacoco:jacoco-maven-plugin:0.8.12:report` → 24 tes lolos, `target/site/jacoco/jacoco.xml` terbentuk.
  5. `mvn.cmd -B org.sonarsource.scanner.maven:sonar-maven-plugin:5.9.0.7382:sonar` dengan properti dari `docs/sonarqube/sonar-project.properties` lewat argumen `-D` (`sonar.projectKey=queuecare`, `sonar.sources=src/main/java`, `sonar.java.binaries=target/classes`, `sonar.tests=src/test/java`, `sonar.java.test.binaries=target/test-classes`, `sonar.sourceEncoding=UTF-8`, `sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml`, `sonar.host.url=http://localhost:9000`, dan `-Dsonar.token` dari variabel lingkungan). `ANALYSIS SUCCESSFUL`; tugas CE berstatus `SUCCESS`.

  Catatan: `mvn.cmd -B sonar:sonar` gagal karena prefiks plugin tidak dikenal, dan versi `5.1.0.4388` tidak tersedia di Maven Central; analisis berhasil dengan koordinat eksplisit `5.9.0.7382`.

- Angka hasil (API): ncloc 4252; coverage **28,1%**; duplikasi **5,0%** (12 blok duplikat); komentar 4,5%. Bugs **0** (Reliability A / 1,0); vulnerabilities **2** (Security **E / 5,0**); code smells **0** (Maintainability A / 1,0); security hotspots **0** (UI menandainya deprecated). Jumlah issue OPEN: 2.
- Temuan terparah: hanya ada **dua** issue, bukan lima, jadi berikut semua yang ada:

  | Severity | Aturan | Berkas:baris | Pesan |
  |---|---|---|---|
  | BLOCKER | `java:S6437` | `src/main/java/com/queuecare/config/DummyDataSeeder.java:231` | Revoke and change this password, as it is compromised. |
  | MINOR | `java:S3752` | `src/main/java/com/queuecare/controller/ErrorController.java:10` | Explicitly specify the HTTP methods this endpoint accepts. |

- Tidak ada perbaikan kode: satu-satunya Blocker adalah kata sandi akun demo di seed, yang sesuai instruksi tidak diubah; temuan MINOR bukan Blocker/Critical. Karena itu tidak ada analisis "sesudah" untuk dibandingkan. Perubahan hanya `.gitignore` (`.scannerwork/` dan `.sonar/`) dan bukti screenshot.
- Bukti: `docs/bukti-pengujian/sonar-dashboard.png` (dasbor QueueCare, terverifikasi menampilkan QG Passed, coverage 28,1%, duplikasi 5,0%, 2 security issue). Angka mentah: berkas `results-first.json` di direktori scratch `sonar/` di luar repo.
- Kredensial: `git grep` hanya menemukan placeholder `TOKEN`/`TOKEN_DARI_LANGKAH_3` di dokumentasi, bukan token atau kata sandi asli. `git diff` tidak memuat rahasia.
- Container tetap berjalan; bila perlu dijalankan lagi: perintah `docker run` di atas dengan volume bernama yang sama (data analisis tetap tersimpan).
- Database demo tidak berubah: users 6, reservations 10, queues 10, businesses 6, services 19, schedules 42.

## Keputusan implementasi 22 Nov 2026 — D3 dan SavedRequest

Bagian ini menambah hasil terbaru; isi lama di atas tidak dihapus. Cabang `keputusan-implementasi` dibuat dari `dokumen-keputusan` (`d9c175a`), tidak di-push dan tidak di-merge.

### D3 — reset password oleh Admin wajib berbeda dari password lama — LOLOS

- Perubahan: `AdminUserService.ubah` menambah pemeriksaan `passwordEncoder.matches(form.getPassword(), target.getPasswordHash())`; bila sama, ditolak dengan pesan "Password baru tidak boleh sama dengan password lama." (sama dengan pesan `AuthService.gantiPassword`).
- Tes baru di `AdminUserServiceTest`: `resetAdminMenolakPasswordSamaDenganLama` (ditolak, pesan cocok) dan `resetAdminMenerimaPasswordBerbedaDariLama` (hash baru tersimpan).
- `mvn.cmd -B test` → **27 tes lolos, 0 gagal** (25 tes lama + 2 baru).
  Diperbarui 24 Nov 2026: `mvn.cmd -B test` sekarang **35 tes lolos, 0 gagal** (dijalankan 24 Nov 2026); angka 27 di atas adalah hitungan saat D3 dicatat, sengaja tidak dihapus.

### SavedRequest — pengalihan setelah login

- Perubahan: `SecurityConfig` memakai `RequestCache` bawaan Spring Security; success handler mengarahkan Customer ke tujuan tersimpan bila ada dan masih jalur relatif internal, dan mengarahkan Owner/Admin ke dashboard role-nya.
- Tes: `SecurityConfigTest` (baru) membuktikan pilihan target dan anti open redirect.
- Bukti browser dan detail langkah ada di bagian berikut pada dokumen ini.

## Uji ulang C28 tulis setelah perbaikan konkurensi (21 November 2026)

Catatan GAGAL di bagian C28 sebelumnya sengaja tidak dihapus; tambahan ini adalah riwayat lanjutan. Cabang `perbaikan-konkurensi` dibuat dari `penutupan-pengujian` (`014a01d`), commit perbaikan `2c197bb`. Tidak ada push atau merge.

Penyebab terbukti: tes integrasi `ReservationConcurrencyTest` (12 thread, kapasitas 3) pada kode lama gagal dengan 9 `DataIntegrityViolationException` berisi `Duplicate entry 'QC-20261122-0003'`. MySQL memakai `REPEATABLE-READ`; transaksi melakukan beberapa pembacaan (`layananAktif`) sebelum mengunci baris bisnis, sehingga `countByReservationDate` dan `maxNomor` membaca snapshot lama meski kunci sudah didapat. Retry 3 kali di transaksi baru belum cukup untuk 12 thread yang bersamaan, dan `DataIntegrityViolationException` yang tersisa lolos sebagai HTTP 500.

Perbaikan terkecil: transaksi pemesanan memakai `READ_COMMITTED`, sehingga hitung kapasitas, kode, dan nomor antrean dibaca setelah kunci bisnis dilepas dan melihat data yang sudah ter-commit. Kunci baris bisnis yang sudah ada dipertahankan; tidak ada perubahan entity atau skema. Sebagai pengaman, `DataIntegrityViolationException` yang tersisa ditangkap controller dengan pesan "Reservasi bentrok dengan pelanggan lain. Silakan coba lagi." bukan 500.

Bukti:

- Tes integrasi lama (sebelum perbaikan): `Tests run: 1, Failures: 1` dengan duplikat kode reservasi.
- Tes integrasi baru (sesudah perbaikan): `Tests run: 25, Failures: 0, Errors: 0, Skipped: 0` (`mvn.cmd -B test`); `ReservationConcurrencyTest` dijalankan ulang 5 kali berturut-turut, 5/5 lolos.
- Uji HTTP tulis ulang di database terpisah `queuecare_load` port 8081, 6 putaran:

  | Pemesan | Tanggal | 302 sukses | 200 slot penuh | 500 | Nomor antrean | Duplikat kode/nomor |
  |---|---|---|---|---|---|---|
  | 12 | 2026-11-23 | 6 | 6 | 0 | 1–6 | 0 |
  | 12 | 2026-11-24 | 6 | 6 | 0 | 1–6 | 0 |
  | 12 | 2026-11-25 | 6 | 6 | 0 | 1–6 | 0 |
  | 30 | 2026-11-26 | 6 | 24 | 0 | 1–6 | 0 |
  | 30 | 2026-11-27 | 6 | 24 | 0 | 1–6 | 0 |
  | 30 | 2026-11-28 | 6 | 24 | 0 | 1–6 | 0 |

  Jumlah sukses selalu `min(N, kapasitas 6)` dan tidak ada HTTP 500.

- Alur satu pengguna di database terpisah: cari bisnis 200, detail 200, form 200, pesan 302, halaman sukses 200, riwayat 200, detail 200, pantau antrean 200, `/status` JSON valid, batal 302; status DB akhir `CANCELLED` untuk reservasi dan antrean.
- Database `queuecare_load` di-drop setelah uji. Database demo `queuecare` tidak disentuh: users 6, reservations 10, queues 10, businesses 6, services 19, schedules 42 (sama sebelum dan sesudah).
- Uji asap ulang: `total sel: 191 | DEVIASI: 0 | SEMUA SESUAI HARAPAN`.

## Keputusan implementasi 22 Nov 2026 — uji lintas bisnis, SavedRequest, guardrail, asap

Bagian ini menambah; isi lama di atas tidak dihapus. Cabang `keputusan-implementasi` dari `dokumen-keputusan` (`d9c175a`), tidak di-push dan tidak di-merge.

### Uji konkurensi lintas bisnis — LOLOS

- Berkas: `src/test/java/com/queuecare/service/ReservationCrossBusinessConcurrencyTest.java`.
- Skenario: dua bisnis berbeda (kapasitas masing-masing 4), tanggal sama, 8 thread per bisnis (16 thread) memesan jam 09:00 bersamaan.
- Dijalankan **5 kali berturut-turut: 5/5 lolos**.
- Pemeriksaan: tidak ada galat bocor (hanya "Slot ini sudah penuh."), kode reservasi unik, nomor antrean unik dan berurutan per bisnis, jumlah sukses = kapasitas kedua bisnis (8).

### SavedRequest — LOLOS (browser)

Bukti browser (`scripts` salinan di scratch `bukti_22nov.py`, hasil `bukti-22nov/hasil.json`):

| Skenario | Hasil |
|---|---|
| Anonim buka `/reservation/create/42` → login Customer | mendarat di `/reservation/create/42` (form tadi) |
| Login langsung dari `/login` (Customer) | `/customer` |
| Login langsung (Owner) | `/owner` |
| Owner buka `/reservation/create/42` | HTTP 403 |
| Login langsung (Admin) | `/admin` |
| Admin buka `/reservation/create/42` | HTTP 403 |
| Tujuan luar (`?continue=http://evil.example.com/...`) | tidak diikuti, ke `/customer` |

Catatan: `HttpSessionRequestCache` menyimpan **URL penuh**; `SecurityConfig` menerima URL absolut hanya bila `host` sama dengan request, lalu mengambil path-nya. Host luar ditolak (uji unit `tujuanTersimpanDomainLuarTidakDiikuti`). Screenshot: `docs/screenshots/22nov-savedrequest-form.png`.

### Guardrail form reservasi — LOLOS

`templates/reservation/create.html`: input tanggal `min` = hari ini dan `max` = hari ini + 14 hari (konstanta `ReservationService.MAKS_HARI_DEPAN` yang sama dengan aturan server); select jam `required`; tombol "Batal, kembali ke bisnis"; judul slot diformat `dd/MM/yyyy`.

| Cek | 1280px | 375px |
|---|---|---|
| `min` / `max` | 2026-11-22 / 2026-12-06 | 2026-11-22 / 2026-12-06 |
| `jam.required` | true | true |
| Tombol Batal | ada | ada |
| Overflow horizontal | 0 | 0 |

Screenshot: `docs/screenshots/22nov-form-1280.png`, `22nov-form-375.png`. Validasi server tidak diubah; alur pesan tetap 302 ke halaman sukses.

### Profil (D2) — LOLOS

`templates/profile.html` memuat kalimat "Email dipakai untuk masuk. Kalau email diubah, kamu akan diminta masuk lagi." Screenshot: `docs/screenshots/22nov-profil-bantuan.png`.

### Uji asap, alur satu pengguna, data demo — LOLOS

- Uji asap: `total sel: 191 | DEVIASI: 0 | SEMUA SESUAI HARAPAN`.
- Alur satu pengguna di database terpisah (port 8081, DB `queuecare_uji22` dari dump demo): login 200, cari 200, detail 200, form 200, pesan 302, halaman sukses 200, riwayat 200, detail 200, pantau 200, `/status` JSON valid, batal 302; status DB akhir `CANCELLED` untuk reservasi dan antrean.
- Database demo `queuecare` tidak berubah (sebelum = sesudah): users 6, reservations 10, queues 10, businesses 6, services 19, business_schedules 42.

### Sonar 22 Nov 2026 — dua temuan ditandai Accepted (WONTFIX)

Bagian ini menambah; isi lama di atas tidak dihapus. Dua temuan di cabang `penutupan-pengujian` ditandai Accepted lewat API SonarQube pada 22 Nov 2026.

- Versi: SonarQube Community **26.11.0.134175** (container `sonarqube-qc`, port 9000, `/api/system/status` = `UP`).
- **Catatan transisi:** versi SonarQube ini **tidak mendukung transisi `accept`** (daftar transisi yang tersedia tidak memuatnya). Padanan Accepted yang dipakai ialah transisi **`wontfix`** → status `RESOLVED`, resolusi `WONTFIX`, ditambah komentar alasan. Profil aturan dan quality gate **tidak** diubah.

| Rule | Berkas:baris | Status akhir | Resolution | Komentar alasan |
|---|---|---|---|---|
| `java:S6437` (BLOCKER) | `src/main/java/com/queuecare/config/DummyDataSeeder.java:231` | RESOLVED | WONTFIX | akun demo lokal untuk pengembangan dan demo, bukan kredensial produksi |
| `java:S3752` (MINOR) | `src/main/java/com/queuecare/controller/ErrorController.java:10` | RESOLVED | WONTFIX | endpoint /error harus menerima semua metode HTTP karena meneruskan error dari request POST; membatasi ke GET akan merusak halaman error |

- Verifikasi lewat API: `GET /api/issues/search?componentKeys=queuecare` mengembalikan 2 issue, keduanya `RESOLVED`/`WONTFIX` dengan komentar di atas; `GET /api/qualitygates/project_status?projectKey=queuecare` = `OK`.
- Bukti mentah (key issue, status, komentar): `results-sonar-22nov.json` di direktori scratch `sonar/` di luar repo.
- Tidak ada token/kata sandi di repo; kredensial ada di scratch `sonar/creds.env` (di luar repo).

## Analisis ulang SonarQube pada hasil gabungan (21 November 2026)

`perbaikan-konkurensi` digabung ke `penutupan-pengujian` sebagai merge commit `5f999fb` (bukan rebase). Konflik hanya di `docs/pengujian.md`; kedua penambahan (bagian SonarQube dan bagian uji ulang C28) dipertahankan utuh, marker konflik dihapus. Tidak ada konflik pada berkas kode.

Status analisis ulang: **LOLOS** — `ANALYSIS SUCCESSFUL`, tugas CE `SUCCESS`, quality gate `Passed`. Angka langsung dari API dengan token di luar repo.

  Metrik                     | Sebelum (`cfa6215`) | Sesudah gabungan (`5f999fb`)
  ---------------------------+--------------------+-----------------------------
  ncloc                      | 4252               | 4262
  Coverage                   | 28,1%              | 28,6%
  Duplikasi                  | 5,0% (12 blok)     | 5,0% (12 blok)
  Komentar                   | 4,5%               | 4,6%
  Bugs / Reliability         | 0 / A              | 0 / A
  Vulnerabilities / Security | 2 / E              | 2 / E
  Code smells / Maintain.    | 0 / A              | 0 / A
  Security hotspots          | 0                  | 0
  Quality gate               | Passed             | Passed

Pemeriksaan kewajaran angka 0 smell dan 0 hotspot:

- Profil kualitas Java yang dipakai proyek adalah **Sonar way core** (default). Jumlah aturan aktif untuk profil ini: **263**, terdiri dari 172 BUG, 59 VULNERABILITY, 32 CODE_SMELL, dan **0 SECURITY_HOTSPOT** (severitas: MAJOR 142, CRITICAL 81, BLOCKER 29, MINOR 11). Angka 4.333 dari pencarian `activation=true&language=java` tanpa filter profil mencakup semua profil yang terpasang, bukan yang dipakai proyek.
- Daftar berkas yang dianalisis: **82 berkas `src/main/java` + 6 berkas `src/test/java` = 88 berkas Java**, sama persis dengan `git ls-files`. Tidak ada pengecualian: `docs/sonarqube/sonar-project.properties` tidak memuat `sonar.exclusions`/`sonar.coverage.exclusions`, dan `/api/settings/values` untuk proyek maupun global tidak mengembalikan nilai pengecualian.
- Jumlah issue menurut semua status: **2**, keduanya `OPEN`, tipenya `VULNERABILITY` (BLOCKER 1, MINOR 1). Tidak ada issue berstatus RESOLVED/CLOSED, dan tidak ada tipe BUG/CODE_SMELL/SECURITY_HOTSPOT.
- Kesimpulan: 0 hotspot bukan tanda kode bersih, melainkan karena profil tidak mengaktifkan aturan hotspot sama sekali (fitur ini deprecated di SonarQube 26.11) dan dasbor menampilkan "Limited security analysis" akibat database H2 bawaan. 0 smell juga bukan karena berkas dilewati — semua berkas ikut terhitung — tetapi hanya 32 aturan smell aktif untuk 4.262 baris kode, sehingga angka 0 wajar secara konfigurasi, bukan bukti tidak ada masalah kualitas. Profil aturan dan quality gate tidak saya ubah.

Dua vulnerability (BLOCKER `java:S6437` di `DummyDataSeeder.java:231` dan MINOR `java:S3752` di `ErrorController.java:10`) sengaja tidak diubah. Dua opsi untuk pemilik proyek:

1. Tandai issue sebagai **Accepted** di Sonar dengan alasan kata sandi tersebut hanya untuk akun demo lokal. Trade-off: cepat dan tidak mengubah perilaku, tetapi kata sandi tetap tertanam di kode dan tetap terlihat oleh siapa pun yang membaca repo.
2. Pindahkan kata sandi demo ke properti lingkungan/konfigurasi yang disuntikkan saat aplikasi berjalan. Trade-off: menghapus temuan dan mengurangi risiko, tetapi mengubah cara seed berjalan dan perlu koordinasi dengan anggota kelompok serta pembaruan dokumentasi akun demo.

Keputusan diserahkan kepada pemilik proyek; tidak ada kode, seed, profil, atau gate yang diubah untuk menurunkan temuan.

Hasil uji gabungan: `mvn.cmd -B test` → 25/25 lolos; uji asap 191 sel, 0 deviasi; alur satu pengguna (cari, pesan, pantau, batal) lolos dengan status akhir `CANCELLED`. Database demo tetap users 6, reservations 10, queues 10, businesses 6, services 19, schedules 42.




