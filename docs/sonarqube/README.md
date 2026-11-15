# SonarQube

Status: **BELUM DIJALANKAN.**

## Kenapa belum dijalankan

Tidak ada server SonarQube lokal yang berjalan, dan tidak ada token SonarCloud. Tidak ada hasil analisis yang bisa dilaporkan, dan tidak ada angka yang dibuat-buat.

## Yang sudah disiapkan

`docs/sonarqube/sonar-project.properties` berisi konfigurasi untuk SonarQube/SonarCloud.

## Langkah menjalankannya

### SonarQube lokal

1. Pasang SonarQube Community Edition dan jalankan di `http://localhost:9000`.
2. Buat project `queuecare` dan token.
3. Pasang `sonar-scanner` dan jalankan:

   ```bash
   sonar-scanner \
     -Dsonar.projectKey=queuecare \
     -Dsonar.sources=src/main/java \
     -Dsonar.java.binaries=target/classes \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.token=TOKEN
   ```

### SonarCloud

1. Buat project di `https://sonarcloud.io` untuk `com.queuecare:queuecare`.
2. Jalankan:

   ```bash
   mvn -B clean verify sonar:sonar \
     -Dsonar.projectKey=KELOMPOK_queuecare \
     -Dsonar.organization=KELOMPOK \
     -Dsonar.host.url=https://sonarcloud.io \
     -Dsonar.token=TOKEN
   ```

3. Simpan tangkapan layar halaman issue dan coverage ke folder ini.

## Catatan

Karena `mvn clean package` dipakai sebagai build akhir, `target/classes` tersedia saat analisis. `target/` tidak masuk Git.
