# Kritik dashboard QueueCare (Owner + Admin) — sintesis dual-agent

Method: dual-agent (A: sa-0-0adddf51 review desain · B: sa-0-c1d5eda7 detektor + bukti browser).
Tanggal: 19 November 2026. Branch desain-impeccable, baseline b21c490. Mode permukaan: Operate.
Cakupan: 22 route (14 owner + 8 admin), 18 template + fragments/layout.html, viewport 375×900 dan 1280×900.
Bukti mentah: scratch/dash-a (44 screenshot review) dan scratch/dash-b (44 screenshot + dashb-hasil.json + detector.json), di luar repo.

## Daftar halaman yang dikritik

Owner: /owner (index), /owner/businesses, /owner/business, /owner/business/baru, /owner/business/{id} (business-form), /owner/services, /owner/services/baru, /owner/services/{id} (service-form), /owner/schedules, /owner/schedules/baru, /owner/schedules/{id} (schedule-form), /owner/capacity, /owner/queues, /owner/reports.
Admin: /admin (index), /admin/users, /admin/users/tambah, /admin/users/{id}/ubah (user-form), /admin/categories, /admin/businesses, /admin/reservations, /admin/reports.

## Ringkasan terukur (Assessment B)

- 44/44 entri HTTP 200; 0 overflow horizontal dokumen; tepat 1 h1 per halaman; 0 placeholder gagal kontras.
- Kontras gagal: 20 instance (10 per viewport) — 18 instance lencana--completed 4,289:1 (teks #2E7D4F di #DFF1E5; teks "ACTIVE", "COMPLETED", "Aktif", "Buka") di 9 route; 2 instance a.sidebar__ganti "Ganti bisnis" 1,373:1 (#0E5A66 di #0A444D) di /owner.
- Target sentuh <44px di 375px: 13 instance link .sidebar__menu (38,4px). Sisanya (200 instance) hanya di 1280px — disengaja dari perbaikan sebelumnya (desktop pointer, WCAG AA 24px lolos).
- Tabel perlu scroll internal di 375px (tercontain, bukan overflow dokumen): /admin/reservations (817px), /admin/users (719px), /admin/businesses (519px), /owner/services (517px), /owner/schedules (461px), /owner/businesses (430px).
- Detektor statis: exit 2, 34 findings (15 primary flat-type-hierarchy, 19 advisory design-system-color), semua line=0. Cross-check computed style rendered: TIDAK TERKONFIRMASI — h1 terukur 40/28/20px (bukan 16px seragam), warna teks dari token (bukan hitam). Konsisten dengan artefak scan statis Thymeleaf (CSS eksternal tidak diselesaikan engine). Ditandai artefak, bukan defect; tidak ditindaklanjuti.

## Skor heuristik Nielsen (Assessment A, kelompok dashboard)

| # | Heuristik | Skor | Masalah kunci |
|---|---|---:|---|
| 1 | Visibility of system status | 2 | Antrean owner tanpa ringkasan/auto-refresh; "Rentang sampai" kosong di laporan admin; cakupan statistik ambigu |
| 2 | Match system / real world | 2 | Enum kapital (IN_SERVICE, NO_SHOW, PENDING) bercampur "Menunggu"; tanggal ISO vs dd/MM |
| 3 | User control and freedom | 2 | Hapus jadwal/kategori & ubah status bisnis tanpa konfirmasi; form bisnis tanpa Batal |
| 4 | Consistency and standards | 1 | Sidebar hanya di 2 dari 22 halaman; .tabel__aksi/.pencarian dipakai separuh halaman; semantik lencana dipinjam |
| 5 | Error prevention | 2 | Aksi destruktif tanpa konfirmasi; role default ADMIN; bantuan field hanya pasca-galat; konteks bisnis hilang |
| 6 | Recognition rather than recall | 2 | Menu seksi hilang di subhalaman; nama bisnis aktif tak tampil di services/schedules |
| 7 | Flexibility and efficiency | 2 | Tanpa filter status antrean; reload manual; aksi bertumpuk; kolom aksi tersembunyi di HP |
| 8 | Aesthetic and minimalist | 3 | Bersih berbasis token; dikurangi panel marketing di form owner dan clipping grid |
| 9 | Error recovery | 3 | Pesan galat per field + role=alert baik; flash 12px mudah terlewat |
| 10 | Help and documentation | 2 | Empty state membimbing; tanpa penjelasan dampak kapasitas/petugas ke SmartWait |
| Total | | 21/40 | Acceptable |

## Masalah bersama (shared) dan keputusan perbaikan

Diperbaiki pada tugas ini (P0/P1), urut prioritas:

1. S1 (P0) — Grid .dash blowout: .dash__isi min-width:auto + min-content tabel memaksa isi 584px dalam wadah 343px di /admin@375; overflow:hidden .dash membuat 2 kartu statistik dan 2 kolom tabel hilang permanen tanpa scroll (juga tablet 721–865px). Fix: .dash__isi { min-width: 0; } (satu baris CSS).
2. S5 (P1) — Kontras & fokus: sidebar__ganti 1,373:1 → --warna-primer-lembut (9,08:1); lencana--completed 4,289:1 → teks --warna-teks di latar lembut (13,30:1, pola dark-on-soft seperti lencana--in-service, tanpa palet baru); outline .field__input:focus #DDEFF0 1,19:1 → --warna-primer; :focus-visible amber 1,89:1 di putih → cincin pendamping --warna-primer-gelap (amber dipertahankan, kontras cincin ≥3:1).
3. Touch sidebar (P1, temuan B) — link .sidebar__menu 38,4px di HP → padding diperbesar ≥44px pada @media 720px.
4. S2 (P1) — Navigasi buntu: sidebar hanya di halaman index; navbar "Beranda" menuju landing publik. Fix minimal: link "Dashboard" di navbar fragment dengan sec:authorize per role (OWNER → /owner, ADMIN → /admin). Ekstraksi sidebar penuh ke fragment = usulan (menulis ulang layout 18 halaman, berisiko regresi).
5. S16 (P1) — Konteks bisnis aktif hilang di /owner/services, /owner/schedules, dan form-formnya (owner demo punya 3 bisnis; risiko salah kelola). Fix: baris konteks "Bisnis: {nama} · Ganti bisnis" mengikuti pola yang sudah ada di queues/reports/capacity.
6. S4 (P1) — Tabel HP: kolom Aksi di luar layar tanpa affordance; kode reservasi patah 3 baris, harga patah 2 baris. Fix: white-space nowrap pada sel tabel + affordance scroll (bayangan tepi CSS) pada .tabel-bungkus. Kolom aksi sticky = usulan (butuh markup per tabel).
7. S6 (P1) — Aksi destruktif tanpa konfirmasi: Hapus jadwal (/owner/schedules), Hapus kategori (/admin/categories), ubah status bisnis (/admin/businesses). Fix: salin pola dialog.modal per baris yang sudah ada di owner/queues dan admin/users (aturan B10 tetap). Plus keamanan: select Role di /admin/users/tambah default ADMIN (enum pertama) → tambah option "Pilih role" kosong + required (validasi backend menolak null).
8. S3 (P1) — Label enum mentah bercampur "Menunggu". Fix template-only di halaman dashboard: antrean (Dipanggil/Dilayani/Selesai/Tidak hadir/Dibatalkan), bisnis (Aktif/Nonaktif/Menunggu verifikasi), reservasi (Dikonfirmasi/Selesai/Dibatalkan), role (Administrator/Owner/Pelanggan). Enum Java, kelas lencana, dan nilai option tidak berubah. Halaman customer queue/show + polling antrean.js di luar cakupan tugas ini (konsistensi lintas role dicatat sebagai usulan).

Dicatat sebagai usulan (P2/P3, tidak dikerjakan tugas ini):

- S7 (P2): sel aksi tanpa .tabel__aksi di services/schedules/categories/businesses → tombol bertumpuk; bungkus dengan komponen yang sudah ada.
- S8 (P2): form filter bertumpuk full-width di queues/reports/admin-reservations → pakai pola .pencarian (admin/users sudah benar).
- S9 (P2): semantik lencana dipinjam — coretan CANCELLED untuk Nonaktif/Libur/INACTIVE; PENDING sama abu dengan WAITING (tugas utama admin kurang menonjol) → kelas nonaktif tanpa coretan, PENDING ke amber.
- S10 (P2): tanggal ISO vs dd/MM/yyyy dalam satu halaman (owner/reports, admin/reports) → #temporals.format.
- S11 (P3): kolom angka rata kiri dengan jurang lebar → text-align right pada .tabel__angka.
- S12 (P3): flash role=alert berupa pil 12px → perbesar khusus p[role=alert].lencana.
- S13 (P2): bantuan field hanya muncul pasca-galat, tanpa penanda wajib, h1 form 20px (kartu__judul) vs halaman lain 40px; panel marketing auth di form bisnis owner (mismatch Operate vs Persuade); tanpa tombol Batal di form bisnis.
- S14 (P2): checkbox terpisah dari labelnya (services/schedules form) → flex + area sentuh.
- S15 (P3): aria-labelledby dialog, aria-label select per baris, scope th, nav pagination, skip-link.
- Per halaman: statistik ambigu cakupannya (owner/index, admin/reports "Rentang sampai" kosong), dashboard sparse tanpa jalan pintas, kartu profil bisnis tanpa alamat/telepon/kapasitas, baris terpilih tanpa highlight, mode edit kategori tanpa penanda, header kolom "Reservasi"/"Antrean" ambigu, angka statistik tanpa link, harga formatInteger vs step 0.01, auto-refresh antrean owner (butuh JS), filter status bisnis (butuh backend).

## Persona red flags (ringkas)

- Alex (power user): navigasi buntu antar subhalaman; tanpa filter status antrean; aksi tabel bertumpuk.
- Sam (aksesibilitas): "Ganti bisnis" 1,37:1; outline fokus 1,19:1 dan amber 1,89:1 (gagal 3:1); select ubah-status tanpa label; dialog tanpa nama.
- Casey (HP satu tangan): dashboard admin terklip tanpa scroll; kolom Aksi di luar layar; kode reservasi patah 3 baris; sidebar memakan ~45% layar pertama.
- Owner sibuk (PRODUCT.md): nama bisnis aktif tidak tampil di services/schedules → risiko salah kelola; panel marketing di form operasional membuang waktu.

## Kekuatan yang dipertahankan

- Filter GET bookmarkable + paginasi; dialog konfirmasi + tombol Kembali di antrean/pengguna (pola B10).
- Empty state membimbing langkah pertama; label for/id dan role=alert sudah benar; konteks bisnis di queues/reports/capacity adalah praktik baik yang diperluas.
- Sistem token konsisten, tanpa dekorasi berlebih; hierarki angka statistik jelas.

Questions skipped: pengguna sudah menetapkan arah dan scope lewat tugas (perbaiki prioritas tinggi, sisanya catat sebagai usulan; hanya template/CSS; tanpa push/merge).