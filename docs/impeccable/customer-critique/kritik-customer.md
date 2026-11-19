# Kritik halaman customer + publik QueueCare — sintesis dual-agent

Method: dual-agent (A: sa-0-61b36c3c review desain · B: sa-1-20f9c5f9 detektor + bukti browser + observasi polling).
Tanggal: 19 November 2026. Branch desain-impeccable, baseline 589d03d. Mode: Operate (form/monitoring/riwayat/profil), temuan-browsing (katalog/detail), Persuade (landing, sudah diperbaiki sebelumnya).
Bukti mentah: scratch/cust-a (32 screenshot + probe polling) dan scratch/cust-b (32 screenshot + custb-hasil.json + detector.json + polling.json/polling-25.json), di luar repo.

## Daftar route yang dikritik (16)

Publik: / (home), /login, /register, /businesses, /businesses?kategori=1, /businesses/20.
Customer (dummy1@queuecare.id): /customer, /reservation/create/42, /reservation/success/563, /reservations, /reservations/563, /reservations/560, /queue/553 (WAITING), /queue/555 (IN_SERVICE), /queue/557 (NO_SHOW), /profile.
Template tambahan dibaca: error.html, error/403.html, fragments/layout.html, fragments/contoh.html.

## Ringkasan terukur (Assessment B)

- 32/32 HTTP 200; 0 teks gagal kontras WCAG (semua ≥4,5:1 normal / ≥3:1 besar); 0 overflow dokumen; h1 ada di semua halaman.
- Target sentuh: 0 <44px di 375px (fix sebelumnya terbukti menjalar); 91 instance <44px di 1280px — disengaja (desktop pointer, keputusan fase sebelumnya).
- Tabel /reservations@375: scroll internal 918>341 (nowrap + scroll-shadow fix dashboard bekerja).
- Dialog B10 ada di /reservations/563 dan /560 (konfirmasi batal sudah benar).
- Enum mentah teramati di 7 route: CONFIRMED (success/563, reservations, reservations/563), CANCELLED (reservations, reservations/560), NO_SHOW (reservations/560, queue/557), IN_SERVICE (queue/555), CUSTOMER (profile).
- Polling (observasi 12+25 detik): /queue/553 WAITING aktif (2 request, label Menunggu bertahan); /queue/557 NO_SHOW berhenti setelah 1 fetch pertama; status selain WAITING tetap enum mentah sebelum dan sesudah polling (antrean.js hanya memetakan WAITING).
- Detektor statis: exit 2, 25 findings (11 primary flat-type-hierarchy, 14 advisory design-system-color), semua line=0. Cross-check computed style: TERBANTAHKAN (h1 40px/20px bukan 16px rata; 0 teks rgb(0,0,0) rendered). Artefak scan Thymeleaf — tidak ditindaklanjuti.

## Skor heuristik Nielsen (Assessment A): 22/40 (Acceptable)

| # | Heuristik | Skor | Masalah kunci |
|---|---|---:|---|
| 1 | Visibility of system status | 2 | Teks "sudah final" salah di antrean hidup; dashboard customer 0 palsu; tanpa indikator gagal-fetch |
| 2 | Match system / real world | 2 | Enum CONFIRMED/IN_SERVICE/NO_SHOW/CUSTOMER; tanggal ISO "Slot pada"; copy dev "menyusul di fase berikutnya" |
| 3 | User control and freedom | 2 | Form reservasi tanpa Batal; login membuang konteks asal (SavedRequest); dashboard buntu |
| 4 | Consistency and standards | 2 | Label customer vs dashboard beda bahasa; hierarki tombol terbalik; format tanggal/nomor ganda |
| 5 | Error prevention | 2 | Tanpa min/required klien; aturan password tersembunyi; kapasitas per jam-mulai persis (R6, backend) |
| 6 | Recognition rather than recall | 2 | Navbar customer tanpa tujuan inti; Status di luar viewport HP; alamat hilang saat hari-H |
| 7 | Flexibility and efficiency | 2 | Tanpa filter/paginasi riwayat; 3 hop dari login ke pantau; tanpa salin kode |
| 8 | Aesthetic and minimalist | 3 | Bersih berbasis token; dikurangi footer melayang, dash SmartWait, stub dashboard |
| 9 | Error recovery | 3 | Galat per field jelas; flash 12px; 404 berasumsi bisnis; hubungan NO_SHOW→CANCELLED tak dijelaskan |
| 10 | Help and documentation | 2 | Bantuan hanya pasca-galat; aturan pembatalan tidak dijelaskan di UI |

## Masalah prioritas dan keputusan

Diperbaiki tugas ini (P1, template/CSS + satu perubahan kecil antrean.js yang DISETUJUI USER untuk peta label polling):

1. S2 (P1) — Navigasi customer buntu: navbar hanya Beranda/Profil/Keluar; sidebar /customer dan /profile satu link ke diri sendiri. Fix: navbar fragment + Dashboard (CUSTOMER → /customer), Riwayat (CUSTOMER → /reservations), Katalog (publik → /businesses) mengikuti pola sec:authorize OWNER/ADMIN yang sudah ada; sidebar customer dan profil diisi tujuan nyata.
2. C1/C2/C3 (P1/P2 terkait) — /customer masih stub Fase 1: tiga kartu statistik hardcoded 0 (padahal dummy1 punya 4 reservasi), teks "Katalog bisnis dan reservasi menyusul di fase berikutnya", tanpa CTA ke fitur inti. Fix template: hapus kartu 0 palsu dan copy dev, ganti panel aksi cepat (Cari bisnis, Riwayat reservasi) memakai .tombol/.kosong yang ada; "Menu Customer"→"Menu kamu"; hapus baris peran. Statistik nyata butuh Java (ReportService + model) → usulan.
3. S4/Q1/Q10 (P1) — queue/show menampilkan "Status antrean sudah final." pada antrean WAITING/IN_SERVICE yang hidup (bisaPantau tidak pernah diisi CustomerQueueController; terverifikasi live). Fix template-only: ekspresi status non-final pada kedua cabang (Java beku; duplikasi logika final di template dicatat sebagai keterbatasan, sumber kebenaran tetap service).
4. S1 (P1) — Label enum mentah di 7 route customer. Fix: pemetaan Indonesia persis pola dashboard a9e281b (antrean: Menunggu/Dipanggil/Dilayani/Selesai/Tidak hadir/Dibatalkan; reservasi: Menunggu/Dikonfirmasi/Dibatalkan/Selesai; role: Pelanggan) di reservations/index, reservations/detail (2 badge), reservation/success, queue/show, profile; copy "Customer" di customer/index dan register → Pelanggan/dihapus. Q7 (P3, enabler): data-antrean-url hanya dirender bila non-final sehingga halaman final benar permanen tanpa polling sia-sia. Polling antrean berjalan: perluasan peta LABEL di antrean.js (DISETUJUI USER via clarify; kelas lencana dan STATUS_FINAL tidak berubah).

Dicatat sebagai usulan (tidak dikerjakan):

- S3/D4 (P1, butuh Java) — Login mengabaikan SavedRequest: alur PRD Fase 4.1 "reservasi → login → kembali" putus; mendarat di /customer. Fix: successHandler membaca RequestCache. PRIORITAS USULAN TERTINGGI (di luar scope template/CSS).
- Statistik customer nyata (C2, butuh Java); harga minimum di kartu katalog (B1, butuh Java); nomor antrean di riwayat (H4, butuh Java); filter+paginasi riwayat (H3, butuh Java); max tanggal +14 hari di form (R4, butuh Java); penanda hari ini di tabel jadwal (D3, butuh Java); okupansi slot tumpang tindih layanan >30 menit (R6, butuh Java — risiko double-book, keputusan kelompok); sufiks "(Data dummy)" (S13, keputusan kelompok).
- P2 template/CSS: guardrail form reservasi (R1 tanggal ISO→dd/MM, R2 Batal/kembali, R3 required select jam, R4 min tanggal, R5 copy durasi slot); hierarki aksi (S6/SU2/RD2: Pantau primer pertama, Batal sekunder/bahaya kedua); SmartWait dash "-" saat pesan saja (S5/RD3/Q5 — fix penuh butuh 1 baris JS display toggle, template-only parsial berlimitasi transisi); alamat+tel: di queue/success/detail (Q4/SU3/RD6/D1); posisi disembunyikan saat IN_SERVICE/final (Q9/Q12); aria-live polling (Q3); penjelasan NO_SHOW→CANCELLED (RC2); kolom Status dipindah setelah Kode di riwayat (H2/S7); bantuan statis password/email (PR2/PR3/A3/S10); required+autocomplete profil (PR4).
- P3: footer melayang halaman pendek (S8, CSS body flex min-height 100vh); kaki kartu katalog sejajar (B2, CSS flex column); jumlah hasil + reset filter katalog (B3/K1); flash diperbesar (S9); format tanggal/ nomor seragam (S11/L1/RD4); copy dialog batal + aria-labelledby (RD5); aksi lanjutan pasca-batal (RC3); error copy netral + kode status + CTA dashboard (E1/E2/E3); aria-current selain beranda (E4); skip-link, th scope (S12); indikator terakhir-diperbarui/gagal fetch (Q6, butuh JS); salin kode (SU4, butuh JS); h1 auth vs halaman (A1, tunggu konvensi S13 dashboard).

## Persona red flags (ringkas)

- Jordan (pertama reservasi): klik Reservasi → login → mendarat di stub "belum ada data" tanpa kembali ke layanan (S3+C1); "Status antrean sudah final." padahal menunggu (Q1) — puncak kecemasan; badge CONFIRMED takexplained (SU1).
- Casey (HP satu tangan): fondasi fisik baik (44px, 0 overflow); Status+Detail tersembunyi di scroll riwayat (H2/S7); telepon bukan link tel: (D1); chrome ganda navbar+sidebar memakan layar (C1/PR5).
- Sam (aksesibilitas): kontras/fokus/label for/id baik; polling senyap tanpa aria-live (Q3); enum mentah dibaca apa adanya (S1); dialog tanpa nama (RD5).
- Customer PRODUCT.md: loop inti teknis terpenuhi (kode+nomor+pantau+estimasi), tetapi "rumah" customer tidak membantu memulai kembali (C1/C2/S2/H4).

## Kekuatan yang dipertahankan

- Identitas tiket + nomor antrean kuat di sukses/monitoring; disclaimer SmartWait jujur; "Kamu berikutnya"/"Estimasi belum tersedia" sesuai keputusan Fase 6.
- Dialog B10 di detail reservasi benar (trigger/Kembali/POST); tombol Batal hanya saat WAITING; Pantau hanya non-final (di detail).
- Pola dua langkah Cek slot tanpa JS; slot penuh/lewat disabled berlabel; empty state membimbing; filter GET bookmarkable.
- Fix landing/dashboard sebelumnya terbukti menjalar konsisten (footer, auth, 44px, nowrap+scroll-shadow, Menunggu di tiket contoh, aria-current Beranda).

Questions skipped: pengguna sudah menetapkan arah dan scope lewat tugas + keputusan clarify (satu perubahan kecil antrean.js untuk peta label polling disetujui; Java tetap beku → usulan).