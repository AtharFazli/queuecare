import datetime
import subprocess
import sys
from playwright.sync_api import sync_playwright

BASE = "http://localhost:8080"
SCRATCH = "C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"
JAR = SCRATCH + "/pw.jar"
TGL = (datetime.date.today() + datetime.timedelta(days=1)).isoformat()


def login_cookie():
    subprocess.run(["curl", "-s", "-c", JAR, "-o", "/dev/null", BASE + "/login"], check=False)
    out = subprocess.run(
        ["curl", "-s", "-b", JAR, BASE + "/login"], capture_output=True, text=True
    ).stdout
    import re

    m = re.search(r'name="_csrf"[^>]*value="([^"]+)"', out)
    tok = m.group(1)
    r = subprocess.run(
        [
            "curl", "-s", "-b", JAR, "-c", JAR, "-o", "/dev/null",
            "-w", "%{http_code} %{redirect_url}",
            "-X", "POST", BASE + "/login",
            "--data-urlencode", "_csrf=" + tok,
            "--data-urlencode", "username=customer@queuecare.id",
            "--data-urlencode", "password=customer123",
        ],
        capture_output=True, text=True,
    )
    print("login:", r.stdout)
    return JAR


def buat_reservasi(jar):
    out = subprocess.run(
        ["curl", "-s", "-b", jar, BASE + "/reservation/create/42?tanggal=" + TGL],
        capture_output=True, text=True,
    ).stdout
    import re

    tok = re.search(r'name="_csrf"[^>]*value="([^"]+)"', out).group(1)
    r = subprocess.run(
        [
            "curl", "-s", "-b", jar, "-o", "/dev/null",
            "-w", "%{http_code} %{redirect_url}",
            "-X", "POST", BASE + "/reservation/create/42",
            "--data-urlencode", "_csrf=" + tok,
            "--data-urlencode", "tanggal=" + TGL,
            "--data-urlencode", "jam=13:00",
            "--data-urlencode", "catatan=tata letak",
        ],
        capture_output=True, text=True,
    )
    print("buat:", r.stdout)
    return r.stdout.strip().split("/")[-1]


def bersihkan(rid):
    subprocess.run(
        [
            "C:/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe", "-u", "root",
            "queuecare", "-e",
            "DELETE FROM queues; DELETE FROM reservations;",
        ],
        capture_output=True,
    )
    print("data uji dibersihkan")


jar = login_cookie()
rid = buat_reservasi(jar)
print("reservasi uji:", rid)

halaman = [
    ("fase4-form", "/reservation/create/42?tanggal=" + TGL),
    ("fase4-sukses", "/reservation/success/" + rid),
    ("fase4-riwayat", "/reservations"),
    ("fase4-detail", "/reservations/" + rid),
]

with sync_playwright() as p:
    browser = p.chromium.launch(channel="msedge", headless=True)
    for lebar in (375, 1280):
        ctx = browser.new_context(viewport={"width": lebar, "height": 900})
        ctx.add_cookies(
            [
                {
                    "name": "JSESSIONID",
                    "value": open(JAR).read().split("JSESSIONID")[-1].split("\t")[-1].strip(),
                    "domain": "localhost",
                    "path": "/",
                }
            ]
        )
        page = ctx.new_page()
        for nama, url in halaman:
            page.goto(BASE + url, wait_until="networkidle")
            page.wait_for_timeout(300)
            sw = page.evaluate("document.documentElement.scrollWidth")
            iw = page.evaluate("window.innerWidth")
            status = "OK" if sw <= iw else "OVERFLOW"
            print(f"{status} {nama} {lebar}px scrollWidth={sw} innerWidth={iw}")
            page.screenshot(path=f"docs/screenshots/{nama}-{lebar}.png", full_page=True)
        ctx.close()
    browser.close()

bersihkan(rid)
