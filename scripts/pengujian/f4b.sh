#!/usr/bin/env bash
# Uji Fase 4: reservasi, nomor antrean, kapasitas, riwayat, pembatalan.
BASE=http://localhost:8080
S="C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch/f4"
rm -rf "$S"; mkdir -p "$S"
JAR="$S/cust.jar"
JAR2="$S/cust2.jar"
MYSQL=/c/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe
OK=0; GAGAL=0

lulus() { echo "OK    - $1"; OK=$((OK+1)); }
gagal() { echo "GAGAL - $1"; GAGAL=$((GAGAL+1)); }
cari() { if echo "$1" | grep -qi -- "$2"; then lulus "$3"; else gagal "$3"; fi }
db() { $MYSQL -u root queuecare -N -B -e "$1" 2>/dev/null; }
csrf() { curl -s -b "$JAR" "$BASE$1" | grep -o 'name="_csrf"[^>]*' | head -1 | grep -o 'value="[^"]*"' | head -1 | cut -d'"' -f2; }
csrf2() { curl -s -b "$JAR2" "$BASE$1" | grep -o 'name="_csrf"[^>]*' | head -1 | grep -o 'value="[^"]*"' | head -1 | cut -d'"' -f2; }

postres() {
  local T; T=$(csrf "/reservation/create/$1?tanggal=$2")
  curl -s -b "$JAR" -c "$JAR" -o "$S/body" -w "%{http_code} %{redirect_url}" -X POST "$BASE/reservation/create/$1" \
    --data-urlencode "_csrf=$T" --data-urlencode "tanggal=$2" --data-urlencode "jam=$3" --data-urlencode "catatan=$4"
}

db "DELETE FROM queues; DELETE FROM reservations; DELETE FROM users WHERE email LIKE 'f4%';"

TGL=$(date -d '+1 day' +%F)
TGLKODE=$(echo "$TGL" | tr -d '-')
MINGGU=$(date -d 'next sunday' +%F)
LUPASTI=$(date -d '-1 day' +%F)
JAUH=$(date -d '+15 day' +%F)
echo "tanggal uji: $TGL (kode $TGLKODE)  minggu: $MINGGU  luar: $JAUH"

echo "== 1. register dan login =="
curl -s -c "$JAR2" -o /dev/null "$BASE/register"
T=$(csrf2 "/register")
curl -s -b "$JAR2" -c "$JAR2" -o /dev/null -w "register=%{http_code} -> %{redirect_url}\n" -X POST "$BASE/register" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Pelanggan F4" --data-urlencode "email=f4b@queuecare.id" \
  --data-urlencode "password=customer123" --data-urlencode "confirmPassword=customer123"
cari "$(db "SELECT email FROM users WHERE email='f4b@queuecare.id';")" "f4b@queuecare.id" "akun customer kedua dibuat"

curl -s -c "$JAR" -o /dev/null "$BASE/login"
T=$(csrf "/login")
curl -s -b "$JAR" -c "$JAR" -o /dev/null -w "login=%{http_code} -> %{redirect_url}\n" -X POST "$BASE/login" \
  --data-urlencode "_csrf=$T" --data-urlencode "username=customer@queuecare.id" --data-urlencode "password=customer123"
cari "$(curl -s -b "$JAR" "$BASE/customer")" "Dashboard" "login customer berhasil"

echo "== 2. halaman form =="
HAL=$(curl -s -b "$JAR" "$BASE/reservation/create/42?tanggal=$TGL")
cari "$HAL" "Slot pada" "form reservasi terbuka"
cari "$HAL" "09:00" "slot 09:00 ada"
cari "$HAL" "Rp 45.000" "harga format ribuan di form"
ANON=$(curl -s -o /dev/null -w "%{http_code} %{redirect_url}" "$BASE/reservation/create/42?tanggal=$TGL")
cari "$ANON" "302" "tanpa login diarahkan ke login"

echo "== 3. buat reservasi =="
RESP=$(postres 42 "$TGL" "09:00" "Uji fase 4")
echo "post=$RESP"
ID=$(echo "$RESP" | sed -n 's#.*/reservation/success/\([0-9]*\).*#\1#p')
cari "$RESP" "302" "reservasi berhasil, redirect ke halaman sukses"
SUK=$(curl -s -b "$JAR" "$BASE/reservation/success/$ID")
cari "$SUK" "QC-$TGLKODE-0001" "kode reservasi QC-YYYYMMDD-0001"
cari "$SUK" "Antrean" "halaman sukses menampilkan nomor antrean"
cari "$(db "SELECT reservation_code,status,reservation_time FROM reservations WHERE id=$ID;")" "QC-$TGLKODE-0001.*CONFIRMED.*09:00:00" "baris reservasi tersimpan CONFIRMED"
cari "$(db "SELECT queue_number,status,business_id FROM queues WHERE reservation_id=$ID;")" "1.*WAITING.*20" "antrean 1 WAITING milik bisnis 20"

echo "== 4. validasi ditolak =="
R=$(postres 44 "$TGL" "09:00" "-"); cari "$(cat "$S/body")" "Layanan ini sedang tidak aktif" "layanan nonaktif ditolak ($R)"
R=$(postres 45 "$TGL" "09:00" "-"); cari "$(cat "$S/body")" "Bisnis sedang tidak aktif" "bisnis INACTIVE ditolak ($R)"
R=$(postres 58 "$TGL" "09:00" "-"); cari "$(cat "$S/body")" "Bisnis sedang tidak aktif" "bisnis PENDING ditolak ($R)"
R=$(postres 42 "$MINGGU" "09:00" "-"); cari "$(cat "$S/body")" "libur" "hari libur ditolak ($R)"
R=$(postres 42 "$JAUH" "09:00" "-"); cari "$(cat "$S/body")" "14 hari" "tanggal > 14 hari ditolak ($R)"
R=$(postres 42 "$LUPASTI" "09:00" "-"); cari "$(cat "$S/body")" "14 hari" "tanggal lampau ditolak ($R)"
R=$(postres 42 "$TGL" "09:10" "-"); cari "$(cat "$S/body")" "slot yang valid" "jam bukan kelipatan 30 ditolak ($R)"
R=$(postres 42 "$TGL" "08:00" "-"); cari "$(cat "$S/body")" "slot yang valid" "jam di luar jadwal ditolak ($R)"

echo "== 5. kapasitas dan balapan (bisnis 24, kapasitas 5) =="
for i in 1 2 3 4; do
  postres 54 "$TGL" "10:00" "isi kapasitas" > /dev/null
done
cari "$(db "SELECT COUNT(*) FROM reservations WHERE business_id=24 AND reservation_date='$TGL' AND reservation_time='10:00:00';")" "^4$" "4 reservasi isi sebagian kapasitas"
TR=$(csrf "/reservation/create/54?tanggal=$TGL")
curl -s -b "$JAR" -o /dev/null -w "%{http_code} %{redirect_url}" -X POST "$BASE/reservation/create/54" \
  --data-urlencode "_csrf=$TR" --data-urlencode "tanggal=$TGL" --data-urlencode "jam=10:00" --data-urlencode "catatan=balapan 1" > "$S/r1" &
curl -s -b "$JAR" -o /dev/null -w "%{http_code} %{redirect_url}" -X POST "$BASE/reservation/create/54" \
  --data-urlencode "_csrf=$TR" --data-urlencode "tanggal=$TGL" --data-urlencode "jam=10:00" --data-urlencode "catatan=balapan 2" > "$S/r2" &
wait
echo "balapan: $(cat "$S/r1") | $(cat "$S/r2")"
MENANG=0; MENANG2=0
grep -q "302" "$S/r1" && MENANG=1
grep -q "302" "$S/r2" && MENANG2=1
if [ $((MENANG + MENANG2)) -eq 1 ]; then lulus "hanya satu dari dua request bersamaan yang lolos"; else gagal "balapan: lolos $MENANG dan $MENANG2"; fi
cari "$(db "SELECT COUNT(*) FROM reservations WHERE business_id=24 AND reservation_date='$TGL' AND reservation_time='10:00:00' AND status='CONFIRMED';")" "^5$" "slot penuh tetap 5, tidak meluber"
cari "$(db "SELECT COUNT(*) FROM queues WHERE business_id=24 AND queue_date='$TGL' AND status<>'CANCELLED';")" "^5$" "lima antrean aktif"
cari "$(db "SELECT GROUP_CONCAT(queue_number ORDER BY queue_number) FROM queues WHERE business_id=24 AND queue_date='$TGL';")" "^1,2,3,4,5$" "nomor antrean berurutan 1..5"
KODE=$(db "SELECT COUNT(DISTINCT reservation_code) FROM reservations;")
TOT=$(db "SELECT COUNT(*) FROM reservations;")
if [ "$KODE" = "$TOT" ]; then lulus "kode reservasi unik ($TOT reservasi)"; else gagal "ada kode duplikat: $KODE dari $TOT"; fi
NOMOR=$(db "SELECT COUNT(*) FROM (SELECT business_id,queue_date,queue_number FROM queues GROUP BY business_id,queue_date,queue_number HAVING COUNT(*)>1) x;")
if [ "$NOMOR" = "0" ]; then lulus "tidak ada nomor antrean duplikat per bisnis per tanggal"; else gagal "nomor antrean duplikat"; fi

echo "== 6. riwayat dan kepemilikan =="
RIW=$(curl -s -b "$JAR" "$BASE/reservations")
cari "$RIW" "QC-" "riwayat menampilkan kode"
cari "$RIW" "Rp 45.000" "riwayat menampilkan harga format ribuan"
LAIN=$(curl -s -o /dev/null -w "%{http_code}" -b "$JAR2" "$BASE/reservations/$ID")
if [ "$LAIN" = "404" ]; then lulus "customer lain membuka detail orang lain -> 404"; else gagal "customer lain -> $LAIN"; fi
cari "$(curl -s -b "$JAR" "$BASE/reservations/$ID")" "Status antrean" "halaman detail menampilkan status antrean"

echo "== 7. pembatalan =="
T=$(csrf "/reservations/$ID")
BATAL=$(curl -s -b "$JAR" -o /dev/null -w "%{http_code} %{redirect_url}" -X POST "$BASE/reservations/$ID/batal" --data-urlencode "_csrf=$T")
cari "$BATAL" "302" "pembatalan redirect ke riwayat"
cari "$(db "SELECT status FROM reservations WHERE id=$ID;")" "^CANCELLED$" "reservasi jadi CANCELLED"
cari "$(db "SELECT status FROM queues WHERE reservation_id=$ID;")" "^CANCELLED$" "antrean jadi CANCELLED"
LAGI=$(postres 42 "$TGL" "09:00" "slot bebas lagi")
cari "$LAGI" "302" "slot yang dibatalkan kembali tersedia"
T=$(csrf "/reservations/$ID")
BATAL2=$(curl -s -b "$JAR" -o /dev/null -w "%{http_code}" -X POST "$BASE/reservations/$ID/batal" --data-urlencode "_csrf=$T")
cari "$(curl -s -b "$JAR" "$BASE/reservations")" "tidak bisa dibatalkan" "antrean non-WAITING tidak bisa dibatalkan ($BATAL2)"

echo "== 8. antrean CALLED =="
ID3=$(db "SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE email='customer@queuecare.id') AND status='CONFIRMED' ORDER BY id DESC LIMIT 1;")
db "UPDATE queues SET status='CALLED' WHERE reservation_id=$ID3;"
T=$(csrf "/reservations/$ID3")
BATAL3=$(curl -s -b "$JAR" -o /dev/null -w "%{http_code}" -X POST "$BASE/reservations/$ID3/batal" --data-urlencode "_csrf=$T")
cari "$(curl -s -b "$JAR" "$BASE/reservations")" "tidak bisa dibatalkan" "antrean CALLED tidak bisa dibatalkan ($BATAL3)"
cari "$(db "SELECT status FROM reservations WHERE id=$ID3;")" "^CONFIRMED$" "reservasi CALLED tidak ikut berubah"

echo "== 9. format harga =="
cari "$(curl -s "$BASE/businesses/20")" "Rp 45.000" "detail bisnis format ribuan"
cari "$(curl -s -b "$JAR" "$BASE/owner/services")" "Rp 45.000" "halaman layanan Owner format ribuan"

echo
echo "TOTAL OK=$OK GAGAL=$GAGAL"
echo "BERSIHKAN"
db "DELETE FROM queues; DELETE FROM reservations; DELETE FROM users WHERE email LIKE 'f4%';"
echo "sisa reservasi: $(db 'SELECT COUNT(*) FROM reservations;')  sisa antrean: $(db 'SELECT COUNT(*) FROM queues;')  sisa users f4: $(db "SELECT COUNT(*) FROM users WHERE email LIKE 'f4%';")"
