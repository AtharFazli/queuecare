#!/usr/bin/env bash
BASE=http://localhost:8080
W="C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"
MYSQL="/c/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe"

csrf() { curl -s -m 5 -b "$1" -c "$1" "$BASE$2" | tr '\n' ' ' | grep -o 'name="_csrf"[^>]*' | head -1 | grep -o 'value="[^"]*"' | cut -d'"' -f2; }
login() { local jar="$1" e="$2" p="$3"; rm -f "$jar"; local t=$(csrf "$jar" /login); curl -s -m 10 -b "$jar" -c "$jar" -o /dev/null -X POST --data-urlencode "_csrf=$t" --data-urlencode "username=$e" --data-urlencode "password=$p" "$BASE/login"; }
get() { local jar="$1" path="$2"; if [ -n "$jar" ]; then curl -s -m 10 -H "Accept: text/html" -b "$jar" -c "$jar" -o "$W/body.html" -w "%{http_code}|%{redirect_url}" "$BASE$path"; else curl -s -m 10 -H "Accept: text/html" -o "$W/body.html" -w "%{http_code}|%{redirect_url}" "$BASE$path"; fi }
hanya() { grep -o "$1" "$W/body.html" | head -1; }
db() { "$MYSQL" -u root -N -e "$1" 2>/dev/null; }

SENJA=20; GRIYA=21; LESTARI=22; AURORA=23; JAYA=24; CANTIK=25
KAT_BARBER=$(db "SELECT id FROM queuecare.categories WHERE name='Barbershop'")
KAT_SALON=$(db "SELECT id FROM queuecare.categories WHERE name='Salon'")
echo "kategori barbershop=$KAT_BARBER salon=$KAT_SALON"

echo "== publik: katalog =="
echo -n "GET /businesses -> "; get "" /businesses; echo " | $(hanya 'Barbershop Senja') $(hanya 'Salon Lestari') $(hanya 'Salon Aurora') $(hanya 'Servis Laptop Jaya')"
echo -n "  PENDING/INACTIVE bocor? (0=aman) -> "; grep -c "Klinik Cantik Sehat\|Pangkas Rambut Griya" "$W/body.html"
JA="$W/f3a.txt"; JO="$W/f3o.txt"; JC="$W/f3c.txt"
login "$JA" admin@queuecare.id admin123; login "$JO" owner@queuecare.id owner123; login "$JC" customer@queuecare.id customer123
echo -n "GET /businesses (admin) -> "; get "$JA" /businesses; echo " | $(hanya 'Barbershop Senja')"
echo -n "GET /businesses (owner) -> "; get "$JO" /businesses; echo " | $(hanya 'Barbershop Senja')"
echo -n "GET /businesses (customer) -> "; get "$JC" /businesses; echo " | $(hanya 'Barbershop Senja')"

echo "== pencarian =="
echo -n "?q=Salon -> "; get "" "/businesses?q=Salon"; echo " | $(hanya 'Salon Lestari') $(hanya 'Salon Aurora')"
echo -n "?q=barber -> "; get "" "/businesses?q=barber"; echo " | $(hanya 'Barbershop Senja')"
echo -n "?q=zzz -> "; get "" "/businesses?q=zzz"; echo " | $(hanya 'Bisnis tidak ditemukan')"

echo "== filter kategori =="
echo -n "?kategori=$KAT_BARBER -> "; get "" "/businesses?kategori=$KAT_BARBER"; echo " | $(hanya 'Barbershop Senja')"
echo -n "?q=salon&kategori=$KAT_SALON -> "; get "" "/businesses?q=salon&kategori=$KAT_SALON"; echo " | $(hanya 'Salon Lestari') $(hanya 'Salon Aurora')"
echo -n "?q=salon&kategori=$KAT_BARBER (tak cocok) -> "; get "" "/businesses?q=salon&kategori=$KAT_BARBER"; echo " | $(hanya 'Bisnis tidak ditemukan')"

echo "== detail =="
echo -n "GET /businesses/$SENJA -> "; get "" "/businesses/$SENJA"; echo " | $(hanya 'Jam operasional') $(hanya 'Cukur rambut')"
echo -n "  layanan nonaktif bocor? (0=aman) -> "; grep -c "Cukur anak" "$W/body.html"
echo -n "  hari libur ditandai? -> "; hanya "Libur"
echo -n "GET /businesses/$GRIYA (INACTIVE) -> "; get "" "/businesses/$GRIYA"; echo " | $(hanya 'Halaman tidak ditemukan')"
echo -n "GET /businesses/$CANTIK (PENDING) -> "; get "" "/businesses/$CANTIK"; echo " | $(hanya 'Halaman tidak ditemukan')"
echo -n "GET /businesses/9999 -> "; get "" "/businesses/9999"; echo " | $(hanya 'Halaman tidak ditemukan')"

echo "== reservasi =="
echo -n "tanpa login -> "; get "" /reservation/create/42; echo
echo -n "customer -> "; get "$JC" /reservation/create/42; echo " | $(hanya 'Fitur reservasi segera hadir')"
echo -n "owner -> "; get "$JO" /reservation/create/42; echo " | $(hanya 'Akses ditolak')"
echo -n "admin -> "; get "$JA" /reservation/create/42; echo " | $(hanya 'Akses ditolak')"

echo "== landing =="
echo -n "GET / -> "; get "" /; echo " | $(hanya 'Cari bisnis')"
grep -o 'href="/businesses"' "$W/body.html" | head -1
echo "== selesai =="
