#!/usr/bin/env bash
# Uji Fase 7: dashboard dan laporan Owner serta Admin, daftar reservasi Admin.
BASE=http://localhost:8080
S="C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch/f7"
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

# cetak "label=nilai" dari kartu statistik halaman
statistik() { echo "$1" | grep -oE '<div class="statistik"><span class="statistik__angka">[0-9]+</span><span class="statistik__label">[^<]+' | sed -E 's/.*angka">([0-9]+)<\/span><span class="statistik__label">(.*)/\2=\1/'; }
# bandingkan satu label halaman dengan SQL
cocok() { local HAL="$1" LABEL="$2" SQL="$3" NAMA="$4"
  local P S; P=$(echo "$HAL" | grep -oE "<div class=\"statistik\"><span class=\"statistik__angka\">[0-9]+</span><span class=\"statistik__label\">$LABEL</span>" | grep -oE '[0-9]+' | head -1)
  S=$(db "$SQL")
  if [ "$P" = "$S" ]; then lulus "$NAMA: halaman $P = SQL $S"; else gagal "$NAMA: halaman '$P' vs SQL '$S'"; fi
}

JA="$S/admin.jar"; JO="$S/owner.jar"; JO2="$S/owner2.jar"; JC="$S/cust.jar"
TGL=$(date -d '+1 day' +%F)
echo "login admin=$(login "$JA" admin@queuecare.id admin123) owner=$(login "$JO" owner@queuecare.id owner123) owner2=$(login "$JO2" owner2@queuecare.id owner123) customer=$(login "$JC" customer@queuecare.id customer123)"
post "$JO" "/owner/businesses/20/pilih" "$TGL" > /dev/null
post "$JO2" "/owner/businesses/23/pilih" "$TGL" > /dev/null

echo "== 1. dashboard Admin vs SQL =="
H=$(curl -s -b "$JA" "$BASE/admin")
echo "$H" | statistik | sed 's/^/    /'
cocok "$H" "Pengguna" "SELECT COUNT(*) FROM users" "admin pengguna"
cocok "$H" "Customer" "SELECT COUNT(*) FROM users WHERE role='CUSTOMER'" "admin customer"
cocok "$H" "Owner" "SELECT COUNT(*) FROM users WHERE role='OWNER'" "admin owner"
cocok "$H" "Bisnis" "SELECT COUNT(*) FROM businesses" "admin bisnis"
cocok "$H" "Bisnis aktif" "SELECT COUNT(*) FROM businesses WHERE status='ACTIVE'" "admin bisnis aktif"
cocok "$H" "Bisnis pending" "SELECT COUNT(*) FROM businesses WHERE status='PENDING'" "admin bisnis pending"
cocok "$H" "Bisnis nonaktif" "SELECT COUNT(*) FROM businesses WHERE status='INACTIVE'" "admin bisnis nonaktif"
cocok "$H" "Reservasi" "SELECT COUNT(*) FROM reservations" "admin reservasi"
cari "$H" "Aktivitas terbaru" "tabel aktivitas terbaru ada"

echo "== 2. laporan Admin vs SQL =="
H=$(curl -s -b "$JA" "$BASE/admin/reports")
echo "$H" | statistik | sed 's/^/    /'
cocok "$H" "Bisnis" "SELECT COUNT(*) FROM businesses" "laporan admin bisnis"
cocok "$H" "Pengguna" "SELECT COUNT(*) FROM users" "laporan admin pengguna"
cocok "$H" "Reservasi pada rentang" "SELECT COUNT(*) FROM reservations" "laporan admin reservasi rentang"
echo "    per hari: $(echo "$H" | grep -A2 'Reservasi per hari' >/dev/null && echo ok)"
SQLHARI=$(db "SELECT reservation_date, COUNT(*) FROM reservations GROUP BY reservation_date ORDER BY reservation_date;")
HALHARI=$(echo "$H" | tr -d '\n' | grep -oE '<tr>[[:space:]]*<td>[0-9]{4}-[0-9]{2}-[0-9]{2}</td>[[:space:]]*<td class="tabel__angka">[0-9]+</td>' | sed -E 's|<tr>[[:space:]]*<td>([0-9-]+)</td>[[:space:]]*<td class="tabel__angka">([0-9]+)</td>|\1 \2|')
echo "    SQL per hari : $(echo "$SQLHARI" | tr '\n' ';')"
echo "    HTML per hari: $(echo "$HALHARI" | tr '\n' ';')"
N1=$(echo "$SQLHARI" | tr -d '\r\n\t '); N2=$(echo "$HALHARI" | tr -d '\r\n\t ')
if [ "$N1" = "$N2" ]; then lulus "reservasi per hari halaman = SQL ($N1)"; else gagal "reservasi per hari: SQL '$N1' vs HTML '$N2'"; fi
SQLSUM=$(db "SELECT status, COUNT(*) FROM queues GROUP BY status ORDER BY status;")
HALSUM=$(echo "$H" | tr -d '\n' | grep -oE 'lencana--[a-z-]+">[A-Z_]+</span></td>[[:space:]]*<td class="tabel__angka">[0-9]+' | sed -E 's|lencana--[a-z-]+">([A-Z_]+)</span></td>[[:space:]]*<td class="tabel__angka">([0-9]+)|\1 \2|' | sort)
echo "    SQL status : $(echo "$SQLSUM" | tr '\n' ';')"
echo "    HTML status: $(echo "$HALSUM" | tr '\n' ';')"
N1=$(echo "$SQLSUM" | tr -d '\r\n\t '); N2=$(echo "$HALSUM" | tr -d '\r\n\t ')
if [ "$N1" = "$N2" ]; then lulus "ringkasan status antrean halaman = SQL ($N1)"; else gagal "ringkasan status: SQL '$N1' vs HTML '$N2'"; fi

echo "== 3. dashboard Owner vs SQL =="
H=$(curl -s -b "$JO" "$BASE/owner")
echo "$H" | statistik | sed 's/^/    /'
cocok "$H" "Reservasi hari ini" "SELECT COUNT(*) FROM reservations WHERE business_id=20 AND reservation_date=CURDATE()" "owner reservasi hari ini"
cocok "$H" "Antrean menunggu" "SELECT COUNT(*) FROM queues WHERE business_id=20 AND status='WAITING'" "owner menunggu"
cocok "$H" "Sedang dilayani" "SELECT COUNT(*) FROM queues WHERE business_id=20 AND status IN ('CALLED','IN_SERVICE')" "owner sedang dilayani"
cocok "$H" "Selesai hari ini" "SELECT COUNT(*) FROM queues q JOIN reservations r ON r.id=q.reservation_id WHERE q.business_id=20 AND q.status='COMPLETED' AND r.reservation_date=CURDATE()" "owner selesai hari ini"

echo "== 4. laporan Owner vs SQL =="
DARI=2026-11-01; SAMPAI=2026-11-30
H=$(curl -s -b "$JO" "$BASE/owner/reports?dari=$DARI&sampai=$SAMPAI")
echo "$H" | statistik | sed 's/^/    /'
cocok "$H" "Total reservasi" "SELECT COUNT(*) FROM reservations WHERE business_id=20 AND reservation_date BETWEEN '$DARI' AND '$SAMPAI'" "owner total reservasi"
cocok "$H" "Antrean selesai" "SELECT COUNT(*) FROM queues q JOIN reservations r ON r.id=q.reservation_id WHERE q.business_id=20 AND q.status='COMPLETED' AND r.reservation_date BETWEEN '$DARI' AND '$SAMPAI'" "owner selesai"
cocok "$H" "Dibatalkan" "SELECT COUNT(*) FROM queues q JOIN reservations r ON r.id=q.reservation_id WHERE q.business_id=20 AND q.status='CANCELLED' AND r.reservation_date BETWEEN '$DARI' AND '$SAMPAI'" "owner dibatalkan"
cocok "$H" "Tidak hadir" "SELECT COUNT(*) FROM queues q JOIN reservations r ON r.id=q.reservation_id WHERE q.business_id=20 AND q.status='NO_SHOW' AND r.reservation_date BETWEEN '$DARI' AND '$SAMPAI'" "owner tidak hadir"
cocok "$H" "Pelanggan unik" "SELECT COUNT(DISTINCT customer_id) FROM reservations WHERE business_id=20 AND reservation_date BETWEEN '$DARI' AND '$SAMPAI'" "owner pelanggan unik"
echo "    layanan terpopuler HTML: $(echo "$H" | tr -d '\n' | grep -oE '<tr><td>[^<]+</td><td class="tabel__angka">[0-9]+</td></tr>' | sed -E 's|<tr><td>([^<]+)</td><td class="tabel__angka">([0-9]+)</td></tr>|\1=\2|' | tr '\n' ';')"
echo "    layanan terpopuler SQL : $(db "SELECT s.name, COUNT(*) c FROM reservations r JOIN services s ON s.id=r.service_id WHERE r.business_id=20 AND r.reservation_date BETWEEN '$DARI' AND '$SAMPAI' GROUP BY s.name ORDER BY c DESC LIMIT 5;" | tr '\n' ';')"
N1=$(db "SELECT s.name, COUNT(*) c FROM reservations r JOIN services s ON s.id=r.service_id WHERE r.business_id=20 AND r.reservation_date BETWEEN '$DARI' AND '$SAMPAI' GROUP BY s.name ORDER BY c DESC LIMIT 5;" | tr -d '\r\n\t')
N2=$(echo "$H" | tr -d '\n' | grep -oE '<tr>[[:space:]]*<td>[^<]+</td>[[:space:]]*<td class="tabel__angka">[0-9]+</td>' | sed -E 's|<tr>[[:space:]]*<td>([^<]+)</td>[[:space:]]*<td class="tabel__angka">([0-9]+)</td>|\1\2|' | tr -d '\r\n\t')
if [ "$N1" = "$N2" ]; then lulus "layanan terpopuler halaman = SQL ($N1)"; else gagal "layanan terpopuler: SQL '$N1' vs HTML '$N2'"; fi

echo "== 5. filter tanggal =="
H=$(curl -s -b "$JO" "$BASE/owner/reports?dari=2030-01-01&sampai=2030-02-01")
cari "$H" "Belum ada data" "rentang tanpa data -> empty state"
H=$(curl -s -b "$JO" "$BASE/owner/reports?dari=2026-11-20&sampai=2026-11-10")
cari "$H" "Tanggal awal tidak boleh setelah tanggal akhir" "tanggal awal setelah akhir ditolak"
H=$(curl -s -b "$JA" "$BASE/admin/reports?dari=2030-01-01&sampai=2030-02-01")
cari "$H" "Belum ada data" "laporan admin rentang tanpa data -> empty state"
H=$(curl -s -b "$JA" "$BASE/admin/reports?dari=2026-11-20&sampai=2026-11-10")
cari "$H" "Tanggal awal tidak boleh setelah tanggal akhir" "laporan admin tanggal tidak valid ditolak"
H=$(curl -s -b "$JA" "$BASE/admin/reports?dari=2026-11-16&sampai=2026-11-16")
cocok "$H" "Reservasi pada rentang" "SELECT COUNT(*) FROM reservations WHERE reservation_date='2026-11-16'" "filter tanggal tepat"

echo "== 6. daftar reservasi Admin =="
J1="$S/d1.jar"; login "$J1" dummy1@queuecare.id customer123 > /dev/null
TK=$(csrf "$J1" "/reservation/create/42?tanggal=$TGL")
for jam in 11:00 13:00; do
  for i in 1 2 3 4 5 6; do
    curl -s -b "$J1" -o /dev/null -X POST "$BASE/reservation/create/42" --data-urlencode "_csrf=$TK" --data-urlencode "tanggal=$TGL" --data-urlencode "jam=$jam"
  done
done
TOTAL=$(db "SELECT COUNT(*) FROM reservations;")
echo "    total reservasi sekarang: $TOTAL"
H=$(curl -s -b "$JA" "$BASE/admin/reservations")
cari "$H" "Halaman <span" "paginasi tampil"
P1=$(echo "$H" | grep -c '<tr><td th:text="${b.kode}"')
cari "$(echo "$H" | grep -o 'Halaman <span[^<]*</span> dari <span[^<]*</span>')" "." "info halaman"
H2=$(curl -s -b "$JA" "$BASE/admin/reservations?hal=2")
cari "$H2" "Halaman <span" "halaman 2 bisa dibuka"
H=$(curl -s -b "$JA" "$BASE/admin/reservations?bisnis=20&status=CONFIRMED")
SQLF=$(db "SELECT COUNT(*) FROM reservations WHERE business_id=20 AND status='CONFIRMED';")
echo "    filter bisnis=20&status=CONFIRMED: halaman $(echo "$H" | grep -oE '>[0-9]+</span> reservasi' | head -1) | SQL $SQLF"
H=$(curl -s -b "$JA" "$BASE/admin/reservations?status=CANCELLED")
cari "$H" "CANCELLED" "filter status CANCELLED"
cari "$(curl -s -b "$JA" "$BASE/admin/reservations?bisnis=9999")" "Belum ada data" "filter bisnis tanpa hasil -> empty state"
H=$(curl -s -b "$JA" "$BASE/admin/reservations?dari=2026-11-16&sampai=2026-11-16")
cari "$H" "2026-11-16" "filter tanggal tampil"

echo "== 7. akses =="
K=$(curl -s -b "$JC" -o /dev/null -w "%{http_code}" "$BASE/admin"); [ "$K" = "403" ] && lulus "customer -> /admin 403" || gagal "customer -> /admin $K"
K=$(curl -s -b "$JC" -o /dev/null -w "%{http_code}" "$BASE/admin/reports"); [ "$K" = "403" ] && lulus "customer -> /admin/reports 403" || gagal "customer -> /admin/reports $K"
K=$(curl -s -b "$JC" -o /dev/null -w "%{http_code}" "$BASE/admin/reservations"); [ "$K" = "403" ] && lulus "customer -> /admin/reservations 403" || gagal "customer -> /admin/reservations $K"
K=$(curl -s -b "$JO" -o /dev/null -w "%{http_code}" "$BASE/admin/reports"); [ "$K" = "403" ] && lulus "owner -> /admin/reports 403" || gagal "owner -> /admin/reports $K"
K=$(curl -s -b "$JC" -o /dev/null -w "%{http_code}" "$BASE/owner/reports"); [ "$K" = "403" ] && lulus "customer -> /owner/reports 403" || gagal "customer -> /owner/reports $K"
K=$(curl -s -b "$JA" -o /dev/null -w "%{http_code}" "$BASE/owner/reports"); [ "$K" = "403" ] && lulus "admin -> /owner/reports 403" || gagal "admin -> /owner/reports $K"

echo "== 8. owner hanya melihat bisnisnya =="
# beri satu reservasi di bisnis Owner2 (Salon Aurora) supaya angkanya bisa dibandingkan
JD1="$S/d1.jar"; login "$JD1" dummy1@queuecare.id customer123 > /dev/null
TK=$(csrf "$JD1" "/reservation/create/51?tanggal=2026-11-16")
curl -s -b "$JD1" -o /dev/null -X POST "$BASE/reservation/create/51" --data-urlencode "_csrf=$TK" --data-urlencode "tanggal=2026-11-16" --data-urlencode "jam=10:00"
DARI=2026-11-01; SAMPAI=2026-11-30
H2=$(curl -s -b "$JO2" "$BASE/owner/reports?dari=$DARI&sampai=$SAMPAI")
echo "    owner2: $(echo "$H2" | statistik | tr '\n' ' ')"
cocok "$H2" "Total reservasi" "SELECT COUNT(*) FROM reservations WHERE business_id=23 AND reservation_date BETWEEN '$DARI' AND '$SAMPAI'" "owner2 total reservasi bisnis 23"
tidak "$H2" "Barbershop Senja" "laporan owner2 tidak memuat bisnis owner1"
H1=$(curl -s -b "$JO" "$BASE/owner/reports?dari=$DARI&sampai=$SAMPAI")
echo "    owner1: $(echo "$H1" | statistik | tr '\n' ' ')"
cocok "$H1" "Total reservasi" "SELECT COUNT(*) FROM reservations WHERE business_id=20 AND reservation_date BETWEEN '$DARI' AND '$SAMPAI'" "owner1 total reservasi bisnis 20"
tidak "$H1" "Salon Aurora" "laporan owner1 tidak memuat bisnis owner2"

echo
echo "TOTAL OK=$OK GAGAL=$GAGAL"
echo "BERSIHKAN"
db "DELETE FROM queues; DELETE FROM reservations;"
