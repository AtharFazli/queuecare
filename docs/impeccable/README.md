# Persiapan Impeccable QueueCare

## Hasil

- Branch: `desain-impeccable`; titik awal `72aa9ce`.
- Commit cadangan: `ddc8d2a`, hanya `docs/DESIGN-sebelum-impeccable.md`.
- PRODUCT.md ditunjukkan penuh sebelum disimpan. Pengguna menjawab “Setujui draf dan lanjut”. Isi disimpan sesuai draf itu.
- DESIGN.md ditambah frontmatter dan delapan bagian canonical. Isi lama tetap identik byte per byte, termasuk komponen, aturan, dan catatan per fase. Daftar judul: `judul-sebelum-sesudah.json`, 29 lama tetap utuh; menjadi 37 dengan tambahan delapan bagian.
- Seluruh 42 deklarasi token direkam: 17 warna, dua stack font, enam ukuran teks, tujuh jarak, empat radius, tiga bayangan, tiga ukuran layout. Bayangan direkam sebagai komentar frontmatter dan metadata pendamping karena schema tidak menerima grup shadows tingkat atas. Ukuran layout masuk component property dengan komentar yang menjelaskan max-width dan pengecualian HP.
- `.impeccable/design.json` berisi metadata bayangan, breakpoint 720px, dan 10 preview dari komponen yang ada. Tidak ada warna ramp sintetis atau metafora merek baru.
- Tidak ada perubahan Java, template, CSS, JavaScript, database sumber, atau scripts/pengujian/. Perbaikan B10 dan urutan pemuatan CSS tetap identik.

## Cara pemasangan dan penyimpangan

Dokumentasi dibaca: https://impeccable.style/docs/, https://impeccable.style/docs/context, https://impeccable.style/docs/detector, panduan getting-started, init, document, dan spesifikasi DESIGN.md.

Prasyarat tersedia: Node.js v22.23.2, npm/npx 12.2.1, Git, sh, JDK 21.0.13, Maven 3.9.16. Tidak memasang runtime atau dependensi aplikasi baru.

Perintah pertama persis permintaan: `npx impeccable install`. CLI npx memakai paket 4.9.1. Pada sesi tanpa stdin interaktif, installer langsung memilih default global dan semua harness terdeteksi. Akibatnya installer melaporkan pembaruan skill global di .claude, .gemini, serta direktori skill Hermes aktif; menulis agent Claude, hook Claude global, plugin Hermes global, dan settings Gemini proyek. Ini penyimpangan scope pemasangan, bukan perubahan aplikasi. Tidak mengklaim semua file global bisa dipulihkan karena salinan sebelumnya tidak tersedia.

Pemasangan proyek kemudian memakai opsi resmi yang dibaca dari `npx impeccable install --help`:

`npx impeccable install --providers=hermes --scope=project --no-hooks --yes`

Hasil: skill 4.9.0 di `.hermes/skills/impeccable`, engine Windows x64 0.1.23. Launcher lokal terbukti bisa menjalankan context dan menampilkan versi `0.1.23+engine`. Hook Gemini proyek yang hanya dibuat installer pertama dihapus. Plugin Hermes global tidak diaktifkan; output context menyatakan hook otomatis tidak aktif pada sesi ini. Status hook global harness lain belum dipulihkan.

Binary engine di `scripts/bin/` diabaikan Git karena artefak unduhan sesuai OS; launcher, checksum, versi, referensi skill tetap disimpan. Tidak ada package.json atau dependency build frontend yang ditambahkan.

`/impeccable init` dan `/impeccable document` adalah alur dalam chat agent, bukan subcommand terminal. Keduanya dijalankan dengan mengikuti reference/init.md dan reference/document.md dari instalasi proyek. Context lokal dijalankan sekali dari root. Init memakai PRD bagian 1–3, DESIGN.md, dan konteks pengguna; tidak menebak keputusan terbuka.

Document memakai mode scan dan penambahan, sesuai instruksi pengguna. Judul lama tidak dinormalisasi atau dipindah; delapan judul canonical ditambahkan berurutan. Langkah metafora merek, ramp warna sintetis, dan keputusan visual baru dilewati karena tugas ini hanya merekam sistem yang ada. Live Mode dan default pembuatan mockup belum disetel karena tidak diminta dan integrasinya dapat menyentuh template; tidak ada injeksi helper. `detect-csp` hanya membaca dan mengembalikan shape null tanpa signals.

web_extract pada lingkungan ini gagal karena backend ddgs hanya mendukung pencarian; halaman dokumentasi dibaca lewat curl sebagai alternatif pengambilan dokumen. Ini bukan alternatif pemasangan.

## Rekaman yang diperjelas

- Menu HP sudah ada di navbar.js dan CSS. Placeholder lama dipertahankan sebagai riwayat, tidak dianggap keputusan yang belum diimplementasikan.
- Pemakaian amber pada sidebar dan fokus sudah ada walaupun kalimat pembatas awal lebih sempit. Tidak dibuat izin dekorasi baru.
- Bobot H1 warisan Bootstrap tidak dijadikan token baru. Permintaan bobot CSS dibedakan dari bobot font yang benar-benar dimuat Google Fonts.
- Bayangan md dan radius sm ada sebagai deklarasi, tetapi belum dipakai pada components.css; tidak dibuat aturan pemakaian karangan.
- Nilai hover bahaya #8F3022 dan transparansi khusus tidak dipromosikan menjadi token merek atau di-ignore.
- Bug dialog tertutup yang tetap tampil ditulis sebagai cacat, bukan pola komponen yang disahkan.

## Detector: hasil dikelompokkan

Perintah:

`npx impeccable detect --json src/main/resources/templates src/main/resources/static/css`

Exit 2: scan selesai dengan primary findings. Target berisi 34 HTML dan dua CSS. Raw output `detector.json`; stderr hanya npm notice, disimpan di `detector-stderr.txt`. Tidak menambah ignore atau memperbaiki kode.

Total 61 hasil: 26 primary warning dan 35 advisory.

### Hierarki tipografi: 26 warning

Rule `flat-type-hierarchy`, pada template. Scanner menyatakan body dan heading sama-sama 16px dengan rasio 1.00:1. Semua hasil ini memakai line 0.

Batas interpretasi: source scanner tidak memproses `th:replace` head atau `th:href` seperti Spring/Thymeleaf. Bukti browser untuk katalog menunjukkan H1 28px pada 375px dan 40px pada 1280px; body 16px. Karena itu hasil raw pada template tersebut tidak membuktikan hierarki aplikasi yang berjalan datar. Tidak mengubah semua halaman berdasarkan hasil ini.

### Warna: 35 advisory

Rule `design-system-color`:

- 34 hasil pada HTML menyebut teks hitam `rgb(0, 0, 0)` di luar palet, line 0. Keterbatasan fragment dan CSS Thymeleaf sama seperti di atas; browser menunjukkan teks body `rgb(20, 38, 43)` sesuai token. Ini belum dianggap drift warna aktual pada semua halaman.
- Satu hasil CSS nyata: `src/main/resources/static/css/components.css:18`, hover tombol bahaya `#8F3022` di luar palet frontmatter. Tetap dibiarkan dan tidak diberi pengecualian.

Tidak ada primary finding lain dari scan sumber ini. Scan ini bukan audit aksesibilitas lengkap atau uji semua halaman setelah render.

## Verifikasi

- `cmd.exe /c 'C:\tools\apache-maven-3.9.16\bin\mvn.cmd -B test'`: BUILD SUCCESS; 19 tes, nol failure/error/skipped. Database pengujian queuecare_test terpisah sesuai konfigurasi yang ada. Ada warning Mockito dynamic agent dan Hibernate saat mencoba drop FK pada tabel uji yang belum ada; hasil tetap sukses. Tidak mengubah test atau konfigurasi untuk menyembunyikannya.
- Perintah `mvn -B test` melalui launcher Bash gagal ClassNotFoundException classworlds. Memakai mvn.cmd resmi instalasi yang sama untuk Windows, bukan memasang Maven baru.
- Browser Edge/Playwright: `/`, `/businesses`, `/login` HTTP 200 di 375px dan 1280px, lebar dokumen sama dengan viewport. Login Admin dan `/admin/users` menjawab 200.
- Urutan stylesheet aktual: Bootstrap 5.3.3, tokens.css, components.css, Google Fonts. Hasil ukuran dan computed styles tersimpan di `ui-verifikasi.json`.
- Dialog Admin: enam dialog tanpa open memiliki display block pada kedua viewport. Klik tombol di 375px terhalang dialog lain. Pemanggilan showModal langsung hanya dipakai untuk mengukur gaya dialog; itu tidak dihitung sebagai klik pengguna yang lolos. Tidak ada perubahan data pengguna atau submit form aktif/nonaktif.
- `git diff --cached --check` melaporkan whitespace pada file vendor Impeccable bawaan (extract.md, harden.md, optimize.md). File vendor tidak diubah; pemeriksaan whitespace pada dokumen dan metadata buatan tahap ini bersih.
- `python docs/impeccable/verifikasi.py`: memastikan badan lama identik, urutan semua judul lama tetap, 42 token cocok sumber, metadata valid, PRODUCT.md ada, dan diff template/CSS/JS/scripts pengujian kosong terhadap 72aa9ce. Memakai PyYAML yang sudah tersedia; tidak memasang dependency baru.

## Yang perlu diputuskan

- Apakah masalah dialog tertutup dibuka sebagai tugas perbaikan terpisah. Aturan B10 tetap dilindungi dalam tahap ini.
- Apakah perubahan global dari default installer hendak ditinjau atau dipulihkan per harness. Jangan menghapus seluruh hook/skill global tanpa membandingkan perubahan sebelumnya.
- Apakah hover #8F3022 kelak dijadikan token atau tetap pengecualian terarah; tidak diputuskan di sini.
- Keputusan final warna/font, logo, gambar bisnis, dan keputusan produk terbuka yang sudah tercatat pada PRODUCT.md tetap milik kelompok.

Tidak menjalankan regresi bisnis lengkap atau memperbaiki temuan UI. Aplikasi merespons, tetapi alur modal HP punya cacat yang sudah ada dan belum diselesaikan.
