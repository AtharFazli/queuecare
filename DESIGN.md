---
# --bayangan-sm: 0 1px 2px rgba(20, 38, 43, 0.08)
# --bayangan-md: 0 6px 18px rgba(20, 38, 43, 0.10)
# --bayangan-lg: 0 16px 40px rgba(20, 38, 43, 0.18)
# --lebar-konten: 1140px; components.wadah.width adalah max-width.
# --lebar-sidebar: 248px; components.sidebar.width adalah kolom desktop.
# --tinggi-navbar: 64px; components.navbar.height berlaku desktop, HP auto.
name: QueueCare
description: "Rekaman sistem visual yang sudah ada; bukan persetujuan warna atau font final."
colors:
  warna-primer: "#0E5A66"
  warna-primer-gelap: "#0A444D"
  warna-primer-lembut: "#DDEFF0"
  warna-aksen: "#F2B134"
  warna-aksen-lembut: "#FDF0D5"
  warna-aksen-teks: "#6B4700"
  warna-teks: "#14262B"
  warna-teks-redup: "#546A6F"
  warna-teks-terbalik: "#FFFFFF"
  warna-latar: "#F4F8F8"
  warna-permukaan: "#FFFFFF"
  warna-batas: "#D3DFE1"
  warna-sukses: "#2E7D4F"
  warna-sukses-lembut: "#DFF1E5"
  warna-bahaya: "#B3402F"
  warna-bahaya-gelap: "#8F3022"
  warna-bahaya-lembut: "#FADFDA"
  warna-netral-lembut: "#E8EEEF"
typography:
  font-judul:
    fontFamily: "\"Bricolage Grotesque\", \"Segoe UI\", system-ui, sans-serif"
  font-dasar:
    fontFamily: "\"Public Sans\", \"Segoe UI\", system-ui, sans-serif"
  teks-xs:
    fontSize: "0.75rem"
  teks-sm:
    fontSize: "0.875rem"
  teks-md:
    fontSize: "1rem"
  teks-lg:
    fontSize: "1.25rem"
  teks-xl:
    fontSize: "1.75rem"
  teks-2xl:
    fontSize: "2.5rem"
  body:
    fontFamily: "\"Public Sans\", \"Segoe UI\", system-ui, sans-serif"
    fontSize: "1rem"
    lineHeight: 1.6
  judul-kartu:
    fontFamily: "\"Bricolage Grotesque\", \"Segoe UI\", system-ui, sans-serif"
    fontSize: "1.25rem"
    fontWeight: 700
  angka-antrean:
    fontFamily: "\"Bricolage Grotesque\", \"Segoe UI\", system-ui, sans-serif"
    fontSize: "2.5rem"
    fontWeight: 800
    lineHeight: 1
  tombol:
    fontFamily: "\"Public Sans\", \"Segoe UI\", system-ui, sans-serif"
    fontSize: "0.875rem"
    fontWeight: 600
rounded:
  radius-sm: "6px"
  radius-md: "10px"
  radius-lg: "16px"
  radius-penuh: "999px"
spacing:
  jarak-1: "4px"
  jarak-2: "8px"
  jarak-3: "12px"
  jarak-4: "16px"
  jarak-6: "24px"
  jarak-8: "32px"
  jarak-12: "48px"
components:
  tombol:
    backgroundColor: "{colors.warna-primer}"
    textColor: "{colors.warna-teks-terbalik}"
    rounded: "{rounded.radius-md}"
    typography: "{typography.tombol}"
    padding: "8px 16px"
  tombol-hover:
    backgroundColor: "{colors.warna-primer-gelap}"
  tombol-sekunder:
    backgroundColor: "transparent"
    textColor: "{colors.warna-primer}"
  tombol-bahaya:
    backgroundColor: "{colors.warna-bahaya}"
  tombol-bahaya-hover:
    backgroundColor: "{colors.warna-bahaya-gelap}"
  kartu:
    backgroundColor: "{colors.warna-permukaan}"
    rounded: "{rounded.radius-lg}"
  field-input:
    backgroundColor: "{colors.warna-permukaan}"
    textColor: "{colors.warna-teks}"
    rounded: "{rounded.radius-md}"
    padding: "8px 12px"
  lencana:
    backgroundColor: "{colors.warna-netral-lembut}"
    textColor: "{colors.warna-teks-redup}"
    rounded: "{rounded.radius-penuh}"
  wadah:
    width: "1140px"
  sidebar:
    width: "248px"
  navbar:
    height: "64px"
---

# Design System: QueueCare

Panduan tampilan untuk website QueueCare (Spring Boot + Thymeleaf + Bootstrap 5). Dokumen ini pelengkap `prd.md`. Nilai warna, font, dan bentuk di bawah adalah usulan, karena master planning tidak menentukannya. Semua bisa diganti lewat `css/tokens.css`.

## 1. Arah desain

- **Suasana:** tenang, jelas, dan mudah dibaca di HP maupun desktop.
- **Ciri khas:** tiket antrean, yaitu kartu dengan nomor antrean besar berlatar amber.
- **Aturan aksen:** amber hanya untuk nomor antrean dan status yang butuh perhatian (CALLED). Jangan dipakai untuk dekorasi.
- **Bahasa antarmuka:** bahasa Indonesia sehari-hari, huruf kecil kecuali awal kalimat. Tombol menyebut hasilnya, misalnya "Buat reservasi", bukan "Submit".

## 2. Cara memakai

Muat berurutan di `<head>` (taruh di `src/main/resources/static/css/`):

```html
<link rel="stylesheet" th:href="@{/css/tokens.css}">
<link rel="stylesheet" th:href="@{/css/components.css}">
```

- Kelas komponen memakai penamaan BEM ringan: `.komponen`, `.komponen__elemen`, `.komponen--varian`.
- Bootstrap 5 tetap dipakai untuk grid dan layout (`container`, `row`, `col-*`). Untuk tombol, form, dan tabel, pakai kelas dari dokumen ini supaya tampilan seragam.
- Jangan menulis warna, jarak, atau radius langsung. Selalu pakai `var(--...)`.
- Font dimuat dari Google Fonts (Bricolage Grotesque dan Public Sans). Tanpa internet, tampilan memakai font sistem.

## 3. Token

### Warna

| Token | Nilai | Dipakai untuk |
|---|---|---|
| `--warna-primer` | `#0E5A66` | Tombol utama, tautan, merek |
| `--warna-primer-gelap` | `#0A444D` | Hover, sidebar, footer |
| `--warna-primer-lembut` | `#DDEFF0` | Latar lembut, status IN_SERVICE |
| `--warna-aksen` | `#F2B134` | Nomor antrean, menu aktif sidebar |
| `--warna-aksen-lembut` | `#FDF0D5` | Latar status CALLED |
| `--warna-aksen-teks` | `#6B4700` | Teks di atas amber lembut |
| `--warna-teks` | `#14262B` | Teks utama |
| `--warna-teks-redup` | `#546A6F` | Keterangan, meta |
| `--warna-teks-terbalik` | `#FFFFFF` | Teks di atas warna primer |
| `--warna-latar` | `#F4F8F8` | Latar halaman |
| `--warna-permukaan` | `#FFFFFF` | Kartu, tabel, modal |
| `--warna-batas` | `#D3DFE1` | Garis dan border |
| `--warna-sukses` / `-lembut` | `#2E7D4F` / `#DFF1E5` | COMPLETED |
| `--warna-bahaya` / `-lembut` | `#B3402F` / `#FADFDA` | Error, NO_SHOW, tombol hapus |
| `--warna-netral-lembut` | `#E8EEEF` | WAITING, CANCELLED |

### Tipografi

| Token | Nilai | Dipakai untuk |
|---|---|---|
| `--font-judul` | Bricolage Grotesque | Judul dan angka besar |
| `--font-dasar` | Public Sans | Isi, form, tabel |
| `--teks-xs` | 0.75rem | Lencana, bantuan form |
| `--teks-sm` | 0.875rem | Tombol, tabel, meta |
| `--teks-md` | 1rem | Teks isi |
| `--teks-lg` | 1.25rem | Judul kartu, H3 |
| `--teks-xl` | 1.75rem | H2, angka statistik |
| `--teks-2xl` | 2.5rem | H1, nomor antrean, menit SmartWait |

Panjang baris isi dijaga di bawah 80 karakter. Angka antrean dan statistik memakai `font-variant-numeric: tabular-nums` agar lebarnya sama.

### Jarak, radius, bayangan, layout

| Kelompok | Token |
|---|---|
| Jarak (skala 4px) | `--jarak-1` 4, `-2` 8, `-3` 12, `-4` 16, `-6` 24, `-8` 32, `-12` 48 |
| Radius | `--radius-sm` 6, `--radius-md` 10, `--radius-lg` 16, `--radius-penuh` 999 |
| Bayangan | `--bayangan-sm`, `--bayangan-md`, `--bayangan-lg` |
| Layout | `--lebar-konten` 1140px, `--lebar-sidebar` 248px, `--tinggi-navbar` 64px |

Pakai radius `md` untuk tombol dan input, `lg` untuk kartu, tabel, dan modal, `penuh` untuk lencana.

## 4. Komponen

### Tombol

| Kelas | Fungsi |
|---|---|
| `.tombol` | Aksi utama, satu per area |
| `.tombol--sekunder` | Aksi pendukung (outline) |
| `.tombol--bahaya` | Aksi yang menghapus atau membatalkan |
| `.tombol--kecil` | Di dalam tabel dan kartu |

```html
<button class="tombol">Buat reservasi</button>
<button class="tombol tombol--sekunder">Lihat detail</button>
<button class="tombol tombol--bahaya">Batalkan reservasi</button>
```

### Lencana status

Enam status antrean dari master planning:

| Status | Kelas |
|---|---|
| WAITING | `lencana--waiting` |
| CALLED | `lencana--called` |
| IN_SERVICE | `lencana--in-service` |
| COMPLETED | `lencana--completed` |
| CANCELLED | `lencana--cancelled` |
| NO_SHOW | `lencana--no-show` |

Status reservasi memakai gaya yang sama: PENDING = `lencana--waiting`, CONFIRMED = `lencana--confirmed` (latar putih dengan border primer, supaya tampil beda dari IN_SERVICE), CANCELLED = `lencana--cancelled`, COMPLETED = `lencana--completed`.

```html
<span class="lencana lencana--called">CALLED</span>
```

Dengan Thymeleaf, kelas bisa dibuat dari enum: `th:classappend="'lencana--' + ${#strings.toLowerCase(#strings.replace(q.status, '_', '-'))}"`.

### Kartu (katalog bisnis)

```html
<article class="kartu">
  <div class="kartu__gambar">Barbershop</div>
  <div class="kartu__badan">
    <h3 class="kartu__judul">[Nama bisnis]</h3>
    <p class="kartu__meta">[Kategori] · [alamat singkat]</p>
    <p>[Deskripsi singkat]</p>
  </div>
  <div class="kartu__kaki">
    <span class="teks-redup">Mulai dari [harga]</span>
    <a class="tombol tombol--kecil" href="#">Lihat detail</a>
  </div>
</article>
```

Bungkus beberapa kartu dengan `.kartu-grid` untuk katalog `/businesses`.

### Form

```html
<div class="field field--galat">
  <label class="field__label" for="staf">Jumlah petugas aktif</label>
  <input class="field__input" id="staf" type="number">
  <p class="field__bantuan">Isi angka 0 atau lebih.</p>
</div>
```

`field--galat` dipakai saat validasi gagal. Pesan galat menyebut apa yang salah dan cara memperbaikinya, tanpa kata maaf.

### Tabel

```html
<div class="tabel-bungkus">
  <table class="tabel">
    <thead><tr><th>No.</th><th>Customer</th><th>Status</th></tr></thead>
    <tbody><tr><td class="tabel__angka">1</td><td>[Nama]</td><td><span class="lencana lencana--waiting">WAITING</span></td></tr></tbody>
  </table>
</div>
```

`.tabel-bungkus` wajib ada supaya tabel bisa digeser di layar HP.

### Modal

Memakai elemen `<dialog>` bawaan browser, jadi tidak butuh library.

```html
<dialog class="modal" id="hapus">
  <div class="modal__kepala"><h3>Batalkan reservasi?</h3></div>
  <div class="modal__badan"><p>Tindakan ini tidak bisa diulang.</p></div>
  <div class="modal__kaki">
    <button class="tombol tombol--sekunder" onclick="this.closest('dialog').close()">Kembali</button>
    <button class="tombol tombol--bahaya">Batalkan reservasi</button>
  </div>
</dialog>
<script>document.getElementById('hapus').showModal()</script>
```

### Navbar dan footer

- `.navbar` (sticky) berisi `.navbar__merek` dan `.navbar__menu`. Halaman aktif ditandai `aria-current="page"`.
- `.footer` untuk footer public website.
- Di layar sempit (≤720px) menu navbar disembunyikan. Tombol menu untuk HP belum dibuat: [isi: tentukan pola menu HP, misalnya collapse Bootstrap].

### Sidebar dan statistik (dashboard Admin dan Owner)

```html
<div class="dash">
  <nav class="sidebar" aria-label="Menu Owner">
    <ul class="sidebar__menu">
      <li><a href="#" aria-current="page">Dashboard</a></li>
    </ul>
  </nav>
  <div class="dash__isi">
    <div class="statistik-grid">
      <div class="statistik"><span class="statistik__angka">12</span><span class="statistik__label">Reservasi hari ini</span></div>
    </div>
  </div>
</div>
```

Menu Owner: Dashboard, Business Profile, Services, Schedules, Capacity, Reservations, Queue Management, Reports. Menu Admin: Dashboard, Users, Businesses, Categories, Reservations, Reports. Di layar sempit, sidebar tampil di atas konten.

### Tiket antrean

Komponen utama untuk `/reservation/success/{id}` dan `/queue/{id}`.

```html
<div class="tiket">
  <div class="tiket__info">
    <div class="tiket__kode">QC-20261110-0001</div>
    <div class="tiket__nama">[Nama bisnis]</div>
    <div>[Layanan] · [tanggal, jam]</div>
    <span class="lencana lencana--waiting">WAITING</span>
  </div>
  <div class="tiket__nomor"><span>Antrean</span><strong>07</strong></div>
</div>
```

### SmartWait

```html
<div class="smartwait">
  <div class="smartwait__menit">30</div>
  <div>
    <p class="smartwait__teks"><b>menit lagi</b> sampai giliran kamu. 4 orang di depan.</p>
    <p class="smartwait__catatan">Ini perkiraan, bukan jaminan waktu.</p>
  </div>
</div>
```

- Catatan "perkiraan, bukan jaminan" selalu tampil, sesuai master planning bagian 5.6.
- Jika petugas aktif = 0, ganti angka dengan teks "Estimasi belum tersedia". Jangan tampilkan error atau "Infinity".

### Landing (hero, langkah, sorotan)

Halaman `/` memakai komponen tambahan ini. Semua nilai tetap lewat `var(--...)`.

```html
<section class="hero">
  <div class="wadah hero__isi">
    <div class="hero__teks">
      <h1 class="hero__judul">[Pesan utama]</h1>
      <p class="hero__subteks">[Satu kalimat penjelas, maksimal 20 kata.]</p>
      <div class="hero__tindakan">
        <a class="tombol" href="#cara-kerja">[Aksi utama]</a>
        <a class="tombol tombol--sekunder" href="/register">Daftar</a>
      </div>
    </div>
    <div class="hero__visual">
      <p class="contoh">Contoh</p>
      <div class="tiket">...</div>
      <div class="smartwait">...</div>
    </div>
  </div>
</section>

<section class="wadah" id="cara-kerja">
  <h2>Cara kerja</h2>
  <div class="langkah">
    <div class="langkah__item">
      <h3 class="langkah__judul">[Kata kerja] [objek]</h3>
      <p class="langkah__teks">[Satu kalimat pendek.]</p>
    </div>
  </div>
</section>

<section class="sorotan">
  <div class="wadah sorotan__isi">
    <div>
      <h2>[Judul]</h2>
      <p class="sorotan__teks">[Penjelas]</p>
    </div>
    <div class="hero__visual">
      <p class="contoh">Contoh</p>
      <div class="smartwait">...</div>
    </div>
  </div>
</section>
```

- `.hero__isi` dua kolom (teks dan visual), jadi hero tidak terpusat. Di bawah 720px jadi satu kolom, visual rata kiri.
- `.langkah` tiga kolom bergaris atas, bukan kartu. Judul langkah memakai kata kerja langsung, bukan "Langkah 1".
- `.sorotan` pita lembut `--warna-primer-lembut` untuk SmartWait.
- `.contoh` label kecil untuk menandai visual yang contoh, bukan data nyata.
- Daftar file: `templates/home.html`, `static/css/components.css`.

### Autentikasi (auth)

Login dan register memakai komposisi dua kolom seperti hero landing: panel teal di kiri berisi tiket contoh, form di kanan.

```html
<main class="auth">
  <div class="wadah auth__isi">
    <div class="auth__panel">
      <p class="contoh">Contoh</p>
      <div class="tiket">...</div>
      <h2 class="auth__judul">[Pesan]</h2>
      <p class="auth__teks">[Penjelas]</p>
    </div>
    <div class="auth__form">
      <div class="kartu">
        <div class="kartu__badan">[Form]</div>
      </div>
    </div>
  </div>
</main>
```

- Di bawah 720px jadi satu kolom. Form tampil lebih dulu (`order`), panel di bawahnya.
- Pesan galat memakai `.lencana`, sama seperti sebelumnya.

### Empty state (kosong)

Dashboard tiap role memakai `.kosong` untuk keadaan belum ada data. Angka statistik diisi 0, bukan placeholder.

```html
<div class="kosong">
  <h2 class="kosong__judul">Belum ada data</h2>
  <p class="kosong__teks">[Penjelas]</p>
</div>
```

### Katalog (pencarian dan detail)

Halaman `/businesses` memakai `.kartu-grid`, `.kartu`, dan `.pencarian`. Detail `/businesses/{id}` memakai `.kartu`, `.tabel`, `.lencana`, dan `.kartu-grid`.

```html
<form action="/businesses" method="get" class="pencarian">
  <div class="field"><label class="field__label" for="q">Nama bisnis</label><input class="field__input" id="q" name="q"></div>
  <div class="field"><label class="field__label" for="kategori">Kategori</label><select class="field__input" id="kategori" name="kategori"></select></div>
  <button class="tombol" type="submit">Cari</button>
</form>
```

- `.pencarian` baris fleksibel, lalu membungkus di layar sempit. Setiap `.field` tumbuh minimal 220px.
- Pencarian dan filter lewat parameter GET (`q`, `kategori`), jadi hasil bisa di-bookmark.
- Katalog hanya menampilkan bisnis ACTIVE; detail non-ACTIVE menghasilkan 404.

### Bisnis aktif (Owner)

Satu Owner boleh punya banyak bisnis. Owner memilih salah satu, dan pilihan ini disimpan di session. Halaman `/owner/business`, `/owner/services`, `/owner/schedules`, dan `/owner/capacity` bekerja untuk bisnis yang dipilih.

```html
<nav class="sidebar">
  <p class="sidebar__judul">Bisnis aktif</p>
  <a class="sidebar__bisnis" href="/owner/business">[Nama bisnis]</a>
  <a class="sidebar__ganti" href="/owner/businesses">Ganti bisnis</a>
</nav>
```

- `/owner/businesses` menampilkan semua bisnis milik Owner dengan tombol "Pilih" dan "Tambah bisnis".
- Kalau Owner hanya punya satu bisnis, bisnis itu dipilih otomatis. Kalau belum punya, diarahkan ke form buat bisnis.
- Pilihan di session selalu dicek kepemilikannya di service, jadi membuka atau memilih bisnis milik Owner lain menghasilkan 403.

### Navbar HP (tombol menu)

Di bawah 720px `.navbar__menu` tersembunyi dan `.navbar__tombol-menu` muncul. Tombol toggle class `navbar__menu--terbuka` lewat `static/js/navbar.js`, tanpa library. `aria-expanded` ikut diperbarui. Di layar ≤720px navbar menampilkan nama pengguna (`.navbar__nama`) dan menyembunyikan email (`.navbar__email`).

## 5. Pemakaian per halaman

| Halaman | Komponen utama |
|---|---|
| `/` Homepage | `navbar`, `hero`, `tiket`, `smartwait`, `langkah`, `sorotan`, `tombol`, `footer` |
| `/businesses` | `field` (search), `kartu-grid` + `kartu` |
| `/businesses/{id}` | `kartu`, `tabel` (layanan), `tombol` |
| `/login`, `/register` | `auth`, `auth__panel`, `tiket`, `kartu`, `field`, `tombol` |
| `/reservation/create/{serviceId}` | `kartu`, `field`, `tombol` |
| `/reservation/success/{id}` | `tiket`, `tombol` |
| `/queue/{id}` | `tiket`, `smartwait`, `lencana` |
| `/reservations` | `tabel`, `lencana` |
| `/reservations/{id}` | `tiket`, `lencana`, `tombol--bahaya`, `modal` |
| `/owner/*`, `/admin/*`, `/customer/*` | `dash`, `sidebar`, `statistik`, `kosong`, `tabel`, `modal`, `field` |
| `/owner/queues` | `tabel`, `tabel__aksi`, `lencana`, `field`, `tombol--kecil`, `tombol--bahaya`, `modal` |
| `/owner/reports` | `statistik`, `tabel`, `field`, `kosong` |
| `/admin/reservations` | `tabel`, `tabel-bungkus`, `field`, `kosong`, `tombol--kecil` |
| `/admin/reports` | `statistik`, `tabel`, `field`, `kosong` |
| `/admin/users` | `tabel`, `tabel-bungkus`, `pencarian`, `field`, `lencana`, `tombol--kecil`, `tombol--bahaya`, `modal`, `kosong` |
| `/admin/users/tambah`, `/admin/users/{id}/ubah` | `kartu`, `field`, `tombol`, `tombol--sekunder` |
| `/profile` | `dash`, `sidebar`, `kartu`, `field`, `tombol` |
| `/queue/{id}` | `tiket`, `lencana`, `smartwait`, `tombol` |
| `/owner/business` | `kartu`, `tombol`, `kosong` |
| `/owner/services` | `tabel`, `lencana`, `tombol--kecil`, `kosong` |
| `/owner/schedules` | `tabel`, `lencana`, `tombol--kecil`, `kosong` |
| `/owner/capacity` | `kartu`, `field`, `tombol`, `kosong` |
| `/admin/categories` | `kartu`, `field`, `tabel`, `tombol--kecil`, `tombol--bahaya` |
| `/admin/businesses` | `tabel`, `lencana`, `field`, `tombol--kecil`, `kosong` |
| `/owner/businesses` | `tabel`, `lencana`, `tombol`, `tombol--kecil`, `kosong` |
| `/businesses` | `pencarian`, `kartu-grid`, `kartu`, `kosong` |
| `/businesses/{id}` | `kartu`, `tabel`, `lencana`, `kartu-grid`, `tombol` |
| `/reservation/create/{serviceId}` | `kartu`, `tombol` |

Catatan Fase 4: `/reservation/create/{serviceId}` memuat ulang halaman lewat parameter `?tanggal=` supaya daftar slot muncul tanpa JavaScript. Slot penuh atau yang sudah lewat tampil sebagai pilihan nonaktif dengan keterangan "Penuh" atau "Lewat". Harga di seluruh aplikasi ditulis dengan pemisah ribuan titik, misalnya "Rp 45.000".

Catatan Fase 5: `/owner/queues` memakai parameter `?tanggal=` untuk memilih hari, dan tombol "Muat ulang" mengirim ulang form tanggal. `/queue/{id}` memuat ulang status dan posisi lewat `static/js/antrean.js`: `fetch` ke `/queue/{id}/status` tiap 10 detik, berhenti saat `document.hidden` atau status sudah final (COMPLETED, CANCELLED, NO_SHOW). Tombol "Pantau antrean" hanya tampil selama antrean belum final. Pesan flash dibersihkan saat ada POST baru lewat `FlashBersihInterceptor`, supaya pesan lama tidak muncul di halaman berikutnya.

Catatan Fase 6: SmartWait dihitung di `SmartWaitService.hitungMenit` (fungsi murni, tanpa database) lalu dibungkus `SmartWaitService.untuk(Queue)` yang mengambil durasi layanan pelanggan di depan dari `QueueRepository.antreanDepan`. Hasilnya masuk ke `/queue/{id}/status` sehingga ikut diperbarui polling, dan ditampilkan di `/queue/{id}` serta `/reservations/{id}` memakai komponen `.smartwait`. Catatan "Ini perkiraan, bukan jaminan waktu." tampil bila ada angka.

Catatan Fase 7: semua angka dashboard dan laporan berasal dari query agregat (`COUNT`, `COUNT DISTINCT`, `GROUP BY`) di repository, bukan dihitung di Java. Dashboard Owner menghitung "reservasi hari ini" dan "selesai hari ini" dengan `reservation_date = hari ini`; "antrean menunggu" dan "sedang dilayani" dihitung seluruh antrean bisnis tanpa filter tanggal. Laporan Owner dan Admin memakai `reservation_date` untuk rentang tanggal, dengan bawaan 30 hari terakhir di laporan Owner. Tabel lebar di `/admin/reservations` digeser di dalam `.tabel-bungkus`, halaman tidak melebar.

Catatan Fase 7b: halaman akun memakai komponen yang sudah ada (`.tabel`, `.pencarian`, `.kartu`, `.field`, `.modal`, `.lencana`, `.tombol--kecil`, `.tombol--bahaya`), kelas CSS baru tidak ada. Modal aktif/nonaktif pengguna memakai `<dialog class="modal">` seperti `/owner/queues` dan `/reservations/{id}`.

Catatan Fase 8: Bootstrap 5 mempunyai class `.modal` sendiri dengan `display: none` untuk overlay-nya, sehingga `<dialog class="modal">` pernah tidak pernah tampil. Ditambah aturan `dialog.modal` di `components.css` yang memaksa dialog tampil dan memusatkannya. Yang tampak di layar ialah `<dialog>` beserta `.modal__kepala`, `.modal__badan`, dan `.modal__kaki`; class `.modal` dipinjam sebagai penanda elemen, bukan sebagai overlay Bootstrap.

## 6. Aksesibilitas dan responsif

- Fokus keyboard selalu terlihat (outline amber). Jangan dihapus.
- Kontras teks sudah dipilih agar terbaca. Jangan mengganti warna teks tanpa mengecek kontrasnya.
- Status tidak hanya dibedakan lewat warna: selalu ada teks status di dalam lencana.
- Titik putus utama di 720px. Uji tampilan di 375px dan 1280px.
- Animasi dimatikan jika pengguna memilih "reduce motion".

## 7. Keputusan terbuka

- [isi: warna dan font final, kelompok bisa mengganti usulan di atas]
- [isi: logo QueueCare, saat ini hanya teks merek]
- [isi: pola menu navbar di HP]
- [isi: gambar bisnis, saat ini kartu memakai blok warna sebagai placeholder]

## 8. File terkait

- `css/tokens.css`: semua token di bagian 3.
- `css/components.css`: gaya dasar dan semua komponen di bagian 4.
- `katalog.html`: halaman contoh yang bisa dibuka di browser.
- `prd.md`: kebutuhan, fase, dan skema database.

## 9. Data dummy

Seed data dummy untuk demo diatur lewat properti `app.seed-dummy` di `application.properties` (nilai awal `true`). Set `app.seed-dummy=false` untuk menonaktifkannya. Seed hanya jalan bila tabel `businesses` masih kosong, jadi aman dijalankan berulang tanpa menggandakan data.

<!-- impeccable:document scan; isi lama di atas dipertahankan utuh -->

## Overview

Rekaman implementasi yang ada, bukan desain ulang. Arah yang sudah tertulis: tenang, jelas, mudah dibaca; tiket antrean menjadi ciri khas. Nilai warna dan font tetap usulan sampai kelompok memutuskan. Tidak ditambahkan metafora merek atau keputusan visual baru.

Bagian 1–9 dan semua catatan fase di atas adalah riwayat yang dipertahankan. Rekaman ini memperjelas keadaan kode terbaru saat ada catatan lama yang bertentangan. Sumber: `src/main/resources/static/css/tokens.css`, `components.css`, `templates/fragments/layout.html`, serta pemeriksaan browser pada 375px dan 1280px.

## Colors

Warna memakai nama peran yang sama dengan CSS; seluruh deklarasi `--warna-*` tercatat di frontmatter tanpa mengganti nilainya. Teal untuk aksi utama dan identitas, warna netral untuk isi, hijau untuk selesai, merah untuk galat dan aksi berbahaya.

Aturan aksen pada bagian 1 lebih sempit dari pemakaian yang juga sudah dicatat pada bagian 3 dan kode: amber ada pada nomor antrean, CALLED, menu aktif atau bisnis aktif di sidebar, garis menu navbar, dan fokus keyboard. Ini rekaman pemakaian yang sudah ada, bukan izin memakai amber sebagai dekorasi baru.

Nilai literal hover tombol bahaya `#8F3022`, backdrop dialog, dan putih transparan hover sidebar tetap dicatat sebagai detail implementasi yang belum menjadi token. Nilai tersebut tidak dimasukkan sebagai token merek baru atau dibuatkan pengecualian detector.

## Typography

Frontmatter mempertahankan dua stack font dan enam ukuran `--teks-*` dari `tokens.css`. Peran tambahan diambil dari deklarasi komponen, bukan dibuat dari ukuran kebetulan satu layar. Judul dan angka besar memakai Bricolage Grotesque; isi memakai Public Sans; fallback Segoe UI, system-ui, sans-serif.

Body berukuran `teks-md` dengan line-height 1.6. Judul kartu memakai `teks-lg` dan bobot 700. Nomor tiket memakai `teks-2xl`, bobot 800, line-height 1. Angka antrean dan statistik memakai tabular-nums.

Bobot 500 yang teramati pada H1 umum berasal dari Bootstrap, bukan token judul baru. Google Fonts saat ini memuat Bricolage Grotesque 600/800 dan Public Sans 400/500/600; permintaan bobot lain pada CSS tidak dianggap bukti adanya aset font tambahan.

## Layout

Jarak mengikuti tujuh token skala 4px yang ada. `wadah.width` merekam batas maksimum konten, bukan lebar tetap semua layar. `sidebar.width` merekam kolom dashboard desktop. `navbar.height` merekam tinggi desktop; pada media query HP tingginya auto. Nilai ketiganya berasal dari token layout, tanpa token baru.

Breakpoint utama adalah `max-width: 720px`. Dashboard menjadi satu kolom, sidebar berada di atas isi; hero, sorotan, dan autentikasi menjadi satu kolom; form autentikasi lebih dulu. Navbar memakai tombol Menu dan toggle `navbar__menu--terbuka` dari `navbar.js`. Catatan “menu HP belum dibuat” pada bagian 4 dan placeholder bagian 7 adalah catatan lama, bukan pekerjaan yang masih belum ada.

Grid katalog memakai auto-fill minmax 260px; tabel digeser di `.tabel-bungkus`. Halaman `/`, `/businesses`, dan `/login` diperiksa di 375px serta 1280px tanpa overflow horizontal. Angka ini adalah ukuran pemeriksaan, bukan breakpoint tambahan. H1 katalog mengecil ke `teks-xl` pada HP; `.hero__judul` tetap `teks-2xl` karena aturan komponen yang lebih akhir, bukan skala tipografi global baru.

## Elevation & Depth

Kartu memakai `--bayangan-sm`; dialog memakai `--bayangan-lg`. Token `--bayangan-md` memang dideklarasikan, tetapi belum ditemukan pemakaiannya pada `components.css`. Ketiga nilai tetap direkam di komentar frontmatter dan `extensions.shadows` pada `.impeccable/design.json`; tidak dibuat aturan pemakaian baru untuk token yang belum dipakai.

Permukaan putih, latar lembut, dan border membedakan area isi. Bayangan tidak dipromosikan menjadi efek hover baru. Format token Impeccable tidak menyediakan grup shadows tingkat atas; nilai lengkap berada di metadata pendamping, dengan salinan sumber di komentar frontmatter.

## Shapes

Empat token radius dipertahankan. Tombol dan input memakai radius-md; kartu, tabel, dan modal memakai radius-lg; lencana memakai radius-penuh. Radius-sm tersedia di sumber, tetapi belum ditemukan pemakaiannya pada komponen saat scan.

Tiket mempunyai tepi putus-putus dan lekukan lingkaran pada sambungan nomor. Geometri khusus tiket tetap detail komponen, bukan token radius global. Ukuran sekali pakai seperti tinggi placeholder gambar dan geometri potongan tiket tidak dijadikan skala baru.

## Components

Pola, contoh HTML, pemakaian per halaman, dan catatan fase pada bagian 4–5 tetap berlaku. Metadata pendamping menampilkan cuplikan komponen yang sudah ada; kelas preview diberi awalan `ds-` hanya di metadata, tidak di template atau CSS aplikasi.

- Tombol: utama, sekunder, bahaya, dan kecil tetap memakai kelas sekarang. Hover, focus-visible, disabled, border, dan validasi tidak diubah.
- Input: border warna batas, permukaan putih, fokus primer lembut, galat memakai warna bahaya. Label tetap berhubungan dengan input.
- Lencana: keenam status antrean tetap punya teks. CONFIRMED reservasi tetap putih dengan border primer agar berbeda dari IN_SERVICE.
- Kartu: badan dan kaki memakai token jarak; gambar bisnis masih placeholder. Placeholder bukan aset merek final.
- Navbar dan sidebar: pemilihan menu dan bisnis aktif mengikuti pola yang sudah ada. Urutan CSS pada fragment head harus Bootstrap 5.3.3, lalu tokens.css, lalu components.css; Google Fonts dimuat setelahnya.
- Dialog: `dialog.modal` pada `components.css` adalah perbaikan B10 yang dilindungi. Jangan dihapus atau diganti dalam tahap persiapan ini. Pemeriksaan browser menemukan enam dialog tanpa `[open]` masih memiliki display block pada halaman Admin pengguna; di 375px salah satunya menghalangi klik tombol Nonaktifkan. Ini cacat yang teramati, bukan keputusan desain. Tidak dijadikan pola preview dan tidak diperbaiki di sini.
- Tiket dan SmartWait: nomor antrean, status, estimasi atau pesan belum tersedia tetap mengikuti komponen dan aturan pada bagian 4. Data contoh tidak menjadi klaim pengguna nyata.

## Do's and Don'ts

- Do pakai token dan komponen yang sudah ada; pertahankan teks status, fokus keyboard, reduced motion, serta pembungkus tabel.
- Do bedakan rekaman kode sekarang, usulan kelompok, catatan historis, dan cacat yang ditemukan.
- Do pertahankan urutan Bootstrap, tokens.css, components.css serta aturan B10 sampai ada tugas perbaikan yang disetujui.
- Don't ubah warna, font, layout, komponen, kelas, ID, JavaScript, atau skrip pengujian dalam persiapan ini.
- Don't menjadikan literal sekali pakai, bobot warisan Bootstrap, atau bug dialog sebagai token atau keputusan baru.
- Don't menganggap scan sumber menggantikan uji browser, atau nilai demo sebagai bukti produk dipakai pelanggan nyata.

### Pembaruan document: dialog tertutup dan token hover bahaya

Penambahan ini memperbarui rekaman persiapan di atas; catatan lama tetap disimpan sebagai riwayat. `--warna-bahaya-gelap` mengambil nilai hover tombol bahaya yang sudah ada, bukan memilih warna baru. Frontmatter `colors.warna-bahaya-gelap` dan `components.tombol-bahaya-hover` merekam peran itu. Nilai literal lama pada catatan sebelumnya bukan lagi status implementasi terbaru.

Aturan `dialog.modal` perbaikan B10 tetap utuh. Tambahan `dialog.modal:not([open]) { display: none; }` membatasi dialog tertutup agar tidak terlihat atau menghalangi klik. Saat `[open]` ada, aturan B10 tetap menampilkan dialog. Kontrak browser: klik pemicu asli membuka, tombol Kembali menutup tanpa submit, dan tombol konfirmasi menjalankan POST sesuai alur. Tidak ada perubahan selector, ID, atau urutan Bootstrap, tokens.css, components.css.

### Pembaruan document: landing pencarian, kontras auth, sentuh 44px, label Menunggu, aria-current

Penambahan ini memperbarui rekaman implementasi setelah kritik landing (commit 927cf84 sampai d049f83 di branch desain-impeccable). Catatan lama, termasuk komposisi hero dua link pada bagian "Landing (hero, langkah, sorotan)", tetap disimpan sebagai riwayat dan bukan lagi implementasi terbaru. Tidak ada token baru; semua perubahan memakai token yang sudah ada.

- Hero landing: tindakan utama sekarang form GET ke `/businesses` dengan parameter `q`, memakai ulang pola `.pencarian` dan `.field`/`.field__input` dari katalog. Submit kosong tetap boleh menjelajah. Kelas baru `.hero__catatan` (teks kecil redup) memuat kalimat "Lihat bisnis dan layanan tanpa masuk. Masuk saat membuat reservasi."; Daftar turun menjadi link inline di kalimat itu.
- Sorotan: contoh SmartWait kedua (30 menit / 4 orang di depan + disclaimer) dihapus karena menduplikasi hero. Penjelasan rumus dan kalimat "Hasilnya perkiraan, bukan jaminan." tetap. Kelas baru `.sorotan__aksi` memuat CTA penutup "Cari bisnis".
- Footer: `.footer .teks-redup` memakai `--warna-primer-lembut`. Teks redup global (#546A6F) hanya 1,88:1 di latar footer; terukur 9,08:1 setelah perubahan. Berlaku di semua halaman karena footer fragment bersama.
- Auth: `.auth__teks` diberi warna `--warna-primer-lembut` (sebelumnya mewarisi teks gelap body di panel gelap, 1,45:1; kini 9,08:1). Flash sukses `.auth__form .lencana--completed` memakai latar `--warna-permukaan` dengan border `--warna-sukses`, mengikuti pola `lencana--confirmed` (teks hijau di latar lembut 4,29:1; di putih 5,05:1). Lencana COMPLETED di tabel tidak diubah.
- Target sentuh: pada `@media (max-width: 720px)`, `.tombol`, `.tombol--kecil`, dan `.field__input` memakai `min-height: 44px`; link navbar non-tombol memakai padding `12px 0` (tinggi sekitar 48px). Desktop tidak berubah (40/32px); tombol Menu HP kini 44px.
- Label status antrean: WAITING ditampilkan sebagai "Menunggu" pada tiket contoh (home, login, register, fragment contoh) dan lencana di queue/show, reservations/detail, owner/queues, admin/reports, admin/reservations, termasuk pembaruan polling di antrean.js. Enum `QueueStatus`, properti `.kelas`, dan kelas `lencana--waiting` tetap; status lain masih tampil dengan nama enum.
- aria-current: fragment navbar membaca variabel opsional `active`; home.html menyetel `th:with="active='beranda'"` pada body sehingga link Beranda mendapat `aria-current="page"` hanya di landing (CSS garis bawah amber sudah ada). Signature fragment berparameter tidak dipakai: Thymeleaf 3.1.3 menolak panggilan tanpa argumen ke fragment bersignature (500), jadi variabel diwariskan lewat konteks pemanggil.

### Pembaruan document: dashboard Owner dan Admin (kritik dual-agent)

Penambahan ini merekam perbaikan hasil kritik dashboard (docs/impeccable/dashboard-critique/kritik-dashboard.md; commit a048f2a sampai a9e281b di branch desain-impeccable). Catatan lama tetap riwayat. Tidak ada token baru; semua memakai token yang sudah ada. Bukti akhir: 44/44 pengukuran halaman HTTP 200, 0 teks gagal kontras (sebelumnya 20 instance), 0 target sentuh <44px di 375px, 0 overflow dokumen.

- Grid dashboard: `.dash__isi { min-width: 0; }` mencegah blowout grid — sebelumnya isi 584px dalam wadah 343px di /admin@375 membuat kartu statistik dan kolom tabel terklip permanen (overflow:hidden .dash). Setelah perbaikan, tabel kembali scroll internal dan semua kartu terjangkau.
- Lencana: `.lencana--completed` kini teks `--warna-teks` di latar `--warna-sukses-lembut` (13,30:1; sebelumnya hijau 4,29:1), mengikuti pola dark-on-soft seperti lencana--in-service. Flash sukses auth tetap teks hijau di latar putih + border hijau (5,05:1). Berlaku juga untuk lencana "Aktif"/"Buka" yang memakai kelas yang sama.
- Sidebar: `.sidebar__ganti` memakai `--warna-primer-lembut` + underline (9,08:1; sebelumnya 1,37:1). Pada @media 720px, `.sidebar__menu a` padding 12px sehingga tinggi 46,4px (sebelumnya 38,4px).
- Fokus: `:focus-visible` mempertahankan outline amber 3px + cincin pendamping `box-shadow: 0 0 0 2px var(--warna-primer-gelap)` (amber/cincin 5,71:1, cincin/putih 10,79:1; sebelumnya amber saja 1,89:1 di putih). `.field__input:focus` outline `--warna-primer` (7,86:1; sebelumnya primer-lembut 1,19:1).
- Tabel: `.tabel td { white-space: nowrap; }` menjaga kode reservasi/harga satu baris; `.tabel-bungkus` memakai scroll-shadow CSS (gradien local/scroll) sebagai penanda bisa digeser di HP, hilang sendiri di ujung scroll. Kolom aksi sticky belum dikerjakan (usulan).
- Navbar: link "Dashboard" untuk OWNER (→ /owner) dan ADMIN (→ /admin) lewat sec:authorize, menghapus jalan buntu dari subhalaman. Ekstraksi sidebar penuh ke fragment tetap usulan.
- Konteks bisnis: halaman /owner/services dan /owner/schedules menampilkan "Bisnis: {nama} · Ganti bisnis" mengikuti pola queues. Form layanan/jadwal belum bisa (model tanpa atribut bisnis; usulan, butuh controller).
- Konfirmasi destruktif: Hapus jadwal, Hapus kategori, dan ubah status bisnis kini lewat dialog.modal per baris (pola B10 yang sama dengan antrean/pengguna). Dialog ubah status memuat form select+submit utuh dengan label terhubung, tanpa JS sinkronisasi. Select Role di form pengguna admin memakai option kosong "Pilih role" + required (backend @NotBlank sudah menolak kosong); sebelumnya default ADMIN.
- Label dashboard bahasa Indonesia (template saja; enum, kelas lencana, dan nilai option tidak berubah): antrean WAITING→Menunggu, CALLED→Dipanggil, IN_SERVICE→Dilayani, COMPLETED→Selesai, NO_SHOW→Tidak hadir, CANCELLED→Dibatalkan; bisnis ACTIVE→Aktif, INACTIVE→Nonaktif, PENDING→Menunggu verifikasi; reservasi PENDING→Menunggu, CONFIRMED→Dikonfirmasi, CANCELLED→Dibatalkan, COMPLETED→Selesai; role ADMIN→Administrator, OWNER→Owner, CUSTOMER→Pelanggan. Header kolom "Customer"→"Pelanggan", "Reservasi"/"Antrean"→"Status reservasi"/"Status antrean"; label statistik "Customer"→"Pelanggan", "Bisnis pending"→"Bisnis menunggu verifikasi". Halaman customer queue/show dan polling antrean.js tidak diubah (di luar cakupan; konsistensi lintas role dicatat sebagai usulan).
- Usulan yang belum dikerjakan (P2/P3, rinci di kritik-dashboard.md): .tabel__aksi di sel aksi yang bertumpuk, form filter pola .pencarian, semantik lencana (coretan untuk Nonaktif/Libur, PENDING abu), format tanggal dd/MM konsisten, kolom angka rata kanan, flash diperbesar, bantuan statis form + penanda wajib + h1 form konsisten, checkbox menyatu dengan label, atribut a11y kecil (aria-labelledby dialog, scope th, nav pagination, skip-link), panel marketing di form bisnis owner, konteks bisnis di form, auto-refresh antrean owner (butuh JS), filter status bisnis (butuh backend).

### Pembaruan document: halaman customer (kritik dual-agent)

Penambahan ini merekam perbaikan hasil kritik halaman customer + publik (docs/impeccable/customer-critique/kritik-customer.md; commit f762bb1 sampai 2cab20a di branch desain-impeccable). Catatan lama tetap riwayat. Tidak ada token baru. Bukti akhir: 32/32 pengukuran HTTP 200, 0 teks gagal kontras, 0 target sentuh <44px di 375px, 0 overflow dokumen, 0 label enum mentah tersisa; alur buat reservasi → pantau → batal diuji live dengan akun dummy (QC-20261120-0001 dibuat lalu dibatalkan).

- Navigasi customer: navbar fragment menambah Dashboard (CUSTOMER → /customer), Riwayat (CUSTOMER → /reservations), dan Katalog (semua peran → /businesses) mengikuti pola sec:authorize yang sudah ada; sidebar /customer dan /profile diisi tujuan nyata (Dashboard, Katalog, Riwayat, Profil) dengan judul "Menu kamu". Navbar anon: Beranda, Katalog, Masuk, Daftar.
- Dashboard /customer: kartu statistik hardcoded 0 dan copy "menyusul di fase berikutnya" dihapus (misinformation); diganti panel aksi cepat "Cari bisnis" + "Riwayat reservasi" memakai .kosong dan utilitas flex Bootstrap yang sudah dimuat. Statistik nyata butuh Java → usulan.
- Monitoring queue/show: `th:with="hidup=..."` (ekspresi status non-final) menggantikan `bisaPantau` yang tidak pernah diisi controller — teks "memperbarui setiap 10 detik" kini tampil di antrean hidup dan "Status antrean sudah final." hanya di antrean final. `data-antrean-url` hanya dirender bila hidup (th:attr kondisional null menghapus atribut; JS early-return) sehingga halaman final tidak polling sama sekali dan labelnya permanen. Duplikasi logika final di template dicatat sebagai keterbatasan (sumber kebenaran tetap QueueService).
- Label customer bahasa Indonesia: reservasi (Menunggu/Dikonfirmasi/Dibatalkan/Selesai) di riwayat, detail, sukses; antrean 6 status di queue/show dan detail; peran (Pelanggan/Owner/Administrator) di profil — atribut model `peran` bertipe String, dipakai perbandingan string; copy register "Akun baru otomatis menjadi Pelanggan." Enum Java, kelas lencana, dan nilai option tidak berubah.
- antrean.js (satu perubahan kecil DISETUJUI USER): peta `LABEL` di samping STATUS_FINAL; `statusEl.textContent = LABEL[data.status] || data.status`. Kelas lencana, STATUS_FINAL, dan seluruh logika polling tidak berubah; terverifikasi label bertahan setelah polling (553 Menunggu, 555 Dilayani) dan halaman final (557 Tidak hadir) tanpa fetch.
- Usulan utama yang belum dikerjakan (rinci di kritik-customer.md): SavedRequest pasca-login (P1, butuh Java — alur PRD Fase 4.1 "reservasi → login → kembali" masih putus, teruji mendarat di /customer); statistik customer nyata; guardrail form reservasi (tanggal ISO→dd/MM, Batal, required jam, min tanggal); hierarki aksi (Pantau primer dulu); SmartWait tanpa dash (butuh 1 baris JS); alamat+tel: di halaman hari-H; aria-live polling; posisi disembunyikan saat IN_SERVICE/final; penjelasan NO_SHOW→CANCELLED; kolom Status di depan pada riwayat HP; bantuan statis password; footer melayang; kaki kartu sejajar; jumlah hasil katalog; flash diperbesar; format tanggal/nomor seragam; a11y kecil (aria-labelledby dialog, skip-link, th scope, aria-current selain beranda).

