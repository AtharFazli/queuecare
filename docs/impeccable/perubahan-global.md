# A. Perubahan global installer pertama

Pengguna sudah meninjau daftar dan batas buktinya, lalu memilih “Sudah ditinjau; biarkan global, lanjut B dan C”. Tidak ada pemulihan global.

## Batas kepastian

Tidak ada snapshot sebelum pemasangan. Daftar tepat path yang masih ada tercatat di `perubahan-global-inventory.json` (184 entri global dan 19 file cache npm). Log installer, timestamp, dan isi saat ini membuktikan target penulisan; tidak membuktikan seluruh beda isi lama, apakah setiap file baru atau tertimpa, atau file lama yang mungkin dihapus saat update. Karena itu tidak disebut sebagai diff historis lengkap.

## Target yang ditulis installer

1. `C:\Users\MyBook Hype AMD\.claude\skills\impeccable\`
   Jenis: skill global diganti/diperbarui ke 4.9.0, launcher, referensi, data font, checksum, engine Windows x64 0.1.23. Bukan hook sendiri.
2. `C:\Users\MyBook Hype AMD\.gemini\skills\impeccable\`
   Jenis: skill global diganti/diperbarui ke 4.9.0 beserta engine 0.1.23. Bukan hook sendiri.
3. `C:\Users\MyBook Hype AMD\AppData\Local\hermes\skills\impeccable\`
   Jenis: skill profil aktif diperbarui ke 4.9.0 beserta engine 0.1.23. Bukan hook sendiri. SKILL.md kemudian punya perubahan tambahan setelah installer; jangan memakai hash sekarang sebagai hash hasil installer murni.
4. `C:\Users\MyBook Hype AMD\.claude\agents\impeccable-asset-producer.md`
5. `C:\Users\MyBook Hype AMD\.claude\agents\impeccable-documenter.md`
6. `C:\Users\MyBook Hype AMD\.claude\agents\impeccable-finish-reviewer.md`
7. `C:\Users\MyBook Hype AMD\.claude\agents\impeccable-manual-edit-applier.md`
   Jenis: empat definisi agent ditulis installer. Bukan hook. Tidak ada baseline untuk membedakan baru/tertimpa.
8. `C:\Users\MyBook Hype AMD\.claude\settings.json`
   Jenis: manifest konfigurasi ditulis saat installer. Ada tiga entri hook Impeccable: SessionStart (5 detik), PostToolUse matcher Edit|Write (5 detik), Stop (30 detik). Ketiganya memanggil launcher global Claude dengan argumen hook. Terdapat di konfigurasi; eksekusi nyata di Claude belum diuji. Tidak mengklaim entri yang sama sebelumnya tidak ada karena baseline tidak tersedia.
9. `C:\Users\MyBook Hype AMD\AppData\Local\hermes\plugins\impeccable-design-hook\plugin.yaml`
10. `C:\Users\MyBook Hype AMD\AppData\Local\hermes\plugins\impeccable-design-hook\__init__.py`
    Jenis: plugin hook dipasang, menyediakan transform_tool_result dan pre_verify. Tidak ada dalam plugins.enabled profil aktif saat diperiksa; belum aktif. Tidak diaktifkan dalam tugas ini.
11. `C:\Users\MyBook Hype AMD\AppData\Local\npm-cache\_npx\1a4eb60c8f6b0f89\`
    Jenis: cache npx paket impeccable 4.9.1 dan @impeccable/cli-windows-x64; bukan npm install global, bukan dependency aplikasi. Daftar 19 file dalam JSON. Tidak ada hook di cache ini.

## Yang bukan perubahan global installer ini

- `C:\Users\MyBook Hype AMD\.gemini\settings.json` memang memiliki BeforeTool/AfterAgent Impeccable, tetapi timestamp-nya 18 Oktober 2026. Tidak ditulis instalasi pertama 15 November 2026; jangan menghapus hook lama ini atas nama rollback tugas QueueCare.
- `D:\PBO-Java\.gemini\settings.json` ditulis installer pertama, tetapi merupakan file proyek, bukan global. Sudah dihapus pada tugas persiapan sebelumnya, sebelum permintaan ini. Tidak ada penghapusan baru sekarang.
- Antigravity, Devin/Windsurf, dan Kiro terdeteksi pada prompt, tetapi log hasil menyebut tiga skill yang diperbarui, dan tidak ditemukan path Impeccable pada folder ketiganya. Tidak ada bukti pemasangan pada ketiganya.
- `C:\Users\MyBook Hype AMD\.impeccable\update-check.json` timestamp 10 November 2026, mendahului installer; tidak dimasukkan sebagai hasil instalasi pertama.

## Keputusan yang diminta

Tinjau target di atas. Rekomendasi saat ini: biarkan global tetap, lanjut perbaikan dan kritik dalam proyek. Jika ingin pemulihan, tentukan targetnya; isi sebelum installer tidak tersedia sehingga rollback skill ke versi lama tidak bisa direkonstruksi dengan pasti. Tidak ada restore, disable, atau delete global tanpa persetujuan.
