# Hasil Pengujian QueueCare

Tanggal: 15 November 2026. Aplikasi dijalankan di `http://localhost:8080`, MySQL 8.0.30 (Laragon), JDK 21, Maven 3.9.16.

Cara mengulang: lihat kolom "Cara menguji". Skrip ada di `scripts/pengujian/` (salinan dari scratch) dan nama file disebut di kolom rujukan.

Status: LOLOS, GAGAL, atau BELUM DIUJI. Tidak ada hasil yang dibuat-buat; yang tidak dijalankan ditulis BELUM DIUJI.

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

## Ringkasan

- LOLOS: 6 skenario utama, 68 butir checklist.
- GAGAL: 0.
- BELUM DIUJI: 5 butir (C25, C27, C28, C29, C30).

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
