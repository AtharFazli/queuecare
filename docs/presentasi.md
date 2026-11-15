# Presentasi QueueCare (maksimal 10 menit)

## Outline slide

1. Judul: QueueCare, reservasi dan antrean untuk bisnis jasa.
2. Masalah: pelanggan tidak tahu gilirannya; bisnis tidak punya catatan rapi.
3. Solusi: katalog bisnis, reservasi dengan kode dan nomor antrean, operasi antrean Owner, estimasi tunggu SmartWait.
4. Arsitektur: Spring Boot + Thymeleaf + MySQL, tiga role, aturan akses di backend.
5. Demo Customer (2 menit).
6. Demo Owner (2 menit).
7. Demo Admin (2 menit).
8. SmartWait dan laporan (1 menit).
9. Keamanan dan data (1 menit).
10. Penutup dan pertanyaan.

## Skrip demo langkah demi langkah

Sebelum mulai: `bash scripts/reset-demo.sh`, restart aplikasi, pastikan `app.seed-dummy=true`.

### Customer (2 menit)

1. Buka `/businesses` tanpa login, cari "Senja", pilih kategori Barbershop.
2. Buka detail "Barbershop Senja", lihat layanan, harga, durasi, jam buka.
3. Klik reservasi pada "Cukur rambut". Kalau diminta login, masuk `customer@queuecare.id` / `customer123`.
4. Pilih tanggal dan jam, isi catatan, klik "Buat Reservasi".
5. Tunjukkan kode reservasi `QC-...` dan nomor antrean.
6. Buka "Pantau antrean": lihat posisi, status, dan estimasi SmartWait.
7. Buka riwayat `/reservations`, batalkan satu reservasi yang masih WAITING.

### Owner (2 menit)

1. Masuk `owner@queuecare.id` / `owner123`.
2. Di sidebar pilih bisnis aktif, lalu buka "Layanan" dan "Jadwal" untuk menunjukkan pengelolaan.
3. Buka "Antrean": tunjukkan tabel hari ini.
4. Jalankan satu antrean: Panggil → Mulai → Selesai.
5. Tunjukkan penolakan loncat status dengan mencoba ubah status lewat tombol yang tidak tersedia, atau tampilkan pesan saat transisi ilegal.
6. Buka "Laporan", ubah rentang tanggal, tunjukkan total dan layanan terpopuler.

### Admin (2 menit)

1. Masuk `admin@queuecare.id` / `admin123`.
2. Dashboard: jumlah pengguna, bisnis per status, reservasi, aktivitas terbaru.
3. "Pengguna": cari, filter role, tambah satu pengguna, ubah, nonaktifkan lalu aktifkan.
4. "Reservasi": filter tanggal, bisnis, status, paginasi.
5. "Laporan": total platform dan tabel per hari.
6. "Bisnis": nonaktifkan satu bisnis, lalu lihat katalog publik menyembunyikannya.

### SmartWait dan laporan (1 menit)

1. Buka `/queue/{id}` sebagai Customer, tunjukkan angka estimasi.
2. Sebagai Owner, tandai antrean di depan sebagai "Selesai", lalu lihat estimasi di layar Customer turun tanpa muat ulang.

### Keamanan dan data (1 menit)

1. Tunjukkan 403 saat Customer membuka `/admin/users`.
2. Tunjukkan pesan "Akun dinonaktifkan" setelah Admin menonaktifkan akun yang sedang login.
3. Sebutkan aturan: email unik, password BCrypt, kepemilikan dicek di service.

## Jawaban pertanyaan yang mungkin ditanya

**Apa itu SmartWait? Bukankah itu AI?**
Bukan. SmartWait adalah rumus aturan: ceil(total durasi layanan pelanggan di depan ÷ jumlah petugas aktif). Pelanggan di depan adalah antrean sebisnis dan se-tanggal dengan nomor lebih kecil yang berstatus WAITING, CALLED, atau IN_SERVICE. Perhitungan ada di `SmartWaitService.hitungMenit` sebagai fungsi murni, jadi bisa diuji tanpa database.

**Bagaimana kalau petugasnya 0?**
Tidak ada pembagian nol. Halaman menampilkan "Estimasi belum tersedia".

**Bagaimana kalau dua pelanggan reservasi bersamaan dan kapasitas penuh?**
Kapasitas diperiksa dengan `SELECT ... FOR UPDATE` di dalam satu transaksi bersama pembuatan antrean. Satu reservasi masuk, yang lain ditolak dengan pesan. Kode reservasi dihitung dari count + 1 dengan retry tiga kali.

**Bagaimana kalau dua tombol "Panggil" diklik bersamaan?**
Antrean dikunci per baris (`lockById`), dan transisi status dijaga dengan tabel transisi. Hanya satu perubahan yang masuk.

**Siapa yang boleh melihat data siapa?**
Owner hanya bisa membaca dan mengubah bisnisnya sendiri, dicek di service pada setiap request. Customer yang membuka reservasi atau antrean orang lain mendapat 404, supaya existence tidak bocor. Admin tidak menangani antrean harian.

**Apa yang terjadi kalau Owner dinonaktifkan?**
Akun tidak bisa masuk, tetapi bisnisnya tidak dihapus dan tetap tampil di katalog dan laporan.

**Di mana aturan akses diterapkan?**
Di lapisan service dan Spring Security, bukan di tampilan. Menu yang disembunyikan tetap tidak bisa diakses lewat URL langsung.
