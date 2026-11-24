# Audit Keamanan QueueCare

Tanggal: 24 November 2026.
Sumber audit: `D:\audits\PBO-Java` (di luar repository proyek).
Cabang saat pencatatan: `catatan-keamanan`, dari `keputusan-implementasi` pada commit `e7b789c1b7ae255a09f2aed6d7c3a56969a53e79`.

## 1. Metode dan batasannya

Audit dilakukan read-only: membaca source, konfigurasi, template, skrip, dan dokumen proyek. **Tidak ada build, tidak ada eksekusi aplikasi, tidak ada pengujian runtime, dan tidak ada probe jaringan.** Alasan: host tempat audit berjalan (Windows) tidak menyediakan sandbox OS yang diminta kerangka audit, jadi eksekusi kode target tidak diizinkan.

Akibatnya, semua kesimpulan bersumber dari pembacaan kode. Hal yang hanya bisa diputuskan dengan menjalankan aplikasi dicatat sebagai "belum divalidasi", bukan disimpulkan. Tidak ada angka, tangkapan layar, atau hasil uji runtime yang dibuat-buat di sini.

Cakupan yang diperiksa: otorisasi dan sesi (Spring Security + service layer), lapisan data (repository/JPQL), template Thymeleaf dan JavaScript, konfigurasi dan rahasia, serta rantai pasok (pom.xml). Daftar unit cakupan ada di `coverage-ledger.json` pada laporan audit.

Ringkasan jumlah temuan: **2 confirmed, 3 needs_validation, 5 rejected**.

## 2. Tabel temuan

| ID | Tingkat | Ringkasan | Status | Keputusan pemilik proyek |
|---|---|---|---|---|
| A1 | High | `DataInitializer` membuat akun Admin `admin@queuecare.id` / `admin123` pada setiap startup, tanpa `@Profile` atau properti pembatas; `DummyDataSeeder` juga aktif dengan `app.seed-dummy=true`. Hash BCrypt ada di `database/queuecare.sql`. | Confirmed (divverifikasi ulang oleh verifier independen) | **Diterima untuk sekarang.** Perbaikan ditunda sampai ada rencana hosting. |
| A2 | Low | Pesan login membedakan akun nonaktif (`/login?disabled`) dari kegagalan lain (`/login?error`), sehingga status akun dapat dibedakan. | Confirmed | **Diterima sebagai pilihan UX.** Password demo tidak diganti. |
| A3 | Belum dinilai | Tidak ada pembatasan percobaan login di source (rate limit / lockout); perlindungan dari reverse proxy/WAF tidak terlihat. | Needs validation — belum divalidasi | Dicatat apa adanya. |
| A4 | Belum dinilai | Parameter `tanggal` pada redirect Owner (`OwnerQueueController`) digabung langsung ke string `redirect:` tanpa encoding. | Needs validation — belum divalidasi | Dicatat apa saja. |
| A5 | Belum dinilai | Bootstrap dan Google Fonts dimuat dari CDN tanpa atribut `integrity` (SRI) dan tanpa Content-Security-Policy. | Needs validation — belum divalidasi | Dicatat apa adanya. |

Catatan keputusan: karena aplikasi **hanya dijalankan lokal** untuk pengembangan dan demo dan belum di-hosting, risiko eksternal A1 dan A2 belum relevan pada lingkungan target saat ini. Risiko itu menjadi relevan bila aplikasi pernah di-hosting publik — lihat bagian 5.

## 3. Lima temuan yang ditolak

1. **CSRF dimatikan** — ditolak. `SecurityConfig` memang tidak menulis `.csrf(...)`, tetapi default Spring Security mengaktifkan CSRF; form Thymeleaf dengan `th:action` menyisipkan token. Tidak ada baris yang mematikannya.
2. **Open redirect lewat backslash pada SavedRequest** — ditolak setelah verifier independen membaca ulang source. Semua tujuan tersimpan dilewati `URI.create`; backslash literal ilegal sehingga melempar `IllegalArgumentException` yang ditangkap dan menghasilkan null sebelum validasi jalur. Host absolut dicek terhadap `request.getServerName()` dan hanya path relatif yang dikirim ke `sendRedirect`.
3. **Injeksi SQL pada JPQL** — ditolak. Semua query memakai parameter terikat (`@Param`); tidak ada `nativeQuery` dan tidak ada konkatenasi input pengguna ke string query.
4. **IDOR pada reservasi/antrean Customer** — ditolak. Jalur berbasis id dicek kepemilikan di service (`milikSaya`, `milikCustomer`, `milikBisnis`, `pastikanPemilik`).
5. **Stored XSS lewat catatan reservasi** — ditolak. Tidak ada `th:utext`/`innerHTML`; output memakai `th:text` (escaped) dan JavaScript hanya memakai `textContent`.

## 4. Kontrol yang sudah baik (dibaca dari source)

- Otorisasi per-peran di filter chain plus pemeriksaan kepemilikan di service layer.
- Password disimpan sebagai hash BCrypt.
- Semua query JPQL berparameter.
- Output template Thymeleaf escaped; JavaScript tidak memakai DOM injection.
- Session invalidated saat logout dan saat email profil berubah.
- Tanpa Spring Boot Actuator; halaman error tidak menampilkan exception atau stack trace.
- Log SQL dan log aplikasi tidak mencetak data sensitif.

## 5. Syarat sebelum hosting publik

Bagian ini berlaku bila pemilik proyek kelak memutuskan untuk menghosting aplikasi. Sebelum hosting publik, hal-hal berikut perlu dikerjakan:

1. **Batasi akun dan data demo ke profil demo.** `DataInitializer` dan `DummyDataSeeder` tidak boleh membuat akun berperan ADMIN pada startup biasa.
2. **Bootstrap admin dari environment.** Kredensial admin pertama diambil dari environment variable, bukan hardcode di source.
3. **Ganti seluruh kata sandi yang tercatat** di dokumentasi, `DataInitializer`, `DummyDataSeeder`, dan dump SQL. Anggap hash di `database/queuecare.sql` sudah bocor karena tetap dapat dipulihkan dari riwayat git; menghapus berkasnya saja tidak memperbaiki.
4. **Pembatasan percobaan login** (rate limit/lockout) di lapisan aplikasi atau di reverse proxy/WAF.
5. **SRI dan CSP**: tambahkan `integrity` pada aset CDN atau self-host, lalu tetapkan Content-Security-Policy.
6. **Jalankan ulang audit** (audit ini dijalankan read-only, tanpa eksekusi) dengan sandbox yang memadai agar temuan "belum divalidasi" dapat diputuskan.

### Menjalankan lokal dengan lebih ketat

Bila ingin memastikan aplikasi hanya dapat diakses dari mesin itu, jalankan dengan mengikat alamat server ke loopback, misalnya:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.address=127.0.0.1
```

Perintah itu hanya membatasi antarmuka yang didengarkan; ia bukan pengganti syarat-syarat pada bagian 5.

## 6. Rujukan

Laporan lengkap berada di luar repository: `D:\audits\PBO-Java` — `REPORT.md`, `FINDINGS-DETAIL.md`, `NEEDS-VALIDATION.md`, `architecture.md`, `coverage-ledger.json`, `findings.json`, `run-metadata.json`.
