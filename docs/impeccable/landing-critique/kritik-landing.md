Method: dual-agent (A: sa-0-48ae056d · B: sa-1-c2f9e1dd)

# Kritik landing QueueCare — draf keputusan

Target `src/main/resources/templates/home.html`, route `/`, mode Persuade. Baseline `43b837ab238ed1fa1d0a202ebd64d0e3bfe03835`. Ini kritik, bukan pekerjaan desain. Tidak ada perubahan aplikasi dari bagian C.

## Kesimpulan

Tiket antrean dan SmartWait membuat manfaat QueueCare konkret. Komposisi landing masih mudah dipakai ulang oleh produk reservasi lain. Hambatan utama ialah tindakan mencari yang belum dimulai di landing, kebutuhan akun yang belum dijelaskan, dan akhir halaman yang tidak menawarkan langkah berikutnya. Tidak ada bukti bahwa link Cari bisnis rusak atau CTA berada di luar viewport pertama.

Skor Nielsen 19/28, 67,86%, Acceptable menurut skala Impeccable. Ini penilaian ahli, bukan metrik konversi, survei pelanggan, atau putusan produksi seluruh aplikasi.

## Bukti

- Screenshot parent dilihat langsung: `landing-375.png`, `landing-1280.png`, serta versi `-viewport.png`, viewport 375×900 dan 1280×900. Full page masing-masing 1729px dan 1022px tinggi; jangan menyebut tinggi ini sebagai tinggi viewport.
- DOM parent `dom.json`: lebar dokumen 375/1280px sesuai viewport; CTA utama y351/330px, tinggi 40px; H1 40px. Assessment A memeriksa ulang melalui konteks Edge baru, menu HP, satu fokus CTA, browse anonim, Back, dan CSS tersaji. Bukti interaksi A berupa laporan self-report; raw trace berada di transcript subagen, bukan JSON DOM baru yang tersimpan di repo.
- B menjalankan source detector serta URL detector. Source: satu primary flat-type-hierarchy dan satu advisory design-system-color; artefak Thymeleaf karena th:replace/th:href tidak dirender. Hasil ini tidak muncul pada scan URL.
- URL detector pada 375×812 dan 1280×900 masing-masing tiga primary, satu advisory. Dua primary low-contrast/gray-on-color merujuk satu copyright footer. Primary side-tab merujuk div.tiket__nomor; pemisah dashed itu bagian tiket yang sudah didokumentasikan, bukan alasan otomatis menghapusnya. Advisory straggler menunjuk kata “langkah.” pada langkah kedua; periksa saat copy disentuh, bukan prioritas utama.
- Footer: warna teks #546A6F di atas #0A444D, 14px; rasio 1,8841:1, di bawah 4,5:1 untuk teks normal. Detector membulatkan 1,9:1.

## Skor heuristik

| Heuristik | Skor | Alasan ringkas |
|---|---:|---|
| 1 Status terlihat | 3 | Contoh jelas, status bertulisan, menu memperbarui aria-expanded; Beranda belum aria-current. |
| 2 Bahasa dunia nyata | 2 | Copy utama alami; WAITING dan waktu kebutuhan akun belum jelas. |
| 3 Kendali pengguna | 3 | Katalog anonim, menu bisa ditutup, Back bekerja; alur reservasi bukan cakupan kritik. |
| 4 Konsistensi | 3 | Sistem visual konsisten; label status publik dan padding tombol menu berbeda. |
| 5 Pencegahan kesalahan | 3 | Label contoh dan disclaimer jujur; kebutuhan akun belum diterangkan. |
| 6 Pengenalan dibanding ingatan | 3 | Tiket dan langkah terbaca; pilihan bisnis nyata baru terlihat setelah navigasi. |
| 7 Efisiensi pengguna mahir | n/a | Shortcut/bulk action bukan pekerjaan landing Persuade. |
| 8 Estetika dan kesederhanaan | 2 | Bersih, tetapi contoh berulang, penutup tanpa aksi, target HP kecil. |
| 9 Pemulihan error | n/a | Landing tanpa form/error state yang diuji. |
| 10 Bantuan/dokumentasi | n/a | Landing pendek tidak memerlukan sistem bantuan tersendiri. |
| Total | 19/28 | Tujuh heuristik dinilai, tiga n/a. |

## Prioritas rekomendasi

1. P1 — Mulai pencarian di hero. Sekarang `.hero__tindakan` hanya link. Usulan: form GET ke `/businesses`, parameter `q`, komponen `.field`/`.field__input` incumbent, submit kosong tetap boleh menjelajah. Tidak perlu API, framework, filter, atau klaim ketersediaan baru. Link sekarang bekerja; ini usulan peningkatan Persuade, bukan bug transaksi.
2. P1 — Jelaskan kebutuhan akun sebelum orang memutuskan. “Cari bisnis” dan “Daftar” berdampingan, Daftar juga ada di navbar. Usulan copy: “Lihat bisnis dan layanan tanpa masuk. Masuk saat membuat reservasi.” Turunkan prioritas Daftar atau pertahankan sebagai opsi ringan; keputusan pengguna masih terbuka.
3. P2 — Ringkas SmartWait kedua dan beri aksi penutup. `.hero .smartwait` dan `.sorotan .smartwait` mengulang 30 menit/4 orang serta disclaimer; setelah hero tidak ada CTA browse lagi. Pertahankan rumus dan batas perkiraan, lalu berikan Cari bisnis di akhir. Tidak perlu angka palsu atau testimoni.
4. P2 — Perbesar area sentuh HP yang relevan. A mengukur Menu 32px dan aksi hero 40px tinggi. Usulan target kenyamanan 44px, scope landing/menu terkait. Ini bukan klaim otomatis gagal WCAG minimum 24px; belum diuji perangkat fisik.
5. P2 — Perbaiki kontras copyright lokal di footer. Gunakan token yang sudah cocok untuk latar gelap, lalu ukur ulang minimal 4,5:1. Tidak perlu mengganti palet atau seluruh teks redup.
6. Pertahankan tiket amber sebagai bukti hasil, headline manfaat yang langsung, CTA utama yang sudah tampak di viewport pertama, serta tiga langkah tanpa tambahan kartu dekoratif.
7. Pertahankan label Contoh dan “Ini perkiraan, bukan jaminan waktu.” Jangan mengarang tingkat akurasi, pelanggan, rating, AI, atau notifikasi yang belum ada. WAITING dapat kelak ditampilkan sebagai Menunggu dengan enum dan selector tetap.

## Beban kognitif dan persona

Checklist A: satu dari delapan gagal, yaitu browse dan komitmen akun ditawarkan sebelum memilih bisnis. Beban lokal rendah; bukan bukti persuasi efektif. Kelompok hero dua pilihan, menu tiga tujuan; tidak ditemukan kelompok lokal lebih dari empat tujuan unik.

Jordan/pengguna pertama harus menebak kebutuhan akun dan arti WAITING. Casey/pengguna HP menghadapi kontrol 32–40px dan perlu scroll kembali setelah membaca bawah. Riley/peninjau janji tidak mendapat bukti baru dari angka demo yang berulang. Customer pencari layanan membutuhkan browse anonim yang jelas; Owner lama cukup mendapat jalur Masuk, tidak perlu menjadikan landing dashboard tiga role. Persona ini simulasi evaluasi, bukan orang yang diwawancarai.

Alur emosi yang diusulkan: manfaat kendali pada headline, bukti lewat tiket, mulai mencari, pahami langkah, percaya estimasi dengan batasnya, lalu tindakan lanjut. Peak tiket sudah kuat; end saat ini berhenti pada contoh ulang dan kredit tugas. Kredit “Dibuat untuk tugas PBO” adalah fakta; jangan dihapus demi berpura-pura sudah produk publik.

## Metode dan keterbatasan

A dan B terisolasi, A tidak melihat detector; sintesis dibuat setelah keduanya selesai. B tidak berhasil membuka browser Camofox. Fallback Playwright B diblokir approval sebelum eksekusi dan tidak dicoba ulang atau dialihkan untuk menghindari blokir. Karena preflight mutasi gagal, live-server dan overlay tidak dimulai. URL detector dipakai sebagai alternatif bukti render; tidak diklaim ada overlay terlihat. Screenshot tersedia dari parent, interaksi terpisah dari A. Ini membatasi metode B, bukan alasan mengarang hasil.

Tidak ada perubahan template/CSS/JS, selector, konfigurasi, atau database pada C. Tidak ada browser overlay server yang perlu dihentikan. Server uji B 8081 sudah dihentikan; aplikasi awal 8080 tetap berjalan.

Tidak menilai screen reader lengkap, seluruh urutan keyboard, perangkat fisik, zoom 200%, jaringan lambat, akurasi SmartWait, atau konversi pelanggan. Kontras fokus amber berpotensi rendah menurut deklarasi; perlu audit kontekstual tersendiri sebelum memberi vonis kepatuhan. Tidak diperbaiki di sini.

Raw HTTP capture yang memuat CSRF sementara dipindah ke scratch dan tidak masuk commit; laporan B menyebut nama artefak pada saat penilaian, tetapi berkas HTML/header itu tidak termasuk paket dokumen Git. Raw detector dan bukti CSS tetap tersimpan. Tidak ada cookie atau kredensial sesi yang ditambahkan.

## Keputusan berikutnya

Draf `.impeccable/surfaces/landing-draft.md` belum disetujui sebagai arahan desain. Pilih apakah hero perlu pencarian langsung atau cukup link; tentukan posisi Daftar; tentukan apakah penutup cukup ringkas + CTA atau butuh contoh state lain. Semua implementasi menunggu tugas baru.

Questions skipped: pengguna membatasi C menjadi kritik dan draf untuk dikonfirmasi; tidak meminta wawancara atau implementasi desain sekarang. Pertanyaan keputusan dicatat di draf, tanpa mengubah scope.
