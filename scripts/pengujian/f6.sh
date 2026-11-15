#!/usr/bin/env bash
# Uji Fase 6: SmartWait.
BASE=http://localhost:8080
S="C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch/f6"
rm -rf "$S"; mkdir -p "$S"
MYSQL=/c/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe
OK=0; GAGAL=0
lulus() { echo "OK    - $1"; OK=$((OK+1)); }
gagal() { echo "GAGAL - $1"; GAGAL=$((GAGAL+1)); }
cari() { if echo "$1" | grep -qi -- "$2"; then lulus "$3"; else gagal "$3"; fi }
tidak() { if echo "$1" | grep -qi -- "$2"; then gagal "$3 (masih ada '$2')"; else lulus "$3"; fi }
db() { $MYSQL -u root queuecare -N -B -e "$1" 2>/dev/null; }
csrf() { curl -s -b "$1" "$BASE$2" | grep -o 'name="_csrf"[^>]*' | head -1 | grep -o 'value="[^"]*"' | head -1 | cut -d'"' -f2; }
login() {
  local J="$1" EMAIL="$2" PASS="$3"
  curl -s -c "$J" -o /dev/null "$BASE/login"
  local T; T=$(csrf "$J" "/login")
  curl -s -b "$J" -c "$J" -o /dev/null -w "%{http_code}" -X POST "$BASE/login" \
    --data-urlencode "_csrf=$T" --data-urlencode "username=$EMAIL" --data-urlencode "password=$PASS"
}
post() { local J="$1" P="$2" TGLX="$3"; local T; T=$(csrf "$J" "/owner/businesses"); curl -s -b "$J" -c "$J" -o /dev/null -w "%{http_code}" -X POST "$BASE$P" --data-urlencode "_csrf=$T" --data-urlencode "tanggal=$TGLX"; }

TGL=$(date -d '+1 day' +%F)
JD2="$S/d2.jar"; JD1="$S/d1.jar"; JO="$S/owner.jar"
echo "login dummy2=$(login "$JD2" dummy2@queuecare.id customer123) dummy1=$(login "$JD1" dummy1@queuecare.id customer123) owner=$(login "$JO" owner@queuecare.id owner123)"
T=$(csrf "$JO" "/owner/businesses"); post "$JO" "/owner/businesses/20/pilih" "$TGL" > /dev/null

Q7=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=7;")
Q2=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=2;")
Q3=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=3;")
Q1=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=1;")
echo "antrean: $Q1 $Q2 $Q3 ... $Q7"

echo "== 1. angka estimasi dari data dummy =="
HAL=$(curl -s -b "$JD2" "$BASE/queue/$Q7")
echo "$HAL" | grep -o '<div class="smartwait__menit"[^>]*>[0-9-]*' | head -1
cari "$HAL" 'smartwait__menit" id="smartwait-menit">45<' "antrean 7 (3 orang x 30 menit / 2 petugas) = 45 menit"
cari "$HAL" "3</span> orang di depan" "tiga orang di depan disebut"
cari "$HAL" "Ini perkiraan, bukan jaminan waktu." "catatan perkiraan tampil"
JSON=$(curl -s -b "$JD2" "$BASE/queue/$Q7/status"); echo "json: $JSON"
cari "$JSON" '"estimasi":45' "JSON memuat estimasi 45"
cari "$JSON" '"orangDepan":3' "JSON memuat orangDepan 3"
R7=$(db "SELECT reservation_id FROM queues WHERE id=$Q7;")
cari "$(curl -s -b "$JD2" "$BASE/reservations/$R7")" "45" "detail reservasi menampilkan estimasi"

echo "== 2. pesan khusus =="
cari "$(curl -s -b "$JD1" "$BASE/queue/$Q1")" "Kamu berikutnya" "WAITING tanpa orang di depan -> Kamu berikutnya"
cari "$(curl -s -b "$JD2" "$BASE/queue/$Q2")" "Kamu sudah dipanggil, silakan menuju lokasi" "CALLED -> pesan sudah dipanggil"
tidak "$(curl -s -b "$JD1" "$BASE/queue/$Q3")" "smartwait" "IN_SERVICE -> tidak ada estimasi"
Q4=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=4;")
tidak "$(curl -s -b "$JD2" "$BASE/queue/$Q4")" "smartwait" "COMPLETED -> tidak ada estimasi"

echo "== 3. petugas 0 =="
db "UPDATE businesses SET active_staff_count=0 WHERE id=20;"
cari "$(curl -s -b "$JD2" "$BASE/queue/$Q7")" "Estimasi belum tersedia" "petugas 0 -> Estimasi belum tersedia"
cari "$(curl -s -b "$JD2" "$BASE/queue/$Q7/status")" '"estimasi":null' "JSON estimasi null saat petugas 0"
cari "$(curl -s -b "$JD2" "$BASE/queue/$Q7/status")" '"pesan":"Estimasi belum tersedia"' "JSON memuat pesan"
db "UPDATE businesses SET active_staff_count=2 WHERE id=20;"
cari "$(curl -s -b "$JD2" "$BASE/queue/$Q7")" 'smartwait__menit" id="smartwait-menit">45<' "setelah petugas dikembalikan, estimasi 45 lagi"

echo "== 4. estimasi turun saat antrean di depan selesai =="
echo "selesai antrean 3: $(post "$JO" "/owner/queues/$Q3/selesai" "$TGL")"
HAL=$(curl -s -b "$JD2" "$BASE/queue/$Q7")
echo "$HAL" | grep -o '<div class="smartwait__menit"[^>]*>[0-9-]*' | head -1
cari "$HAL" 'smartwait__menit" id="smartwait-menit">30<' "antrean 3 selesai -> (2 x 30) / 2 = 30 menit"

echo
echo "TOTAL OK=$OK GAGAL=$GAGAL"
