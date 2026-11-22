# Ringkasan keputusan terbuka QueueCare — bahan diskusi kelompok

Tujuan dokumen ini: merangkum dan **membuktikan**, bukan memutuskan. Status "SUDAH DIPUTUSKAN" hanya dipakai bila ada jejak tertulis di PRD/catatan Fase/komit, atau bukti perilaku di kode. Semua saran diberi label **USULAN** dan belum disetujui kelompok. Kolom PRD yang diusulkan di bawah **hanya disimpan di dokumen ini**; `PRD.md` tidak diubah.

Dibuat di branch `dokumen-keputusan` dari `main` (`986ede5`, = `origin/main`). Tidak ada push, tidak ada merge.

Konteksi terverifikasi saat penulisan: Fase 1–8 selesai; `mvn.cmd -B test` → **25 tes lolos**, 0 gagal; uji asap 191 sel, 0 deviasi; perbaikan konkurensi `2c197bb` sudah masuk. Database demo **tidak disentuh**: users 6, reservations 10, queues 10, businesses 6, services 19, business_schedules 42 (dibaca lewat `SELECT COUNT(*)`, bukan diubah).

---

## 1. Ringkasan

| Butir | Status | Inti bukti | USULAN satu baris |
|---|---|---|---|
| K1 slot / antrean | SEBAGIAN | Keduanya dipakai: `ReservationService.java:35,84-109` slot 30 mnt + `:186-192` nomor antrean | Tetapkan "antrean berjalan + grid slot 30 menit", bukan pilih salah satu |
| K2 kapasitas & petugas | SUDAH DIPUTUSKAN (de facto) | `Business.java:53-57`, `database/queuecare.sql:75,79` | Isi kolom: "Setuju — angka di tabel `businesses`" |
| K3 owner daftar / dibuat Admin | SUDAH DIPUTUSKAN (tapi PRD kontradiktif) | `PRD.md:257`, `AuthService.java:35` | Isi kolom: "Dibuat Administrator"; perbaiki `PRD.md:56` |
| K4 walk-in | SUDAH DIPUTUSKAN (tertulis) | `PRD.md:43` | Isi kolom: "Tidak — di luar cakupan versi pertama" |
| K5 teks / tabel kategori | SUDAH DIPUTUSKAN (de facto) | `Business.java:36-38`, `database/queuecare.sql:84,89,111` | Isi kolom: "Tabel `categories` + `category_id`" |
| K6 batas pembatalan | SEBAGIAN | `ReservationService.java:259`, `QueueService.java:41-42`; tidak ada batas jam | Isi kolom: "Customer: WAITING; Owner: s.d. CALLED; tanpa batas jam" |
| D1 Admin aktif terakhir | SUDAH DIPUTUSKAN (terima) | `docs/daftar-bug.md:31`, `AdminUserService.java:83-89,114-126`, `AdminUserServiceTest.java:95-116` | Terima & dokumentasikan; jangan ubah hitungan tanpa persetujuan |
| D2 email → login ulang | SUDAH DIPUTUSKAN (terima) | `PRD.md:257`, `ProfileController.java:58,66-72` | Terima & jelaskan di UI form profil |
| D3 reset password Admin | SEBAGIAN | `AdminUserService.java:70-75` vs `AuthService.java:60-62` | Putuskan dulu: samakan dengan aturan ganti-password sendiri atau tidak |

---

## 2. K1–K6 — pertanyaan PRD, bukti, opsi, usulan

### K1 — Reservasi pakai slot waktu atau antrean berjalan?

- Pertanyaan PRD apa adanya (`PRD.md:49`): `| K1 | Reservasi pakai slot waktu atau antrean berjalan? | Pilih satu saja agar sederhana | [isi: slot / antrean berjalan] |`
- Status: **SEBAGIAN**. Implementasi memakai **keduanya**, dan tidak ada catatan Fase/komit yang memilih salah satu.
- Bukti:
  - Slot 30 menit: `ReservationService.java:35` (`DURASI_SLOT_MENIT = 30`), `:84-109` (`slotUntuk`), validasi `:158-160` (`slotValid`), batas 14 hari `:36,146`.
  - Antrean berjalan: `ReservationService.java:186-192` membuat `Queue` dengan `queue_number` dari `nomorBerikut` (`:211-213`); `QueueService.java:40-43` transisi WAITING→CALLED→IN_SERVICE→COMPLETED; constraint unik `database/queuecare.sql:150`.
  - Keputusan Fase 4 mengukuhkan nomor antrean (`PRD.md:249`, komit `ea64485`), bukan menghapus grid slot.
- Opsi & trade-off:
  1. Antrean berjalan saja — paling sederhana, tapi jam kunjungan tidak terjamin dan kapasitas per jam hilang.
  2. Slot waktu saja — jam pasti, tapi konsep "nomor giliran" dan SmartWait kehilangan arti.
  3. Keduanya (kondisi saat ini) — jam reservasi teruktur dan giliran tetap ada; biayanya dua konsep yang harus dijelaskan ke pengguna.
- **USULAN**: pilih opsi 3 dan tulis terus terang di PRD, jangan diamkan "pilih satu saja" karena sudah tidak sesuai kode.
- Teks usulan kolom PRD (disimpan di sini saja): `Antrean berjalan + grid slot 30 menit. Reservasi memesan slot, sistem memberi nomor antrean; SmartWait memakai nomor antrean. Tidak ada opsi walk-in (lihat K4).`

### K2 — Bagaimana kapasitas dan petugas aktif disimpan?

- Pertanyaan PRD apa adanya (`PRD.md:50`): `| K2 | Bagaimana kapasitas dan petugas aktif disimpan? | Simpan sebagai angka di data bisnis | [isi: setuju atau pakai tabel terpisah] |`
- Status: **SUDAH DIPUTUSKAN (de facto — lewat implementasi; kolom PRD belum diisi)**.
- Bukti: `Business.java:53-57` memetakan `active_staff_count` dan `max_capacity` sebagai kolom di entity `businesses`; `database/queuecare.sql:75,79` mendefinisikan keduanya sebagai `int NOT NULL`; `PRD.md:190-191` masih menandainya "(usulan, K2)". Kapasitas dipakai nyata di `ReservationService.java:105,171`; petugas dipakai SmartWait (`PRD.md:253`).
- Opsi & trade-off: (1) angka di `businesses` —sedikit tabel, satu nilai global per bisnis (terpilih); (2) tabel terpisah per hari/jam — fleksibel, tapi migrasi + CRUD baru.
- **USULAN**: sahkan opsi 1, cukup koreksi label "(usulan, K2)" di `PRD.md:190-191`.
- Teks usulan kolom PRD: `Setuju — dua angka (active_staff_count, max_capacity) disimpan di tabel businesses, diatur Owner di halaman Kapasitas.`

### K3 — Owner daftar sendiri atau dibuatkan Administrator?

- Pertanyaan PRD apa adanya (`PRD.md:51`): `| K3 | Owner daftar sendiri atau dibuatkan Administrator? | Owner daftar, bisnis menunggu verifikasi | [isi: keputusan kelompok] |`
- Status: **SUDAH DIPUTUSKAN, tetapi PRD bertentangan dengan sendirinya**.
- Bukti:
  - `PRD.md:257` (catatan Fase 7b, komit `d4c59d6`): "Pendaftaran Owner mandiri tidak dibuat, akun Owner dibuat Admin."
  - `AuthService.java:25-37` — `register()` selalu membuat `Role.CUSTOMER` (baris 35); tidak ada jalur register OWNER.
  - `PRD.md:56` (catatan revisi Fase 2) masih bertulis: "K3 tetap memakai usulan awal, yaitu Owner mendaftar dan membuat bisnisnya sendiri…". Ini **sudah kedaluwarsa**.
  - Admin bisa membuat akun OWNER lewat `AdminUserService.java:47-56`.
- Opsi & trade-off: (1) Owner dibuat Admin — kontrol akses awal ketat, tapi Admin jadi bottleneck (kondisi saat ini); (2) Owner daftar mandiri + verifikasi bisnis — otonom, tapi butuh alur registrasi role baru dan approval.
- **USULAN**: sahkan opsi 1 dan perbaiki `PRD.md:56` menjadi catatan riwayat, bukan keputusan aktif.
- Teks usulan kolom PRD: `Owner dibuat oleh Administrator (Fase 7b). Registrasi publik hanya membuat Customer. Bisnis baru menunggu verifikasi Admin. Catatan Fase 2 di baris 56 sudah tidak berlaku.`

### K4 — Perlu dukung walk-in?

- Pertanyaan PRD apa adanya (`PRD.md:52`): `| K4 | Perlu dukung walk-in? | Tidak di versi pertama | [isi: ya / tidak] |`
- Status: **SUDAH DIPUTUSKAN (tertulis)**: `PRD.md:43` memasukkan "Pelanggan walk-in tanpa reservasi" ke "Di luar cakupan (versi pertama)". Tidak ada implementasi walk-in — `grep -i walk-in` pada `src/main/java` dan template menghasilkan 0 kecocokan.
- Opsi & trade-off: (1) tidak — sesuai ruang lingkup, antrean tetap konsisten (pilihan saat ini); (2) ya — butuh entity/alamat antrean baru, kode kasir, dan aturan nomor campuran.
- **USULAN**: sahkan opsi 1 untuk versi pertama.
- Teks usulan kolom PRD: `Tidak. Walk-in sengaja di luar cakupan versi pertama (lihat bagian Di luar cakupan).`

### K5 — Kategori disimpan sebagai teks atau tabel `categories`?

- Pertanyaan PRD apa adanya (`PRD.md:53`): `| K5 | Kategori disimpan sebagai teks atau tabel categories? | Tabel categories, karena Admin mengelola kategori (R12) | [isi: keputusan kelompok] |`
- Status: **SUDAH DIPUTUSKAN (de facto — lewat implementasi; PRD masih berlabel usulan)**.
- Bukti:
  - `Business.java:36-38`: `@ManyToOne @JoinColumn(name = "category_id", nullable = false) private Category category;`
  - `database/queuecare.sql:111` (tabel `categories`), `:84,89` (`category_id` FK NOT NULL).
  - Admin mengelola kategori: `docs/pengujian.md:44` LOLOS; entity `Category.java` dan `CategoryService.java` ada.
  - `PRD.md:185` masih menulis kolom `category VARCHAR(100)` "(diganti `category_id` bila K5 memilih tabel)" — kini sudah diganti di kode.
- Opsi & trade-off: (1) tabel `categories` — satu sumber kebenaran, bisa dikelola Admin (kondisi saat ini); (2) teks bebas — lebih sedikit tabel, tapi filter tidak konsisten dan R12 sulit.
- **USULAN**: sahkan opsi 1 dan perbarui `PRD.md:171,185` agar tidak lagi berlabel usulan.
- Teks usulan kolom PRD: `Tabel categories. businesses.category_id adalah FK NOT NULL; Admin mengelola kategori lewat R12.`

### K6 — Aturan batas waktu pembatalan

- Pertanyaan PRD apa adanya (`PRD.md:54`): `| K6 | Aturan batas waktu pembatalan | Boleh sebelum status IN_SERVICE | [isi: misal minimal berapa jam sebelum jadwal] |`
- Status: **SEBAGIAN**. Aturan berbasis status sudah jalan dan diuji; tidak ada batas jam, dan aturannya lebih ketat dari usulan awal untuk Customer.
- Bukti:
  - Customer: `ReservationService.java:248-267` — `batalkan()` menolak bila `queue.getStatus() != QueueStatus.WAITING` (baris 259). Teruji `docs/pengujian.md:68-69` (`f4c.sh`).
  - Owner: `QueueService.java:40-43` mengizinkan `WAITING→CANCELLED` dan `CALLED→CANCELLED`; `IN_SERVICE` hanya bisa `COMPLETED`.
  - `PRD.md:25` (R10) hanya berkata "sebelum antrean masuk tahap pelayanan", tidak menyebut jam.
  - Tidak ada konstanta batas jam di kode; satu-satunya batas waktu adalah jendela reservasi 14 hari (`ReservationService.java:36,146`).
- Opsi & trade-off: (1) status saja (kondisi saat ini) — sederhana, tapi pelanggan bisa membatalkan 5 menit sebelum giliran; (2) tambah minimal N jam — lebih adil untuk Owner, tapi butuh keputusan N dan penyesuaian UI + validasi + uji.
- **USULAN**: sahkan status quo dulu; bila kelompok ingin batas jam, putuskan N di pertemuan ini (lihat pertanyaan 6).
- Teks usulan kolom PRD: `Customer hanya dapat membatalkan saat antrean WAITING; Owner dapat membatalkan sampai CALLED. Tidak ada batas minimal jam (versi pertama).`

---

## 3. D1–D3 — gejala, perilaku nyata, dampak, opsi, usulan

### D1 — Aturan "Admin aktif terakhir tidak boleh dinonaktifkan/diturunkan" tidak terbukti lewat UI

- Status: **SUDAH DIPUTUSKAN (terima dan dokumentasikan)** — `docs/daftar-bug.md:31`; aturan dicatat di `PRD.md:257` dan diuji di `AdminUserServiceTest.java:95-116`.
- Perilaku nyata (dibuktikan dari kode):
  - `AdminUserService.java:83-85`: menonaktifkan akun sendiri ditolak lebih dulu.
  - `AdminUserService.java:114-117`: mengubah role akun sendiri ditolak lebih dulu.
  - `AdminUserService.java:86-89` dan `:121-126`: aturan "Admin aktif terakhir" memakai `countByRoleAndActiveTrue(Role.ADMIN) <= 1`. Hitungan itu **memasukkan pelaku sendiri**. Karena hanya Admin yang bisa membuka `/admin/**` (`SecurityConfig.java:36`), bila tersisa satu Admin aktif, targetnya pasti pelaku — sehingga pemeriksaan "akun sendiri" selalu menang dan pesan "Admin aktif terakhir…" tidak pernah muncul di UI.
  - Bukti aturan itu nyata, bukan mati: `AdminUserServiceTest.java:95-116` menyuntik `pelaku` Admin **lain** (`id=2`) dengan mock dan membuktikan aturan menolak. Jadi jaring pengaman bekerja di service, hanya tidak terjangkau lewat antarmuka.
- Dampak: rendah. Tidak ada celah keamanan — platform tetap tidak bisa kehilangan Admin terakhir. Yang hilang hanya **bukti UI** untuk auditee/penguji.
- Opsi: (a) terima + dokumentasikan (0 kode); (b) ubah hitungan agar pelaku tidak dihitung, lalu tambah uji UI — mengubah aturan bisnis, perlu persetujuan; (c) tunda sampai ada waktu.
- Perkiraan kerja: (a) 0; (b) kecil–sedang, ≈1 metode service + 1–2 tes + 1 skenario UI (2–3 jam).
- Perlu ubah kode Java? **Ya, hanya jika opsi (b)** — `AdminUserService.java` + test.
- **USULAN**: opsi (a). Simpan aturan sebagai jaring pengaman, catat keterbatasan UI di dokumen pengujian. Jangan ubah aturan hanya agar pesannya muncul.

### D2 — Mengubah email profil memaksa login ulang

- Status: **SUDAH DIPUTUSKAN (terima)** — `docs/daftar-bug.md:32` dan `PRD.md:257` mencatat pilihan ini.
- Perilaku nyata: `ProfileController.java:58` mendeteksi perubahan email, `:60` memanggil `AuthService.ubahProfil`, `:66-72` melakukan logout paksa dan redirect `/login?profil=1`. `AuthService.java:41-50` menyimpan email baru. Email dipakai sebagai nama login, sehingga `UserDetails` lama tidak cocok lagi. Teruji `docs/pengujian.md:22-24`.
- Dampak: gangguan kecil (sekali login ulang), tapi jujur dan aman — sesi lama tidak dipakai dengan identitas baru.
- Opsi: (a) terima + beri pesan di form; (b) bangun ulang `UserDetails`/`SecurityContext` setelah email berubah agar sesi tetap; (c) tunda.
- Perkiraan kerja: (a) ≈15 menit (satu baris teks bantuan); (b) sedang, ≈2–4 jam + uji sesi (risiko auth regression).
- Perlu ubah kode Java? **Ya, hanya jika opsi (b)** — `ProfileController.java` / `AuthService.java`.
- **USULAN**: opsi (a), tambahkan kalimat "Email dipakai untuk masuk; kamu akan diminta masuk lagi" di `templates/profile.html`.
- Catatan: `AkunNonaktifFilter` juga memutus sesi dengan pola serupa (`docs/pengujian.md:206`); ini bukan bug D2, hanya catatan teknis.

### D3 — Reset password oleh Admin tidak mewajibkan password baru berbeda dari yang lama

- Status: **SEBAGIAN**. Kode sengaja membedakan dua jalur; belum ada catatan keputusan kelompok tertulis yang menyetujui perbedaan ini (`docs/daftar-bug.md:33` hanya memberi alasan, `PRD.md:257` tidak membahas aturan password Admin).
- Perilaku nyata:
  - `AdminUserService.java:70-75` — reset oleh Admin hanya mengecek panjang ≥ 8, lalu `passwordEncoder.encode(...)`. Tidak ada `matches()` terhadap hash lama.
  - `AuthService.java:52-64` — ganti password oleh pemilik akun **menolak** password sama dengan lama (`:60-62`). Teruji `docs/pengujian.md:28`.
  - Jadi dua aturan berbeda memang berlaku; bukan bug yang tidak disengaja.
- Dampak: sedang. Admin bisa mengembalikan kata sandi ke nilai lama tanpa terdeteksi sebagai "tidak berubah"; bila akun sedang dalam masa kompromi, ini melemahkan pemaksaan rotasi. Di sisi lain, Admin reset memang bukan alur "pemilik membuktikan password lama".
- Opsi: (a) samakan dengan aturan self-service (tambah cek `matches`); (b) terima + dokumentasikan alasan; (c) tunda.
- Perkiraan kerja: (a) kecil — satu kondisi di `AdminUserService.ubah` + satu unit test (±30–60 menit); (b) 0; (c) 0.
- Perlu ubah kode Java? **Ya, hanya jika opsi (a)** — `AdminUserService.java` + `AdminUserServiceTest.java`.
- **USULAN**: opsi (a), karena biayanya satu baris dan menghilangkan inkonsistensi aturan. Tetapi **putuskan dulu di kelompok** — ini mengubah perilaku reset yang sudah diuji (`docs/pengujian.md:46`).

---

## 4. Ketidaksesuaian PRD vs implementasi nyata

| # | Ketidaksesuaian | Bukti |
|---|---|---|
| M1 | K1 meminta "pilih satu saja", kode memakai slot waktu **dan** antrean berjalan | `PRD.md:49` vs `ReservationService.java:35,84-109,186-192` |
| M2 | K3 di `PRD.md:56` masih "Owner mendaftar sendiri", tapi Fase 7b memutuskan Owner dibuat Admin | `PRD.md:56` vs `PRD.md:257`, `AuthService.java:35` |
| M3 | Skema `businesses.category VARCHAR(100)` masih ditulis sebagai usulan, kode sudah FK `category_id` | `PRD.md:171,185` vs `Business.java:36-38`, `database/queuecare.sql:84,89` |
| M4 | Catatan "karena `queues` tidak punya `business_id`" sudah kedaluwarsa; tabel `queues` di PRD sendiri mencantumkan `business_id`, dan Fase 4 menambah constraint unik | `PRD.md:237,247` vs `PRD.md:249`, `database/queuecare.sql:150` |
| M5 | Usulan K6 "boleh sebelum IN_SERVICE" lebih longgar dari kode: Customer hanya WAITING, Owner s.d. CALLED; tidak ada batas jam | `PRD.md:54` vs `ReservationService.java:259`, `QueueService.java:41-42` |
| M6 | User flow Fase 4.1 menjanjikan "diarahkan ke login lalu kembali", tapi login selalu redirect ke dashboard role (SavedRequest diabaikan) | `PRD.md:103` vs `SecurityConfig.java:77-86`; bukti browser `docs/impeccable/customer-critique/kritik-customer.md:85` |
| M7 | Placeholder `PRD.md:77` "status mana yang dihitung" sudah dijawab Fase 6, tapi tidak dicoret | `PRD.md:77` vs `PRD.md:253` (WAITING, CALLED, IN_SERVICE) |
| M8 | Jalur NO_SHOW→CANCELLED di Fase 5 masih berlabel "usulan, perlu persetujuan kelompok", padahal sudah diimplementasikan dan diuji | `PRD.md:251` vs `QueueService.java:178-179`, `docs/pengujian.md:81` |
| M9 | Placeholder di luar K1–K6 masih tersisa: deadline, hosting, dan metode hosting | `PRD.md:35,36,288` |

Catatan: `PRD.md` sengaja **tidak diubah** untuk M1–M9; semua koreksi di atas baru usulan.

---

## 5. Pekerjaan tersisa yang sudah diketahui

Urutan: P1 dulu. Label **K** = butuh keputusan kelompok; **T** = murni teknis (bisa dikerjakan tanpa keputusan baru).

| Prioritas | Pekerjaan | Ukuran | Label | Bukti sumber |
|---|---|---|---|---|
| P1 | SavedRequest pasca-login: klik Reservasi saat anonim → login → kembali ke halaman bisnis/layanan, bukan `/customer` | Sedang (Java: `SecurityConfig` successHandler + `RequestCache` + 1 tes) | **K** — tujuan kembali perlu disepakati; **T** untuk implementasinya | `PRD.md:103`; `SecurityConfig.java:77-86`; `kritik-customer.md:49,85`; `DESIGN.md:642` |
| P1 | Konfirmasi jalur NO_SHOW→CANCELLED (sudah jalan, belum ada tanda tangan kelompok) | 0 kode untuk mengiyakan | **K** | `PRD.md:251`; `QueueService.java:178-179`; `docs/pengujian.md:81` |
| P1 | Dua temuan Sonar: `java:S6437` password demo di seed dan `java:S3752` di `ErrorController` | Kecil (S3752: 1 baris; S6437: kecil sedang + docs akun demo) | **K** — accepted vs pindah ke env; dua opsi dan trade-off sudah ditulis | `docs/pengujian.md:286-287,348-353`; `DummyDataSeeder.java:231`; `ErrorController.java:10` |
| P2 | Guardrail form reservasi: tanggal dd/MM, tombol Batal/kembali, `required` select jam, `min` tanggal, salinan durasi slot | Kecil–sedang (template + sedikit validasi) | **T** | `kritik-customer.md:51`; `templates/reservation/create.html:24,40-44` (belum ada `min`/`required`/Batal) |
| P2 | Uji konkurensi **lintas bisnis** — tes saat ini hanya satu bisnis | Kecil–sedang (test only) | **T** | `ReservationConcurrencyTest.java:68-75`; `docs/pengujian.md:221` memakai bisnis 20 saja |
| P2 | D3 — samakan aturan reset password Admin (lihat §3) | Kecil (1 kondisi + 1 tes) | **K** | `AdminUserService.java:70-75`; `AuthService.java:60-62` |
| P2 | Statistik customer yang **nyata** — kartu angka 0 sudah dihapus, jadi bukan bug lagi; yang tersisa ialah metrik pengganti | Sedang (service + model + template) | **K** — metrik apa yang ingin ditampilkan | komit `e4790e2`; `templates/customer/index.html:19-24`; `kritik-customer.md:50`; `DESIGN.md:638` |
| P2 | Risiko double-book: okupansi slot bertumpang tindih untuk layanan > 30 menit | Sedang (logika slot + uji) | **K** — mengubah aturan kapasitas | `kritik-customer.md:50` |
| P3 | Daftar P2/P3 desain: dashboard S7–S15; customer hierarki aksi, SmartWait tanpa dash, alamat/telepon hari-H, aria-live, format tanggal, a11y kecil; landing P2 | Kecil per item, tapi banyak | **T** untuk sebagian besar; **K** untuk prioritas dan sufiks "(Data dummy)" | `kritik-dashboard.md:50-61`; `kritik-customer.md:51-52`; `kritik-landing.md:41-43` |
| P3 | Tabel riwayat perubahan status opsional (`reservation_status_histories`, `queue_status_histories`) | Sedang (entity + migrasi) | **K** | `PRD.md:259` |

Sudah dikerjakan dan **tidak perlu masuk daftar lagi**: perbaikan konkurensi `2c197bb` (uji ulang `docs/pengujian.md:294-321`, 25 tes), statistik 0 palsu di `/customer` (komit `e4790e2`), S1–S15 P0/P1 dashboard dan customer (komit `a048f2a`–`a9e281b`, `f762bb1`–`2cab20a`).

---

## 6. Pertanyaan untuk kelompok (maksimal 10)

1. K1 — Tetap "antrean berjalan + slot 30 menit", atau ingin menghapus grid slot? (opsi: tetap / hapus slot)
2. K2 — Setuju kapasitas dan petugas tetap angka di tabel `businesses`? (ya/tidak)
3. K3 — Konfirmasi Owner dibuat Admin, dan setuju `PRD.md:56` diperbaiki? (ya/tidak)
4. K4 — Walk-in tetap di luar cakupan versi pertama? (ya/tidak)
5. K5 — Setuju tabel `categories` + FK `category_id`, dan `PRD.md:171,185` diperbarui? (ya/tidak)
6. K6 — Aturan batal: (a) status quo tanpa batas jam, (b) tambah minimal N jam — jika (b), N = berapa?
7. D1–D2 — Terima keduanya sebagai perilaku terdokumentasi, atau perbaiki salah satu? (opsi: terima dua / perbaiki D1 / perbaiki D2)
8. D3 — Samakan reset password Admin dengan aturan password pemilik akun, atau terima perbedaan? (opsi)
9. SavedRequest pasca-login — setelah login, kembali ke (a) detail bisnis, (b) form reservasi, (c) dashboard role? (opsi)
10. Sonar dan NO_SHOW — password demo: (a) Accepted di Sonar / (b) pindah ke environment variable? Dan jalur NO_SHOW→CANCELLED: disetujui / ditunda? (opsi)

---

## 7. Batas pembuktian

- Semua perilaku di dokumen ini dibuktikan dengan membaca kode/konfigurasi yang dikutip, atau dengan catatan pengujian yang sudah ada di repo. Tidak ada aplikasi yang dijalankan untuk dokumen ini, dan tidak ada DB yang diubah.
- "Belum terbukti" dipakai bila hanya ada niat/usulan tanpa jejak: tidak ada klaim bahwa keputusan kelompok sudah diambil untuk K1 dan K6 secara eksplisit, dan tidak ada klaim persetujuan kelompok untuk D3, M8, atau metrik statistik customer.
- Skrip pengujian bersejarah (`f4.sh`, `f7b.sh`, dll.) tidak dijalankan ulang; angka yang dikutip berasal dari `docs/pengujian.md`, bukan hasil baru. Satu-satunya eksekusi baru ialah `mvn.cmd -B test` → 25/25 dan `SELECT COUNT(*)` read-only pada DB demo.
- Dokumen ini tidak mengubah `PRD.md`, kode Java, template, atau data.
