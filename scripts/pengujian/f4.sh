#!/usr/bin/env bash
# Uji Fase 4: reservasi, nomor antrean, kapasitas, riwayat, pembatalan.
BASE=http://localhost:8080
S="$TMPDIR/f4"
mkdir -p "$S"
JAR="$S/cust.jar"
JAR2="$S/cust2.jar"
MYSQL=/c/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe
OK=0; GAGAL=0

lulus() { echo "OK    - $1"; OK=$((OK+1)); }
gagal() { echo "GAGAL - $1"; GAGAL=$((GAGAL+1)); }
cari() { if echo "$1" | grep -qi -- "$2"; then lulus "$3"; else gagal "$3"; fi }
tidak() { if echo "$1" | grep -qi -- "$2"; then gagal "$3 (masih ada '$2')"; else lulus "$3"; fi }
db() { $MYSQL -u root queuecare -N -B -e "$1" 2>/dev/null; }

# bersihkan sisa data reservasi
db "DELETE FROM queues; DELETE FROM reservations; DELETE FROM users WHERE email LIKE 'f4%';"

TGL=$(date -d '+1 day' +%F)
MINGGU=$(date -d 'next sunday' +%F)
LUPASTI=$(date -d '-1 day' +%F)
JAUH=$(date -d '+15 day' +%F)
echo "tanggal uji: $TGL  minggu: $MINGGU  luar: $JAUH"

echo "== 1. register dan login =="
curl -s -c "$JAR2" -o /dev/null "$BASE/register"
curl -s -b "$JAR2" -c "$JAR2" -o /dev/null -w "register=%{http_code} " -X POST "$BASE/register" \
  --data-urlencode "name=Pelanggan F4" --data-urlencode "email=f4b@queuecare.id" \
  --data-urlencode "password=customer123" --data-urlencode "confirmPassword=customer123"
echo
cari "$(db "SELECT email FROM users WHERE email='f4b@queuecare.id';")" "f4b@queuecare.id" "akun customer kedua dibuat"

curl -s -c "$JAR" -o /dev/null "$BASE/login"
curl -s -b "$JAR" -c "$JAR" -o /dev/null -w "login=%{http_code} -> %{redirect_url}\n" -X POST "$BASE/login" \
  --data-urlencode "username=customer@queuecare.id" --data-urlencode "password=customer123"
cari "$(db "SELECT email FROM users WHERE email='customer@queuecare.id';")" "customer@queuecare.id" "akun customer seed ada"

echo "== 2. halaman form =="
HAL=$(curl -s -b "$JAR" "$BASE/reservation/create/42?tanggal=$TGL")
cari "$HAL" "Slot pada" "form reservasi terbuka"
cari "$HAL" "09:00" "slot 09:00 ada"
cari "$HAL" "Rp 45.000" "harga format ribuan di form"
ANON=$(curl -s -o /dev/null -w "%{http_code} %{redirect_url}" "$BASE/reservation/create/42?tanggal=$TGL")
cari "$ANON" "302" "tanpa login tidak bisa buka form"

echo "== 3. buat reservasi =="
RESP=$(curl -s -b "$JAR" -c "$JAR" -o /dev/null -w "%{http_code} %{redirect_url}" -X POST "$BASE/reservation/create/42" \
  --data-urlencode "tanggal=$TGL" --data-urlencode "jam=09:00" --data-urlencode "catatan=Uji fase 4")
echo "post=$RESP"
ID=$(echo "$RESP" | sed -n 's#.*/reservation/success/\([0-9]*\).*#\1#p')
cari "$RESP" "302" "reservasi berhasil, redirect ke halaman sukses"
SUK=$(curl -s -b "$JAR" "$BASE/reservation/success/$ID")
TGLKODE=$(echo "$TGL" | tr -d '-')
cari "$SUK" "QC-$TGLKODE-0001" "kode reservasi QC-YYYYMMDD-0001"
cari "$SUK" "Antrean" "halaman sukses menampilkan nomor antrean"
cari "$(db "SELECT reservation_code,status,reservation_time FROM reservations WHERE id=$ID;")" "QC-$TGLKODE-0001.*CONFIRMED.*09:00:00" "baris reservasi tersimpan CONFIRMED"
cari "$(db "SELECT queue_number,status,business_id FROM queues WHERE reservation_id=$ID;")" "1.*WAITING.*20" "antrean 1 WAITING milik bisnis 20"

echo "== 4. validasi ditolak =="
POST() { curl -s -b "$JAR" -o "$S/body" -w "%{http_code}" -X POST "$BASE/reservation/create/$1" \
  --data-urlencode "tanggal=$2" --data-urlencode "jam=$3"; }
B=$(POST 44 "$TGL" "09:00"); cari "$(cat "$S/body")" "Layanan ini sedang tidak aktif" "layanan nonaktif ditolak ($B)"
B=$(POST 45 "$TGL" "09:00"); cari "$(cat "$S/body")" "Bisnis sedang tidak aktif" "bisnis INACTIVE ditolak ($B)"
B=$(POST 58 "$TGL" "09:00"); cari "$(cat "$S/body")" "Bisnis sedang tidak aktif" "bisnis PENDING ditolak ($B)"
B=$(POST 42 "$MINGGU" "09:00"); cari "$(cat "$S/body")" "libur" "hari libur ditolak ($B)"
B=$(POST 42 "$JAUH" "09:00"); cari "$(cat "$S/body")" "14 hari" "tanggal > 14 hari ditolak ($B)"
B=$(POST 42 "$LUPASTI" "09:00"); cari "$(cat "$S/body")" "14 hari" "tanggal lampau ditolak ($B)"
B=$(POST 42 "$TGL" "09:10"); cari "$(cat "$S/body")" "slot yang valid" "jam bukan kelipatan 30 ditolak ($B)"
B=$(POST 42 "$TGL" "08:00"); cari "$(cat "$S/body")" "slot yang valid" "jam di luar jadwal ditolak ($B)"

echo "== 5. kapasitas dan balapan (bisnis 24, kapasitas 5) =="
for i in 1 2 3 4; do
  curl -s -b "$JAR" -o /dev/null -X POST "$BASE/reservation/create/54" \
    --data-urlencode "tanggal=$TGL" --data-urlencode "jam=10:00" --data-urlencode "catatan=isi kapasitas"
done
cari "$(db "SELECT COUNT(*) FROM reservations WHERE business_id=24 AND reservation_date='$TGL' AND reservation_time='10:00:00';")" "^4$" "4 reservasi isi sebagian kapasitas"
curl -s -b "$JAR" -w "%{http_code} %{redirect_url}" -X POST "$BASE/reservation/create/54" \
  --data-urlencode "tanggal=$TGL" --data-urlencode "jam=10:00" --data-urlencode "catatan=balapan 1" > "$S/r1" &
curl -s -b "$JAR" -w "%{http_code} %{redirect_url}" -X POST "$BASE/reservation/create/54" \
  --data-urlencode "tanggal=$TGL" --data-urlencode "jam=10:00" --data-urlencode "catatan=balapan 2" > "$S/r2" &
wait
echo "balapan: $(cat "$S/r1") | $(cat "$S/r2")"
SATU=0
if grep -q "302" "$S/r1"; then MENANG=1; else MENANG=0; fi
if grep -q "302" "$S/r2"; then MENANG2=1; else MENANG2=0; fi
if [ $((MENANG + MENANG2)) -eq 1 ]; then lulus "hanya satu dari dua request bersamaan yang lolos"; else gagal "balapan: lolos $MENANG dan $MENANG2"; fi
PENUH=0
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
BATAL=$(curl -s -b "$JAR" -o /dev/null -w "%{http_code} %{redirect_url}" -X POST "$BASE/reservations/$ID/batal")
cari "$BATAL" "302" "pembatalan redirect ke riwayat"
cari "$(db "SELECT status FROM reservations WHERE id=$ID;")" "^CANCELLED$" "reservasi jadi CANCELLED"
cari "$(db "SELECT status FROM queues WHERE reservation_id=$ID;")" "^CANCELLED$" "antrean jadi CANCELLED"
LAGI=$(curl -s -b "$JAR" -X POST "$BASE/reservation/create/42" --data-urlencode "tanggal=$TGL" --data-urlencode "jam=09:00")
cari "$LAGI" "302" "slot yang dibatalkan kembali tersedia"
BATAL2=$(curl -s -b "$JAR" -o /dev/null -w "%{http_code}" -X POST "$BASE/reservations/$ID/batal")
cari "$(curl -s -b "$JAR" "$BASE/reservations")" "tidak bisa dibatalkan" "antrean non-WAITING tidak bisa dibatalkan ($BATAL2)"

echo "== 8. antrean non-WAITING =="
ID3=$(db "SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE email='customer@queuecare.id') AND status='CONFIRMED' ORDER BY id DESC LIMIT 1;")
db "UPDATE queues SET status='CALLED' WHERE reservation_id=$ID3;"
BATAL3=$(curl -s -b "$JAR" -o /dev/null -w "%{http_code}" -X POST "$BASE/reservations/$ID3/batal")
cari "$(curl -s -b "$JAR" "$BASE/reservations")" "tidak bisa dibatalkan" "antrean CALLED tidak bisa dibatalkan ($BATAL3)"

echo "== 9. format harga di katalog dan detail =="
cari "$(curl -s "$BASE/businesses/20")" "Rp 45.000" "detail bisnis format ribuan"
cari "$(curl -s -b "$JAR" "$BASE/owner/services")" "Rp 45.000" "halaman layanan Owner format ribuan"

echo
echo "OTAL OK=$OK GAGAL=$GAGAL"
echo "BERSIHKAN"
db "DELETE FROM queues; DELETE FROM reservations; DELETE FROM users WHERE email LIKE 'f4%';"
echo "sisa reservasi: $(db 'SELECT COUNT(*) FROM reservations;')  sisa antrean: $(db 'SELECT COUNT(*) FROM queues;')"
