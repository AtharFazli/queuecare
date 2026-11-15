#!/usr/bin/env bash
# Uji regresi Fase 8: jalankan ulang semua skrip uji dari fase 1 sampai 7b.
S="C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"
MYSQL="/c/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe"
MVN='cmd.exe /c "C:\tools\apache-maven-3.9.16\bin\mvn.cmd -B spring-boot:run"'
export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"

matikan() {
  local PID=$(netstat -ano | grep -i listening | grep ':8080' | head -1 | awk '{print $NF}')
  [ -n "$PID" ] && cmd.exe /c "taskkill /F /PID $PID" >/dev/null 2>&1
  sleep 2
}
hidupkan() {
  nohup bash -c "cd /d/PBO-Java && $MVN" > "$S/regresi-app.log" 2>&1 &
  for i in $(seq 1 40); do
    sleep 3
    code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/login)
    [ "$code" = "200" ] && return 0
  done
  echo "APP GAGAL NAIK"; return 1
}
reset_db() { "$MYSQL" -u root queuecare -e "DELETE FROM queues; DELETE FROM reservations;" >/dev/null; }

echo "### 1. login & register (Fase 1)"
bash "$S/qc_test.sh" 2>&1 | grep -c "ok:"

echo "### 2. katalog (Fase 3)"
bash "$S/qc_fase3b.sh" 2>&1 | grep -E "bocor|selesai"

echo "### 3. reservasi (Fase 4)"
matikan; reset_db; hidupkan || exit 1
bash "$S/f4c.sh" 2>&1 | grep -E "TOTAL|GAGAL"

echo "### 4. operasional antrean (Fase 5)"
matikan; reset_db; hidupkan || exit 1
bash "$S/f5c.sh" 2>&1 | grep -E "TOTAL|GAGAL"

echo "### 5. SmartWait (Fase 6)"
matikan; reset_db; hidupkan || exit 1
bash "$S/f6.sh" 2>&1 | grep -E "TOTAL|GAGAL"

echo "### 6. dashboard & laporan (Fase 7)"
matikan; reset_db; hidupkan || exit 1
bash "$S/f7.sh" 2>&1 | grep -E "TOTAL|GAGAL"

echo "### 7. pengelolaan akun (Fase 7b)"
matikan; reset_db; hidupkan || exit 1
bash "$S/f7b.sh" 2>&1 | grep -E "TOTAL|GAGAL"

echo "### 8. unit test"
cd /d/PBO-Java && cmd.exe /c "C:\tools\apache-maven-3.9.16\bin\mvn.cmd -B test" 2>&1 | grep -E "Tests run:.*Failures|BUILD"

echo "### 9. bersih"
"$MYSQL" -u root queuecare -e "UPDATE users u JOIN users d ON d.email='dummy1@queuecare.id' SET u.password_hash=d.password_hash, u.name='Customer Demo', u.email='customer@queuecare.id', u.active=1 WHERE u.email IN ('customer@queuecare.id','customerbaru@queuecare.id'); DELETE FROM users WHERE email LIKE 'uji%@queuecare.id'; DELETE FROM users WHERE email IN ('barubaru@queuecare.id','pendek@queuecare.id','customerbaru@queuecare.id','baru@queuecare.id','f4b@queuecare.id'); DELETE FROM queues; DELETE FROM reservations;"
"$MYSQL" -u root -e "DROP DATABASE IF EXISTS queuecare_test;"
"$MYSQL" -u root queuecare -e "SELECT COUNT(*) pengguna FROM users; SELECT COUNT(*) antrean FROM queues;"
echo "### SELESAI"
