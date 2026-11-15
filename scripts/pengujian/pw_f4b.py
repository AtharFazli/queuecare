import datetime
import re
import subprocess
from playwright.sync_api import sync_playwright

BASE = "http://localhost:8080"
SCRATCH = "C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"
JAR = SCRATCH + "/pw.jar"
TGL = (datetime.date.today() + datetime.timedelta(days=1)).isoformat()


def buat_reservasi():
    subprocess.run(["curl", "-s", "-c", JAR, "-o", "/dev/null", BASE + "/login"], check=False)
    html = subprocess.run(["curl", "-s", "-b", JAR, BASE + "/login"], capture_output=True, text=True).stdout
    tok = re.search(r'name="_csrf"[^>]*value="([^"]+)"', html).group(1)
    subprocess.run(
        ["curl", "-s", "-b", JAR, "-c", JAR, "-o", "/dev/null", "-X", "POST", BASE + "/login",
         "--data-urlencode", "_csrf=" + tok,
         "--data-urlencode", "username=customer@queuecare.id",
         "--data-urlencode", "password=customer123"],
        check=False,
    )
    html = subprocess.run(
        ["curl", "-s", "-b", JAR, BASE + "/reservation/create/42?tanggal=" + TGL],
        capture_output=True, text=True,
    ).stdout
    tok = re.search(r'name="_csrf"[^>]*value="([^"]+)"', html).group(1)
    r = subprocess.run(
        ["curl", "-s", "-b", JAR, "-o", "/dev/null", "-w", "%{http_code} %{redirect_url}",
         "-X", "POST", BASE + "/reservation/create/42",
         "--data-urlencode", "_csrf=" + tok,
         "--data-urlencode", "tanggal=" + TGL,
         "--data-urlencode", "jam=13:00",
         "--data-urlencode", "catatan=tata letak"],
        capture_output=True, text=True,
    )
    print("buat:", r.stdout)
    return r.stdout.strip().split("/")[-1]


def bersihkan():
    subprocess.run(
        ["C:/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe", "-u", "root", "queuecare", "-e",
         "DELETE FROM queues; DELETE FROM reservations;"],
        capture_output=True,
    )
    print("data uji dibersihkan")


rid = buat_reservasi()
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
        page = ctx.new_page()
        page.goto(BASE + "/login", wait_until="networkidle")
        page.fill('input[name="username"]', "customer@queuecare.id")
        page.fill('input[name="password"]', "customer123")
        page.click('button[type="submit"]')
        page.wait_for_load_state("networkidle")
        print(f"login di {lebar}px ->", page.url)
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

bersihkan()
