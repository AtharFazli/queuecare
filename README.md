# QueueCare

Website reservasi dan antrean untuk banyak bisnis jasa (barbershop, salon, servis laptop, klinik kecantikan). Customer mencari bisnis, memilih layanan dan jadwal, lalu mendapat kode reservasi dan nomor antrean. Owner mengelola layanan, jadwal, kapasitas, dan antrean. Admin mengelola akun, kategori, dan status bisnis.

SmartWait menghitung estimasi waktu tunggu dari durasi layanan pelanggan di depan dibagi jumlah petugas aktif. Ini rumus biasa, bukan AI.

## Prasyarat

- JDK 21 (diuji dengan Eclipse Temurin 21.0.13)
- Maven 3.9.x
- MySQL 8 (diuji dengan Laragon 8.0.30)
- Browser modern (Chrome atau Edge)

## Instalasi dan menjalankan

1. Buat database kosong bernama `queuecare` (utf8mb4_unicode_ci). Hibernate menyesuaikan tabel dari entity saat aplikasi pertama jalan (`spring.jpa.hibernate.ddl-auto=update`).

   ```sql
   CREATE DATABASE queuecare CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. Atur koneksi database lewat environment variable. Bawaan di `application.properties` adalah `root` tanpa password, cocok dengan Laragon.

   ```
   DB_USERNAME=root
   DB_PASSWORD=
   ```

3. Jalankan.

   ```bash
   mvn spring-boot:run
   ```

   Aplikasi ada di http://localhost:8080.

4. Akun demo dibuat otomatis saat pertama jalan (Fase 1 `DataInitializer`).

### Akun demo

| Role | Email | Password |
|---|---|---|
| Admin | admin@queuecare.id | admin123 |
| Owner | owner@queuecare.id | owner123 |
| Owner (bisnis kedua) | owner2@queuecare.id | owner123 |
| Customer | customer@queuecare.id | customer123 |
| Customer dummy | dummy1@queuecare.id | customer123 |
| Customer dummy | dummy2@queuecare.id | customer123 |

Owner demo memiliki enam bisnis (4 ACTIVE, 1 PENDING, 1 INACTIVE). Satu bisnis dipilih sebagai "bisnis aktif" lewat menu Bisnis; pilihan itu diperiksa ulang di setiap request.

### Mematikan seed data dummy

`app.seed-dummy=false` di `src/main/resources/application.properties`, atau jalankan dengan:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--app.seed-dummy=false
```

Seed dummy berisi enam bisnis, 21 layanan, jadwal, dan sembilan antrean. Seed hanya jalan bila tabel `reservations` masih kosong, dan semua data dummy bertanda "(Data dummy)".

### Reset data demo sebelum presentasi

```bash
bash scripts/reset-demo.sh
```

Skrip ini mengosongkan `queues` dan `reservations`, membuang dan membuat ulang akun dummy, lalu anda restart aplikasi supaya `DummyDataSeeder` mengisi antrean demo untuk hari ini. Lihat `scripts/reset-demo.sql`.

## Struktur proyek

```
src/main/java/com/queuecare
├── config/        SecurityConfig, SecurityBeans, DataInitializer, DummyDataSeeder, filter
├── controller/    Controller per role dan alur
├── dto/           Form dan objek baca untuk tampilan
├── entity/        User, Category, Business, Service, BusinessSchedule, Reservation, Queue
├── enums/         Role, BusinessStatus, ReservationStatus, QueueStatus
├── exception/     Exception bisnis dengan pesan untuk pengguna
├── repository/    Spring Data JPA
├── service/       Aturan bisnis: reservasi, antrean, SmartWait, laporan, akun
└── util/          RentangTanggal
src/main/resources
├── templates/     Thymeleaf per role (admin, owner, customer) dan halaman publik
└── static/css     tokens.css dan components.css
docs/              pengujian, daftar bug, diagram, presentasi, screenshot, sonarqube
database/          queuecare.sql (skema dan data seed)
scripts/           reset-demo.sql dan reset-demo.sh
```

## Catatan tampilan

Token warna, font, dan radius ada di `src/main/resources/static/css/tokens.css`. Komponen dan aturan responsif di `components.css`. Daftar komponen, halaman yang memakainya, dan aturan aksesibilitas ada di `DESIGN.md`.

## Build

```bash
mvn clean package
```

Hasil ada di `target/queuecare-0.0.1-SNAPSHOT.jar`. Folder `target/` tidak masuk Git.

## Dokumentasi dan pengujian

- `docs/pengujian.md` — enam skenario pengujian, checklist kelengkapan, status LOLOS/GAGAL/BELUM DIUJI.
- `docs/daftar-bug.md` — bug yang ditemukan, penyebab, dan perbaikan.
- `docs/diagram/` — ERD, class diagram, use case diagram (sumber `.mmd` dan gambar `.svg`/`.png`).
- `docs/presentasi.md` — outline slide dan skrip demo.
- `docs/sonarqube/` — status SonarQube dan langkah menjalankannya.
- `docs/screenshots/` — tangkapan layar halaman utama pada 375px dan 1280px.
- `scripts/pengujian/` — skrip uji yang dipakai, termasuk driver regresi `regresi8.sh`.
- `database/queuecare.sql` — skema dan data seed (password berupa hash BCrypt).

