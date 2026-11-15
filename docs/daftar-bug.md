# Daftar Bug QueueCare

Catatan bug yang ditemukan saat pengujian. Bug kecil sudah diperbaiki dan diuji ulang. Perbaikan yang menyentuh skema atau aturan bisnis tidak diubah, hanya dilaporkan.

## Sudah diperbaiki

| # | Gejala | Penyebab | Perbaikan | Status |
|---|---|---|---|---|
| B1 | Laporan Owner "Pelanggan unik" menampilkan 8, sedangkan SQL `COUNT(DISTINCT customer_id)` = 2 (Fase 7) | Query turunan `countDistinctCustomerByBusinessAndReservationDateBetween` tidak memakai `DISTINCT` | Diganti `@Query("select count(distinct r.customer.id) ...")` | Diperbaiki, `f7.sh` 45/45 |
| B2 | Laporan Admin dengan tanggal kosong menampilkan 0 reservasi, padahal harusnya seluruh platform (Fase 7) | JPQL memakai `between` yang tidak menerima `null` | Kondisi `:dari is null or ...` dan `:sampai is null or ...` | Diperbaiki, `f7.sh` 45/45 |
| B3 | `/reservations/{id}` mengembalikan 500 `LazyInitializationException` (Fase 6) | `Queue.reservation` dimuat di luar transaksi (`open-in-view=false`) | `QueueRepository.findByReservationLengkap` dengan `join fetch` | Diperbaiki, `f6.sh` 15/15 |
| B4 | Ubah nama/email profil dan ganti password tidak tersimpan ke database (Fase 7b) | `UserContext.currentUser()` mengembalikan entity lepas (detached), lalu diubah di transaksi lain | `AuthService.ubahProfil(Long id, ...)` dan `gantiPassword(Long id, ...)` memuat ulang entity di dalam transaksi | Diperbaiki, `f7b.sh` 57/57 |
| B5 | Akun yang baru dinonaktifkan Admin masih bisa dipakai selama sesi lama belum habis (Fase 7b) | Penolakan hanya terjadi saat login | `AkunNonaktifFilter` mengosongkan konteks, membuang sesi, dan mengarahkan ke `/login?disabled` | Diperbaiki, `f7b.sh` 57/57 |
| B6 | Halaman `/admin/users` mengembalikan 500 (Fase 7b) | Salah tulis `th:text="${...}'}"` (tanda `}` berlebih) di tiga tempat | Tanda `}` berlebih dihapus | Diperbaiki, `f7b.sh` 57/57 |
| B7 | Halaman `/admin/users/tambah` mengembalikan 500 (Fase 7b) | Salah tulis `th:text="${...}'}"` pada tombol simpan | Tanda `}` berlebih dihapus | Diperbaikan, `f7b.sh` 57/57 |
| B8 | Isian form pengguna Admin hilang saat validasi gagal, misalnya email duplikat (Fase 8) | Controller mengarahkan ke `/admin/users` dengan flash, form dibuang | Controller menampilkan ulang `admin/user-form` dengan isian dan pesan galat | Diperbaiki, diuji manual (lihat `docs/pengujian.md` C16) |
| B9 | Di halaman ubah pengguna, menyimpan tanpa mengisi password gagal "Password minimal 8 karakter" (ditemukan lewat browser, Fase 8) | Input password kosong terkirim sebagai `""`, dan `@Size(min = 8)` menolak string kosong | `AdminUserForm.setPassword` mengubah yang kosong menjadi `null` | Diperbaiki, diuji `pw_f8_browser.py` |
| B10 | Modal konfirmasi (batalkan reservasi, nonaktifkan antrean, nonaktifkan pengguna) tidak pernah tampil di browser, tombol tidak bisa diklik (ditemukan lewat browser, Fase 8) | Bootstrap punya class `.modal` sendiri dengan `display: none` untuk overlay-nya, dan menimpa `<dialog class="modal">` | Ditambah aturan `dialog.modal` di `components.css` yang memaksa tampil dan memusatkan | Diperbaiki, diuji `pw_f8_browser.py` |

## Bukan bug, tapi tercatat

| # | Gejala | Penjelasan |
|---|---|---|
| C1 | Log Hibernate `Duplicate entry 'QC-...' for key reservations...` (Fase 4) | Dua reservasi bersamaan memakai kode yang sama; transaksi diulang sampai dapat kode lain. Ini jalur retry yang diharapkan, bukan kegagalan. |
| C2 | Log `No converter for [class java.util.LinkedHashMap] with preset Content-Type 'text/html'` | Muncul saat halaman mengembalikan 500 (B6/B7). Menghilang setelah template diperbaiki. |

## Belum diperbaiki (dilaporkan, butuh keputusan)

| # | Gejala | Alasan belum diperbaiki |
|---|---|---|
| D1 | Aturan "Admin aktif terakhir tidak boleh dinonaktifkan atau diturunkan" tidak dapat dibuktikan lewat antarmuka | Aktor selalu ikut dihitung sebagai Admin aktif, sehingga aturan akun-sendiri lebih dulu menolak. Mengubah hitungan berarti mengubah aturan bisnis, jadi tidak diubah tanpa persetujuan. Aturan tetap ada sebagai jaring pengaman dan diuji di `AdminUserServiceTest`. |
| D2 | Mengubah email profil memaksa masuk ulang | Pilihan paling sederhana. Alternatifnya membangun ulang `UserDetails` di `SecurityContext`, lebih banyak kode dan risiko. |
| D3 | Reset password oleh Admin tidak mewajibkan password baru berbeda dari yang lama | Hanya ganti password oleh pemilik akun yang mewajibkan itu, sesuai instruksi Fase 7b. |
