# QueueCare — Panduan Pemakaian Logo (singkat)

Tanda: **Giliran Terpanggil** — tiga bar antrean mengecil menuju satu titik amber.
Wordmark: **QueueCare** (Q dan C kapital), font judul Bricolage Grotesque, di-outline jadi path.
Semua berkas master ada di folder `kit\`. Jangan mengubah kode/template aplikasi dari folder ini.

---

## 1. Palet & rasio kontras (WCAG 2.1, terukur)

| Warna | HEX | Peran |
|---|---|---|
| Teal | `#0E5A66` | warna utama (bar, wordmark) |
| Teal gelap | `#0A444D` | latar gelap / navbar gelap |
| Amber | `#F2B134` | aksen titik "giliran" |
| Putih | `#FFFFFF` | latar terang / versi terbalik |
| Ink | `#14262B` | teks / versi hitam |

Rasio kontras antar pasangan (angka terukur, bukan perkiraan):

| Pasangan | Rasio | Layak teks (≥4.5) | Layak grafis (≥3) |
|---|---|---|---|
| Teal / Putih | 7.86:1 | ✅ | ✅ |
| Teal gelap / Putih | 10.79:1 | ✅ | ✅ |
| Amber / Ink | 8.28:1 | ✅ | ✅ |
| Teal gelap / Amber | 5.71:1 | ✅ | ✅ |
| Teal / Amber | 4.16:1 | ❌ | ✅ |
| **Amber / Putih** | **1.89:1** | ❌ | ❌ |
| Teal / Ink | 1.99:1 | ❌ | ❌ |
| Teal / Teal gelap | 1.37:1 | ❌ | ❌ |

### Aturan pakai amber (penting)
- **Amber di atas putih = 1.89:1, GAGAL bahkan untuk grafis (≥3:1).** Titik amber hanya boleh
  tampil di latar putih sebagai **aksen yang menempel pada bar teal** — identitas tanda dibawa oleh
  teal (7.86:1), bukan oleh amber. Jangan pernah memakai amber sendirian sebagai satu-satunya
  penanda di latar putih/terang.
- Untuk **elemen amber mandiri di latar terang** (mis. badge notifikasi): beri **cincin/outline teal**
  atau ganti ke **teal gelap**. Amber polos di atas putih tidak boleh jadi pembawa makna.
- **Amber di atas teal gelap = 5.71:1 (lulus).** Jadi versi terbalik berwarna boleh: bar **putih** +
  titik **amber** di atas teal gelap. Lihat `queuecare-horizontal-putih.svg` (mono putih penuh) —
  untuk versi yang tetap memakai amber di latar gelap, pakai bar putih + titik amber.
- **Amber sebagai teks** hanya di atas ink/teal gelap (8.28 / 5.71). Jangan teks amber di atas putih.
- Jangan menaruh tanda **teal di atas teal gelap** (1.37:1) — pakai versi **putih**.

---

## 2. Versi & kapan dipakai

| Berkas | Pakai untuk |
|---|---|
| `queuecare-symbol.svg` | tanda berdiri sendiri (avatar, ikon), latar terang |
| `favicon-small.svg` / `favicon.svg` | **favicon & ikon kecil** — potongan tebal (lihat §4) |
| `queuecare-horizontal.svg` | navbar web, header, kop surat (latar terang) |
| `queuecare-horizontal-putih.svg` | navbar/footer gelap, latar teal gelap atau foto gelap |
| `queuecare-horizontal-hitam.svg` | cetak satu warna hitam, fax, emboss |
| `queuecare-stacked.svg` / `-putih.svg` | format persegi/sosial, kartu nama, signage |
| `queuecare-black.svg` / `-white.svg` / `-mono-0e5a66.svg` | satu warna (hitam / putih / teal) |
| `queuecare-app-icon.svg` | ikon aplikasi (tile teal, bar putih, titik amber) |
| `queuecare-maskable.svg` | ikon PWA maskable (zona aman 80%) |
| `wordmark.svg` | nama saja, bila tanda sudah hadir di tempat lain |

---

## 3. Ruang aman (clear space)

- Satuan ruang aman **x = tinggi satu bar** pada tanda.
- Sisakan ruang kosong minimal **1x** di keempat sisi tanda/lockup. Tidak boleh ada teks, ikon,
  atau tepi bidang lain masuk ke zona ini.
- Pada lockup, jarak tanda→wordmark sudah dikunci (≈ 0.48× tinggi tanda). **Jangan diubah.**
- Wordmark tidak boleh lebih menonjol dari titik amber; amber tetap satu-satunya aksen hangat.

## 4. Ukuran minimum

| Bentuk | Minimum | Catatan |
|---|---|---|
| Tanda (symbol) | 24 px | di bawah 24 px **wajib** pakai potongan tebal `favicon-small.svg` |
| Favicon / ikon kecil | 16 px | pakai `favicon-small.svg` (bar tebal, jarak lega) |
| Lockup horizontal | tinggi 24 px (≈ lebar 110 px) | di bawah ini pakai tanda saja |
| Lockup bertumpuk | lebar 72 px | di bawah ini pakai tanda saja |
| Wordmark saja | tinggi 16 px | hindari di bawah 14 px |

**Bukti ukuran kecil (terukur):** tanda biasa = 23 px tinta @16px (mudah mush); potongan favicon
tebal = **56 px tinta @16px** (2.4× lebih tebal, bahkan di atas konsep awal 54). Vision-check: tiga
bar + titik tetap terbaca jelas di 16px pada potongan tebal. Karena itu `favicon.ico`,
`favicon-16/32/48.png`, dan semua ikon kecil memakai potongan tebal.

## 5. Larangan (misuse)

- ❌ Jangan mengubah warna tanda/wordmark di luar palet di atas.
- ❌ Jangan meregangkan, memampatkan, memiringkan, atau memutar lockup.
- ❌ Jangan menambah bayangan, gradient, outline/stroke, atau efek apa pun.
- ❌ Jangan menyusun ulang posisi tanda↔wordmark atau mengubah jaraknya.
- ❌ Jangan menaruh tanda teal di atas teal gelap / foto ramai tanpa versi putih.
- ❌ Jangan memakai amber sendirian di latar putih sebagai penanda utama (1.89:1).
- ❌ Jangan memakai tanda biasa (tipis) di bawah 24 px — pakai potongan favicon tebal.
- ❌ Jangan mengetik ulang wordmark dengan font lain; pakai path outline yang sudah diverifikasi.
- ❌ Jangan memotong/mengklipping tanda atau wordmark.

## 6. Tipografi

- Judul & wordmark: **Bricolage Grotesque** (SIL OFL — aman untuk logo). Wordmark pada logo sudah
  di-outline; font tetap diperlukan untuk teks aplikasi sehari-hari.
- Isi/form/tabel: **Public Sans** (sesuai `DESIGN.md` aplikasi, hanya dibaca).
- Wordmark logo diverifikasi: IoU **0.967** terhadap render Edge `<text>` pada font & ukuran sama;
  lebar huruf per-glyph cocok (Q terlebar, r tersempit); versi cermin hanya 0.372 → terbukti tegak.

## 7. Catatan teknis

- Semua SVG final: tanpa `<text>`, tanpa raster/filter/mask, fill solid, lubang pakai `fill-rule`.
- Audit produksi (svg_audit): tanda 98–100, lockup 91, ikon 98+ (semua ≥ 90).
- Lockup lama yang rusak (huruf menumpuk) dipindah ke `rusak\` — jangan dipakai.
- Folder ini di luar repo; `D:\PBO-Java` tidak diubah.
