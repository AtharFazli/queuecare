#!/usr/bin/env bash
BASE=http://localhost:8080
W="C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"
JAR="$W/qc_cookies.txt"
rm -f "$JAR"

csrf() {
  curl -s -m 5 -b "$JAR" -c "$JAR" "$BASE$1" | tr '\n' ' ' \
    | grep -o 'name="_csrf"[^>]*' | head -1 | grep -o 'value="[^"]*"' | head -1 | cut -d'"' -f2
}

post() { # url, then extra curl args
  local url="$1"; shift
  curl -s -m 10 -b "$JAR" -c "$JAR" -o "$W/qc_body.html" -w "%{http_code} %{redirect_url}" "$@" "$BASE$url"
}

get() {
  curl -s -m 10 -b "$JAR" -c "$JAR" -o "$W/qc_body.html" -w "%{http_code}" "$BASE$1"
}

body() { cat "$W/qc_body.html"; }

echo "== 1. halaman register tampil =="
echo "GET /register -> $(get /register)"
body | grep -q "Daftar" && echo "   ok: judul Daftar ada" || echo "   GAGAL"

echo "== 2. email duplikat ditolak =="
T=$(csrf /register)
echo "POST /register (admin@queuecare.id) -> $(post /register -X POST --data-urlencode "_csrf=$T" --data-urlencode "name=Budi" --data-urlencode "email=admin@queuecare.id" --data-urlencode "password=password123" --data-urlencode "confirmPassword=password123")"
body | grep -q "sudah terdaftar" && echo "   ok: pesan email duplikat ada" || { echo "   GAGAL"; body | grep -o "sudah terdaftar\|Email[^<]*" | head -3; }

echo "== 3. registrasi baru =="
T=$(csrf /register)
echo "POST /register (baru@queuecare.id) -> $(post /register -X POST --data-urlencode "_csrf=$T" --data-urlencode "name=Budi Baru" --data-urlencode "email=baru@queuecare.id" --data-urlencode "password=password123" --data-urlencode "confirmPassword=password123")"
echo "GET /login -> $(get /login)"
body | grep -q "Akun dibuat" && echo "   ok: flash sukses tampil" || echo "   GAGAL"

echo "== 4. password tidak sama =="
T=$(csrf /register)
echo "POST /register (beda) -> $(post /register -X POST --data-urlencode "_csrf=$T" --data-urlencode "name=Budi" --data-urlencode "email=beda@queuecare.id" --data-urlencode "password=password123" --data-urlencode "confirmPassword=lain12345")"
body | grep -q "tidak sama" && echo "   ok: pesan konfirmasi tampil" || echo "   GAGAL"

echo "== 5. login salah password =="
T=$(csrf /login)
echo "POST /login (salah) -> $(post /login -X POST --data-urlencode "_csrf=$T" --data-urlencode "username=customer@queuecare.id" --data-urlencode "password=salah")"
echo "GET /login?error -> $(get '/login?error')"
body | grep -q "Email atau password salah" && echo "   ok: pesan salah tampil" || echo "   GAGAL"

echo "== 6. login customer =="
T=$(csrf /login)
echo "POST /login -> $(post /login -X POST --data-urlencode "_csrf=$T" --data-urlencode "username=customer@queuecare.id" --data-urlencode "password=customer123")"
echo "GET /customer -> $(get /customer)"
body | grep -q "Halo" && echo "   ok: halaman customer" || echo "   GAGAL"
echo "GET /admin (customer) -> $(get /admin)"
body | grep -q "Akses ditolak" && echo "   ok: 403 + halaman akses ditolak" || echo "   GAGAL"
echo "GET /owner (customer) -> $(get /owner)"
body | grep -q "Akses ditolak" && echo "   ok: 403 + halaman akses ditolak" || echo "   GAGAL"

echo "== 7. logout =="
T=$(csrf /customer)
echo "POST /logout -> $(post /logout -X POST --data-urlencode "_csrf=$T")"
echo "GET /customer (sudah keluar) -> $(get /customer)"
echo "GET /login?logout -> $(get '/login?logout')"
body | grep -q "Kamu sudah keluar" && echo "   ok: pesan logout tampil" || echo "   GAGAL"

echo "== 8. login admin =="
T=$(csrf /login)
echo "POST /login -> $(post /login -X POST --data-urlencode "_csrf=$T" --data-urlencode "username=admin@queuecare.id" --data-urlencode "password=admin123")"
echo "GET /admin -> $(get /admin)"
body | grep -q "Menu Admin" && echo "   ok: dashboard admin" || echo "   GAGAL"
echo "GET /owner (admin) -> $(get /owner)"
body | grep -q "Akses ditolak" && echo "   ok: admin ditolak di /owner" || echo "   GAGAL"
T=$(csrf /admin)
post /logout -X POST --data-urlencode "_csrf=$T" >/dev/null

echo "== 9. login owner =="
T=$(csrf /login)
echo "POST /login -> $(post /login -X POST --data-urlencode "_csrf=$T" --data-urlencode "username=owner@queuecare.id" --data-urlencode "password=owner123")"
echo "GET /owner -> $(get /owner)"
body | grep -q "Menu Owner" && echo "   ok: dashboard owner" || echo "   GAGAL"
echo "GET /admin (owner) -> $(get /admin)"
body | grep -q "Akses ditolak" && echo "   ok: owner ditolak di /admin" || echo "   GAGAL"
T=$(csrf /owner)
post /logout -X POST --data-urlencode "_csrf=$T" >/dev/null

echo "== 10. akun baru dari registrasi bisa login =="
T=$(csrf /login)
echo "POST /login (baru@queuecare.id) -> $(post /login -X POST --data-urlencode "_csrf=$T" --data-urlencode "username=baru@queuecare.id" --data-urlencode "password=password123")"
echo "GET /customer -> $(get /customer)"
body | grep -q "Halo" && echo "   ok: akun baru masuk ke /customer" || echo "   GAGAL"
