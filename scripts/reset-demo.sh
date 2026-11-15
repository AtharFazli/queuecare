#!/usr/bin/env bash
# Reset data demo lalu ingatkan restart. Antrean demo diisi aplikasi saat start.
# Pakai: bash scripts/reset-demo.sh
set -e
MYSQL="${MYSQL:-/c/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe}"
"$MYSQL" -u root < "$(dirname "$0")/reset-demo.sql"
echo "Reservasi dan antrean dikosongkan, akun dummy dibuat ulang."
echo "Restart aplikasi (app.seed-dummy=true) supaya antrean demo terisi untuk hari ini."
