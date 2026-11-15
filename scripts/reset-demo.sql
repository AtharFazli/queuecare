-- Reset data demo QueueCare.
-- Aman dijalankan berulang: mengosongkan reservasi dan antrean, membuang akun dummy,
-- lalu membuat ulang akun dummy. Antrean demo diisi aplikasi saat start berikutnya
-- (lihat DummyDataSeeder) supaya tanggalnya selalu hari ini.
--
-- Cara pakai:
--   mysql -u root queuecare < scripts/reset-demo.sql
--   lalu restart aplikasi (mvn spring-boot:run) dengan app.seed-dummy=true

USE queuecare;

DELETE FROM queues;
DELETE FROM reservations;

DELETE FROM users WHERE email IN ('dummy1@queuecare.id', 'dummy2@queuecare.id');

-- Akun dummy dibuat ulang dengan password yang sama dengan akun Customer demo.
INSERT INTO users(name, email, password_hash, role, active, created_at, updated_at)
SELECT 'Dummy Satu (Data dummy)', 'dummy1@queuecare.id', password_hash, 'CUSTOMER', 1, NOW(), NOW()
FROM users WHERE email = 'customer@queuecare.id';

INSERT INTO users(name, email, password_hash, role, active, created_at, updated_at)
SELECT 'Dummy Dua (Data dummy)', 'dummy2@queuecare.id', password_hash, 'CUSTOMER', 1, NOW(), NOW()
FROM users WHERE email = 'customer@queuecare.id';
