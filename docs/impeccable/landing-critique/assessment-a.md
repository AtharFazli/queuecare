**Verdict design specificity: QueueCare sudah punya ciri produk pada tiket antrean dan SmartWait, tetapi komposisi halaman masih mudah dipakai ulang oleh produk reservasi lain.** Identitas terkuat ada pada nomor amber `07`, kode reservasi, posisi antrean, dan estimasi `30` menit. Struktur hero dua kolom, tiga langkah, lalu pita penjelasan belum mengubah ciri itu menjadi pengalaman mencari bisnis yang langsung bisa dipakai. Prioritas Persuade: dekatkan janji dengan pencarian nyata dan jelaskan kapan akun dibutuhkan.

# Assessment A — kritik desain landing QueueCare

Metode: subagen Assessment A mandiri; pembacaan sumber, inspeksi visual screenshot, dan browser langsung pada tab/konteks baru. Ini keluaran A saja, bukan sintesis A+B atau hasil detector. Detector tidak dijalankan; Assessment B dan JSON detector sebelumnya tidak dibaca.

- Target sumber: `src/main/resources/templates/home.html`.
- URL diperiksa: <http://localhost:8080/>; judul `Beranda · QueueCare`, HTTP 200.
- Acuan Git: branch `desain-impeccable`, HEAD `43b837ab238ed1fa1d0a202ebd64d0e3bfe03835`.
- Mode: **Persuade**. Tujuan landing: Customer menemukan bisnis tanpa login, lalu login saat membuat reservasi; memahami tiket, antrean, dan batas estimasi SmartWait.
- Konteks resmi: `PRODUCT.md` dan `DESIGN.md`. Customer memakai HP; Owner tablet/laptop; Administrator desktop. Landing ini berfokus pada Customer, bukan pengoperasian dashboard tiga role.
- Batas kerja: kritik saja. Hanya laporan ini ditulis. Tidak memperbaiki template, CSS, JavaScript, konfigurasi, atau database; tidak melakukan commit.

## Kesan menyeluruh

Halaman tenang, ringkas, dan mudah dipindai. Headline menyebut manfaat manusiawi: tidak perlu bertanya kapan giliran tiba. Tiket membuat hasil penggunaan lebih konkret dibanding ilustrasi dekoratif. Disclaimer SmartWait menjaga ekspektasi tanpa klaim AI atau ketepatan palsu.

Celah terbesar bukan warna atau kebutuhan dekorasi baru. Pengunjung masih harus meninggalkan landing untuk mulai mencari, sambil diminta mempertimbangkan pendaftaran yang belum diperlukan. Setelah penjelasan terakhir, tidak ada aksi untuk meneruskan minat. Katalog memang dapat dibuka tanpa login; manfaat itu belum dinyatakan di tempat keputusan.

Penilaian ini merupakan evaluasi desain ahli, bukan hasil wawancara, eksperimen konversi, atau pengamatan pelanggan nyata. Nama bisnis dan angka demo tidak menjadi bukti penggunaan atau akurasi produk.

## Bukti visual dan inspeksi langsung

Screenshot yang benar-benar dilihat:

- [Landing HP, seluruh halaman](landing-375.png).
- [Landing desktop, seluruh halaman](landing-1280.png).
- [Viewport HP](landing-375-viewport.png), 375 × 900.
- [Viewport desktop](landing-1280-viewport.png), 1280 × 900.
- [Pengukuran DOM dari parent](dom.json), dibandingkan dengan pengukuran baru melalui Edge.

Browser bawaan gagal: `Cannot connect to Camofox at http://localhost:9377`. Fallback berhasil dengan Playwright terpasang, Chromium channel `msedge`, headless, konteks browser baru dan `new_page()`. Tidak memasang paket, memulai server, menyuntikkan detector, atau menyimpan screenshot tambahan.

| Elemen/bukti | HP 375 × 900 | Desktop 1280 × 900 | Arti untuk kritik |
|---|---|---|---|
| Lebar dokumen / viewport | 375 / 375px | 1280 / 1280px | Tidak ada overflow horizontal pada dua ukuran ini. |
| Tinggi dokumen | 1729px | 1022px | Screenshot full-page bukan tinggi viewport. |
| `.hero__judul` | x16, y113; 343 × 96px | x86, y142,56; 564,89 × 96px | Dua baris; font terhitung 40px, bobot 500 pada keduanya. Tampak kuat, tetapi tidak diasumsikan bobot 800. |
| `.hero__subteks` | 343 × 102,38px | 548,34 × 51,19px | Empat baris pada HP, dua baris desktop. |
| `.hero__tindakan a[href="/businesses"]` | x16, y351,38; 104,44 × 40px | x86, y329,75; 104,44 × 40px | CTA terlihat di viewport pertama; bukan tersembunyi di bawah fold. |
| `.hero__tindakan a[href="/register"]` | x132,44, y351,38; 77,95 × 40px | x202,44, y329,75; 77,95 × 40px | Pendaftaran diletakkan pada keputusan yang sama dengan penjelajahan. |
| `.navbar__tombol-menu` | 59,67 × 32px | Disembunyikan | Menu HP relatif kecil untuk skenario satu tangan. |
| `.hero .tiket` | y458,56; 343 × 159,56px | y147,19; 440 × 159,56px | Bukti bentuk tiket terlihat pada viewport pertama. |
| `.hero .smartwait` | y634,13; 343 × 99,97px | y322,75; 431,31 × 77,58px | Estimasi pertama terlihat utuh sebelum y900. |
| `.sorotan` | y1123,25; tinggi 424,72px | y642,70; tinggi 198,38px | Blok kedua memakan ruang lebih besar di HP dan mengulang angka yang sama. |
| `.sorotan .smartwait` | y1416 | y720,69 | Mengulang `30`, `4 orang di depan`, dan disclaimer yang sama. |
| `.footer` | y1595,97 | y889,08 | Pada viewport desktop, footer baru berupa strip; isi footer dinilai dari full-page dan DOM. |

Pengujian interaksi terbatas:

- Klik Menu HP menampilkan `Beranda`, `Masuk`, `Daftar`; `aria-expanded` berubah menjadi `true`. Klik ulang menutup, kembali `false`.
- Klik CTA utama membuka <http://localhost:8080/businesses>, judul `Katalog bisnis · QueueCare`, heading `Cari bisnis`; tidak ada input password. Navigasi kembali berhasil ke `/`.
- Satu perpindahan Tab menempatkan fokus pada `Cari bisnis`; outline terhitung amber `rgb(242, 177, 52) solid 3px`. Ini bukti fokus CTA tersebut, bukan klaim semua alur lolos keyboard.
- CSS yang dilayani `/css/tokens.css` dan `/css/components.css` HTTP 200 dan byte-identik dengan sumber lokal.
- Urutan stylesheet langsung: Bootstrap 5.3.3, `tokens.css`, `components.css`, Google Fonts. Tidak diubah.

## Design Health Score — Nielsen

Skala 0–4: 0 bermasalah berat, 2 sebagian terpenuhi, 3 baik dengan celah, 4 sangat baik. Skor dibatasi pada landing anonim dan interaksi yang diperiksa; tidak menilai kualitas alur reservasi, dashboard, atau engine antrean.

| # | Heuristik | Skor | Dasar penilaian |
|---|---|---:|---|
| 1 | Visibility of System Status | 3 | Label `Contoh` membedakan demo dari data nyata; tiket menampilkan status teks dan posisi. Toggle menu memperbarui `aria-expanded`. Tautan Beranda belum memakai `aria-current="page"`. Tidak menuntut indikator antrean live pada contoh statis. |
| 2 | Match Between System and Real World | 2 | Headline dan `Cari bisnis` alami. `WAITING` melanggar bahasa Indonesia sehari-hari; narasi pendaftaran belum sesuai urutan cari tanpa login, login saat reservasi. |
| 3 | User Control and Freedom | 3 | Penjelajahan katalog tidak mengunci akun; menu dapat ditutup; browser Back kembali ke landing. Tidak ada dialog wajib atau aksi destruktif pada landing. Kontrol setelah login/reservasi belum dinilai. |
| 4 | Consistency and Standards | 3 | Tiket, tombol, radius, teal, dan amber konsisten dengan incumbent. Tombol Daftar di navbar mendapat padding dari `.navbar__menu a`, berbeda dengan tombol hero; label status belum konsisten dengan bahasa publik. |
| 5 | Error Prevention | 3 | `Contoh` dan `Ini perkiraan, bukan jaminan waktu.` mencegah salah baca demo dan jaminan waktu. Namun tidak ada penjelasan singkat bahwa akun baru diperlukan ketika reservasi; pilihan Daftar bisa diambil terlalu dini. Landing tidak menerima input transaksi. |
| 6 | Recognition Rather Than Recall | 3 | Kata kerja tiga langkah, kode, nomor, dan waktu terlihat bersama. Pengunjung belum dapat mengenali bisnis atau layanan yang tersedia di landing karena tidak ada pencarian atau pilihan nyata. Tidak ada tuntutan menghafal kode untuk CTA utama. |
| 7 | Flexibility and Efficiency of Use | n/a | Accelerator, bulk action, dan kustomisasi pengguna mahir tidak relevan untuk keputusan awal landing Persuade. Kekurangan pencarian dinilai sebagai masalah tindakan Persuade, bukan tuntutan shortcut. |
| 8 | Aesthetic and Minimalist Design | 2 | Hierarki bersih dan kelompok mudah dipindai. Contoh estimasi kedua tidak menambah informasi hasil; akhir tanpa CTA; copyright redup dan target HP kecil mengurangi kualitas akhir. |
| 9 | Recognize, Diagnose, and Recover from Errors | n/a | Landing statis tanpa form input, validasi, atau state galat yang ditampilkan. Alur galat katalog/login/reservasi di luar cakupan; tidak diberi nilai nol hanya karena tidak diuji. |
| 10 | Help and Documentation | n/a | Landing singkat tidak memerlukan sistem bantuan atau dokumentasi tersendiri. Kualitas penjelasan tiga langkah tetap dinilai pada heuristik 2 dan 6. |
| **Total** | **7 heuristik dinilai; 3 n/a: 7, 9, 10** | **19/28** | **67,86% — Acceptable. Perbaikan signifikan pada persuasi; bukan bukti produk tidak bisa dipakai.** |

Penyebut eksplisit: 7 × 4 = 28. Kategori mengikuti proporsi panduan Impeccable, 50% sampai sebelum 70% = Acceptable. Ini skor desain ahli, bukan metrik retensi atau konversi.

## Yang sudah bekerja

1. **Janji dan artefak hasil saling mendukung.** `.hero__judul` berbicara tentang giliran, lalu `.tiket__nomor` amber dan `.smartwait__menit` menunjukkan apa yang akan diketahui pelanggan. Bukti visual ini spesifik antrean dan selaras dengan `DESIGN.md`; pertahankan sebagai ciri utama.
2. **Jalur baca ringan pada dua ukuran.** CTA utama terlihat, tiga langkah tidak dibungkus kartu tambahan, kolom menjadi satu di HP, dan tidak ada overflow horizontal. Pada 375 × 900, tiket serta estimasi pertama masih terlihat utuh. Tidak perlu mengecilkan headline hanya untuk mengejar semua isi satu layar.
3. **Klaim estimasi cukup jujur.** Label `Contoh`, penjelasan rumus biasa, dan disclaimer berulang tidak menjanjikan ketepatan mutlak atau AI. Produk menjelaskan hasil yang terbatas; jangan menambah testimoni, angka adopsi, atau ketepatan tanpa bukti.

## Masalah prioritas

Lima masalah; dua P1, tiga P2. Tidak ditemukan bukti P0 pada landing yang diperiksa. Semua perubahan di bawah berupa rekomendasi, belum dikerjakan. Token, urutan CSS, kelas/ID kontrak, dan komponen incumbent tetap menjadi batas perbaikan berikutnya.

### 1. [P1] Janji mencari bisnis belum hadir sebagai tindakan yang bekerja di landing

**Bukti:** `home.html:13–16`, `.hero__tindakan a[href="/businesses"]` hanya tautan. `main` tidak memiliki form, hasil bisnis, atau pilihan layanan. Isi terkuat setelah copy adalah tiket statis `Bisnis contoh` / `Layanan contoh`. Browser membuktikan tautannya bekerja; masalahnya jarak antara janji dan mulai memilih, bukan link rusak.

**Dampak:** Dalam mode Persuade, orang yang datang mencari layanan belum bisa mulai mengetik atau memilih pada halaman pembuka. Mereka perlu satu navigasi lagi untuk mengetahui ketersediaan. Identitas antrean sudah terlihat, tetapi kemampuan menemukan bisnis belum terbukti dari permukaan ini. Ini penilaian risiko desain, bukan angka drop-off terukur.

| Before | After | Why |
|---|---|---|
| `.hero__tindakan` menampilkan `Cari bisnis` sebagai link ke halaman lain. | Hadirkan pencarian GET sederhana ke `/businesses` memakai nama parameter `q` dan komponen `.field` / `.field__input` yang sudah dipakai katalog. Jadikan tombol `Cari bisnis` submit pencarian; pertahankan pilihan jelajah semua bisnis. | Pengunjung melakukan tindakan yang ditawarkan langsung; pakai kontrak katalog yang sudah ada, tanpa API, dependency, atau filter baru. |

**Kriteria tinjau berikutnya:** pengunjung anonim dapat memasukkan nama bisnis dari landing dan tiba pada hasil `/businesses?q=...`; submit kosong tetap memberi jalur jelajah. Validasi detail kontrak katalog sebelum implementasi. Jangan menambah klaim ketersediaan atau daftar bisnis fiktif.

**Perintah yang sesuai:** `/impeccable shape` untuk penempatan pencarian; `/impeccable clarify` untuk label.

### 2. [P1] Waktu kebutuhan akun tidak jelas; Daftar bersaing terlalu awal

**Bukti:** `.hero__tindakan` menaruh `Cari bisnis` dan `Daftar` berdampingan. Desktop juga memiliki `.navbar__menu a[href="/register"]` berisi Daftar. `.hero__subteks` dan `#cara-kerja .langkah__teks` tidak menyebut batas login. `Ambil nomor` menjanjikan kode dan nomor dalam satu langkah, tanpa menjelaskan bahwa pengguna anonim harus login sebelum reservasi. `PRODUCT.md:25` menyatakan pencarian dan layanan terbuka, login saat reservasi. Browser mengonfirmasi akses katalog tanpa password.

**Dampak:** Customer baru harus menebak apakah bisa melihat layanan dahulu, mendaftar dahulu, atau akan diputus alurnya nanti. Halaman mengajukan komitmen akun sebelum calon pelanggan tahu bisnis yang ingin dipilih.

| Before | After | Why |
|---|---|---|
| CTA Daftar hadir setara area keputusan browse; tahapan akun tidak diterangkan. | Tambahkan keterangan dekat aksi utama: `Lihat bisnis dan layanan tanpa masuk. Masuk saat membuat reservasi.` Jadikan pencarian fokus hero; pertahankan akses Daftar sebagai opsi lebih ringan. Perjelas langkah `Ambil nomor` agar mencakup konfirmasi reservasi setelah masuk. | Mengurangi tebak-tebakan dan kejutan di tahap autentikasi, sesuai kontrak produk yang sudah ada. |

**Kriteria tinjau berikutnya:** pengguna dapat menjawab kapan login dibutuhkan sebelum menekan CTA; copy tidak menyiratkan reservasi anonim. Perubahan prioritas tombol tidak boleh menghilangkan akses login/daftar.

**Perintah yang sesuai:** `/impeccable clarify`, lalu `/impeccable distill` bila hirarki aksi masih bersaing.

### 3. [P2] Penutup mengulang estimasi, lalu membiarkan minat tanpa aksi

**Bukti:** `.hero .smartwait` dan `.sorotan .smartwait` identik: `30`, `4 orang di depan`, serta disclaimer. `.sorotan` di HP setinggi 424,72px, dari y1123,25. Setelah CTA hero berakhir di y391,38, sisa dokumen sampai y1728,75 tidak punya link katalog lain. Footer dimulai y1595,97; navbar sticky hanya menyediakan menu akun, bukan Cari bisnis.

**Dampak:** Penjelasan rumus menambah alasan percaya, tetapi contoh kedua tidak menambah pembuktian baru. Pembaca yang baru yakin di akhir perlu kembali ke atas untuk bertindak. Peak-end halaman melemah menjadi pengulangan dan kredit tugas.

| Before | After | Why |
|---|---|---|
| `.sorotan` mengulang angka demo, lalu footer tanpa aksi lanjut. | Pertahankan penjelasan rumus dan disclaimer; ringkas atau hapus duplikasi contoh kedua. Tutup bagian dengan `Cari bisnis` ke katalog atau ke pencarian landing. Bila perlu contoh kedua, tampilkan keterbatasan yang benar seperti `Estimasi belum tersedia` saat petugas aktif nol, dengan label contoh jelas. | Akhir halaman memberi keputusan berikutnya; ruang dipakai untuk informasi yang belum disampaikan. Jangan menghapus tiket utama atau catatan estimasi. |

**Kriteria tinjau berikutnya:** setelah membaca alasan estimasi, tindakan browse tersedia tanpa scroll kembali; tidak ada angka demo baru yang dianggap data nyata. Contoh state nol bersifat opsi, bukan kewajiban menambah bagian.

**Perintah yang sesuai:** `/impeccable distill`, `/impeccable layout`.

### 4. [P2] Kontrol HP kecil untuk konteks satu tangan

**Bukti:** Edge pada 375px mengukur `.navbar__tombol-menu` 59,67 × 32px, tombol hero 104,44 × 40px dan 77,95 × 40px. Jarak dua tombol hero 12px; tidak ada bukti tumpang tindih. Angka ini ukuran elemen DOM, bukan perkiraan screenshot.

**Dampak:** Tinggi 32px dan 40px berada di bawah sasaran kenyamanan sentuh 44px yang dipakai persona Casey. Ini bukan klaim gagal WCAG 2.5.8: batas WCAG dan pengecualiannya berbeda, dan target saat ini tidak otomatis melanggar batas 24px. Masalahnya kenyamanan pengguna HP yang terganggu atau memakai satu tangan.

| Before | After | Why |
|---|---|---|
| `.navbar__tombol-menu` 32px, `.hero__tindakan .tombol` 40px. | Pada HP, buat area sentuh relevan minimal 44px tinggi, misalnya memakai token `--jarak-8` dan `--jarak-3` lewat `calc(...)`. Pertahankan jarak tombol dan fokus terlihat; batasi cakupan landing bila perubahan global belum ditinjau. | Target lebih mudah disentuh tanpa mengganti identitas, menambah navigasi, atau memaksa sticky CTA. |

**Kriteria tinjau berikutnya:** ukur ulang hit area Menu dan dua aksi hero; uji buka/tutup menu serta layout saat menu terbuka. Jangan menganggap emulasi ini menggantikan uji perangkat fisik.

**Perintah yang sesuai:** `/impeccable adapt`.

### 5. [P2] Copyright footer terlalu redup terhadap latarnya

**Bukti:** `.footer .teks-redup` memakai teks `rgb(84, 106, 111)` (`--warna-teks-redup`) di atas latar footer `rgb(10, 68, 77)` (`--warna-primer-gelap`). Teks 14px, bobot normal. Perhitungan luminans relatif sRGB dari computed colors memberi **1,88:1**; batas teks normal WCAG AA 4,5:1. Ini bukan vonis berdasarkan tampak pucat di screenshot.

**Dampak:** Informasi kredit/sifat proyek sulit dibaca pada akhir halaman. Bukan penghalang browse, tetapi mengurangi aksesibilitas dan mutu penutup. Warna teks redup bukan bermasalah di semua tempat: body redup di latar halaman **5,35:1**, catatan SmartWait di latar lembut **4,82:1**, teks putih tombol utama **7,86:1**.

| Before | After | Why |
|---|---|---|
| `.footer .teks-redup` mewarisi token redup untuk permukaan terang. | Gunakan token teks yang sesuai latar gelap, seperti `--warna-primer-lembut` yang sudah dipakai isi footer; ukur ulang rasio. | Perbaikan lokal memakai token incumbent; tidak perlu mengganti palet atau memutihkan semua teks sekunder. |

**Kriteria tinjau berikutnya:** teks footer normal mencapai setidaknya 4,5:1 pada latar aktual, termasuk bila opacity dipakai.

**Perintah yang sesuai:** `/impeccable polish`; audit aksesibilitas tersendiri hanya jika diminta.

## Cognitive load — checklist delapan butir

| Butir | Hasil | Bukti / batas |
|---|---|---|
| Single focus | Lulus dengan catatan | `Cari bisnis` tetap CTA hero terkuat; tidak ada carousel, popup, atau banyak promo. Daftar mengganggu urutan keputusan, tetapi tidak menghapus hirarki visual utama. |
| Chunking ≤4 | Lulus | Tiga kelompok langkah; dua tindakan hero; satu kelompok tiket dan estimasi. Banyak metadata tiket adalah isi satu artefak, bukan semua opsi aksi. |
| Grouping | Lulus | Tiket dibatasi border; estimasi latar lembut; proses tiga kolom/kelompok; batas section jelas. |
| Visual hierarchy | Lulus | H1 40px, nomor amber, angka menit, lalu teks kecil. CTA tetap terlihat pada viewport pertama di dua ukuran. |
| One thing at a time | **Gagal** | Browse dan komitmen Daftar ditawarkan sebelum pengguna memilih bisnis; batas login tidak terlihat. Ini fokus masalah prioritas 2. |
| Minimal choices ≤4 | Lulus per keputusan | Hero dua opsi. Menu tiga tujuan. Desktop memiliki lima posisi link di luar merek, tetapi Daftar duplikat; empat tujuan unik: Beranda, Masuk, Daftar, Cari bisnis. Jangan menyamakan jumlah elemen dengan lima pilihan unik. |
| Working memory | Lulus dalam cakupan | Tidak perlu mengingat kode demo untuk menekan Cari bisnis. Tiket dan estimasi berdampingan/berdekatan. Preservasi pilihan lintas login belum diuji. |
| Progressive disclosure | Lulus dengan catatan | Janji dan CTA dahulu, proses lalu rumus. Tidak memaparkan pengaturan Owner/Admin. Belum ada pencarian langsung, tetapi itu celah tindakan Persuade, bukan otomatis kegagalan pengungkapan bertahap. |

**Hasil formal: 1 dari 8 gagal, beban kognitif rendah menurut checklist.** Risiko konversi tetap nyata pada urutan keputusan akun; skor beban ringan tidak membuktikan persuasi efektif.

**Keputusan dengan >4 opsi:** tidak ditemukan kelompok lokal dengan lebih dari empat tujuan unik. Pada desktop, jika merek dihitung sebagai kontrol, ada enam link terlihat: merek, Beranda, Masuk, Daftar navbar, Cari bisnis, Daftar hero. Tujuan tetap empat karena `/` dan `/register` berulang. Pada HP tertutup, Menu adalah pembuka kelompok, bukan tiga tujuan tersembunyi yang harus dihitung sebagai terlihat. Empat jenis bisnis dalam subteks adalah contoh kategori dalam kalimat, bukan empat kontrol filter.

- **Intrinsic load:** memilih bisnis, layanan, dan jadwal memang diperlukan produk; landing belum memperlihatkan pilihan nyata itu.
- **Extraneous load:** menebak kebutuhan akun, membaca `WAITING`, dan menemukan kembali CTA setelah scroll.
- **Germane load:** tiga kata kerja dan contoh tiket membantu membangun model reservasi lalu pantau antrean. Rumus menjelaskan batas produk tanpa jargon AI.

## Emotional journey

Ini pemetaan kemungkinan pengalaman berdasarkan susunan UI, bukan pernyataan perasaan pelanggan hasil riset.

| Tahap | Sinyal tampilan | Efek yang dituju / celah |
|---|---|---|
| Datang | `Tahu giliranmu tanpa bertanya.` | Memberi rasa kendali atas ketidakpastian. Bahasanya relevan untuk Customer. |
| Menimbang | Tiket `07`, posisi empat orang, estimasi 30 menit | Puncak visual terkuat: hasil mudah dibayangkan. Label Contoh menjaga kejujuran; tidak membuktikan akurasi. |
| Memutuskan | Cari bisnis berdampingan Daftar | Lembah pertama: kapan perlu akun belum jelas; orang diminta memikirkan akun sebelum menemukan layanan. |
| Belajar | Pilih layanan, Ambil nomor, Pantau giliran | Mengurangi kesan rumit. Lompatan login yang tidak disebut bisa menjadi kejutan pada alur berikutnya; belum diuji di sini. |
| Memercayai estimasi | Rumus SmartWait dan disclaimer | Reassurance tepat di dekat angka. Batas petugas nol belum dicontohkan; halaman tidak perlu berubah jadi manual teknis. |
| Mencapai akhir | Angka demo sama, tanpa CTA, kredit tugas redup | End tidak memanfaatkan kepercayaan yang baru dibangun. Bagi konteks tugas, kredit wajar; untuk peluncuran publik, persepsi kesiapan perlu diputuskan kelompok, bukan otomatis dihapus. |

**Peak-end:** pertahankan tiket sebagai peak; beri jalan browse pada end. **Momen berisiko lebih tinggi:** sebelum reservasi dan saat membaca estimasi. Landing sudah menenangkan soal waktu, tetapi belum menenangkan soal kapan harus masuk. Jangan menjanjikan push notification, WhatsApp, pembayaran, atau AI; semuanya di luar versi pertama menurut `PRODUCT.md`.

## Persona red flags

Persona berikut alat inspeksi, bukan orang yang diwawancarai; tidak ada kutipan atau hasil uji pelanggan yang dikarang.

### Jordan — pengguna pertama

Alur tinjau: baca headline, bandingkan Cari bisnis/Daftar, pahami tiket, lanjut katalog.

- `.hero__tindakan` tidak menjelaskan apakah akun wajib sebelum melihat layanan. Jordan harus menyimpulkan sendiri.
- `.lencana--waiting` berisi `WAITING`, sementara seluruh penjelasan lain berbahasa Indonesia. Label publik `Menunggu` lebih langsung; enum backend dan selector harus tetap utuh saat perbaikan.
- `#cara-kerja` tidak menyebut autentikasi; `Ambil nomor` dapat terbaca sebagai tindakan instan dari sesi anonim.
- Sisi yang membantu: CTA berlabel teks, contoh diberi label, dan klik Cari bisnis benar-benar membuka katalog tanpa password. Tidak ada dasar menyatakan Jordan pasti meninggalkan halaman.

### Riley — peninjau janji dan edge case

Alur tinjau: bandingkan janji dengan contoh, buka katalog anonim, periksa arti estimasi.

- Seluruh bukti produk di landing berupa contoh statis; pengulangan `30` tidak membuktikan perubahan hasil atau ketersediaan layanan.
- `07` dengan empat orang di depan bukan kontradiksi yang terbukti. Nomor urut dan posisi berbeda; jangan menghitung posisi dari `07` tanpa aturan antrean aktual.
- `PRODUCT.md` menetapkan petugas nol menghasilkan `Estimasi belum tersedia`; landing hanya memperlihatkan angka. Ini kesempatan menjelaskan batas bila berguna, bukan bukti state nol aplikasi rusak.
- Jalur error, hasil kosong, input panjang, refresh saat reservasi, dan state setelah login tidak diuji. Tidak boleh diklaim gagal atau lolos.

### Casey — pengguna HP mudah terganggu

Alur tinjau: pakai HP satu tangan, buka/tutup menu, temukan bisnis, lanjut setelah membaca bawah.

- Menu 32px dan tombol hero 40px kurang nyaman dibanding target 44px.
- Pada HP, informasi bawah berakhir jauh setelah satu-satunya CTA katalog. Navbar sticky tidak menyediakan Cari bisnis; kembali ke aksi memerlukan scroll kembali.
- Demo kedua menambah panjang halaman tanpa pilihan baru. Ini biaya perhatian, bukan bukti performa jaringan lambat.
- CTA dan estimasi pertama sudah terlihat pada 375 × 900; jangan melaporkan CTA di luar fold. Koneksi lambat dan pemakaian fisik satu tangan belum diuji.

### Customer pencari layanan — persona proyek, turunan PRODUCT.md

**Profil:** memakai HP untuk mencari bisnis, memilih layanan/jadwal, lalu mendapat kode reservasi dan nomor antrean. Tidak diasumsikan usia, pekerjaan, lokasi, atau frekuensi penggunaan.

**Perilaku yang diturunkan dari alur produk:** mencari tanpa login; melihat layanan; login ketika membuat reservasi; kembali memantau status dan estimasi.

**Red flags:** landing belum memulai pencarian nyata; batas akun tidak jelas; `WAITING` perlu diterjemahkan untuk antarmuka sehari-hari; akhir halaman tidak memberi jalan lanjut. Bentuk tiket dan disclaimer justru mendukung kebutuhan persona ini.

### Owner di tempat kerja — persona proyek, turunan PRODUCT.md

**Profil:** memakai laptop/tablet untuk mengelola satu atau beberapa bisnis, layanan, petugas aktif, dan antrean. Bukan bukti Owner sedang mengunjungi landing untuk membeli produk.

**Perilaku yang diturunkan dari peran:** masuk ke akun; memilih bisnis aktif; memproses antrean; memahami pengaruh petugas aktif pada estimasi.

**Red flags di batas landing:** footer menyebut bisnis jasa, tetapi Daftar tidak menjelaskan tujuan role; Owner baru tidak dapat menilai jalur pendaftarannya dari copy landing. Ini observasi sekunder, bukan alasan menambah hero Owner atau pricing. Owner lama punya Masuk di menu; menu HP sudah terbukti dapat dibuka. Dashboard tablet belum diuji, sehingga tidak diberi skor dari screenshot desktop landing.

Administrator desktop tidak diberi persona tambahan: sasaran Persuade ini bukan admin. Jangan memasukkan navigasi administrasi ke CTA publik hanya untuk memenuhi tiga role.

## Observasi kecil dan batas kepastian

- `home.html:25`: ubah presentasi `WAITING` menjadi bahasa sehari-hari pada tugas perbaikan; pertahankan `.lencana--waiting` dan nilai enum agar kontrak tidak rusak.
- `layout.html:18`: Beranda belum memiliki `aria-current="page"`; label aktif bisa lebih jelas bagi pengguna bantu. Tidak dibuktikan sebagai hambatan utama.
- Tanggal tiket membungkus berbeda: pada HP `10` terpisah dari `November`; desktop tahun pindah baris. Masih terbaca; rapikan pengelompokan layanan/tanggal bila tiket disentuh, tanpa menambah lebar yang menyebabkan overflow.
- Angka `30` mempunyai unit pada teks sebelah. Tidak terpotong dan bukan angka tanpa unit dalam keseluruhan komponen; penyatuan `30 menit` bisa mempercepat pemindaian, tetapi bukan prioritas di atas pencarian dan akun.
- Jangan menambahkan angka langkah hanya karena ada tiga kolom. `DESIGN.md` sengaja memakai judul kata kerja langsung; susunan sekarang tetap dapat diikuti.
- Font terhitung menyebut stack Bricolage Grotesque/Public Sans; ini tidak membuktikan glyph font eksternal berhasil untuk semua pengunjung. Tidak ada alasan mengubah font incumbent dalam kritik ini.
- Footer `Dibuat untuk tugas PBO.` adalah fakta konteks proyek. Tidak dianjurkan menghapusnya demi berpura-pura produk sudah dipakai publik.
- Tidak ada testimoni/rating bukan cacat otomatis. Bukti paling aman saat ini adalah kemampuan yang benar-benar bekerja dan contoh berlabel.

## Pertanyaan desain untuk sintesis parent

Pertanyaan ini opsi keputusan berikutnya, bukan izin melakukan perbaikan sekarang.

1. Untuk masalah 1, awal browse yang paling sesuai: **A)** pencarian nama bisnis langsung dengan GET `q`, **B)** pencarian nama dan kategori memakai kontrol katalog yang sudah ada, **C)** pertahankan link sekarang dengan menerima keterbatasan Persuade?
2. Untuk masalah 2, posisi akun: **A)** pencarian jadi aksi tunggal hero, Daftar tinggal di menu, **B)** pertahankan Daftar sekunder dengan penjelasan kapan login diperlukan?
3. Untuk masalah 3, akhir halaman: **A)** ringkas duplikasi dan beri CTA Cari bisnis, **B)** ganti contoh kedua dengan state estimasi belum tersedia serta CTA?

## Verifikasi dan yang belum diperiksa

**Dikerjakan:** membaca target dan fragment layout, CSS token/komponen, JavaScript menu, PRODUCT/DESIGN; melihat empat screenshot; membandingkan DOM parent dengan DOM baru; mengecek menu, fokus satu CTA, navigasi browse anonim dan kembali; membandingkan CSS served dengan file lokal; menghitung skor dan empat pasangan kontras dari computed colors.

**Tidak dikerjakan:** detector, Assessment B, overlay, build/Maven, unit/integration/regression suite, screen reader, seluruh urutan keyboard, zoom 200%, perangkat fisik, viewport tablet, throttling jaringan, state login, submit reservasi, state antrean/error/hasil kosong, akurasi SmartWait, pengujian keamanan, atau pemeriksaan database. Tidak ada klaim pemeriksaan ini lolos. Tidak memulai atau menghentikan server milik parent.

**Risiko penafsiran:** skor dan persona bersifat evaluasi ahli pada landing anonim yang tersedia. Tidak mengukur perubahan konversi atau mewakili bukti pelanggan nyata. Rekomendasi butuh tugas implementasi tersendiri; laporan ini tidak mengubah tampilan.
