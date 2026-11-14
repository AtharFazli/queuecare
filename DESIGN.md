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

Status reservasi memakai gaya yang sama (usulan, perlu disepakati kelompok): PENDING = waiting, CONFIRMED = in-service, CANCELLED = cancelled, COMPLETED = completed.

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

### Navbar HP (tombol menu)

Di bawah 720px `.navbar__menu` tersembunyi dan `.navbar__tombol-menu` muncul. Tombol toggle class `navbar__menu--terbuka` lewat `static/js/navbar.js`, tanpa library. `aria-expanded` ikut diperbarui.

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
| `/reservations/{id}` | `tiket`, `smartwait`, `tombol--bahaya`, `modal` |
| `/owner/*`, `/admin/*`, `/customer/*` | `dash`, `sidebar`, `statistik`, `kosong`, `tabel`, `modal`, `field` |
| `/owner/queues` | `tabel`, `lencana`, `tombol--kecil` |

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