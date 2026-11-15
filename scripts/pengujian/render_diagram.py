import base64
import io
import json
import os
import time
import urllib.request

DIAG = "D:/PBO-Java/docs/diagram"


def ambil(url):
    req = urllib.request.Request(url, headers={"User-Agent": "curl/8.5.0"})
    with urllib.request.urlopen(req, timeout=120) as r:
        return r.read()


for nama in ("erd", "class", "usecase"):
    kode = io.open(os.path.join(DIAG, nama + ".mmd"), encoding="utf-8").read()
    payload = base64.urlsafe_b64encode(
        json.dumps({"code": kode, "mermaid": {"theme": "base"}}).encode("utf-8")
    ).decode("ascii")
    for fmt in ("svg", "img"):
        ekstensi = "png" if fmt == "img" else fmt
        path = os.path.join(DIAG, f"{nama}.{ekstensi}")
        if os.path.exists(path) and os.path.getsize(path) > 1000:
            print("LEWAT", nama, fmt, os.path.getsize(path))
            continue
        url = f"https://mermaid.ink/{fmt}/{payload}"
        hasil = None
        for percobaan in range(4):
            try:
                hasil = ambil(url)
                break
            except Exception as ex:
                print("  coba ulang", nama, fmt, percobaan + 1, ex)
                time.sleep(6)
        if hasil:
            io.open(path, "wb").write(hasil)
            print("OK", nama, fmt, len(hasil))
        else:
            print("GAGAL", nama, fmt)
        time.sleep(3)
