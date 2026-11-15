---
status: draft
mode: persuade
target: src/main/resources/templates/home.html
route: /
---

# Landing QueueCare — DRAF, belum dikonfirmasi

Dokumen ini rekomendasi dari kritik pada commit 43b837a, bukan kontrak arahan yang sudah disetujui. Tidak mengizinkan implementasi atau perubahan identitas.

## Tujuan halaman

Customer yang datang dari HP memahami manfaat reservasi dan antrean, melihat bukti berbentuk tiket, lalu mulai mencari bisnis tanpa kewajiban akun di awal. Login tetap diperlukan saat membuat reservasi. Owner lama tetap mempunyai jalur Masuk; Admin bukan sasaran persuasi landing.

## Arah yang disarankan

Pertahankan identitas tiket amber, teal, font dan komponen incumbent. Jadikan pencarian bisnis tindakan utama hero memakai form GET q ke /businesses dan kontrol yang sudah ada. Tunjukkan kapan perlu masuk dengan satu kalimat. Pertahankan penjelasan tiga langkah dan SmartWait beserta disclaimer, ringkas contoh estimasi yang berulang, lalu tutup dengan jalan kembali ke Cari bisnis.

## Bukti yang dipakai

Kritik dual-agent, screenshot landing 375px/1280px di docs/impeccable/landing-critique/, dan pengukuran DOM pada halaman berjalan. Tidak ada testimoni pelanggan atau angka efektivitas. Data tiket diberi label Contoh.

## Batas perubahan berikutnya

Thymeleaf + Bootstrap 5, tokens.css, components.css tetap fondasi. Pertahankan urutan stylesheet, kelas/ID kontrak JS dan pengujian, B10 serta aturan sembunyikan dialog tertutup. Peningkatan area sentuh HP dan kontras footer perlu scope eksplisit karena komponen tersebut digunakan halaman lain. Tidak mengganti warna, font, logo atau menambah framework.

## Menunggu keputusan

- Awal browse: pencarian nama langsung, nama + kategori yang sudah ada, atau pertahankan link sekarang?
- Daftar: cukup di menu, atau tetap sekunder hero dengan penjelasan kapan perlu akun?
- Penutup: ringkas contoh berulang + CTA, atau contoh state estimasi belum tersedia + CTA?

Tidak ada pilihan yang dianggap disetujui lewat diam. Setelah konfirmasi, buat tugas implementasi tersendiri dan ukur ulang viewport serta kontras.
