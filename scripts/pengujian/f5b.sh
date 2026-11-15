#!/usr/bin/env bash
# Uji Fase 5: operasional antrean Owner dan monitoring Customer.
BASE=http://localhost:8080
S="C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch/f5"
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
post() { # jar, path, tanggal
  local J="$1" PATHX="$2" TGLX="$3"
  local T; T=$(csrf "$J" "/owner/businesses")
  curl -s -b "$J" -c "$J" -o /dev/null -w "%{http_code} %{redirect_url}" -X POST "$BASE$PATHX" \
    --data-urlencode "_csrf=$T" --data-urlencode "tanggal=$TGLX"
}

TGL=$(date -d '+1 day' +%F)
TGL2=$(date -d '+2 day' +%F)
JO="$S/owner.jar"; JO2="$S/owner2.jar"; JC="$S/cust.jar"; JC2="$S/cust2.jar"

echo "== 1. login dan pilih bisnis =="
echo "owner=$(login "$JO" owner@queuecare.id owner123)"
echo "pilih20=$(post "$JO" "/owner/businesses/20/pilih" "$TGL")"
echo "owner2=$(login "$JO2" owner2@queuecare.id owner123)"
echo "pilih23=$(post "$JO2" "/owner/businesses/23/pilih" "$TGL")"
echo "customer=$(login "$JC" dummy1@queuecare.id customer123)"
echo "customer2=$(login "$JC2" dummy2@queuecare.id customer123)"

Q1=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=1;")
Q2=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=2;")
Q3=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=3;")
Q4=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=4;")
Q5=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=5;")
Q6=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number=6;")
Q7=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL2' AND queue_number=1;")
echo "antrean: $Q1 $Q2 $Q3 $Q4 $Q5 $Q6 | lusa $Q7"

echo "== 2. halaman Owner =="
HAL=$(curl -s -b "$JO" "$BASE/owner/queues?tanggal=$TGL")
cari "$HAL" "Antrean" "halaman antrean Owner terbuka"
cari "$HAL" "WAITING" "tabel menampilkan status WAITING"
cari "$HAL" "Barbershop Senja" "bisnis aktif tampil"

echo "== 3. WAITING -> CALLED -> IN_SERVICE -> COMPLETED =="
echo "panggil=$(post "$JO" "/owner/queues/$Q1/panggil" "$TGL")"
cari "$(db "SELECT status FROM queues WHERE id=$Q1;")" "^CALLED$" "status jadi CALLED"
cari "$(db "SELECT called_at FROM queues WHERE id=$Q1;")" "." "called_at terisi"
echo "mulai=$(post "$JO" "/owner/queues/$Q1/mulai" "$TGL")"
cari "$(db "SELECT status FROM queues WHERE id=$Q1;")" "^IN_SERVICE$" "status jadi IN_SERVICE"
cari "$(db "SELECT started_at FROM queues WHERE id=$Q1;")" "." "started_at terisi"
echo "selesai=$(post "$JO" "/owner/queues/$Q1/selesai" "$TGL")"
cari "$(db "SELECT status FROM queues WHERE id=$Q1;")" "^COMPLETED$" "status jadi COMPLETED"
cari "$(db "SELECT completed_at FROM queues WHERE id=$Q1;")" "." "completed_at terisi"
R1=$(db "SELECT reservation_id FROM queues WHERE id=$Q1;")
cari "$(db "SELECT status FROM reservations WHERE id=$R1;")" "^COMPLETED$" "reservasi ikut COMPLETED"

echo "== 4. transisi tidak diizinkan =="
Q8=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND status='WAITING' LIMIT 1;")
echo "selesai paksa WAITING=$(post "$JO" "/owner/queues/$Q8/selesai" "$TGL")"
cari "$(curl -s -b "$JO" "$BASE/owner/queues?tanggal=$TGL")" "tidak bisa diubah menjadi COMPLETED" "WAITING langsung COMPLETED ditolak"
echo "selesai lagi COMPLETED=$(post "$JO" "/owner/queues/$Q1/selesai" "$TGL")"
cari "$(curl -s -b "$JO" "$BASE/owner/queues?tanggal=$TGL")" "tidak bisa diubah" "antrean COMPLETED tidak bisa diubah"
echo "panggil NO_SHOW=$(post "$JO" "/owner/queues/$Q5/panggil" "$TGL")"
cari "$(curl -s -b "$JO" "$BASE/owner/queues?tanggal=$TGL")" "tidak bisa diubah" "antrean NO_SHOW tidak bisa dipanggil"
echo "mulai CANCELLED=$(post "$JO" "/owner/queues/$Q6/mulai" "$TGL")"
cari "$(db "SELECT status FROM queues WHERE id=$Q6;")" "^CANCELLED$" "antrean CANCELLED tidak berubah"

echo "== 5. tidak hadir =="
echo "tidak-hadir=$(post "$JO" "/owner/queues/$Q2/tidak-hadir" "$TGL")"
cari "$(db "SELECT status FROM queues WHERE id=$Q2;")" "^NO_SHOW$" "status jadi NO_SHOW"
R2=$(db "SELECT reservation_id FROM queues WHERE id=$Q2;")
cari "$(db "SELECT status FROM reservations WHERE id=$R2;")" "^CANCELLED$" "reservasi ikut CANCELLED (usulan NO_SHOW)"

echo "== 6. batalkan dari Owner =="
echo "batalkan=$(post "$JO" "/owner/queues/$Q7/batalkan" "$TGL2")"
cari "$(db "SELECT status FROM queues WHERE id=$Q7;")" "^CANCELLED$" "antrean jadi CANCELLED"
R7=$(db "SELECT reservation_id FROM queues WHERE id=$Q7;")
cari "$(db "SELECT status FROM reservations WHERE id=$R7;")" "^CANCELLED$" "reservasi ikut CANCELLED"

echo "== 7. dua klik bersamaan =="
Q9=$(db "SELECT id FROM queues WHERE business_id=20 AND queue_date='$TGL' AND status='WAITING' LIMIT 1;")
T=$(csrf "$JO" "/owner/queues?tanggal=$TGL")
curl -s -b "$JO" -o /dev/null -w "%{http_code}" -X POST "$BASE/owner/queues/$Q9/panggil" --data-urlencode "_csrf=$T" --data-urlencode "tanggal=$TGL" > "$S/c1" &
curl -s -b "$JO" -o /dev/null -w "%{http_code}" -X POST "$BASE/owner/queues/$Q9/panggil" --data-urlencode "_csrf=$T" --data-urlencode "tanggal=$TGL" > "$S/c2" &
wait
echo "dua klik: $(cat "$S/c1") | $(cat "$S/c2")"
cari "$(db "SELECT status FROM queues WHERE id=$Q9;")" "^CALLED$" "hanya satu transisi yang masuk (status CALLED)"
cari "$(curl -s -b "$JO" "$BASE/owner/queues?tanggal=$TGL")" "tidak bisa diubah" "satu klik ditolak dengan pesan"

echo "== 8. kepemilikan Owner =="
echo "owner2 ubah antrean owner1=$(post "$JO2" "/owner/queues/$Q3/selesai" "$TGL")"
cari "$(db "SELECT status FROM queues WHERE id=$Q3;")" "^IN_SERVICE$" "antrean orang lain tidak berubah"
KODE=$(curl -s -b "$JO2" -o /dev/null -w '%{http_code}' "$BASE/owner/queues?tanggal=$TGL")
if [ "$KODE" = "403" ]; then lulus "owner2 membuka antrean bisnis owner1 -> 403"; else gagal "owner2 membuka -> $KODE"; fi

echo "== 9. halaman Customer =="
HAL=$(curl -s -b "$JC" "$BASE/queue/$Q3")
cari "$HAL" "Pantau antrean" "halaman monitoring terbuka"
cari "$HAL" "Posisi antrean kamu" "posisi antrean tampil"
cari "$HAL" "QC-" "kode reservasi tampil"
JSON=$(curl -s -b "$JC" "$BASE/queue/$Q3/status")
echo "json: $JSON"
cari "$JSON" '"status":"IN_SERVICE"' "JSON memuat status"
Posisi=$(db "SELECT COUNT(*) FROM queues WHERE business_id=20 AND queue_date='$TGL' AND queue_number < 3 AND status IN ('WAITING','CALLED','IN_SERVICE');")
echo "antrean aktif di depan nomor 3: $Posisi"
cari "$JSON" "\"posisi\":$((Posisi + 1))" "posisi = $((Posisi + 1)), status final tidak dihitung"
LAIN=$(curl -s -b "$JC2" -o /dev/null -w "%{http_code}" "$BASE/queue/$Q3")
if [ "$LAIN" = "404" ]; then lulus "customer lain membuka antrean orang lain -> 404"; else gagal "customer lain -> $LAIN"; fi

echo "== 10. tombol Pantau antrean =="
RS=$(db "SELECT r.id FROM reservations r JOIN queues q ON q.reservation_id=r.id WHERE q.id=$Q3;")
cari "$(curl -s -b "$JC" "$BASE/reservation/success/$RS")" "Pantau antrean" "tombol pantau di halaman sukses (belum final)"
RF=$(db "SELECT r.id FROM reservations r JOIN queues q ON q.reservation_id=r.id WHERE q.id=$Q1;")
tidak "$(curl -s -b "$JC" "$BASE/reservation/success/$RF")" "Pantau antrean" "tombol pantau hilang kalau antrean final"

echo
echo "TOTAL OK=$OK GAGAL=$GAGAL"
