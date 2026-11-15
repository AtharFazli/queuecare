# Product: QueueCare

<!-- impeccable:product-schema 1 -->

## Platform

web

Website, termasuk mobile web. Bukan aplikasi mobile native.

## Users

- Customer memakai HP untuk mencari bisnis jasa, membuat reservasi, dan memantau antrean.
- Owner memakai laptop atau tablet di tempat kerja untuk mengelola bisnis dan memproses antrean.
- Administrator memakai desktop untuk mengelola platform. Admin tidak menangani antrean harian.

## Product Purpose

Membantu pelanggan mengetahui giliran dan perkiraan waktu tunggu, serta membantu bisnis mengatur reservasi dan antrean.

Alur utama berhasil bila Customer mendapat kode reservasi dan nomor antrean, Owner bisa memproses antrean sampai selesai, dan Customer bisa memantau status serta estimasi waktu tunggu.

## Operating Context

Customer bisa mencari bisnis dan melihat layanan tanpa login. Login diperlukan saat membuat reservasi. Customer memilih satu bisnis, layanan, dan jadwal, lalu mendapat kode reservasi dan nomor antrean.

Owner mengelola bisnis miliknya, layanan, jadwal, kapasitas, petugas aktif, dan antrean. Satu Owner bisa mengelola beberapa bisnis dengan memilih bisnis aktif.

Administrator mengelola akun, kategori, status bisnis, serta melihat dashboard dan laporan seluruh platform.

## Capabilities and Constraints

- Tiga role: CUSTOMER, OWNER, ADMIN. Hak akses dan kepemilikan diperiksa di backend.
- SmartWait menghitung estimasi dari total durasi layanan pelanggan di depan dibagi jumlah petugas aktif. Ini rumus biasa, bukan AI. Estimasi bukan jaminan waktu.
- Jika petugas aktif nol, tampilkan “Estimasi belum tersedia”.
- Status antrean: WAITING, CALLED, IN_SERVICE, COMPLETED, CANCELLED, NO_SHOW.
- Reservasi dan antrean dibuat dalam satu transaksi agar tidak setengah tersimpan.
- Stack: Java Spring Boot, Thymeleaf, Bootstrap 5, MySQL; pengembangan memakai NetBeans dan GitHub.
- Web responsif untuk HP, tablet, dan desktop; bisa dijalankan lokal.
- Versi pertama tidak mencakup pembayaran online, WhatsApp/SMS, notifikasi push real-time, rating, membership, rekomendasi AI, aplikasi native, payroll, atau perangkat antrean fisik.
- Keputusan yang masih terbuka di PRD tidak dianggap sudah disetujui: slot atau antrean berjalan, penyimpanan kapasitas, dukungan walk-in, kategori, batas pembatalan, status yang dihitung SmartWait, deadline, dan cara demo/hosting. Catatan revisi Fase 2 menyatakan Owner membuat bisnis sendiri lalu menunggu verifikasi Admin.

## Brand Commitments

Nama produk QueueCare. Bahasa antarmuka Indonesia sehari-hari. Tombol menyebut hasil tindakan, misalnya “Buat reservasi”.

Sistem visual saat ini dipertahankan. Warna dan font masih berstatus usulan dalam DESIGN.md; pencatatan implementasi tidak menjadikannya keputusan final kelompok. Logo dan gambar bisnis final belum ditentukan.

## Accessibility & Inclusion

Fokus keyboard harus terlihat. Status harus disertai teks, tidak hanya warna. Tabel lebar dapat digeser di HP. Hormati prefers-reduced-motion. Ukuran pemeriksaan yang sudah dicatat: 375px dan 1280px.

## Evidence on Hand

Sumber konteks: PRD.md bagian 1–3 dan DESIGN.md. Proyek memiliki tokens.css, components.css, template Thymeleaf, katalog komponen docs/katalog.html, serta screenshot di docs/screenshots/.

Angka, nama bisnis, dan antrean demo tidak boleh dianggap bukti penggunaan pelanggan nyata.

## Batas persiapan Impeccable

Tahap ini hanya pemasangan, dokumentasi, dan scan. Jangan mengubah tampilan, aturan dialog.modal untuk B10, urutan Bootstrap lalu tokens.css lalu components.css, atau kelas dan ID yang dipakai JavaScript dan scripts/pengujian/.
