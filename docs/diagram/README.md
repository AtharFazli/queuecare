# Diagram

Sumber Mermaid (`.mmd`) dan hasil render (`.svg` dan `.png`).

| Berkas | Isi |
|---|---|
| `erd.mmd` / `.svg` / `.png` | ERD tujuh tabel: `users`, `categories`, `businesses`, `services`, `business_schedules`, `reservations`, `queues` |
| `class.mmd` / `.svg` / `.png` | Class diagram entity, relasi, dan enum |
| `usecase.mmd` / `.svg` / `.png` | Use case diagram tiga role dan 15 fitur yang dibangun |

Class diagram disusun dari entity dan relasi yang ada di `src/main/java/com/queuecare/entity/`, bukan dari dokumen lain. ERD mengikuti nama kolom aktual di database.

## Cara render ulang

Alat render lokal (Graphviz, PlantUML, mermaid-cli) tidak terpasang, jadi gambar dihasilkan dengan layanan `mermaid.ink`. Perintah yang dipakai ada di `scripts/pengujian/render-diagram.py`. Contoh:

```bash
python scripts/pengujian/render-diagram.py
```

Catatan: `mermaid.ink` menolak `subgraph` pada diagram use case ini (parse error), sehingga diagram use case digambar tanpa `subgraph`. ERD dan class diagram tidak terpengaruh.
