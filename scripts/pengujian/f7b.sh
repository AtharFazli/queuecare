#!/usr/bin/env bash
# Uji Fase 7b: pengelolaan akun Admin, profil, ganti password.
BASE=http://localhost:8080
S="C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"
MYSQL="/c/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe"
OK=0; BAD=0

db() { "$MYSQL" -u root queuecare -N -B -e "$1" | tr -d '\r'; }
lulus() { OK=$((OK+1)); echo "OK    - $1"; }
gagal() { BAD=$((BAD+1)); echo "GAGAL - $1"; }
cari() { if echo "$1" | grep -qi -- "$2"; then lulus "$3"; else gagal "$3"; fi }
tidak() { if echo "$1" | grep -qi -- "$2"; then gagal "$3 (masih ada '$2')"; else lulus "$3"; fi }

csrf() { curl -s -b "$1" "$BASE$2" | grep -o 'name="_csrf"[^>]*' | head -1 | grep -o 'value="[^"]*"' | head -1 | cut -d'"' -f2; }

login() { # $1=jar $2=email $3=password -> mencetak kode HTTP akhir
  rm -f "$1"
  curl -s -c "$1" -o /dev/null "$BASE/login"
  local T=$(csrf "$1" "/login")
  curl -s -b "$1" -c "$1" -o /dev/null -w "%{http_code}" -X POST "$BASE/login" \
    --data-urlencode "_csrf=$T" --data-urlencode "username=$2" --data-urlencode "password=$3"
}

login_gagal() { # $1=jar $2=email $3=password -> mencetak URL redirect
  rm -f "$1"
  curl -s -c "$1" -o /dev/null "$BASE/login"
  local T=$(csrf "$1" "/login")
  curl -s -b "$1" -c "$1" -o /dev/null -w "%{redirect_url}" -X POST "$BASE/login" \
    --data-urlencode "_csrf=$T" --data-urlencode "username=$2" --data-urlencode "password=$3"
}

JA="$S/7b-a.jar"; JO="$S/7b-o.jar"; JC="$S/7b-c.jar"; JD="$S/7b-d.jar"; JX="$S/7b-x.jar"

echo "== 1. akses dasar =="
KODE=$(login "$JA" admin@queuecare.id admin123);   echo "    login admin -> $KODE"
KODE=$(login "$JO" owner@queuecare.id owner123);   echo "    login owner -> $KODE"
KODE=$(login "$JC" customer@queuecare.id customer123); echo "    login customer -> $KODE"
H=$(curl -s -b "$JA" "$BASE/admin/users")
echo "$H" | grep -q "Pengguna" && lulus "admin membuka /admin/users" || gagal "admin membuka /admin/users"
for P in 1 2 3 4 5 6; do :; done
JML=$(echo "$H" | tr -d '\n' | grep -oE '<tr>[[:space:]]*<td>[^<]+</td>' | wc -l)
[ "$JML" -ge 6 ] && lulus "tabel pengguna menampilkan $JML baris" || gagal "tabel pengguna hanya $JML baris"
cari "$H" "owner@queuecare.id" "email owner tampil"
cari "$H" "Nonaktifkan" "tombol nonaktif tampil"

echo "== 2. cari dan filter =="
H=$(curl -s -b "$JA" "$BASE/admin/users?q=owner2")
cari "$H" "owner2@queuecare.id" "pencarian email ditemukan"
tidak "$H" "customer@queuecare.id" "pencarian menyaring yang lain"
H=$(curl -s -b "$JA" "$BASE/admin/users?role=CUSTOMER")
cari "$H" "customer@queuecare.id" "filter role CUSTOMER"
tidak "$H" "owner@queuecare.id" "filter role menyaring OWNER"
H=$(curl -s -b "$JA" "$BASE/admin/users?q=zzzznihil")
cari "$H" "Belum ada data" "pencarian tanpa hasil memakai empty state"

echo "== 3. paginasi =="
"$MYSQL" -u root queuecare -e "
$(for i in $(seq 1 20); do echo "INSERT INTO users(name,email,password_hash,role,active,created_at,updated_at) VALUES('Uji $i','uji$i@queuecare.id','x','CUSTOMER',1,NOW(),NOW());"; done)"
H=$(curl -s -b "$JA" "$BASE/admin/users")
HAL1=$(echo "$H" | tr -d '\n' | grep -oE 'Halaman <span[^>]*>[0-9]+' | grep -oE '[0-9]+$' | head -1)
H2=$(curl -s -b "$JA" "$BASE/admin/users?hal=2")
HAL2=$(echo "$H2" | tr -d '\n' | grep -oE 'Halaman <span[^>]*>[0-9]+' | grep -oE '[0-9]+$' | head -1)
[ "$HAL1" = "1" ] && [ "$HAL2" = "2" ] && lulus "paginasi halaman 1 dan 2 terbaca" || gagal "paginasi: hal1=$HAL1 hal2=$HAL2"
cari "$H2" "Berikutnya\|Sebelumnya" "paginasi punya tautan"
H3=$(curl -s -b "$JA" "$BASE/admin/users?hal=99")
cari "$H3" "Halaman" "halaman di luar jangkauan tetap dirender"

echo "== 4. tambah pengguna =="
T=$(csrf "$JA" "/admin/users/tambah")
curl -s -b "$JA" -c "$JA" -o /dev/null -X POST "$BASE/admin/users" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Owner Baru" \
  --data-urlencode "email=barubaru@queuecare.id" --data-urlencode "role=OWNER" \
  --data-urlencode "password=rahasia123"
KODE=$(login "$JD" barubaru@queuecare.id rahasia123); echo "    login akun baru -> $KODE"
[ "$KODE" = "302" ] && lulus "akun Owner buatan Admin bisa login" || gagal "akun Owner buatan Admin bisa login ($KODE)"
H=$(curl -s -b "$JA" "$BASE/admin/users?q=barubaru")
cari "$H" "Owner Baru" "pengguna baru tampil di daftar"
# email duplikat saat tambah
T=$(csrf "$JA" "/admin/users/tambah")
H=$(curl -s -b "$JA" -c "$JA" -X POST "$BASE/admin/users" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Duplikat" \
  --data-urlencode "email=barubaru@queuecare.id" --data-urlencode "role=CUSTOMER" \
  --data-urlencode "password=rahasia123")
cari "$H" "sudah dipakai pengguna lain" "email duplikat saat tambah ditolak"
# password pendek saat tambah
T=$(csrf "$JA" "/admin/users/tambah")
H=$(curl -s -b "$JA" -c "$JA" -X POST "$BASE/admin/users" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Pendek" \
  --data-urlencode "email=pendek@queuecare.id" --data-urlencode "role=CUSTOMER" \
  --data-urlencode "password=abc")
cari "$H" "minimal 8 karakter" "password awal pendek ditolak"

echo "== 5. ubah pengguna =="
IDBARU=$(db "SELECT id FROM users WHERE email='barubaru@queuecare.id'")
T=$(csrf "$JA" "/admin/users/$IDBARU/ubah")
curl -s -b "$JA" -c "$JA" -o /dev/null -X POST "$BASE/admin/users/$IDBARU/ubah" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Owner Baru Ubah" \
  --data-urlencode "email=barubaru@queuecare.id" --data-urlencode "role=OWNER"
H=$(curl -s -b "$JA" "$BASE/admin/users?q=barubaru")
cari "$H" "Owner Baru Ubah" "ubah nama pengguna"
cari "$H" "diperbarui" "pesan sukses ubah"
KODE=$(login "$JD" barubaru@queuecare.id rahasia123)
[ "$KODE" = "302" ] && lulus "password tidak berubah saat ubah (kolom kosong)" || gagal "password berubah saat ubah ($KODE)"
# email duplikat saat ubah
T=$(csrf "$JA" "/admin/users/$IDBARU/ubah")
H=$(curl -s -b "$JA" -c "$JA" -X POST "$BASE/admin/users/$IDBARU/ubah" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Owner Baru Ubah" \
  --data-urlencode "email=owner@queuecare.id" --data-urlencode "role=OWNER")
cari "$H" "sudah dipakai pengguna lain" "email duplikat saat ubah ditolak"

echo "== 6. aturan keselamatan Admin =="
IDADMIN=$(db "SELECT id FROM users WHERE email='admin@queuecare.id'")
T=$(csrf "$JA" "/admin/users")
curl -s -b "$JA" -c "$JA" -o /dev/null -X POST "$BASE/admin/users/$IDADMIN/status" \
  --data-urlencode "_csrf=$T" --data-urlencode "aktif=false"
H=$(curl -s -b "$JA" "$BASE/admin/users")
cari "$H" "tidak bisa menonaktifkan akun sendiri" "admin tidak bisa menonaktifkan dirinya"
AIF=$(db "SELECT active FROM users WHERE email='admin@queuecare.id'")
[ "$AIF" = "1" ] && lulus "akun admin sendiri tetap aktif" || gagal "akun admin sendiri ikut nonaktif"
T=$(csrf "$JA" "/admin/users/$IDADMIN/ubah")
H=$(curl -s -b "$JA" -c "$JA" -X POST "$BASE/admin/users/$IDADMIN/ubah" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Admin QueueCare" \
  --data-urlencode "email=admin@queuecare.id" --data-urlencode "role=OWNER")
cari "$H" "tidak bisa mengubah role akun sendiri" "admin tidak bisa mengubah role dirinya"
ROLE=$(db "SELECT role FROM users WHERE email='admin@queuecare.id'")
[ "$ROLE" = "ADMIN" ] && lulus "role admin sendiri tetap ADMIN" || gagal "role admin sendiri berubah: $ROLE"
IDOWNER=$(db "SELECT id FROM users WHERE email='owner@queuecare.id'")
T=$(csrf "$JA" "/admin/users/$IDOWNER/ubah")
H=$(curl -s -b "$JA" -c "$JA" -X POST "$BASE/admin/users/$IDOWNER/ubah" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Owner Demo" \
  --data-urlencode "email=owner@queuecare.id" --data-urlencode "role=CUSTOMER")
cari "$H" "masih memegang bisnis" "owner ber-bisnis tidak bisa diubah role-nya"
ROLE=$(db "SELECT role FROM users WHERE email='owner@queuecare.id'")
[ "$ROLE" = "OWNER" ] && lulus "role owner tetap OWNER" || gagal "role owner berubah: $ROLE"
# owner tanpa bisnis boleh diubah role
T=$(csrf "$JA" "/admin/users/$IDBARU/ubah")
curl -s -b "$JA" -c "$JA" -o /dev/null -X POST "$BASE/admin/users/$IDBARU/ubah" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Owner Baru Ubah" \
  --data-urlencode "email=barubaru@queuecare.id" --data-urlencode "role=CUSTOMER"
ROLE=$(db "SELECT role FROM users WHERE email='barubaru@queuecare.id'")
[ "$ROLE" = "CUSTOMER" ] && lulus "owner tanpa bisnis boleh diubah role-nya" || gagal "owner tanpa bisnis gagal diubah: $ROLE"

echo "== 7. nonaktif dan sesi =="
IDUST=$(db "SELECT id FROM users WHERE email='customer@queuecare.id'")
T=$(csrf "$JA" "/admin/users")
curl -s -b "$JA" -c "$JA" -o /dev/null -X POST "$BASE/admin/users/$IDUST/status" \
  --data-urlencode "_csrf=$T" --data-urlencode "aktif=false"
AIF=$(db "SELECT active FROM users WHERE email='customer@queuecare.id'")
[ "$AIF" = "0" ] && lulus "admin menonaktifkan customer" || gagal "customer gagal dinonaktifkan: $AIF"
R=$(curl -s -b "$JC" -o /dev/null -w "%{redirect_url}" "$BASE/customer")
echo "    sesi aktif -> $R"
echo "$R" | grep -q "disabled" && lulus "sesi aktif ikut ditolak pada request berikutnya" || gagal "sesi aktif tidak ditolak: $R"
R=$(login_gagal "$JC" customer@queuecare.id customer123)
echo "    redirect login -> $R"
echo "$R" | grep -q "disabled" && lulus "login akun nonaktif ditolak dengan penanda disabled" || gagal "login nonaktif: $R"
H=$(curl -s -b "$JC" "$BASE/login?disabled")
cari "$H" "Akun dinonaktifkan. Hubungi Administrator." "pesan nonaktif jelas di halaman login"
# bisnis milik owner tidak hilang saat owner nonaktif
T=$(csrf "$JA" "/admin/users")
curl -s -b "$JA" -c "$JA" -o /dev/null -X POST "$BASE/admin/users/$IDOWNER/status" \
  --data-urlencode "_csrf=$T" --data-urlencode "aktif=false"
BISNIS=$(db "SELECT COUNT(*) FROM businesses WHERE owner_id=$IDOWNER")
[ "$BISNIS" -gt 0 ] && lulus "bisnis owner tetap ada ($BISNIS) saat owner nonaktif" || gagal "bisnis owner hilang"
KAT=$(curl -s "$BASE/businesses")
cari "$KAT" "Barbershop Senja" "bisnis owner nonaktif tetap tampil di katalog"
T=$(csrf "$JA" "/admin/users")
curl -s -b "$JA" -c "$JA" -o /dev/null -X POST "$BASE/admin/users/$IDOWNER/status" \
  --data-urlencode "_csrf=$T" --data-urlencode "aktif=true"
T=$(csrf "$JA" "/admin/users")
curl -s -b "$JA" -c "$JA" -o /dev/null -X POST "$BASE/admin/users/$IDUST/status" \
  --data-urlencode "_csrf=$T" --data-urlencode "aktif=true"
KODE=$(login "$JC" customer@queuecare.id customer123)
[ "$KODE" = "302" ] && lulus "aktifkan kembali customer bisa login" || gagal "customer yang diaktifkan tidak bisa login ($KODE)"

echo "== 8. profil =="
T=$(csrf "$JC" "/profile")
curl -s -b "$JC" -c "$JC" -o /dev/null -w "%{http_code} %{redirect_url}\n" -X POST "$BASE/profile" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Customer Baru" \
  --data-urlencode "email=customerbaru@queuecare.id"
NAMA=$(db "SELECT name FROM users WHERE id=$IDUST")
EMAIL=$(db "SELECT email FROM users WHERE id=$IDUST")
[ "$NAMA" = "Customer Baru" ] && [ "$EMAIL" = "customerbaru@queuecare.id" ] && lulus "ubah nama dan email profil" || gagal "ubah profil: $NAMA / $EMAIL"
KODE=$(login "$JD" customerbaru@queuecare.id customer123)
[ "$KODE" = "302" ] && lulus "email baru bisa dipakai login" || gagal "email baru tidak bisa login ($KODE)"
KODE=$(login_gagal "$JX" customer@queuecare.id customer123)
echo "$KODE" | grep -q "error" && lulus "email lama tidak bisa login lagi" || gagal "email lama masih bisa login: $KODE"
H=$(curl -s -b "$JD" "$BASE/login?profil=1")
cari "$H" "Masuk lagi dengan email baru" "pesan masuk ulang setelah email berubah"
# email duplikat saat ubah profil
T=$(csrf "$JD" "/profile")
H=$(curl -s -b "$JD" -c "$JD" -X POST "$BASE/profile" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Customer Baru" \
  --data-urlencode "email=owner@queuecare.id")
cari "$H" "sudah dipakai pengguna lain" "email duplikat saat ubah profil ditolak"

echo "== 9. ganti password =="
gan() { T=$(csrf "$JD" "/profile"); curl -s -b "$JD" -c "$JD" -X POST "$BASE/profile/password" \
  --data-urlencode "_csrf=$T" --data-urlencode "passwordLama=$1" \
  --data-urlencode "passwordBaru=$2" --data-urlencode "konfirmasi=$3"; }
HASH0=$(db "SELECT password_hash FROM users WHERE email='customerbaru@queuecare.id'")
H=$(gan bukanpassword rahabaru1 rahabaru1)
cari "$H" "Password lama tidak cocok" "pesan password lama salah"
HASH=$(db "SELECT password_hash FROM users WHERE email='customerbaru@queuecare.id'")
[ "$HASH" = "$HASH0" ] && lulus "password tidak berubah saat password lama salah" || gagal "password berubah saat password lama salah"
H=$(gan customer123 rahabaru1 rahabaru2)
cari "$H" "Konfirmasi password tidak sama" "konfirmasi tidak sama ditolak"
H=$(gan customer123 pendek pendek)
cari "$H" "minimal 8 karakter" "password baru pendek ditolak"
H=$(gan customer123 customer123 customer123)
cari "$H" "tidak boleh sama" "password baru sama dengan lama ditolak"
HASH1=$(db "SELECT password_hash FROM users WHERE email='customerbaru@queuecare.id'")
H=$(gan customer123 rahabaru1 rahabaru1)
HASH2=$(db "SELECT password_hash FROM users WHERE email='customerbaru@queuecare.id'")
[ "$HASH1" != "$HASH2" ] && lulus "hash password berubah di database" || gagal "hash password tidak berubah"
H=$(curl -s -b "$JD" "$BASE/profile")
cari "$H" "Password diganti" "ganti password berhasil"
echo "$HASH2" | grep -q '^\$2[aby]\$' && lulus "password tersimpan sebagai BCrypt" || gagal "password bukan BCrypt: $HASH2"
KODE=$(login "$JD" customerbaru@queuecare.id rahabaru1)
[ "$KODE" = "302" ] && lulus "password baru bisa dipakai login" || gagal "password baru tidak bisa login ($KODE)"
KODE=$(login_gagal "$JX" customerbaru@queuecare.id customer123)
echo "$KODE" | grep -q "error" && lulus "password lama tidak bisa dipakai lagi" || gagal "password lama masih bisa login: $KODE"
KODE=$(curl -s -b "$JD" -o /dev/null -w "%{http_code}" "$BASE/profile")
[ "$KODE" = "200" ] && lulus "pengguna tetap login setelah ganti password" || gagal "sesi hilang setelah ganti password ($KODE)"

echo "== 10. profil untuk role lain =="
T=$(csrf "$JO" "/profile")
curl -s -b "$JO" -c "$JO" -o /dev/null -X POST "$BASE/profile" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Owner Demo" --data-urlencode "email=owner@queuecare.id"
H=$(curl -s -b "$JO" "$BASE/profile")
cari "$H" "OWNER" "profil owner menampilkan peran"
T=$(csrf "$JA" "/profile")
curl -s -b "$JA" -c "$JA" -o /dev/null -X POST "$BASE/profile" \
  --data-urlencode "_csrf=$T" --data-urlencode "name=Admin QueueCare" --data-urlencode "email=admin@queuecare.id"
H=$(curl -s -b "$JA" "$BASE/profile")
cari "$H" "ADMIN" "profil admin menampilkan peran"

echo "== 11. akses 403 =="
KODE=$(login "$JC" customerbaru@queuecare.id rahabaru1); echo "    login ulang customer -> $KODE"
KODE=$(login "$JO" owner@queuecare.id owner123); echo "    login ulang owner -> $KODE"
for J in "$JC" "$JO"; do
  K=$(curl -s -o /dev/null -w "%{http_code}" -b "$J" "$BASE/admin/users")
  [ "$K" = "403" ] && lulus "buka /admin/users sebagai $( [ "$J" = "$JC" ] && echo customer || echo owner ) -> 403" || gagal "/admin/users -> $K"
done

echo "== 12. login tidak membocorkan data =="
R=$(login_gagal "$JD" "tidakada@queuecare.id" "salah")
echo "$R" | grep -q "error" && lulus "email tidak terdaftar mendapat pesan generik" || gagal "pesan bocor: $R"
H=$(curl -s "$BASE/login?error")
cari "$H" "Email atau password salah." "pesan login generik"
tidak "$H" "tidak terdaftar" "pesan login tidak menyebut email tidak terdaftar"

echo
echo "TOTAL OK=$OK GAGAL=$BAD"
"$MYSQL" -u root queuecare -e "UPDATE users u JOIN users d ON d.email='dummy1@queuecare.id' SET u.password_hash=d.password_hash, u.name='Customer Demo', u.email='customer@queuecare.id', u.active=1 WHERE u.email IN ('customer@queuecare.id','customerbaru@queuecare.id'); DELETE FROM users WHERE email LIKE 'uji%@queuecare.id'; DELETE FROM users WHERE email IN ('barubaru@queuecare.id','pendek@queuecare.id','customerbaru@queuecare.id');"
echo "== bersih =="
db "SELECT COUNT(*) FROM users; SELECT email, active FROM users WHERE email IN ('admin@queuecare.id','owner@queuecare.id','customer@queuecare.id');"
